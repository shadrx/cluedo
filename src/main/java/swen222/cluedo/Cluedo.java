package swen222.cluedo;

import swen222.cluedo.gui.CluedoFrame;
import swen222.cluedo.gui.MessageAndCardDialog;
import swen222.cluedo.gui.PlayerSelectionDialog;
import swen222.cluedo.model.Board;
import swen222.cluedo.model.Game;
import swen222.cluedo.model.Player;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.asciiinterface.ASCIIInterface;
import swen222.cluedo.model.card.Room;
import swen222.cluedo.model.card.Weapon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class Cluedo {

    public static void main(String[] args) {

        //new MessageAndCardDialog(null, "Title", "This is a longform message meant to span multiple lines.", Arrays.asList(CluedoCharacter.MissScarlet));
        new PlayerSelectionDialog(null);

        //Get the number of players
        CluedoInterface cluedoInterface = new ASCIIInterface(System.in, System.out);

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
            CluedoCharacter character = cluedoInterface.askToSelectACharacter(availableCharacters);
            availableCharacters.remove(character);
            Player player = new Player(character, hands[i], cluedoInterface);
            players.add(player);
        }


        Game game = new Game(board, solution, players);

        CluedoFrame frame = new CluedoFrame();
        frame.canvas.setGameState(game);

      //  game.gameLoop(game.allPlayers);


    }
}