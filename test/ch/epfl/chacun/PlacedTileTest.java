package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

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
    void placedTileContructorDoesntThrowOnNullPlacer() {}

}
