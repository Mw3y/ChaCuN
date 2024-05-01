package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class to encode the game actions.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public class ActionEncoder {
    /**
     * Manages the encoding of the action of placing a tile.
     * <p>
     * Sorts the fringe and encodes the index of the placedTile in the fringe and the applied rotation in a
     * 10-bit integer.
     * Updates the game state using the withPlacedTile method of GameState.
     *
     * @param gameState the given game state
     * @param placedTile the placed tile
     * @return a new state action with the updated game state and the encoded action
     */
    public static StateAction withPLacedTile(GameState gameState, PlacedTile placedTile) {
        // Sort the positions in ascending order, first by their x-coordinate,
        // then by their y-coordinate
        Comparator<Pos> comparator = Comparator.comparing(Pos::x);
        comparator = comparator.thenComparing(Pos::y);
        List<Pos> sortedFringe = gameState.board().insertionPositions()
                .stream().sorted(comparator).toList();
        // Encode the placed tile index then shift it of two positions to the left to merge the encoded rotation
        String encodedAction = Base32.encodeBits10((sortedFringe.indexOf(placedTile.pos()) << 2)
                + placedTile.rotation().ordinal());

        return new StateAction(gameState.withPlacedTile(placedTile), encodedAction);
    }

    /**
     * Represents a state action.
     * <p>
     * A state action is a pair of the game state resulting of the application of the given action and the encoded in
     * base 32 version of the action.
     *
     * @param gameState the game state
     * @param action the encoded action
     */
    public record StateAction(GameState gameState, String action){
    }


}
