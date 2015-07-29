package swen222.cluedo.userinterface;

import swen222.cluedo.model.*;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;

import java.util.List;
import java.util.Optional;

public interface CluedoInterface {

    int getNumberOfPlayers(int min, int max);

    /** Requests that the player choose a character to play as. */
    CluedoCharacter askToSelectACharacter(List<CluedoCharacter> availableCharacters);

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
     * Display the game state in a mannner fitting to this implementation. For instance, a text-based implementation might print an ASCII map;
     * a GUI based application would update its GUI.
     * @param gameState The current game state.
     */
    void showGameState(GameState gameState);

    /**
     * Request a movement on the board that is exactly the given distance.
     *
     * @param player the player to move
     * @param distance the distance the move must be
     * @return a sequence of directions making up the move
     */
    List<Direction> requestPlayerMove(Player player, int distance);



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
