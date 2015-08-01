package swen222.cluedo.unit.model;

import org.junit.Test;
import swen222.cluedo.model.Location;

import static org.junit.Assert.assertEquals;

public class LocationTest {

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
            assertEquals(new Location(x, y), new Location(x + 1, y));
            assertEquals(new Location(x, y), new Location(x, y + 1));
        }
    }
}
