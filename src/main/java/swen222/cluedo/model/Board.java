package swen222.cluedo.model;

import swen222.cluedo.model.card.Room;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Board {


    public final Tile tiles[][];
    public final int width, height;
    private Map<Room, Location<Float>> _roomCentres = new HashMap<>();

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
    public Tile tileAtLocation(Location<Integer> location) {
        return tiles[location.x][location.y];
    }

    /**
     * Gets the tile at the given, possibly valid, location.
     *
     * @param location the location to get the tile from
     * @return the tile at the given location, or Optional.empty if the location is empty.
     */
    private Optional<Tile> safeTileAtLocation(Location<Integer> location) {
        if (location.x < 0 || location.x >= this.width || location.y < 0 || location.y >= this.height) {
            return Optional.empty();
        }
        return Optional.of(tiles[location.x][location.y]);
    }

    /**
     * Finds the location that is in the middle of a room.
     * @param room the room to look for
     * @return the location of the central tile.
     */
    public Location<Float> centreLocationForRoom(Room room) {
        Location<Float> centre;
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

        centre = new Location<>((minX + maxX) / 2.f, (minY + maxY) / 2.f);
        _roomCentres.put(room, centre);

        return centre;

    }

    /**
     * Given two physically adjacent locations, returns whether there is a wall between them.
     */
    public boolean hasWallBetween(Location<Integer> l1, Location<Integer> l2) {
        Optional<Tile> t1 = this.safeTileAtLocation(l1);
        Optional<Tile> t2 = this.safeTileAtLocation(l2);

        if (t1.isPresent() && t2.isPresent() && (!t1.get().adjacentLocations.isEmpty() || !t2.get().adjacentLocations.isEmpty())) {

            boolean hasWall = false;
            if (t1.isPresent()) {
                hasWall = !t1.get().adjacentLocations.containsValue(l2);
            } else if (t2.isPresent()) {
                hasWall = !t2.get().adjacentLocations.containsValue(l1);
            }
            return hasWall;

        }

        return false;
    }

    private class DijkstraNode {
        public int distanceFromSource = Integer.MAX_VALUE;
        public Optional<Location<Integer>> previousLocation = Optional.empty();
    }

    private Location<Integer>[] reconstructPath(Location<Integer> endLocation, DijkstraNode[][] nodeData, int distance) {
        @SuppressWarnings("unchecked")
        Location<Integer>[] path = new Location[distance + 1];

        Location<Integer> location = endLocation;

        for (int i = distance; i >= 0; i--) {
            path[i] = location;
            location = nodeData[location.x][location.y].previousLocation.orElse(null);
        }

        return path;
    }

    public Set<Location<Integer>[]> pathsFromLocation(Location<Integer> location, int maxDistance, Stream<Location<Integer>> blockedLocations) {
        Set<Location<Integer>[]> paths = new HashSet<>();

        DijkstraNode[][] nodeData = new DijkstraNode[this.width][this.height];
        for (DijkstraNode[] column : nodeData) {
            Arrays.fill(column, new DijkstraNode());
        }

        nodeData[location.x][location.y].distanceFromSource = 0;

        PriorityQueue<Location<Integer>> queue = new PriorityQueue<>(
                (l1, l2) -> nodeData[l1.x][l1.y].distanceFromSource - nodeData[l2.x][l2.y].distanceFromSource
        );

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                queue.add(new Location<>(x, y));
            }
        }

        while (!queue.isEmpty()) {
            Location<Integer> currentLocation = queue.poll();
            for (Location<Integer> neighbour : this.tileAtLocation(currentLocation).adjacentLocations.values()) {
                if (blockedLocations.anyMatch(location1 -> location1.equals(neighbour))) {
                    continue;
                }

                int distance = nodeData[currentLocation.x][currentLocation.y].distanceFromSource + 1;

                if (distance > maxDistance) {
                    return paths;
                }

                if (distance < nodeData[neighbour.x][neighbour.y].distanceFromSource) {
                    nodeData[neighbour.x][neighbour.y].distanceFromSource = distance;
                    nodeData[neighbour.x][neighbour.y].previousLocation = Optional.of(currentLocation);

                    paths.add(this.reconstructPath(neighbour, nodeData, distance));
                }

            }
        }
        return paths;
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

    private Map<Direction, Location<Integer>> adjacentLocations(int mask, int locationX, int locationY) {
        Map<Direction, Location<Integer>> adjacentLocations = new HashMap<>(4);
        if ((mask & 1) != 0) {
            adjacentLocations.put(Direction.Left, new Location<>(locationX - 1, locationY));
        }
        if ((mask & (1 << 1)) != 0) {
            adjacentLocations.put(Direction.Up, new Location<>(locationX, locationY - 1));
        }
        if ((mask & (1 << 2)) != 0) {
            adjacentLocations.put(Direction.Right, new Location<>(locationX + 1, locationY));
        }
        if ((mask & (1 << 3)) != 0) {
            adjacentLocations.put(Direction.Down, new Location<>(locationX, locationY + 1));
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
    private void linkTiles(Tile t1, Location<Integer> l1, Tile t2, Location<Integer> l2) {
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
        Location<Integer> kitchenPathLocation = null, conservatoryPathLocation = null, studyPathLocation = null, loungePathLocation = null;

        int y = 0;
        for (String line : lines) {
            String[] tiles = line.split(" ");
            int x = 0;
            for (String tileStr : tiles) {
                Room room = roomForCharacter(tileStr.charAt(0));
                Integer mask = Integer.parseInt(tileStr.substring(1));
                Map<Direction, Location<Integer>> adjacentLocations = this.adjacentLocations(mask, x, y);
                Tile tile = new Tile(Optional.ofNullable(room), adjacentLocations);
                map[x][y] = tile;


                if ((mask & (1 << 4)) != 0) { //We need to check for the special path tiles within this method – we can't assign the locations until we've read all the tiles.
                   kitchenPathTile = tile; kitchenPathLocation = new Location<>(x, y);
                } else if ((mask & (1 << 5)) != 0) {
                    studyPathTile = tile; studyPathLocation = new Location<>(x, y);
                } else if ((mask & (1 << 6)) != 0) {
                    conservatoryPathTile = tile; conservatoryPathLocation = new Location<>(x, y);
                } else if ((mask & (1 << 7)) != 0) {
                    loungePathTile = tile; loungePathLocation = new Location<>(x, y);
                }

                x++;
            }

            y++;
        }

        this.linkTiles(kitchenPathTile, kitchenPathLocation, studyPathTile, studyPathLocation);
        this.linkTiles(loungePathTile, loungePathLocation, conservatoryPathTile, conservatoryPathLocation);

        return map;
    }

    /**
     * Recursively finds the new location given a move sequence from a start location
     *
     * @param move          A sequence of directions in which the user wishes to move
     * @param startLocation the location from which this move sequence goes
     * @return The new location, if the move sequence is valid; else, the empty optional.
     */
    public Optional<Location<Integer>> newLocationForMove(List<Direction> move, Location<Integer> startLocation, Stream<Location<Integer>> blockedLocations) {
        if (blockedLocations.anyMatch(location -> location.equals(startLocation))) {
            return Optional.empty();
        }
        if (move.size() > 0) {
            Direction head = move.get(0);
            Board.Tile tile = this.tileAtLocation(startLocation);
            Location<Integer> nextLocationOrNull = tile.adjacentLocations.get(head);

            if (nextLocationOrNull == null) {
                return Optional.empty();
            }
            List<Direction> tail = move.subList(1, move.size());
            return newLocationForMove(tail, nextLocationOrNull, blockedLocations);
        }
        return Optional.of(startLocation);
    }

    public static class Tile {
        public final Optional<Room> room;
        public final Map<Direction, Location<Integer>> adjacentLocations;
        // two tiles are adjacent if there is no wall between them

        public Tile(Optional<Room> room, Map<Direction, Location<Integer>> adjacentLocations) {
            this.room = room;
            this.adjacentLocations = adjacentLocations;
        }
    }
}
