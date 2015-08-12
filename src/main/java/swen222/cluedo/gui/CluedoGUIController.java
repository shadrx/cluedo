package swen222.cluedo.gui;

import swen222.cluedo.CluedoInterface;
import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CluedoGUIController implements CluedoInterface {

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
        return 0;
    }

    @Override
    public CluedoCharacter askToSelectACharacter(List<CluedoCharacter> availableCharacters) {
        return null;
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
