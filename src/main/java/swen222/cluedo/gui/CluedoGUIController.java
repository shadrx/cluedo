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

    private void runOnUIThread(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

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

        Integer selectedValue = null;

        while (selectedValue == null) {
            selectedValue = (Integer) JOptionPane.showInputDialog(null,
                    "Choose the number of players.", "How many players?",
                    JOptionPane.PLAIN_MESSAGE, null,
                    possibleValues, possibleValues[0]);
        }
        return selectedValue;
    }

    @Override
    public Pair<Optional<String>, CluedoCharacter> askForNameAndCharacter(List<CluedoCharacter> availableCharacters) {
        Set<CluedoCharacter> availableCharactersSet = new HashSet<>(availableCharacters);

        @SuppressWarnings("unchecked")
        final Pair<Optional<String>, CluedoCharacter>[] retVal = new Pair[]{null}; //One-element array to work around issues with variable modification in blocks.

        new PlayerSelectionDialog((dialog, selectedName, selectedCharacter) -> {
            retVal[0] = new Pair<>(Optional.of(selectedName), selectedCharacter);
            resumeGameThread();
        }, availableCharactersSet);

        waitForGUI();

        return retVal[0];
    }

    @Override
    public void notifyStartOfTurn(Player player) {
        if (_cluedoFrame == null) {
            _cluedoFrame = new CluedoFrame();
        }


    }

    @Override
    public void notifySuccess(Player player) {
        JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s has made a correct accusation and has won!"), String.format("%s has Won!", player.name), JOptionPane.PLAIN_MESSAGE);
        _cluedoFrame.dispose();
    }

    @Override
    public void notifyFailure(Player player) {
        JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s has made an incorrect accusation and is no longer playing."), String.format("%s Made an Incorrect Accusation", player.name), JOptionPane.PLAIN_MESSAGE);
        _cluedoFrame.dispose();
    }

    @Override
    public void notifyGameOver() {
        JOptionPane.showMessageDialog(_cluedoFrame, "No one is left to play!", "Game Over", JOptionPane.PLAIN_MESSAGE);
        _cluedoFrame.dispose();
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

    @Override
    public Optional<Suggestion> requestPlayerAccusation(Player player) {
        return null;
    }

    @Override
    public Optional<Suggestion> requestPlayerSuggestion(Player player, Room room) {
        return null;
    }

    @Override
    public SuggestionResponse requestPlayerResponse(Player player, List<SuggestionResponse> possibleResponses) {
        JOptionPane.showMessageDialog(_cluedoFrame, String.format("For %s's eyes only:", player.name));

        String title = player.name.get();
        String message = "Click on a card to refute the suggestion.";
        List<Card> responseCards = possibleResponses.stream().map((suggestionResponse -> suggestionResponse.card)).collect(Collectors.toList());

        final SuggestionResponse[] response = {null};

        new MessageAndCardDialog(_cluedoFrame, title, message, responseCards, (cardView, selectedCard) -> {
            int responseIndex = responseCards.indexOf(selectedCard);
            response[0] = possibleResponses.get(responseIndex);
            resumeGameThread();
        });

        waitForGUI();

        return response[0];
    }

    @Override
    public void notifyPlayerResponse(Player player, SuggestionResponse response) {
        String title = player.name.get();
        String message = response.toString();

        new MessageAndCardDialog(_cluedoFrame, title, message, Arrays.asList(response.card), null);
    }
}
