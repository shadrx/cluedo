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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

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
