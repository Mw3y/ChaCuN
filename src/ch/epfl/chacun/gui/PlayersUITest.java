package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import ch.epfl.chacun.tile.Tiles;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PlayersUITest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        var playerNames = Map.of(PlayerColor.BLUE, "Bernard", PlayerColor.RED, "Rose"
                );
        var playerColors = playerNames.keySet().stream()
                .sorted()
                .toList();

        var tilesByKind = Tiles.TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks =
                new TileDecks(tilesByKind.get(Tile.Kind.START),
                        tilesByKind.get(Tile.Kind.NORMAL),
                        tilesByKind.get(Tile.Kind.MENHIR));

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

        var unoccupyableTiles = Set.of(62);
        var normalTilesIds = List.of(61, 55, 51, 18, 62, 1, 34, 67, 31, 3, 49);
        var gameStateO = new SimpleObjectProperty<>(initialGameState(playerNames, normalTilesIds, List.of()));
        var playersNode = PlayersUI.create(gameStateO, textMaker);
        var messagesNode = MessageBoardUI.create(gameStateO.map(g -> g.messageBoard().messages()), new SimpleObjectProperty<>(Set.of()));
        var decksNode = DecksUI.create(new SimpleObjectProperty<>(gameStateO.getValue().tileToPlace()), new SimpleObjectProperty<>(normalTilesIds.size()), new SimpleObjectProperty<>(0), new SimpleObjectProperty<>("Lorem ipsum dolor sit amet"), o -> {});
        var rootNode = new VBox(playersNode, messagesNode, decksNode);
        primaryStage.setScene(new Scene(rootNode, 271, 720));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();

        gameStateO.setValue(truncateDeck(gameStateO.getValue(), Tile.Kind.NORMAL, normalTilesIds.size() - 1));

        var players = gameStateO.getValue().players();
        var playersIt = Stream.generate(() -> players)
                .flatMap(Collection::stream)
                .iterator();

        var nextPlacedTile = (Function<GameState, PlacedTile>) s -> {
            var t = s.tileToPlace();
            return new PlacedTile(t, playersIt.next(), Rotation.NONE, positions.get(t.id()));
        };

        // Place all tiles
        for (int i = 0; i < positions.size(); i += 1) {
            var placedTile = nextPlacedTile.apply(gameStateO.getValue());
            gameStateO.setValue(gameStateO.getValue().withPlacedTile(placedTile));
            if (!unoccupyableTiles.contains(placedTile.id()))
                gameStateO.setValue(gameStateO.getValue().withNewOccupant(occupants.get(placedTile.id())));
        }
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
        return new GameState(List.copyOf(players.keySet()), tileDecks1, firstTileToPlace, board, GameState.Action.PLACE_TILE, messageBoard);
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
}