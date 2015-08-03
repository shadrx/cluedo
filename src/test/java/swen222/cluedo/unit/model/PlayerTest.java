package swen222.cluedo.unit.model;

import org.junit.Before;
import org.junit.Test;
import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.SuggestionResponse;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PlayerTest {
    private Player player = null;

    @Before
    public void setupPlayer() {
        Set<Card> cards = new HashSet<>();

        cards.add(CluedoCharacter.ColonelMustard);
        cards.add(Weapon.Candlestick);
        cards.add(Room.Ballroom);


        this.player = new Player(CluedoCharacter.ColonelMustard, cards, null);
    }

    @Test
    public void testPossibleResponsesReturnsAllForRequestWithAllCards() {
        List<SuggestionResponse> possibleResponses = player.possibleResponses(new Suggestion(CluedoCharacter.ColonelMustard, Weapon.Candlestick, Room.Ballroom));
        assertEquals(3, possibleResponses.size());
    }

    @Test
    public void testPossibleResponsesReturnsNoneForRequestWithNoCards() {
        List<SuggestionResponse> possibleResponses = player.possibleResponses(new Suggestion(CluedoCharacter.MissScarlet, Weapon.Dagger, Room.BilliardRoom));
        assertEquals(0, possibleResponses.size());
    }

    @Test
    public void testStartsAtStartLocation() {
        for (CluedoCharacter character : CluedoCharacter.values()) {
            Player player = new Player(character, null, null);
            assertEquals(player.location(), character.startLocation());
        }
    }

}
