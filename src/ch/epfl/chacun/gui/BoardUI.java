package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Set;
import java.util.function.Consumer;

import static ch.epfl.chacun.gui.ImageLoader.LARGE_TILE_FIT_SIZE;
import static ch.epfl.chacun.gui.ImageLoader.NORMAL_TILE_FIT_SIZE;

/**
 * Helper class to display the board.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class BoardUI {

    /**
     * Non-instantiable class constructor.
     */
    private BoardUI() {
    }

    public static Node create(int reach, ObservableValue<GameState> gameStateO, ObservableValue<Rotation> rotationO,
                              ObservableValue<Set<Occupant>> occupantsO, ObservableValue<Set<Integer>> tileIdsO,
                              Consumer<Rotation> rotationToApply, Consumer<Pos> tileToPlacePos,
                              Consumer<Occupant> selectedOccupant) {

        Preconditions.checkArgument(reach > 0);

        ScrollPane container = new ScrollPane();
        container.getStylesheets().add("/board.css");
        container.setId("board-scroll-pane");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");

        ObservableValue<Board> boardO = gameStateO.map(GameState::board);

        for (int x = -reach; x <= reach; ++x) {
            for (int y = -reach; y <= reach; ++y) {
                ImageView view = new ImageView();
                view.setFitHeight(NORMAL_TILE_FIT_SIZE);
                view.setFitWidth(NORMAL_TILE_FIT_SIZE);
                // Display the image of the tile to place
                int finalX = x;
                int finalY = y;
                boardO.addListener((_, _, board) -> {
                    PlacedTile placedTile = board.tileAt(new Pos(finalX, finalY));
                    if (placedTile != null) {
                        Image newImage = ImageLoader.largeImageForTile(placedTile.id());
                        view.setImage(newImage);
                    }
                });

                gridPane.getChildren().add(new Group(view));
            }
        }

        container.setContent(gridPane);
        return container;
    }

}
