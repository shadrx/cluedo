package swen222.cluedo.model.card;

import swen222.cluedo.model.Suggestion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The cards available in the game
 */
public interface Card {

    static Set<Card>[] dealHands(int numHands, Suggestion solution) {
        Set<CluedoCharacter> availableCharacters = new HashSet<>(Arrays.asList(CluedoCharacter.values()));
        Set<Weapon> availableWeapons = new HashSet<>(Arrays.asList(Weapon.values()));
        Set<Room> availableRooms = new HashSet<>(Arrays.asList(Room.values()));

        availableCharacters.remove(solution.character);
        availableWeapons.remove(solution.weapon);
        availableRooms.remove(solution.room);

        Set<Card> availableCards = new HashSet<>(availableCharacters);
        availableCards.addAll(availableWeapons);
        availableCards.addAll(availableRooms);

        @SuppressWarnings("unchecked")
        Set<Card>[] hands = new Set[numHands];

        int i = 0;

        for (Card c : availableCards) {
            Set<Card> hand = hands[i % numHands];
            if (hand == null) {
                hand = new HashSet<>();
                hands[i % numHands] = hand;
            }

            hand.add(c);
            i++;
        }

        return hands;
    }
}
