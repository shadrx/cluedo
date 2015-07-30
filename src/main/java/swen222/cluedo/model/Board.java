package swen222.cluedo.model;

import swen222.cluedo.model.card.Room;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Board {


    public final Tile tiles[][];
    public final int width, height;
    private Map<Room, Location> _roomCentres = new HashMap<>();

    public Board(Path mapPath, int width, int height) throws IOException {
        List<String> lines = Files.readAllLines(mapPath);
        this.tiles = loadMap(lines, width, height);
        this.width = width;
        this.height = height;
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

    /**
     * Finds the location that is in the middle of a room.
     * @param room the room to look for
     * @return the location of the central tile.
     */
    public Location centreLocationForRoom(Room room) {
        Location centre = null;
        if ((centre = _roomCentres.get(room)) != null) {
            return centre;
        }

        int minX = this.width, maxX = -1, minY = this.height, maxY = -1;

        int x = 0;
        for (Tile[] column : this.tiles) {
            int y = 0;
            for (Tile tile : column) {
                if (tile.room.isPresent() && tile.room.get() == room) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
                y++;
            }
            x++;
        }

        centre = new Location((minX + maxX) / 2, (minY + maxY) / 2);
        _roomCentres.put(room, centre);

        return centre;

    }

    /**
     * Given two physically adjacent locations, returns whether there is a wall between them.
     */
    public boolean hasWallBetween(Location l1, Location l2) {
        Tile t1 = this.tileAtLocation(l1);
        return !t1.adjacentLocations.containsValue(l2);
    }

    private Room roomForCharacter(char c) {
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
        Map<Direction, Location> adjacentLocations = new HashMap<Direction, Location>(4);
        if ((mask & 1) != 0) {
            adjacentLocations.put(Direction.Left, new Location(locationX - 1, locationY));
        }
        if ((mask & (1 << 1)) != 0) {
            adjacentLocations.put(Direction.Up, new Location(locationX, locationY - 1));
        }
        if ((mask & (1 << 2)) != 0) {
            adjacentLocations.put(Direction.Right, new Location(locationX + 1, locationY));
        }
        if ((mask & (1 << 3)) != 0) {
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

    private Tile[][] loadMap(List<String> lines, int width, int height) {
        Tile[][] map = new Tile[width][height];

        Tile kitchenPathTile = null, conservatoryPathTile = null, studyPathTile = null, loungePathTile = null;
        Location kitchenPathLocation = null, conservatoryPathLocation = null, studyPathLocation = null, loungePathLocation = null;

        int y = 0;
        for (String line : lines) {
            String[] tiles = line.split(" ");
            int x = 0;
            for (String tileStr : tiles) {
                Room room = roomForCharacter(tileStr.charAt(0));
                Integer mask = Integer.parseInt(tileStr.substring(1));
                Map<Direction, Location> adjacentLocations = this.adjacentLocations(mask, x, y);
                Tile tile = new Tile(Optional.ofNullable(room), adjacentLocations);
                map[x][y] = tile;


                if ((mask & (1 << 4)) != 0) { //We need to check for the special path tiles within this method – we can't assign the locations until we've read all the tiles.
                   kitchenPathTile = tile; kitchenPathLocation = new Location(x, y);
                } else if ((mask & (1 << 5)) != 0) {
                    studyPathTile = tile; studyPathLocation = new Location(x, y);
                } else if ((mask & (1 << 6)) != 0) {
                    conservatoryPathTile = tile; conservatoryPathLocation = new Location(x, y);
                } else if ((mask & (1 << 7)) != 0) {
                    loungePathTile = tile; loungePathLocation = new Location(x, y);
                }

                x++;
            }

            y++;
        }

        this.linkTiles(kitchenPathTile, kitchenPathLocation, studyPathTile, studyPathLocation);
        this.linkTiles(loungePathTile, loungePathLocation, conservatoryPathTile, conservatoryPathLocation);

        return map;
    }

    public static class Tile {
        public final Optional<Room> room;
        public final Map<Direction, Location> adjacentLocations;
        // two tiles are adjacent if there is no wall between them

        public Tile(Optional<Room> room, Map<Direction, Location> adjacentLocations) {
            this.room = room;
            this.adjacentLocations = adjacentLocations;
        }
    }
}
