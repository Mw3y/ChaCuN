package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

/**
 * Helper class to map player colors to JavaFX colors.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class ColorMap {

    /**
     * The opacity factor to use for the stroke color.
     */
    private static final double STROKE_BRIGHTNESS_FACTOR = .6;

    /**
     * Non-instantiable class constructor
     */
    private ColorMap() {
    }

    /**
     * Returns the fill color for the given player color.
     *
     * @param playerColor the player color
     * @return the fill color
     */
    public static Color fillColor(PlayerColor playerColor) {
        return switch (playerColor) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case YELLOW -> Color.YELLOW;
            case PURPLE -> Color.PURPLE;
        };
    }

    /**
     * Returns the stroke color for the given player color.
     *
     * @param color the player color
     * @return the stroke color
     */
    public static Color strokeColor(PlayerColor color) {
        // For better contrast with light colors, use a darker stroke color
        // For dark colors, use a lighter stroke color
        return switch (color) {
            case PlayerColor.RED, PlayerColor.BLUE, PlayerColor.PURPLE -> Color.WHITE;
            case PlayerColor.YELLOW, PlayerColor.GREEN -> deriveStrokeColor(color);
        };
    }

    /**
     * Derives the stroke color from the fill color to prevent low contrast.
     * @param color the fill color
     * @return the derived stroke color
     */
    private static Color deriveStrokeColor(PlayerColor color) {
        return fillColor(color)
                .deriveColor(0, 1, STROKE_BRIGHTNESS_FACTOR, 1);
    }

}
