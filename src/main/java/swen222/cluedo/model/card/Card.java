package swen222.cluedo.model.card;

import swen222.cluedo.model.Suggestion;

import java.util.*;

/**
 * The cards available in the game
 */
public interface Card {

    public static Set<Card>[] dealHands(int numHands, Suggestion solution) {
        Set<CluedoCharacter> availableCharacters = new HashSet<CluedoCharacter>(Arrays.asList(CluedoCharacter.values()));
        Set<Weapon> availableWeapons = new HashSet<Weapon>(Arrays.asList(Weapon.values()));
        Set<Room> availableRooms = new HashSet<Room>(Arrays.asList(Room.values()));

        availableCharacters.remove(solution.character);
        availableWeapons.remove(solution.weapon);
        availableRooms.remove(solution.room);

        Set<Card> availableCards = new HashSet<Card>(availableCharacters);
        availableCards.addAll(availableWeapons);
        availableCards.addAll(availableRooms);

        Set<Card>[] hands = new Set[numHands];

        int i = 0;

        for (Card c : availableCards) {
            Set<Card> hand = hands[i % numHands];
            if (hand == null) { hand = new HashSet<Card>(); hands[i % numHands] = hand; }

            hand.add(c);
            i++;
        }

        return hands;
    }
}
