package ch.epfl.chacun;

public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Lake> lakes,
                             ZonePartition<Zone.Water> riverSystems) {
    public final static ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>());

    public static final class Builder {

        private ZonePartition.Builder<Zone.Forest> forests;
        private ZonePartition.Builder<Zone.Meadow> meadows;
        private ZonePartition.Builder<Zone.River> rivers;
        private ZonePartition.Builder<Zone.Water> riverSystems;

        public Builder(ZonePartitions initial) {
            this.forests = new ZonePartition.Builder<>(initial.forests);
            this.meadows = new ZonePartition.Builder<>(initial.meadows);
            this.rivers = new ZonePartition.Builder<>(initial.rivers);
            this.riverSystems = new ZonePartition.Builder<>(initial.riverSystems);
        }

        public void addTile(Tile tile) {
            int[] openConnections = new int[10];
            // Calculate the number of open connections for each zone
            for (TileSide side : tile.sides()) {
                for (Zone zone : side.zones()) {
                    ++openConnections[zone.id()];
                    // A lake can have a connection to a river
                    // And vice versa
                    if (zone instanceof Zone.River river && river.hasLake()) {
                        ++openConnections[river.lake().id()];
                        ++openConnections[zone.id()];
                    }
                }
            }
            // Add the zones to the partitions
            for (Zone zone : tile.sideZones()) {
                switch (zone) {
                    case Zone.Forest f -> forests.addSingleton(f, openConnections[zone.id()]);
                    case Zone.Meadow m -> meadows.addSingleton(m, openConnections[zone.id()]);
                    case Zone.River r when !r.hasLake() -> rivers.addSingleton(r, openConnections[r.id()]);
                    case Zone.River r -> {
                        // If the river has a lake, the river has one less open connection
                        rivers.addSingleton(r, openConnections[r.id()] - 1);
                        // Create the union between the river and the lake
                        riverSystems.addSingleton(r, openConnections[r.id()]);
                        riverSystems.addSingleton(r.lake(), openConnections[r.lake().id()]);
                        riverSystems.union(r.lake(), r);
                    }
                    // A lake should not be in the side zones
                    default -> throw new IllegalArgumentException();
                }
            }
        }

    }


}
