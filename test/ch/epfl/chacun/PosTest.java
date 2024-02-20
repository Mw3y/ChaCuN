package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PosTest {
    @Test
    void posOriginIsCorrectlyDefined() {
        assertEquals(0, Pos.ORIGIN.x());
        assertEquals(0, Pos.ORIGIN.y());
    }

    @Test
    void posTranslatedWorksForRandomAmounts() {
        for (int x = -10; x <= 10; x += 1) {
            for (int y = -10; y <= 10; y += 1) {
                var pos = new Pos(x, y);
                for (int dX = -5; dX <= 5; dX += 1) {
                    for (int dY = -5; dY <= 5; dY += 1) {
                        var pos1 = pos.translated(dX, dY);
                        assertEquals(x + dX, pos1.x());
                        assertEquals(y + dY, pos1.y());
                    }
                }
            }
        }
    }

    @Test
    void posNeighborWorksForRandomPositions() {
        for (int x = -10; x <= 10; x += 1) {
            for (int y = -10; y <= 10; y += 1) {
                var pos = new Pos(x, y);
                assertEquals(new Pos(x, y - 1), pos.neighbor(Direction.N));
                assertEquals(new Pos(x + 1, y), pos.neighbor(Direction.E));
                assertEquals(new Pos(x, y + 1), pos.neighbor(Direction.S));
                assertEquals(new Pos(x - 1, y), pos.neighbor(Direction.W));
            }
        }
    }
}