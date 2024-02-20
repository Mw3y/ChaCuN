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

    static int tileId(int zoneId) {
        // zoneId = 10 * tileId + localId
        return (zoneId - localId(zoneId) / 10);
    }

    static int localId(int zoneId) {
        // zoneId = 10 * tileId + localId
        return zoneId - 10 * tileId(zoneId);
    }

    int id();

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
