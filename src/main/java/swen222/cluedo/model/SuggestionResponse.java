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
                return "The crime was not committed in the " + this.card + ".";
            default:
                return "No one is able to refute your suggestion.";
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SuggestionResponse)) {
            return false;
        }

        SuggestionResponse otherResponse = (SuggestionResponse) other;
        return otherResponse.type == this.type && otherResponse.card.equals(this.card);
    }

    public enum Type {
        DisproveCharacter,
        DisproveWeapon,
        DisproveRoom,
        UnableToDisprove
    }
}
