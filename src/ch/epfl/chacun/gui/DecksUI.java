package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

import static ch.epfl.chacun.gui.ImageLoader.LARGE_TILE_FIT_SIZE;
import static ch.epfl.chacun.gui.ImageLoader.NORMAL_TILE_FIT_SIZE;

/**
 * Helper class to create the decks UI.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class DecksUI {

    /**
     * The factor to apply to the tile fit size to get the wrapping width of the tile count.
     */
    private static final double TILE_COUNT_WRAPPING_FACTOR = .8;

    /**
     * Non-instantiable class constructor
     */
    private DecksUI() {
    }

    /**
     * Creates the decks UI.
     *
     * @param tileToPlaceO        the observable value of the tile to place
     * @param normalTileDeckSizeO the observable value of the size of the normal tile deck
     * @param menhirTileDeckSizeO the observable value of the size of the menhir tile deck
     * @param textToDisplayO      the observable value of the text to display
     * @param occupantConsumer    the consumer of the occupant to skip the special action of retaking a pawn
     * @return the decks UI
     */
    public static Node create(
            ObservableValue<Tile> tileToPlaceO,
            ObservableValue<Integer> normalTileDeckSizeO,
            ObservableValue<Integer> menhirTileDeckSizeO,
            ObservableValue<String> textToDisplayO,
            Consumer<Occupant> occupantConsumer
    ) {
        VBox container = new VBox();
        container.getStylesheets().add("/decks.css");
        // Create the decks container
        HBox decks = new HBox();
        decks.setId("decks");
        // Create the decks UI
        decks.getChildren().add(createDeckCover(Tile.Kind.NORMAL, normalTileDeckSizeO));
        decks.getChildren().add(createDeckCover(Tile.Kind.MENHIR, menhirTileDeckSizeO));
        container.getChildren().add(decks);
        // Create the next tile to place UI
        container.getChildren().add(createNextTileCover(tileToPlaceO, textToDisplayO, occupantConsumer));

        return container;
    }

    /**
     * Creates a deck cover UI.
     *
     * @param kind  the kind of the deck
     * @param sizeO the observable value of the size of the deck
     * @return the deck cover UI
     */
    private static StackPane createDeckCover(Tile.Kind kind, ObservableValue<Integer> sizeO) {
        ImageView view = new ImageView();
        Text tileCount = new Text();

        // Configure the view
        view.setFitHeight(NORMAL_TILE_FIT_SIZE);
        view.setFitWidth(NORMAL_TILE_FIT_SIZE);
        view.setId(kind.toString());
        // Configure the text
        tileCount.textProperty().bind(sizeO.map(String::valueOf));
        tileCount.setWrappingWidth(TILE_COUNT_WRAPPING_FACTOR * NORMAL_TILE_FIT_SIZE);

        return new StackPane(view, tileCount);
    }

    /**
     * Creates the next tile to place cover UI.
     *
     * @param tileToPlaceO     the observable value of the tile to place
     * @param textToDisplayO   the observable value of the text to display
     * @param occupantConsumer the consumer of the occupant
     * @return the next tile to place cover UI
     */
    private static StackPane createNextTileCover(
            ObservableValue<Tile> tileToPlaceO,
            ObservableValue<String> textToDisplayO,
            Consumer<Occupant> occupantConsumer
    ) {
        StackPane tileToPlace = new StackPane();
        tileToPlace.setId("next-tile");

        // Configure the view
        ImageView view = new ImageView();
        view.setFitHeight(LARGE_TILE_FIT_SIZE);
        view.setFitWidth(LARGE_TILE_FIT_SIZE);
        view.visibleProperty().bind(textToDisplayO.map(String::isEmpty));
        // Display the image of the tile to place
        ObservableValue<Image> nextTileImage = tileToPlaceO.map(tile -> ImageLoader.largeImageForTile(tile.id()));
        view.imageProperty().bind(nextTileImage);
        // Display the text of the special action and register a mouse click event to skip it
        Text text = new Text();
        text.textProperty().bind(textToDisplayO);
        text.visibleProperty().bind(textToDisplayO.map(s -> !s.isEmpty()));
        text.setWrappingWidth(TILE_COUNT_WRAPPING_FACTOR * LARGE_TILE_FIT_SIZE);
        text.setOnMouseClicked(_ -> occupantConsumer.accept(null));

        tileToPlace.getChildren().addAll(view, text);
        return tileToPlace;
    }
}
