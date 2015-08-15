package swen222.cluedo.gui;

import swen222.cluedo.CluedoInterface;
import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import utilities.Pair;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CluedoGUIController implements CluedoInterface {

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
                    JOptionPane.INFORMATION_MESSAGE, null,
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

    }

    @Override
    public void notifySuccess(Player player) {

    }

    @Override
    public void notifyFailure(Player player) {

    }

    @Override
    public void notifyGameOver() {

    }

    @Override
    public void showGame(Game game) {

    }

    @Override
    public TurnOption requestPlayerChoiceForTurn(Set<TurnOption> possibleOptions, Player player) {
        //gui.requestPlayerChoiceForTurn(possibleOptions, player);

        waitForGUI();
        return _playerOptionForTurn.get();
    }

    @Override
    public List<Direction> requestPlayerMove(Player player, int distance) {
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
        return null;
    }

    @Override
    public void notifyPlayerResponse(Player player, SuggestionResponse response) {

    }
}
