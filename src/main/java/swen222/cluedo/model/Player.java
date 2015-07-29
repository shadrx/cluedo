package swen222.cluedo.model;


import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.userinterface.CluedoInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Player {

    public final CluedoCharacter character;
    /** Each player has their own referenced interface, so that, if desired, the output for each player can be sent to a different interface
     * without providing other players with information.
     */
    public final CluedoInterface cluedoInterface;
    public final Set<Card> cards;
    private Location location;

    public Player(CluedoCharacter character, Set<Card> cards, CluedoInterface cluedoInterface) {
        this.character = character;
        this.location = character.startLocation();
        this.cards = cards;
        this.cluedoInterface = cluedoInterface;
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
    List<SuggestionResponse> possibleResponses(Suggestion suggestion) {
        List<SuggestionResponse> responses = new ArrayList<>(3);

        if (this.cards.contains(suggestion.character)) {
            responses.add(new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, suggestion.character));
        }
        if (this.cards.contains(suggestion.weapon)) {
            responses.add(new SuggestionResponse(SuggestionResponse.Type.DisproveWeapon, suggestion.weapon));
        }
        if (this.cards.contains(suggestion.room)) {
            responses.add(new SuggestionResponse(SuggestionResponse.Type.DisproveRoom, suggestion.room));
        }
        return responses;
    }
}
