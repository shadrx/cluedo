package swen222.cluedo.game.model;


import swen222.cluedo.game.model.card.Card;

/**
 * A response to a player's suggestion.
 */
public class SuggestionResponse {

    public final Type type;
    public final Card card;

    public SuggestionResponse(Type type, Card card) {
        this.type = type;
        this.card = card;
    }

    enum Type {
        DISPROVE_CHARACTER,
        DISPROVE_WEAPON,
        DISPROVE_ROOM,
        UNABLE_TO_DISPROVE
    }
}
