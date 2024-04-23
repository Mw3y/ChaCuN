package ch.epfl.chacun.gui;

import javafx.scene.image.Image;

import static java.util.FormatProcessor.FMT;

/**
 * Helper class to load images.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class ImageLoader {

    /**
     * Size of large tiles, in pixels.
     */
    public static final int LARGE_TILE_PIXEL_SIZE = 512;

    /**
     * Display size of large tiles, in pixels.
     */
    public static final int LARGE_TILE_FIT_SIZE = 256;

    /**
     * Size of normal tiles, in pixels.
     */
    public static final int NORMAL_TILE_PIXEL_SIZE = 256;

    /**
     * Display size of normal tiles, in pixels.
     */
    public static final int NORMAL_TILE_FIT_SIZE = 128;

    /**
     * Size of the marker, in pixels.
     */
    public static final int MARKER_PIXEL_SIZE = 96;

    /**
     * Display size of the marker, in pixels.
     */
    public static final int MARKER_FIT_SIZE = 48;

    /**
     * Non-instantiable class constructor
     */
    private ImageLoader() {
    }

    /**
     * Loads the 512x512 image for the given tile id.
     *
     * @param tileId the id of the tile
     * @return the 512x512 tile image
     */
    public static Image normalImageForTile(int tileId) {
        return new Image(FMT."/\{NORMAL_TILE_PIXEL_SIZE}/%02d\{tileId}.jpg");
    }

    /**
     * Loads the 256x256 image for the given tile id.
     *
     * @param tileId the id of the tile
     * @return the 256x256 tile image
     */
    public static Image largeImageForTile(int tileId) {
        return new Image(FMT."/\{LARGE_TILE_PIXEL_SIZE}/%02d\{tileId}.jpg");
    }
}
