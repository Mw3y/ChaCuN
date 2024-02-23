package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different possible rotations of a tile.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public enum Rotation {
    NONE, // 0 degrees
    RIGHT, // 90 degrees
    HALF_TURN, // 180 degrees
    LEFT; // 270 degrees

    // All the possible rotations
    public static final List<Rotation> ALL = List.of(Rotation.values());
    // The number of different rotations
    public static final int COUNT = ALL.size();

    /**
     * Calculates the addition of two rotations.
     *
     * @param that the rotation to add to the current one
     * @return a new rotation which is the sum of the two rotations
     */
    public Rotation add(Rotation that) {
        return ALL.get((this.ordinal() + that.ordinal()) % COUNT);
    }

    /**
     * Calculates the opposite rotation of the current one.
     * The opposite rotation is the rotation that, when added to the current one, gives the NONE rotation.
     *
     * @return the opposite rotation
     */
    public Rotation negated() {
        return ALL.get((COUNT - this.ordinal()) % COUNT);
    }

    /**
     * Calculates the number of quarter turns clockwise of the current rotation.
     *
     * @return the number of quarter turns clockwise of the rotation
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /**
     * Converts the rotation to degrees clockwise.
     *
     * @return the number of degrees clockwise of the rotation
     */
    public int degreesCW() {
        return this.quarterTurnsCW() * 90;
    }

}
