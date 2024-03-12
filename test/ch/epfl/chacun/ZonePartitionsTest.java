package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ZonePartitionsTest {

    @Test
    void addTileWorks() {
        Zone.Meadow meadow0 = new Zone.Meadow(560, new ArrayList<>(List.of(new Animal(0
                , Animal.Kind.AUROCHS))), null);
        Zone.Meadow meadow2 = new Zone.Meadow(562, new ArrayList<>(), null);
        Zone.Forest forest1 = new Zone.Forest(561, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Lake lake8 = new Zone.Lake(568, 1, null);
        Zone.River river3 = new Zone.River(563, 0, lake8);

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

        assertTrue(expectedZonePartitions.meadows().equals(testBuilder.build().meadows()));

    }

    @Test
    void addTileWorksWith4Rivers() {
        Zone.Meadow meadow0 = new Zone.Meadow(130, new ArrayList<>(), null);
        Zone.Meadow meadow2 = new Zone.Meadow(132, new ArrayList<>(), null);
        Zone.Meadow meadow4 = new Zone.Meadow(134, new ArrayList<>(), null);
        Zone.Meadow meadow6 = new Zone.Meadow(136, new ArrayList<>(), null);
        Zone.Lake lake8 = new Zone.Lake(138, 2, null);
        Zone.River river1 = new Zone.River(131, 0, lake8);
        Zone.River river3 = new Zone.River(133, 0, lake8);
        Zone.River river5 = new Zone.River(135, 0, lake8);
        Zone.River river7 = new Zone.River(137, 0, lake8);

        TileSide.River nSide = new TileSide.River(meadow0, river1, meadow2);
        TileSide.River eSide = new TileSide.River(meadow2, river3, meadow4);
        TileSide.River sSide = new TileSide.River(meadow4, river5, meadow6);
        TileSide.River wSide = new TileSide.River(meadow6, river7, meadow0);

        Tile tile = new Tile(56, Tile.Kind.NORMAL, nSide, eSide, sSide, wSide);

        Area<Zone.Meadow> meadowArea1 = new Area<>(Set.of(meadow0), new ArrayList<>(), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(Set.of(meadow2), new ArrayList<>(), 2);
        Area<Zone.Meadow> meadowArea3 = new Area<>(Set.of(meadow4), new ArrayList<>(), 2);
        Area<Zone.Meadow> meadowArea4 = new Area<>(Set.of(meadow6), new ArrayList<>(), 2);
        Area<Zone.River> riverArea1 = new Area<>(Set.of(river1), new ArrayList<>(), 1);
        Area<Zone.River> riverArea2 = new Area<>(Set.of(river3), new ArrayList<>(), 1);
        Area<Zone.River> riverArea3 = new Area<>(Set.of(river5), new ArrayList<>(), 1);
        Area<Zone.River> riverArea4 = new Area<>(Set.of(river7), new ArrayList<>(), 1);
        Area<Zone.Water> waterArea = new Area<>(Set.of(river1, river3, river5, river7, lake8), new ArrayList<>(), 4);

        ZonePartition<Zone.Meadow> meadowZonePartition = new ZonePartition<>(Set.of(meadowArea1, meadowArea2, meadowArea3, meadowArea4));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>();
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea1, riverArea2, riverArea3, riverArea4));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));

        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
                riverZonePartition, waterZonePartition);
        ZonePartitions.Builder testBuilder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        testBuilder.addTile(tile);

        assertEquals(expectedZonePartitions, testBuilder.build());

    }

    @Test
    void connectSidesWorks() {
        Zone.Meadow meadow0 = new Zone.Meadow(560, new ArrayList<>(), null);
        Zone.Meadow meadow = new Zone.Meadow(170, new ArrayList<>(), null);
        Zone.Forest forest1 = new Zone.Forest(561, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Lake lake8 = new Zone.Lake(568, 1, null);
        Zone.River river3 = new Zone.River(563, 0, lake8);

//        Set<Zone.Meadow> meadows1 = Set.of(meadow1);
//        Set<Zone.Meadow> meadows2 = Set.of(meadow2);
//        Set<Zone.Forest> forests = Set.of(forest1);
//        Set<Zone.River> rivers = Set.of(river3);
//        Set<Zone.Lake> lakes = Set.of(lake8);
//        Set<Zone.Water> waterZones = Set.of(river3, lake8);
//
//        TileSide meadowSide1 = new TileSide.Meadow(meadow1);
//        TileSide meadowSide2 = new TileSide.Meadow(meadow2);
//        TileSide.Forest forestSide1 = new TileSide.Forest(forest1);
//        TileSide.Forest sSide = new TileSide.Forest(forest1);
//        TileSide.River wSide = new TileSide.River(meadow2, river3, meadow1);


        // Tile tile = new Tile(56, Tile.Kind.START, meadowSide1, forestSide1, , wSide);

//        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, List.of(PlayerColor.YELLOW), 2);
//        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, new ArrayList<>(), 1);
//        Area<Zone.Forest> forestArea = new Area<>(forests, new ArrayList<>(), 2);
//        Area<Zone.River> riverArea = new Area<>(rivers, new ArrayList<>(), 1);
//        Area<Zone.Water> waterArea = new Area<>(waterZones, new ArrayList<>(), 1);

//        ZonePartition<Zone.Meadow> meadowZonePartition = new ZonePartition<>(Set.of(meadowArea1, meadowArea2));
//        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
//        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea));
//        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));
//
//        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
//                riverZonePartition, waterZonePartition);
//        ZonePartitions.Builder testBuilder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
//        testBuilder.addTile();
//        testBuilder.connectSides(meadowSide1, meadowSide2);
//
//        assertEquals(expectedZonePartitions, testBuilder.build());
    }

    @Test
    void addInitialOccupantWorksWithPawn() {
        Zone.Meadow meadow0 = new Zone.Meadow(560, new ArrayList<>(List.of(new Animal(0
                , Animal.Kind.AUROCHS))), null);
        Zone.Forest forest1 = new Zone.Forest(561, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Meadow meadow2 = new Zone.Meadow(562, new ArrayList<>(), null);
        Zone.Lake lake8 = new Zone.Lake(568, 1, null);
        Zone.River river3 = new Zone.River(563, 0, lake8);

        TileSide.Meadow nSide = new TileSide.Meadow(meadow0);
        TileSide.Forest eSide = new TileSide.Forest(forest1);
        TileSide.Forest sSide = new TileSide.Forest(forest1);
        TileSide.River wSide = new TileSide.River(meadow2, river3, meadow0);

        Tile tile = new Tile(56, Tile.Kind.START, nSide, eSide, sSide, wSide);

        Set<Zone.Meadow> meadows1 = Set.of(meadow0);
        Set<Zone.Meadow> meadows2 = Set.of(meadow2);
        Set<Zone.Forest> forests = Set.of(forest1);
        Set<Zone.River> rivers = Set.of(river3);
        Set<Zone.Water> waterZones = Set.of(river3, lake8);

        Area<Zone.Meadow> meadowArea1WithOccupant = new Area<>(meadows1, List.of(PlayerColor.YELLOW), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, new ArrayList<>(), 1);
        Area<Zone.Forest> forestArea = new Area<>(forests, new ArrayList<>(), 2);
        Area<Zone.River> riverArea = new Area<>(rivers, new ArrayList<>(), 1);
        Area<Zone.Water> waterArea = new Area<>(waterZones, new ArrayList<>(), 1);

        ZonePartition<Zone.Meadow> meadowZonePartitionWithOccupant =
                new ZonePartition<>(Set.of(meadowArea1WithOccupant, meadowArea2));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));

        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        builder.addTile(tile);
        builder.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, meadow0);
        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartitionWithOccupant,
                riverZonePartition, waterZonePartition);

        assertEquals(expectedZonePartitions, builder.build());
    }

}
