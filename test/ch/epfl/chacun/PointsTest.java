package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointsTest {
    @Test
    void allPointsMethodThrowOnInvalidCounts() {
        // Tile counts must be > 1
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forClosedForest(1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forClosedRiver(1, 1);
        });

        // Lake counts must be > 0
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forLogboat(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forRaft(0);
        });

        // Animal (fish, mammoth, etc.) and mushroom group counts must be >= 0
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forClosedForest(2, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forClosedRiver(2, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forMeadow(-1, 0, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forMeadow(0, -1, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forMeadow(0, 0, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Points.forRiverSystem(-1);
        });
    }

    @Test
    void pointsForClosedForestWorksForKnownCases() {
        assertEquals(4, Points.forClosedForest(2, 0));
        assertEquals(10, Points.forClosedForest(2, 2));
        assertEquals(9, Points.forClosedForest(3, 1));
    }

    @Test
    void pointsForClosedRiverWorksForKnownCases() {
        assertEquals(2, Points.forClosedRiver(2, 0));
        assertEquals(3, Points.forClosedRiver(2, 1));
        assertEquals(8, Points.forClosedRiver(3, 5));
    }

    @Test
    void pointsForMeadowWorksForKnownCases() {
        assertEquals(0, Points.forMeadow(0, 0, 0));
        assertEquals(3, Points.forMeadow(1, 0, 0));
        assertEquals(2, Points.forMeadow(0, 1, 0));
        assertEquals(1, Points.forMeadow(0, 0, 1));
        assertEquals(5, Points.forMeadow(1, 1, 0));
        assertEquals(4, Points.forMeadow(1, 0, 1));
        assertEquals(3, Points.forMeadow(0, 1, 1));
        assertEquals(6, Points.forMeadow(1, 1, 1));
        assertEquals(17, Points.forMeadow(2, 3, 5));
    }

    @Test
    void pointsForRiverSystemWorksForKnownCases() {
        assertEquals(0, Points.forRiverSystem(0));
        assertEquals(1, Points.forRiverSystem(1));
        assertEquals(5, Points.forRiverSystem(5));
    }

    @Test
    void pointsForLogboatWorksForKnownCases() {
        assertEquals(2, Points.forLogboat(1));
        assertEquals(4, Points.forLogboat(2));
        assertEquals(10, Points.forLogboat(5));
    }

    @Test
    void pointsForRaftWorksForKnownCases() {
        assertEquals(1, Points.forRaft(1));
        assertEquals(2, Points.forRaft(2));
        assertEquals(5, Points.forRaft(5));
    }
}