package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import ch.epfl.chacun.TextMaker;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class to create the players UI.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class PlayersUI {

    private static final float OCCUPANT_USED_OPACITY = 0.1f;

    /**
     * Non-instantiable class constructor
     */
    private PlayersUI() {
    }

    public static Node create(ObservableValue<GameState> gameStateO, TextMaker textMaker) {
        VBox scene = new VBox();
        scene.setId("players");
        // Add the CSS to the scene
        URL sceneStyle = PlayersUI.class.getResource("/players.css");
        scene.getStylesheets().add(sceneStyle.toExternalForm());

        // General reactive state
        ObservableValue<PlayerColor> currentPlayerO =
                gameStateO.map(GameState::currentPlayer);
        ObservableValue<Map<PlayerColor, Integer>> pointsO =
                gameStateO.map((GameState gameState) -> gameState.messageBoard().points());

        // Create the UI for each player
        for (PlayerColor playerColor : gameStateO.getValue().players()) {
            String playerName = textMaker.playerName(playerColor);
            TextFlow textFlow = new TextFlow();
            Text pointsText = new Text();
            HBox occupantsBox = new HBox();
            VBox playerBox = new VBox();

            // Dynamically update the points text
            ObservableValue<String> pointsTextO = pointsO.map(points -> {
                int pointsValue = points.getOrDefault(playerColor, 0);
                return STR." \{playerName} : \{textMaker.points(pointsValue)}";
            });
            // Show which player is currently playing
            currentPlayerO.addListener((o, prev, currentPlayer) -> {
                playerBox.getStyleClass().remove("current");
                if (currentPlayer == playerColor)
                    playerBox.getStyleClass().add("current");
            });

            // Add the player's name and points to the UI
            textFlow.getChildren().add(new Circle(5, ColorMap.fillColor(playerColor)));
            pointsText.textProperty().bind(pointsTextO);
            textFlow.getChildren().add(pointsText);
            playerBox.getStyleClass().add("player");
            // Add the player's occupants to the UI
            for (int i = Occupant.Kind.values().length - 1; i >= 0 ; --i) {
                Occupant.Kind kind = Occupant.Kind.values()[i];
                HBox box = new HBox();
                for (int j = 0; j < Occupant.occupantsCount(kind); ++j) {
                    Node icon = Icon.newFor(playerColor, kind);
                    // Make the opacity of the icon dependent on the number of free occupants
                    int occupantId = j;
                    ObservableValue<Float> opacityO = gameStateO.map(gameState -> {
                        int hutsCount = gameState.freeOccupantsCount(playerColor, kind);
                        return hutsCount > occupantId ? 1f : OCCUPANT_USED_OPACITY;
                    });
                    icon.opacityProperty().bind(opacityO);
                    box.getChildren().add(icon);
                }
                occupantsBox.getChildren().add(box);
            }

            occupantsBox.setSpacing(10);
            playerBox.setSpacing(2);
            // Add everything to the UI
            playerBox.getChildren().addAll(textFlow, occupantsBox);
            scene.getChildren().add(playerBox);
        }

        return scene;
    }
}
