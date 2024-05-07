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
                    // Add action
                    List<String> actions = new ArrayList<>(actionsP.get());
                    actions.add(stateAction.action());
                    actionsP.set(actions);
                    // Register action
                    gameStateO.set(stateAction.gameState());
                    tileToPlaceRotationP.set(Rotation.NONE);

                    if (gameStateO.get().nextAction() == GameState.Action.OCCUPY_TILE) {
                        // Display potential occupants
                        Set<Occupant> occupantsToDisplay = new HashSet<>(visibleOccupantsP.get());
                        occupantsToDisplay.addAll(gameStateO.get().lastTilePotentialOccupants());
                        visibleOccupantsP.set(Set.copyOf(occupantsToDisplay));
                        textToDisplayP.set(textMaker.clickToOccupy());
                    }
                }
            }
        };

        Consumer<Occupant> selectOccupant = occupant -> {
            if (!gameStateO.get().board().occupants().contains(occupant)) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withNewOccupant(gameStateO.get(), occupant);
                // Add action
                List<String> actions = new ArrayList<>(actionsP.get());
                actions.add(stateAction.action());
                actionsP.set(actions);
                // Register action
                gameStateO.set(stateAction.gameState());
                // Reset properties
                visibleOccupantsP.set(gameStateO.get().board().occupants());
                textToDisplayP.set("");
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

                switch (stateAction.gameState().nextAction()) {
                    case PLACE_TILE -> {
                        visibleOccupantsP.set(stateAction.gameState().board().occupants());
                        textToDisplayP.set("");
                    }
                    case OCCUPY_TILE -> {
                        Set<Occupant> occupantsToDisplay = new HashSet<>(visibleOccupantsP.get());
                        occupantsToDisplay.addAll(gameStateO.get().lastTilePotentialOccupants());
                        visibleOccupantsP.set(Set.copyOf(occupantsToDisplay));
                        textToDisplayP.set(textMaker.clickToOccupy());
                    }
                    case RETAKE_PAWN -> {
                        // display player's occupants
                        textToDisplayP.set(textMaker.clickToUnoccupy());
                    }
                }
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
        Collections.shuffle(tiles, shuffler);
        // Group tiles by kind to create the decks
        return new TileDecks(tiles.stream().collect(Collectors.groupingBy(Tile::kind)));
    }
}
