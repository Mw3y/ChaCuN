package ch.epfl.chacun;

import java.util.List;
import java.util.Set;

/**
 * Represents the complete state of the game.
 *
 * @param players      the list of all the players, in the order in which they have to play
 * @param tileDecks    the three decks of the remaining tiles
 * @param tileToPlace  the eventual tile which needs to be placed
 * @param board        the game board
 * @param nextAction   the next action to execute
 * @param messageBoard the message board containing the messages generated in the game
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record GameState(List<PlayerColor> players, TileDecks tileDecks, Tile tileToPlace, Board board,
                        Action nextAction, MessageBoard messageBoard) {

    public GameState {
        Preconditions.checkArgument(players.size() >= 2);
        Preconditions.checkArgument(tileToPlace != null && nextAction != Action.PLACE_TILE);
        Preconditions.checkArgument(tileDecks != null
                || board != null
                || nextAction != null
                || messageBoard != null);
        players = List.copyOf(players);
    }

    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME,
                new MessageBoard(textMaker, List.of()));
    }

    public PlayerColor currentPlayer() {
        if (nextAction() != Action.START_GAME || nextAction != Action.END_GAME) {
            return players.getFirst();
        } else return null;
    }

    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    public Set<Occupant> lastTilePotentialOccupants() {
        if (board == Board.EMPTY) {
            throw new IllegalArgumentException("The board is empty");
        }
        else return board.lastPlacedTile().potentialOccupants();
    }



    public enum Action {
        START_GAME,
        PLACE_TILE,
        RETAKE_PAWN,
        OCCUPY_TILE,
        END_GAME;
    }
}
