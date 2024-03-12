package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class ZonePartitionsTest {

    @Test
    void addTileWorks() {
        Zone.Meadow meadow0 = new Zone.Meadow(0, new ArrayList<>(List.of(new Animal(0
                , Animal.Kind.AUROCHS))), null);
        Zone.Forest forest1 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Meadow meadow2 = new Zone.Meadow(2, new ArrayList<>(), null);
        Zone.Lake lake8 = new Zone.Lake(8, 1, null);
        Zone.River river3 = new Zone.River(3, 0, lake8);

        TileSide.Meadow nSide = new TileSide.Meadow(meadow0);
        TileSide.Forest eSide = new TileSide.Forest(forest1);
        TileSide.Forest sSide = new TileSide.Forest(forest1);
        TileSide.River wSide = new TileSide.River(meadow2, river3, meadow0);

        Tile tile = new Tile(56, Tile.Kind.START, nSide, eSide, sSide, wSide);

        Set<Zone.Meadow> meadows1 = Set.of(meadow0);
        Set<Zone.Meadow> meadows2 = Set.of(meadow2);
        Set<Zone.Forest> forests = Set.of(forest1);
        Set<Zone.River> rivers = Set.of(river3);
        Set<Zone.Lake> lakes = Set.of(lake8);
        Set<Zone.Water> waterZones = Set.of(river3, lake8);

        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, new ArrayList<>(), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, new ArrayList<>(), 1);
        Area<Zone.Forest> forestArea = new Area<>(forests, new ArrayList<>(), 2);
        Area<Zone.River> riverArea = new Area<>(rivers, new ArrayList<>(), 1);
        Area<Zone.Water> waterArea = new Area<>(waterZones, new ArrayList<>(), 1);

        ZonePartition<Zone.Meadow> meadowZonePartition = new ZonePartition<>(Set.of(meadowArea1, meadowArea2));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));

        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
                riverZonePartition, waterZonePartition);
        ZonePartitions.Builder testBuilder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        testBuilder.addTile(tile);

        assertEquals(expectedZonePartitions, testBuilder.build());

    }

    @Test
    void connectSidesWorks() {
        // Meadows
        Zone.Meadow meadow1 = new Zone.Meadow(0, new ArrayList<>(List.of(new Animal(0
                , Animal.Kind.AUROCHS))), null);
        Zone.Meadow meadow2 = new Zone.Meadow(1, new ArrayList<>(), null);

        TileSide.Meadow meadowSide1 = new TileSide.Meadow(meadow1);
        TileSide.Meadow meadowSide2 = new TileSide.Meadow(meadow2);

        Set<Zone.Meadow> meadows1 = Set.of(meadow1);
        Set<Zone.Meadow> meadows2 = Set.of(meadow2);

        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, new ArrayList<>(), 1);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, new ArrayList<>(), 1);

        ZonePartition<Zone.Meadow> meadowZonePartition = new ZonePartition<>(Set.of(meadowArea1, meadowArea2));   
    }

}
