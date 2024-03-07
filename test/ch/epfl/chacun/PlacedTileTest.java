package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlacedTileTest {

    @Test
    void placedTileConstructorThrowsOnNullTile() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(null, null, Rotation.NONE, Pos.ORIGIN));
    }

    @Test
    void placedTileConstructorThrowsOnNullRotation() {
        Tile tile = new Tile(0, null, null, null, null, null);
        assertThrows(NullPointerException.class, () -> new PlacedTile(tile, null, null, Pos.ORIGIN));
    }

    @Test
    void placedTileConstructorThrowsOnNullPos() {
        Tile tile = new Tile(0, null, null, null, null, null);
        assertThrows(NullPointerException.class, () -> new PlacedTile(tile, null, Rotation.NONE, null));
    }

    @Test
    void placedTileConstructorDoesntThrowOnNullPlacer() {
        Tile tile = new Tile(0, null, null, null, null, null);
        assertDoesNotThrow(() -> new PlacedTile(tile, null, Rotation.NONE, Pos.ORIGIN));
    }

    @Test
    void placedTileSideWorks() {
        TileSide north = new TileSide.River(null, null, null);
        TileSide east = new TileSide.Forest(new Zone.Forest(0, Zone.Forest.Kind.PLAIN));
        TileSide south = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR));
        TileSide west = new TileSide.Meadow(null);

        List<TileSide> ALL = List.of(north, east, south, west);
        Tile tile = new Tile(0, null, north, east, south, west);
        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.ALL.get(i);
            for (int j = 0; j < 4; j++) {
                Rotation rotation = Rotation.ALL.get(j);
                PlacedTile placedTile = new PlacedTile(tile, null, rotation, Pos.ORIGIN);
                assertEquals(ALL.get(direction.rotated(rotation.negated()).ordinal()), placedTile.side(direction));
            }
        }
    }

    @Test
    void zoneWithIdWorks() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Meadow meadow1 = new Zone.Meadow(2, List.of(new Animal(2, Animal.Kind.AUROCHS)), Zone.Meadow.SpecialPower.HUNTING_TRAP);
        Zone.River river = new Zone.River(3, 9, null);
        Zone.Meadow meadow2 = new Zone.Meadow(4, List.of(new Animal(2, Animal.Kind.AUROCHS)), Zone.Meadow.SpecialPower.HUNTING_TRAP);

        TileSide north = new TileSide.Meadow(meadow1);
        TileSide east = new TileSide.Forest(forest1);
        TileSide south = new TileSide.River(meadow1, river, meadow2);
        TileSide west = new TileSide.Forest(forest2);
        Tile tile = new Tile(0, null, north, east, south, west);
        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, Pos.ORIGIN);

        assertEquals(forest1, placedTile.zoneWithId(0));
        assertEquals(forest2, placedTile.zoneWithId(1));
        assertEquals(meadow1, placedTile.zoneWithId(2));
        assertEquals(river, placedTile.zoneWithId(3));
        assertEquals(meadow2, placedTile.zoneWithId(4));
    }

    @Test
    void zoneWithIdThrowsOnUnknownId() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Meadow meadow1 = new Zone.Meadow(2, List.of(new Animal(2, Animal.Kind.AUROCHS)), Zone.Meadow.SpecialPower.HUNTING_TRAP);
        Zone.River river = new Zone.River(3, 9, null);
        Zone.Meadow meadow2 = new Zone.Meadow(4, List.of(new Animal(2, Animal.Kind.AUROCHS)), Zone.Meadow.SpecialPower.HUNTING_TRAP);

        TileSide north = new TileSide.Meadow(meadow1);
        TileSide east = new TileSide.Forest(forest1);
        TileSide south = new TileSide.River(meadow1, river, meadow2);
        TileSide west = new TileSide.Forest(forest2);
        Tile tile = new Tile(0, null, north, east, south, west);
        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, Pos.ORIGIN);

        assertThrows(IllegalArgumentException.class, () -> placedTile.zoneWithId(10));
    }

    @Test
    public void testPotentialOccupantsReturnsCorrectValue() {
        Zone.Meadow meadow = new Zone.Meadow(613, List.of(new Animal(6131, Animal.Kind.AUROCHS)), Zone.Meadow.SpecialPower.HUNTING_TRAP);
        Zone.Meadow meadow2 = new Zone.Meadow(614, List.of(new Animal(6141, Animal.Kind.MAMMOTH)), null);
        Zone.Forest forest2 = new Zone.Forest(615, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest = new Zone.Forest(612, Zone.Forest.Kind.WITH_MENHIR);
        TileSide forestSide = new TileSide.Forest(forest);
        TileSide meadowSide = new TileSide.Meadow(meadow);
        TileSide forestSide2 = new TileSide.Forest(forest2);
        TileSide meadowSide2 = new TileSide.Meadow(meadow2);
        Tile tile = new Tile(1, Tile.Kind.NORMAL, forestSide, meadowSide, forestSide2, meadowSide2);
        PlayerColor Habib = PlayerColor.RED;

        PlacedTile placedTile = new PlacedTile(tile, Habib, Rotation.RIGHT, new Pos(0, 0));

        Set<Occupant> set = new HashSet<>();
        set.add(new Occupant(Occupant.Kind.PAWN, 613));
        set.add(new Occupant(Occupant.Kind.PAWN, 614));
        set.add(new Occupant(Occupant.Kind.PAWN, 615));
        set.add(new Occupant(Occupant.Kind.PAWN, 612));

        assertEquals(set, placedTile.potentialOccupants());

        PlacedTile placedTile2 = new PlacedTile(tile, null, Rotation.RIGHT, new Pos(0, 0));

        assertEquals(new HashSet<>(), placedTile2.potentialOccupants());

        Zone.River river = new Zone.River(623, 3, null);
        Zone.Lake lake = new Zone.Lake(628, 0, Zone.SpecialPower.LOGBOAT);
        Zone.River river2 = new Zone.River(624, 2, lake);

        TileSide riverSide1 = new TileSide.River(meadow, river, meadow2);
        TileSide riverSide2 = new TileSide.River(meadow2, river2, meadow);

        Tile tile2 = new Tile(1, Tile.Kind.NORMAL, forestSide, riverSide1, riverSide2, meadowSide2);
        PlacedTile placedTile3 = new PlacedTile(tile2, Habib, Rotation.RIGHT, new Pos(0, 0));

        Set<Occupant> set2 = new HashSet<>();
        set2.add(new Occupant(Occupant.Kind.PAWN, 623));
        set2.add(new Occupant(Occupant.Kind.PAWN, 624));
        set2.add(new Occupant(Occupant.Kind.HUT, 628));
        set2.add(new Occupant(Occupant.Kind.HUT, 623));

        set2.add(new Occupant(Occupant.Kind.PAWN, 613));
        set2.add(new Occupant(Occupant.Kind.PAWN, 614));
        set2.add(new Occupant(Occupant.Kind.PAWN, 612));

        assertEquals(set2, placedTile3.potentialOccupants());
    }

    @Test
    public void testWithOccupantWorks() {
        Zone.Meadow meadow = new Zone.Meadow(613, List.of(new Animal(6131, Animal.Kind.AUROCHS)), Zone.Meadow.SpecialPower.HUNTING_TRAP);
        Zone.Meadow meadow2 = new Zone.Meadow(614, List.of(new Animal(6141, Animal.Kind.MAMMOTH)), null);
        Zone.Forest forest2 = new Zone.Forest(615, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest = new Zone.Forest(612, Zone.Forest.Kind.WITH_MENHIR);
        TileSide forestSide = new TileSide.Forest(forest);
        TileSide meadowSide = new TileSide.Meadow(meadow);
        TileSide forestSide2 = new TileSide.Forest(forest2);
        TileSide meadowSide2 = new TileSide.Meadow(meadow2);
        Tile tile = new Tile(1, Tile.Kind.NORMAL, forestSide, meadowSide, forestSide2, meadowSide2);
        PlayerColor Habib = PlayerColor.RED;

        PlacedTile placedTile = new PlacedTile(tile, Habib, Rotation.RIGHT, new Pos(0, 0));

        Occupant occupant = new Occupant(Occupant.Kind.PAWN, 613);
        assertThrows(IllegalArgumentException.class, () -> {
            PlacedTile withOccupant = placedTile.withOccupant(occupant);
            withOccupant.withOccupant(new Occupant(Occupant.Kind.PAWN, 613));
        });

        PlacedTile withOccupant = placedTile.withOccupant(new Occupant(Occupant.Kind.PAWN, 613));
        assertEquals(occupant, withOccupant.occupant());

        assertEquals(613, withOccupant.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        assertEquals(-1, withOccupant.idOfZoneOccupiedBy(Occupant.Kind.HUT));

    }

}
