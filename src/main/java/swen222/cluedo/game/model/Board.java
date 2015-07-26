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

    static class Location  {
        int x;
        int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Location location = (Location) o;

            if (x != location.x) return false;
            return y == location.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
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
