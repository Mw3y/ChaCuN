package ch.epfl.chacun;

import java.util.List;

public enum Direction {
    N,
    E,
    S,
    W;
    public static final List<Direction> ALL = List.of(Direction.values());
    public static final int COUNT = ALL.size();

    public Direction rotated(Rotation rotation) {
        return ALL.get((this.ordinal() + rotation.ordinal()) % COUNT);
    }

    public Direction opposite() {
        // There is only four directions, so we can just add 2 to the ordinal
        return ALL.get((this.ordinal() + 2) % COUNT);
    }
}
