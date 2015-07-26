package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.game.model.Suggestion;
import swen222.cluedo.game.model.card.Character;
import swen222.cluedo.game.model.card.Room;
import swen222.cluedo.game.model.card.Weapon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SuggestionTest {

    @Test
    public void suggestionsAreEqualIfThreeCardsAreTheSame() {
        Suggestion equalSuggestionOne = new Suggestion(Character.COLONEL_MUSTARD, Weapon.DAGGER, Room.BALLROOM);
        Suggestion equalSuggestionTwo = new Suggestion(Character.COLONEL_MUSTARD, Weapon.DAGGER, Room.BALLROOM);

        assertEquals(equalSuggestionOne, equalSuggestionTwo);
    }

    @Test
    public void suggestionsAreNotEqualIfThreeCardsAreDifferent() {
        Suggestion equalSuggestionOne = new Suggestion(Character.COLONEL_MUSTARD, Weapon.DAGGER, Room.BALLROOM);
        Suggestion equalSuggestionTwo = new Suggestion(Character.COLONEL_MUSTARD, Weapon.LEADPIPE, Room.BALLROOM);

        assertNotEquals(equalSuggestionOne, equalSuggestionTwo);
    }
}
