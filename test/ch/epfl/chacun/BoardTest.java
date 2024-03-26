package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void boardTileAtReturnsNullForPositionsOutsideOfBoard() {
        var board = Board.EMPTY;
        assertNull(board.tileAt(new Pos(-100, -100)));
    }

    @Test
    void boardTileAtWorksWithStartingTile() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var board = Board.EMPTY.withNewTile(t56);
        for (int x = -12; x <= 12; x += 1) {
            for (int y = -12; y <= 12; y += 1) {
                if (x == 0 && y == 0) {
                    assertEquals(t56, board.tileAt(new Pos(x, y)));
                } else {
                    assertNull(board.tileAt(new Pos(x, y)));
                }
            }
        }
    }

    @Test
    void boardTileWithIdWorksWithTilesOnBoard() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        assertEquals(t56, board.tileWithId(56));
        assertEquals(t17, board.tileWithId(17));
        assertEquals(t27, board.tileWithId(27));
    }

    @Test
    void boardTileWithIdThrowsWithUnplacedTile() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        for (int i = 0; i < 95; i += 1) {
            if (i == 17 || i == 27 || i == 56) continue;
            var tileId = i;
            assertThrows(IllegalArgumentException.class, () -> board.tileWithId(tileId));
        }
    }

    @Test
    void boardCancelledAnimalsIsImmutable() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var cancelledAnimals = Set.of(
                new Animal(56_0_0, Animal.Kind.AUROCHS),
                new Animal(17_2_0, Animal.Kind.DEER),
                new Animal(17_4_0, Animal.Kind.TIGER));
        var mutableCancelledAnimals = new HashSet<>(cancelledAnimals);

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27)
                .withMoreCancelledAnimals(mutableCancelledAnimals);

        mutableCancelledAnimals.clear();
        assertEquals(cancelledAnimals, board.cancelledAnimals());

        try {
            board.cancelledAnimals().clear();
        } catch (UnsupportedOperationException e) {
            // nothing to do
        }
        assertEquals(cancelledAnimals, board.cancelledAnimals());
    }

    @Test
    void boardOccupantsWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var occupant17 = new Occupant(Occupant.Kind.PAWN, 17_0);
        var occupant27 = new Occupant(Occupant.Kind.HUT, 27_1);

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withOccupant(occupant17)
                .withNewTile(t27)
                .withOccupant(occupant27);

        assertEquals(Set.of(occupant17, occupant27), board.occupants());
    }

    @Test
    void boardForestAreaWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var allForests = Stream.of(t17, t27, t56)
                .flatMap(t -> t.forestZones().stream())
                .collect(Collectors.toSet());

        var forest56 = t56.forestZones().iterator().next();
        assertEquals(new Area<>(allForests, List.of(), 1), board.forestArea(forest56));
    }

    @Test
    void boardMeadowAreaWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var aurochsZone = (Zone.Meadow) t56.zoneWithId(56_0);
        var aurochAndDeerMeadows = Set.of(
                aurochsZone,
                (Zone.Meadow) t17.zoneWithId(17_2));

        var emptyStartingZone = (Zone.Meadow) t56.zoneWithId(56_2);
        var emptyMeadows = Set.of(
                emptyStartingZone,
                (Zone.Meadow) t17.zoneWithId(17_0));

        var area1 = new Area<>(aurochAndDeerMeadows, List.of(), 2);
        var area2 = new Area<>(emptyMeadows, List.of(), 3);

        assertEquals(area1, board.meadowArea(aurochsZone));
        assertEquals(area2, board.meadowArea(emptyStartingZone));
    }

    @Test
    void boardRiverAreaWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var startingRiver = (Zone.River) t56.zoneWithId(56_3);
        var lakeConnectedRivers = Set.of(
                startingRiver,
                (Zone.River) t17.zoneWithId(17_1));
        var area = new Area<>(lakeConnectedRivers, List.of(), 1);
        assertEquals(area, board.riverArea(startingRiver));
    }

    @Test
    void boardRiverSystemAreaWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var startingRiver = (Zone.River) t56.zoneWithId(56_3);
        var riverSystemZones = Set.<Zone.Water>of(
                startingRiver,
                (Zone.River) t17.zoneWithId(17_1),
                (Zone.Lake) t56.zoneWithId(56_8));
        var area = new Area<>(riverSystemZones, List.of(), 1);
        assertEquals(area, board.riverSystemArea(startingRiver));
    }

    @Test
    void boardMeadowAreasWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var meadows1 = Set.of(
                ((Zone.Meadow) t17.zoneWithId(17_0)),
                ((Zone.Meadow) t56.zoneWithId(56_2)));
        var area1 = new Area<>(meadows1, List.of(), 3);

        var meadows2 = Set.of((Zone.Meadow) t17.zoneWithId(17_4));
        var area2 = new Area<>(meadows2, List.of(), 2);

        var meadows3 = Set.of(
                (Zone.Meadow) t17.zoneWithId(17_2),
                (Zone.Meadow) t56.zoneWithId(56_0));
        var area3 = new Area<>(meadows3, List.of(), 2);

        var meadows4 = Set.of((Zone.Meadow) t27.zoneWithId(27_0));
        var area4 = new Area<>(meadows4, List.of(), 3);

        var meadows5 = Set.of((Zone.Meadow) t27.zoneWithId(27_2));
        var area5 = new Area<>(meadows5, List.of(), 2);

        var allMeadowAreas = Set.of(area1, area2, area3, area4, area5);
        assertEquals(allMeadowAreas, board.meadowAreas());
    }

    @Test
    void boardRiverSystemAreasWorksOnSmallExample() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t01 = new PlacedTile(allTiles.get(1), null, Rotation.NONE, new Pos(-1, 0));
        var t03 = new PlacedTile(allTiles.get(3), null, Rotation.NONE, new Pos(-2, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t01)
                .withNewTile(t03);

        var waterZones = Set.of(
                (Zone.Water) t56.zoneWithId(56_8),
                (Zone.Water) t56.zoneWithId(56_3),
                (Zone.Water) t01.zoneWithId(1_1),
                (Zone.Water) t01.zoneWithId(1_5),
                (Zone.Water) t01.zoneWithId(1_8),
                (Zone.Water) t03.zoneWithId(3_1),
                (Zone.Water) t03.zoneWithId(3_3),
                (Zone.Water) t03.zoneWithId(3_5),
                (Zone.Water) t03.zoneWithId(3_8));
        var riverSystem = new Area<>(waterZones, List.of(), 2);
        assertEquals(Set.of(riverSystem), board.riverSystemAreas());
    }

    @Test
    void boardAdjacentMeadowWorksOnGivenExample() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.RED, Rotation.LEFT, new Pos(1, 0));
        var t60 = new PlacedTile(allTiles.get(60), PlayerColor.BLUE, Rotation.RIGHT, new Pos(1, -1));
        var t49 = new PlacedTile(allTiles.get(49), PlayerColor.GREEN, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.YELLOW, Rotation.NONE, new Pos(-2, 0));
        var t62 = new PlacedTile(allTiles.get(62), PlayerColor.PURPLE, Rotation.NONE, new Pos(-2, -1));
        var t61 = new PlacedTile(allTiles.get(61), PlayerColor.RED, Rotation.NONE, new Pos(-1, -1));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1));

        var meadow56 = (Zone.Meadow) t56.zoneWithId(56_0);
        var meadow94 = (Zone.Meadow) t94.zoneWithId(94_1);
        var meadow60 = (Zone.Meadow) t60.zoneWithId(60_2);
        var meadow49 = (Zone.Meadow) t49.zoneWithId(49_0);
        var meadow62 = (Zone.Meadow) t62.zoneWithId(62_0);
        var meadow42 = (Zone.Meadow) t42.zoneWithId(42_1);
        var meadow61 = (Zone.Meadow) t61.zoneWithId(61_0);

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t42)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, meadow42.id()))
                .withNewTile(t60)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, meadow60.id()))
                .withNewTile(t49)
                .withNewTile(t27)
                .withNewTile(t62)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, meadow62.id()))
                .withNewTile(t61)
                .withNewTile(t94);

        var adjacentMeadows = Set.of(meadow60, meadow56, meadow94, meadow49, meadow61);
        var occupants = Stream.of(PlayerColor.BLUE, PlayerColor.PURPLE)
                .sorted()
                .toList();

        var actualAdjacentMeadow = board.adjacentMeadow(t94.pos(), meadow94);
        assertEquals(adjacentMeadows, actualAdjacentMeadow.zones());
        assertEquals(occupants, actualAdjacentMeadow.occupants());
    }

    @Test
    void boardOccupantCountWorks() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.RED, Rotation.LEFT, new Pos(1, 0));
        var t60 = new PlacedTile(allTiles.get(60), PlayerColor.BLUE, Rotation.RIGHT, new Pos(1, -1));
        var t49 = new PlacedTile(allTiles.get(49), PlayerColor.GREEN, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.YELLOW, Rotation.NONE, new Pos(-2, 0));
        var t62 = new PlacedTile(allTiles.get(62), PlayerColor.PURPLE, Rotation.NONE, new Pos(-2, -1));
        var t61 = new PlacedTile(allTiles.get(61), PlayerColor.RED, Rotation.NONE, new Pos(-1, -1));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t42)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 42_1))
                .withNewTile(t60)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 60_1))
                .withNewTile(t49)
                .withOccupant(new Occupant(Occupant.Kind.HUT, 49_1))
                .withNewTile(t27)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 27_1))
                .withNewTile(t62)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 62_0))
                .withNewTile(t61)
                .withNewTile(t94)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 94_0));

        var expectedPawns = Map.of(
                PlayerColor.RED, 1,
                PlayerColor.BLUE, 2,
                PlayerColor.GREEN, 0,
                PlayerColor.YELLOW, 1,
                PlayerColor.PURPLE, 1);

        var expectedHuts = Map.of(
                PlayerColor.RED, 0,
                PlayerColor.BLUE, 0,
                PlayerColor.GREEN, 1,
                PlayerColor.YELLOW, 0,
                PlayerColor.PURPLE, 0);

        expectedPawns.forEach((p, c) ->
                assertEquals(c, board.occupantCount(p, Occupant.Kind.PAWN)));
        expectedHuts.forEach((p, c) ->
                assertEquals(c, board.occupantCount(p, Occupant.Kind.HUT)));
    }

    @Test
    void boardInsertionPositionsWorksWithTilesInTheMiddleOfBoard() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.RED, Rotation.LEFT, new Pos(1, 0));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t42)
                .withNewTile(t94);

        var expectedInsertionPositions = Set.of(
                new Pos(1, 1),
                new Pos(0, 1),
                new Pos(0, -2),
                new Pos(-1, 0),
                new Pos(1, -1),
                new Pos(2, 0),
                new Pos(-1, -1));

        assertEquals(expectedInsertionPositions, board.insertionPositions());
    }

    @Test
    void boardInsertionPositionsWorkWithTilesOnBoardSide() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t1 = new PlacedTile(allTiles.get(1), null, Rotation.NONE, new Pos(-1, 0));
        var t3 = new PlacedTile(allTiles.get(3), null, Rotation.NONE, new Pos(-2, 0));
        var t5 = new PlacedTile(allTiles.get(5), null, Rotation.NONE, new Pos(-3, 0));
        var t6 = new PlacedTile(allTiles.get(6), null, Rotation.NONE, new Pos(-4, 0));
        var t8 = new PlacedTile(allTiles.get(8), null, Rotation.NONE, new Pos(-5, 0));
        var t10 = new PlacedTile(allTiles.get(10), null, Rotation.NONE, new Pos(-6, 0));
        var t11 = new PlacedTile(allTiles.get(11), null, Rotation.NONE, new Pos(-7, 0));
        var t13 = new PlacedTile(allTiles.get(13), null, Rotation.NONE, new Pos(-8, 0));
        var t46 = new PlacedTile(allTiles.get(46), null, Rotation.NONE, new Pos(-9, 0));
        var t47 = new PlacedTile(allTiles.get(47), null, Rotation.NONE, new Pos(-10, 0));
        var t48 = new PlacedTile(allTiles.get(48), null, Rotation.NONE, new Pos(-11, 0));
        var t49 = new PlacedTile(allTiles.get(49), null, Rotation.NONE, new Pos(-12, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t1)
                .withNewTile(t3)
                .withNewTile(t5)
                .withNewTile(t6)
                .withNewTile(t8)
                .withNewTile(t10)
                .withNewTile(t11)
                .withNewTile(t13)
                .withNewTile(t46)
                .withNewTile(t47)
                .withNewTile(t48)
                .withNewTile(t49);

        var expectedInsertionPositions = new HashSet<>(Set.of(new Pos(1, 0)));
        for (int x = -12; x <= 0; x += 1)
            expectedInsertionPositions.addAll(Set.of(new Pos(x, -1), new Pos(x, 1)));

        assertEquals(expectedInsertionPositions, board.insertionPositions());
    }

    @Test
    void boardLastPlacedTileWorks() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.RED, Rotation.LEFT, new Pos(1, 0));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1));

        var board = Board.EMPTY;
        assertNull(board.lastPlacedTile());
        board = board.withNewTile(t56);
        assertEquals(t56, board.lastPlacedTile());
        board = board.withNewTile(t42);
        assertEquals(t42, board.lastPlacedTile());
        board = board.withNewTile(t94);
        assertEquals(t94, board.lastPlacedTile());
    }

    @Test
    void boardForestsClosedByLastTileWorksWithNoForestClosed() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY.withNewTile(t56);
        assertEquals(Set.of(), board.forestsClosedByLastTile());

        board = board.withNewTile(t17);
        assertEquals(Set.of(), board.forestsClosedByLastTile());

        board = board.withNewTile(t27);
        assertEquals(Set.of(), board.forestsClosedByLastTile());
    }

    @Test
    void boardForestsClosedByLastTileWorksWithOneClosedForest() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        var t37 = new PlacedTile(allTiles.get(37), PlayerColor.RED, Rotation.NONE, new Pos(0, 1));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27)
                .withNewTile(t37);

        var forest56 = (Zone.Forest) t56.zoneWithId(56_1);
        var forest27 = (Zone.Forest) t27.zoneWithId(27_3);
        var forest37 = (Zone.Forest) t37.zoneWithId(37_0);

        var expectedForest = new Area<>(Set.of(forest56, forest27, forest37), List.of(), 0);
        assertEquals(Set.of(expectedForest), board.forestsClosedByLastTile());
    }

    @Test
    void boardForestsClosedByLastTileWorksWithOneComplexClosedForest() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), PlayerColor.RED, Rotation.NONE, new Pos(0,0));
        var t1 = new PlacedTile(allTiles.get(1), null, Rotation.NONE, new Pos(-1,0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.RED, Rotation.NONE, new Pos(-1,1));
        var t47 = new PlacedTile(allTiles.get(47), PlayerColor.RED, Rotation.RIGHT, new Pos(1,0));
        var t28 = new PlacedTile(allTiles.get(28), PlayerColor.RED, Rotation.RIGHT, new Pos(1,1));
        var t58 = new PlacedTile(allTiles.get(58), PlayerColor.RED, Rotation.NONE, new Pos(0,1));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t1)
                .withNewTile(t42)
                .withNewTile(t47)
                .withNewTile(t28)
                .withNewTile(t58);

        var forest56 = (Zone.Forest) t56.zoneWithId(56_1);
        var forest1 = (Zone.Forest) t1.zoneWithId(1_3);
        var forest42 = (Zone.Forest) t42.zoneWithId(42_0);
        var forest47 = (Zone.Forest) t47.zoneWithId(47_3);
        var forest28 = (Zone.Forest) t28.zoneWithId(28_3);
        var forest58 = (Zone.Forest) t58.zoneWithId(58_0);

        var forestZones = Set.of(forest56, forest1, forest42, forest47, forest28, forest58);

        var expectedForest = new Area<>(forestZones, List.of(), 0);
        assertEquals(Set.of(expectedForest), board.forestsClosedByLastTile());
    }

    @Test
    void boardForestsClosedByLastTileWorksWithThreeForests() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), PlayerColor.RED, Rotation.NONE, new Pos(0,0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.RED, Rotation.NONE, new Pos(0,1));
        var t54 = new PlacedTile(allTiles.get(54), PlayerColor.RED, Rotation.NONE, new Pos(1,1));
        var t53 = new PlacedTile(allTiles.get(53), PlayerColor.RED, Rotation.NONE, new Pos(2,1));
        var t34 = new PlacedTile(allTiles.get(34), PlayerColor.RED, Rotation.NONE, new Pos(2,0));
        var t14 = new PlacedTile(allTiles.get(14), PlayerColor.RED, Rotation.NONE, new Pos(1,0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t67)
                .withNewTile(t54)
                .withNewTile(t53)
                .withNewTile(t34)
                .withNewTile(t14);

        var forests1 = Set.of(
                (Zone.Forest) t56.zoneWithId(56_1),
                (Zone.Forest) t67.zoneWithId(67_0),
                (Zone.Forest) t14.zoneWithId(14_5));
        var forests2 = Set.of(
                (Zone.Forest) t67.zoneWithId(67_1),
                (Zone.Forest) t54.zoneWithId(54_0),
                (Zone.Forest) t14.zoneWithId(14_4));
        var forests3 = Set.of(
                (Zone.Forest) t34.zoneWithId(34_3),
                (Zone.Forest) t14.zoneWithId(14_3));

        var forest1 = new Area<>(forests1, List.of(), 0);
        var forest2 = new Area<>(forests2, List.of(), 0);
        var forest3 = new Area<>(forests3, List.of(), 0);
        assertEquals(Set.of(forest1, forest2, forest3), board.forestsClosedByLastTile());
    }

    @Test
    void boardRiversClosedByLastTileWorksWithNoRiverClosed() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.RED, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY.withNewTile(t56);
        assertEquals(Set.of(), board.riversClosedByLastTile());

        board = board.withNewTile(t17);
        assertEquals(Set.of(), board.riversClosedByLastTile());

        board = board.withNewTile(t27);
        assertEquals(Set.of(), board.riversClosedByLastTile());
    }

    @Test
    void boardRiversClosedByLastTileWorksWithOneClosedRiver() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t53 = new PlacedTile(allTiles.get(53), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        var t86 = new PlacedTile(allTiles.get(86), PlayerColor.RED, Rotation.NONE, new Pos(-2, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t53)
                .withNewTile(t86);

        var rivers = Set.of(
                (Zone.River) t56.zoneWithId(56_3),
                (Zone.River) t53.zoneWithId(53_1),
                (Zone.River) t86.zoneWithId(86_3));

        var expectedRiver = new Area<>(rivers, List.of(), 0);
        assertEquals(Set.of(expectedRiver), board.riversClosedByLastTile());
    }

    @Test
    void boardRiversClosedByLastTileWorksWithOneRiverLoop() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), PlayerColor.RED, Rotation.NONE, new Pos(0,0));
        var t61 = new PlacedTile(allTiles.get(61), PlayerColor.RED, Rotation.NONE, new Pos(0,-1));
        var t3 = new PlacedTile(allTiles.get(3), PlayerColor.RED, Rotation.NONE, new Pos(-1,0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.RED, Rotation.NONE, new Pos(-2,0));
        var t15 = new PlacedTile(allTiles.get(15), PlayerColor.RED, Rotation.NONE, new Pos(-2,-1));
        var t16 = new PlacedTile(allTiles.get(16), PlayerColor.RED, Rotation.RIGHT, new Pos(-1,-1));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t61)
                .withNewTile(t3)
                .withNewTile(t17)
                .withNewTile(t15)
                .withNewTile(t16);

        var rivers = Set.of(
                (Zone.River) t3.zoneWithId(3_1),
                (Zone.River) t3.zoneWithId(3_5),
                (Zone.River) t17.zoneWithId(17_1),
                (Zone.River) t15.zoneWithId(15_1),
                (Zone.River) t16.zoneWithId(16_1));

        var expectedRiver = new Area<>(rivers, List.of(), 0);
        assertEquals(Set.of(expectedRiver), board.riversClosedByLastTile());
    }

    @Test
    void boardRiversClosedByLastTileWorksWithFourRivers() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), PlayerColor.RED, Rotation.NONE, new Pos(0,0));
        var t44 = new PlacedTile(allTiles.get(44), PlayerColor.RED, Rotation.NONE, new Pos(0,-1));
        var t58 = new PlacedTile(allTiles.get(58), PlayerColor.RED, Rotation.NONE, new Pos(-1,-1));
        var t60 = new PlacedTile(allTiles.get(60), PlayerColor.RED, Rotation.NONE, new Pos(-2,-1));
        var t2 = new PlacedTile(allTiles.get(2), PlayerColor.RED, Rotation.NONE, new Pos(-2,0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.RED, Rotation.NONE, new Pos(-2,1));
        var t0 = new PlacedTile(allTiles.get(0), PlayerColor.RED, Rotation.NONE, new Pos(-1,1));
        var t13 = new PlacedTile(allTiles.get(13), PlayerColor.RED, Rotation.NONE, new Pos(-1,0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t44)
                .withNewTile(t58)
                .withNewTile(t60)
                .withNewTile(t2)
                .withNewTile(t42)
                .withNewTile(t0)
                .withNewTile(t13);

        var rivers1 = Set.of(
                (Zone.River) t56.zoneWithId(56_3),
                (Zone.River) t13.zoneWithId(13_3));
        var rivers2 = Set.of(
                (Zone.River) t0.zoneWithId(1),
                (Zone.River) t13.zoneWithId(13_5));
        var rivers3 = Set.of(
                (Zone.River) t2.zoneWithId(2_1),
                (Zone.River) t13.zoneWithId(13_7));
        var rivers4 = Set.of(
                (Zone.River) t58.zoneWithId(58_2),
                (Zone.River) t13.zoneWithId(13_1));

        var expectedRivers = Set.of(
                new Area<>(rivers1, List.of(), 0),
                new Area<>(rivers2, List.of(), 0),
                new Area<>(rivers3, List.of(), 0),
                new Area<>(rivers4, List.of(), 0));
        assertEquals(expectedRivers, board.riversClosedByLastTile());
    }

    @Test
    void boardCouldPlaceTileWorksWhenTileCanBePlaced() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));

        var board = Board.EMPTY.withNewTile(t56);

        for (var tile : allTiles) {
            if (tile.id() == 56) continue;
            assertTrue(board.couldPlaceTile(tile));
        }
    }

    @Test
    void boardCouldPlaceTileWorksWhenTileCannotBePlaced() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t65 = new PlacedTile(allTiles.get(65), null, Rotation.NONE, new Pos(0, -1));
        var t2 = new PlacedTile(allTiles.get(2), null, Rotation.NONE, new Pos(-1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t65); // block meadow
        for (var tileId : List.of(61, 62, 85))
            assertFalse(board.couldPlaceTile(allTiles.get(tileId)));

        var board2 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t2); // block river
        for (var tileId : List.of(13, 17, 83))
            assertFalse(board2.couldPlaceTile(allTiles.get(tileId)));
    }

    @Test
    void boardCanAddTileWorksForOutsideOfBordOrOccupiedPositions() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t65 = new PlacedTile(allTiles.get(65), null, Rotation.NONE, new Pos(0, -1));
        // t2 is outside of board
        var t2 = new PlacedTile(allTiles.get(2), null, Rotation.NONE, new Pos(13, 13));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t65);

        assertFalse(board.canAddTile(t56));
        assertFalse(board.canAddTile(t65));
        assertFalse(board.canAddTile(t2));
    }

    @Test
    void boardCanAddTileWorksForPositionsWithoutNeighbors() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        // t65 and t2 are not next to the t56
        var t65 = new PlacedTile(allTiles.get(65), null, Rotation.NONE, new Pos(0, -2));
        var t2 = new PlacedTile(allTiles.get(2), null, Rotation.NONE, new Pos(1, 1));

        var board = Board.EMPTY
                .withNewTile(t56);

        assertFalse(board.canAddTile(t65));
        assertFalse(board.canAddTile(t2));
    }

    @Test
    void boardCanAddTileWorksForVariousRotations() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t0 = allTiles.get(0);

        var board = Board.EMPTY
                .withNewTile(t56);

        record PosRot(Pos pos, Rotation rotation) {}
        var validPositionsAndRotations = Set.of(
                new PosRot(new Pos(1, 0), Rotation.NONE),
                new PosRot(new Pos(1, 0), Rotation.RIGHT),
                new PosRot(new Pos(0, 1), Rotation.RIGHT),
                new PosRot(new Pos(0, 1), Rotation.HALF_TURN),
                new PosRot(new Pos(-1, 0), Rotation.RIGHT),
                new PosRot(new Pos(0, -1), Rotation.RIGHT));
        for (var rotation : Rotation.ALL) {
            for (var pos : List.of(new Pos(1, 0), new Pos(0, 1), new Pos(-1, 0), new Pos(0, -1))) {
                var tile = new PlacedTile(t0, null, rotation, pos);
                assertEquals(
                        validPositionsAndRotations.contains(new PosRot(pos, rotation)),
                        board.canAddTile(tile));
            }
        }
    }

    // No explicit test for `withNewTile`, which is used extensively by other tests

    @Test
    void boardWithOccupantAddsOccupantToBoardAndPartition() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0));

        var occupant17 = new Occupant(Occupant.Kind.PAWN, 17_0);
        var occupant27 = new Occupant(Occupant.Kind.PAWN, 27_3);

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withOccupant(occupant17)
                .withNewTile(t27)
                .withOccupant(occupant27);

        var occupiedMeadow = board.meadowArea((Zone.Meadow) t17.zoneWithId(17_0));
        assertEquals(List.of(PlayerColor.BLUE), occupiedMeadow.occupants());
        assertEquals(occupant17, board.tileWithId(17).occupant());

        var occupiedForest = board.forestArea((Zone.Forest) t27.zoneWithId(27_3));
        assertEquals(List.of(PlayerColor.GREEN), occupiedForest.occupants());
        assertEquals(occupant27, board.tileWithId(27).occupant());
    }

    @Test
    void boardWithoutOccupantRemovesOccupantFromBoardAndPartition() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.GREEN, Rotation.NONE, new Pos(-2, 0));

        var occupant17 = new Occupant(Occupant.Kind.PAWN, 17_3);
        var occupant27 = new Occupant(Occupant.Kind.HUT, 27_1);

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withOccupant(occupant17)
                .withNewTile(t27)
                .withOccupant(occupant27)
                .withoutOccupant(occupant17);

        var unoccupiedRiver = board.riverArea((Zone.River) t17.zoneWithId(17_3));
        assertEquals(List.of(), unoccupiedRiver.occupants());
        assertNull(board.tileWithId(17).occupant());

        var occupiedRiverSystem = board.riverSystemArea((Zone.River) t17.zoneWithId(17_3));
        assertEquals(List.of(PlayerColor.GREEN), occupiedRiverSystem.occupants());
        assertEquals(occupant27, board.tileWithId(27).occupant());
    }

    @Test
    void boardWithoutGatherersOrFishersInWorksWithForest() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0,0));
        var t1 = new PlacedTile(allTiles.get(1), PlayerColor.RED, Rotation.NONE, new Pos(-1,0));
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.GREEN, Rotation.NONE, new Pos(-1,1));
        var t47 = new PlacedTile(allTiles.get(47), PlayerColor.BLUE, Rotation.RIGHT, new Pos(1,0));
        var t28 = new PlacedTile(allTiles.get(28), PlayerColor.YELLOW, Rotation.RIGHT, new Pos(1,1));
        var t58 = new PlacedTile(allTiles.get(58), PlayerColor.PURPLE, Rotation.NONE, new Pos(0,1));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t1)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 1_3)) // gatherer
                .withNewTile(t42)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 42_1)) // hunter
                .withNewTile(t47)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 47_3)) // gatherer
                .withNewTile(t28)
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 28_3)) // gatherer
                .withNewTile(t58);

        var forest56 = (Zone.Forest) t56.zoneWithId(56_1);
        var meadow42 = (Zone.Meadow) t42.zoneWithId(42_1);

        var occupiedForest = board.forestArea(forest56);
        assertEquals(
                List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW),
                occupiedForest.occupants());
        assertEquals(List.of(PlayerColor.GREEN), board.meadowArea(meadow42).occupants());

        var board1 = board.withoutGatherersOrFishersIn(Set.of(occupiedForest), Set.of());
        assertEquals(List.of(), board1.forestArea(forest56).occupants());
        assertEquals(List.of(PlayerColor.GREEN), board1.meadowArea(meadow42).occupants());
    }

    @Test
    void boardWithMoreCancelledAnimalsWorks() {
        var allTiles = allTiles();
        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var animalsToCancel = List.of(
                new Animal(17_4_0, Animal.Kind.TIGER),
                new Animal(56_0_0, Animal.Kind.AUROCHS),
                new Animal(27_2_0, Animal.Kind.DEER),
                new Animal(17_2_0, Animal.Kind.DEER));

        for (int i = 0; i < animalsToCancel.size(); i += 1) {
            assertEquals(Set.copyOf(animalsToCancel.subList(0, i)), board.cancelledAnimals());
            board = board.withMoreCancelledAnimals(Set.of(animalsToCancel.get(i)));
        }
        assertEquals(Set.copyOf(animalsToCancel), board.cancelledAnimals());
    }

    @Test
    void boardEqualsComparesByStructure() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0));

        var board1 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var board2 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        assertEquals(board1, board2);
    }

    @Test
    void boardEqualsComparesAllComponents() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0));

        var board = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17);

        assertNotEquals(board, board.withNewTile(t27));
        assertNotEquals(board, board.withOccupant(new Occupant(Occupant.Kind.PAWN, 17_0)));
        assertNotEquals(board, board.withMoreCancelledAnimals(Set.of(new Animal(17_4_0, Animal.Kind.TIGER))));

        var board1 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);
        var board2 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t27)
                .withNewTile(t17);
        assertNotEquals(board1, board2);
    }

    @Test
    void boardHashcodeHashedByStructure() {
        var allTiles = allTiles();

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t17 = new PlacedTile(allTiles.get(17), PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0));

        var board1 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        var board2 = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t17)
                .withNewTile(t27);

        assertEquals(board1.hashCode(), board2.hashCode());
    }

    //<editor-fold desc="Tiles">
    private static List<Tile> allTiles() {
        ArrayList<Tile> tiles = new ArrayList<>();
        {   // Tile 0
            var l1 = new Zone.Lake(/*0_*/8, 2, null);
            var z0 = new Zone.Meadow(/*0_*/0, List.of(), null);
            var z1 = new Zone.River(/*0_*/1, 0, l1);
            var z2 = new Zone.Meadow(/*0_*/2, List.of(), null);
            var z3 = new Zone.Forest(/*0_*/3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(/*0_*/4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(0, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 1
            var l1 = new Zone.Lake(1_8, 3, null);
            var z0 = new Zone.Meadow(1_0, List.of(), null);
            var z1 = new Zone.River(1_1, 0, l1);
            var z2 = new Zone.Meadow(1_2, List.of(), null);
            var z3 = new Zone.Forest(1_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(1_4, List.of(), null);
            var z5 = new Zone.River(1_5, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 2
            var l1 = new Zone.Lake(2_8, 1, null);
            var z0 = new Zone.Meadow(2_0, List.of(), null);
            var z1 = new Zone.River(2_1, 0, l1);
            var z2 = new Zone.Meadow(2_2, List.of(), null);
            var z3 = new Zone.Forest(2_3, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(2, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 3
            var l1 = new Zone.Lake(3_8, 1, null);
            var z0 = new Zone.Meadow(3_0, List.of(), null);
            var z1 = new Zone.River(3_1, 0, l1);
            var z2 = new Zone.Meadow(3_2, List.of(), null);
            var z3 = new Zone.River(3_3, 0, l1);
            var z4 = new Zone.Meadow(3_4, List.of(), null);
            var z5 = new Zone.River(3_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(3, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 4
            var l1 = new Zone.Lake(4_8, 1, null);
            var z0 = new Zone.Meadow(4_0, List.of(), null);
            var z1 = new Zone.River(4_1, 0, l1);
            var a2_0 = new Animal(4_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(4_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(4_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(4_4, List.of(), null);
            var z5 = new Zone.River(4_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(4, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 5
            var l1 = new Zone.Lake(5_8, 1, null);
            var a0_0 = new Animal(5_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(5_0, List.of(a0_0), null);
            var z1 = new Zone.River(5_1, 0, l1);
            var z2 = new Zone.Meadow(5_2, List.of(), null);
            var z3 = new Zone.River(5_3, 0, l1);
            var z4 = new Zone.Meadow(5_4, List.of(), null);
            var z5 = new Zone.River(5_5, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z3, z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(5, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 6
            var l1 = new Zone.Lake(6_8, 1, null);
            var z0 = new Zone.Meadow(6_0, List.of(), null);
            var z1 = new Zone.River(6_1, 0, l1);
            var z2 = new Zone.Meadow(6_2, List.of(), null);
            var z3 = new Zone.River(6_3, 0, l1);
            var a4_0 = new Animal(6_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(6_4, List.of(a4_0), null);
            var z5 = new Zone.River(6_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(6, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 7
            var l1 = new Zone.Lake(7_8, 1, null);
            var a0_0 = new Animal(7_0_0, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(7_0, List.of(a0_0), null);
            var z1 = new Zone.River(7_1, 0, l1);
            var z2 = new Zone.Meadow(7_2, List.of(), null);
            var z3 = new Zone.River(7_3, 0, l1);
            var z4 = new Zone.Meadow(7_4, List.of(), null);
            var z5 = new Zone.River(7_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(7, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 8
            var l1 = new Zone.Lake(8_8, 1, null);
            var a0_0 = new Animal(8_0_0, Animal.Kind.MAMMOTH);
            var z0 = new Zone.Meadow(8_0, List.of(a0_0), null);
            var z1 = new Zone.River(8_1, 0, l1);
            var a2_0 = new Animal(8_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(8_2, List.of(a2_0), null);
            var z3 = new Zone.River(8_3, 0, l1);
            var z4 = new Zone.Meadow(8_4, List.of(), null);
            var z5 = new Zone.River(8_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(8, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 9
            var l1 = new Zone.Lake(9_8, 2, null);
            var z0 = new Zone.Meadow(9_0, List.of(), null);
            var z1 = new Zone.River(9_1, 0, l1);
            var z2 = new Zone.Meadow(9_2, List.of(), null);
            var z3 = new Zone.River(9_3, 0, l1);
            var z4 = new Zone.Meadow(9_4, List.of(), null);
            var z5 = new Zone.River(9_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(9, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 10
            var l1 = new Zone.Lake(10_8, 1, null);
            var z0 = new Zone.Meadow(10_0, List.of(), null);
            var z1 = new Zone.River(10_1, 0, l1);
            var z2 = new Zone.Meadow(10_2, List.of(), null);
            var z3 = new Zone.River(10_3, 0, l1);
            var a4_0 = new Animal(10_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(10_4, List.of(a4_0), null);
            var z5 = new Zone.River(10_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(10, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 11
            var l1 = new Zone.Lake(11_8, 1, null);
            var z0 = new Zone.Meadow(11_0, List.of(), null);
            var z1 = new Zone.River(11_1, 0, l1);
            var z2 = new Zone.Meadow(11_2, List.of(), null);
            var z3 = new Zone.River(11_3, 0, l1);
            var z4 = new Zone.Meadow(11_4, List.of(), null);
            var z5 = new Zone.Forest(11_5, Zone.Forest.Kind.PLAIN);
            var z6 = new Zone.Meadow(11_6, List.of(), null);
            var z7 = new Zone.River(11_7, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Forest(z5);
            var sW = new TileSide.River(z6, z7, z0);
            tiles.add(new Tile(11, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 12
            var l1 = new Zone.Lake(12_8, 1, null);
            var z0 = new Zone.Meadow(12_0, List.of(), null);
            var z1 = new Zone.River(12_1, 0, l1);
            var z2 = new Zone.Meadow(12_2, List.of(), null);
            var z3 = new Zone.River(12_3, 0, l1);
            var a4_0 = new Animal(12_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(12_4, List.of(a4_0), null);
            var z5 = new Zone.River(12_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.River(z2, z3, z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(12, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 13
            var l1 = new Zone.Lake(13_8, 2, null);
            var a0_0 = new Animal(13_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(13_0, List.of(a0_0), null);
            var z1 = new Zone.River(13_1, 0, l1);
            var z2 = new Zone.Meadow(13_2, List.of(), null);
            var z3 = new Zone.River(13_3, 0, l1);
            var z4 = new Zone.Meadow(13_4, List.of(), null);
            var z5 = new Zone.River(13_5, 0, l1);
            var a6_0 = new Animal(13_6_0, Animal.Kind.DEER);
            var z6 = new Zone.Meadow(13_6, List.of(a6_0), null);
            var z7 = new Zone.River(13_7, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z6);
            var sW = new TileSide.River(z6, z7, z0);
            tiles.add(new Tile(13, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 14
            var z0 = new Zone.Meadow(14_0, List.of(), null);
            var z1 = new Zone.River(14_1, 0, null);
            var a2_0 = new Animal(14_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(14_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(14_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(14_4, Zone.Forest.Kind.PLAIN);
            var z5 = new Zone.Forest(14_5, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.Forest(z4);
            var sW = new TileSide.Forest(z5);
            tiles.add(new Tile(14, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 15
            var a0_0 = new Animal(15_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(15_0, List.of(a0_0), null);
            var z1 = new Zone.River(15_1, 0, null);
            var z2 = new Zone.Meadow(15_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(15, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 16
            var z0 = new Zone.Meadow(16_0, List.of(), null);
            var z1 = new Zone.River(16_1, 0, null);
            var a2_0 = new Animal(16_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(16_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(16, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 17
            var z0 = new Zone.Meadow(17_0, List.of(), null);
            var z1 = new Zone.River(17_1, 0, null);
            var a2_0 = new Animal(17_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(17_2, List.of(a2_0), null);
            var z3 = new Zone.River(17_3, 0, null);
            var a4_0 = new Animal(17_4_0, Animal.Kind.TIGER);
            var z4 = new Zone.Meadow(17_4, List.of(a4_0), null);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.River(z0, z3, z4);
            var sW = new TileSide.River(z4, z3, z0);
            tiles.add(new Tile(17, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 18
            var a0_0 = new Animal(18_0_0, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(18_0, List.of(a0_0), null);
            var z1 = new Zone.River(18_1, 0, null);
            var z2 = new Zone.Meadow(18_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(18, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 19
            var a0_0 = new Animal(19_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(19_0, List.of(a0_0), null);
            var z1 = new Zone.River(19_1, 1, null);
            var z2 = new Zone.Meadow(19_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(19, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 20
            var a0_0 = new Animal(20_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(20_0, List.of(a0_0), null);
            var z1 = new Zone.River(20_1, 2, null);
            var z2 = new Zone.Meadow(20_2, List.of(), null);
            var z3 = new Zone.Meadow(20_3, List.of(), null);
            var z4 = new Zone.Forest(20_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z3);
            var sS = new TileSide.Forest(z4);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(20, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 21
            var z0 = new Zone.Meadow(21_0, List.of(), null);
            var z1 = new Zone.River(21_1, 0, null);
            var a2_0 = new Animal(21_2_0, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(21_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(21, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 22
            var z0 = new Zone.Meadow(22_0, List.of(), null);
            var z1 = new Zone.River(22_1, 0, null);
            var a2_0 = new Animal(22_2_0, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(22_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(22_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(22, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 23
            var a0_0 = new Animal(23_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(23_0, List.of(a0_0), null);
            var z1 = new Zone.River(23_1, 0, null);
            var z2 = new Zone.Meadow(23_2, List.of(), null);
            var z3 = new Zone.Forest(23_3, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(23, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 24
            var l1 = new Zone.Lake(24_8, 1, null);
            var z0 = new Zone.Meadow(24_0, List.of(), null);
            var z1 = new Zone.River(24_1, 0, l1);
            var a2_0 = new Animal(24_2_0, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(24_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(24_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(24_4, List.of(), null);
            var z5 = new Zone.River(24_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(24, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 25
            var z0 = new Zone.Meadow(25_0, List.of(), null);
            var z1 = new Zone.River(25_1, 0, null);
            var a2_0 = new Animal(25_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(25_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(25_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(25, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 26
            var z0 = new Zone.Meadow(26_0, List.of(), null);
            var z1 = new Zone.River(26_1, 0, null);
            var z2 = new Zone.Meadow(26_2, List.of(), null);
            var z3 = new Zone.Forest(26_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(26_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(26, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 27
            var z0 = new Zone.Meadow(27_0, List.of(), null);
            var z1 = new Zone.River(27_1, 0, null);
            var a2_0 = new Animal(27_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(27_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(27_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(27, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 28
            var z0 = new Zone.Meadow(28_0, List.of(), null);
            var z1 = new Zone.River(28_1, 1, null);
            var z2 = new Zone.Meadow(28_2, List.of(), null);
            var z3 = new Zone.Forest(28_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(28, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 29
            var z0 = new Zone.Forest(29_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(29_1, List.of(), null);
            var z2 = new Zone.River(29_2, 0, null);
            var z3 = new Zone.Meadow(29_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.River(z1, z2, z3);
            var sS = new TileSide.River(z3, z2, z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(29, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 30
            var a0_0 = new Animal(30_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(30_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(30_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(30, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 31
            var z0 = new Zone.Forest(31_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(31_1_0, Animal.Kind.TIGER);
            var z1 = new Zone.Meadow(31_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(31, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 32
            var z0 = new Zone.Forest(32_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(32_1_0, Animal.Kind.TIGER);
            var z1 = new Zone.Meadow(32_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(32, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 33
            var z0 = new Zone.Forest(33_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(33_1, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(33, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 34
            var z0 = new Zone.Meadow(34_0, List.of(), null);
            var z1 = new Zone.Meadow(34_1, List.of(), null);
            var a2_0 = new Animal(34_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(34_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(34_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(34, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 35
            var a0_0 = new Animal(35_0_0, Animal.Kind.MAMMOTH);
            var z0 = new Zone.Meadow(35_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(35_1, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(35, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 36
            var a0_0 = new Animal(36_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(36_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(36_1, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(36, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 37
            var z0 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
            var a1_0 = new Animal(37_1_0, Animal.Kind.DEER);
            var z1 = new Zone.Meadow(37_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(37, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 38
            var a0_0 = new Animal(38_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(38_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(38_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(38_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(38, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 39
            var z0 = new Zone.Meadow(39_0, List.of(), null);
            var z1 = new Zone.Forest(39_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(39_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(39, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 40
            var z0 = new Zone.Meadow(40_0, List.of(), null);
            var z1 = new Zone.Forest(40_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(40_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(40, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 41
            var a0_0 = new Animal(41_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(41_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(41_1, Zone.Forest.Kind.PLAIN);
            var z2 = new Zone.Forest(41_2, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Forest(z2);
            tiles.add(new Tile(41, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 42
            var z0 = new Zone.Forest(42_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(42_1, List.of(), null);
            var z2 = new Zone.Meadow(42_2, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z2);
            tiles.add(new Tile(42, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 43
            var z0 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
            var z2 = new Zone.Meadow(43_2, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z2);
            tiles.add(new Tile(43, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 44
            var z0 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
            var a2_0 = new Animal(44_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(44_2, List.of(a2_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(44, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 45
            var z0 = new Zone.Meadow(45_0, List.of(), null);
            var z1 = new Zone.River(45_1, 1, null);
            var z2 = new Zone.Meadow(45_2, List.of(), null);
            var z3 = new Zone.Forest(45_3, Zone.Forest.Kind.PLAIN);
            var a4_0 = new Animal(45_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(45_4, List.of(a4_0), null);
            var z5 = new Zone.Meadow(45_5, List.of(), null);
            var z6 = new Zone.Forest(45_6, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z4, z1, z5);
            var sW = new TileSide.Forest(z6);
            tiles.add(new Tile(45, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 46
            var z0 = new Zone.Meadow(46_0, List.of(), null);
            var z1 = new Zone.River(46_1, 0, null);
            var z2 = new Zone.Meadow(46_2, List.of(), null);
            var z3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(46, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 47
            var z0 = new Zone.Meadow(47_0, List.of(), null);
            var z1 = new Zone.River(47_1, 1, null);
            var a2_0 = new Animal(47_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(47_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(47, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 48
            var z0 = new Zone.Meadow(48_0, List.of(), null);
            var z1 = new Zone.River(48_1, 0, null);
            var a2_0 = new Animal(48_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(48_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(48, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 49
            var a0_0 = new Animal(49_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(49_0, List.of(a0_0), null);
            var z1 = new Zone.River(49_1, 0, null);
            var a2_0 = new Animal(49_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(49_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(49, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 50
            var a0_0 = new Animal(50_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(50_0, List.of(a0_0), null);
            var z1 = new Zone.River(50_1, 0, null);
            var z2 = new Zone.Meadow(50_2, List.of(), null);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(50, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 51
            var a0_0 = new Animal(51_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(51_0, List.of(a0_0), null);
            var z1 = new Zone.River(51_1, 0, null);
            var z2 = new Zone.Meadow(51_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(51, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 52
            var a0_0 = new Animal(52_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(52_0, List.of(a0_0), null);
            var z1 = new Zone.River(52_1, 0, null);
            var z2 = new Zone.Meadow(52_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(52, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 53
            var z0 = new Zone.Meadow(53_0, List.of(), null);
            var z1 = new Zone.River(53_1, 0, null);
            var a2_0 = new Animal(53_2_0, Animal.Kind.MAMMOTH);
            var z2 = new Zone.Meadow(53_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(53, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 54
            var z0 = new Zone.Forest(54_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(54_1, List.of(), null);
            var z2 = new Zone.River(54_2, 0, null);
            var a3_0 = new Animal(54_3_0, Animal.Kind.DEER);
            var z3 = new Zone.Meadow(54_3, List.of(a3_0), null);
            var z4 = new Zone.Meadow(54_4, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.River(z1, z2, z3);
            var sS = new TileSide.River(z3, z2, z4);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(54, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 55
            var z0 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
            var z2 = new Zone.Meadow(55_2, List.of(), null);
            var z3 = new Zone.River(55_3, 0, null);
            var z4 = new Zone.Meadow(55_4, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.River(z2, z3, z4);
            tiles.add(new Tile(55, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 56
            var l1 = new Zone.Lake(56_8, 1, null);
            var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(56_2, List.of(), null);
            var z3 = new Zone.River(56_3, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.River(z2, z3, z0);
            tiles.add(new Tile(56, Tile.Kind.START, sN, sE, sS, sW));
        }
        {   // Tile 57
            var l1 = new Zone.Lake(57_8, 2, null);
            var z0 = new Zone.Meadow(57_0, List.of(), null);
            var z1 = new Zone.River(57_1, 0, l1);
            var z2 = new Zone.Meadow(57_2, List.of(), null);
            var z3 = new Zone.Forest(57_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(57_4, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.Forest(z4);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(57, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 58
            var l1 = new Zone.Lake(58_8, 1, null);
            var z0 = new Zone.Forest(58_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(58_1, List.of(), null);
            var z2 = new Zone.River(58_2, 0, l1);
            var z3 = new Zone.Meadow(58_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.River(z1, z2, z3);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(58, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 59
            var l1 = new Zone.Lake(59_8, 1, null);
            var z0 = new Zone.Meadow(59_0, List.of(), null);
            var z1 = new Zone.River(59_1, 0, l1);
            var a2_0 = new Animal(59_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(59_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(59_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(59, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 60
            var z0 = new Zone.Meadow(60_0, List.of(), null);
            var z1 = new Zone.Forest(60_1, Zone.Forest.Kind.WITH_MENHIR);
            var a2_0 = new Animal(60_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(60_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(60, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 61
            var a0_0 = new Animal(61_0_0, Animal.Kind.MAMMOTH);
            var z0 = new Zone.Meadow(61_0, List.of(a0_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(61, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 62
            var a0_0 = new Animal(62_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(62_0, List.of(a0_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(62, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 63
            var z0 = new Zone.Forest(63_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(63_1, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(63, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 64
            var z0 = new Zone.Forest(64_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(64_1, List.of(), null);
            var z2 = new Zone.Forest(64_2, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z2);
            tiles.add(new Tile(64, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 65
            var z0 = new Zone.Forest(65_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(65_1_0, Animal.Kind.TIGER);
            var z1 = new Zone.Meadow(65_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(65, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 66
            var z0 = new Zone.Forest(66_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(66_1, List.of(), null);
            var z2 = new Zone.River(66_2, 0, null);
            var z3 = new Zone.Meadow(66_3, List.of(), null);
            var z4 = new Zone.Meadow(66_4, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.River(z1, z2, z3);
            var sW = new TileSide.Meadow(z4);
            tiles.add(new Tile(66, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 67
            var z0 = new Zone.Forest(67_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Forest(67_1, Zone.Forest.Kind.PLAIN);
            var a2_0 = new Animal(67_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(67_2, List.of(a2_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Meadow(z2);
            tiles.add(new Tile(67, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 68
            var z0 = new Zone.Forest(68_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Meadow(68_1, List.of(), null);
            var z2 = new Zone.River(68_2, 0, null);
            var z3 = new Zone.Meadow(68_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.River(z1, z2, z3);
            var sW = new TileSide.Meadow(z3);
            tiles.add(new Tile(68, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 69
            var z0 = new Zone.Forest(69_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(69_1, List.of(), null);
            var z2 = new Zone.River(69_2, 0, null);
            var z3 = new Zone.Meadow(69_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.River(z1, z2, z3);
            var sS = new TileSide.River(z3, z2, z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(69, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 70
            var z0 = new Zone.Meadow(70_0, List.of(), null);
            var z1 = new Zone.River(70_1, 0, null);
            var z2 = new Zone.Meadow(70_2, List.of(), null);
            var z3 = new Zone.River(70_3, 0, null);
            var z4 = new Zone.Meadow(70_4, List.of(), null);
            var z5 = new Zone.Forest(70_5, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Forest(z5);
            var sW = new TileSide.Forest(z5);
            tiles.add(new Tile(70, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 71
            var l1 = new Zone.Lake(71_8, 2, null);
            var z0 = new Zone.Meadow(71_0, List.of(), null);
            var z1 = new Zone.River(71_1, 0, null);
            var z2 = new Zone.Meadow(71_2, List.of(), null);
            var z3 = new Zone.Forest(71_3, Zone.Forest.Kind.WITH_MENHIR);
            var z4 = new Zone.Meadow(71_4, List.of(), null);
            var z5 = new Zone.River(71_5, 0, l1);
            var z6 = new Zone.Meadow(71_6, List.of(), null);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z4, z5, z6);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(71, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 72
            var a0_0 = new Animal(72_0_0, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(72_0, List.of(a0_0), null);
            var z1 = new Zone.River(72_1, 0, null);
            var z2 = new Zone.Meadow(72_2, List.of(), null);
            var z3 = new Zone.Forest(72_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(72_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(72, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 73
            var z0 = new Zone.Meadow(73_0, List.of(), null);
            var z1 = new Zone.River(73_1, 0, null);
            var a2_0 = new Animal(73_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(73_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(73_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(73_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(73, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 74
            var z0 = new Zone.Meadow(74_0, List.of(), null);
            var z1 = new Zone.River(74_1, 1, null);
            var z2 = new Zone.Meadow(74_2, List.of(), null);
            var z3 = new Zone.Forest(74_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(74_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(74, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 75
            var z0 = new Zone.Meadow(75_0, List.of(), null);
            var z1 = new Zone.Forest(75_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(75, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 76
            var a0_0 = new Animal(76_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(76_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(76_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(76, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 77
            var z0 = new Zone.Meadow(77_0, List.of(), null);
            var z1 = new Zone.Forest(77_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(77, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 78
            var z0 = new Zone.Forest(78_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(78_1_0, Animal.Kind.DEER);
            var z1 = new Zone.Meadow(78_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(78, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 79
            var z0 = new Zone.Meadow(79_0, List.of(), null);
            var z1 = new Zone.Forest(79_1, Zone.Forest.Kind.WITH_MUSHROOMS);
            var z2 = new Zone.Meadow(79_2, List.of(), null);
            var z3 = new Zone.River(79_3, 0, null);
            var z4 = new Zone.Meadow(79_4, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.River(z2, z3, z4);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(79, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 80
            var l1 = new Zone.Lake(80_8, 1, null);
            var z0 = new Zone.Meadow(80_0, List.of(), null);
            var z1 = new Zone.River(80_1, 0, l1);
            var a2_0 = new Animal(80_2_0, Animal.Kind.MAMMOTH);
            var a2_1 = new Animal(80_2_1, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(80_2, List.of(a2_0, a2_1), null);
            var z3 = new Zone.River(80_3, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z3, z0);
            tiles.add(new Tile(80, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 81
            var a0_0 = new Animal(81_0_0, Animal.Kind.MAMMOTH);
            var a0_1 = new Animal(81_0_1, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(81_0, List.of(a0_0, a0_1), null);
            var z1 = new Zone.Forest(81_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(81, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 82
            var l1 = new Zone.Lake(82_8, 2, null);
            var z0 = new Zone.Meadow(82_0, List.of(), null);
            var z1 = new Zone.River(82_1, 0, l1);
            var a2_0 = new Animal(82_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(82_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(82_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(82, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 83
            var l1 = new Zone.Lake(83_8, 2, null);
            var l2 = new Zone.Lake(83_9, 2, null);
            var a0_0 = new Animal(83_0_0, Animal.Kind.DEER);
            var a0_1 = new Animal(83_0_1, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(83_0, List.of(a0_0, a0_1), null);
            var z1 = new Zone.River(83_1, 0, l1);
            var z2 = new Zone.Meadow(83_2, List.of(), null);
            var z3 = new Zone.River(83_3, 0, l1);
            var z4 = new Zone.River(83_4, 0, l2);
            var z5 = new Zone.Meadow(83_5, List.of(), null);
            var z6 = new Zone.River(83_6, 0, l2);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z0);
            var sS = new TileSide.River(z0, z4, z5);
            var sW = new TileSide.River(z5, z6, z0);
            tiles.add(new Tile(83, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 84
            var z0 = new Zone.Meadow(84_0, List.of(), null);
            var z1 = new Zone.River(84_1, 0, null);
            var a2_0 = new Animal(84_2_0, Animal.Kind.MAMMOTH);
            var z2 = new Zone.Meadow(84_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(84_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(84, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 85
            var z0 = new Zone.Meadow(85_0, List.of(), Zone.SpecialPower.WILD_FIRE);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(85, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 86
            var l1 = new Zone.Lake(86_8, 1, null);
            var a0_0 = new Animal(86_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(86_0, List.of(a0_0), null);
            var z1 = new Zone.River(86_1, 0, l1);
            var z2 = new Zone.Meadow(86_2, List.of(), null);
            var z3 = new Zone.River(86_3, 0, l1);
            var a4_0 = new Animal(86_4_0, Animal.Kind.TIGER);
            var z4 = new Zone.Meadow(86_4, List.of(a4_0), null);
            var z5 = new Zone.River(86_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(86, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 87
            var l1 = new Zone.Lake(87_8, 2, null);
            var z0 = new Zone.Meadow(87_0, List.of(), null);
            var z1 = new Zone.Forest(87_1, Zone.Forest.Kind.WITH_MUSHROOMS);
            var z2 = new Zone.Meadow(87_2, List.of(), null);
            var z3 = new Zone.River(87_3, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.River(z2, z3, z0);
            tiles.add(new Tile(87, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 88
            var z0 = new Zone.Meadow(88_0, List.of(), Zone.SpecialPower.SHAMAN);
            var z1 = new Zone.River(88_1, 1, null);
            var z2 = new Zone.Meadow(88_2, List.of(), null);
            var z3 = new Zone.Forest(88_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(88, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 89
            var l1 = new Zone.Lake(89_8, 3, null);
            var z0 = new Zone.Meadow(89_0, List.of(), null);
            var z1 = new Zone.River(89_1, 0, l1);
            var z2 = new Zone.Meadow(89_2, List.of(), null);
            var z3 = new Zone.Forest(89_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(89_4, List.of(), null);
            var z5 = new Zone.River(89_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(89, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 90
            var a0_0 = new Animal(90_0_0, Animal.Kind.MAMMOTH);
            var a0_1 = new Animal(90_0_1, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(90_0, List.of(a0_0, a0_1), null);
            var z1 = new Zone.Forest(90_1, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(90, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 91
            var l1 = new Zone.Lake(91_8, 1, Zone.SpecialPower.RAFT);
            var z0 = new Zone.Meadow(91_0, List.of(), null);
            var z1 = new Zone.River(91_1, 0, l1);
            var z2 = new Zone.Meadow(91_2, List.of(), null);
            var z3 = new Zone.River(91_3, 0, l1);
            var z4 = new Zone.Meadow(91_4, List.of(), null);
            var z5 = new Zone.River(91_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(91, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 92
            var z0 = new Zone.Forest(92_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Meadow(92_1, List.of(), Zone.SpecialPower.PIT_TRAP);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(92, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 93
            var l1 = new Zone.Lake(93_8, 1, Zone.SpecialPower.LOGBOAT);
            var z0 = new Zone.Meadow(93_0, List.of(), null);
            var z1 = new Zone.River(93_1, 0, l1);
            var z2 = new Zone.Meadow(93_2, List.of(), null);
            var z3 = new Zone.River(93_3, 0, l1);
            var z4 = new Zone.Meadow(93_4, List.of(), null);
            var z5 = new Zone.River(93_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(93, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 94
            var z0 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(94, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        tiles.trimToSize();
        return Collections.unmodifiableList(tiles);
    }
    //</editor-fold>
}
