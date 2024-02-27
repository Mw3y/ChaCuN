package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different possible sides of a tile.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public sealed interface TileSide {

    /**
     * Returns {@code true} if and only if the given edge ({@code that})
     * is of the same kind as the current one ({@code this}).
     *
     * @param that the edge to compare with the current one
     * @return {@code true} if and only if the given edge is of the same kind as the current one
     */
    boolean isSameKindAs(TileSide that);

    /**
     * Returns the zones that touch the edge represented by the receiver ({@code this}).
     *
     * @return the zones that touch the current edge
     */
    List<Zone> zones();

    /**
     * Represents a side of a tile that is a forest.
     *
     * @param forest the forest zone
     */
    record Forest(Zone.Forest forest) implements TileSide {
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Forest;
        }

        @Override
        public List<Zone> zones() {
            return List.of(forest);
        }
    }

    /**
     * Represents a side of a tile that is a meadow.
     *
     * @param meadow the meadow zone
     */
    record Meadow(Zone.Meadow meadow) implements TileSide {
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Meadow;
        }

        @Override
        public List<Zone> zones() {
            return List.of(meadow);
        }
    }

    /**
     * Represents a side which contains a river between two meadows.
     *
     * @param meadow1 the first meadow zone found clockwise
     * @param river   the river zone
     * @param meadow2 the second meadow zone found clockwise
     */
    record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof River;
        }

        @Override
        public List<Zone> zones() {
            return List.of(meadow1, river, meadow2);
        }
    }
}
