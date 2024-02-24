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
     * @param startTiles
     * @param normalTiles
     * @param menhirTiles
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
        if (this.deckSize(kind) <= 0) {
            return null;
        } else {
            return switch (kind) {
                case START -> startTiles.getFirst();
                case NORMAL -> normalTiles.getFirst();
                case MENHIR -> menhirTiles.getFirst();
            };
        }
    }

    /**
     * Returns a new triplet of decks after removing from the receiver triplet the top tile of the deck
     * containing tiles of a given kind.
     *
     * @param kind the kind of tile
     * @return a new triplet of decks after removing from the receiver triplet the top tile of the deck
     *         containing tiles of a given kind
     * @throws IllegalArgumentException if the receiver deck of the given tile kind is empty
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        if (this.deckSize(kind) <= 0) {
            throw new IllegalArgumentException("The deck of the given tile is empty");
        }
        switch (kind) {
            case START -> this.startTiles.remove(0);
            case NORMAL -> this.normalTiles.remove(0);
            case MENHIR -> this.menhirTiles.remove(0);
        }
        return this;
    }

    /**
     * Returns a new triplet of decks after testing on the receiver deck containing the given tile kind a
     * given predicate using testPredicateOnDeck.
     *
     * @param kind the kind of tile
     * @param predicate the predicate to test
     * @return a new triplet of decks after testing a given predicate on the receiver triplet
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        switch (kind) {
            case START -> testPredicateOnDeck(this.startTiles, predicate);
            case NORMAL -> testPredicateOnDeck(this.normalTiles, predicate);
            case MENHIR -> testPredicateOnDeck(this.menhirTiles, predicate);
        }
        return this;
    }

    /**
     * Returns a new deck of tiles after removing the tiles that does not respect a given predicate.
     *
     * @param deck the deck of tiles
     * @param predicate the predicate to check
     * @return a new deck of tiles after removing the tiles that does not respect a given predicate
     */
    private List<Tile> testPredicateOnDeck(List<Tile> deck, Predicate predicate) {
        List<Tile> newdeck = deck;
        for (int i = 0; i < newdeck.size(); ++i) {
            if (!predicate.test(newdeck.get(i))) {
                newdeck.remove(i);
            }
        }
        return newdeck;
    }
}