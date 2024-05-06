package ch.epfl.chacun;

import java.util.ArrayList;
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
        return deckSize(kind) <= 0 ? null : switch (kind) {
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
        Preconditions.checkArgument(deckSize(kind) > 0);
        return switch (kind) {
            case START -> new TileDecks(removeDeckFirstTile(startTiles), normalTiles, menhirTiles);
            case NORMAL -> new TileDecks(startTiles, removeDeckFirstTile(normalTiles), menhirTiles);
            case MENHIR -> new TileDecks(startTiles, normalTiles, removeDeckFirstTile(menhirTiles));
        };
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
        return switch (kind) {
            case START -> new TileDecks(filterDeck(startTiles, predicate), normalTiles, menhirTiles);
            case NORMAL -> new TileDecks(startTiles, filterDeck(normalTiles, predicate), menhirTiles);
            case MENHIR -> new TileDecks(startTiles, normalTiles, filterDeck(menhirTiles, predicate));
        };
    }

    /**
     * Modify a deck of tiles by removing the tiles that does not respect a given predicate.
     *
     * @param deck      the deck of tiles
     * @param predicate the predicate to check
     * @return a new deck of tiles after removing the tiles that does not respect the given predicate
     */
    private List<Tile> filterDeck(List<Tile> deck, Predicate<Tile> predicate) {
        List<Tile> filteredDeck = deck;
        while (!filteredDeck.isEmpty() && !predicate.test(filteredDeck.getFirst())) {
            filteredDeck = removeDeckFirstTile(filteredDeck);
        }
        return filteredDeck;
    }

    /**
     * Removes the first tile from a given deck of tiles.
     *
     * @param deck the deck of tiles
     * @return a new deck that excludes the first tile from the original deck
     */
    private List<Tile> removeDeckFirstTile(List<Tile> deck) {
        return deck.subList(1, deck.size());
    }
}