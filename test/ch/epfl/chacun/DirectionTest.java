package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectionTest {
    @Test
    void directionAllIsCorrectlyDefined() {
        assertEquals(4, Direction.ALL.size());
        assertEquals(Direction.N, Direction.ALL.get(0));
        assertEquals(Direction.E, Direction.ALL.get(1));
        assertEquals(Direction.S, Direction.ALL.get(2));
        assertEquals(Direction.W, Direction.ALL.get(3));
    }

    @Test
    void directionCountIsCorrectlyDefined() {
        assertEquals(4, Direction.COUNT);
    }

    @Test
    void directionRotatedWorksForAllDirectionsAndRotations() {
        assertEquals(Direction.N.rotated(Rotation.RIGHT), Direction.E);
        assertEquals(Direction.N.rotated(Rotation.LEFT), Direction.W);
        assertEquals(Direction.N.rotated(Rotation.HALF_TURN), Direction.S);
        assertEquals(Direction.N.rotated(Rotation.NONE), Direction.N);

        assertEquals(Direction.E.rotated(Rotation.RIGHT), Direction.S);
        assertEquals(Direction.E.rotated(Rotation.LEFT), Direction.N);
        assertEquals(Direction.E.rotated(Rotation.HALF_TURN), Direction.W);
        assertEquals(Direction.E.rotated(Rotation.NONE), Direction.E);

        assertEquals(Direction.S.rotated(Rotation.RIGHT), Direction.W);
        assertEquals(Direction.S.rotated(Rotation.LEFT), Direction.E);
        assertEquals(Direction.S.rotated(Rotation.HALF_TURN), Direction.N);
        assertEquals(Direction.S.rotated(Rotation.NONE), Direction.S);

        assertEquals(Direction.W.rotated(Rotation.RIGHT), Direction.N);
        assertEquals(Direction.W.rotated(Rotation.LEFT), Direction.S);
        assertEquals(Direction.W.rotated(Rotation.HALF_TURN), Direction.E);
        assertEquals(Direction.W.rotated(Rotation.NONE), Direction.W);
    }

    @Test
    void directionOppositeWorksForAllDirections() {
        assertEquals(Direction.N.opposite(), Direction.S);
        assertEquals(Direction.E.opposite(), Direction.W);
        assertEquals(Direction.S.opposite(), Direction.N);
        assertEquals(Direction.W.opposite(), Direction.E);
    }
}