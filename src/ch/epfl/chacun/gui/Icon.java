package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

public final class Icon {

    private final static String PAWN_SVG_PATH = "M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6 L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z";
    private final static String HUT_SVG_PATH = "M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z";

    // Non-instantiable
    private Icon() {
    }

    public static Node newFor(PlayerColor playerColor, Occupant.Kind occupantKind) {
        SVGPath occupantIcon = new SVGPath();
        occupantIcon.setFill(ColorMap.fillColor(playerColor));
        occupantIcon.setStroke(ColorMap.strokeColor(playerColor));
        occupantIcon.setContent(occupantKind == Occupant.Kind.PAWN ? PAWN_SVG_PATH : HUT_SVG_PATH);
        return occupantIcon;
    }

}
