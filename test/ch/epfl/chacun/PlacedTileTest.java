package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlacedTileTest {
    @Test
    void placedTileConstructorThrowsWhenTileOrRotationOrPosIsNull() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        var tile = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        var occupant = new Occupant(Occupant.Kind.PAWN, 10);

        assertThrows(NullPointerException.class, () -> {
            new PlacedTile(null, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), occupant);
        });
        assertThrows(NullPointerException.class, () -> {
            new PlacedTile(tile, PlayerColor.RED, null, new Pos(0, 0), occupant);
        });
        assertThrows(NullPointerException.class, () -> {
            new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, null, occupant);
        });
    }

    @Test
    void placedTileConstructorDoesNotThrowWhenPlacerOrOccupantIsNull() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        var tile = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        var occupant = new Occupant(Occupant.Kind.PAWN, 10);

        assertDoesNotThrow(() -> {
            new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), occupant);
        });
        assertDoesNotThrow(() -> {
            new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), null);
        });
    }

    @Test
    void placedTileIdAndKindWork() {
        var forestZone = new Zone.Forest(93, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        var tile = new Tile(9, Tile.Kind.MENHIR, forestSide, forestSide, forestSide, forestSide);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(9, placedTile.id());
        assertEquals(Tile.Kind.MENHIR, placedTile.kind());
    }

    @Test
    void placedTileSideWorksWithAllRotationsAndDirections() {
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneN);
        var sideE = new TileSide.Forest(zoneE);
        var sideS = new TileSide.Forest(zoneS);
        var sideW = new TileSide.Forest(zoneW);

        var expectedSides = new ArrayList<>(List.of(sideN, sideE, sideS, sideW));

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        for (var r : List.of(Rotation.NONE, Rotation.RIGHT, Rotation.HALF_TURN, Rotation.LEFT)) {
            var placedTile = new PlacedTile(tile, PlayerColor.RED, r, new Pos(0, 0));
            var actualSides = new ArrayList<TileSide>();
            for (var d : List.of(Direction.N, Direction.E, Direction.S, Direction.W))
                actualSides.add(placedTile.side(d));
            assertEquals(expectedSides, actualSides);
            Collections.rotate(expectedSides, 1);
        }
    }

    @Test
    void placedTileZoneWithIdWorksWithExistingZone() {
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneN);
        var sideE = new TileSide.Forest(zoneE);
        var sideS = new TileSide.Forest(zoneS);
        var sideW = new TileSide.Forest(zoneW);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));

        assertEquals(zoneN, placedTile.zoneWithId(10));
        assertEquals(zoneE, placedTile.zoneWithId(11));
        assertEquals(zoneS, placedTile.zoneWithId(12));
        assertEquals(zoneW, placedTile.zoneWithId(13));
    }

    @Test
    void placedTileZoneWithIdThrowsWithNonExistingZone() {
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneN);
        var sideE = new TileSide.Forest(zoneE);
        var sideS = new TileSide.Forest(zoneS);
        var sideW = new TileSide.Forest(zoneW);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));

        assertThrows(IllegalArgumentException.class, () -> {
            placedTile.zoneWithId(14);
        });
    }

    @Test
    void placedTileSpecialPowerZoneWorksWithMeadowSpecialPower() {
        var zoneSpecialPower = new Zone.Meadow(10, List.of(), Zone.SpecialPower.SHAMAN);
        var zoneForest1 = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest3 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sides = new ArrayList<>(List.of(
            new TileSide.Meadow(zoneSpecialPower),
            new TileSide.Forest(zoneForest1),
            new TileSide.Forest(zoneForest2),
            new TileSide.Forest(zoneForest3)
        ));
        for (int i = 0; i < 4; i += 1) {
            var tile = new Tile(1, Tile.Kind.NORMAL, sides.get(0), sides.get(1), sides.get(2), sides.get(3));
            var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
            assertEquals(zoneSpecialPower, placedTile.specialPowerZone());
            Collections.rotate(sides, 1);
        }
    }

    @Test
    void placedTileSpecialPowerZoneWorksWithLakeSpecialPower() {
        var zoneSpecialPower = new Zone.Lake(18, 1, Zone.SpecialPower.LOGBOAT);
        var zoneRiver = new Zone.River(10, 0, zoneSpecialPower);
        var zoneMeadow = new Zone.Meadow(11, List.of(), null);

        var sideRiver = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var sideMeadow = new TileSide.Meadow(zoneMeadow);

        var sides = new ArrayList<>(List.of(sideRiver, sideMeadow, sideMeadow, sideMeadow));
        for (int i = 0; i < 4; i += 1) {
            var tile = new Tile(1, Tile.Kind.NORMAL, sides.get(0), sides.get(1), sides.get(2), sides.get(3));
            var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
            assertEquals(zoneSpecialPower, placedTile.specialPowerZone());
            Collections.rotate(sides, 1);
        }
    }

    @Test
    void placedTileSpecialPowerZoneWorksWithNoSpecialPower() {
        var zoneMeadow = new Zone.Meadow(10, List.of(), null);
        var zoneForest1 = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest3 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sides = new ArrayList<>(List.of(
            new TileSide.Meadow(zoneMeadow),
            new TileSide.Forest(zoneForest1),
            new TileSide.Forest(zoneForest2),
            new TileSide.Forest(zoneForest3)
        ));
        for (int i = 0; i < 4; i += 1) {
            var tile = new Tile(1, Tile.Kind.NORMAL, sides.get(0), sides.get(1), sides.get(2), sides.get(3));
            var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
            assertNull(placedTile.specialPowerZone());
            Collections.rotate(sides, 1);
        }
    }

    @Test
    void placedTileForestZonesWorks() {
        var zoneMeadow = new Zone.Meadow(10, List.of(), null);
        var zoneForest1 = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest3 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneForest1);
        var sideE = new TileSide.Forest(zoneForest2);
        var sideS = new TileSide.Forest(zoneForest3);
        var sideW = new TileSide.Meadow(zoneMeadow);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneForest1, zoneForest2, zoneForest3), placedTile.forestZones());
    }

    @Test
    void placedTileMeadowZonesWorks() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneMeadow3 = new Zone.Meadow(12, List.of(), null);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Meadow(zoneMeadow3);
        var sideW = new TileSide.Meadow(zoneMeadow1);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneMeadow1, zoneMeadow2, zoneMeadow3), placedTile.meadowZones());
    }

    @Test
    void placedTileRiverZonesWorks() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneMeadow3 = new Zone.Meadow(12, List.of(), null);
        var zoneMeadow4 = new Zone.Meadow(13, List.of(), null);

        var zoneLake1 = new Zone.Lake(18, 0, null);
        var zoneRiver1 = new Zone.River(14, 0, zoneLake1);
        var zoneRiver2 = new Zone.River(15, 0, zoneLake1);
        var zoneRiver3 = new Zone.River(16, 0, zoneLake1);
        var zoneRiver4 = new Zone.River(17, 0, zoneLake1);

        var sideN = new TileSide.River(zoneMeadow1, zoneRiver1, zoneMeadow2);
        var sideE = new TileSide.River(zoneMeadow2, zoneRiver2, zoneMeadow3);
        var sideS = new TileSide.River(zoneMeadow3, zoneRiver3, zoneMeadow4);
        var sideW = new TileSide.River(zoneMeadow4, zoneRiver4, zoneMeadow1);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneRiver1, zoneRiver2, zoneRiver3, zoneRiver4), placedTile.riverZones());
    }

    @Test
    void placedTilePotentialOccupantsWorksWithMeadowsAndForests() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);

        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var expectedOccupants = Set.of(
            new Occupant(Occupant.Kind.PAWN, 10),
            new Occupant(Occupant.Kind.PAWN, 11),
            new Occupant(Occupant.Kind.PAWN, 12),
            new Occupant(Occupant.Kind.PAWN, 13)
        );
        assertEquals(expectedOccupants, placedTile.potentialOccupants());
    }

    @Test
    void placedTilePotentialOccupantsWorksWithRiverButNoLake() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneRiver = new Zone.River(12, 0, null);

        var sideN = new TileSide.River(zoneMeadow1, zoneRiver, zoneMeadow2);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.River(zoneMeadow2, zoneRiver, zoneMeadow1);
        var sideW = new TileSide.Meadow(zoneMeadow1);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var expectedPotentialOccupants = Set.of(
                new Occupant(Occupant.Kind.PAWN, 10),
                new Occupant(Occupant.Kind.PAWN, 11),
                new Occupant(Occupant.Kind.PAWN, 12),
                new Occupant(Occupant.Kind.HUT, 12));
        assertEquals(expectedPotentialOccupants, placedTile.potentialOccupants());
    }

    @Test
    void placedTilePotentialOccupantsWorksWithRiverAndLake() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneMeadow3 = new Zone.Meadow(12, List.of(), null);
        var zoneMeadow4 = new Zone.Meadow(13, List.of(), null);

        var zoneLake1 = new Zone.Lake(18, 0, null);
        var zoneRiver1 = new Zone.River(14, 0, zoneLake1);
        var zoneRiver2 = new Zone.River(15, 0, zoneLake1);
        var zoneRiver3 = new Zone.River(16, 0, zoneLake1);
        var zoneRiver4 = new Zone.River(17, 0, zoneLake1);

        var sideN = new TileSide.River(zoneMeadow1, zoneRiver1, zoneMeadow2);
        var sideE = new TileSide.River(zoneMeadow2, zoneRiver2, zoneMeadow3);
        var sideS = new TileSide.River(zoneMeadow3, zoneRiver3, zoneMeadow4);
        var sideW = new TileSide.River(zoneMeadow4, zoneRiver4, zoneMeadow1);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var expectedPotentialOccupants = Set.of(
                new Occupant(Occupant.Kind.PAWN, 10),
                new Occupant(Occupant.Kind.PAWN, 11),
                new Occupant(Occupant.Kind.PAWN, 12),
                new Occupant(Occupant.Kind.PAWN, 13),
                new Occupant(Occupant.Kind.PAWN, 14),
                new Occupant(Occupant.Kind.PAWN, 15),
                new Occupant(Occupant.Kind.PAWN, 16),
                new Occupant(Occupant.Kind.PAWN, 17),
                new Occupant(Occupant.Kind.HUT, 18));
        assertEquals(expectedPotentialOccupants, placedTile.potentialOccupants());
    }

    @Test
    void placedTilePotentialOccupantsWorksWithTwoLakes() {
        var l0 = new Zone.Lake(18, 2, null);
        var l1 = new Zone.Lake(19, 2, null);
        var a0_0 = new Animal(100, Animal.Kind.DEER);
        var a0_1 = new Animal(101, Animal.Kind.DEER);
        var z0 = new Zone.Meadow(10, List.of(a0_0, a0_1), null);
        var z1 = new Zone.River(11, 0, l0);
        var z2 = new Zone.Meadow(12, List.of(), null);
        var z3 = new Zone.River(13, 0, l0);
        var z4 = new Zone.River(14, 0, l1);
        var z5 = new Zone.Meadow(15, List.of(), null);
        var z6 = new Zone.River(16, 0, l1);
        var sN = new TileSide.River(z0, z1, z2);
        var sE = new TileSide.River(z2, z3, z0);
        var sS = new TileSide.River(z0, z4, z5);
        var sW = new TileSide.River(z5, z6, z0);
        var tile = new Tile(83, Tile.Kind.MENHIR, sN, sE, sS, sW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var expectedPotentialOccupants = Set.of(
                new Occupant(Occupant.Kind.PAWN, 10),
                new Occupant(Occupant.Kind.PAWN, 11),
                new Occupant(Occupant.Kind.PAWN, 12),
                new Occupant(Occupant.Kind.PAWN, 13),
                new Occupant(Occupant.Kind.PAWN, 14),
                new Occupant(Occupant.Kind.PAWN, 15),
                new Occupant(Occupant.Kind.PAWN, 16),
                new Occupant(Occupant.Kind.HUT, 18),
                new Occupant(Occupant.Kind.HUT, 19));
        assertEquals(expectedPotentialOccupants, placedTile.potentialOccupants());
    }

    @Test
    void placedTileWithOccupantThrowsIfTileIsAlreadyOccupied() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 10));
        assertThrows(IllegalArgumentException.class, () -> {
            placedTile.withOccupant(new Occupant(Occupant.Kind.PAWN, 11));
        });
    }

    @Test
    void placedTileWithOccupantWorks() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var newOccupant = new Occupant(Occupant.Kind.PAWN, 10);
        var newPlacedTile = placedTile.withOccupant(newOccupant);
        assertEquals(newOccupant, newPlacedTile.occupant());
        assertEquals(placedTile.tile(), newPlacedTile.tile());
        assertEquals(placedTile.placer(), newPlacedTile.placer());
        assertEquals(placedTile.rotation(), newPlacedTile.rotation());
        assertEquals(placedTile.pos(), newPlacedTile.pos());
    }

    @Test
    void placedTileWithNoOccupantWorksForUnoccupiedTile() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var newPlacedTile = placedTile.withNoOccupant();
        assertEquals(placedTile, newPlacedTile);
    }

    @Test
    void placedTileWithNoOccupantWorksForOccupiedTile() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 10));
        var newPlacedTile = placedTile.withNoOccupant();
        assertNull(newPlacedTile.occupant());
        assertEquals(placedTile.tile(), newPlacedTile.tile());
        assertEquals(placedTile.placer(), newPlacedTile.placer());
        assertEquals(placedTile.rotation(), newPlacedTile.rotation());
        assertEquals(placedTile.pos(), newPlacedTile.pos());
    }

    @Test
    void placedTileIdOfZoneOccupiedByWorksWithUnoccupiedTile() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(-1, placedTile.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        assertEquals(-1, placedTile.idOfZoneOccupiedBy(Occupant.Kind.HUT));
    }

    @Test
    void placedTileIdOfZoneOccupiedByWorksWithOccupiedTile() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var occupant = new Occupant(Occupant.Kind.PAWN, 10);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), occupant);
        assertEquals(10, placedTile.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        assertEquals(-1, placedTile.idOfZoneOccupiedBy(Occupant.Kind.HUT));
    }

    @Test
    void placedTileIdOfZoneOccupiedByWorksWithWronglyOccupiedTile() {
        var zoneMeadow1 = new Zone.Meadow(10, List.of(), null);
        var zoneMeadow2 = new Zone.Meadow(11, List.of(), null);
        var zoneForest1 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneForest2 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Meadow(zoneMeadow1);
        var sideE = new TileSide.Meadow(zoneMeadow2);
        var sideS = new TileSide.Forest(zoneForest1);
        var sideW = new TileSide.Forest(zoneForest2);

        var tile = new Tile(1, Tile.Kind.NORMAL, sideN, sideE, sideS, sideW);
        var occupant = new Occupant(Occupant.Kind.HUT, 10);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), occupant);
        assertEquals(-1, placedTile.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        assertEquals(10, placedTile.idOfZoneOccupiedBy(Occupant.Kind.HUT));

        var occupant2 = new Occupant(Occupant.Kind.PAWN, 10);
        var placedTile2 = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), occupant2);
        assertEquals(10, placedTile2.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        assertEquals(-1, placedTile2.idOfZoneOccupiedBy(Occupant.Kind.HUT));
    }
}