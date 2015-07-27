package swen222.cluedo.model;

import java.util.List;
import java.util.Optional;

import swen222.cluedo.model.Board;
import swen222.cluedo.model.Suggestion;
import swen222.cluedo.model.Player;

public class GameState {
    public final Board board;
    public final Suggestion solution;
    public final List<Player> allPlayers;

    public GameState(Board board, Suggestion solution, List<Player> players) {
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
        List<Player> retVal = this.allPlayers.subList(index + 1, allPlayers.size());
        retVal.addAll(this.allPlayers.subList(0, index));
        return retVal;
    }

    public void gameLoop(List<Player> players) {
        if (players.isEmpty()) {
            for (Player p : this.allPlayers) {
                p.cluedoInterface.notifyGameOver();
            }
            return;
        }

        while (true) {
            int i = 0;
            for (Player player : players) {
                Optional<Suggestion> accusation = player.cluedoInterface.requestPlayerAccusation(player);
                if (accusation.isPresent()) {
                    if (accusation.get().equals(this.solution)) {
                        for (Player p : this.allPlayers) { p.cluedoInterface.notifySuccess(player); }
                        return;
                    } else {
                        for (Player p : this.allPlayers) { p.cluedoInterface.notifyFailure(player); }
                        List<Player> newPlayers = players.subList(i + 1, players.size());
                        newPlayers.addAll(players.subList(0, i)); //The play loop now starts from the next player and skips over the player who made the incorrect accusation.
                        this.gameLoop(newPlayers);
                        return;
                    }
                }

                int diceRoll = (int)(Math.floor(Math.random() * 6) + 1);

                Optional<Location> newLocation = Optional.empty();
                while (!newLocation.isPresent()) {
                    List<Direction> moveSequence = player.cluedoInterface.requestPlayerMove(player, diceRoll);
                    newLocation = this.newLocationForMove(moveSequence, player.location());
                }
                player.setLocation(newLocation.get());

                Board.Tile tile = this.board.tileAtLocation(player.location());

                if (tile.room.isPresent()) {
                    Optional<Suggestion> suggestion = player.cluedoInterface.requestPlayerSuggestion(player, tile.room.get());
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

                i++;
            }
        }
    }

    /**
     * Recursively finds the new location given a move sequence from a start location
     * @param move A sequence of directions in which the user wishes to move
     * @param startLocation
     * @return The new location, if the move sequence is valid; else, the empty optional.
     */
    Optional<Location> newLocationForMove(List<Direction> move, Location startLocation) {
        if (move.size() > 0) {
            Direction head = move.get(0);
            Board.Tile tile = board.tileAtLocation(startLocation);
            Location nextLocationOrNull = tile.adjacentLocations.get(head);

            if (nextLocationOrNull == null) {
                return Optional.empty();
            }
            List<Direction> tail = move.subList(1, move.size());
            return newLocationForMove(tail, nextLocationOrNull);
        }
        return Optional.of(startLocation);
    }

}