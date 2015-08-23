package swen222.cluedo.model.card;

import utilities.Utils;

import java.awt.Image;

public enum Weapon implements Card {
    Candlestick,
    Knife,
    LeadPipe,
    Revolver,
    Rope,
    Wrench;

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
        return super.toString() + ".png"; //the name as specified in the enum, plus .png
    }

    public Image tokenImage() {
        String imageName = "images/weapon_tokens/" + this.imageName();
        return Utils.loadImage(imageName);
    }
}
