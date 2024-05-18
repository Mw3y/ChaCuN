package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different possible zones of a tile.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public sealed interface Zone {

    /**
     * Calculates the id of the tile containing the zone.
     *
     * @param zoneId the id of the zone
     * @return the tileId of the zone
     */
    static int tileId(int zoneId) {
        // Since a zoneId is obtained using zoneId = 10 * tileId + localId and localId is between 0 and 9
        // We can use integer division to obtain the tileId
        return zoneId / 10;
    }

    /**
     * Calculates the "local" identifier of the zone.
     *
     * @param zoneId the id of the zone
     * @return the local identifier of the zone
     */
    static int localId(int zoneId) {
        // zoneId = 10 * tileId + localId
        return zoneId % 10;
    }

    /**
     * Returns the identifier of the zone.
     *
     * @return the identifier of the zone
     */
    int id();

    /**
     * Default method to calculate the tileId of the zone.
     *
     * @return the tileId of the zone
     */
    default int tileId() {
        return tileId(id());
    }

    /**
     * Default method to calculate the localId of the zone.
     *
     * @return the localId of the zone
     */
    default int localId() {
        return localId(id());
    }

    /**
     * Returns the special power of the zone.
     *
     * @return the special power of the zone
     */
    default SpecialPower specialPower() {
        return null;
    }

    /**
     * Represents the different special powers.
     */
    enum SpecialPower {
        SHAMAN, LOGBOAT, HUNTING_TRAP, PIT_TRAP, WILD_FIRE, RAFT
    }

    /**
     * Represents a water zone.
     */
    sealed interface Water extends Zone {
        /**
         * Returns the number of fishes in the zone.
         *
         * @return the number of fishes in the zone
         */
        int fishCount();
    }

    /**
     * Represents a forest zone.
     *
     * @param id   the identifier of the zone
     * @param kind the kind of the forest
     */
    record Forest(int id, Kind kind) implements Zone {
        /**
         * Represents the different kinds of forests.
         */
        public enum Kind {
            PLAIN, WITH_MENHIR, WITH_MUSHROOMS
        }
    }

    /**
     * Represents a meadow zone.
     *
     * @param id           the identifier of the zone
     * @param animals      the animals present in the meadow
     * @param specialPower the special power of the meadow
     */
    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {

        /**
         * Makes a defensive copy of the animal list.
         */
        public Meadow {
            // Defensive copy of animals
            animals = List.copyOf(animals);
        }
    }

    /**
     * Represents a lake zone.
     *
     * @param id           the identifier of the zone
     * @param fishCount    the number of fishes in the lake
     * @param specialPower the special power of the lake
     */
    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    /**
     * Represents a river zone.
     *
     * @param id        the identifier of the zone
     * @param fishCount the number of fishes in the river
     * @param lake      (optional) the lake connected to the river
     */
    record River(int id, int fishCount, Lake lake) implements Water {

        /**
         * Returns whether the river has a lake or not.
         *
         * @return whether the river has a lake or not
         */
        public boolean hasLake() {
            return lake != null;
        }
    }
}
