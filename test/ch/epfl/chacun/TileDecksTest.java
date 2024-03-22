package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class TileDecksTest {
    private static Tile getTile(int id, Tile.Kind kind) {
        var zoneId = id * 10;
        var l0 = new Zone.Lake(zoneId + 8, 1, null);
        var a0_0 = new Animal(zoneId * 100, Animal.Kind.AUROCHS);
        var z0 = new Zone.Meadow(zoneId * 10, List.of(a0_0), null);
        var z1 = new Zone.Forest(zoneId * 10 + 1, Zone.Forest.Kind.WITH_MENHIR);
        var z2 = new Zone.Meadow(zoneId * 10 + 2, List.of(), null);
        var z3 = new Zone.River(zoneId * 10 + 3, 0, l0);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.Forest(z1);
        var sS = new TileSide.Forest(z1);
        var sW = new TileSide.River(z2, z3, z0);
        return new Tile(id, kind, sN, sE, sS, sW);
    }

    private static Tile getTile(Tile.Kind kind) {
        return getTile(56, kind);
    }

    @Test
    void tileDecksConstructorCopiesDecks() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(getTile(Tile.Kind.NORMAL));
        var dM = List.of(getTile(Tile.Kind.MENHIR));

        var mutS = new ArrayList<>(dS);
        var mutN = new ArrayList<>(dN);
        var mutM = new ArrayList<>(dM);

        var decks = new TileDecks(mutS, mutN, mutM);
        mutS.clear();
        mutN.clear();
        mutM.clear();
        assertEquals(dS, decks.startTiles());
        assertEquals(dN, decks.normalTiles());
        assertEquals(dM, decks.menhirTiles());
    }

    @Test
    void tileDecksIsImmutable() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(getTile(Tile.Kind.NORMAL));
        var dM = List.of(getTile(Tile.Kind.MENHIR));

        var decks = new TileDecks(dS, dN, dM);
        try {decks.startTiles().clear();} catch (UnsupportedOperationException e) {/**/}
        try {decks.normalTiles().clear();} catch (UnsupportedOperationException e) {/**/}
        try {decks.menhirTiles().clear();} catch (UnsupportedOperationException e) {/**/}

        assertEquals(dS, decks.startTiles());
        assertEquals(dN, decks.normalTiles());
        assertEquals(dM, decks.menhirTiles());
    }

    @Test
    void tileDecksTopTileReturnsFirstTile() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(
                getTile(0, Tile.Kind.NORMAL),
                getTile(1, Tile.Kind.NORMAL),
                getTile(2, Tile.Kind.NORMAL));
        var dM = List.of(
                getTile(3, Tile.Kind.MENHIR),
                getTile(4, Tile.Kind.MENHIR),
                getTile(5, Tile.Kind.MENHIR));
        var decks = new TileDecks(dS, dN, dM);
        assertEquals(dS.getFirst(), decks.topTile(Tile.Kind.START));
        assertEquals(dN.getFirst(), decks.topTile(Tile.Kind.NORMAL));
        assertEquals(dM.getFirst(), decks.topTile(Tile.Kind.MENHIR));
    }

    @Test
    void tileDecksTopTileReturnsNullWhenDeckIsEmpty() {
        var decks = new TileDecks(List.of(), List.of(), List.of());
        assertNull(decks.topTile(Tile.Kind.START));
        assertNull(decks.topTile(Tile.Kind.NORMAL));
        assertNull(decks.topTile(Tile.Kind.MENHIR));
    }

    @Test
    void tileDecksWithTopTileDrawnCorrectlyRemovesTiles() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(
                getTile(0, Tile.Kind.NORMAL),
                getTile(1, Tile.Kind.NORMAL),
                getTile(2, Tile.Kind.NORMAL));
        var dM = List.of(
                getTile(3, Tile.Kind.MENHIR),
                getTile(4, Tile.Kind.MENHIR),
                getTile(5, Tile.Kind.MENHIR));
        var decks = new TileDecks(dS, dN, dM);

        decks = decks.withTopTileDrawn(Tile.Kind.START);
        assertEquals(List.of(), decks.startTiles());

        decks = decks.withTopTileDrawn(Tile.Kind.NORMAL);
        assertEquals(dN.subList(1, dN.size()), decks.normalTiles());

        decks = decks.withTopTileDrawn(Tile.Kind.MENHIR);
        assertEquals(dM.subList(1, dM.size()), decks.menhirTiles());

        decks = decks.withTopTileDrawn(Tile.Kind.NORMAL);
        assertEquals(dN.subList(2, dN.size()), decks.normalTiles());

        decks = decks.withTopTileDrawn(Tile.Kind.MENHIR);
        assertEquals(dM.subList(2, dM.size()), decks.menhirTiles());

        decks = decks.withTopTileDrawn(Tile.Kind.NORMAL);
        assertEquals(List.of(), decks.normalTiles());

        decks = decks.withTopTileDrawn(Tile.Kind.MENHIR);
        assertEquals(List.of(), decks.menhirTiles());
    }

    @Test
    void tileDecksWithTopTileDrawnThrowsWhenDeckIsEmpty() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(
                getTile(0, Tile.Kind.NORMAL),
                getTile(1, Tile.Kind.NORMAL),
                getTile(2, Tile.Kind.NORMAL));
        var dM = List.of(
                getTile(3, Tile.Kind.MENHIR),
                getTile(4, Tile.Kind.MENHIR),
                getTile(5, Tile.Kind.MENHIR));
        var decks = new TileDecks(dS, dN, dM);

        var emptyDecks = decks.withTopTileDrawn(Tile.Kind.START)
                .withTopTileDrawn(Tile.Kind.NORMAL)
                .withTopTileDrawn(Tile.Kind.MENHIR)
                .withTopTileDrawn(Tile.Kind.NORMAL)
                .withTopTileDrawn(Tile.Kind.MENHIR)
                .withTopTileDrawn(Tile.Kind.NORMAL)
                .withTopTileDrawn(Tile.Kind.MENHIR);
        assertThrows(IllegalArgumentException.class,
                () -> emptyDecks.withTopTileDrawn(Tile.Kind.START));
        assertThrows(IllegalArgumentException.class,
                () -> emptyDecks.withTopTileDrawn(Tile.Kind.NORMAL));
        assertThrows(IllegalArgumentException.class,
                () -> emptyDecks.withTopTileDrawn(Tile.Kind.MENHIR));
    }

    @Test
    void tileDecksWithTopTileDrawnUntilWorksWithTruePredicate() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(
                getTile(0, Tile.Kind.NORMAL),
                getTile(1, Tile.Kind.NORMAL),
                getTile(2, Tile.Kind.NORMAL));
        var dM = List.of(
                getTile(3, Tile.Kind.MENHIR),
                getTile(4, Tile.Kind.MENHIR),
                getTile(5, Tile.Kind.MENHIR));
        var decks = new TileDecks(dS, dN, dM);

        var truePredicate = new ConstantPredicate(true);
        assertEquals(decks, decks.withTopTileDrawnUntil(Tile.Kind.START, truePredicate));
        assertEquals(decks, decks.withTopTileDrawnUntil(Tile.Kind.NORMAL, truePredicate));
        assertEquals(decks, decks.withTopTileDrawnUntil(Tile.Kind.MENHIR, truePredicate));
    }

    @Test
    void tileDecksWithTopTileDrawnUntilWorksWithFalsePredicate() {
        var dS = List.of(getTile(Tile.Kind.START));
        var dN = List.of(
                getTile(0, Tile.Kind.NORMAL),
                getTile(1, Tile.Kind.NORMAL),
                getTile(2, Tile.Kind.NORMAL));
        var dM = List.of(
                getTile(3, Tile.Kind.MENHIR),
                getTile(4, Tile.Kind.MENHIR),
                getTile(5, Tile.Kind.MENHIR));
        var decks = new TileDecks(dS, dN, dM);

        var falsePredicate = new ConstantPredicate(false);
        assertEquals(List.of(),
                decks.withTopTileDrawnUntil(Tile.Kind.START, falsePredicate).startTiles());
        assertEquals(List.of(),
                decks.withTopTileDrawnUntil(Tile.Kind.NORMAL, falsePredicate).normalTiles());
        assertEquals(List.of(),
                decks.withTopTileDrawnUntil(Tile.Kind.MENHIR, falsePredicate).menhirTiles());
    }

    record ConstantPredicate(boolean b) implements Predicate<Tile> {
        @Override
        public boolean test(Tile tile) {
            return b;
        }
    }
}