package ch.epfl.chacun;

import java.util.List;

/**
 * @param startTiles
 * @param normalTiles
 * @param menhirTiles
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {

    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    public int deckSize(Tile.Kind kind) {
        int deckSize = switch (kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
        return deckSize;
    }

    public Tile topTile(Tile.Kind kind) {
        Tile topTile = switch (kind) {
            case START -> startTiles.getFirst();
            case NORMAL -> normalTiles.getFirst();
            case MENHIR -> menhirTiles.getFirst();
        };
        return topTile;
    }

    public TileDecks withTopTileDrawn(Tile.Kind kind) {
    }
}
