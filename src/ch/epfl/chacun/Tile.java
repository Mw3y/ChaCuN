package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a tile of the game.
 *
 * @param id   the id of the tile
 * @param kind the kind of the tile
 * @param n    north side of the tile
 * @param e    east side od^f the tile
 * @param s    south side of the tile
 * @param w    west side of the tile
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {

    /**
     * Returns the list of all the possible sides of the tile.
     *
     * @return the list of all the possible sides of the tile
     */
    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }

    /**
     * Returns the set of all the zones that touch at least one side of the tile.
     *
     * @return the set of all the zones that touch at least one side of the tile
     */
    public Set<Zone> sideZones() {
        HashSet<Zone> zones = new HashSet<>();
        zones.addAll(n.zones());
        zones.addAll(e.zones());
        zones.addAll(s.zones());
        zones.addAll(w.zones());
        return zones;
    }

    /**
     * Returns the set of all the zones that are contained in the tile.
     *
     * @return the set of all the zones that are contained in the tile
     */
    public Set<Zone> zones() {
        Set<Zone> zones = Set.copyOf(sideZones());
        for (Zone zone : sideZones()) {
            if (zone instanceof Zone.River river) {
                if (river.hasLake())
                    zones.add(river.lake());
            }
        }
        return zones;
    }

    /**
     * Represents the different kinds of tiles.
     */
    public enum Kind {
        START,
        NORMAL,
        MENHIR;
    }
}
