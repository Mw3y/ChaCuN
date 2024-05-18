package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

/**
 * Helper class to create an icon for a player color and occupant kind.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class Icon {

    /**
     * The pawn icon SVG path
     */
    private final static String PAWN_SVG_PATH = "M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6 L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z";

    /**
     * The hut icon SVG path
     */
    private final static String HUT_SVG_PATH = "M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z";

    /**
     * Non-instantiable class constructor
     */
    private Icon() {
    }

    /**
     * Creates a new icon for the given player color and occupant kind.
     *
     * @param playerColor  the player color
     * @param occupantKind the occupant kind
     * @return the icon node
     */
    public static Node newFor(PlayerColor playerColor, Occupant.Kind occupantKind) {
        SVGPath occupantIcon = new SVGPath();
        occupantIcon.setFill(ColorMap.fillColor(playerColor));
        occupantIcon.setStroke(ColorMap.strokeColor(playerColor));
        occupantIcon.setContent(switch (occupantKind) {
            case PAWN -> PAWN_SVG_PATH;
            case HUT -> HUT_SVG_PATH;
        });
        return occupantIcon;
    }

}
