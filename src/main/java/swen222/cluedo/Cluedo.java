package swen222.cluedo;

import swen222.cluedo.model.Board;
import swen222.cluedo.model.GameState;
import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class Cluedo {

    public static void main(String[] args) {
        //Get the number of players
        int numPlayers = 0; //TODO: get the number of players from the interface
        CluedoInterface cluedoInterface = null; //TODO: replace with a valid implementation.

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

        List<CluedoCharacter> availableCharacters =  Arrays.asList(CluedoCharacter.values());

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            CluedoCharacter character = cluedoInterface.askToSelectACharacter(availableCharacters);
            Player player = new Player(character, hands[i], cluedoInterface);
            players.add(player);
        }


        GameState gameState = new GameState(board, solution, players);

        gameState.gameLoop(gameState.allPlayers);

    }
}