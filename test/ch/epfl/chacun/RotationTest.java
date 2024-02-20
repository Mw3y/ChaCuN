package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RotationTest {
    @Test
    void rotationAllIsCorrectlyDefined() {
        assertEquals(4, Rotation.ALL.size());
        assertEquals(Rotation.NONE, Rotation.ALL.get(0));
        assertEquals(Rotation.RIGHT, Rotation.ALL.get(1));
        assertEquals(Rotation.HALF_TURN, Rotation.ALL.get(2));
        assertEquals(Rotation.LEFT, Rotation.ALL.get(3));
    }

    @Test
    void rotationCountIsCorrectlyDefined() {
        assertEquals(4, Rotation.COUNT);
    }

    @Test
    void rotationAddWorksForAllCombinations() {
        assertEquals(Rotation.NONE, Rotation.NONE.add(Rotation.NONE));
        assertEquals(Rotation.RIGHT, Rotation.NONE.add(Rotation.RIGHT));
        assertEquals(Rotation.HALF_TURN, Rotation.NONE.add(Rotation.HALF_TURN));
        assertEquals(Rotation.LEFT, Rotation.NONE.add(Rotation.LEFT));

        assertEquals(Rotation.RIGHT, Rotation.RIGHT.add(Rotation.NONE));
        assertEquals(Rotation.HALF_TURN, Rotation.RIGHT.add(Rotation.RIGHT));
        assertEquals(Rotation.LEFT, Rotation.RIGHT.add(Rotation.HALF_TURN));
        assertEquals(Rotation.NONE, Rotation.RIGHT.add(Rotation.LEFT));

        assertEquals(Rotation.HALF_TURN, Rotation.HALF_TURN.add(Rotation.NONE));
        assertEquals(Rotation.LEFT, Rotation.HALF_TURN.add(Rotation.RIGHT));
        assertEquals(Rotation.NONE, Rotation.HALF_TURN.add(Rotation.HALF_TURN));
        assertEquals(Rotation.RIGHT, Rotation.HALF_TURN.add(Rotation.LEFT));

        assertEquals(Rotation.LEFT, Rotation.LEFT.add(Rotation.NONE));
        assertEquals(Rotation.NONE, Rotation.LEFT.add(Rotation.RIGHT));
        assertEquals(Rotation.RIGHT, Rotation.LEFT.add(Rotation.HALF_TURN));
        assertEquals(Rotation.HALF_TURN, Rotation.LEFT.add(Rotation.LEFT));
    }

    @Test
    void rotationNegatedWorksForAllRotations() {
        assertEquals(Rotation.NONE, Rotation.NONE.negated());
        assertEquals(Rotation.LEFT, Rotation.RIGHT.negated());
        assertEquals(Rotation.HALF_TURN, Rotation.HALF_TURN.negated());
        assertEquals(Rotation.RIGHT, Rotation.LEFT.negated());
    }

    @Test
    void rotationQuarterTurnsCWWorksForAllRotations() {
        assertEquals(0, Rotation.NONE.quarterTurnsCW());
        assertEquals(1, Rotation.RIGHT.quarterTurnsCW());
        assertEquals(2, Rotation.HALF_TURN.quarterTurnsCW());
        assertEquals(3, Rotation.LEFT.quarterTurnsCW());
    }

    @Test
    void rotationDegreesCWWorksForAllRotations() {
        assertEquals(0, Rotation.NONE.degreesCW());
        assertEquals(90, Rotation.RIGHT.degreesCW());
        assertEquals(180, Rotation.HALF_TURN.degreesCW());
        assertEquals(270, Rotation.LEFT.degreesCW());
    }
}