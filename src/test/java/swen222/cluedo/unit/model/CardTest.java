package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import java.util.HashSet;
import java.util.Set;

public class CardTest {

    @Test
    public void testDealtHandsDoNotShareCards() {
        Set<Card>[] hands = Card.dealHands(4, new Suggestion(CluedoCharacter.ColonelMustard, Weapon.Candlestick, Room.BilliardRoom));

        for (int i = 0; i < hands.length - 1; i++) {
            Set<Card> intersection = new HashSet<>(hands[0]);
            intersection.retainAll(hands[1]);
            assert (intersection.size() == 0);
        }
    }

    @Test
    public void testDealtHandsDoNotContainSuggestionCards() {
        Suggestion suggestion = new Suggestion(CluedoCharacter.ColonelMustard, Weapon.Candlestick, Room.BilliardRoom);
        Set<Card>[] hands = Card.dealHands(4, suggestion);

        for (Set<Card> hand : hands) {
            assert (!hand.contains(suggestion.character));
            assert (!hand.contains(suggestion.weapon));
            assert (!hand.contains(suggestion.room));
        }
    }
}
