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
        return new GameState(players, tileDecks, null,
                Board.EMPTY, Action.START_GAME, new MessageBoard(textMaker, List.of()));
    }

    /**
     * Returns the current player or null if there is none.
     *
     * @return the current player
     */
    public PlayerColor currentPlayer() {
        if (nextAction != Action.START_GAME && nextAction != Action.END_GAME)
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
        assert lastPlacedTile != null;
        // Calculate the potential occupants of the last placed tile
        Set<Occupant> potentialOccupants = lastPlacedTile.potentialOccupants();
        potentialOccupants.removeIf(occupant -> {
            Zone zone = lastPlacedTile.zoneWithId(occupant.zoneId());
            // If the player has no more occupants of the given kind, any occupant should be removed
            if (freeOccupantsCount(currentPlayer(), occupant.kind()) <= 0)
                return true;
            // Prevent the player from placing a pawn on a zone within an occupied area
            return switch (zone) {
                case Zone.Forest forest -> board.forestArea(forest).isOccupied();
                case Zone.River river when occupant.kind() == Occupant.Kind.PAWN -> board.riverArea(river).isOccupied();
                case Zone.Meadow meadow -> board.meadowArea(meadow).isOccupied();
                case Zone.Water water -> board.riverSystemArea(water).isOccupied();
            };
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
        PlacedTile startPlacedTile = new PlacedTile(tileDecks.topTile(Tile.Kind.START),
                null, Rotation.NONE, Pos.ORIGIN, null);
        // Draw the first tile from the start and normal tiles decks
        TileDecks updatedDecks = tileDecks
                .withTopTileDrawn(Tile.Kind.START).withTopTileDrawn(Tile.Kind.NORMAL);
        return new GameState(players, updatedDecks, tileDecks.topTile(Tile.Kind.NORMAL),
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
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE);
        Preconditions.checkArgument(placedTile.occupant() == null);

        Board boardWithTile = board.withNewTile(placedTile);
        // Compute the next game state by default
        GameState normalNextGameState = new GameState(players, tileDecks, null,
                boardWithTile, Action.OCCUPY_TILE, messageBoard)
                .withTurnFinishedIfOccupationImpossible();

        // Handle the MENHIR tiles special traits
        return switch (placedTile.specialPowerZone()) {
            /*
              The shaman tile allows the player to retake one of his pawns.
             */
            case Zone.Meadow meadow when meadow.specialPower() == Zone.SpecialPower.SHAMAN -> {
                if (board.occupantCount(currentPlayer(), Occupant.Kind.PAWN) >= 1)
                    yield new GameState(players, tileDecks, null, boardWithTile,
                            Action.RETAKE_PAWN, messageBoard);
                yield normalNextGameState;
            }
            /*
              The hunting trap tile allows the player to get the points corresponding
              to the animals present in the adjacent meadow.
             */
            case Zone.Meadow meadow when meadow.specialPower() == Zone.SpecialPower.HUNTING_TRAP -> {
                Area<Zone.Meadow> adjacentMeadows = boardWithTile.adjacentMeadow(placedTile.pos(),
                        (Zone.Meadow) placedTile.specialPowerZone());
                // Determine deer to cancel in the adjacent meadows
                Set<Animal> cancelledAnimals = computeCancelledAnimals(adjacentMeadows);
                Board updatedBoard = boardWithTile
                        .withMoreCancelledAnimals(Area.animals(adjacentMeadows, Set.of()));
                MessageBoard updatedMessageBoard = messageBoard
                        .withScoredHuntingTrap(currentPlayer(), adjacentMeadows, cancelledAnimals);
                yield new GameState(players, tileDecks, null, updatedBoard,
                        Action.OCCUPY_TILE, updatedMessageBoard).withTurnFinishedIfOccupationImpossible();
            }
            /*
              The logboat tile allows the player to obtain points that depend on the number
              of lakes in the hydrographic network containing it.
             */
            case Zone.Lake lake when lake.specialPower() == Zone.SpecialPower.LOGBOAT -> {
                Area<Zone.Water> area = boardWithTile.riverSystemArea((Zone.Water) placedTile.specialPowerZone());
                MessageBoard updatedMessageBoard = messageBoard.withScoredLogboat(currentPlayer(), area);
                yield new GameState(players, tileDecks, null, boardWithTile,
                        Action.OCCUPY_TILE, updatedMessageBoard).withTurnFinishedIfOccupationImpossible();
            }
            /*
              Default next game state
             */
            case null, default -> normalNextGameState;
        };
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
        Board updatedBoard = occupant == null ? board : board.withoutOccupant(occupant);
        return new GameState(players, tileDecks, null,
                updatedBoard, Action.OCCUPY_TILE, messageBoard).withTurnFinishedIfOccupationImpossible();
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
        GameState updatedState = new GameState(
                players, tileDecks, null, updatedBoard, nextAction, messageBoard);
        return updatedState.withTurnFinished();
    }

    /**
     * Returns a new game state identical to the receiver excepted the action is OCCUPY_TILE if the
     * occupation is possible or with calling withTurnFinished on it otherwise.
     *
     * @return a new game state identical to the receiver excepted the action is OCCUPY_TILE if the
     * occupation is possible or with calling withTurnFinished on it otherwise
     */
    private GameState withTurnFinishedIfOccupationImpossible() {
        if (lastTilePotentialOccupants().isEmpty())
            return withTurnFinished();
        return new GameState(players, tileDecks, null, board, Action.OCCUPY_TILE, messageBoard);
    }

    /**
     * Manages the end of the player turn.
     * <p>
     * - Attributes the points scored by the closed forests and rivers.<p>
     * - Determines whether the current player can play again.<p>
     * - Draws from the deck containing the next tile to place all the tiles which can't be placed.<p>
     * - Pass to the next player if the current one can't play again.<p>
     * - Ends the game when there is no more normal tiles to place.
     *
     * @return an updated game state
     */
    private GameState withTurnFinished() {
        assert board.lastPlacedTile() != null;
        Set<Area<Zone.Forest>> closedForests = board.forestsClosedByLastTile();
        Set<Area<Zone.River>> closedRivers = board.riversClosedByLastTile();
        Area<Zone.Forest> closedForestWithMenhir = null;
        // Updated game state data
        MessageBoard updatedMessageBoard = messageBoard;
        List<PlayerColor> updatedPlayers = new LinkedList<>(players);
        Board updatedBoard = board;
        // Attribute points scored by each closed forest
        for (Area<Zone.Forest> closedForest : closedForests) {
            updatedMessageBoard = updatedMessageBoard.withScoredForest(closedForest);
            // Determine if there is a closed forest with a menhir
            if (Area.hasMenhir(closedForest))
                closedForestWithMenhir = closedForest;
        }
        // Attribute points scored by each closed river
        for (Area<Zone.River> closedRiver : closedRivers) {
            updatedMessageBoard = updatedMessageBoard.withScoredRiver(closedRiver);
        }
        // Remove the occupants from the closed forests and rivers
        updatedBoard = updatedBoard.withoutGatherersOrFishersIn(closedForests, closedRivers);
        // The player can play again if he closed a forest containing a menhir with a normal tile
        boolean hasSecondTurn = closedForestWithMenhir != null
                && board.lastPlacedTile().kind() == Tile.Kind.NORMAL;

        // SECOND TURN CHECK
        TileDecks updatedMenhirDeck = hasSecondTurn ?
                tileDecks.withTopTileDrawnUntil(Tile.Kind.MENHIR, board::couldPlaceTile) : tileDecks;

        // Check if the player can place a menhir tile
        if (hasSecondTurn && updatedMenhirDeck.deckSize(Tile.Kind.MENHIR) > 0) {
            updatedMessageBoard = updatedMessageBoard
                    .withClosedForestWithMenhir(currentPlayer(), closedForestWithMenhir);
            return new GameState(updatedPlayers, updatedMenhirDeck.withTopTileDrawn(Tile.Kind.MENHIR),
                    updatedMenhirDeck.topTile(Tile.Kind.MENHIR),
                    updatedBoard, Action.PLACE_TILE, updatedMessageBoard);
        }

        // END GAME CHECK
        TileDecks updatedNormalDecks =
                updatedMenhirDeck.withTopTileDrawnUntil(Tile.Kind.NORMAL, board::couldPlaceTile);
        // Check if the player can place a normal tile
        if (updatedNormalDecks.deckSize(Tile.Kind.NORMAL) == 0) {
            return new GameState(updatedPlayers, updatedNormalDecks,
                    null, updatedBoard, Action.END_GAME, updatedMessageBoard).withFinalPointsCounted();
        }
        // Change current player
        Collections.rotate(updatedPlayers, -1);
        // NEXT TURN
        return new GameState(updatedPlayers, updatedNormalDecks.withTopTileDrawn(Tile.Kind.NORMAL),
                updatedNormalDecks.topTile(Tile.Kind.NORMAL),
                updatedBoard, Action.PLACE_TILE, updatedMessageBoard);
    }

    /**
     * Manages the attribution of the points at the end of the game.
     * <p>
     * Attributes the points given by : <p>
     * - the meadows, considering the presence of the wildfire and the pit trap.<p>
     * - the river systems, considering the presence of the raft.<p>
     * Determines the winners of the game.
     *
     * @return an updated game state
     */
    private GameState withFinalPointsCounted() {
        Board updatedBoard = board;
        MessageBoard updatedMessageBoard = messageBoard;
        // Add all points scored with meadow areas
        for (Area<Zone.Meadow> meadow : updatedBoard.meadowAreas()) {
            boolean containsWildFire = meadow.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE) != null;
            Zone.Meadow zoneWithPitTrap = (Zone.Meadow) meadow.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);
            if (zoneWithPitTrap != null) {
                PlacedTile tileWithPitTrap = updatedBoard.tileWithId(zoneWithPitTrap.tileId());
                if (!containsWildFire) {
                    updatedBoard = updatedBoard.withMoreCancelledAnimals(computeCancelledAnimalsWithPitTrap(meadow));
                }
                Area<Zone.Meadow> adjacentMeadows = updatedBoard.adjacentMeadow(tileWithPitTrap.pos(), zoneWithPitTrap);
                updatedMessageBoard =
                        updatedMessageBoard.withScoredPitTrap(adjacentMeadows, updatedBoard.cancelledAnimals());
            } else if (!containsWildFire) {
                updatedBoard = updatedBoard.withMoreCancelledAnimals(computeCancelledAnimals(meadow));
            }

            updatedMessageBoard = updatedMessageBoard.withScoredMeadow(meadow, updatedBoard.cancelledAnimals());
        }

        // Add all points scored with river systems
        for (Area<Zone.Water> riverSystem : board.riverSystemAreas()) {
            updatedMessageBoard = riverSystem.zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null
                    ? updatedMessageBoard.withScoredRaft(riverSystem).withScoredRiverSystem(riverSystem)
                    : updatedMessageBoard.withScoredRiverSystem(riverSystem);
        }

        // Determine the winners of the game
        Set<PlayerColor> winners = new HashSet<>();
        Map<PlayerColor, Integer> points = updatedMessageBoard.points();
        int maxPoints = !points.isEmpty() ? Collections.max(points.values()) : 0;
        // Add the players with the maximum number of points to the winners set
        points.entrySet().stream().filter(entry -> entry.getValue().equals(maxPoints))
                .forEach(entry -> winners.add(entry.getKey()));
        updatedMessageBoard = updatedMessageBoard.withWinners(winners, maxPoints);
        // Return a new game state with updated data
        return new GameState(players, tileDecks, null, updatedBoard,
                Action.END_GAME, updatedMessageBoard);
    }

    /**
     * Determine the animals to be cancelled from a given meadow area with a given number of tigers.
     * <p>
     * If the number of tigers provided is 0, take the number of tigers in the given area.
     *
     * @param meadowArea the meadow area
     * @param tigerCount the number of tigers that will eat deer
     * @return the set of animals to be cancelled
     */
    private Set<Animal> computeCancelledAnimals(Area<Zone.Meadow> meadowArea, int tigerCount) {
        Set<Animal> animals = Area.animals(meadowArea, Set.of());
        Set<Animal> deer = getAnimalsOfKind(animals, Animal.Kind.DEER);
        Set<Animal> tigers = getAnimalsOfKind(animals, Animal.Kind.TIGER);
        //
        tigerCount = tigerCount > 0 ? tigerCount : tigers.size();
        int deerToCancelCount = Math.min(tigerCount, deer.size());
        Set<Animal> cancelledAnimals = new HashSet<>(deerToCancelCount);
        // Cancel deer from outside the adjacent meadow in priority
        for (int i = 0; i < deerToCancelCount; ++i) {
            Animal removedDeer = deer.stream().findFirst().orElse(null);
            if (removedDeer != null) {
                deer.remove(removedDeer);
                cancelledAnimals.add(removedDeer);
            }
        }
        return cancelledAnimals;
    }

    /**
     * Determines the animals to be cancelled from a given meadow area.
     *
     * @param meadowArea the meadow area
     * @return the animals to be cancelled
     */
    private Set<Animal> computeCancelledAnimals(Area<Zone.Meadow> meadowArea) {
        return computeCancelledAnimals(meadowArea, 0);
    }

    /**
     * Determines the cancelled animals of a meadow which contains a pit trap.
     * <p>
     * When a meadow contains a pit trap, the deer which are out of the pit trap reach must be cancelled
     * in priority.
     *
     * @param meadow the meadow containing the pit trap
     * @return the set of cancelled animals
     */
    private Set<Animal> computeCancelledAnimalsWithPitTrap(Area<Zone.Meadow> meadow) {
        PlacedTile pitTrapTile = board.tileWithId(meadow.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP).tileId());
        // The area containing the adjacent meadows
        Area<Zone.Meadow> adjacentMeadowArea = board.adjacentMeadow(pitTrapTile.pos(),
                (Zone.Meadow) pitTrapTile.specialPowerZone());
        // The set of the meadow zones which are out of the pit trap reach
        Set<Zone.Meadow> outOfReachMeadowZones = new HashSet<>(meadow.zones());
        outOfReachMeadowZones.removeAll(adjacentMeadowArea.zones());
        // Create a new meadow area containing only the out-of-reach meadows
        Area<Zone.Meadow> outOfReachMeadowArea =
                new Area<>(outOfReachMeadowZones, meadow.occupants(), meadow.openConnections());
        // The total number of tigers
        int tigerCount = getAnimalsOfKind(Area.animals(meadow, Set.of()), Animal.Kind.TIGER).size();
        // Compute the cancelled animals which are out of the pit trap reach
        Set<Animal> cancelledAnimals = computeCancelledAnimals(outOfReachMeadowArea, tigerCount);
        // Subtract the tigers that have already eaten a deer
        tigerCount -= getAnimalsOfKind(Area.animals(outOfReachMeadowArea, Set.of()), Animal.Kind.DEER).size();
        // If there are some tigers left, add the remaining cancelled animals from the adjacent meadows
        if (tigerCount > 0)
            cancelledAnimals.addAll(computeCancelledAnimals(adjacentMeadowArea, tigerCount));
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
        END_GAME
    }
}