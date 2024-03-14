package ch.epfl.chacun;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the board of the game.
 * <p>
 * This board will have a size of 625 (25×25) elements, and will be organized in reading order,
 * starting from the cell at the top left of the board, and going through the rows before the columns.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class Board {

    public static final int REACH = 12;
    public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], ZonePartitions.EMPTY, Set.of());
    private static final int SIZE = 25;
    private final PlacedTile[] placedTiles;
    private final int[] tileIndices;
    private final ZonePartitions zonePartitions;
    private final Set<Animal> cancelledAnimals;

    /**
     * Private constructor of the board to initialize values.
     */
    private Board(PlacedTile[] placedTiles, int[] tileIndices, ZonePartitions zonePartitions, Set<Animal> cancelledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndices = tileIndices;
        this.zonePartitions = zonePartitions;
        this.cancelledAnimals = cancelledAnimals;
    }

    /**
     * Calculates the row major index of the given position.
     *
     * @param pos the position
     * @return the row major index of the given position
     */
    private int calculateRowMajorIndex(Pos pos) {
        int originIndex = (placedTiles.length - 1) / 2;
        return originIndex + pos.x() + pos.y() * SIZE;
    }

    /**
     * Returns the tile in the given position, or null if there is none or if the position is off the board.
     *
     * @param pos the position of the tile
     * @return the tile in the given position, or null if there is none or if the position is off the board
     */
    public PlacedTile tileAt(Pos pos) {
        int index = calculateRowMajorIndex(pos);
        if (index < 0 || index >= placedTiles.length) {
            return null;
        }
        return placedTiles[index];
    }

    /**
     * Returns the tile with the given id.
     *
     * @param tileId the id of the tile
     * @return the tile with the given id, if found
     * @throws IllegalArgumentException if no tile with the given id is found
     */
    public PlacedTile tileWithId(int tileId) {
        for (int placedTileIndex : tileIndices) {
            PlacedTile placedTile = placedTiles[placedTileIndex];
            if (placedTile.tile().id() == tileId) {
                return placedTile;
            }
        }
        throw new IllegalArgumentException("No tile with the given id found.");
    }

    /**
     * Returns the cancelled animals.
     * <p>Cancelled animals can be, for example, deer eaten by smilodons.
     *
     * @return the set of cancelled animals
     */
    public Set<Animal> cancelledAnimals() {
        // Defensive copy
        return Collections.unmodifiableSet(cancelledAnimals);
    }

    /**
     * Return all the occupants on the tiles placed on the board.
     *
     * @return the set of all occupants on the board
     */
    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();
        for (int placedTileIndex : tileIndices) {
            occupants.add(placedTiles[placedTileIndex].occupant());
        }
        return occupants;
    }

    /**
     * Returns the forest area containing the given zone.
     *
     * @param forest the forest zone
     * @return the area containing the given zone
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return zonePartitions.forests().areaContaining(forest);
    }

    /**
     * Returns the meadow area containing the given zone.
     *
     * @param meadow the meadow zone
     * @return the area containing the given zone
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return zonePartitions.meadows().areaContaining(meadow);
    }

    /**
     * Returns the river area containing the given zone.
     *
     * @param riverZone the river zone
     * @return the area containing the given zone
     */
    public Area<Zone.River> riverArea(Zone.River riverZone) {
        return zonePartitions.rivers().areaContaining(riverZone);
    }

    /**
     * Returns the river system area containing the given zone.
     *
     * @param waterZone the water zone
     * @return the area containing the given zone
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water waterZone) {
        return zonePartitions.riverSystems().areaContaining(waterZone);
    }

    /**
     * Returns all meadow areas of the board.
     *
     * @return the set of all meadow areas of the board
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        return zonePartitions.meadows().areas();
    }

    /**
     * Returns all river system areas of the board.
     *
     * @return the set of all river system areas of the board
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return zonePartitions.riverSystems().areas();
    }

    /**
     * Returns the meadow adjacent to the given zone, in the form of an area which contains only
     * the zones of this meadow but all the occupants of the complete meadow,
     * and which, for simplicity, has no open connections.
     *
     * @param pos        the position of the tile
     * @param meadowZone the meadow zone
     * @return the meadow adjacent to the given zone
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Area<Zone.Meadow> originalArea = meadowArea(meadowZone);
        Set<Zone.Meadow> adjacentZones = new HashSet<>();
        for (Zone.Meadow zone : originalArea.zones()) {
            // Get the placed tile with the given id
            PlacedTile tile = tileWithId(zone.tileId());
            // Check if the given tile is adjacent to the given one
            int dX = Math.abs(pos.x() - tile.pos().x());
            int dY = Math.abs(pos.y() - tile.pos().y());
            if (dX <= 1 && dY <= 1) {
                adjacentZones.add(zone);
            }
        }
        // Create the adjacent area, with the same occupants
        return new Area<>(adjacentZones, originalArea.occupants(), 0);
    }

    /**
     * Return the number of occupants of the given type belonging to the given player on the board.
     *
     * @param player       the player
     * @param occupantKind the occupant kind
     * @return the number of occupants of the given type belonging to the given player on the board
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int occupantCount = 0;
        for (int placedTileIndex : tileIndices) {
            PlacedTile placedTile = placedTiles[placedTileIndex];
            // If a tile has an occupant, it has been placed by the tile placer
            if (placedTile.occupant().kind() == occupantKind && placedTile.placer() == player) {
                occupantCount++;
            }
        }
        return occupantCount;
    }

    /**
     * Returns the set of all insertion positions on the board.
     * <p> An insertion position is a position on the board where a tile can be placed.
     *
     * @return the set of all insertion positions on the board
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> insertionPositions = new HashSet<>();
        for (int placedTileIndex : tileIndices) {
            PlacedTile placedTile = placedTiles[placedTileIndex];
            for (Direction direction : Direction.ALL) {
                Pos neighbor = placedTile.pos().neighbor(direction);
                if (tileAt(neighbor) == null) {
                    insertionPositions.add(neighbor);
                }
            }
        }
        return insertionPositions;
    }

    /**
     * Returns the last placed tile on the board.
     * <p>
     * It can be the starting tile if the first normal tile has not yet been placed, or null if the board is empty
     *
     * @return the last placed tile on the board
     */
    public PlacedTile lastPlacedTile() {
        if (tileIndices.length > 0) {
            return placedTiles[tileIndices[tileIndices.length - 1]];
        }
        return null;
    }

    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        // TODO: implement this method
        return Set.of();
    }

    public Set<Area<Zone.River>> riversClosedByLastTile() {
        // TODO: implement this method
        return Set.of();
    }

    /**
     * Returns true if the given placed tile can be added to the board.
     * <p>
     * i.e. if its position is an insertion position and every edge of the tile that touches
     * an edge of a tile already placed is of the same kind as it.
     *
     * @param tile the placed tile to check
     * @return true if the given placed tile can be added to the board
     */
    public boolean canAddTile(PlacedTile tile) {
        // Check if the tile cannot be placed on the board
        if (!insertionPositions().contains(tile.pos())) {
            return false;
        }
        // Check for potential conflicts with adjacent tiles
        for (Direction direction : Direction.ALL) {
            Pos neighbor = tile.pos().neighbor(direction);
            PlacedTile neighborTile = tileAt(neighbor);
            if (neighborTile != null) {
                TileSide neighborSide = neighborTile.side(direction.opposite());
                TileSide tileSide = tile.side(direction);
                if (!tileSide.isSameKindAs(neighborSide)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns whether if the given tile could be placed on one of the board's
     * insertion positions, possibly after rotation, or not.
     *
     * @param tile the tile to check for placement possibilities
     * @return whether the given tile could be placed on one of the board's insertion positions or not
     */
    public boolean couldPlaceTile(Tile tile) {
        for (Pos insertionPosition : insertionPositions()) {
            for (Rotation rotation : Rotation.ALL) {
                PlacedTile placedTile = new PlacedTile(tile, null, rotation, insertionPosition);
                if (canAddTile(placedTile)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns an identical board, but with the given tile in addition.
     *
     * @param tile the tile to place
     * @return an identical board, but with the given tile in addition
     * @throws IllegalArgumentException if the board is not empty and the given tile cannot be added to the board
     */
    public Board withNewTile(PlacedTile tile) {
        // Check if the tile can be placed on the board
        if (placedTiles.length != 0 && !canAddTile(tile)) {
            throw new IllegalArgumentException("The tile cannot be placed on the board.");
        }
        // Create the new placed tiles array
        int newTileIndex = calculateRowMajorIndex(tile.pos());
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        newPlacedTiles[newTileIndex] = tile;
        // Create the new tile indices array
        int[] newTileIndices = Arrays.copyOf(tileIndices, tileIndices.length + 1);
        newTileIndices[tileIndices.length] = newTileIndex;
        // Update zone partitions
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        builder.addTile(tile.tile());
        for (Direction direction : Direction.ALL) {
            TileSide side = tile.side(direction);
            PlacedTile adjacentTile = tileAt(tile.pos().neighbor(direction));
            if (adjacentTile != null) {
                builder.connectSides(side, adjacentTile.side(direction.opposite()));
            }
        }
        // Create a new board with the new tile
        return new Board(newPlacedTiles, newTileIndices, builder.build(), cancelledAnimals());
    }

    /**
     * Returns the same board, but with the given occupant on the given tile.
     *
     * @param occupant the occupant to place
     * @return the same board, but with the given occupant on the given tile
     */
    public Board withOccupant(Occupant occupant) {
        int tileId = Zone.tileId(occupant.zoneId());
        PlacedTile placedTile = tileWithId(tileId);
        if (placedTile.occupant() != null) {
            throw new IllegalArgumentException("The tile already has an occupant.");
        }
        // Update zone partitions
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        builder.addInitialOccupant(placedTile.placer(), occupant.kind(), placedTile.zoneWithId(occupant.zoneId()));
        // Create the new placed tiles array
        PlacedTile[] newPlacedTile = placedTiles.clone();
        newPlacedTile[calculateRowMajorIndex(placedTile.pos())] = placedTile.withOccupant(occupant);
        // Create the new board
        return new Board(newPlacedTile, tileIndices.clone(), builder.build(), cancelledAnimals());
    }

    public Board withoutOccupant(Occupant occupant) {
        // TODO: implement this method
        return null;
    }

    /**
     * Returns the same board but without any occupants in the given forests and rivers.
     *
     * @param forests the forest areas to remove gatherers from
     * @param rivers  the river areas to remove fishers from
     * @return the same board but without any occupants in the given forests and rivers
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        // Remove gatherers from all given areas
        for (Area<Zone.Forest> forest : forests) {
            builder.clearGatherers(forest);
        }
        // Remove fishers from all given areas
        for (Area<Zone.River> river : rivers) {
            builder.clearFishers(river);
        }
        // TODO: Sync placedTile
        // Create the new board
        return new Board(placedTiles.clone(), tileIndices.clone(), builder.build(), cancelledAnimals());
    }

    /**
     * Returns the same board, but with more cancelled animals.
     *
     * @param newlyCancelledAnimals the set of animals to add to the cancelled animals
     * @return the same board, but with more cancelled animals
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        Set<Animal> newCancelledAnimals = new HashSet<>(cancelledAnimals);
        newCancelledAnimals.addAll(newlyCancelledAnimals);
        return new Board(placedTiles.clone(), tileIndices.clone(), zonePartitions, newCancelledAnimals);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Board board) {
            // TODO: Compare zonePartitions
            return Arrays.equals(placedTiles, board.placedTiles)
                    && Arrays.equals(tileIndices, board.tileIndices)
                    && Arrays.equals(cancelledAnimals.toArray(), board.cancelledAnimals.toArray());
        }
        return false;
    }
}