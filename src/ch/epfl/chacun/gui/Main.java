package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

/**
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters params = getParameters();
        List<String> playerNames = params.getUnnamed();
        String rawSeed = params.getNamed().get("seed");

        Preconditions.checkArgument(playerNames.size() >= 2 && playerNames.size() <= 5);

        // Apply the seed to ALL tiles
        long seed = Long.parseUnsignedLong(rawSeed);
        RandomGenerator shuffler = RandomGeneratorFactory.getDefault().create(seed);
        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, shuffler);
        // Group tiles by kind to create the decks
        Map<Tile.Kind, List<Tile>> decks = tiles.stream().collect(Collectors.groupingBy(Tile::kind));
        TileDecks tileDecks = new TileDecks(decks);

        TextMaker textMaker = new TextMakerFr(Map.of());
        MessageBoard messageBoard = new MessageBoard(textMaker, List.of());
        GameState gameState = new GameState(PlayerColor.ALL.subList(0, playerNames.size()), tileDecks,
                null, Board.EMPTY, GameState.Action.START_GAME, messageBoard);
    }
}
