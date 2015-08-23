package swen222.cluedo;

import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;
import utilities.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CluedoInterface {

    /**
     * Get the number of players that will take part in the game.
     *
     * @param min the minimum amount of players (inclusive)
     * @param max the maximum amount of players (inclusive)
     *
     * @return the number of players that will take part in the game.
     */
    int getNumberOfPlayers(int min, int max);

    /**
     * Requests that the player choose a character to play as.
     *
     * @param availableCharacters list of characters available to choose from
     * @return the character the player chose
     * */
    Pair<Optional<String>, CluedoCharacter> askForNameAndCharacter(List<CluedoCharacter> availableCharacters);

    /**
     * Notify that player is about to start their turn.
     */
    void notifyStartOfTurn(Player player, int diceRoll);

    /**
     * Notify the player that a successful accusation was made.
     *
     * @param player the player that made the successful accusation
     */
    void notifySuccess(Player player);

    /**
     * Notify the player that an incorrect accusation was made.
     *
     * @param player the player that made the incorrect accusation
     */
    void notifyFailure(Player player);

    /**
     * Notify the player that the game is over.
     */
    void notifyGameOver();


    /**
     * Display the game in a manner fitting to this implementation. For instance,
     * a text-based implementation might print an ASCII map; a GUI based application would update its GUI.
     *
     * @param game The current game.
     * @param blockedLocations The currently inaccessible locations on the board.
     * @param weaponLocations The locations of the weapons currently on the board.
     */
    void showGame(Game game, Set<Location<Integer>> blockedLocations, Map<Room, Weapon> weaponLocations);

    /**
     * Ask the player what action they want to take for their turn.
     *
     * @param possibleOptions The possible actions that the player can take at this time.
     * @param player The player to ask
     * @return The action that the player wants to perform.
     */
    TurnOption requestPlayerChoiceForTurn(Set<TurnOption> possibleOptions, Player player, int remainingMoves);

    /**
     * Request a movement on the board that is exactly the given distance.
     * The interface guarantees that the move is valid.
     *
     * @param player the player to move
     * @param board The game board.
     * @param blockedLocations Any blocked locations that cannot be travelled through.
     * @param distance the distance the move must be
     * @return a sequence of directions making up the move
     */
    Board.Path requestPlayerMove(Player player, Board board, Set<Location<Integer>> blockedLocations, int distance);


    /**
     * Request an accusation from this player.
     *
     * If the player does not want to make one, then this method will return null.
     *
     * @param player the player to request the accusation from
     * @return an accusation from this player or null if no accusation is made
     */
    Optional<Suggestion> requestPlayerAccusation(Player player);

    /**
     * Request a suggestion from a player that uses the given room.
     *
     * @param player the player to get the suggestion from
     * @param room the room that is part of the suggestion
     * @return a suggestion as to what happened to the victim
     */
    Optional<Suggestion> requestPlayerSuggestion(Player player, Room room);

    /**
     * Request a response to a suggestion made by another player that could disprove the validity of it.
     *
     * If the player cannot disprove the response then null will be returned.
     *
     * @param player the player to get the response from
     * @param possibleResponses the possible responses the player can make
     * @return a response to a suggestion made by another player
     */
    SuggestionResponse requestPlayerResponse(Player player, List<SuggestionResponse> possibleResponses);

    /**
     * Notify the player with a response to their suggestion.
     *
     * @param player the player to whom the response is being given
     * @param response a response to the suggestion the player made
     */
    void notifyPlayerResponse(Player player, SuggestionResponse response);
}
