package swen222.cluedo.gui;

import swen222.cluedo.CluedoInterface;
import swen222.cluedo.model.*;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import utilities.Pair;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class CluedoGUIController implements CluedoInterface {


    private CluedoFrame _cluedoFrame = null;
    private final Object _syncObject = new Object();

    private Optional<TurnOption> _playerOptionForTurn = Optional.empty();

    /**
     * Tells the game thread that the response it was waiting for has been set, and that it may continue execution.
     */
    public void resumeGameThread() {
        synchronized (_syncObject) {
            _syncObject.notify();
        }
    }

    private void waitForGUI() {
        synchronized(_syncObject) {
            try {
                _syncObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //GUI interaction methods. All of these should be executed on the main (i.e GUI) thread.

    public void setPlayerOptionForTurn(TurnOption optionForTurn) {
        _playerOptionForTurn = Optional.of(optionForTurn);
    }

    //CluedoInterface response methods. All of these are executed on the game (i.e. second) thread.

    @Override
    public int getNumberOfPlayers(int min, int max) {
        Integer[] possibleValues = new Integer[max - min + 1];
        for (int i = 0; i < max - min + 1; i++) {
            possibleValues[i] = min + i;
        }

        final Integer[] selectedValue = new Integer[]{null};

        while (selectedValue[0] == null) {
            SwingUtilities.invokeLater(() -> {
                selectedValue[0] = (Integer) JOptionPane.showInputDialog(null,
                        "Choose the number of players.", "How many players?",
                        JOptionPane.PLAIN_MESSAGE, null,
                        possibleValues, possibleValues[0]);
                resumeGameThread();
            });
            waitForGUI();
        }
        return selectedValue[0];
    }

    @Override
    public Pair<Optional<String>, CluedoCharacter> askForNameAndCharacter(List<CluedoCharacter> availableCharacters) {
        Set<CluedoCharacter> availableCharactersSet = new HashSet<>(availableCharacters);

        @SuppressWarnings("unchecked")
        final Pair<Optional<String>, CluedoCharacter>[] retVal = new Pair[]{null}; //One-element array to work around issues with variable modification in blocks.

        SwingUtilities.invokeLater(() -> new PlayerSelectionDialog((dialog, selectedName, selectedCharacter) -> {
            retVal[0] = new Pair<>(Optional.of(selectedName), selectedCharacter);
            resumeGameThread();
        }, availableCharactersSet));

        waitForGUI();

        return retVal[0];
    }

    @Override
    public void notifyStartOfTurn(Player player) {
        if (_cluedoFrame == null) {
            SwingUtilities.invokeLater(() -> _cluedoFrame = new CluedoFrame());
        }

        JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s's (%s) Turn", player.name, player.character));

        //TODO setup the GUI to show player's cards, roll the dice.

    }

    @Override
    public void notifySuccess(Player player) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s has made a correct accusation and has won!", player.name), String.format("%s has Won!", player.name), JOptionPane.PLAIN_MESSAGE);
            _cluedoFrame.dispose();
        });
    }

    @Override
    public void notifyFailure(Player player) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s has made an incorrect accusation and is no longer playing.", player.name), String.format("%s Made an Incorrect Accusation", player.name), JOptionPane.PLAIN_MESSAGE);
            _cluedoFrame.dispose();
        });
    }

    @Override
    public void notifyGameOver() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(_cluedoFrame, "No one is left to play!", "Game Over", JOptionPane.PLAIN_MESSAGE);
            _cluedoFrame.dispose();
        });
    }

    @Override
    public void showGame(Game game) {
        _cluedoFrame.canvas.setGameState(game);
    }

    @Override
    public TurnOption requestPlayerChoiceForTurn(Set<TurnOption> possibleOptions, Player player) {
        //gui.requestPlayerChoiceForTurn(possibleOptions, player);

        waitForGUI();
        return _playerOptionForTurn.get();
    }

    @Override
    public List<Direction> requestPlayerMove(Player player, int distance) {

        List<Direction> move = null; //...;

        _cluedoFrame.canvas.setLastPlayerMove(move, player);

        return null;
    }

    private Optional<Suggestion> getPlayerSuggestion(Optional<Room> room) {
        @SuppressWarnings("unchecked")
        final Optional<Suggestion>[] retVal = new Optional[]{null};


        SwingUtilities.invokeLater(() -> new PlayerSuggestionDialog(_cluedoFrame, new PlayerSuggestionDialog.PlayerSuggestionDelegate() {
            @Override
            public void playerDidMakeSuggestion(PlayerSuggestionDialog dialog, Suggestion suggestion) {
                retVal[0] = Optional.of(suggestion);
                resumeGameThread();
            }

            @Override
            public void playerDidCancelSuggestion(PlayerSuggestionDialog dialog) {
                retVal[0] = Optional.empty();
                resumeGameThread();
            }
        },
                room));

        waitForGUI();

        return retVal[0];
    }

    @Override
    public Optional<Suggestion> requestPlayerAccusation(Player player) {
        return this.getPlayerSuggestion(Optional.empty());
    }

    @Override
    public Optional<Suggestion> requestPlayerSuggestion(Player player, Room room) {
        return this.getPlayerSuggestion(Optional.of(room));
    }

    @Override
    public SuggestionResponse requestPlayerResponse(Player player, List<SuggestionResponse> possibleResponses) {

        String title = player.name.get();
        String message = "Click on a card to refute the suggestion.";
        List<Card> responseCards = possibleResponses.stream().map((suggestionResponse -> suggestionResponse.card)).collect(Collectors.toList());

        final SuggestionResponse[] response = {null};


        SwingUtilities.invokeLater(() -> {

            JOptionPane.showMessageDialog(_cluedoFrame, String.format("For %s's eyes only:", player.name.get()));

            new MessageAndCardDialog(_cluedoFrame, title, message, false, responseCards, (cardView, selectedCard) -> {
                int responseIndex = responseCards.indexOf(selectedCard);
                response[0] = possibleResponses.get(responseIndex);
                long threadId = Thread.currentThread().getId();
                resumeGameThread();
            });
        });

        waitForGUI();

        return response[0];
    }

    @Override
    public void notifyPlayerResponse(Player player, SuggestionResponse response) {
        String title = player.name.get();
        String message = response.toString();

        SwingUtilities.invokeLater(() -> new MessageAndCardDialog(_cluedoFrame, title, message, true, Collections.singletonList(response.card), null));
    }
}
