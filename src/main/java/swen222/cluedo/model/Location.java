package swen222.cluedo.model;


import java.util.Objects;

public class Location<T extends Number> {
    public final T x;
    public final T y;

    public Location(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public Location<T> copy() {
        return new Location<>(this.x, this.y);
    }

    /**
     * Returns the location in a particular direction.
     */
    public Location<Integer> locationInDirection(Direction direction) {
        switch (direction) {
            case Up:
                return new Location<>(this.x.intValue(), this.y.intValue() - 1);
            case Down:
                return new Location<>(this.x.intValue(), this.y.intValue() + 1);
            case Left:
                return new Location<>(this.x.intValue() - 1, this.y.intValue());
            case Right:
                return new Location<>(this.x.intValue() + 1, this.y.intValue());
            default:
                return null;
        }
    }

    /**
     * Linearly interpolates between two locations, based upon a time t.
     * @param l1 The location to interpolate from.
     * @param l2 The location to interpolate to.
     * @param t The time, usually in the range [0, 1], indicating how far through the interpolation we currently are.
     * @return The interpolated location.
     */
    public static Location<Float> lerp(Location<Float> l1, Location<Float> l2, float t) {
        float lerpedX = l1.x + (l2.x - l1.x) * t;
        float lerpedY = l1.y + (l2.y - l1.y) * t;
        return new Location<>(lerpedX, lerpedY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        @SuppressWarnings("unchecked")
		Location<T> location = (Location<T>) o;

        return x.equals(location.x) && y.equals(location.y);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Location(" + this.x + ", " + this.y + ")";
    }
}
