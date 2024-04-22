package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static ch.epfl.chacun.gui.ImageLoader.NORMAL_TILE_FIT_SIZE;

/**
 * Helper class to display the board.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class BoardUI {

    private static final double EMPTY_TILE_GRAY_SCALE = 0.98;

    /**
     * Non-instantiable class constructor.
     */
    private BoardUI() {
    }

    public static Node create(int reach, ObservableValue<GameState> gameStateO, ObservableValue<Rotation> rotationO, ObservableValue<Set<Occupant>> occupantsO, ObservableValue<Set<Integer>> tileIdsO, Consumer<Rotation> rotationToApply, Consumer<Pos> tileToPlacePos, Consumer<Occupant> selectedOccupant) {

        Preconditions.checkArgument(reach > 0);

        ScrollPane container = new ScrollPane();
        container.getStylesheets().add("/board.css");
        container.setId("board-scroll-pane");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");

        WritableImage emptyTileImage = new WritableImage(1, 1);
        emptyTileImage.getPixelWriter().setColor(0, 0, Color.gray(EMPTY_TILE_GRAY_SCALE));
        
        Map<Integer, Image> tileImagesCache = new HashMap<>();

        for (int x = -reach; x <= reach; ++x) {
            for (int y = -reach; y <= reach; ++y) {
                ImageView view = new ImageView();
                view.setFitHeight(NORMAL_TILE_FIT_SIZE);
                view.setFitWidth(NORMAL_TILE_FIT_SIZE);

                Pos tilePos = new Pos(x, y);
                ObservableValue<PlacedTile> placedTileO = gameStateO
                        .map(gameState -> gameState.board().tileAt(tilePos));

                // Reactive image data
                ObservableValue<Image> tileImageO = placedTileO.map(placedTile -> {
                    if (placedTile != null)
                        return tileImagesCache.computeIfAbsent(placedTile.id(), ImageLoader::normalImageForTile);
                    return emptyTileImage;
                });

                view.imageProperty().bind(tileImageO);
                gridPane.getChildren().add(new Group(view));
            }
        }

        container.setContent(gridPane);
        return container;
    }

}
