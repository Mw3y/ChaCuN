package ch.epfl.chacun;

import java.util.List;

public enum Rotation {
    NONE, // 0 degres
    RIGHT, // 90 degrees
    HALF_TURN, // 180 degrees
    LEFT; // 270 degrees

    public static final List<Rotation> ALL = List.of(Rotation.values());
    public static final int COUNT = ALL.size();

    public Rotation add(Rotation that) {
        return ALL.get((this.ordinal() + that.ordinal()) % COUNT);
    }

    public Rotation negated() {
        return ALL.get((COUNT - this.ordinal()) % COUNT);
    }

    public int quarterTurnsCW() {
        return this.ordinal();
    }

    public int degreesCW() {
        return this.quarterTurnsCW() * 90;
    }

}
