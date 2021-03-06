package swen222.cluedo.model;

import swen222.cluedo.model.card.Room;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Board {


    public final Tile tiles[][];
    public final int width, height;
    private Map<Room, Location<Float>> _roomCentres = new HashMap<>();

    public Board(InputStream boardStream, int width, int height) throws IOException {
        List<String> lines = Board.linesFromStream(boardStream);
        this.tiles = loadMap(lines, width, height);
        this.width = width;
        this.height = height;
    }

    private static List<String> linesFromStream(InputStream is) {
        Scanner s = new Scanner(is);
        s.useDelimiter("\n");
        List<String> lines = new ArrayList<>();

        while (s.hasNext()) {
            lines.add(s.next());
        }

        s.close();

        return lines;
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

        if ((t1.isPresent() && !t1.get().connectedLocations.isEmpty()) || (t2.isPresent() && !t2.get().connectedLocations.isEmpty())) {

            boolean hasWall = false;
            if (t1.isPresent()) {
                hasWall = !t1.get().connectedLocations.containsValue(l2) || !t2.isPresent();
            } else if (t2.isPresent()) {
                hasWall = true; //there's always a wall between border tiles and the outside.
            }
            return hasWall;
        }

        return false;
    }

    private Path reconstructPath(Location<Integer> endLocation, DijkstraNode[][] nodeData, int distance) {


        int cost = nodeData[endLocation.x][endLocation.y].cost;
        @SuppressWarnings("unchecked")
        Location<Integer>[] path = new Location[distance + 1];

        Location<Integer> location = endLocation;

        for (int i = distance; i >= 0; i--) {
            path[i] = location;
            location = nodeData[location.x][location.y].previousLocation.orElse(null);
        }

        return new Path(path, cost);
    }

    /**
     * Computes, using Dijkstra's algorithm, a set of the possible paths that can be taken from a location at a given cost
     *
     * @param location         The start location
     * @param maxCost          The maximum allowed cost
     * @param blockedLocations Any un-pathable locations
     * @return A set of the possible paths.
     */
    public Set<Path> pathsFromLocation(Location<Integer> location, int maxCost, Set<Location<Integer>> blockedLocations) {
        Set<Path> paths = new HashSet<>();

        DijkstraNode[][] nodeData = new DijkstraNode[this.width][this.height];
        for (DijkstraNode[] column : nodeData) {
            for (int i = 0; i < column.length; i++) {
                column[i] = new DijkstraNode();
            }
        }

        nodeData[location.x][location.y].distanceFromSource = 0;
        nodeData[location.x][location.y].cost = 0;

        PriorityQueue<Location<Integer>> queue = new PriorityQueue<>(
                (l1, l2) -> nodeData[l1.x][l1.y].distanceFromSource - nodeData[l2.x][l2.y].distanceFromSource
        );

        queue.add(location);

        Optional<Room> startingRoom = this.tileAtLocation(location).room;

        while (!queue.isEmpty()) {
            Location<Integer> currentLocation = queue.poll();
            Tile currentTile = this.tileAtLocation(currentLocation);
            Optional<Room> endRoom = nodeData[currentLocation.x][currentLocation.y].endRoom;

            for (Location<Integer> neighbour : currentTile.connectedLocations.values()) {

                if (blockedLocations.contains(neighbour)) {
                    continue; //we can't continue along this path.
                }

                int cost = 1;

                Tile neighbourTile = this.tileAtLocation(neighbour);
                if (currentTile.room.isPresent() &&
                        neighbourTile.room.isPresent() &&
                        currentTile.room.get() == neighbourTile.room.get()) { //it's free to travel between tiles within the same room.
                    cost = 0;
                }

                cost += nodeData[currentLocation.x][currentLocation.y].cost;
                int distance = nodeData[currentLocation.x][currentLocation.y].distanceFromSource + 1;

                if (cost > maxCost) {
                    continue;
                }

                if (distance < nodeData[neighbour.x][neighbour.y].distanceFromSource) {
                    nodeData[neighbour.x][neighbour.y].distanceFromSource = distance;
                    nodeData[neighbour.x][neighbour.y].cost = cost;
                    nodeData[neighbour.x][neighbour.y].previousLocation = Optional.of(currentLocation);
                    nodeData[neighbour.x][neighbour.y].endRoom = endRoom;

                    if (neighbourTile.room.isPresent() && !neighbourTile.room.equals(startingRoom)) {
                        nodeData[neighbour.x][neighbour.y].endRoom = neighbourTile.room;
                        nodeData[neighbour.x][neighbour.y].cost = maxCost; //Maximum cost if we end in a room.
                    }

                    paths.add(this.reconstructPath(neighbour, nodeData, distance));

                    queue.add(neighbour); //add the neighbour to the queue,
                }

            }
        }
        return paths;
    }

    public List<Direction> pathToDirections(Path path) {
        List<Direction> directions = new ArrayList<>();

        for (int i = 0; i < path.locations.length - 1; i++) {
            Location<Integer> from = path.locations[i];
            Location<Integer> to = path.locations[i + 1];
            Tile fromTile = this.tileAtLocation(from);
            Direction direction = null;
            for (Map.Entry<Direction, Location<Integer>> entry : fromTile.connectedLocations.entrySet()) {
                if (entry.getValue().equals(to)) {
                    direction = entry.getKey();
                    break;
                }
            }

            directions.add(direction);
        }
        return directions;
    }

    /**
     * Converts a list of directions (i.e. from the ASCII interface) to a path.
     * The path is assumed to have the same cost as the length of the list.
     * @param startingLocation The location at which the path starts.
     * @param directions The directions in which to travel.
     * @param blockedLocations Any inaccessible locations.
     * @return Either the path, if it's valid, or an empty optional, if the directions were not valid.
     */
    @SuppressWarnings("unchecked")
    public Optional<Path> directionsToPath(Location<Integer> startingLocation, List<Direction> directions, Set<Location<Integer>> blockedLocations) {

        List<Location<Integer>> locations = new ArrayList<>();

        locations.add(startingLocation);

        Location<Integer> location = startingLocation;
        for (Direction direction : directions) {
            location = this.tileAtLocation(location).connectedLocations.get(direction);
            if (location == null) {
                return Optional.empty();
            }
            locations.add(location);
        }

        return Optional.of(
                new Path(locations.toArray(new Location[locations.size()]),
                        directions.size())); //We assume, for this, that
    }

    private Room roomForCharacter(char c) {
        switch (c) {
            case 'K':
                return Room.Kitchen;
            case 'B':
                return Room.Ballroom;
            case 'C':
                return Room.Conservatory;
            case 'D':
                return Room.DiningRoom;
            case 'I':
                return Room.BilliardRoom;
            case 'H':
                return Room.Hall;
            case 'L':
                return Room.Lounge;
            case 'S':
                return Room.Study;
            case 'R':
                return Room.Library;
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
            if (t1.connectedLocations.get(d) == null) {
                t1.connectedLocations.put(d, l2);
            }

            if (t2.connectedLocations.get(d) == null) {
                t2.connectedLocations.put(d, l1);
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
                    kitchenPathTile = tile;
                    kitchenPathLocation = new Location<>(x, y);
                } else if ((mask & (1 << 5)) != 0) {
                    studyPathTile = tile;
                    studyPathLocation = new Location<>(x, y);
                } else if ((mask & (1 << 6)) != 0) {
                    conservatoryPathTile = tile;
                    conservatoryPathLocation = new Location<>(x, y);
                } else if ((mask & (1 << 7)) != 0) {
                    loungePathTile = tile;
                    loungePathLocation = new Location<>(x, y);
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
    public Optional<Location<Integer>> newLocationForMove(List<Direction> move, Location<Integer> startLocation, Set<Location<Integer>> blockedLocations) {
        if (blockedLocations.contains(startLocation)) {
            return Optional.empty();
        }
        if (move.size() > 0) {
            Direction head = move.get(0);
            Board.Tile tile = this.tileAtLocation(startLocation);
            Location<Integer> nextLocationOrNull = tile.connectedLocations.get(head);

            if (nextLocationOrNull == null) {
                return Optional.empty();
            }
            List<Direction> tail = move.subList(1, move.size());
            return newLocationForMove(tail, nextLocationOrNull, blockedLocations);
        }
        return Optional.of(startLocation);
    }

    public static class Path {
        public final Location<Integer>[] locations;
        public final int cost;
        public final int distance;

        public Path(Location<Integer>[] locations, int cost) {
            this.locations = locations;
            this.distance = locations.length;
            this.cost = cost;
        }
    }

    public static class Tile {
        public final Optional<Room> room;
        public final Map<Direction, Location<Integer>> connectedLocations;
        // two tiles are adjacent if there is no wall between them

        public Tile(Optional<Room> room, Map<Direction, Location<Integer>> connectedLocations) {
            this.room = room;
            this.connectedLocations = connectedLocations;
        }

        public boolean isPassageway(Board board) {
            for (Map.Entry<Direction, Location<Integer>> entry : this.connectedLocations.entrySet()) {
                Location<Integer> location = entry.getValue();
                Tile otherTile = board.tileAtLocation(location);
                if (this.room.isPresent() && otherTile.room.isPresent() && !otherTile.room.get().equals(this.room.get())) {
                    return true;
                }
            }
            return false;
        }
    }

    private class DijkstraNode {
        public int distanceFromSource = Integer.MAX_VALUE;
        public int cost = Integer.MAX_VALUE;
        public Optional<Location<Integer>> previousLocation = Optional.empty();
        public Optional<Room> endRoom = Optional.empty();
    }
}
