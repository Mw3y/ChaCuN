package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OccupantTest {
    @Test
    void occupantConstructorThrowsOnNullKind() {
        assertThrows(NullPointerException.class, () -> new Occupant(null, 0));
    }

    @Test
    void occupantConstructorThrowsOnNegativeZoneId() {
        assertThrows(IllegalArgumentException.class, () -> new Occupant(Occupant.Kind.PAWN, -1));
    }

    @Test
    void occupantOccupantCountIsCorrectlyDefined() {
        assertEquals(5, Occupant.occupantsCount(Occupant.Kind.PAWN));
        assertEquals(3, Occupant.occupantsCount(Occupant.Kind.HUT));
    }
}