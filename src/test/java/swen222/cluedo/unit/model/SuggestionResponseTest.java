package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.model.SuggestionResponse;
import swen222.cluedo.model.card.CluedoCharacter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SuggestionResponseTest {

    @Test
    public void responsesAreEqualIfCardAndTypeAreTheSame() {
        SuggestionResponse equalResponseOne = new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, CluedoCharacter.ColonelMustard);
        SuggestionResponse equalResponseTwo = new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, CluedoCharacter.ColonelMustard);

        assertEquals(equalResponseOne, equalResponseTwo);
    }

    @Test
    public void responsesAreNotEqualIfCardsAreDifferent() {
        SuggestionResponse differentResponseOne = new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, CluedoCharacter.ColonelMustard);
        SuggestionResponse differentResponseTwo = new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, CluedoCharacter.MissScarlet);

        assertNotEquals(differentResponseOne, differentResponseTwo);
    }

    @Test
    public void responsesAreNotEqualIfTypesAreDifferent() {
        SuggestionResponse differentResponseOne = SuggestionResponse.UnableToDisprove();
        SuggestionResponse differentResponseTwo = new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, CluedoCharacter.MissScarlet);

        assertNotEquals(differentResponseOne, differentResponseTwo);
    }
}
