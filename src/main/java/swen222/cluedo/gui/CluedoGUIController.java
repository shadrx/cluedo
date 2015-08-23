package swen222.cluedo.gui;

import swen222.cluedo.CluedoInterface;
import swen222.cluedo.model.*;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import utilities.Pair;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CluedoGUIController represents the 'controller' layer in the MVC approach for this game, in contrast to the integrated ViewController approach.
 * It responds to all of the methods declared in the CluedoInterface and manages state required for the GUI.
 * All state is managed by this object itself: it is required to store any passed variables it will later need,
 * since from the perspective of the model, all calls are purely functional and have no side effects.
 */
public class CluedoGUIController implements CluedoInterface {


    private final Object _syncObject = new Object();
    private Game _gameState = null;
    private CluedoFrame _cluedoFrame = null;
    private Player _currentPlayer = null;
    private Set<Location<Integer>> _blockedLocations = null;
    private Map<Location, Board.Path> _pathsForTurn = new HashMap<>();
    private Board.Path _selectedPath = null;
    private Set<TurnOption> _possibleOptionsForTurn = Collections.emptySet();
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

    private void setupGUI() {
                if (_cluedoFrame == null) {

                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty(
                            "com.apple.mrj.application.apple.menu.about.name", "Name"); //use the OS X menu bar.

                    _cluedoFrame = new CluedoFrame();
                    _cluedoFrame.setActionDelegate(new ActionDelegate() {
                        @Override
                        public void makeASuggestion() {
                            _playerOptionForTurn = Optional.of(TurnOption.Suggestion);
                            resumeGameThread();
                        }

                        @Override
                        public void endTurn() {
                            _playerOptionForTurn = Optional.of(TurnOption.EndTurn);
                            resumeGameThread();
                        }

                        @Override
                        public void makeAnAccusation() {
                            _playerOptionForTurn = Optional.of(TurnOption.Accusation);
                            resumeGameThread();
                        }

                    });

                    _cluedoFrame.canvas().setDelegate((location) -> {
                        Board.Path path = _pathsForTurn.get(location);
                        if (_possibleOptionsForTurn.contains(TurnOption.Move) && path != null) {
                            _playerOptionForTurn = Optional.of(TurnOption.Move);

                            _selectedPath = path;
                            resumeGameThread();
                        }

                    });
                }

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
    public void notifyStartOfTurn(Player player, int diceRoll) {
        _currentPlayer = player;

        SwingUtilities.invokeLater(() -> {

            this.setupGUI();

            _cluedoFrame.cardView().setCards(Collections.emptyList());

            JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s's (%s) Turn", player.name.get(), player.character));

            _cluedoFrame.cardView().setCards(
                    new ArrayList<>(player.cards)
            );
            _cluedoFrame.diceView().setDiceValue(diceRoll);

        });

    }

    @Override
    public void notifySuccess(Player player) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s has made a correct accusation and has won!", player.name.get()), String.format("%s has Won!", player.name.get()), JOptionPane.PLAIN_MESSAGE);
            _cluedoFrame.dispose();
        });
    }

    @Override
    public void notifyFailure(Player player) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(_cluedoFrame, String.format("%s has made an incorrect accusation and is no longer playing.", player.name.get()), String.format("%s Made an Incorrect Accusation", player.name), JOptionPane.PLAIN_MESSAGE);
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
    public void showGame(Game game, Set<Location<Integer>> blockedLocations) {
        _gameState = game;

        _blockedLocations = blockedLocations;
        SwingUtilities.invokeLater(() -> {
            _cluedoFrame.canvas().setGameState(game);
            _cluedoFrame.canvas().setAccessibleTilePaths(null);
        });
    }

    @Override
    public TurnOption requestPlayerChoiceForTurn(Set<TurnOption> possibleOptions, Player player, int remainingMoves) {
        _playerOptionForTurn = Optional.empty();
        _possibleOptionsForTurn = possibleOptions;

        //Show the player the accessible tiles if moving is a possible option

        Set<Board.Path> possibleMoves;
        if (possibleOptions.contains(TurnOption.Move)) {
            possibleMoves = _gameState.board.pathsFromLocation(player.location(), remainingMoves, _blockedLocations);

            _pathsForTurn.clear();
            for (Board.Path path : possibleMoves) {
                _pathsForTurn.put(path.locations[path.distance - 1], path);
            }
        } else {
            possibleMoves = null;
        }

        SwingUtilities.invokeLater(() -> {
            _cluedoFrame.canvas().setAccessibleTilePaths(possibleMoves);

            _cluedoFrame.setEnabledActions(possibleOptions);
        });

        waitForGUI();
        return _playerOptionForTurn.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Board.Path requestPlayerMove(Player player, Board board, Set<Location<Integer>> blockedLocations, int distance) {

        if (distance == 0 || _selectedPath == null) {
            new Board.Path(new Location[]{player.location()}, 0);
        }

        SwingUtilities.invokeLater(() -> _cluedoFrame.diceView().setRemainingValue(distance - _selectedPath.cost));

        _cluedoFrame.canvas().setLastPlayerMove(_selectedPath, player);

        return _selectedPath;
    }

    private Optional<Suggestion> getPlayerSuggestion(Player player, Optional<Room> room) {
        @SuppressWarnings("unchecked")
        final Optional<Suggestion>[] retVal = new Optional[]{null};

        SwingUtilities.invokeLater(() -> new PlayerSuggestionDialog(_cluedoFrame, new PlayerSuggestionDialog.PlayerSuggestionDelegate() {
            @Override
            public void playerDidMakeSuggestion(PlayerSuggestionDialog dialog, Suggestion suggestion) {
                Optional<Suggestion> suggestionOptional = Optional.of(suggestion);
                _cluedoFrame.canvas().moveWeaponsBasedOnSuggestion(suggestionOptional);
                retVal[0] = suggestionOptional;
                resumeGameThread();
            }

            @Override
            public void playerDidCancelSuggestion(PlayerSuggestionDialog dialog) {
                retVal[0] = Optional.empty();
                resumeGameThread();
            }
        },
                player,
                room));

        waitForGUI();

        return retVal[0];
    }

    @Override
    public Optional<Suggestion> requestPlayerAccusation(Player player) {
        return this.getPlayerSuggestion(player, Optional.empty());
    }

    @Override
    public Optional<Suggestion> requestPlayerSuggestion(Player player, Room room) {
        return this.getPlayerSuggestion(player, Optional.of(room));
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
