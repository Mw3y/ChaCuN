package ch.epfl.chacun;

import java.util.Arrays;
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
    public static final String NO_OCCUPANT_ENCODED_ACTION = Base32.encodeBits5(0x1F);

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
        List<Pos> sortedFringe = sortPos(gameState);
        // Encode the placed tile index then shift it of two positions to the left to merge the encoded rotation
        int fringeBits = sortedFringe.indexOf(placedTile.pos()) << 2;
        int rotationBits = placedTile.rotation().ordinal();
        return new StateAction(
                gameState.withPlacedTile(placedTile), Base32.encodeBits10(fringeBits + rotationBits));
    }

    /**
     * Sorts the positions of the given game state fringe in ascending order, first by their x-coordinate,
     * then by their y-coordinate.
     *
     * @param gameState the given game state
     * @return a list containing the sorted positions
     */
    private static List<Pos> sortPos(GameState gameState) {
        Comparator<Pos> comparator = Comparator.comparing(Pos::x).thenComparing(Pos::y);
        return gameState.board().insertionPositions().stream().sorted(comparator).toList();
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
            int kindBits = occupantToPlace.kind().ordinal() << 4;
            int zoneBits = gameState.board().lastPlacedTile().idOfZoneOccupiedBy(occupantToPlace.kind());
            return new StateAction(
                    gameState.withNewOccupant(occupantToPlace), Base32.encodeBits5(kindBits + zoneBits));
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
            List<Occupant> sortedOccupants = gameState.board().occupants().stream()
                    .sorted(Comparator.comparing(Occupant::zoneId)).toList();
            // Encode action
            int occupantIndex = sortedOccupants.indexOf(occupantToRemove);
            return new StateAction(
                    gameState.withOccupantRemoved(occupantToRemove), Base32.encodeBits5(occupantIndex));
        }
        return new StateAction(gameState.withOccupantRemoved(null), NO_OCCUPANT_ENCODED_ACTION);
    }

    public static StateAction decodeAndApply(GameState gameState, String action) {
        return null;
    }

    private static StateAction decodeAndApplySlave(GameState gameState, String action) {
        Preconditions.checkArgument(Base32.isValid(action) || action.isEmpty() || action.length() > 2);
        int decodedAction = Base32.decode(action);
        switch (gameState.nextAction()) {
            case PLACE_TILE -> {
                Rotation rotationToApply = Arrays.stream(Rotation.values()).toList().get(decodedAction & (1 << 2 - 1));
                Pos placedTilePosition = sortPos(gameState).get(decodedAction);
                PlacedTile placedTile = new PlacedTile(gameState.tileToPlace(), gameState.currentPlayer(),
                        rotationToApply, placedTilePosition);
                Preconditions.checkArgument(gameState.board().couldPlaceTile(placedTile.tile()));
                GameState updatedGameState = gameState.withPlacedTile(placedTile);
                return new StateAction(updatedGameState, action);
            }
            case OCCUPY_TILE -> {
                Occupant.Kind occupantKind = decodedAction >> 4 == 0 ? Occupant.Kind.PAWN : Occupant.Kind.HUT;
                int zoneID = decodedAction & (1 << 4 - 1);
                Occupant newOccupant = action.equals(NO_OCCUPANT_ENCODED_ACTION)
                        ? new Occupant(occupantKind, zoneID)
                        : null;
                GameState updatedGameState = gameState.withNewOccupant(newOccupant);
                return new StateAction(updatedGameState, action);
            }
        }
        return null;
    }

    /**
     * Represents a state action.
     * <p>
     * A state action is a pair of the game state resulting of the application of the given action and the encoded in
     * base 32 version of the action.
     *
     * @param gameState the game state
     * @param action    the encoded action
     */
    public record StateAction(GameState gameState, String action) {
    }


}
