package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static ch.epfl.chacun.gui.ImageLoader.MARKER_FIT_SIZE;
import static ch.epfl.chacun.gui.ImageLoader.NORMAL_TILE_FIT_SIZE;

/**
 * Helper class to display the board.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class BoardUI {

    /**
     * The gray scale of the empty tile.
     */
    private static final double EMPTY_TILE_GRAY_SCALE = 0.98;

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

        // The default image to display when a position doesn't have any tile
        WritableImage emptyTileImage = new WritableImage(1, 1);
        emptyTileImage.getPixelWriter().setColor(0, 0, Color.gray(EMPTY_TILE_GRAY_SCALE));
        // Cache the tile images to avoid reloading them
        Map<Integer, Image> tilesCache = new HashMap<>();

        // Create a tile for each board position within the reach
        for (int x = -reach; x <= reach; ++x) {
            for (int y = -reach; y <= reach; ++y) {
                Group tileContainer = new Group();

                ImageView tileView = new ImageView();
                tileView.setFitHeight(NORMAL_TILE_FIT_SIZE);
                tileView.setFitWidth(NORMAL_TILE_FIT_SIZE);

                Pos tilePos = new Pos(x, y);
                ObservableValue<PlacedTile> placedTileO = gameStateO
                        .map(gameState -> gameState.board().tileAt(tilePos));

                // Reactive image data
                ObservableValue<Image> tileImageO = placedTileO.map(tile ->
                        tilesCache.computeIfAbsent(tile.id(), ImageLoader::normalImageForTile)).orElse(emptyTileImage);
                tileView.imageProperty().bind(tileImageO);
                tileContainer.getChildren().add(tileView);

                // Handle tile rotation and placement
                tileContainer.setOnMouseClicked(event -> {
                    // Prevent the player from interacting with the tile when it has been placed
                    if (placedTileO.getValue() == null) {
                        // Rotate the tile
                        if (event.getButton() == MouseButton.SECONDARY) {
                            // Allow for clockwise and counter-clockwise rotation using the ALT key
                            Rotation rotationToAdd = event.isAltDown() ? Rotation.RIGHT : Rotation.LEFT;
                            rotationToApply.accept(rotationO.getValue().add(rotationToAdd));
                        }
                        // Place the tile
                        if (event.getButton() == MouseButton.PRIMARY) {
                            tileToPlacePos.accept(tilePos);
                        }
                    }
                });

                // When the tile has been placed
                placedTileO.addListener((_, _, placedTile) -> {
                    // Add all potential markers to the tile
                    for (Zone.Meadow meadowZone : placedTile.meadowZones()) {
                        for (Animal animal : meadowZone.animals()) {
                            ImageView markerView = new ImageView();
                            // Style the marker
                            markerView.setFitHeight(MARKER_FIT_SIZE);
                            markerView.setFitWidth(MARKER_FIT_SIZE);
                            markerView.getStyleClass().add("marker");
                            markerView.setId(STR."marker_\{animal.id()}");
                            // Hide the marker when not needed
                            ObservableValue<Boolean> isCancelledO = gameStateO
                                    .map(gameState -> gameState.board().cancelledAnimals().contains(animal));
                            markerView.visibleProperty().bind(isCancelledO);
                            tileContainer.getChildren().add(markerView);
                        }
                    }

                    // Add all potential occupants to the tile
                    for (Occupant occupant : placedTile.potentialOccupants()) {
                        Node occupantIcon = Icon.newFor(placedTile.placer(), occupant.kind());
                        occupantIcon.setId(STR."\{occupant.kind().toString().toLowerCase()}_\{occupant.zoneId()}");
                        // Ensure the occupant is always oriented upwards
                        occupantIcon.rotateProperty().bind(rotationO.map(rotation -> rotation.negated().degreesCW()));
                        // Hide the occupant when not needed
                        occupantIcon.visibleProperty().bind(occupantsO.map(occupants -> occupants.contains(occupant)));
                        // Allow the player to select an occupant and place it/remove it
                        occupantIcon.setOnMouseClicked(_ -> selectedOccupant.accept(occupant));
                        tileContainer.getChildren().add(occupantIcon);
                    }

                    // Apply the placed tile rotation
                    tileContainer.setRotate(placedTile.rotation().degreesCW());
                });

                ObservableValue<Set<Pos>> insertionPositionsO = gameStateO
                        .map(gameState -> gameState.board().insertionPositions());

                ObjectBinding<CellData> cellData = Bindings.createObjectBinding(() -> {
                    GameState gameState = gameStateO.getValue();
                    PlacedTile placedTile = placedTileO.getValue();

                    ColorInput veilColor = new ColorInput();
                    veilColor.setHeight(NORMAL_TILE_FIT_SIZE);
                    veilColor.setWidth(NORMAL_TILE_FIT_SIZE);

                    Set<Integer> highlightedTileIds = tileIdsO.getValue();
                    if(!highlightedTileIds.isEmpty() && (placedTile == null || !highlightedTileIds.contains(placedTile.id()))){
                        veilColor.setPaint(Color.BLACK);
                        return new CellData(null, null, veilColor);
                    }

                    if (gameState.tileToPlace() != null && insertionPositionsO.getValue().contains(tilePos)) {
                        PlacedTile tileToPlace = new PlacedTile(gameState.tileToPlace(), gameState.currentPlayer(), rotationO.getValue(), tilePos);
                        if (!tileContainer.isHover()) {
                            veilColor.setPaint(ColorMap.fillColor(gameState.currentPlayer()));
                            return new CellData(null, null, veilColor);
                        }
                        else if (placedTile == null && !gameState.board().canAddTile(tileToPlace)) {
                            veilColor.setPaint(Color.WHITE);
                            return new CellData(tileImageO.getValue(), rotationO.getValue(), veilColor);
                        }
                    }
                    return new CellData(null, null, null);
                }, rotationO, tileIdsO, insertionPositionsO, tileContainer.hoverProperty());

                tileContainer.effectProperty().bind(cellData.map(data -> {
                    Blend blend = new Blend();
                    blend.setMode(BlendMode.SRC_OVER);
                    blend.setTopInput(data.veilColor());
                    blend.setOpacity(.5);
                    return blend;
                }));

                // Add the tile to the grid while ensuring its coordinates are positive
                gridPane.add(tileContainer, x + reach, y + reach);
            }
        }

        container.setContent(gridPane);
        return container;
    }

    private record CellData(Image tileImage, Rotation tileRotation, ColorInput veilColor) {

    }

}
