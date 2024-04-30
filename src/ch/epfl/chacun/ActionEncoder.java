package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

public class ActionEncoder {

    public static StateAction withPLacedTile(GameState gameState, PlacedTile placedTile) {
        Comparator<Pos> comparator = Comparator.comparing(Pos::x);
        comparator = comparator.thenComparing(Pos::y);
        List<Pos> sortedFringe = gameState.board().insertionPositions()
                .stream().sorted(comparator).toList();

        String encodedAction = Base32.encodeBits10((sortedFringe.indexOf(placedTile.pos()) << 2)
                + placedTile.rotation().ordinal());
        System.out.println(encodedAction);
        return new StateAction(gameState.withPlacedTile(placedTile), encodedAction);
    }
    public record StateAction(GameState gameState, String action){
    }
}
