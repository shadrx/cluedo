package swen222.cluedo.unit.model;

import org.junit.Before;
import org.junit.Test;
import swen222.cluedo.model.Board;
import swen222.cluedo.model.Direction;
import swen222.cluedo.model.Location;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class MapTest {
    private Board board = null;

    @Before
    public void loadBoard() {
        try {
            InputStream boardStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/cluedo.map");
            this.board = new Board(boardStream, 24, 25);
        } catch (IOException ex) {
            System.err.println("Error reading map file: " + ex);
        }
    }

    public Direction oppositeDirection(Direction direction) {
        switch (direction) {
            case Left: return Direction.Right;
            case Right: return Direction.Left;
            case Up: return Direction.Down;
            case Down: return Direction.Up;
            default: return null;
        }
    }

    @Test
    public void testBoardTileInterconnectedness() {
        //Condition: if tile A is adjacent to tile B in a given direction,
        //then tile B must be adjacent to tile A in the opposite direction,
        //assuming that the locations are physically adjacent.

        int x = 0;
        for (Board.Tile[] column : this.board.tiles) {
            int y = 0;
            for (Board.Tile tile : column) {
                Location<Integer> tileLocation = new Location<>(x, y);
                for (Map.Entry<Direction, Location<Integer>> entry : tile.adjacentLocations.entrySet()) {
                    Board.Tile other = board.tileAtLocation(entry.getValue());
                    Direction oppositeDirection = this.oppositeDirection(entry.getKey());

                    Location<Integer> returnedLocation = other.adjacentLocations.get(oppositeDirection);
                    String message = String.format("Looking at tile at %d, %d in the direction %s",x, y, entry.getKey());

                    assertNotNull(message + ". The returned location should not be null.", returnedLocation);
                    if (Math.abs(tileLocation.x - returnedLocation.x) <= 1 && Math.abs(tileLocation.y - returnedLocation.y) <= 1) {
                        assertEquals(message, tileLocation, other.adjacentLocations.get(oppositeDirection));
                    }
                }
                y++;
            }
            x++;
        }

    }

    @Test
    public void testInvalidMoveReturnsInvalid() {
        List<Direction> move = Arrays.asList(Direction.Up, Direction.Up, Direction.Up);

        assertFalse(board.newLocationForMove(move, new Location<>(0, 0), Stream.<Location<Integer>>empty()).isPresent());
    }

    @Test
    public void testValidMoveReturnsValid() {
        List<Direction> move = Arrays.asList(Direction.Right, Direction.Right, Direction.Up, Direction.Up, Direction.Left, Direction.Left);
        Location<Integer> startLocation = new Location<>(4, 17);
        Location<Integer> endLocation = new Location<>(4, 15);

        assertEquals(board.newLocationForMove(move, startLocation, Stream.<Location<Integer>>empty()).get(), endLocation);
    }

    @Test
    public void testConnectedRoomsHaveNoWallBetweenThem() {
        assertFalse(board.hasWallBetween(new Location<>(4, 6), new Location<>(4, 7)));
    }

    @Test
    public void testDisconnectedRoomsHaveWallsBetweenThem() {
        assertTrue(board.hasWallBetween(new Location<>(3, 6), new Location<>(3, 7)));
    }
}
