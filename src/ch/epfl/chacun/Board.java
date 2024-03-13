package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the board of the game.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public class Board {

    public static final int REACH = 12;
    public static final Board EMPTY = new Board();
    private final PlacedTile[] placedTiles;
    private final int[] tileIndices;
    private final ZonePartitions zonePartitions;
    private final Set<Animal> cancelledAnimals;

    /**
     * Private constructor of the board to initialize values.
     */
    private Board() {
        this.placedTiles = new PlacedTile[625];
        this.tileIndices = new int[0];
        this.zonePartitions = ZonePartitions.EMPTY;
        this.cancelledAnimals = new HashSet<>();
    }

    public PlacedTile tileAt(Pos pos) {
        // Implement method
        return null;
    }

    public PlacedTile tileWithId(int tileId) {
        // Implement method
        return null;
    }

    public Set<Animal> cancelledAnimals() {
        return new HashSet<>(cancelledAnimals);
    }

    public Set<Occupant> occupants() {
        // Implement method
        return null;
    }

    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        // Implement method
        return null;
    }

    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        // Implement method
        return null;
    }

    public Area<Zone.River> riverArea(Zone.River riverZone) {
        // Implement method
        return null;
    }

    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        // Implement method
        return null;
    }

    public Set<Area<Zone.Meadow>> meadowAreas() {
        // Implement method
        return null;
    }

    public Set<Area<Zone.Water>> riverSystemAreas() {
        // Implement method
        return null;
    }

    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        // Implement method
        return null;
    }

    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        // Implement method
        return 0;
    }

    public Set<Pos> insertionPositions() {
        // Implement method
        return null;
    }
}