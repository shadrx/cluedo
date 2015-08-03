package swen222.cluedo;

import swen222.cluedo.model.Board;
import swen222.cluedo.model.Game;
import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.userinterface.ASCIIInterface;
import swen222.cluedo.userinterface.CluedoInterface;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class Cluedo {

    public static void main(String[] args) {
        //Get the number of players
        CluedoInterface cluedoInterface = new ASCIIInterface(System.in, System.out);

        int numPlayers = cluedoInterface.getNumberOfPlayers(3, 6);

        //Load the board
        Board board;
        try {
            board = new Board(Paths.get("resources/cluedo.map"), 24, 25);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Suggestion solution = Suggestion.randomSuggestion();

        Set<Card>[] hands = Card.dealHands(numPlayers, solution);

        List<CluedoCharacter> availableCharacters = new ArrayList<>(Arrays.asList(CluedoCharacter.values()));

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            CluedoCharacter character = cluedoInterface.askToSelectACharacter(availableCharacters);
            availableCharacters.remove(character);
            Player player = new Player(character, hands[i], cluedoInterface);
            players.add(player);
        }


        Game game = new Game(board, solution, players);

        game.gameLoop(game.allPlayers);

    }
}