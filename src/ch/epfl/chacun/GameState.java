package ch.epfl.chacun;

import ch.epfl.chacun.GameState.Action;

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
     * The id of the tile containing the shaman
     */
    private static final int SHAMAN_ID = 88;

    /**
     * The id of the tile containing the hunting trap
     */
    private static final int HUNTING_TRAP_ID = 94;

    /**
     * The id of the tile containing the log boat
     */
    private static final int LOGBOAT_ID = 93;

    /**
     * The id of the tile containing the wildfire
     */
    private static final int WILD_FIRE_ID = 85;

    /**
     * The id of the tile containing the pit trap
     */
    private static final int PIT_TRAP_ID = 92;
    /**
     * The id of the tile containing the raft
     */
    private static final int RAFT_ID = 91;

    /**
     * Checks the validity of the arguments.
     * <p>
     * Checks that either the tile to be placed is null, or the next action is PLACE_TILE.
     * Checks that the tile decks, the board, the message board or the next action are not null.
     *
     * @throws IllegalArgumentException if invalid player number or next action
     * @throws NullPointerException     if null arguments
     */
    public GameState {
        Preconditions.checkArgument(players.size() >= 2);
        Preconditions.checkArgument(tileToPlace == null ^ nextAction == Action.PLACE_TILE);
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
     * @param players   the list of players
     * @param tileDecks the decks of tiles
     * @param textMaker the text maker
     * @return the initial state of the game
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME,
                new MessageBoard(textMaker, List.of()));
    }

    /**
     * Returns the current player or null if there is none.
     *
     * @return the current player
     */
    public PlayerColor currentPlayer() {
        if (nextAction() != Action.START_GAME || nextAction != Action.END_GAME)
            return players.getFirst();
        return null;
    }

    /**
     * Returns the number of occupants of a given player that can still be placed.
     *
     * @param player the player
     * @param kind   the occupant kind
     * @return the number of occupants of a given player that can still be placed
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Returns the set of all the potential occupants of the last placed tile.
     *
     * @return the set of all the potential occupants of the last placed tile
     * @throws IllegalArgumentException if the board is empty
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        Preconditions.checkArgument(!board.equals(Board.EMPTY));
        PlacedTile lastPlacedTile = board.lastPlacedTile();

        Set<Occupant> potentialOccupants = lastPlacedTile.potentialOccupants();
        potentialOccupants.removeIf(occupant -> {
            Zone zone = lastPlacedTile.zoneWithId(occupant.zoneId());
            return zone instanceof Zone.Forest forest && board.forestArea(forest).isOccupied()
                    || zone instanceof Zone.River river && board.riverArea(river).isOccupied()
                    || zone instanceof Zone.Meadow meadow && board.meadowArea(meadow).isOccupied()
                    || zone instanceof Zone.Lake lake && board.riverSystemArea(lake).isOccupied();
        });
        return potentialOccupants;
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
     * Manages all the transitions from PLACE_TILE.
     * <p>
     * Adds the given placed tile to the board, attributes the eventual points given by a log boat or a
     * hunting trap and determines the next action.
     *
     * @param placedTile the placed tile to add
     * @return a new game state with the specified modifications
     * @throws IllegalArgumentException if the given next action is not PLACE_TILE or if the given
     *                                  placed tile is already occupied
     */
    public GameState withPlacedTile(PlacedTile placedTile) {
        // Check the validity of the action
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE);
        // Check if the given tile is already occupied
        Preconditions.checkArgument(placedTile.occupant() == null);

        MessageBoard updatedMessageBoard = messageBoard;
        Board updatedBoard = board.withNewTile(placedTile);
        Action updatedAction = this.nextAction;
        // Check if the placed tile contains a SHAMAN, a hunting trap or a log boat
        switch (placedTile.tile().id()) {
            case SHAMAN_ID -> {
                // The next action is RETAKE_PAWN if the placed tile contains the shaman and if the player
                // has at least an occupant placed on the board
                if(this.board.occupantCount(currentPlayer(), Occupant.Kind.PAWN) >= 1) {
                    updatedAction = Action.RETAKE_PAWN;
                }
            }
            case HUNTING_TRAP_ID -> {
                // Determine the adjacent meadows of the placed tile
                Area<Zone.Meadow> adjacentMeadows = board.adjacentMeadow(placedTile.pos(),
                        (Zone.Meadow) placedTile.specialPowerZone());
                // Determine the animals to cancel in the adjacent meadows
                Set<Animal> cancelledAnimals = determineCancelledAnimals(adjacentMeadows, 0);
                // Update the board with more cancelled animals
                updatedBoard.withMoreCancelledAnimals(cancelledAnimals);
                // Add to the message board a message indicating that points was scored with a hunting trap
                updatedMessageBoard = updatedMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadows);
            }
            // Add to the message board a message indicating that points was scored with a log boat
            case LOGBOAT_ID -> updatedMessageBoard = updatedMessageBoard.withScoredLogboat(currentPlayer(),
                            board.riverSystemArea((Zone.Water) placedTile.specialPowerZone()));
        }
        // Draw the top tile from the normal tiles deck
        TileDecks updatedTileDecks = tileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
        // Get the next tile to place
        Tile nextTileToPlace = tileDecks.topTile(placedTile.kind());
        // Update the game state
        GameState updatedGameState = new GameState(players, updatedTileDecks, nextTileToPlace, updatedBoard,
                updatedAction, updatedMessageBoard);
        // Check if occupation is possible
        return updatedGameState.withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Manages all the transitions from RETAKE_PAWN.
     * <p>
     * Removes the given occupant from the board, unless it is null, which means the player does not want
     * to retake a pawn.
     *
     * @param occupant the occupant
     * @return a new game state with the specified modifications
     * @throws IllegalArgumentException if the next action is not RETAKE_PAWN or if the given occupant
     *                                  is null or not a pawn
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.RETAKE_PAWN);
        Preconditions.checkArgument(occupant == null || occupant.kind() == Occupant.Kind.PAWN);
        // Updated game state
        // Remove the occupant from the board if it is not null
        Board updatedBoard = occupant == null ? board : board.withoutOccupant(occupant);
        // If the occupant is null, the player can't do the action OCCUPY_TILE
        Action nextAction = occupant == null ? Action.PLACE_TILE : Action.OCCUPY_TILE;
        GameState updatedGameState = new GameState(players, tileDecks, null, updatedBoard,
                nextAction, messageBoard);
        // Check if the occupation is possible
        return updatedGameState.withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Manages all the transitions from OCCUPY_TILE.
     * <p>
     * Adds the given occupant to the last placed tile, unless it is null, which means that the player does
     * not want to place an occupant.
     *
     * @param occupant the occupant to add
     * @return a new game state with the specified modifications
     * @throws IllegalArgumentException if the next action is not OCCUPY_TILE
     */
    public GameState withNewOccupant(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.OCCUPY_TILE);
        Board updatedBoard = occupant == null ? board : board.withOccupant(occupant);
        return new GameState(
                players, tileDecks, null, updatedBoard, Action.PLACE_TILE, messageBoard);
    }

    /**
     * Returns a new game state identical to the receiver excepted the action is OCCUPY_TILE if the
     * occupation is possible or with calling withTurnFinished on it otherwise.
     *
     * @return a new game state identical to the receiver excepted the action is OCCUPY_TILE if the
     * occupation is possible or with calling withTurnFinished on it otherwise
     */
    private GameState withTurnFinishedIfOccupationImpossible() {
        if (this.lastTilePotentialOccupants().isEmpty()
                || this.freeOccupantsCount(this.currentPlayer(), Occupant.Kind.PAWN) == 0) {
            return this.withTurnFinished();
        }
        return new GameState(
                players, tileDecks, null, this.board, Action.OCCUPY_TILE, messageBoard);
    }

    /**
     * Manages the end of the player turn.
     * <p>
     * - Attributes the points scored by the closed forests and rivers.<p>
     * - Determines whether the current player can play again.<p>
     * - Draws from the deck containing the next tile to place all the tiles which can't be placed.<p>
     * - Pass to the next player if the current one can't play again.<p>
     * - Ends the game when the there is no more normal tiles to place.
     *
     * @return an updated game state
     */
    private GameState withTurnFinished() {
        // Determine the forests closed by last placed tile
        Set<Area<Zone.Forest>> closedForests = this.board.forestsClosedByLastTile();
        // Determine the rivers closed by last placed tile
        Set<Area<Zone.River>> closedRivers = this.board.riversClosedByLastTile();
        // Updated message board
        MessageBoard updatedMessageBoard = this.messageBoard;
        // Updated list of players
        List<PlayerColor> updatedPlayers = this.players;
        // Updated board
        Board updatedBoard = this.board;
        // Attribute points scored by each closed forests
        for (int i = 0; i < closedForests.size(); ++i) {
            updatedMessageBoard = updatedMessageBoard.withScoredForest(closedForests
                    .stream()
                    .toList()
                    .get(i));
        }
        // Attribute points scored by each closed rivers
        for (int i = 0; i < closedRivers.size(); ++i) {
            updatedMessageBoard = updatedMessageBoard.withScoredRiver(closedRivers
                    .stream()
                    .toList()
                    .get(i));
        }
        // Remove the occupants from the closed forests and rivers
        updatedBoard = updatedBoard.withoutGatherersOrFishersIn(closedForests, closedRivers);
        // The player can play again if he closed a forest containing a menhir with a normal tile
        boolean canPlayAgain = this.board.forestsClosedByLastTile().stream().anyMatch(Area::hasMenhir)
                && this.board.lastPlacedTile().kind() == Tile.Kind.NORMAL;
        // Determine the kind of the next tile
        Tile.Kind nextTileKind = canPlayAgain
                ? Tile.Kind.MENHIR
                : Tile.Kind.NORMAL;
        // Update the tile decks
        TileDecks updatedTileDecks = this.tileDecks;
        // If it is not already empty, draw from the deck containing the next tile to place all the
        // tiles which can't be placed
        if (this.tileDecks.deckSize(nextTileKind) > 0) {
            updatedTileDecks = this.tileDecks.withTopTileDrawnUntil(nextTileKind,
                    tile -> this.board.couldPlaceTile(this.tileDecks.topTile(nextTileKind)));
        }
        // Update the list of players
        PlayerColor currentPlayer = currentPlayer();
        // Pass to the next player if the current one can't play again
        if (!canPlayAgain || updatedTileDecks.deckSize(Tile.Kind.MENHIR) == 0) {
            // Remove the current player from first position
            updatedPlayers.remove(currentPlayer);
            // Add it to the end of the list
            updatedPlayers.add(currentPlayer);
        }
        // Determine the next action : END_GAME if the current player can't play again and if the normal
        // tiles deck is empty, PLACE_TILE otherwise
        Action nextAction = !canPlayAgain && tileDecks.normalTiles().isEmpty()
                ? Action.END_GAME
                : Action.PLACE_TILE;
        // Return a new game state with updated parameters
        return new GameState(updatedPlayers, updatedTileDecks, updatedTileDecks.topTile(nextTileKind),
                updatedBoard, nextAction, updatedMessageBoard);
    }

    /**
     * Manages the attribution of the points at the end of the game.
     * <p>
     * Attributes the points given by : <p>
     * - the meadows, considering the presence of the wildfire or the pit trap.<p>
     * - the river systems, considering the presence of the raft.<p>
     * Determines the winners of the game.
     *
     * @return an updated game state
     */
    private GameState withFinalPointsCounted() {
        Board updatedBoard = this.board;
        MessageBoard updatedMessageBoard = this.messageBoard;
        // Iterate over all the meadow areas
        for (Area<Zone.Meadow> meadow : this.board.meadowAreas()) {
            // Check that the meadow does not contain a wildfire
            if (!meadow.tileIds().contains(WILD_FIRE_ID)) {
                // Determine the cancelled animals of the meadow
                Set<Animal> cancelledAnimals = determineCancelledAnimals(meadow, 0);
                // Check if the meadow contains a pit trap
                if (meadow.tileIds().contains(PIT_TRAP_ID)) {
                    // Change the cancelled animals to optimize the points scored by the pit trap
                    cancelledAnimals = determineCancelledAnimalsWithPitTrap(meadow, this.board);
                }
                updatedMessageBoard = updatedMessageBoard.withScoredMeadow(meadow, cancelledAnimals);
                updatedBoard = updatedBoard.withMoreCancelledAnimals(cancelledAnimals);
            }
        }
        // Iterate over all the river system areas
        for (Area<Zone.Water> riverSystem : this.board.riverSystemAreas()) {
            // Update the message board withs points scored after checking the presence of the raft
            updatedMessageBoard = riverSystem.tileIds().contains(RAFT_ID)
                    ? updatedMessageBoard.withScoredRaft(riverSystem).withScoredRiverSystem(riverSystem)
                    : updatedMessageBoard.withScoredRiverSystem(riverSystem);
        }
        // The set of winners
        Set<PlayerColor> winners = new HashSet<>();
        // The map of the players and their points
        Map<PlayerColor, Integer> points = updatedMessageBoard.points();
        // The maximum amount of points scored by one or more players
        int maxPoints = Collections.max(points.values());
        // Iterate over the map entries to find the winner players
        for (Map.Entry<PlayerColor, Integer> entry : points.entrySet()) {
            if (Objects.equals(maxPoints, entry.getValue())) {
                winners.add(entry.getKey());
            }
        }
        updatedMessageBoard = updatedMessageBoard.withWinners(winners, maxPoints);
        // Return a new game state with updated data
        return new GameState(this.players, this.tileDecks, this.tileToPlace, updatedBoard,
                this.nextAction, updatedMessageBoard);
    }

    /**
     * Determine the animals to be cancelled from a given meadow area with a given number of tigers.
     * <p>
     * If the number of tigers provided is 0, take the number of tigers in the given area.
     *
     * @param meadowArea       the meadow area
     * @param specifiedTigerNb th number of tigers that will eat deer
     * @return the set of animals to be cancelled
     */
    private Set<Animal> determineCancelledAnimals(Area<Zone.Meadow> meadowArea, int specifiedTigerNb) {
        Set<Animal> animals = Area.animals(meadowArea, Set.of());
        // Find deer
        Set<Animal> deer = getAnimalsOfKind(animals, Animal.Kind.DEER);
        // Find tigers
        Set<Animal> tigers = getAnimalsOfKind(animals, Animal.Kind.TIGER);
        // If the specified number of tigers is 0, take the number of tigers of the given meadow area
        specifiedTigerNb = specifiedTigerNb >= 0 ? specifiedTigerNb : tigers.size();
        // Compute the number of deer to cancel
        int cancelledDeerNb = Math.min(specifiedTigerNb, deer.size());
        Set<Animal> cancelledAnimals = new HashSet<>();
        // Remove from the deer set and add to the cancelled animals set the correct number of deer
        for (int i = 0; i < cancelledDeerNb; ++i) {
            Animal removedDeer = deer.stream().findFirst().get();
            deer.remove(removedDeer);
            cancelledAnimals.add(removedDeer);
        }
        return cancelledAnimals;
    }

    /**
     * Determines the cancelled animals of a meadow which contains a pit trap.
     * <p>
     * When a meadow contains a pit trap, the deer which are out of the pit trap reach must be cancelled
     * in priority.
     *
     * @param meadow the meadow containing the pit trap
     * @param board  the current board
     * @return the set of cancelled animals
     */
    private Set<Animal> determineCancelledAnimalsWithPitTrap(Area<Zone.Meadow> meadow, Board board) {
        PlacedTile pitTrapTile = board.tileWithId(PIT_TRAP_ID);

        // The area containing the adjacent meadows
        Area<Zone.Meadow> adjacentMeadowArea = board.adjacentMeadow(pitTrapTile.pos(),
                (Zone.Meadow) pitTrapTile.specialPowerZone());
        // The set of the meadow zones which are out of the pit trap reach
        Set<Zone.Meadow> outOfReachMeadowZones = new HashSet<>(meadow.zones());
        // Remove from the set of all meadow zones the adjacent meadow zones
        outOfReachMeadowZones.removeAll(adjacentMeadowArea.zones());
        // Create a new meadow area containing only the out-of-reach meadows
        Area<Zone.Meadow> outOfReachMeadowArea =
                new Area<>(outOfReachMeadowZones, meadow.occupants(), meadow.openConnections());
        // The total number of tigers
        int tigerNb = getAnimalsOfKind(Area.animals(meadow, Set.of()), Animal.Kind.TIGER).size();
        // The cancelled animals which are out of the pit trap reach
        Set<Animal> cancelledAnimals = determineCancelledAnimals(outOfReachMeadowArea, tigerNb);
        // Subtract the number of deer which are out of the pit trap reach from the tiger number
        tigerNb -= getAnimalsOfKind(Area.animals(outOfReachMeadowArea, Set.of()), Animal.Kind.DEER).size();
        // Add the remaining cancelled animals from the adjacent meadows
        cancelledAnimals.addAll(determineCancelledAnimals(adjacentMeadowArea, tigerNb));

        return cancelledAnimals;
    }

    /**
     * Returns a set of animals of the given kind from a given set of possibly different animals.
     *
     * @param animals the set of different animals
     * @param kind    the kind of animal searched
     * @return a set of animals of the given kind
     */
    private Set<Animal> getAnimalsOfKind(Set<Animal> animals, Animal.Kind kind) {
        return animals.stream().filter(a -> a.kind() == kind).collect(Collectors.toSet());
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
