package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

public final class ColorMap {

    private static final double OPACITY_FACTOR = .6;

    // Non-instantiable
    private ColorMap() {
    }

    public static Color fillColor(PlayerColor playerColor) {
        return switch (playerColor) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case YELLOW -> Color.YELLOW;
            case PURPLE -> Color.PURPLE;
        };
    }

    public static Color strokeColor(PlayerColor playerColor) {
        if (playerColor == PlayerColor.YELLOW || playerColor == PlayerColor.GREEN)
            return fillColor(playerColor).deriveColor(0, 1, 1, OPACITY_FACTOR);
        return Color.WHITE;
    }

}
