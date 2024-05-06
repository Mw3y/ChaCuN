package ch.epfl.chacun;

import java.util.Comparator;
import java.util.List;

/**
 * Helper class to encode the game actions.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public class ActionEncoder {

    /**
     * When the player doesn't want to add or remove an occupant, send 0b11111.
     */
    private static final String NO_OCCUPANT_ENCODED_ACTION = Base32.encodeBits5(0b11111);

    /**
     * The number of bits to shift to encode the placed tile index.
     * Encoded format: ppppp-ppprr, where p is a bit of the placed tile index and r is a bit of the rotation.
     */
    private static final int PLACED_TILE_INDEX_SHIFT = 2;

    /**
     * The mask to apply to the encoded action to get the placed tile index.
     * Encoded format: ppppp-ppprr, where p is a bit of the placed tile index and r is a bit of the rotation.
     */
    private static final int PLACED_TILE_ROTATION_MASK = (1 << PLACED_TILE_INDEX_SHIFT) - 1;

    /**
     * The base 32 string length of the action of placing a tile.
     */
    private static final int PLACE_TILE_ENCODED_ACTION_LENGTH = 2;

    /**
     * The number of bits to shift to encode the occupant kind.
     * Encoded format: kzzzz, where k is a bit of the occupant kind and z is a bit of the zone id.
     */
    private static final int OCCUPANT_KIND_SHIFT = 4;

    /**
     * The mask to apply to the encoded action to get the occupant zone id.
     * Encoded format: kzzzz, where k is a bit of the occupant kind and z is a bit of the zone id.
     */
    private static final int OCCUPANT_ZONE_MASK = (1 << OCCUPANT_KIND_SHIFT) - 1;

    /**
     * The base 32 string length of the action of placing or removing an occupant.
     */
    private static final int OCCUPANT_ENCODED_ACTION_LENGTH = 1;

    /**
     * Sorts the insertion positions of the given game state in ascending order, first by their x-coordinate,
     * then by their y-coordinate.
     *
     * @param gameState the given game state
     * @return a list containing the sorted positions
     */
    private static List<Pos> sortFringe(GameState gameState) {
        Comparator<Pos> comparator = Comparator.comparing(Pos::x).thenComparing(Pos::y);
        return gameState.board().insertionPositions().stream().sorted(comparator).toList();
    }

    /**
     * Sorts the occupants of the given game state in ascending order by their zone id.
     *
     * @param gameState the given game state
     * @return a list containing the sorted occupants
     */
    private static List<Occupant> sortOccupants(GameState gameState) {
        return gameState.board().occupants().stream()
                .sorted(Comparator.comparing(Occupant::zoneId)).toList();
    }

    /**
     * Manages the encoding of the action of placing a tile.
     * <p>
     * Sorts the fringe and encodes the index of the placedTile in the fringe and the applied rotation in a
     * base 32 string.
     * Updates the game state with the new placed tile.
     *
     * @param gameState  the game state to be updated
     * @param placedTile the placed tile
     * @return a new state action with the updated game state and the encoded action
     */
    public static StateAction withPLacedTile(GameState gameState, PlacedTile placedTile) {
        List<Pos> sortedFringe = sortFringe(gameState);
        // Encode the placed tile index then shift it of two positions to the left to merge the encoded rotation
        int fringeBits = sortedFringe.indexOf(placedTile.pos()) << PLACED_TILE_INDEX_SHIFT;
        int rotationBits = placedTile.rotation().ordinal();
        return new StateAction(
                gameState.withPlacedTile(placedTile), Base32.encodeBits10(fringeBits | rotationBits));
    }

    /**
     * Manages the encoding of the action of placing a new occupant.
     * <p>
     * Encodes the id of the occupied zone and the occupant kind in a base 32 string.
     * Updates the given game state with a new occupant.
     *
     * @param gameState       the game state to be updated
     * @param occupantToPlace the occupant to place
     * @return a new state action with the updated game state and the encoded action
     */
    public static StateAction withNewOccupant(GameState gameState, Occupant occupantToPlace) {
        assert gameState.board().lastPlacedTile() != null;
        if (occupantToPlace != null) {
            // Format the action data
            int kindBits = occupantToPlace.kind().ordinal() << OCCUPANT_KIND_SHIFT;
            int zoneBits = Zone.localId(occupantToPlace.zoneId());
            return new StateAction(
                    gameState.withNewOccupant(occupantToPlace), Base32.encodeBits5(kindBits | zoneBits));
        }
        return new StateAction(gameState.withNewOccupant(null), NO_OCCUPANT_ENCODED_ACTION);
    }

    /**
     * Manages the encoding of the action of taking back an occupant.
     * <p>
     * Encodes the zone id from which the occupant is removed.
     * Updates the given game state with an occupant removed.
     *
     * @param gameState        the game state to be updated
     * @param occupantToRemove the occupant to remove
     * @return a new state action with the updated game state and the encoded action
     */
    public static StateAction withOccupantRemoved(GameState gameState, Occupant occupantToRemove) {
        if (occupantToRemove != null) {
            List<Occupant> sortedOccupants = sortOccupants(gameState);
            // Encode action
            int occupantIndex = sortedOccupants.indexOf(occupantToRemove);
            return new StateAction(
                    gameState.withOccupantRemoved(occupantToRemove), Base32.encodeBits5(occupantIndex));
        }
        return new StateAction(gameState.withOccupantRemoved(null), NO_OCCUPANT_ENCODED_ACTION);
    }

    /**
     * Decodes the given action and applies it to the given game state based on the next action.
     *
     * @param gameState the given game state
     * @param action    the encoded action
     * @return a new state action with an updated game state or null if the action is not valid
     */
    public static StateAction decodeAndApply(GameState gameState, String action) {
        try {
            return unsafeDecodeAndApply(gameState, action);
        } catch (IllegalActionException e) {
            return null;
        }
    }

    /**
     * Unsafely decodes the given action and applies it to the given game state based on the next action.
     *
     * @param gameState the game state
     * @param action    the encoded action
     * @return a new state action with an updated game state
     * @throws IllegalActionException if the action is illegal
     */
    private static StateAction unsafeDecodeAndApply(GameState gameState, String action) throws IllegalActionException {
        // Check if the received action is valid
        if (!Base32.isValid(action) || action.isEmpty())
            throw new IllegalActionException();

        int decodedAction = Base32.decode(action);
        // Execute the provided action based on the next action context
        return switch (gameState.nextAction()) {
            case PLACE_TILE -> {
                Rotation placedTileRotation = Rotation.ALL.get(decodedAction & PLACED_TILE_ROTATION_MASK);
                int posIndex = decodedAction >> PLACED_TILE_INDEX_SHIFT;
                List<Pos> fringe = sortFringe(gameState);
                // Check if the all the data is present and if the insertion position exists in the fringe
                if (action.length() != PLACE_TILE_ENCODED_ACTION_LENGTH || fringe.size() <= posIndex)
                    throw new IllegalActionException();

                Pos placedTilePos = fringe.get(posIndex);
                PlacedTile placedTile = new PlacedTile(gameState.tileToPlace(), gameState.currentPlayer(),
                        placedTileRotation, placedTilePos);

                // Check if the tile can be placed
                if (!gameState.board().canAddTile(placedTile))
                    throw new IllegalActionException();

                yield new StateAction(gameState.withPlacedTile(placedTile), action);
            }
            case OCCUPY_TILE -> {
                assert gameState.board().lastPlacedTile() != null;
                // Check if the player doesn't want to add an occupant
                if (action.equals(NO_OCCUPANT_ENCODED_ACTION))
                    yield new StateAction(gameState.withNewOccupant(null), action);

                if (action.length() != OCCUPANT_ENCODED_ACTION_LENGTH)
                    throw new IllegalActionException();

                // Decode the action
                int occupantLocalId = decodedAction & OCCUPANT_ZONE_MASK;
                // The zone id is the tile id * 10 + the local id of the zone
                int occupantZoneId = gameState.board().lastPlacedTile().tile().id() * 10 + occupantLocalId;
                int occupantKindIndex = decodedAction >> OCCUPANT_KIND_SHIFT;
                Occupant.Kind occupantKind = Occupant.Kind.values()[occupantKindIndex];
                Occupant newOccupant = new Occupant(occupantKind, occupantZoneId);

                // Check if the occupant can be placed
                if (gameState.board().lastPlacedTile() == null
                        || !gameState.lastTilePotentialOccupants().contains(newOccupant))
                    throw new IllegalActionException();

                yield new StateAction(gameState.withNewOccupant(newOccupant), action);
            }
            case RETAKE_PAWN -> {
                // Check if the player doesn't want to retake an occupant
                if (action.equals(NO_OCCUPANT_ENCODED_ACTION))
                    yield new StateAction(gameState.withOccupantRemoved(null), action);

                // Decode the action
                List<Occupant> sortedOccupants = sortOccupants(gameState);
                Occupant pawnToRemove = sortedOccupants.get(decodedAction);

                // Check if the occupant can be removed
                PlacedTile placedTileWithPawn = gameState.board().tileWithId(Zone.tileId(pawnToRemove.zoneId()));
                if (action.length() != OCCUPANT_ENCODED_ACTION_LENGTH
                        || placedTileWithPawn.placer() != gameState.currentPlayer()
                        || placedTileWithPawn.occupant().equals(pawnToRemove))
                    throw new IllegalActionException();

                yield new StateAction(gameState.withOccupantRemoved(pawnToRemove), action);
            }
            default -> throw new IllegalActionException();
        };
    }

    /**
     * Represents a state action.
     * <p>
     * A state action is a pair of the game state resulting of the application of the given action and the encoded in
     * base 32 version of the action.
     *
     * @param gameState the game state
     * @param action    the encoded action
     * @author Maxence Espagnet (sciper: 372808)
     * @author Balthazar Baillat (sciper: 373420)
     */
    public record StateAction(GameState gameState, String action) {
    }

    /**
     * Represents an illegal action exception.
     *
     * @author Maxence Espagnet (sciper: 372808)
     * @author Balthazar Baillat (sciper: 373420)
     */
    public static class IllegalActionException extends Exception {

        /**
         * Constructs an illegal action exception.
         */
        public IllegalActionException() {
            super();
        }

        /**
         * Constructs an illegal action exception with a message.
         *
         * @param message the message
         */
        public IllegalActionException(String message) {
            super(message);
        }
    }


}
