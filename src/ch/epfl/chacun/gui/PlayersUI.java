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

import java.util.Map;

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

    public static Node create(ObservableValue<GameState> gameStateO, TextMaker textMaker) {
        VBox scene = new VBox();
        scene.setId("players");
        scene.getStylesheets().add(PlayersUI.class.getResource("/players.css").toExternalForm());

        ObservableValue<PlayerColor> currentPlayerO =
                gameStateO.map(GameState::currentPlayer);

        ObservableValue<Map<PlayerColor, Integer>> pointsO =
                gameStateO.map((GameState gameState) -> gameState.messageBoard().points());

        for (PlayerColor playerColor : gameStateO.getValue().players()) {
            String playerName = textMaker.playerName(playerColor);
            TextFlow textFlow = new TextFlow();
            Text pointsText = new Text();
            HBox hutsBox = new HBox(), pawnsBox = new HBox(), occupantsBox = new HBox();
            VBox playerBox = new VBox();

            // Reactive state
            ObservableValue<String> pointsTextO = pointsO.map(points -> {
                int pointsValue = points.getOrDefault(playerColor, 0);
                return STR." \{playerName} : \{textMaker.points(pointsValue)}";
            });

            currentPlayerO.addListener((o, prev, currentPlayer) -> {
                textFlow.getStyleClass().remove("current");
                if (currentPlayer == playerColor)
                    textFlow.getStyleClass().add("current");
            });

            // Bindings
            pointsText.textProperty().bind(pointsTextO);

            // Add to UI
            // The player's name and points
            textFlow.getChildren().add(new Circle(5, ColorMap.fillColor(playerColor)));
            textFlow.getChildren().add(pointsText);
            textFlow.getStyleClass().add("player");
            // The player's occupants
            // TODO: Dynamic occupants opacity
            for (int i = 0; i < Occupant.occupantsCount(Occupant.Kind.HUT); ++i) {
                hutsBox.getChildren().add(Icon.newFor(playerColor, Occupant.Kind.HUT));
            }
            for (int i = 0; i < Occupant.occupantsCount(Occupant.Kind.PAWN); ++i) {
                pawnsBox.getChildren().add(Icon.newFor(playerColor, Occupant.Kind.PAWN));
            }
            occupantsBox.getChildren().addAll(hutsBox, pawnsBox);
            occupantsBox.setSpacing(10);
            // The player's box
            playerBox.getChildren().addAll(textFlow, occupantsBox);
            scene.getChildren().add(playerBox);
        }

        return scene;
    }
}
