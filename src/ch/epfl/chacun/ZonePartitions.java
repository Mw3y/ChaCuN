package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the partition which regroups the four partitions of the different zones.
 *
 * @param forests      the forests partition
 * @param meadows      the meadows partition
 * @param rivers       the rivers partition
 * @param riverSystems the river systems partition
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Water> riverSystems) {

    /**
     * Represent a group of four empty partitions
     */
    public final static ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<>(),
            new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>());

    /**
     * Represents the builder of zone partitions.
     */
    public static final class Builder {
        /**
         * The builder of the forests partition
         */
        private final ZonePartition.Builder<Zone.Forest> forests;

        /**
         * The builder of the meadows partition
         */
        private final ZonePartition.Builder<Zone.Meadow> meadows;

        /**
         * The builder of the rivers partition
         */
        private final ZonePartition.Builder<Zone.River> rivers;

        /**
         * The builder of the river systems partition
         */
        private final ZonePartition.Builder<Zone.Water> riverSystems;

        /**
         * Returns a new builder whose four partitions are initially identical to those of the given
         * group of four partitions.
         *
         * @param initial the initial group of four partitions
         */
        public Builder(ZonePartitions initial) {
            this.forests = new ZonePartition.Builder<>(initial.forests);
            this.meadows = new ZonePartition.Builder<>(initial.meadows);
            this.rivers = new ZonePartition.Builder<>(initial.rivers);
            this.riverSystems = new ZonePartition.Builder<>(initial.riverSystems);
        }

        /**
         * Adds to the partitions the areas corresponding to the zones of the given tile.
         *
         * @param tile the tile
         */
        public void addTile(Tile tile) {
            int[] openConnections = new int[10];
            // Calculate the number of open connections for each zone
            for (TileSide side : tile.sides()) {
                for (Zone zone : side.zones()) {
                    ++openConnections[zone.localId()];
                    // A lake can have a connection to a river and vice versa
                    if (zone instanceof Zone.River river && river.hasLake()) {
                        ++openConnections[river.lake().localId()];
                        ++openConnections[zone.localId()];
                    }
                }
            }
            Set<Zone.Lake> lakes = new HashSet<>();
            // Add zones to partitions
            for (Zone zone : tile.sideZones()) {
                switch (zone) {
                    case Zone.Forest f -> forests.addSingleton(f, openConnections[zone.localId()]);
                    case Zone.Meadow m -> meadows.addSingleton(m, openConnections[zone.localId()]);
                    case Zone.River r -> {
                        // Check if the river has a lake
                        if (r.hasLake()) {
                            // If a river has a lake, the river has in fact one less open connection
                            rivers.addSingleton(r, openConnections[r.localId()] - 1);
                            riverSystems.addSingleton(r, openConnections[r.localId()]);
                            // Prevent the same lake from being added twice
                            if (!lakes.contains(r.lake())) {
                                riverSystems.addSingleton(r.lake(), openConnections[r.lake().localId()]);
                                lakes.add(r.lake());
                            }
                            // Create the union between the river and the lake
                            riverSystems.union(r, r.lake());
                        } else {
                            rivers.addSingleton(r, openConnections[r.localId()]);
                            riverSystems.addSingleton(r, openConnections[r.localId()]);
                        }
                    }
                    default -> {
                    }
                }
            }
        }

        /**
         * Connects to each other the two given tile sides by connecting the corresponding areas.
         *
         * @param s1 the first tile side
         * @param s2 the second tile side
         * @throws IllegalArgumentException if the two given tile sides are not of the same kind
         */
        public void connectSides(TileSide s1, TileSide s2) {
            switch (s1) {
                case TileSide.Meadow(Zone.Meadow m1) when s2 instanceof TileSide.Meadow(Zone.Meadow m2) ->
                        meadows.union(m1, m2);
                case TileSide.Forest(Zone.Forest f1) when s2 instanceof TileSide.Forest(Zone.Forest f2) ->
                        forests.union(f1, f2);
                case TileSide.River(
                        Zone.Meadow m3, Zone.River r1, Zone.Meadow m4
                ) when s2 instanceof TileSide.River(Zone.Meadow m5, Zone.River r2, Zone.Meadow m6) -> {
                    riverSystems.union(r1, r2);
                    rivers.union(r1, r2);
                    meadows.union(m3, m6);
                    meadows.union(m4, m5);
                }
                default -> throw new IllegalArgumentException("The tile sides are not of the same kind");
            }
        }


        /**
         * Adds an initial occupant, of the given type and belonging to the given player,
         * to the area containing the given zone.
         *
         * @param player       the player
         * @param occupantKind the occupant kind
         * @param occupiedZone the occupied zone
         * @throws IllegalArgumentException if the occupant cannot be placed on the desired zone
         */
        public void addInitialOccupant(PlayerColor player, Occupant.Kind occupantKind, Zone occupiedZone) {
            if (occupantKind == Occupant.Kind.PAWN) {
                switch (occupiedZone) {
                    case Zone.Forest f -> forests.addInitialOccupant(f, player);
                    case Zone.Meadow m -> meadows.addInitialOccupant(m, player);
                    case Zone.River r -> rivers.addInitialOccupant(r, player);
                    default -> throw new IllegalArgumentException("A pawn cannot be placed on a lake");
                }
            } else {
                switch (occupiedZone) {
                    case Zone.River river when !river.hasLake() -> riverSystems.addInitialOccupant(river, player);
                    case Zone.River river -> riverSystems.addInitialOccupant(river.lake(), player);
                    case Zone.Lake lake -> riverSystems.addInitialOccupant(lake, player);
                    default -> throw new IllegalArgumentException("A hut can only be on a lake or river");
                }
            }
        }

        /**
         * Removes an occupant (a pawn) belonging to the given player from the area containing the given zone.
         *
         * @param player       the player
         * @param occupiedZone the occupied zone
         * @throws IllegalArgumentException if the zone is a lake
         */
        public void removePawn(PlayerColor player, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest f -> forests.removeOccupant(f, player);
                case Zone.Meadow m -> meadows.removeOccupant(m, player);
                case Zone.River r -> rivers.removeOccupant(r, player);
                default -> throw new IllegalArgumentException("A pawn cannot be removed from a lake");
            }
        }

        /**
         * Removes all occupants (pawns playing the role of gatherers) from the given forest.
         *
         * @param forest the forest to remove all pawns from
         */
        public void clearGatherers(Area<Zone.Forest> forest) {
            forests.removeAllOccupantsOf(forest);
        }

        /**
         * Removes all occupants (pawns playing the role of fishers) from the given river.
         *
         * @param river the river to remove all pawns from
         */
        public void clearFishers(Area<Zone.River> river) {
            rivers.removeAllOccupantsOf(river);
        }

        /**
         * Builds the group  of four partitions under construction.
         *
         * @return the group of four partitions under construction
         */
        public ZonePartitions build() {
            return new ZonePartitions(forests.build(), meadows.build(), rivers.build(), riverSystems.build());
        }

    }


}
