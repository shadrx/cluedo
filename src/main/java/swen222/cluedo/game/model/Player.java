package swen222.cluedo.game.model;


import swen222.cluedo.game.model.card.Card;

import java.util.Set;

public class Player {

    public final Character character;
    private Location location;

    /** Each player has their own referenced interface, so that, if desired, the output for each player can be sent to a different interface
     * without providing other players with information.
     */
    public final CluedoInterface cluedoInterface;

    public final Set<Card> cards;

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
     * Gives the possible responses to a suggestion for this player.
     * @param suggestion The suggestion that the player is responding to
     * @return A list containing all the possible responses that this player may give. May be empty.
     */
    List<Suggestion> possibleResponses(Suggestion suggestion) {
        List<SuggestionResponse> responses = new ArrayList<SuggestionResponse>(3);

        if (this.cards.contains(character)) {
            responses.add(new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, character));
        }
        if (this.cards.contains(weapon)) {
            responses.add(new SuggestionResponse(SuggestionResponse.Type.DisproveWeapon, weapon));
        }
        if (this.cards.contains(room)) {
            responses.add(new SuggestionResponse(SuggestionResponse.Type.DisproveRoom, room));
        }
        return responses;
    }
}
