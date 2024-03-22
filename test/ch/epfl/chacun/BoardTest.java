package ch.epfl.chacun;

import ch.epfl.chacun.tile.Tiles;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    PlacedTile originTile = new PlacedTile(Tiles.TILES.get(56), null, Rotation.NONE, Pos.ORIGIN);
    PlacedTile secondTile = new PlacedTile(Tiles.TILES.get(58), PlayerColor.RED, Rotation.NONE, Pos.ORIGIN.neighbor(Direction.S));
    PlacedTile thirdTile = new PlacedTile(Tiles.TILES.get(62), PlayerColor.RED, Rotation.NONE, Pos.ORIGIN.neighbor(Direction.N));
    PlacedTile fourthTile = new PlacedTile(Tiles.TILES.get(20), PlayerColor.GREEN, Rotation.NONE, thirdTile.pos().neighbor(Direction.E));
    PlacedTile fifthTile = new PlacedTile(Tiles.TILES.get(68), PlayerColor.BLUE, Rotation.NONE, fourthTile.pos().neighbor(Direction.N));

    Board dummyBoard = Board.EMPTY
            .withNewTile(originTile)
            .withNewTile(secondTile)
            .withNewTile(thirdTile)
            .withNewTile(fourthTile)
            .withNewTile(fifthTile);

    // TODO: What happens if withOccupant is used on the origin tile

    private ZonePartitions getBoardZonePartitions() {
        ZonePartitions.Builder zonePartitionsBuilder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        zonePartitionsBuilder.addTile(originTile.tile());
        zonePartitionsBuilder.addTile(secondTile.tile());
        zonePartitionsBuilder.addTile(thirdTile.tile());
        zonePartitionsBuilder.addTile(fourthTile.tile());
        zonePartitionsBuilder.addTile(fifthTile.tile());

        zonePartitionsBuilder.connectSides(originTile.side(Direction.S), secondTile.side(Direction.N));
        zonePartitionsBuilder.connectSides(originTile.side(Direction.N), thirdTile.side(Direction.S));
        zonePartitionsBuilder.connectSides(thirdTile.side(Direction.E), fourthTile.side(Direction.W));
        zonePartitionsBuilder.connectSides(fourthTile.side(Direction.N), fifthTile.side(Direction.S));
        return zonePartitionsBuilder.build();
    }

    @Test
    void tileAtWorks() {
        assertEquals(originTile, dummyBoard.tileAt(Pos.ORIGIN));
        assertEquals(secondTile, dummyBoard.tileAt(Pos.ORIGIN.neighbor(Direction.S)));
        assertNull(dummyBoard.tileAt(Pos.ORIGIN.neighbor(Direction.E)));
        assertNull(dummyBoard.tileAt(new Pos(-12, -12)));
    }

    @Test
    void tileWithIdWorks() {
        assertEquals(originTile, dummyBoard.tileWithId(originTile.id()));
        assertEquals(secondTile, dummyBoard.tileWithId(secondTile.id()));
        assertEquals(fourthTile, dummyBoard.tileWithId(fourthTile.id()));
        assertThrows(IllegalArgumentException.class, () -> dummyBoard.tileWithId(-1));
    }

    @Test
    void cancelledAnimalsWorks() {
        assertEquals(0, dummyBoard.cancelledAnimals().size());
        Animal mammoth = new Animal(0, Animal.Kind.MAMMOTH);
        Board boardWithCancelledAnimals = dummyBoard.withMoreCancelledAnimals(Set.of(mammoth));
        assertEquals(1, boardWithCancelledAnimals.cancelledAnimals().size());
        assertThrows(UnsupportedOperationException.class, () -> boardWithCancelledAnimals.cancelledAnimals().remove(mammoth));
    }

    @Test
    void occupantsWorks() {
        assertEquals(0, dummyBoard.occupants().size());
        Board boardWithTwoOccupants = dummyBoard.withOccupant(new Occupant(Occupant.Kind.HUT, 588));
        assertEquals(1, boardWithTwoOccupants.occupants().size());
    }

    @Test
    void forestAreaWorks() {
        ZonePartitions zonePartitions = getBoardZonePartitions();
        Zone.Forest forestZone = new Zone.Forest(580, Zone.Forest.Kind.WITH_MENHIR);
        assertEquals(zonePartitions.forests().areaContaining(forestZone), dummyBoard.forestArea(forestZone).withoutOccupants());
        assertThrows(IllegalArgumentException.class, () -> dummyBoard.forestArea(new Zone.Forest(-1, Zone.Forest.Kind.PLAIN)));
    }

    @Test
    void adjacentMeadowWorks() {
        var expectedOccupant = new Occupant(Occupant.Kind.PAWN, 683);
        var board = dummyBoard.withOccupant(expectedOccupant);
        var meadowZone = new Zone.Meadow(560, List.of(new Animal(5600, Animal.Kind.AUROCHS)), null);
        var adjacentMeadow = board.adjacentMeadow(Pos.ORIGIN, meadowZone);
        var adjacentMeadowZones = Set.of(
                meadowZone,
                new Zone.Meadow(620, List.of(new Animal(6200, Animal.Kind.DEER)), null),
                new Zone.Meadow(200, List.of(new Animal(2000, Animal.Kind.AUROCHS)), null)
        );
        var expectedMeadow = new Area<>(adjacentMeadowZones, List.of(PlayerColor.BLUE), 0);
        assertEquals(expectedMeadow, adjacentMeadow);
    }

    @Test
    void occupantCountWorks() {
        var board = dummyBoard.withOccupant(new Occupant(Occupant.Kind.PAWN, 581))
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 204))
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 620))
                .withOccupant(new Occupant(Occupant.Kind.HUT, 682));
        assertEquals(2, board.occupantCount(PlayerColor.RED, Occupant.Kind.PAWN));
        assertEquals(1, board.occupantCount(PlayerColor.GREEN, Occupant.Kind.PAWN));
        assertEquals(0, board.occupantCount(PlayerColor.BLUE, Occupant.Kind.PAWN));
        assertEquals(1, board.occupantCount(PlayerColor.BLUE, Occupant.Kind.HUT));
    }

    @Test
    void insertionPositionsWorks() {
        var expectedInsertionPos = new HashSet<>(Set.of(
                new Pos(-1, 0),
                new Pos(1, 0),
                new Pos(-1, 1),
                new Pos(1, 1),
                new Pos(0, 2),
                new Pos(-1, -1),
                new Pos(0, -2),
                new Pos(2, -1),
                new Pos(2, -2),
                new Pos(1, -3)
        ));
        assertEquals(expectedInsertionPos, dummyBoard.insertionPositions());
    }
    
    @Test
    void forestsClosedByLastTileWorks() {
        assertEquals(Set.of(), Board.EMPTY.forestsClosedByLastTile());
        var nonClosedForestBoard = dummyBoard
                .withNewTile(new PlacedTile(Tiles.TILES.get(57), PlayerColor.YELLOW, Rotation.RIGHT, new Pos(1, 0)));
        assertEquals(Set.of(), nonClosedForestBoard.forestsClosedByLastTile());
        var closedForestBoard = nonClosedForestBoard
                .withNewTile(new PlacedTile(Tiles.TILES.get(76), PlayerColor.RED, Rotation.RIGHT, new Pos(1, 1)))
                .withNewTile(new PlacedTile(Tiles.TILES.get(35), PlayerColor.RED, Rotation.NONE, new Pos(-1, 1)));

        var closedForestAreas = new Area<>(Set.of(
                new Zone.Forest(561, Zone.Forest.Kind.WITH_MENHIR),
                new Zone.Forest(761, Zone.Forest.Kind.WITH_MENHIR),
                new Zone.Forest(574, Zone.Forest.Kind.WITH_MENHIR),
                new Zone.Forest(351, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(580, Zone.Forest.Kind.WITH_MENHIR),
                new Zone.Forest(573, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(204, Zone.Forest.Kind.PLAIN)
        ), List.of(), 0);
        assertEquals(Set.of(closedForestAreas), closedForestBoard.forestsClosedByLastTile());
    }

    @Test
    void riversClosedByLastTileWorks() {
        assertEquals(Set.of(), Board.EMPTY.forestsClosedByLastTile());
        assertEquals(Set.of(), dummyBoard.riversClosedByLastTile());
        var closedRiverBoard = dummyBoard
                .withNewTile(new PlacedTile(Tiles.TILES.get(57), PlayerColor.YELLOW, Rotation.RIGHT, new Pos(1, 0)))
                .withNewTile(new PlacedTile(Tiles.TILES.get(28), PlayerColor.RED, Rotation.NONE, new Pos(2, 0)))
                .withNewTile(new PlacedTile(Tiles.TILES.get(16), PlayerColor.GREEN, Rotation.RIGHT, new Pos(2, -1)));

        var closedRiverAreas = new Area<>(Set.of(
                new Zone.River(571, 0, new Zone.Lake(578, 2, null)),
                new Zone.River(201, 2, null),
                new Zone.River(161, 0, null),
                new Zone.River(682, 0, null),
                new Zone.River(281, 1, null)
        ), List.of(), 0);

        assertEquals(Set.of(closedRiverAreas), closedRiverBoard.riversClosedByLastTile());
    }

    @Test
    void canAddTileWorksWithGoodTile() {
        var board = dummyBoard;
        var placedTile = new PlacedTile(Tiles.TILES.get(48), PlayerColor.RED, Rotation.NONE, fourthTile.pos().neighbor(Direction.E));
        assertTrue(board.canAddTile(placedTile));

    }

    @Test
    void canAddTileWorksWithBadTile() {
        var originTile = new PlacedTile(Tiles.TILES.get(58), null, Rotation.NONE, Pos.ORIGIN);
        var placedTile = new PlacedTile(Tiles.TILES.get(62), PlayerColor.RED, Rotation.NONE, Pos.ORIGIN.neighbor(Direction.N));
        var board = Board.EMPTY.withNewTile(originTile);
        assertFalse(board.canAddTile(placedTile));

    }

    @Test
    void couldPlaceTileWorksWithGoodTile() {
        var board = dummyBoard;
        assertTrue(board.couldPlaceTile(Tiles.TILES.get(94)));
    }

    @Test
    void couldPlaceTileWorksWithBadTile() {
        var originTile = new PlacedTile(Tiles.TILES.get(58), null, Rotation.NONE, Pos.ORIGIN);
        var board = Board.EMPTY.withNewTile(originTile);
        assertFalse(board.couldPlaceTile(Tiles.TILES.get(62)));
    }

    @Test
    void withNewTileThrows() {
        var board = dummyBoard;
        var placedTile = new PlacedTile(Tiles.TILES.get(48), PlayerColor.RED, Rotation.NONE, fourthTile.pos().neighbor(Direction.W));
        assertThrows(IllegalArgumentException.class, () ->  board.withNewTile(placedTile));
    }

    @Test
    void withNewTileWorks() {
        var placedTile = new PlacedTile(Tiles.TILES.get(48), PlayerColor.RED, Rotation.NONE, fourthTile.pos().neighbor(Direction.E));
        var board = dummyBoard.withNewTile(placedTile);
        assertEquals(placedTile, board.tileAt(fourthTile.pos().neighbor(Direction.E)));
    }

    @Test
    void withOccupantWorks() {
        var occupant = new Occupant(Occupant.Kind.PAWN, 204);
        var board = dummyBoard.withOccupant(occupant);
        assertEquals(Set.of(occupant), board.occupants());
    }

    @Test
    void withOutOccupantWorks() {
        var board = dummyBoard;
        var occupant = new Occupant(Occupant.Kind.PAWN, 204);
        var boardWithOccupant = board.withOccupant(occupant);
        assertEquals(board, boardWithOccupant.withoutOccupant(occupant));
    }

    @Test
    void withoutGatherersOrFishersInWorks() {
        var forest = new Zone.Forest(204, Zone.Forest.Kind.PLAIN);
        var board = dummyBoard.withOccupant(new Occupant(Occupant.Kind.PAWN, 204))
                .withOccupant(new Occupant(Occupant.Kind.PAWN, 682));
        var forestArea = new Area<>(Set.of(forest), List.of(PlayerColor.GREEN), 1);
        var riverArea = new Area<>(Set.of(new Zone.River(682, 0, null), new Zone.River(201, 2, null))
                , List.of(PlayerColor.BLUE), 1);
        var clearedBoard = board.withoutGatherersOrFishersIn(Set.of(forestArea), Set.of(riverArea));
        assertEquals(Set.of(), clearedBoard.occupants());
    }

    @Test
    void withMoreCancelledAnimalsWorks() {
        var boardWithMoreAnimals = dummyBoard.withMoreCancelledAnimals(Set.of(new Animal(1, Animal.Kind.MAMMOTH)));
        assertEquals(Set.of(new Animal(1, Animal.Kind.MAMMOTH)), boardWithMoreAnimals.cancelledAnimals());
    }
}