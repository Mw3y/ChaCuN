package ch.epfl.chacun;

import java.util.Objects;

/**
 * Represents the different possible occupants of a zone.
 *
 * @param kind   the occupant kind
 * @param zoneId the id of the zone in which is the occupant is located
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record Occupant(Kind kind, int zoneId) {

    private static final int PAWNS_PER_PLAYER = 5;
    private static final int HUT_PER_PLAYER = 3;

    /**
     * Checks for the validity of the given kind and zoneId.
     *
     * @throws NullPointerException     if kind is null
     * @throws IllegalArgumentException if zoneId is smaller than 0
     */
    public Occupant {
        Objects.requireNonNull(kind);
        Preconditions.checkArgument(zoneId >= 0);
    }

    /**
     * Returns the number of occupants of the given kind owned by a player
     *
     * @param kind the occupant kind
     * @return the number of occupants of the given kind owned by a player
     */
    public static int occupantsCount(Kind kind) {
        return switch (kind) {
            case PAWN -> PAWNS_PER_PLAYER;
            case HUT -> HUT_PER_PLAYER;
        };
    }

    /**
     * Represents the different kinds of occupants.
     */
    public enum Kind {
        PAWN, HUT
    }
}
