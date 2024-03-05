package ch.epfl.chacun;

public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Lake> lakes,
                             ZonePartition<Zone.Water> riverSystems) {

    public final static ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>());
}
