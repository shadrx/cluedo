package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SuggestionTest {

    @Test
    public void suggestionsAreEqualIfThreeCardsAreTheSame() {
        Suggestion equalSuggestionOne = new Suggestion(CluedoCharacter.ColonelMustard , Weapon.Knife, Room.Ballroom);
        Suggestion equalSuggestionTwo = new Suggestion(CluedoCharacter.ColonelMustard, Weapon.Knife, Room.Ballroom);

        assertEquals(equalSuggestionOne, equalSuggestionTwo);
    }

    @Test
    public void suggestionsAreNotEqualIfThreeCardsAreDifferent() {
        Suggestion equalSuggestionOne = new Suggestion(CluedoCharacter.ColonelMustard, Weapon.Knife, Room.Ballroom);
        Suggestion equalSuggestionTwo = new Suggestion(CluedoCharacter.ColonelMustard, Weapon.LeadPipe, Room.Ballroom);

        assertNotEquals(equalSuggestionOne, equalSuggestionTwo);
    }
}
