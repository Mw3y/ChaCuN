package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

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
    /**
     * Checks the validity of the arguments.
     * <p>
     * Checks that there is at least two players and that either the tile to be placed is null,
     * or the next action is PLACE_TILE, throws {@link IllegalArgumentException} if not.
     * Checks that the tile decks, the board, the message board or the next action are not null or throws
     * {@link NullPointerException} if null.
     * @throws IllegalArgumentException if invalid player number or next action
     * @throws NullPointerException if null arguments
     */
    public GameState {
        Preconditions.checkArgument(players.size() >= 2);
        Preconditions.checkArgument(tileToPlace != null && nextAction != Action.PLACE_TILE);
        Objects.requireNonNull(tileDecks);
        Objects.requireNonNull(board);
        Objects.requireNonNull(messageBoard);
        Objects.requireNonNull(nextAction);
        players = List.copyOf(players);
    }

    /**
     * Returns the initial state of the game with START_GAME as next action and with empty board and
     * message board.
     *
     * @param players the list of players
     * @param tileDecks the decks of tiles
     * @param textMaker the text maker
     * @return the initial state of the game
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME,
                new MessageBoard(textMaker, List.of()));
    }

    /**
     * Returns the current player or null if there is no.
     *
     * @return the current player
     */
    public PlayerColor currentPlayer() {
        if (nextAction() != Action.START_GAME || nextAction != Action.END_GAME) {
            return players.getFirst();
        }
        return null;
    }

    /**
     * Returns the number of occupants of a given player that can still be placed.
     *
     * @param player the player
     * @param kind the occupant kind
     * @return the number of occupants of a given player that can still be placed
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Returns the set of all the potential occupants of the last placed tile or throws
     * {@link IllegalArgumentException} if the board is empty.
     *
     * @return the set of all the potential occupants of the last placed tile
     * @throws IllegalArgumentException if the board is empty
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        Preconditions.checkArgument(!board.equals(Board.EMPTY));
        PlacedTile lastPlacedTile = board.lastPlacedTile();

        if (lastPlacedTile == null) {
            return Set.of();
        }

        Set<Occupant> possibleOccupants = new HashSet<>();
        for (Occupant occupant : lastPlacedTile.potentialOccupants()) {
            Zone zone = lastPlacedTile.zoneWithId(occupant.zoneId());
            switch (zone) {
                case Zone.Forest f when board.forestArea(f).occupants().isEmpty() ->
                        possibleOccupants.add(occupant);
                case Zone.River r when board.riverArea(r).occupants().isEmpty() ->
                        possibleOccupants.add(occupant);
                case Zone.Meadow m when board.meadowArea(m).occupants().isEmpty() ->
                        possibleOccupants.add(occupant);
                case Zone.Lake l when board.riverSystemArea(l).occupants().isEmpty() ->
                        possibleOccupants.add(occupant);
                default -> {}
            }
        }

        return possibleOccupants;
    }

    /**
     * Manages the transition from START_GAME to PLACE_TILE.
     * <p>
     * Places the starting tile in the center of the board and draws the first tile from the pile of normal
     * tiles, which becomes the tile to be played.
     * Checks if the next action is START_GAME.
     *
     * @return a new game state with the start tile placed
     * @throws IllegalArgumentException if the next action is not START_GAME
     */
    public GameState withStartingTilePlaced() {
        Preconditions.checkArgument(nextAction == Action.START_GAME);
        PlacedTile startPlacedTile = new PlacedTile(tileDecks.topTile(Tile.Kind.START), null,
                Rotation.NONE, Pos.ORIGIN, null);

        return new GameState(
                players, tileDecks.withTopTileDrawn(Tile.Kind.START), tileDecks.topTile(Tile.Kind.NORMAL),
                board.withNewTile(startPlacedTile), Action.PLACE_TILE, messageBoard);
    }

    /**
     * Determine the animals to be cancelled from a given meadow area.
     *
     * @param meadowArea the meadow area
     * @return the set of animals to be cancelled
     */
    private Set<Animal> determineCancelledAnimals(Area<Zone.Meadow> meadowArea) {
        // Find the animals present in the adjacent meadows of placed tile
        Set<Animal> animals = Area.animals(meadowArea, Set.of());
        // Find deers
        Set<Animal> deers = animals.stream()
                .filter(a -> a.kind() == Animal.Kind.DEER)
                .collect(Collectors.toSet());
        // Find tigers
        Set<Animal> tigers = animals.stream()
                .filter(a -> a.kind() == Animal.Kind.TIGER)
                .collect(Collectors.toSet());
        // Compute the number of deers to cancel
        int cancelledDeersNb = Math.min(tigers.size(), deers.size());

        Set<Animal> cancelledAnimals = new HashSet<>();
        // Remove from the deers set and add to the cancelled animals set the correct number of deers
        for(int i = 0; i < cancelledDeersNb; ++i) {
            Animal removedDeer = deers.stream().findFirst().get();
            deers.remove(removedDeer);
            cancelledAnimals.add(removedDeer);
        }
        return cancelledAnimals;
    }

    /**
     * Manage
     * @param placedTile
     * @return
     */
    public GameState withPlacedTile(PlacedTile placedTile) {
        // Check the validity of the action or if the given tile is already occupied
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE
                || placedTile.occupant() == null);
        MessageBoard newMessageBoard = messageBoard;
        // The id of the tile containing the shaman
        int SHAMAN_ID = 88;
        // The id of the tile containing the pit trap
        int PIT_TRAP_ID = 92;

        int LOGBOAT_ID = 93;
        // Determine the nextAction
        Action nextAction = placedTile.tile().id() == SHAMAN_ID
                ? Action.RETAKE_PAWN
                : Action.PLACE_TILE;

        if(placedTile.tile().id() == PIT_TRAP_ID) {
            // Determine the adjacent meadows of the placed tile
            Area<Zone.Meadow> adjacentMeadows = board.adjacentMeadow(placedTile.pos(),
                    (Zone.Meadow) placedTile.specialPowerZone());
            Set<Animal> cancelledAnimals = determineCancelledAnimals(placedTile, adjacentMeadows);
            newMessageBoard = newMessageBoard.withScoredPitTrap(adjacentMeadows, cancelledAnimals);
        }
        if(placedTile.tile().id() == LOGBOAT_ID) {
            newMessageBoard = newMessageBoard.withScoredLogboat(currentPlayer(),
                    board.riverSystemArea((Zone.Water) placedTile.specialPowerZone()));
        }
        return new GameState(players, tileDecks.withTopTileDrawn(Tile.Kind.NORMAL),
                tileDecks.topTile(placedTile.kind()), board.withNewTile(placedTile),
                nextAction, newMessageBoard);
    }

    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.RETAKE_PAWN);
        Preconditions.checkArgument(occupant == null || occupant.kind() == Occupant.Kind.PAWN);
        // Updated game state
        Board updatedBoard = occupant == null ? board : board.withoutOccupant(occupant);
        Action nextAction = occupant == null ? Action.PLACE_TILE : Action.OCCUPY_TILE;
        return new GameState(players, tileDecks, null, updatedBoard, nextAction, messageBoard);
    }

    public GameState withNewOccupant(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.OCCUPY_TILE);
        Board updatedBoard = occupant == null ? board : board.withOccupant(occupant);
        return new GameState(
                players, tileDecks, null, updatedBoard, Action.PLACE_TILE, messageBoard);
    }

    /**
     * Represents an action that must be carried out for the game to progress.
     */
    public enum Action {
        /**
         * The starting tile must be placed in the center of the board, and the tile at the top of the
         * normal tile pile must be turned over to be placed by the first player.
         */
        START_GAME,
        /**
         * The current player must place the current tile, which is either a normal tile or a menhir tile.
         */
        PLACE_TILE,
        /**
         * The current player - who has just placed the tile containing the shaman - must decide whether he
         * wishes to take back one of the counters he previously placed on the board, and if so, which one.
         */
        RETAKE_PAWN,
        /**
         * The current player - who has just placed a tile - must decide whether he wishes to occupy
         * one of his zones using one of the occupants in his hand.
         */
        OCCUPY_TILE,
        /**
         * Points must be counted and the winner(s) announced, as the last player has completed
         * his turn(s) and the pile of normal tiles is empty.
         */
        END_GAME;
    }
}
