package swen222.cluedo.unit.model;

import org.junit.Before;
import org.junit.Test;
import swen222.cluedo.model.Board;
import swen222.cluedo.model.Direction;
import swen222.cluedo.model.Location;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapTest {
    private Board board = null;

    @Before
    public void loadBoard() {
        try {
            this.board = new Board(Paths.get("resources/cluedo.map"), 24, 25);
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
                Location tileLocation = new Location(x, y);
                for (Map.Entry<Direction, Location> entry : tile.adjacentLocations.entrySet()) {
                    Board.Tile other = board.tileAtLocation(entry.getValue());
                    Direction oppositeDirection = this.oppositeDirection(entry.getKey());

                    Location returnedLocation = other.adjacentLocations.get(oppositeDirection);
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
}
