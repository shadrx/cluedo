package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.model.Direction;

import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void testCharacterToDirection() {
        assertEquals(Direction.Down, Direction.fromCharacter('D'));
        assertEquals(Direction.Left, Direction.fromCharacter('L'));
        assertEquals(Direction.Right, Direction.fromCharacter('R'));
        assertEquals(Direction.Up, Direction.fromCharacter('U'));
    }
}
