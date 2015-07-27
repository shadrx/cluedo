package swen222.cluedo.game.model;

import swen222.cluedo.game.model.card.Room;

import javax.xml.stream.Location;
import java.util.HashMap;
import java.util.Map;

public class Board {

    public final Tile tiles[][];

    public static class Tile {
        public final Room room;
        public final Map<Direction, Location> adjacentLocations;
        // two tiles are adjacent if there is no wall between them

        public Tile(Room room, Map<Direction, Location> adjacentLocations) {
            this.room = room;
            this.adjacentLocations = adjacentLocations;
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


    private Room roomForCharacter(Character c) {
        switch (c) {
            case 'K': return Room.Kitchen;
            case 'B': return Room.Ballroom;
            case 'C': return Room.Conservatory;
            case 'D': return Room.DiningRoom;
            case 'I': return Room.BilliardRoom;
            case 'H': return Room.Hall;
            case 'L': return Room.Lounge;
            case 'S': return Room.Study;
            case 'R': return Room.Library;
            default: return null;
        }
    }

    private Map<Direction, Location> adjacentLocations(int mask, int locationX, int locationY) {
        Map<Direction, Location> adjacentLocations = new HashMap<Direction, Room>(4);
        if (mask & 1 != 0) {
            adjacentLocations.put(Direction.Left, new Location(locationX - 1, locationY));
        }
        if (mask & (1 << 1) != 0) {
            adjacentLocations.put(Direction.Up, new Location(locationX, locationY - 1));
        }
        if (mask & (1 << 2) != 0) {
            adjacentLocations.put(Direction.Right, new Location(locationX + 1, locationY));
        }
        if (mask & (1 << 3) != 0) {
            adjacentLocations.put(Direction.Down, new Location(locationX, locationY + 1));
        }
        return adjacentLocations;
    }

    /**
     * Links two tiles so that they are marked as being adjacent. Any direction not already mapped to a tile will be mapped to the location of the other tile.
     * @param t1 The first tile.
     * @param l1 The location of the first tile.
     * @param t2 The second tile.
     * @param l2 The location of the second tile.
     */
    private void linkTiles(Tile t1, Location l1, Tile t2, Location l2) {
        for (Direction d : Direction.values()) {
            if (t1.adjacentLocations.get(d) == null) {
                t1.adjacentLocations.put(d, l2);
            }

            if (t2.adjacentLocations.get(d) == null) {
                t2.adjacentLocations.put(d, l1);
            }
        }
    }

    private Tile[][] loadMap(String[] lines) {
        Tile[][] map = new Tile[lines.length][];

        Tile kitchenPathTile = null, conservatoryPathTile = null, studyPathTile = null, loungePathTile = null;
        Location kitchenPathLocation = null, conservatoryPathLocation = null, studyPathLocation = null, loungePathLocation = null;

        int y = 0;
        for (String line : lines) {
            String[] tiles = line.split(" ");
            map[i] = new Tile[tiles.length];
            int x = 0;
            for (String tile : tiles) {
                Room room = roomForCharacter(tile.characterAt(0));
                Integer mask = Integer.parseInt(tile.substring(1));
                Map<Direction, Location> adjacentLocations = this.adjacentLocations(mask, x, y);
                Tile tile = new Tile(room, adjacentLocations);
                map[x][y] = tile;


                if (mask & (1 << 4) != 0) { //We need to check for the special path tiles within this method – we can't assign the locations until we've read all the tiles.
                   kitchenPathTile = tile; kitchenPathLocation = new Location(x, y);
                } else if (mask & (1 << 5) != 0) {
                    studyPathTile = tile; studyPathTile = new Location(x, y);
                } else if (mask & (1 << 6) != 0) {
                    conservatoryPathTile = tile; conservatoryPathLocation = new Location(x, y);
                } else if (mask & (1 << 7) != 0) {
                    loungePathTile = tile; loungePathLocation = new Location(x, y);
                }

                x++;
            }

            y++;
        }

        this.linkTiles(kitchenPathTile, kitchenPathLocation, studyPathTile, studyPathLocation);
        this.linkTiles(loungePathTile, loungePathLocation, conservatoryPathTile, conservatoryPathLocation);

    }
}
