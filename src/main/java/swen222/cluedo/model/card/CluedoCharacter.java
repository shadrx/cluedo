package swen222.cluedo.model.card;

import swen222.cluedo.model.Location;

import java.awt.*;

public enum CluedoCharacter implements Card {
    MissScarlet,
    ColonelMustard,
    MrsWhite,
    ReverendGreen,
    MrsPeacock,
    ProfessorPlum;

    public Location<Integer> startLocation() {
        switch (this) {
            case MissScarlet:
                return new Location<>(7, 24);
            case ColonelMustard:
                return new Location<>(0, 17);
            case MrsWhite:
                return new Location<>(9, 0);
            case ReverendGreen:
                return new Location<>(14, 0);
            case MrsPeacock:
                return new Location<>(23, 6);
            case ProfessorPlum:
                return new Location<>(23, 19);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case MissScarlet:
                return "Miss Scarlet";
            case ColonelMustard:
                return "Colonel Mustard";
            case MrsWhite:
                return "Mrs. White";
            case ReverendGreen:
                return "Reverend Green";
            case MrsPeacock:
                return "Mrs. Peacock";
            case ProfessorPlum:
                return "Professor Plum";
            default:
                return null;
        }
    }

    @Override
    public String imageName() {
        return this.name() + ".png"; //the name as specified in the enum, plus .png
    }

    public Color colour() {
        switch (this) {
            case MissScarlet:
                return Color.red;
            case ColonelMustard:
                return Color.decode("#eacd3a");
            case MrsWhite:
                return Color.white;
            case ReverendGreen:
                return Color.decode("#405935");
            case MrsPeacock:
                return Color.decode("#c6139a");
            case ProfessorPlum:
                return Color.decode("#46347a");
            default:
                return Color.black;
        }
    }
}
