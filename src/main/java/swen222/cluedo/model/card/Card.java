package swen222.cluedo.model.card;

import swen222.cluedo.model.Suggestion;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The cards available in the game
 */
public interface Card {

    static Set<Card> allCards() {
        Set<Card> cards = new HashSet<>();
        cards.addAll(Arrays.asList(CluedoCharacter.values()));
        cards.addAll(Arrays.asList(Room.values()));
        cards.addAll(Arrays.asList(Weapon.values()));
        return cards;
    }

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

        int cardsPerHand = availableCards.size() / numHands;

        for (Card c : availableCards) {
            Set<Card> hand = hands[i % numHands];
            if (hand == null) {
                hand = new HashSet<>();
                hands[i % numHands] = hand;
            }

            hand.add(c);
            i++;

            if (i >= cardsPerHand * numHands) {
                return hands;
            }
        }

        return hands;
    }

    static BufferedImage loadImage(String imageFileName){
        URL url = Card.class.getClassLoader().getResource(imageFileName);
        if(url == null) return null;

        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    String imageName();

    default Image cardImage() {
        String imageName = "images/" + this.imageName();
        return loadImage(imageName);
    }
}
