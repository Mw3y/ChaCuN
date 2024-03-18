package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZonePartitionsTest {
    @Test
    void zonePartitionsEmptyContainsFourEmptyPartitions() {
        assertEquals(Set.of(), ZonePartitions.EMPTY.forests().areas());
        assertEquals(Set.of(), ZonePartitions.EMPTY.meadows().areas());
        assertEquals(Set.of(), ZonePartitions.EMPTY.rivers().areas());
        assertEquals(Set.of(), ZonePartitions.EMPTY.riverSystems().areas());
    }

    @Test
    void zonePartitionsBuilderAddTileWorksWithOneMeadowAndOneForest() {
        // Tile 32
        var z0 = new Zone.Forest(32_0, Zone.Forest.Kind.WITH_MENHIR);
        var a1_0 = new Animal(32_1_0, Animal.Kind.TIGER);
        var z1 = new Zone.Meadow(32_1, List.of(a1_0), null);
        var sN = new TileSide.Forest(z0);
        var sE = new TileSide.Meadow(z1);
        var sS = new TileSide.Meadow(z1);
        var sW = new TileSide.Forest(z0);
        var tile = new Tile(32, Tile.Kind.NORMAL, sN, sE, sS, sW);

        var emptyPartitions = new ZonePartitions(
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>());
        var b = new ZonePartitions.Builder(emptyPartitions);
        b.addTile(tile);
        var partitions = b.build();

        var expectedMeadows = Set.of(new Area<>(Set.of(z1), List.of(), 2));
        var expectedForests = Set.of(new Area<>(Set.of(z0), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedForests, partitions.forests().areas());
        assertEquals(Set.of(), partitions.rivers().areas());
        assertEquals(Set.of(), partitions.riverSystems().areas());
    }

    @Test
    void zonePartitionsBuilderAddTileWorksWithOneRiverAndTwoMeadows() {
        // Tile 52
        var a0_0 = new Animal(52_0_0, Animal.Kind.DEER);
        var z0 = new Zone.Meadow(52_0, List.of(a0_0), null);
        var z1 = new Zone.River(52_1, 0, null);
        var z2 = new Zone.Meadow(52_2, List.of(), null);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.River(z0, z1, z2);
        var sS = new TileSide.Meadow(z2);
        var sW = new TileSide.River(z2, z1, z0);
        var tile = new Tile(52, Tile.Kind.NORMAL, sN, sE, sS, sW);

        var emptyPartitions = new ZonePartitions(
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>());
        var b = new ZonePartitions.Builder(emptyPartitions);
        b.addTile(tile);
        var partitions = b.build();

        var expectedMeadows = Set.of(
                new Area<>(Set.of(z0), List.of(), 3),
                new Area<>(Set.of(z2), List.of(), 3));
        var expectedRivers = Set.of(new Area<>(Set.of(z1), List.of(), 2));
        var expectedRiverSystems = Set.of(new Area<>(Set.of(z1), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedRivers, partitions.rivers().areas());
        assertEquals(expectedRiverSystems, partitions.riverSystems().areas());
        assertEquals(Set.of(), partitions.forests().areas());
    }

    @Test
    void zonePartitionsBuilderAddTileWorksWithOneForestOneMeadowOneRiverOneLake() {
        // Tile 56
        var l0 = new Zone.Lake(56_8, 1, null);
        var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
        var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
        var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
        var z2 = new Zone.Meadow(56_2, List.of(), null);
        var z3 = new Zone.River(56_3, 0, l0);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.Forest(z1);
        var sS = new TileSide.Forest(z1);
        var sW = new TileSide.River(z2, z3, z0);
        var tile = new Tile(56, Tile.Kind.START, sN, sE, sS, sW);

        var emptyPartitions = new ZonePartitions(
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>());
        var b = new ZonePartitions.Builder(emptyPartitions);
        b.addTile(tile);
        var partitions = b.build();

        var expectedMeadows = Set.of(
                new Area<>(Set.of(z0), List.of(), 2),
                new Area<>(Set.of(z2), List.of(), 1));
        var expectedRivers = Set.of(new Area<>(Set.of(z3), List.of(), 1));
        var expectedRiverSystems = Set.of(new Area<>(Set.of(z3, l0), List.of(), 1));
        var expectedForests = Set.of(new Area<>(Set.of(z1), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedRivers, partitions.rivers().areas());
        assertEquals(expectedRiverSystems, partitions.riverSystems().areas());
        assertEquals(expectedForests, partitions.forests().areas());
    }

    @Test
    void zonePartitionsBuilderAddTileWorksWithThreeMeadowsTwoLakes() {
        // Tile 83
        var l0 = new Zone.Lake(83_8, 2, null);
        var l1 = new Zone.Lake(83_9, 2, null);
        var a0_0 = new Animal(83_0_0, Animal.Kind.DEER);
        var a0_1 = new Animal(83_0_1, Animal.Kind.DEER);
        var z0 = new Zone.Meadow(83_0, List.of(a0_0, a0_1), null);
        var z1 = new Zone.River(83_1, 0, l0);
        var z2 = new Zone.Meadow(83_2, List.of(), null);
        var z3 = new Zone.River(83_3, 0, l0);
        var z4 = new Zone.River(83_4, 0, l1);
        var z5 = new Zone.Meadow(83_5, List.of(), null);
        var z6 = new Zone.River(83_6, 0, l1);
        var sN = new TileSide.River(z0, z1, z2);
        var sE = new TileSide.River(z2, z3, z0);
        var sS = new TileSide.River(z0, z4, z5);
        var sW = new TileSide.River(z5, z6, z0);
        var tile = new Tile(83, Tile.Kind.MENHIR, sN, sE, sS, sW);

        var emptyPartitions = new ZonePartitions(
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>());
        var b = new ZonePartitions.Builder(emptyPartitions);
        b.addTile(tile);
        var partitions = b.build();

        var expectedMeadows = Set.of(
                new Area<>(Set.of(z0), List.of(), 4),
                new Area<>(Set.of(z2), List.of(), 2),
                new Area<>(Set.of(z5), List.of(), 2));
        var expectedRivers = Set.of(
                new Area<>(Set.of(z1), List.of(), 1),
                new Area<>(Set.of(z3), List.of(), 1),
                new Area<>(Set.of(z4), List.of(), 1),
                new Area<>(Set.of(z6), List.of(), 1));
        var expectedRiverSystems = Set.of(
                new Area<>(Set.of(z1, l0, z3), List.of(), 2),
                new Area<>(Set.of(z4, l1, z6), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedRivers, partitions.rivers().areas());
        assertEquals(expectedRiverSystems, partitions.riverSystems().areas());
        assertEquals(Set.of(), partitions.forests().areas());
    }

    @Test
    void zonePartitionsBuilderConnectSidesWorksWithMeadowSides() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.connectSides(s46_N, s47_N);
        var partitions = b.build();

        var expectedMeadows = Set.of(
                new Area<>(Set.of(z46_0, z47_0), List.of(), 4),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_2), List.of(), 2));
        var expectedForests = Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1));
        var expectedRivers = Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2));
        var expectedRiverSystems = Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedForests, partitions.forests().areas());
        assertEquals(expectedRivers, partitions.rivers().areas());
        assertEquals(expectedRiverSystems, partitions.riverSystems().areas());
    }

    @Test
    void zonePartitionsBuilderConnectSidesWorksWithForestSides() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.connectSides(s46_S, s47_S);
        var partitions = b.build();

        var expectedMeadows = Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_2), List.of(), 2));
        var expectedForests = Set.of(
                new Area<>(Set.of(z46_3, z47_3), List.of(), 0));
        var expectedRivers = Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2));
        var expectedRiverSystems = Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedForests, partitions.forests().areas());
        assertEquals(expectedRivers, partitions.rivers().areas());
        assertEquals(expectedRiverSystems, partitions.riverSystems().areas());
    }

    @Test
    void zonePartitionsBuilderConnectSidesWorksWithRiverSides() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.connectSides(s46_E, s47_W);
        var partitions = b.build();

        var expectedMeadows = Set.of(
                new Area<>(Set.of(z46_0, z47_0), List.of(), 4),
                new Area<>(Set.of(z46_2, z47_2), List.of(), 2));
        var expectedForests = Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1));
        var expectedRivers = Set.of(
                new Area<>(Set.of(z46_1, z47_1), List.of(), 2));
        var expectedRiverSystems = Set.of(
                new Area<>(Set.of(z46_1, z47_1), List.of(), 2));

        assertEquals(expectedMeadows, partitions.meadows().areas());
        assertEquals(expectedForests, partitions.forests().areas());
        assertEquals(expectedRivers, partitions.rivers().areas());
        assertEquals(expectedRiverSystems, partitions.riverSystems().areas());
    }

    @Test
    void zonePartitionsBuilderConnectSidesThrowsWithIncompatibleSides() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        {
            var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
            assertThrows(IllegalArgumentException.class, () -> {
                b.connectSides(s46_N, s47_E);
            });
        }
        {
            var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
            assertThrows(IllegalArgumentException.class, () -> {
                b.connectSides(s46_N, s47_S);
            });
        }
        {
            var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
            assertThrows(IllegalArgumentException.class, () -> {
                b.connectSides(s46_S, s47_W);
            });
        }
    }

    @Test
    void zonePartitionsBuilderAddInitialOccupantWorksForGatherers() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, z46_3);
        b.addInitialOccupant(PlayerColor.BLUE, Occupant.Kind.PAWN, z47_3);
        var partitions = b.build();

        var expectedForests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(PlayerColor.RED), 1),
                new Area<>(Set.of(z47_3), List.of(PlayerColor.BLUE), 1)));
        assertEquals(expectedForests, partitions.forests());
        assertEquals(meadows, partitions.meadows());
        assertEquals(rivers, partitions.rivers());
        assertEquals(riverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderAddInitialOccupantWorksForHunters() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.addInitialOccupant(PlayerColor.GREEN, Occupant.Kind.PAWN, z46_0);
        b.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, z46_2);
        b.addInitialOccupant(PlayerColor.PURPLE, Occupant.Kind.PAWN, z47_0);
        b.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, z47_2);
        var partitions = b.build();

        var expectedMeadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(PlayerColor.GREEN), 3),
                new Area<>(Set.of(z46_2), List.of(PlayerColor.YELLOW), 2),
                new Area<>(Set.of(z47_0), List.of(PlayerColor.PURPLE), 3),
                new Area<>(Set.of(z47_2), List.of(PlayerColor.RED), 2)));
        assertEquals(expectedMeadows, partitions.meadows());
        assertEquals(forests, partitions.forests());
        assertEquals(rivers, partitions.rivers());
        assertEquals(riverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderAddInitialOccupantWorksForFishers() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.addInitialOccupant(PlayerColor.YELLOW, Occupant.Kind.PAWN, z46_1);
        b.addInitialOccupant(PlayerColor.PURPLE, Occupant.Kind.PAWN, z47_1);
        var partitions = b.build();

        var expectedRivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(PlayerColor.YELLOW), 2),
                new Area<>(Set.of(z47_1), List.of(PlayerColor.PURPLE), 2)));
        assertEquals(forests, partitions.forests());
        assertEquals(meadows, partitions.meadows());
        assertEquals(expectedRivers, partitions.rivers());
        assertEquals(riverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderAddInitialOccupantWorksForFishingHuts() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, z46_1);
        b.addInitialOccupant(PlayerColor.BLUE, Occupant.Kind.HUT, z47_1);
        var partitions = b.build();

        var expectedRiverSystems = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(PlayerColor.RED), 2),
                new Area<>(Set.of(z47_1), List.of(PlayerColor.BLUE), 2)));
        assertEquals(forests, partitions.forests());
        assertEquals(meadows, partitions.meadows());
        assertEquals(rivers, partitions.rivers());
        assertEquals(expectedRiverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderAddInitialOccupantThrowsForIncorrectOccupants() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        {
            var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
            assertThrows(IllegalArgumentException.class, () -> {
                b.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, z46_0);
            });
        }
        {
            var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
            assertThrows(IllegalArgumentException.class, () -> {
                b.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, z46_3);
            });
        }
    }

    @Test
    void zonePartitionsBuilderRemovePawnWorksForGatherers() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(PlayerColor.RED), 1),
                new Area<>(Set.of(z47_3), List.of(PlayerColor.BLUE), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.removePawn(PlayerColor.RED, z46_3);
        b.removePawn(PlayerColor.BLUE, z47_3);
        var partitions = b.build();

        var expectedForests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        assertEquals(expectedForests, partitions.forests());
        assertEquals(meadows, partitions.meadows());
        assertEquals(rivers, partitions.rivers());
        assertEquals(riverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderRemovePawnWorksForHunters() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(PlayerColor.GREEN), 3),
                new Area<>(Set.of(z46_2), List.of(PlayerColor.YELLOW), 2),
                new Area<>(Set.of(z47_0), List.of(PlayerColor.PURPLE), 3),
                new Area<>(Set.of(z47_2), List.of(PlayerColor.RED), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.removePawn(PlayerColor.GREEN, z46_0);
        b.removePawn(PlayerColor.YELLOW, z46_2);
        b.removePawn(PlayerColor.PURPLE, z47_0);
        b.removePawn(PlayerColor.RED, z47_2);
        var partitions = b.build();

        var expectedMeadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        assertEquals(expectedMeadows, partitions.meadows());
        assertEquals(forests, partitions.forests());
        assertEquals(rivers, partitions.rivers());
        assertEquals(riverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderRemovePawnWorksForFishers() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0), List.of(), 3),
                new Area<>(Set.of(z46_2), List.of(), 2),
                new Area<>(Set.of(z47_0), List.of(), 3),
                new Area<>(Set.of(z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(PlayerColor.YELLOW), 2),
                new Area<>(Set.of(z47_1), List.of(PlayerColor.PURPLE), 2)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.removePawn(PlayerColor.YELLOW, z46_1);
        b.removePawn(PlayerColor.PURPLE, z47_1);
        var partitions = b.build();

        var expectedRivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1), List.of(), 2),
                new Area<>(Set.of(z47_1), List.of(), 2)));
        assertEquals(forests, partitions.forests());
        assertEquals(meadows, partitions.meadows());
        assertEquals(expectedRivers, partitions.rivers());
        assertEquals(riverSystems, partitions.riverSystems());
    }

    @Test
    void zonePartitionsBuilderRemovePawnThrowsForInvalidZone() {
        // Tile 56
        var l0 = new Zone.Lake(56_8, 1, null);
        var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
        var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
        var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
        var z2 = new Zone.Meadow(56_2, List.of(), null);
        var z3 = new Zone.River(56_3, 0, l0);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.Forest(z1);
        var sS = new TileSide.Forest(z1);
        var sW = new TileSide.River(z2, z3, z0);
        var tile = new Tile(56, Tile.Kind.START, sN, sE, sS, sW);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z0), List.of(), 2),
                new Area<>(Set.of(z2), List.of(), 1)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z1), List.of(), 2)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z3), List.of(), 1)));
        var riverSystems = new ZonePartition<>(Set.<Area<Zone.Water>>of(
                new Area<>(Set.of(z3), List.of(), 2),
                new Area<>(Set.of(l0), List.of(PlayerColor.BLUE), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        assertThrows(IllegalArgumentException.class, () -> {
            b.removePawn(PlayerColor.BLUE, l0);
        });
    }

    @Test
    void zonePartitionsBuilderClearGatherersWorks() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0, z47_0), List.of(PlayerColor.PURPLE), 4),
                new Area<>(Set.of(z46_2, z47_2), List.of(), 2)));
        var forestArea = new Area<>(Set.of(z46_3), List.of(PlayerColor.BLUE), 1);
        var forests = new ZonePartition<>(Set.of(
                forestArea,
                new Area<>(Set.of(z47_3), List.of(PlayerColor.YELLOW), 1)));
        var rivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1, z47_1), List.of(PlayerColor.RED, PlayerColor.BLUE), 2)));
        var riverSystems = new ZonePartition<>(Set.of(
                new Area<>(Set.<Zone.Water>of(z46_1, z47_1), List.of(PlayerColor.YELLOW), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.clearGatherers(forestArea);
        var partition = b.build();

        var expectedForests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(), 1),
                new Area<>(Set.of(z47_3), List.of(PlayerColor.YELLOW), 1)));
        assertEquals(expectedForests, partition.forests());
        assertEquals(meadows, partition.meadows());
        assertEquals(rivers, partition.rivers());
        assertEquals(riverSystems, partition.riverSystems());
    }

    @Test
    void zonePartitionsBuilderClearFishersWorks() {
        // Tile 46
        var z46_0 = new Zone.Meadow(46_0, List.of(), null);
        var z46_1 = new Zone.River(46_1, 0, null);
        var z46_2 = new Zone.Meadow(46_2, List.of(), null);
        var z46_3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
        var s46_N = new TileSide.Meadow(z46_0);
        var s46_E = new TileSide.River(z46_0, z46_1, z46_2);
        var s46_S = new TileSide.Forest(z46_3);
        var s46_W = new TileSide.River(z46_2, z46_1, z46_0);

        // Tile 47
        var z47_0 = new Zone.Meadow(47_0, List.of(), null);
        var z47_1 = new Zone.River(47_1, 1, null);
        var a47_2_0 = new Animal(47_2_0, Animal.Kind.DEER);
        var z47_2 = new Zone.Meadow(47_2, List.of(a47_2_0), null);
        var z47_3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
        var s47_N = new TileSide.Meadow(z47_0);
        var s47_E = new TileSide.River(z47_0, z47_1, z47_2);
        var s47_S = new TileSide.Forest(z47_3);
        var s47_W = new TileSide.River(z47_2, z47_1, z47_0);

        var meadows = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_0, z47_0), List.of(PlayerColor.PURPLE), 4),
                new Area<>(Set.of(z46_2, z47_2), List.of(), 2)));
        var forests = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_3), List.of(PlayerColor.BLUE), 1),
                new Area<>(Set.of(z47_3), List.of(), 1)));
        var riverArea = new Area<>(Set.of(z46_1, z47_1), List.of(PlayerColor.RED, PlayerColor.BLUE), 2);
        var rivers = new ZonePartition<>(Set.of(riverArea));
        var riverSystems = new ZonePartition<>(Set.of(
                new Area<>(Set.<Zone.Water>of(z46_1, z47_1), List.of(PlayerColor.YELLOW), 2)));

        var b = new ZonePartitions.Builder(new ZonePartitions(forests, meadows, rivers, riverSystems));
        b.clearFishers(riverArea);
        var partition = b.build();

        var expectedRivers = new ZonePartition<>(Set.of(
                new Area<>(Set.of(z46_1, z47_1), List.of(), 2)));
        assertEquals(expectedRivers, partition.rivers());
        assertEquals(meadows, partition.meadows());
        assertEquals(forests, partition.forests());
        assertEquals(riverSystems, partition.riverSystems());
    }
}