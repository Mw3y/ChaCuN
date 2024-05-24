package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import ch.epfl.chacun.TextMaker;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * Helper class to create the players UI.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class PlayersUI {

    /**
     * The default opacity of the occupant icon.
     */
    private static final float DEFAULT_OCCUPANT_OPACITY = 1f;

    /**
     * The opacity of the occupant icon when it is used.
     */
    private static final float OCCUPANT_USED_OPACITY = .1f;

    /**
     * The radius of the player circle.
     */
    private static final int PLAYER_CIRCLE_RADIUS = 5;

    /**
     * Non-instantiable class constructor
     */
    private PlayersUI() {
    }

    /**
     * Creates the players UI.
     *
     * @param gameStateO the observable value of the game state
     * @param textMaker  the text maker
     * @return the players UI
     */
    public static Node create(ObservableValue<GameState> gameStateO, TextMaker textMaker) {
        VBox container = new VBox();
        container.getStylesheets().add("/players.css");
        container.setId("players");

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

            // Dynamically update the points text
            ObservableValue<String> pointsTextO = pointsO.map(points -> {
                int pointsValue = points.getOrDefault(playerColor, 0);
                return STR." \{playerName} : \{textMaker.points(pointsValue)}\n";
            });
            // Show which player is currently playing
            currentPlayerO.addListener((_, _, currentPlayer) -> {
                textFlow.getStyleClass().remove("current");
                if (currentPlayer == playerColor)
                    textFlow.getStyleClass().add("current");
            });

            // Add the player's name and points to the UI
            textFlow.getChildren().add(new Circle(PLAYER_CIRCLE_RADIUS, ColorMap.fillColor(playerColor)));
            pointsText.textProperty().bind(pointsTextO);
            textFlow.getChildren().add(pointsText);
            textFlow.getStyleClass().add("player");
            // Add the player's occupants to the UI
            for (int i = Occupant.Kind.values().length - 1; i >= 0; --i) {
                Occupant.Kind kind = Occupant.Kind.values()[i];
                for (int j = 0; j < Occupant.occupantsCount(kind); ++j) {
                    Node icon = Icon.newFor(playerColor, kind);
                    // Make the opacity of the icon dependent on the number of free occupants
                    int occupantId = j;
                    ObservableValue<Float> opacityO = gameStateO.map(gameState -> {
                        int hutsCount = gameState.freeOccupantsCount(playerColor, kind);
                        return hutsCount > occupantId ? DEFAULT_OCCUPANT_OPACITY : OCCUPANT_USED_OPACITY;
                    });
                    icon.opacityProperty().bind(opacityO);
                    textFlow.getChildren().add(icon);
                }
                // Add some space between the occupant types
                textFlow.getChildren().add(new Text("   "));
            }

            // Add everything to the UI
            container.getChildren().add(textFlow);
        }
        return container;
    }

}
