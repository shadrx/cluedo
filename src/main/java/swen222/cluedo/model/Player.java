package swen222.cluedo.model;


import swen222.cluedo.CluedoInterface;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Player {

    public final Optional<String> name;
    public final CluedoCharacter character;
    /** Each player has their own referenced interface, so that, if desired, the output for each player can be sent to a different interface
     * without providing other players with information.
     */
    public final CluedoInterface cluedoInterface;
    public final Set<Card> cards;
    private Location<Integer> location;

    public Player(Optional<String> name, CluedoCharacter character, Set<Card> cards, CluedoInterface cluedoInterface) {
        this.name = name;
        this.character = character;
        this.location = character.startLocation();
        this.cards = cards;
        this.cluedoInterface = cluedoInterface;
    }

    public Player(CluedoCharacter character, Set<Card> cards, CluedoInterface cluedoInterface) {
        this(Optional.empty(), character, cards, cluedoInterface);
    }

    public Player(Player player) {
        this.name = player.name;
        this.character = player.character;
        this.cards = player.cards;
        this.location = player.location.copy();
        this.cluedoInterface = player.cluedoInterface;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player)obj;

        return super.equals(obj) &&
                this.name.equals(other.name) &&
                this.character == other.character &&
                this.cluedoInterface == other.cluedoInterface &&
                this.cards.equals(other.cards) &&
                this.location.equals(other.location);
    }

    public Player copy() {
        return new Player(this);
    }

    /**
     * Get the location of this player.
     *
     * @return the location of this player
     */
    public Location<Integer> location() {
        return location;
    }

    /**
     * Set the location of this player.
     *
     * @param location the location to set
     */
    public void setLocation(Location<Integer> location) {
        this.location = location;
    }


    /**
     * Gives the possible responses to a suggestion for this player.
     * @param suggestion The suggestion that the player is responding to
     * @return A list containing all the possible responses that this player may give. May be empty.
     */
    public List<SuggestionResponse> possibleResponses(Suggestion suggestion) {
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
