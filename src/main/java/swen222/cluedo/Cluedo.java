package swen222.cluedo;

import swen222.cluedo.gui.CluedoGUIController;
import swen222.cluedo.model.Board;
import swen222.cluedo.model.Game;
import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import utilities.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class Cluedo {



    public static void main(String[] args) {
        //Get the number of players
        CluedoInterface cluedoInterface = new CluedoGUIController();

            int numPlayers = cluedoInterface.getNumberOfPlayers(3, 6);

            //Load the board
            Board board;
            try {
                InputStream boardStream = ClassLoader.getSystemClassLoader().getResourceAsStream("cluedo.map");
                board = new Board(boardStream, 24, 25);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Suggestion solution = Suggestion.randomSuggestion();

            Set<Card>[] hands = Card.dealHands(numPlayers, solution);

            List<CluedoCharacter> availableCharacters = new ArrayList<>(Arrays.asList(CluedoCharacter.values()));

            List<Player> players = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                Pair<Optional<String>, CluedoCharacter> nameAndCharacter = cluedoInterface.askForNameAndCharacter(availableCharacters);
                availableCharacters.remove(nameAndCharacter.y);
                Player player = new Player(nameAndCharacter.x, nameAndCharacter.y, hands[i], cluedoInterface);
                players.add(player);
            }


            Game game = new Game(board, solution, players);

            game.gameLoop(game.allPlayers);
    }
}