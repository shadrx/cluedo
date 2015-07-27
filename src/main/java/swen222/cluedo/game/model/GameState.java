package swen222.cluedo.game.model;

import java.util.List;
import java.util.Optional;

import swen222.cluedo.game.model.Board;
import swen222.cluedo.game.model.Suggestion;
import swen222.cluedo.game.model.Player;

class GameState {
    public final Board board;
    public final Suggestion solution;
    public final List<Player> allPlayers;

    /**
     * Recursively finds the new location given a move sequence from a start location
     * @param move A sequence of directions in which the user wishes to move
     * @param startLocation
     * @return The new location, if the move sequence is valid; else, the empty optional.
     */
    Optional<Location> newLocationForMove(List<Direction> move, Location startLocation) {
        if (move.size() > 0) {
            Direction head = move.get(0);
            Tile tile = board.tileAtLocation(startLocation);
            Location nextLocationOrNull = tile.adjacentLocations(head);

            if (nextLocationOrNull == null) {
                return Optional<Location>.empty();
            }
            List<Direction> tail = move.subList(1, move.size());
            return newLocationForMove(tail, nextLocationOrNull);
        }
        return startLocation;
    }

}