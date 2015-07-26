package swen222.cluedo.game;

import swen222.cluedo.game.model.SuggestionResponse;
import swen222.cluedo.game.model.card.Card;
import swen222.cluedo.game.model.Direction;
import swen222.cluedo.game.model.Player;
import swen222.cluedo.game.model.Suggestion;
import swen222.cluedo.game.model.card.Room;

import java.util.List;

public interface CluedoInterface {

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
     * Request a movement on the board that is exactly the given distance.
     *
     * @param distance the distance the move must be
     * @return a sequence of directions making up the move
     */
    List<Direction> requestPlayerMove(int distance);



    /**
     * Request an accusation from this player.
     *
     * If the player does not want to make one, then this method will return null.
     *
     * @return an accusation from this player or null if no accusation is made
     */
    Suggestion requestPlayerAccusation();



    /**
     * Request a suggestion from a player that uses the given room.
     *
     * @param room the room that is part of the suggestion
     * @return a suggestion as to what happened to the victim
     */
    Suggestion requestPlayerSuggestion(Room room);

    /**
     * Request a response to a suggestion made by another player that could disprove the validity of it.
     *
     * If the player cannot disprove the response then null will be returned.
     *
     * @param possibleResponses the possible responses the player can make
     * @return a response to a suggestion made by another player
     */
    SuggestionResponse requestPlayerResponse(List<SuggestionResponse> possibleResponses);

    /**
     * Notify the player with a response to their suggestion.
     *
     * @param response a response to the suggestion the player made
     */
    void notifyPlayerResponse(Card response);
}
