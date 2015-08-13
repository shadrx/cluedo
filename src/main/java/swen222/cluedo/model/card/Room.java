package swen222.cluedo.model.card;

public enum Room implements Card{
    Ballroom,
    Kitchen,
    Conservatory,
    DiningRoom,
    BilliardRoom,
    Lounge,
    Hall,
    Library,
    Study;

    /**
     * Returns the name to display on the map.
     */
    public String shortName() {
        switch (this) {
            case Conservatory:
                return "Conserv.";
            case BilliardRoom:
                return "Billiard";
            default:
                return this.toString();
        }
    }

    @Override
    public String imageName() {
        return super.toString() + ".png"; //the name as specified in the enum, plus .png
    }

    @Override
    public String toString() {
        switch (this) {
            case DiningRoom:
                return "Dining Room";
            case BilliardRoom:
                return "Billiard Room";
            default:
                return super.toString();
        }
    }
}
