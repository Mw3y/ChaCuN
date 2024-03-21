package ch.epfl.chacun;

import ch.epfl.chacun.tile.Tiles;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    PlacedTile originTile = new PlacedTile(Tiles.TILES.get(56), null, Rotation.NONE, Pos.ORIGIN);
    PlacedTile secondTile = new PlacedTile(Tiles.TILES.get(58), PlayerColor.YELLOW, Rotation.NONE, Pos.ORIGIN.neighbor(Direction.S));
    PlacedTile thirdTile = new PlacedTile(Tiles.TILES.get(62), PlayerColor.RED, Rotation.NONE, Pos.ORIGIN.neighbor(Direction.N));
    Board dummyBoard = Board.EMPTY.withNewTile(originTile).withNewTile(secondTile).withNewTile(thirdTile);

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
        assertEquals(thirdTile, dummyBoard.tileWithId(thirdTile.id()));
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
        Board board = dummyBoard.withOccupant(new Occupant(Occupant.Kind.PAWN, 620));
        assertEquals(1, board.occupants().size());
        Board boardWithTwoOccupants = board.withOccupant(new Occupant(Occupant.Kind.HUT, 588));
        assertEquals(2, boardWithTwoOccupants.occupants().size());
    }

    @Test
    void forestArea() {
        ZonePartitions.Builder zonePartitionsBuilder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        zonePartitionsBuilder.addTile(Tiles.TILES.get(56));
        zonePartitionsBuilder.addTile(Tiles.TILES.get(58));
        zonePartitionsBuilder.addTile(Tiles.TILES.get(62));
        zonePartitionsBuilder.connectSides(originTile.side(Direction.S), secondTile.side(Direction.N));
        ZonePartitions zonePartitions = zonePartitionsBuilder.build();
        Zone.Forest forestZone = new Zone.Forest(580, Zone.Forest.Kind.WITH_MENHIR);
        assertEquals(zonePartitions.forests().areaContaining(forestZone), dummyBoard.forestArea(forestZone));
    }

}
