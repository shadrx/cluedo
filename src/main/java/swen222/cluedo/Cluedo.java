package swen222.cluedo;

import swen222.cluedo.gui.CluedoFrame;
import swen222.cluedo.gui.CluedoGUIController;
import swen222.cluedo.gui.PlayerSelectionDialog;
import swen222.cluedo.gui.PlayerSuggestionDialog;
import swen222.cluedo.model.*;
import swen222.cluedo.model.card.Card;
import swen222.cluedo.model.card.CluedoCharacter;
import swen222.cluedo.asciiinterface.ASCIIInterface;
import swen222.cluedo.model.card.Room;
import utilities.Pair;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class Cluedo {



    public static void main(String[] args) {
        //Get the number of players
        CluedoInterface cluedoInterface = new CluedoGUIController();

        new Thread(() -> {
            List<SuggestionResponse> possibleResponses = Arrays.asList(new SuggestionResponse(SuggestionResponse.Type.DisproveCharacter, CluedoCharacter.MissScarlet),
                    new SuggestionResponse(SuggestionResponse.Type.DisproveRoom, Room.Conservatory));
            SuggestionResponse selectedResponse = cluedoInterface.requestPlayerResponse(new Player(Optional.of("Thomas"), CluedoCharacter.ColonelMustard, null, cluedoInterface), possibleResponses);
            System.out.println(selectedResponse);

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

            CluedoFrame frame = new CluedoFrame();
            frame.canvas.setGameState(game);

            //  game.gameLoop(game.allPlayers);
        }).run();
    }
}