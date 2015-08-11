package swen222.cluedo.model;

public enum TurnOption {
    Move,
    Suggestion,
    Accusation,
    EndTurn;

    @Override
    public String toString() {
        switch (this) {
            case Move:
                return "Move your character";
            case Suggestion:
                return "Make a suggestion";
            case Accusation:
                return "Make an accusation";
            case EndTurn:
                return "End your turn";
            default:
                return super.toString();
        }
    }
}
