package swen222.cluedo.game.model;


import swen222.cluedo.game.model.card.Card;

import java.util.Set;

public class Player {

    private final Character character;
    private Location location;

    private Set<Card> cards;

    public Player(Character character, Location location, Set<Card> cards) {
        this.character = character;
        this.location = location;
        this.cards = cards;
    }

    /**
     * Get the location of this player.
     *
     * @return the location of this player
     */
    public Location location() {
        return location;
    }

    /**
     * Set the location of this player.
     *
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get the character of this player is playing as.
     *
     * @return the character this player is playing as
     */
    public Character character() {
        return character;
    }
}
