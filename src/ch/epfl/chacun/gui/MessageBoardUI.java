package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Helper class to create the message board UI.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class MessageBoardUI {

    /**
     * Non-instantiable class constructor
     */
    private MessageBoardUI() {
    }

    /**
     * Creates the message board UI.
     *
     * @param messagesO the observable value of the messages
     * @param tileIdsP  the property of the tile ids
     * @return the message board UI
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> messagesO,
                              ObjectProperty<Set<Integer>> tileIdsP) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("message-board");
        // Add the CSS to the scene
        URL sceneStyle = PlayersUI.class.getResource("/message-board.css");
        scrollPane.getStylesheets().add(sceneStyle.toExternalForm());

        VBox container = new VBox();
        messagesO.addListener((o, previousMessages, currentMessages) -> {
            List<MessageBoard.Message> newMessages = new ArrayList<>(currentMessages);
            newMessages.removeAll(previousMessages);
            for (MessageBoard.Message newMessage : newMessages) {
                Text message = new Text(newMessage.text());
                message.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
                // Dynamically update the tile ids mentioned in the message if needed
                message.setOnMouseEntered(_ -> tileIdsP.set(newMessage.tileIds()));
                message.setOnMouseExited(_ -> tileIdsP.set(Set.of()));
                container.getChildren().add(message);
            }
            // Scroll to the last message
            Platform.runLater(() -> scrollPane.setVvalue(1));
        });

        scrollPane.setContent(container);
        return scrollPane;
    }
}
