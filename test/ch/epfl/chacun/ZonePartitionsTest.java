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
        Zone.Meadow meadow1_0 = new Zone.Meadow(560, new ArrayList<>(), null);
        Zone.Meadow meadow1_2 = new Zone.Meadow(562, new ArrayList<>(), null);
        Zone.Forest forest1_1 = new Zone.Forest(561, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Lake lake8 = new Zone.Lake(568, 1, null);
        Zone.River river1_3 = new Zone.River(563, 0, lake8);
        Zone.Meadow meadow2_0 = new Zone.Meadow(170, new ArrayList<>(), null);
        Zone.Meadow meadow2_2 = new Zone.Meadow(172, new ArrayList<>(), null);
        Zone.Meadow meadow2_6 = new Zone.Meadow(176, new ArrayList<>(), null);
        Zone.River river2_1 = new Zone.River(171, 0, null);
        Zone.River river2_3 = new Zone.River(173, 0, null);
        Zone.River river2_5 = new Zone.River(175, 0, null);
        Zone.River river2_7 = new Zone.River(177, 0, null);

        Set<Zone.Meadow> meadows1 = Set.of(meadow1_0, meadow2_2);
        Set<Zone.Meadow> meadows2 = Set.of(meadow2_0, meadow1_2);
        Set<Zone.Meadow> meadows3 = Set.of(meadow2_6);
        Set<Zone.Forest> forests = Set.of(forest1_1);
        Set<Zone.River> rivers1 = Set.of(river1_3, river2_1, river2_3);
        Set<Zone.River> rivers2 = Set.of(river2_5, river2_7);
        Set<Zone.Water> waterZones1 = Set.of(river1_3, river2_1, river2_3, lake8);
        Set<Zone.Water> waterZones2 = Set.of(river2_5, river2_7);

        TileSide.Meadow meadowSide1_1 = new TileSide.Meadow(meadow1_0);
        TileSide.Forest forestSide1_2 = new TileSide.Forest(forest1_1);
        TileSide.Forest forestSide1_3 = new TileSide.Forest(forest1_1);
        TileSide.River riverSide1_4 = new TileSide.River(meadow1_2, river1_3, meadow1_0);

        TileSide.River riverSide2_1 = new TileSide.River(meadow2_0, river2_1, meadow2_2);
        TileSide.River riverSide2_2 = new TileSide.River(meadow2_2, river2_3, meadow2_0);
        TileSide.River riverSide2_3 = new TileSide.River(meadow2_0, river2_5, meadow2_6);
        TileSide.River riverSide2_4 = new TileSide.River(meadow2_6, river2_7, meadow2_0);

        Tile tile1 = new Tile(56, Tile.Kind.START, meadowSide1_1, forestSide1_2, forestSide1_3, riverSide1_4);
        Tile tile2 = new Tile(17, Tile.Kind.NORMAL, riverSide2_1, riverSide2_2, riverSide2_3, riverSide2_4);

        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, new ArrayList<>(), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, new ArrayList<>(), 3);
        Area<Zone.Meadow> meadowArea3 = new Area<>(meadows3, new ArrayList<>(), 2);
        Area<Zone.Forest> forestArea = new Area<>(forests, new ArrayList<>(), 2);
        Area<Zone.River> riverArea1 = new Area<>(rivers1, new ArrayList<>(), 1);
        Area<Zone.River> riverArea2 = new Area<>(rivers2, new ArrayList<>(), 2);
        Area<Zone.Water> waterArea1 = new Area<>(waterZones1, new ArrayList<>(), 1);
        Area<Zone.Water> waterArea2 = new Area<>(waterZones2, new ArrayList<>(), 2);

        ZonePartition<Zone.Meadow> meadowZonePartition = new ZonePartition<>(Set.of(meadowArea3, meadowArea2, meadowArea1));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea2, riverArea1));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea2, waterArea1));

        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
                riverZonePartition, waterZonePartition);
        ZonePartitions.Builder testBuilder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        testBuilder.addTile(tile1);
        testBuilder.addTile(tile2);
        testBuilder.connectSides(riverSide1_4, riverSide2_2);

        assertEquals(expectedZonePartitions, testBuilder.build());
    }

    @Test
    void addInitialOccupantWorks() {
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

        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, List.of(PlayerColor.YELLOW), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, List.of(PlayerColor.GREEN), 1);
        Area<Zone.Forest> forestArea = new Area<>(forests, new ArrayList<>(), 2);
        Area<Zone.River> riverArea = new Area<>(rivers, new ArrayList<>(), 1);
        Area<Zone.Water> waterArea = new Area<>(waterZones, List.of(PlayerColor.RED), 1);

        ZonePartition<Zone.Meadow> meadowZonePartition =
                new ZonePartition<>(Set.of(meadowArea1, meadowArea2));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));

        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        builder.addTile(tile);
        builder.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, meadow0);
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, lake8);
        builder.addInitialOccupant(PlayerColor.GREEN, Occupant.Kind.PAWN, meadow2);
        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
                riverZonePartition, waterZonePartition);

        assertEquals(expectedZonePartitions, builder.build());
        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, meadow0));
        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.GREEN, Occupant.Kind.HUT, river3));
    }

    @Test
    void removePawnWorks() {
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

        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, List.of(), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, List.of(), 1);
        Area<Zone.Forest> forestArea = new Area<>(forests, List.of(), 2);
        Area<Zone.River> riverArea = new Area<>(rivers, List.of(), 1);
        Area<Zone.Water> waterArea = new Area<>(waterZones, List.of(PlayerColor.RED), 1);

        ZonePartition<Zone.Meadow> meadowZonePartition =
                new ZonePartition<>(Set.of(meadowArea1, meadowArea2));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));

        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        builder.addTile(tile);
        builder.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, meadow0);
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, lake8);
        builder.addInitialOccupant(PlayerColor.GREEN, Occupant.Kind.PAWN, meadow2);
        builder.removePawn(PlayerColor.YELLOW, meadow0);
        builder.removePawn(PlayerColor.GREEN, meadow2);
        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
                riverZonePartition, waterZonePartition);

        assertEquals(expectedZonePartitions, builder.build());
        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.GREEN, meadow0));
        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.BLUE, forest1));
    }

    @Test
    void clearWorks() {
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

        Area<Zone.Meadow> meadowArea1 = new Area<>(meadows1, List.of(PlayerColor.YELLOW), 2);
        Area<Zone.Meadow> meadowArea2 = new Area<>(meadows2, List.of(), 1);
        Area<Zone.Forest> forestArea = new Area<>(forests, List.of(), 2);
        Area<Zone.River> riverArea = new Area<>(rivers, List.of(), 1);
        Area<Zone.Water> waterArea = new Area<>(waterZones, List.of(), 1);

        ZonePartition<Zone.Meadow> meadowZonePartition =
                new ZonePartition<>(Set.of(meadowArea1, meadowArea2));
        ZonePartition<Zone.Forest> forestZonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition<Zone.River> riverZonePartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartition<Zone.Water> waterZonePartition = new ZonePartition<>(Set.of(waterArea));

        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        builder.addTile(tile);
        builder.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, meadow0);
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, river3);
        builder.addInitialOccupant(PlayerColor.GREEN, Occupant.Kind.PAWN, forest1);
        builder.clearGatherers(forestArea.withInitialOccupant(PlayerColor.GREEN));
        builder.clearFishers(riverArea.withInitialOccupant(PlayerColor.RED));
        ZonePartitions expectedZonePartitions = new ZonePartitions(forestZonePartition, meadowZonePartition,
                riverZonePartition, waterZonePartition);

        assertEquals(expectedZonePartitions, builder.build());
        assertThrows(IllegalArgumentException.class, () -> builder.clearGatherers(forestArea.withInitialOccupant(PlayerColor.GREEN)));
    }

}
