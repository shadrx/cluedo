package swen222.cluedo.game.model;

import swen222.cluedo.game.model.card.Room;

import java.util.Map;

public class Board {

    private Tile tiles[][];

    public static class Tile {
        Room room;
        Map<Direction, Room> adjacentLocations;

        // two tiles are adjacent if there is no wall between them
    }

    /**
     * Gets the tile at the given location.
     *
     * @param location the location to get the tile from
     * @return the tile at the given location
     */
    public Tile tileAtLocation(Location location) {
        return tiles[location.x][location.y];
    }

}
