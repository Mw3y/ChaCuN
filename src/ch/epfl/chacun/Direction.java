package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different possible directions of a tile.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public enum Direction {
    N,
    E,
    S,
    W;

    // All the values of Direction.
    public static final List<Direction> ALL = List.of(Direction.values());

    // The number of elements of Direction.
    public static final int COUNT = ALL.size();

    /**
     * Returns the direction after applying the given rotation.
     *
     * @param rotation the rotation to apply
     * @return the direction after applying the rotation
     */
    public Direction rotated(Rotation rotation) {
        return ALL.get((this.ordinal() + rotation.ordinal()) % COUNT);
    }

    /**
     * Returns the opposite of the receiver direction.
     *
     * @return the opposite of the receiver direction
     */
    public Direction opposite() {
        // There is only four directions, so we can just add 2 to the ordinal
        return ALL.get((this.ordinal() + 2) % COUNT);
    }
}
