package ch.epfl.chacun;

import java.util.Objects;

public record Occupant(Kind kind, int zoneId) {

    public enum Kind {
        PAWN,
        HUT
    }

    public Occupant {
        Objects.requireNonNull(kind);
        Preconditions.checkArgument(zoneId < 0);
    }

    public static int occupantsCount(Kind kind) {
        return switch (kind) {
            case PAWN -> 5;
            case HUT -> 3;
        };
    }
}
