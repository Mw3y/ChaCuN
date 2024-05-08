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

    private final SimpleObjectProperty<List<String>> actionsP = new SimpleObjectProperty<>(List.of());
    private final SimpleObjectProperty<GameState> gameStateO = new SimpleObjectProperty<>();

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

        // Dynamic game state properties
        ObservableValue<MessageBoard> messageBoardO = gameStateO.map(GameState::messageBoard);
        ObservableValue<GameState.Action> nextGameAction = gameStateO.map(GameState::nextAction);

        // Initialize game state observer
        gameStateO.set(initialGameState);

        textToDisplayP.bind(nextGameAction.map(nextAction -> switch (nextAction) {
            case RETAKE_PAWN -> textMaker.clickToUnoccupy();
            case OCCUPY_TILE -> textMaker.clickToOccupy();
            default -> "";
        }));

        visibleOccupantsP.bind(nextGameAction.map(nextAction -> {
            GameState currentGameState = gameStateO.get();
            Board board = currentGameState.board();
            return switch (nextAction) {
                case OCCUPY_TILE -> {
                    Set<Occupant> occupantsToDisplay = new HashSet<>(board.occupants());
                    occupantsToDisplay.addAll(currentGameState.lastTilePotentialOccupants());
                    yield Set.copyOf(occupantsToDisplay);
                }
                case RETAKE_PAWN -> board.occupants().stream().filter(occupant -> {
                    PlacedTile tile = board.tileWithId(Zone.tileId(occupant.zoneId()));
                    return tile.placer() == currentGameState.currentPlayer();
                }).collect(Collectors.toSet());
                default -> gameStateO.get().board().occupants();
            };
        }));

        Consumer<Rotation> applyRotation = rotation -> {
            tileToPlaceRotationP.set(tileToPlaceRotationP.get().add(rotation));
        };

        Consumer<Pos> placeTileAtPos = pos -> {
            GameState state = gameStateO.get();
            if (state.tileToPlace() != null) {
                PlacedTile placedTile = new PlacedTile(
                        state.tileToPlace(), state.currentPlayer(), tileToPlaceRotationP.get(), pos);

                if (state.board().canAddTile(placedTile)) {
                    ActionEncoder.StateAction stateAction = ActionEncoder.withPLacedTile(state, placedTile);
                    registerActionToGameState(stateAction);
                    tileToPlaceRotationP.set(Rotation.NONE);
                }
            }
        };

        Consumer<Occupant> selectOccupant = occupant -> {
            if (gameStateO.get().nextAction() == GameState.Action.OCCUPY_TILE
                    && !gameStateO.get().board().occupants().contains(occupant)) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withNewOccupant(gameStateO.get(), occupant);
                registerActionToGameState(stateAction);
            } else if (gameStateO.get().nextAction() == GameState.Action.RETAKE_PAWN) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withOccupantRemoved(gameStateO.get(), occupant);
                registerActionToGameState(stateAction);
            }
        };

        Consumer<String> applyAction = action -> {
            ActionEncoder.StateAction stateAction = ActionEncoder.decodeAndApply(gameStateO.get(), action);
            if (stateAction != null) {
                List<String> previousActions = actionsP.get();
                List<String> newActions = new ArrayList<>(previousActions);
                newActions.add(action);
                actionsP.set(newActions);

                gameStateO.set(stateAction.gameState());
            }
        };


        Node boardUI = BoardUI.create(Board.REACH, gameStateO, tileToPlaceRotationP, visibleOccupantsP, highlightedTilesP, applyRotation, placeTileAtPos, selectOccupant);

        Node playersUI = PlayersUI.create(gameStateO, textMaker);
        Node messageBoardUI = MessageBoardUI.create(messageBoardO.map(MessageBoard::messages), highlightedTilesP);

        ObservableValue<Tile> tileToPlaceO = gameStateO.map(GameState::tileToPlace);
        ObservableValue<TileDecks> decksO = gameStateO.map(GameState::tileDecks);
        ObservableValue<Integer> normalTilesSizeO = decksO.map(gameDecks -> gameDecks.normalTiles().size());
        ObservableValue<Integer> menhirTilesSizeO = decksO.map(gameDecks -> gameDecks.menhirTiles().size());
        Node decksUI = DecksUI.create(tileToPlaceO, normalTilesSizeO, menhirTilesSizeO, textToDisplayP, selectOccupant);

        Node actionsUI = ActionsUI.create(actionsP, applyAction);

        BorderPane sidePanel = new BorderPane(
                messageBoardUI, playersUI, null, new VBox(actionsUI, decksUI), null);
        Scene scene = new Scene(new BorderPane(boardUI, null, sidePanel, null, null));

        primaryStage.setScene(scene);
        primaryStage.setWidth(INITIAL_WIDTH);
        primaryStage.setHeight(INITIAL_HEIGHT);
        primaryStage.setTitle("ChaCuN");
        primaryStage.show();

        gameStateO.set(gameStateO.get().withStartingTilePlaced());
    }

    private void registerActionToGameState(ActionEncoder.StateAction stateAction) {
        List<String> actions = new ArrayList<>(actionsP.get());
        actions.add(stateAction.action());
        actionsP.set(actions);
        gameStateO.set(stateAction.gameState());
    }

    private Map<PlayerColor, String> createPlayers(List<String> playerNames) {
        List<PlayerColor> playerColors = PlayerColor.ALL.subList(0, playerNames.size());
        Map<PlayerColor, String> players = new HashMap<>();
        for (int i = 0; i < playerNames.size(); ++i) {
            players.put(playerColors.get(i), playerNames.get(i));
        }
        return Collections.unmodifiableMap(players);
    }

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
        // for (int i = 79; i <= 94; ++i) {
        //     if (i != 88)
        //         tiles.remove(Tiles.TILES.get(i));
        /// }
        Collections.shuffle(tiles, shuffler);
        // Group tiles by kind to create the decks
        return new TileDecks(tiles.stream().collect(Collectors.groupingBy(Tile::kind)));
    }
}
