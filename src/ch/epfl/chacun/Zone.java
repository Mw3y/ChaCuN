package ch.epfl.chacun;

public interface Zone {
    enum SpecialPower{
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT;
    }

    int id();

    static int tileId(int zoneId) {
        // Since a zoneId is obtained using zoneId = 10 * tileId + localId and localId is between 0 and 9
        // We can use integer division to obtain the tileId
        return (int) (zoneId /10);
    }

    static int localId(int zoneId) {
        // zoneId = 10 * tileId + localId
        return zoneId - 10 * tileId(zoneId);
    }

    default int tileId() {
        return tileId(id());
    }

    default int localId() {
        return localId(id());
    }

    default SpecialPower specialPower() {
        return null;
    };

    record Forest() implements Zone {

    }
}
