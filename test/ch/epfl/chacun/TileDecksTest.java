package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TileDecksTest {
    @Test
    void deckSizeWorks(){
        List<Tile> startTiles = new ArrayList<>(1);
        List<Tile> normalTiles = new ArrayList<>(1);
        List<Tile> menhirTiles = new ArrayList<>(1);

        startTiles.add(new Tile(0, Tile.Kind.START, null, null, null, null));
        normalTiles.add(new Tile(1, Tile.Kind.NORMAL, null, null, null, null));
        menhirTiles.add(new Tile(2, Tile.Kind.MENHIR, null, null, null, null));

        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        assertEquals(startTiles.size(), tileDecks.deckSize(Tile.Kind.START));
        assertEquals(normalTiles.size(), tileDecks.deckSize(Tile.Kind.NORMAL));
        assertEquals(menhirTiles.size(), tileDecks.deckSize(Tile.Kind.MENHIR));

    }

    @Test
    void topTileReturnsNullOnEmptyDeck(){
        List<Tile> startTiles = new ArrayList<>(1);
        List<Tile> normalTiles = new ArrayList<>(1);
        List<Tile> menhirTiles = new ArrayList<>(1);

        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        assertEquals(null, tileDecks.topTile(Tile.Kind.START));
        assertEquals(null, tileDecks.topTile(Tile.Kind.NORMAL));
        assertEquals(null, tileDecks.topTile(Tile.Kind.MENHIR));
    }

    @Test
    void topTileWorks(){
        List<Tile> startTiles = new ArrayList<>(1);
        List<Tile> normalTiles = new ArrayList<>(1);
        List<Tile> menhirTiles = new ArrayList<>(1);

        startTiles.add(new Tile(0, Tile.Kind.START, null, null, null, null));
        normalTiles.add(new Tile(1, Tile.Kind.NORMAL, null, null, null, null));
        menhirTiles.add(new Tile(2, Tile.Kind.MENHIR, null, null, null, null));

        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        assertEquals(startTiles.getFirst().id(), tileDecks.topTile(Tile.Kind.START).id());
        assertEquals(normalTiles.getFirst().id(), tileDecks.topTile(Tile.Kind.NORMAL).id());
        assertEquals(menhirTiles.getFirst().id(), tileDecks.topTile(Tile.Kind.MENHIR).id());
    }

    @Test
    void withTopTileDrawnThrowsOnEmptyDeck(){
        List<Tile> startTiles = new ArrayList<>(1);
        List<Tile> normalTiles = new ArrayList<>(1);
        List<Tile> menhirTiles = new ArrayList<>(1);

        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        assertThrows(IllegalArgumentException.class, () -> tileDecks.withTopTileDrawn(Tile.Kind.START));
        assertThrows(IllegalArgumentException.class, () -> tileDecks.withTopTileDrawn(Tile.Kind.NORMAL));
        assertThrows(IllegalArgumentException.class, () -> tileDecks.withTopTileDrawn(Tile.Kind.MENHIR));
    }

    @Test
    void withTopTileWorks(){
        List<Tile> startTiles1 = new ArrayList<>(1);
        List<Tile> normalTiles1 = new ArrayList<>(1);
        List<Tile> menhirTiles1 = new ArrayList<>(1);

        List<Tile> startTiles2 = new ArrayList<>(1);
        List<Tile> normalTiles2 = new ArrayList<>(1);
        List<Tile> menhirTiles2 = new ArrayList<>(1);

        startTiles1.add(new Tile(0, Tile.Kind.START, null, null, null, null));
        normalTiles1.add(new Tile(1, Tile.Kind.NORMAL, null, null, null, null));
        menhirTiles1.add(new Tile(2, Tile.Kind.MENHIR, null, null, null, null));

        startTiles1.add(new Tile(3, Tile.Kind.START, null, null, null, null));
        normalTiles1.add(new Tile(4, Tile.Kind.NORMAL, null, null, null, null));
        menhirTiles1.add(new Tile(5, Tile.Kind.MENHIR, null, null, null, null));

        TileDecks tileDecks1 = new TileDecks(startTiles1, normalTiles1, menhirTiles1);
        TileDecks tileDecks2 = new TileDecks(startTiles2, normalTiles2, menhirTiles2);

        assertEquals(tileDecks2.deckSize(Tile.Kind.START), tileDecks1.withTopTileDrawn(Tile.Kind.START)
                .deckSize(Tile.Kind.START));
        assertEquals(tileDecks2.deckSize(Tile.Kind.START), tileDecks1.withTopTileDrawn(Tile.Kind.START)
                .deckSize(Tile.Kind.START));
    }
}
