package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.PlayerColor;
import ch.epfl.chacun.TextMaker;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Helper class to create the players UI.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class PlayersUI {

    /**
     * Non-instantiable class constructor
     */
    private PlayersUI() {
    }

    public static Node create(ObservableValue<GameState> gameState, TextMaker textMaker) {
        VBox scene = new VBox();
        scene.setId("players");

        for (PlayerColor playerColor : PlayerColor.ALL) {
            if (textMaker.playerName(playerColor) != null) {
                // TODO: Implement the player UI
            }
        }

        return scene;
    }
}
