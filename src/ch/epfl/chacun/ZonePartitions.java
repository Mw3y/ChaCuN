package ch.epfl.chacun;

public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Lake> lakes,
                             ZonePartition<Zone.Water> riverSystems) {
    public final static ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>());


    public static final class Builder {

        private ZonePartition.Builder<Zone.Forest> forests;
        private ZonePartition.Builder<Zone.Meadow> meadows;
        private ZonePartition.Builder<Zone.River> rivers;
        private ZonePartition.Builder<Zone.Lake> lakes;

        public Builder(ZonePartitions initial) {
            this.forests = new ZonePartition.Builder<>(initial.forests);
            this.meadows = new ZonePartition.Builder<>(initial.meadows);
            this.rivers = new ZonePartition.Builder<>(initial.rivers);
            this.lakes = new ZonePartition.Builder<>(initial.lakes);
        }

        public void addTile(Tile tile) {
            int[] openConnections = new int[10];
            for (TileSide side : tile.sides()) {
                for (Zone zone : side.zones()) {
                    ++openConnections[zone.id()];
                }
            }

            // if (tile.zones().stream().some)

        }

    }

}
