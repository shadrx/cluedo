package swen222.cluedo.model;

import swen222.cluedo.model.card.Room;
import swen222.cluedo.CluedoInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Game {
    public final Board board;
    public final Suggestion solution;
    public final List<Player> allPlayers;

    public Game(Board board, Suggestion solution, List<Player> players) {
        this.board = board;
        this.solution = solution;
        this.allPlayers = players;
    }

    /**
     * Returns allPlayers minus 'player', offset so that the player immediately after 'player' is at the start
     * of the returned list.
     */
    private List<Player> otherPlayersFrom(Player player) {
        int index = allPlayers.indexOf(player);
        List<Player> retVal = new ArrayList<>(this.allPlayers.subList(index + 1, allPlayers.size()));
        retVal.addAll(this.allPlayers.subList(0, index));
        return retVal;
    }

    private boolean checkGameOver(List<Player> players) {
        if (players.isEmpty()) {
            for (Player p : this.allPlayers) {
                p.cluedoInterface.notifyGameOver();
            }
            return true;
        }
        return false;
    }

    /**
     * @param playersInPlay The players who are currently playing the game.
     * @param player        the player with the option to make the accusation.
     * @return true if an accusation was made
     */
    private boolean checkForAccusation(List<Player> playersInPlay, Player player) {
        Optional<Suggestion> accusation = player.cluedoInterface.requestPlayerAccusation(player);
        if (accusation.isPresent()) {
            if (accusation.get().equals(this.solution)) {
                CluedoInterface cI = null;
                for (Player p : this.allPlayers) {
                    if (p.cluedoInterface != cI) { //don't notify the same interface object multiple times.
                        p.cluedoInterface.notifySuccess(player);
                        cI = p.cluedoInterface;
                    }
                }
            } else {
                CluedoInterface cI = null;
                for (Player p : this.allPlayers) {
                    if (p.cluedoInterface != cI) { //don't notify the same interface object multiple times.
                        p.cluedoInterface.notifyFailure(player);
                        cI = p.cluedoInterface;
                    }
                }

                int i = playersInPlay.indexOf(player);
                List<Player> newPlayers = new ArrayList<>(playersInPlay.subList(i + 1, playersInPlay.size()));
                newPlayers.addAll(playersInPlay.subList(0, i)); //The play loop now starts from the next player and skips over the player who made the incorrect accusation.
                this.gameLoop(newPlayers);
            }
            return true;
        }
        return false;
    }

    private void checkForSuggestion(Player player, Room room) {
        Optional<Suggestion> suggestion = player.cluedoInterface.requestPlayerSuggestion(player, room);
        if (suggestion.isPresent()) {
            SuggestionResponse response = SuggestionResponse.UnableToDisprove();

            for (Player otherPlayer : this.otherPlayersFrom(player)) {
                List<SuggestionResponse> possibleResponses = otherPlayer.possibleResponses(suggestion.get());
                if (possibleResponses.size() > 0) {
                    response = otherPlayer.cluedoInterface.requestPlayerResponse(otherPlayer, possibleResponses);
                    break;
                }
            }

            player.cluedoInterface.notifyPlayerResponse(player, response);
        }
    }

    public void gameLoop(List<Player> players) {

        if (this.checkGameOver(players)) {
            return;
        }

        while (true) {
            for (Player player : players) {

                player.cluedoInterface.notifyStartOfTurn(player);

                if (this.checkForAccusation(players, player)) {
                    return;
                }

                player.cluedoInterface.showGame(this);

                int diceRoll = (int)(Math.floor(Math.random() * 6) + 1);

                Optional<Location> newLocation = Optional.empty();
                while (!newLocation.isPresent()) {
                    List<Direction> moveSequence = player.cluedoInterface.requestPlayerMove(player, diceRoll);
                    Stream<Location> otherPlayerLocations = players.stream().filter(player1 -> player1 != player)
                            .map(player2 -> player2.location());
                    newLocation = this.board.newLocationForMove(moveSequence, player.location(), otherPlayerLocations);
                }
                player.setLocation(newLocation.get());

                Board.Tile tile = this.board.tileAtLocation(player.location());

                if (tile.room.isPresent()) {
                    this.checkForSuggestion(player, tile.room.get());
                }
            }
        }
    }


}