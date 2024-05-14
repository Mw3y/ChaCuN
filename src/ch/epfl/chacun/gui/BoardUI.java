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
import java.util.Objects;
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
     * The cache to store the images of the tiles.
     */
    private static final Map<Integer, Image> tilesCache = new HashMap<>();

    /**
     * The value to scroll to center the board.
     */
    private static final double SCROLL_CENTER_SCALE = .5;

    /**
     * Non-instantiable class constructor.
     */
    private BoardUI() {
    }

    /**
     * Creates the board UI based on the given reach.
     *
     * @param reach               the reach of the board
     * @param gameStateO          the observable game state
     * @param rotationO           the observable rotation
     * @param occupantsO          the observable set of occupants
     * @param highlightedTileIdsO the observable set of highlighted tile ids
     * @param rotationToApply     the consumer to apply a rotation
     * @param tileToPlacePos      the consumer to place a tile
     * @param selectedOccupant    the consumer to select an occupant
     * @return the created board UI
     */
    public static Node create(
            int reach,
            ObservableValue<GameState> gameStateO,
            ObservableValue<Rotation> rotationO,
            ObservableValue<Set<Occupant>> occupantsO,
            ObservableValue<Set<Integer>> highlightedTileIdsO,
            Consumer<Rotation> rotationToApply,
            Consumer<Pos> tileToPlacePos,
            Consumer<Occupant> selectedOccupant
    ) {
        Preconditions.checkArgument(reach > 0);

        ScrollPane container = new ScrollPane();
        container.getStylesheets().add("/board.css");
        container.setId("board-scroll-pane");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");

        ObservableValue<Board> boardO = gameStateO.map(GameState::board);
        ObservableValue<Set<Pos>> insertionPositionsO = boardO.map(Board::insertionPositions);

        // Create a tile for each board position within the reach
        for (int x = -reach; x <= reach; ++x) {
            for (int y = -reach; y <= reach; ++y) {
                Pos tilePos = new Pos(x, y);
                Group tileContainer = new Group();

                ImageView tileView = new ImageView();
                tileView.setFitHeight(NORMAL_TILE_FIT_SIZE);
                tileView.setFitWidth(NORMAL_TILE_FIT_SIZE);

                ObjectBinding<CellData> cellData = CellData.createBinding(gameStateO, boardO,
                        insertionPositionsO, rotationO, highlightedTileIdsO, tileContainer, tilePos);

                tileView.imageProperty().bind(cellData.map(CellData::tileImage));
                tileContainer.rotateProperty().bind(cellData.map(data -> data.tileRotation().degreesCW()));
                tileContainer.effectProperty().bind(cellData.map(CellData::createVeil));
                tileContainer.getChildren().add(tileView);

                ObservableValue<PlacedTile> placedTileO =
                        gameStateO.map(gameState -> gameState.board().tileAt(tilePos));

                // Handle tile rotation and placement
                tileContainer.setOnMouseClicked(event -> {
                    // Prevent the player from interacting with the tile when it has been placed
                    // Allows the player to keep his mouse button pressed to move on the board
                    if (placedTileO.getValue() == null && event.isStillSincePress()) {
                        // Rotate the tile
                        if (event.getButton() == MouseButton.SECONDARY) {
                            // Allow for clockwise and counter-clockwise rotation using the ALT key
                            Rotation rotationToAdd = event.isAltDown() ? Rotation.RIGHT : Rotation.LEFT;
                            rotationToApply.accept(rotationToAdd);
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
                            Node markerView = createAnimalMarker(gameStateO, animal);
                            tileContainer.getChildren().add(markerView);
                        }
                    }
                    // Add all potential occupants to the tile
                    for (Occupant occupant : placedTile.potentialOccupants()) {
                        Node occupantIcon = createTileOccupant(occupantsO, selectedOccupant, placedTile, occupant);
                        tileContainer.getChildren().add(occupantIcon);
                    }
                });
                // Add the tile to the grid while ensuring its coordinates are positive
                gridPane.add(tileContainer, x + reach, y + reach);
            }
        }

        container.setContent(gridPane);
        // Center board
        container.setVvalue(SCROLL_CENTER_SCALE);
        container.setHvalue(SCROLL_CENTER_SCALE);
        return container;
    }

    /**
     * Creates a marker for a given cancelled animal.
     * <p>
     * The marker is only visible when the animal has been cancelled.
     *
     * @param gameStateO the observable game state
     * @param animal     the animal to create a marker for
     * @return the created marker
     */
    private static Node createAnimalMarker(ObservableValue<GameState> gameStateO, Animal animal) {
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
        return markerView;
    }

    /**
     * Creates an occupant icon for a given occupant.
     * <p>
     * The icon is only visible when the occupant is present on the tile.
     *
     * @param occupantsO       the observable set of occupants
     * @param selectedOccupant the consumer to select an occupant
     * @param placedTile       the placed tile
     * @param occupant         the occupant to create an icon for
     * @return the created occupant icon
     */
    private static Node createTileOccupant(ObservableValue<Set<Occupant>> occupantsO,
                                           Consumer<Occupant> selectedOccupant,
                                           PlacedTile placedTile,
                                           Occupant occupant) {
        Node occupantIcon = Icon.newFor(placedTile.placer(), occupant.kind());
        occupantIcon.setId(STR."\{occupant.kind().toString().toLowerCase()}_\{occupant.zoneId()}");
        // Ensure the occupant is always oriented upwards
        occupantIcon.setRotate(placedTile.rotation().negated().degreesCW());
        // Hide the occupant when not needed
        occupantIcon.visibleProperty().bind(occupantsO.map(occupants -> occupants.contains(occupant)));
        // Allow the player to select an occupant and place it/remove it
        occupantIcon.setOnMouseClicked(_ -> selectedOccupant.accept(occupant));
        return occupantIcon;
    }

    /**
     * Returns the image of the tile with the given id and cache it.
     *
     * @param tileId the id of the tile
     * @return the image of the tile
     */
    private static Image cachedTileImage(int tileId) {
        return tilesCache.computeIfAbsent(tileId, ImageLoader::normalImageForTile);
    }

    /**
     * Helper class to store the data of a cell.
     *
     * @param tileImage    the image of the tile
     * @param tileRotation the rotation of the tile
     * @param veilColor    the color of the veil to apply on the tile
     */
    private record CellData(Image tileImage, Rotation tileRotation, Color veilColor) {

        /**
         * The opacity of the veil to apply on the tile.
         */
        private static final double VEIL_OPACITY = .5;

        /**
         * The default image to display when a position doesn't have any tile.
         */
        private static final Image EMPTY_TILE_IMAGE;

        static {
            WritableImage writableImage = new WritableImage(1, 1);
            // Fill the empty tile with a gray color
            writableImage.getPixelWriter().setColor(0, 0, Color.gray(EMPTY_TILE_GRAY_SCALE));
            EMPTY_TILE_IMAGE = writableImage;
        }

        /**
         * Validates the given tile image, rotation and veil color.
         */
        public CellData {
            Objects.requireNonNull(tileImage);
            Objects.requireNonNull(tileRotation);
            Objects.requireNonNull(veilColor);
        }

        /**
         * Creates a cell data with the given placed tile and veil color.
         *
         * @param placedTile the placed tile
         * @param veilColor  the color of the veil to apply on the tile
         */
        public CellData(PlacedTile placedTile, Color veilColor) {
            this(placedTile != null ? cachedTileImage(placedTile.id()) : EMPTY_TILE_IMAGE,
                    placedTile != null ? placedTile.rotation() : Rotation.NONE, veilColor);
        }

        /**
         * Creates a binding to update the cell data based on the game state, rotation and highlighted tile ids.
         * @param gameStateO the observable game state
         * @param boardO the observable board
         * @param insertionPositionsO the observable set of insertion positions
         * @param rotationO the observable rotation
         * @param highlightedTileIdsO the observable set of highlighted tile ids
         * @param tileContainer the tile container
         * @param tilePos the position of the tile
         * @return the created binding
         */
        public static ObjectBinding<CellData> createBinding(
                ObservableValue<GameState> gameStateO,
                ObservableValue<Board> boardO,
                ObservableValue<Set<Pos>> insertionPositionsO,
                ObservableValue<Rotation> rotationO,
                ObservableValue<Set<Integer>> highlightedTileIdsO,
                Group tileContainer,
                Pos tilePos) {
            ObservableValue<PlacedTile> placedTileO = boardO.map(board -> board.tileAt(tilePos));

            return Bindings.createObjectBinding(() -> {
                GameState gameState = gameStateO.getValue();
                PlacedTile placedTile = boardO.getValue().tileAt(tilePos);
                Set<Integer> highlightedTileIds = highlightedTileIdsO.getValue();

                boolean isTileBeingPlaced = gameState.tileToPlace() != null;
                boolean isInFringe = insertionPositionsO.getValue().contains(tilePos);

                if (placedTile != null && !highlightedTileIds.isEmpty()
                        && !highlightedTileIds.contains(placedTile.id()))
                    return new CellData(placedTile, Color.BLACK);

                if (isTileBeingPlaced && isInFringe) {
                    if (tileContainer.isHover()) {
                        PlacedTile tileCandidate = new PlacedTile(
                                gameState.tileToPlace(), gameState.currentPlayer(), rotationO.getValue(), tilePos);
                        Color veilColor = !gameState.board().canAddTile(tileCandidate)
                                ? Color.WHITE : Color.TRANSPARENT;
                        return new CellData(tileCandidate, veilColor);
                    }
                    return new CellData(placedTile, ColorMap.fillColor(gameState.currentPlayer()));
                }

                return new CellData(placedTile, Color.TRANSPARENT);
            }, rotationO, highlightedTileIdsO, tileContainer.hoverProperty(), placedTileO, insertionPositionsO);
        }

        /**
         * Creates a veil to apply on the tile based on its current color.
         *
         * @return the created veil
         */
        public Blend createVeil() {
            // TODO: Create only one time
            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_OVER);
            blend.setTopInput(new ColorInput(0, 0, NORMAL_TILE_FIT_SIZE, NORMAL_TILE_FIT_SIZE, veilColor));
            blend.setOpacity(VEIL_OPACITY);
            return blend;
        }

    }

}
