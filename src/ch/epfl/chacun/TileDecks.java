package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the three decks of tiles.
 *
 * @param startTiles
 * @param normalTiles
 * @param menhirTiles
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {
    /**
     * Makes a defensive copy of the tile lists.
     *
     * @param startTiles  the deck containing the start tiles
     * @param normalTiles the deck containing the normal tiles
     * @param menhirTiles the deck containing the menhir tiles
     */
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    /**
     * Returns the size of the deck containing tiles of a given kind.
     *
     * @param kind the kind of tile
     * @return the size of the deck o a given kind
     */
    public int deckSize(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
    }

    /**
     * Returns the tile at the top of the deck containing tiles of the given kind,
     * or null if the deck is empty.
     *
     * @param kind the kind of tile
     * @return the tile at the top of the deck containing tiles of the given kind,
     * or null if the deck is empty
     */
    public Tile topTile(Tile.Kind kind) {
        if (deckSize(kind) <= 0) {
            return null;
        }
        return switch (kind) {
            case START -> startTiles.getFirst();
            case NORMAL -> normalTiles.getFirst();
            case MENHIR -> menhirTiles.getFirst();
        };
    }

    /**
     * Returns a new triplet of decks after removing from the receiver triplet the top tile of the deck
     * containing tiles of a given kind.
     *
     * @param kind the kind of tile
     * @return a new triplet of decks after removing from the receiver triplet the top tile of the deck
     * containing tiles of a given kind
     * @throws IllegalArgumentException if the receiver deck of the given tile kind is empty
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        if (deckSize(kind) <= 0) {
            throw new IllegalArgumentException("The deck of the given tile kind is empty");
        }

        // Copy to prevent modification of the original list since this class is immutable
        List<Tile> startTiles = List.copyOf(this.startTiles);
        List<Tile> normalTiles = List.copyOf(this.normalTiles);
        List<Tile> menhirTiles = List.copyOf(this.menhirTiles);

        switch (kind) {
            case START -> startTiles.removeFirst();
            case NORMAL -> normalTiles.removeFirst();
            case MENHIR -> menhirTiles.removeFirst();
        }

        return new TileDecks(startTiles, normalTiles, menhirTiles);
    }

    /**
     * Returns a new triplet of decks after testing on the receiver deck containing the given tile kind a
     * given predicate using the {@code testPredicateOnDeck} function.
     *
     * @param kind      the kind of tile
     * @param predicate the predicate to test
     * @return a new triplet of decks after testing a given predicate on the receiver triplet
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        // Copy to prevent modification of the original list since this class is immutable
        List<Tile> startTiles = List.copyOf(this.startTiles);
        List<Tile> normalTiles = List.copyOf(this.normalTiles);
        List<Tile> menhirTiles = List.copyOf(this.menhirTiles);

        switch (kind) {
            case START -> filterDeckUsingPredicate(startTiles, predicate);
            case NORMAL -> filterDeckUsingPredicate(normalTiles, predicate);
            case MENHIR -> filterDeckUsingPredicate(menhirTiles, predicate);
        }

        return new TileDecks(startTiles, normalTiles, menhirTiles);
    }

    /**
     * Modify a deck of tiles by removing the tiles that does not respect a given predicate.
     *
     * @param deck      the deck of tiles
     * @param predicate the predicate to check
     */
    private void filterDeckUsingPredicate(List<Tile> deck, Predicate predicate) {
        for (int i = 0; i < deck.size(); ++i) {
            if (!predicate.test(deck.get(i))) {
                deck.remove(i);
            }
        }
    }
}