package ch.epfl.chacun.gui;

import ch.epfl.chacun.Base32;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Helper class to display the actions interface.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class ActionUI {

    /**
     * The maximum number of actions to display at a time.
     */
    private static final int NUMBER_OF_ACTIONS_TO_DISPLAY = 4;

    /**
     * Non-instantiable class constructor.
     */
    private ActionUI() {
    }

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
        // Only display the last NUMBER_OF_ACTIONS_TO_DISPLAY actions
        fourPreviousActionsText.textProperty().bind(actionsO.map(actions -> {
            int numberOfAppliedActions = actions.size();
            int firstIndex = Math.max(0, numberOfAppliedActions - NUMBER_OF_ACTIONS_TO_DISPLAY);
            return IntStream.range(firstIndex, numberOfAppliedActions)
                    .mapToObj(i -> STR."\{i + 1}:\{actions.get(i)}")
                    .collect(Collectors.joining(", "));
        }));

        TextField actionField = new TextField();
        actionField.setId("action-field");
        // Sanitize the user input
        actionField.setTextFormatter(new TextFormatter<>(change -> {
            String sanitizedInput = sanitizeInput(change.getText());
            change.setText(sanitizedInput);
            return change;
        }));

        // Define the action to apply when the user presses enter
        actionField.setOnAction(_ -> {
            actionToApply.accept(actionField.getText());
            actionField.clear();
        });

        container.getChildren().addAll(fourPreviousActionsText, actionField);
        return container;
    }

    /**
     * Sanitizes the input by uppercasing it and only keeping the characters that are in the base 32 alphabet.
     * @param input the input to sanitize
     * @return the sanitized input
     */
    private static String sanitizeInput(String input) {
        StringBuilder sanitizedInput = new StringBuilder();
        for (char character : input.toUpperCase().toCharArray()) {
            if (Base32.ALPHABET.indexOf(character) != -1)
                sanitizedInput.append(character);
        }
        return sanitizedInput.toString();
    }
}
