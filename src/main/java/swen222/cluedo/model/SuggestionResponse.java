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

    public String toString() {
        switch (this.type) {
            case DisproveCharacter:
                return this.card + " didn't commit the crime.";
            case DisproveWeapon:
                return "The " + this.card + " was not used in the crime.";
            case DisproveRoom:
                return "The crime was not commited in the " + this.card + ".";
            default:
                return null;
        }
    }

    enum Type {
        DisproveCharacter,
        DisproveWeapon,
        DisproveRoom,
        UnableToDisprove
    }
}
