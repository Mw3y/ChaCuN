package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChaCuNUITest extends Application {
    private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new),
            list -> {
                Collections.shuffle(list);
                return list;
            }
    );

    public static void main(String[] args) {
        launch(args);
    }

    private static GameState initialGameState(Map<PlayerColor, String> players,
                                              List<Integer> firstNormalTiles,
                                              List<Integer> firstMenhirTiles) {
        var tileDecks = tileDecks(firstNormalTiles, firstMenhirTiles);

        var startingTile = tileDecks.topTile(Tile.Kind.START);
        var firstTileToPlace = tileDecks.topTile(Tile.Kind.NORMAL);
        var tileDecks1 = tileDecks
                .withTopTileDrawn(Tile.Kind.START)
                .withTopTileDrawn(Tile.Kind.NORMAL);
        var placedStartingTile = new PlacedTile(startingTile, null, Rotation.NONE, Pos.ORIGIN);
        var board = Board.EMPTY.withNewTile(placedStartingTile);
        var messageBoard = new MessageBoard(new TextMakerFr(players), List.of());
        return new GameState(new ArrayList<>(players.keySet()).stream().sorted().toList(), tileDecks1, firstTileToPlace, board, GameState.Action.PLACE_TILE, messageBoard);
    }

    private static GameState truncateDeck(GameState state, Tile.Kind deckKind, int deckSize) {
        var decks1 = switch (state.tileDecks()) {
            case TileDecks(List<Tile> s, List<Tile> n, List<Tile> m) when deckKind == Tile.Kind.NORMAL ->
                    new TileDecks(s, n.subList(0, deckSize), m);
            case TileDecks(List<Tile> s, List<Tile> n, List<Tile> m) when deckKind == Tile.Kind.MENHIR ->
                    new TileDecks(s, n, m.subList(0, deckSize));
            default -> throw new Error("cannot truncate that deck");
        };
        return new GameState(
                state.players(),
                decks1,
                state.tileToPlace(),
                state.board(),
                state.nextAction(),
                state.messageBoard());
    }

    private static TileDecks shuffledTileDecks() {
        return shuffledTileDecks(2024);
    }

    private static TileDecks shuffledTileDecks(long shufflingSeed) {
        var tileDecks = tileDecks(List.of(), List.of());
        var random = new Random(shufflingSeed);

        var shuffledNormalTiles = new ArrayList<>(tileDecks.normalTiles());
        Collections.shuffle(shuffledNormalTiles, random);

        var shuffledMenhirTiles = new ArrayList<>(tileDecks.menhirTiles());
        Collections.shuffle(shuffledMenhirTiles, random);

        return new TileDecks(
                tileDecks.startTiles(),
                List.copyOf(shuffledNormalTiles),
                List.copyOf(shuffledMenhirTiles));
    }

    private static TileDecks tileDecks(List<Integer> firstNormalTiles, List<Integer> firstMenhirTiles) {
        var partitionedTiles = Tiles.TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));

        var normalTiles = moveTilesToFront(partitionedTiles.get(Tile.Kind.NORMAL), firstNormalTiles);
        var menhirTiles = moveTilesToFront(partitionedTiles.get(Tile.Kind.MENHIR), firstMenhirTiles);

        return new TileDecks(
                List.copyOf(partitionedTiles.get(Tile.Kind.START)),
                List.copyOf(normalTiles),
                List.copyOf(menhirTiles));
    }

    private static List<Tile> moveTilesToFront(List<Tile> tiles, List<Integer> tileIds) {
        var reorderedTiles = new ArrayList<Tile>(Collections.nCopies(tileIds.size(), null));
        for (var t : tiles) {
            var i = tileIds.indexOf(t.id());
            if (i == -1)
                reorderedTiles.add(t);
            else {
                var oldTile = reorderedTiles.set(i, t);
                assert oldTile == null;
            }
        }
        return List.copyOf(reorderedTiles);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, List<T>> toShuffledList() {
        return (Collector<T, ?, List<T>>) SHUFFLER;
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        var playerNames = Map.of(
                PlayerColor.GREEN, "Balthazar", PlayerColor.YELLOW, "Max"
        );

        var tileToPlaceRotationP =
                new SimpleObjectProperty<>(Rotation.NONE);
        var visibleOccupantsP =
                new SimpleObjectProperty<>(Set.<Occupant>of());
        var highlightedTilesP =
                new SimpleObjectProperty<>(Set.<Integer>of());

        var textMaker = new TextMakerFr(playerNames);
        var positions = Map.ofEntries(
                Map.entry(34, new Pos(-3, -1)),
                Map.entry(67, new Pos(-2, -1)),
                Map.entry(31, new Pos(-1, -1)),
                Map.entry(61, new Pos(0, -1)),
                Map.entry(62, new Pos(-3, 0)),
                Map.entry(18, new Pos(-2, 0)),
                Map.entry(51, new Pos(-1, 0)),
                Map.entry(1, new Pos(-3, 1)),
                Map.entry(3, new Pos(-2, 1)),
                Map.entry(49, new Pos(-1, 1)),
                Map.entry(55, new Pos(0, 1)));

        var occupants = Map.of(
                61, new Occupant(Occupant.Kind.PAWN, 61_0), // hunter (RED)
                55, new Occupant(Occupant.Kind.PAWN, 55_3), // fisher (BLUE)
                51, new Occupant(Occupant.Kind.PAWN, 51_1), // fisher (GREEN)
                18, new Occupant(Occupant.Kind.PAWN, 18_2), // hunter (YELLOW)
                1, new Occupant(Occupant.Kind.HUT, 1_8), // fisher's hut (RED)
                34, new Occupant(Occupant.Kind.PAWN, 34_1), // hunter (BLUE)
                3, new Occupant(Occupant.Kind.PAWN, 3_5), // fisher (PURPLE)
                49, new Occupant(Occupant.Kind.PAWN, 49_2) // hunter (RED)
        );

        var normalTilesIds = Tiles.TILES.stream().filter(tile -> tile.kind() == Tile.Kind.NORMAL)
                .map(Tile::id).collect(toShuffledList());
        var menhirTilesIds = Tiles.TILES.stream().filter(tile -> tile.kind() == Tile.Kind.MENHIR)
                .map(Tile::id).collect(toShuffledList());
        var state = initialGameState(playerNames, normalTilesIds, menhirTilesIds);
        var gameStateO = new SimpleObjectProperty<>(state);
        var unoccupyableTiles = Set.of(62);
        Map<Integer, Rotation> rotations = Map.of();
        var playersNode = PlayersUI.create(gameStateO, textMaker);
        var messagesNode = MessageBoardUI.create(gameStateO.map(g -> g.messageBoard().messages()), highlightedTilesP);

        Consumer<String> actionToApply = (action) -> {

        };

        SimpleObjectProperty<List<String>> actions = new SimpleObjectProperty<>(List.of());
        var actionsUI = ActionUI.create(actions, actionToApply);
        var decksNode = DecksUI.create(gameStateO.map(GameState::tileToPlace), gameStateO.map(g -> g.tileDecks().normalTiles().size()), gameStateO.map(g -> g.tileDecks().menhirTiles().size()), new SimpleObjectProperty<>(""), o -> {
        });
        var boardNode = (ScrollPane) BoardUI
                .create(Board.REACH,
                        gameStateO,
                        tileToPlaceRotationP,
                        visibleOccupantsP,
                        highlightedTilesP,
                        tileToPlaceRotationP::set,
                        t -> {
                            GameState gameState = gameStateO.getValue();
                            PlacedTile tileToPlace = new PlacedTile(gameState.tileToPlace(), gameState.currentPlayer(), tileToPlaceRotationP.getValue(), t);
                            if (gameState.board().canAddTile(tileToPlace)) {
                                ActionEncoder.StateAction stateAction = ActionEncoder.withPLacedTile(gameState, tileToPlace);
                                gameStateO.set(stateAction.gameState());

                                List<String> actionsList = new ArrayList<>(actions.get());
                                actionsList.add(stateAction.action());
                                actions.set(actionsList);

                                tileToPlaceRotationP.set(Rotation.NONE);
                                Set<Occupant> visibleOccupants = new HashSet<>(gameState.board().occupants());
                                visibleOccupants.addAll(gameStateO.get().lastTilePotentialOccupants());
                                visibleOccupantsP.set(visibleOccupants);
                            }
                        },
                        o -> {
                            ActionEncoder.StateAction stateAction = ActionEncoder.withNewOccupant(gameStateO.getValue(), o);
                            gameStateO.set(stateAction.gameState());
                            visibleOccupantsP.set(gameStateO.get().board().occupants());
                            List<String> actionsList = new ArrayList<>(actions.get());
                            actionsList.add(stateAction.action());
                            actions.set(actionsList);
                        });

        var sideBar = new VBox(playersNode, messagesNode, actionsUI, decksNode);
        VBox.setVgrow(messagesNode, Priority.ALWAYS);

        boardNode.setFitToHeight(true);
        boardNode.setFitToWidth(true);
        boardNode.setMinWidth(720);
        boardNode.maxHeightProperty().bind(primaryStage.heightProperty());
        boardNode.maxWidthProperty().bind(primaryStage.widthProperty().map(w -> w.doubleValue() - sideBar.getWidth()));
        boardNode.setHvalue(.5);
        boardNode.setVvalue(.5);

        var rootNode = new HBox(boardNode, sideBar);
        HBox.setHgrow(boardNode, Priority.ALWAYS);
        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();

        gameStateO.setValue(truncateDeck(gameStateO.getValue(), Tile.Kind.NORMAL, normalTilesIds.size() - 1));

        var players = gameStateO.getValue().players();
        var playersIt = Stream.generate(() -> players)
                .flatMap(Collection::stream)
                .iterator();

        var nextPlacedTile = (Function<GameState, PlacedTile>) s -> {
            var t = s.tileToPlace();
            var r = rotations.getOrDefault(t.id(), Rotation.NONE);
            return new PlacedTile(t, playersIt.next(), r, positions.get(t.id()));
        };

        // Place all tiles
        int timelinePace = 1;
        Timeline timeline = new Timeline();
        for (int i = 0; i < 0; i += 1) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds((i + 1) * timelinePace), _ -> {
                var placedTile = nextPlacedTile.apply(gameStateO.getValue());
                gameStateO.setValue(gameStateO.getValue().withPlacedTile(placedTile));
                if (!unoccupyableTiles.contains(placedTile.id())) {
                    gameStateO.setValue(gameStateO.getValue().withNewOccupant(occupants.get(placedTile.id())));
                    visibleOccupantsP.set(gameStateO.get().board().occupants());
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        Platform.runLater(timeline::play);
    }
}