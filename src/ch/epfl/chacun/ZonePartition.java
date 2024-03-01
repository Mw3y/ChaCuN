package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a partition of zones of a given kind.
 *
 * @param areas the set of areas forming the partition
 * @param <Z>   the kind of zones of the partition
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas) {

    /**
     * Defensive copy of the set of areas.
     */
    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    /**
     * Additional constructor to construct a partition with an empty set of areas.
     */
    public ZonePartition() {
        this(new HashSet<>());
    }

    /**
     * Returns the area containing the given zone.
     *
     * @param zone the given zone
     * @return the area containing the given zone
     * @throws IllegalArgumentException if the given zone is not assigned to any area of the partition
     */
    public Area<Z> areaContaining(Z zone) {
        return areas.stream().filter(a -> a.zones().contains(zone)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The zone is not assigned to any area."));
    }

    /**
     * Represents the builder of ZonePartition.
     *
     * @param <Z> the zone type
     */

    public static final class Builder<Z extends Zone> {
        // The set of areas constituting the partition
        private HashSet<Area<Z>> areas;

        /**
         * Initialises the builder's area set with that of this partition.
         *
         * @param zonePartition the existing partition
         */
        public Builder(ZonePartition<Z> zonePartition) {
            this.areas = (HashSet<Area<Z>>) zonePartition.areas;
        }

        /**
         * Adds to the partition under construction a new unoccupied area constituted of the given zone
         * and the given number of open connections.
         *
         * @param zone            the given zone
         * @param openConnections the given number of open connections
         */
        public void addSingleton(Z zone, int openConnections) {
            areas.add(new Area<>(Set.of(zone), new ArrayList<>(), openConnections));
        }

        /**
         * Adds to the area containing the given zone an initial occupant of the given color.
         *
         * @param zone  the given zone
         * @param color the given occupant color
         * @throws IllegalArgumentException if the given zone is not assigned to any area of the partition
         *                                  or if the area containing the given zone is already occupied
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {
            ZonePartition<Z> zonePartition = new ZonePartition<>(areas);
            Area<Z> areaContainingZone = zonePartition.areaContaining(zone);
            Area<Z> newArea = areaContainingZone.withInitialOccupant(color);

            try {
                zonePartition.areaContaining(zone);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Zone is not assigned to any area of the partition.");
            }
            if (areaContainingZone.isOccupied()){
                throw new IllegalArgumentException("The area is already occupied.");
            }

            zonePartition.areas.remove(areaContainingZone);
            zonePartition.areas.add(newArea);
        }

    }
}
