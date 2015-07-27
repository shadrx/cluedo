package swen222.cluedo.model;


import swen222.cluedo.model.card.Card;

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

    public static SuggestionResponse UnableToDisprove() {
        return new SuggestionResponse(Type.UnableToDisprove, null);
    }

    enum Type {
        DisproveCharacter,
        DisproveWeapon,
        DisproveRoom,
        UnableToDisprove
    }
}
