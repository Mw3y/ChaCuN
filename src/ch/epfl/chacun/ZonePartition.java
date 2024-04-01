package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.Collections;
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
        this(Set.of());
    }

    /**
     * Returns the area containing the given zone, or throws an {@link IllegalArgumentException} if the
     * given zone is not assigned to any area of the partition.
     *
     * @param zone the zone
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
     * @author Maxence Espagnet (sciper: 372808)
     * @author Balthazar Baillat (sciper: 373420)
     */
    public static final class Builder<Z extends Zone> {
        // The set of areas constituting the partition
        private final Set<Area<Z>> areas;

        /**
         * Initialises the builder's area set with the one of the given partition.
         *
         * @param zonePartition the partition
         */
        public Builder(ZonePartition<Z> zonePartition) {
            this.areas = new HashSet<>(zonePartition.areas);
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
         * Checks the validity of a given zone and returns the area of the partition
         * containing the given zone.
         * <p>
         * Verifies if the given zone is already occupied or not assigned to any area of the partition
         * and throws {@link IllegalArgumentException} if so.
         *
         * @param zone the zone to check
         * @return the area of the partition containing the given zone
         * @throws IllegalArgumentException if the zone is not available
         */
        private Area<Z> findAreaContainingZone(Z zone) {
            // Create a zone partition (needed to use areaContaining method) containing the set of areas
            ZonePartition<Z> zonePartition = new ZonePartition<>(areas);
            try {
                return zonePartition.areaContaining(zone);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Zone is not assigned to any area of the partition.");
            }
        }

        private Area<Z> findAreaContainingZoneWithoutOccupant(Z zone) {
            Area<Z> areaContainingZone = findAreaContainingZone(zone);
            if (areaContainingZone.isOccupied()) {
                throw new IllegalArgumentException("The area is already occupied.");
            }
            return areaContainingZone;
        }


        /**
         * Adds to the area containing the given zone an initial occupant of the given color after checking
         * if the given zone is available.
         *
         * @param zone  the given zone
         * @param color the given occupant color
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {
            // Check if the zone is available in the partition and find the area containing the given zone
            Area<Z> areaContainingZone = findAreaContainingZoneWithoutOccupant(zone);
            // Create a new area without the occupant of the given color
            Area<Z> newArea = areaContainingZone.withInitialOccupant(color);
            // Replace the area containing the given zone by the new one in the set of areas
            areas.remove(areaContainingZone);
            areas.add(newArea);
        }

        /**
         * Removes from the area containing the given zone an occupant of the given color after checking
         * if the given zone is available.
         *
         * @param zone  the given zone
         * @param color the given occupant color
         */
        public void removeOccupant(Z zone, PlayerColor color) {
            // Check if the zone is available in the partition and find the area containing the given zone
            Area<Z> areaContainingZone = findAreaContainingZone(zone);
            // Create a new area without the occupant of the given color
            Area<Z> newArea = areaContainingZone.withoutOccupant(color);
            // Replace the area containing the given zone by the new one in the set of areas
            areas.remove(areaContainingZone);
            areas.add(newArea);
        }

        /**
         * Removes all the occupants of a given area of the partition, or throws an
         * {@link IllegalArgumentException} if the area is not part of the partition.
         *
         * @param area the area to be emptied of its occupants
         * @throws IllegalArgumentException if the area is not part of the partition
         */
        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(areas.contains(area));
            // Create a new area with no occupants
            Area<Z> unoccupiedArea = area.withoutOccupants();
            // Replace the area containing the given zone by the new one in the set of areas
            areas.remove(area);
            areas.add(unoccupiedArea);
        }

        /**
         * Connects to each other two areas containing two zones, or throws {@link IllegalArgumentException}
         * if at least one of the two zones is not assigned to any area of the partition
         *
         * @param zone1 the first zone
         * @param zone2 the second zone
         * @throws IllegalArgumentException if at least one of the two zones is not assigned to any area
         *                                  of the partition
         */
        public void union(Z zone1, Z zone2) {
            Area<Z> area1 = findAreaContainingZone(zone1);
            Area<Z> area2 = findAreaContainingZone(zone2);
            // If the two zones are not assigned to the same area, connect the two areas
            // Otherwise connect the area to itself
            if(!area1.equals(area2)) {
                // Remove the two areas from the set of areas and add the new one
                areas.remove(area1);
                areas.remove(area2);
            } else {
                // Remove the area from the set of areas and add the new one
                areas.remove(area1);
            }
            areas.add(area1.connectTo(area2));
        }

        /**
         * Builds the final zone partition instance.
         *
         * @return the final zone partition
         */
        public ZonePartition<Z> build() {
            return new ZonePartition<>(this.areas);
        }

    }
}
