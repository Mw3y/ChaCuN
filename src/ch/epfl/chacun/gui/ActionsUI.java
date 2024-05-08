package ch.epfl.chacun.gui;

import ch.epfl.chacun.Base32;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Helper class to display the actions interface.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class ActionsUI {

    /**
     * Non-instantiable class constructor.
     */
    private ActionsUI() {}

    /**
     * Creates the actions UI.
     *
     * @param actionsO the observable list of all the actions played since the beginning of the game
     * @param actionToApply the consumer to execute an action
     * @return the created actions UI
     */
    public static Node create(ObservableValue<List<String>> actionsO, Consumer<String> actionToApply) {
        HBox container = new HBox();
        container.getStylesheets().add("/actions.css");
        container.setId("actions");

        Text fourPreviousActionsText = new Text();
        fourPreviousActionsText.textProperty().bind(actionsO.map(actions -> {
            StringBuilder s = new StringBuilder();
            List<String> fourPreviousActions = actions.size() < 4 ? actions
                    : actions.subList(actions.size() - 4, actions.size());

            int index = actions.size() / 4;
            for (int i = 0; i < fourPreviousActions.size(); ++i) {
                s.append(STR."\{index + i + 1}:\{fourPreviousActions.get(i)} ");
            }
            return s.toString();
        }));

        TextField actionField = new TextField();
        actionField.setId("action-field");

        actionField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            change.getText().chars().forEach(i -> {
                String c = String.valueOf((char) i);
                if (!Base32.isValid(String.valueOf(c)))
                    change.setText(change.getText().replace(c, ""));
            });
            return change;
        }));

        actionField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                actionToApply.accept(actionField.getText());
                actionField.clear();
            }
        });

        container.getChildren().add(fourPreviousActionsText);
        container.getChildren().add(actionField);
        return container;
    }
}
