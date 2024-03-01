package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a tile that has been placed on the board.
 *
 * @param tile     the tile that has been placed
 * @param placer   the player who placed the tile
 * @param rotation the rotation of the tile
 * @param pos      the position of the tile
 * @param occupant the first occupant of the tile
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {

    /**
     * Additional constructor to ease to creating tiles placed without an occupant.
     *
     * @param tile     the tile that has been placed
     * @param placer   the player who placed the tile
     * @param rotation the rotation of the tile
     * @param pos      the position of the tile
     */
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    /**
     * Validates the given tile, rotation and position.
     *
     * @throws NullPointerException if tile, rotation or pos is null
     */
    public PlacedTile {
        Objects.requireNonNull(tile);
        Objects.requireNonNull(rotation);
        Objects.requireNonNull(pos);

        // Prevent a null placer
        if (placer == null && !pos.equals(Pos.ORIGIN)) {
            throw new IllegalArgumentException("A tile must have a placer except for the origin.");
        }
    }

    /**
     * Returns the id of the placed tile.
     *
     * @return the id of the tile
     */
    public int id() {
        return tile.id();
    }

    /**
     * Returns the kind of the tile.
     *
     * @return the kind of the tile
     */
    public Tile.Kind kind() {
        return tile.kind();
    }

    /**
     * Represents the direction of each side of the placed tile considering the rotation.
     *
     * @param direction the direction  of the placed tile
     * @return the direction of each side of the placed tile considering the rotation
     */
    public TileSide side(Direction direction) {
        Direction rotatedDirection = direction.rotated(rotation.negated());
        return switch (rotatedDirection) {
            case N -> tile.n();
            case E -> tile.e();
            case S -> tile.s();
            case W -> tile.w();
        };
    }

    /**
     * Returns the area of the placed tile whose id is given, or throws IllegalArgumentException
     * if the tile has no area with this id.
     *
     * @param id the id of the placed tile
     * @return the area of the placed tile whose id is given
     * @throws IllegalArgumentException if the tile has no area with this id
     */
    public Zone zoneWithId(int id) {
        return tile.zones().stream().filter(z -> z.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown zone id: " + id));
    }

    /**
     * Returns the special power zone of the placed tile if any.
     *
     * @return the special power zone of the placed tile or null if there is none
     */
    public Zone specialPowerZone() {
        return tile.zones().stream().filter(z -> z.specialPower() != null).findFirst().orElse(null);
    }

    /**
     * Returns all zones present in the tile that have the forest type.
     *
     * @return all zones present in the tile that have the forest type
     */
    public Set<Zone.Forest> forestZones() {
        return tile.zones().stream().filter(Zone.Forest.class::isInstance)
                .map(Zone.Forest.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns all zones present in the tile that have the meadow type.
     *
     * @return all zones present in the tile that have the meadow type
     */
    public Set<Zone.Meadow> meadowZones() {
        return tile.zones().stream().filter(Zone.Meadow.class::isInstance)
                .map(Zone.Meadow.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns all zones present in the tile that have the river type.
     *
     * @return all zones present in the tile that have the river type
     */
    public Set<Zone.River> riverZones() {
        return tile.zones().stream().filter(Zone.River.class::isInstance)
                .map(Zone.River.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns each potential occupant of each zone of the tile based on the game rules.
     *
     * @return each potential occupant of each zone of the tile
     */
    public Set<Occupant> potentialOccupants() {
        // Calculate all the potential occupants
        Set<Occupant> potentialOccupants = new HashSet<>();
        // The origin tile cannot be occupied
        if (placer != null) {
            for (Zone zone : tile.zones()) {
                // A pawn can only be placed on a meadow, a forest or a river
                if (!(zone instanceof Zone.Lake)) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                }
                // A hut can only be placed on a lake if it is connected to a river
                // or on a river if there's no lake
                if (zone instanceof Zone.Lake || (zone instanceof Zone.River river && !river.hasLake())) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.HUT, zone.id()));
                }
            }
        }
        return potentialOccupants;
    }

    /**
     * Returns the same tile, with a given occupant added (if there were none).
     *
     * @param occupant the occupant to add
     * @return the same tile, with a given occupant added
     * @throws IllegalArgumentException if the tile already has an occupant
     */
    public PlacedTile withOccupant(Occupant occupant) {
        if (this.occupant != null) {
            throw new IllegalArgumentException("Tile already has an occupant");
        }
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }

    /**
     * Returns the same tile, with no occupant.
     *
     * @return the same tile, with no occupant
     */
    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos);
    }

    /**
     * Returns the id of the zone occupied by the given kind of occupant, or -1 if there is none.
     * Based on the game rules, a tile can only have one occupant.
     *
     * @param occupantKind the kind of the occupant
     * @return the id of the zone occupied by the given kind of occupant, or -1 if there is none
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (occupant != null && occupant.kind() == occupantKind) {
            return occupant.zoneId();
        }
        return -1;
    }

}
