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
    }

    public int id() {
        return tile.id();
    }

    public Tile.Kind kind() {
        return tile.kind();
    }

    public TileSide side(Direction direction) {
        Direction rotatedDirection = direction.rotated(rotation);
        return switch (rotatedDirection) {
            case N -> tile.n();
            case E -> tile.e();
            case S -> tile.s();
            case W -> tile.w();
        };
    }

    public Zone zoneWithId(int id) {
        return tile.zones().stream().filter(z -> z.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown zone id: " + id));
    }

    public Zone specialPowerZone() {
        return tile.zones().stream().filter(z -> z.specialPower() != null).findFirst().orElse(null);

    }

    public Set<Zone.Forest> forestZones() {
        return tile.zones().stream().filter(Zone.Forest.class::isInstance)
                .map(Zone.Forest.class::cast).collect(Collectors.toSet());
    }

    public Set<Zone.Meadow> meadowZones() {
        return tile.zones().stream().filter(Zone.Meadow.class::isInstance)
                .map(Zone.Meadow.class::cast).collect(Collectors.toSet());
    }

    public Set<Zone.River> riverZones() {
        return tile.zones().stream().filter(Zone.River.class::isInstance)
                .map(Zone.River.class::cast).collect(Collectors.toSet());
    }

    public Set<Occupant> potentialOccupants() {
        // The origin tile cannot be occupied
        if (placer == null) {
            return null;
        }
        // Calculate all the potential occupants
        Set<Occupant> potentialOccupants = new HashSet<>();
        for (Zone zone : tile.zones()) {
            Occupant potentialPawn = switch (zone) {
                case Zone.Meadow m -> new Occupant(Occupant.Kind.PAWN, zone.id());
                case Zone.Forest f -> new Occupant(Occupant.Kind.PAWN, zone.id());
                case Zone.River r -> new Occupant(Occupant.Kind.PAWN, zone.id());
                default -> null;
            };
            // A HUT can only be placed on a river with at least one lake
            if (zone instanceof Zone.River river && river.hasLake()) {
                potentialOccupants.add(new Occupant(Occupant.Kind.HUT, zone.id()));
            }
            // Add the potential pawn
            if (potentialPawn != null) {
                potentialOccupants.add(potentialPawn);
            }
        }
        return potentialOccupants;
    }

    public PlacedTile withOccupant(Occupant occupant) {
        if (this.occupant != null) {
            throw new IllegalArgumentException("Tile already has an occupant");
        }
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }

    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos);
    }

    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (occupant != null && occupant.kind() == occupantKind) {
            return occupant.zoneId();
        }
        return -1;
    }

}
