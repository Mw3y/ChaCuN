package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

/**
 * The main class of the game.
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public class Main extends Application {

    /**
     * The initial width of the window.
     */
    private static final int INITIAL_WIDTH = 1440;

    /**
     * The initial height of the window.
     */
    private static final int INITIAL_HEIGHT = 1080;

    /**
     * The name of the game window.
     */
    private static final String WINDOW_NAME = "ChaCuN";

    /**
     * The main method of the game.
     * @param args the arguments to pass to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters params = getParameters();
        List<String> playerNames = params.getUnnamed();

        // Check if the number of players is valid
        Preconditions.checkArgument(playerNames.size() >= 2 && playerNames.size() <= 5);
        // Create the player map
        Map<PlayerColor, String> players = createPlayers(playerNames);

        // Apply the seed to ALL tiles
        String rawSeed = params.getNamed().get("seed");
        TileDecks tileDecks = createTileDecksWithSeed(rawSeed);

        TextMaker textMaker = new TextMakerFr(players);
        GameState initialGameState = GameState.initial(players.keySet().stream().toList(), tileDecks, textMaker);

        // Dynamic UI properties
        SimpleObjectProperty<Rotation> tileToPlaceRotationP = new SimpleObjectProperty<>(Rotation.NONE);
        SimpleObjectProperty<Set<Occupant>> visibleOccupantsP = new SimpleObjectProperty<>(Set.of());
        SimpleObjectProperty<Set<Integer>> highlightedTilesP = new SimpleObjectProperty<>(Set.of());
        SimpleObjectProperty<String> textToDisplayP = new SimpleObjectProperty<>("");
        SimpleObjectProperty<List<String>> actionsP = new SimpleObjectProperty<>(List.of());

        // Dynamic game state properties
        SimpleObjectProperty<GameState> gameStateO = new SimpleObjectProperty<>(initialGameState);
        ObservableValue<MessageBoard> messageBoardO = gameStateO.map(GameState::messageBoard);
        ObservableValue<GameState.Action> nextGameAction = gameStateO.map(GameState::nextAction);

        ObservableValue<Tile> tileToPlaceO = gameStateO.map(GameState::tileToPlace);
        ObservableValue<TileDecks> decksO = gameStateO.map(GameState::tileDecks);
        ObservableValue<Integer> normalTilesSizeO = decksO.map(gameDecks -> gameDecks.normalTiles().size());
        ObservableValue<Integer> menhirTilesSizeO = decksO.map(gameDecks -> gameDecks.menhirTiles().size());

        // Determine the text to display based on the next game action
        textToDisplayP.bind(nextGameAction.map(nextAction -> switch (nextAction) {
            case RETAKE_PAWN -> textMaker.clickToUnoccupy();
            case OCCUPY_TILE -> textMaker.clickToOccupy();
            default -> "";
        }));

        // Determine the occupants to display based on the next game action
        visibleOccupantsP.bind(nextGameAction.map(nextAction -> {
            GameState currentGameState = gameStateO.get();
            Board board = currentGameState.board();
            if (nextAction == GameState.Action.OCCUPY_TILE) {
                Set<Occupant> occupantsToDisplay = new HashSet<>(board.occupants());
                occupantsToDisplay.addAll(currentGameState.lastTilePotentialOccupants());
                return Set.copyOf(occupantsToDisplay);
            }
            return board.occupants();
        }));

        // Apply an action to the game state
        Consumer<String> applyAction = action -> {
            ActionEncoder.StateAction stateAction = ActionEncoder.decodeAndApply(gameStateO.get(), action);
            if (stateAction != null)
                applyStateAction(stateAction, gameStateO, actionsP);
        };

        // Apply a rotation to the tile to place
        Consumer<Rotation> applyRotation = rotation ->
                tileToPlaceRotationP.set(tileToPlaceRotationP.get().add(rotation));

        // Place a tile at a given position if possible
        Consumer<Pos> placeTileAtPos = pos -> {
            GameState state = gameStateO.get();
            if (state.tileToPlace() != null) {
                // The tile the current player is trying to place
                PlacedTile placedTile = new PlacedTile(
                        state.tileToPlace(), state.currentPlayer(), tileToPlaceRotationP.get(), pos);
                // Check if the tile can be placed
                if (state.board().canAddTile(placedTile)) {
                    ActionEncoder.StateAction stateAction = ActionEncoder.withPLacedTile(state, placedTile);
                    applyStateAction(stateAction, gameStateO, actionsP);
                    // Reset the tile to place rotation for the next player
                    tileToPlaceRotationP.set(Rotation.NONE);
                }
            }
        };

        // Place or remove an occupant on the board if possible
        Consumer<Occupant> selectOccupant = occupant -> {
            GameState gameState = gameStateO.get();
            switch (gameState.nextAction()) {
                // Normal tile has been placed
                case GameState.Action.OCCUPY_TILE  -> {
                    // Check if the player has clicked on a potential occupant and not one already on the board
                    if (!gameState.board().occupants().contains(occupant)) {
                        ActionEncoder.StateAction stateAction =
                                ActionEncoder.withNewOccupant(gameStateO.get(), occupant);
                        applyStateAction(stateAction, gameStateO, actionsP);
                    }
                }
                // Shaman tile has been placed
                case RETAKE_PAWN -> {
                    ActionEncoder.StateAction stateAction =
                            ActionEncoder.withOccupantRemoved(gameStateO.get(), occupant);
                    applyStateAction(stateAction, gameStateO, actionsP);
                }
            }
        };

        // Side panel UI
        Node playersUI = PlayersUI.create(gameStateO, textMaker);
        Node messageBoardUI = MessageBoardUI.create(messageBoardO.map(MessageBoard::messages), highlightedTilesP);
        Node decksUI = DecksUI.create(tileToPlaceO, normalTilesSizeO, menhirTilesSizeO, textToDisplayP, selectOccupant);
        Node actionsUI = ActionUI.create(actionsP, applyAction);
        // Put all elements into the side panel
        BorderPane sidePanel = new BorderPane(
                messageBoardUI, playersUI, null, new VBox(actionsUI, decksUI), null);

        // Create the board UI
        Node boardUI = BoardUI.create(Board.REACH, gameStateO, tileToPlaceRotationP, visibleOccupantsP,
                highlightedTilesP, applyRotation, placeTileAtPos, selectOccupant);

        // Put all UI elements into one window
        Scene scene = new Scene(new BorderPane(boardUI, null, sidePanel, null, null));

        // Scene settings
        primaryStage.setScene(scene);
        primaryStage.setWidth(INITIAL_WIDTH);
        primaryStage.setHeight(INITIAL_HEIGHT);
        primaryStage.setTitle(WINDOW_NAME);
        primaryStage.show();

        // After the UI has been rendered, start the game
        gameStateO.set(gameStateO.get().withStartingTilePlaced());
    }

    /**
     * Apply the state action to the game state and actions list.
     * @param stateAction the state action to apply
     * @param gameStateO the game state property
     * @param actionsP the actions list property
     */
    private void applyStateAction(ActionEncoder.StateAction stateAction,
                             SimpleObjectProperty<GameState> gameStateO, SimpleObjectProperty<List<String>> actionsP) {
        List<String> actions = new ArrayList<>(actionsP.get());
        actions.add(stateAction.action());
        actionsP.set(actions);
        gameStateO.set(stateAction.gameState());
    }

    /**
     * Maps each player color to a player name if any.
     * @param playerNames the list of player names
     * @return the map of player colors to player names
     */
    private Map<PlayerColor, String> createPlayers(List<String> playerNames) {
        List<PlayerColor> playerColors = PlayerColor.ALL.subList(0, playerNames.size());
        Map<PlayerColor, String> players = new EnumMap<>(PlayerColor.class);
        for (int i = 0; i < playerNames.size(); ++i) {
            players.put(playerColors.get(i), playerNames.get(i));
        }
        return Collections.unmodifiableMap(players);
    }

    /**
     * Creates the tile decks with a seed if any, or randomly picks one.
     * @param rawSeed the raw seed to use
     * @return the created tile decks
     */
    private TileDecks createTileDecksWithSeed(String rawSeed) {
        RandomGeneratorFactory<RandomGenerator> defaultRandomFactory = RandomGeneratorFactory.getDefault();

        RandomGenerator shuffler;
        if (rawSeed != null) {
            long seed = Long.parseUnsignedLong(rawSeed);
            shuffler = defaultRandomFactory.create(seed);
        } else {
            shuffler = defaultRandomFactory.create();
        }

        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, shuffler);
        // Group tiles by kind to create the decks
        return new TileDecks(tiles.stream().collect(Collectors.groupingBy(Tile::kind)));
    }
}
