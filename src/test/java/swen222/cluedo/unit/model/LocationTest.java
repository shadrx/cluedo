package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.model.Location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class LocationTest {

    @Test
    public void testLocationCanHavePositiveCoordinates() {
        try {
            new Location(5, 5);
        } catch (IllegalArgumentException e) {
            fail("Should be able to create location with positive coordinates");
        }
    }

    @Test
    public void testLocationsEqualIfTheSame() {
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.random() * 25);
            int y = (int) (Math.random() * 25);
            assertEquals(new Location(x, y), new Location(x, y));
        }
    }

    @Test
    public void testLocationsNotEqualIfDifferent() {
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.random() * 25);
            int y = (int) (Math.random() * 25);
            assertNotEquals(new Location(x, y), new Location(x + 1, y));
            assertNotEquals(new Location(x, y), new Location(x, y + 1));
        }
    }
}
