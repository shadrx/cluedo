package swen222.cluedo.model.card;

public enum Weapon implements Card {
    Candlestick,
    Dagger,
    LeadPipe,
    Revolver,
    Rope,
    Spanner;

    @Override
    public String toString() {
        switch (this) {
            case LeadPipe:
                return "Lead Pipe";
            default:
                return super.toString();
        }
    }

    @Override
    public String imageName() {
        switch (this) {
            case Spanner:
                return "Wrench.png";
            default:
                return super.toString() + ".png"; //the name as specified in the enum, plus .png
        }
    }
}
