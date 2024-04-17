package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public final class DecksUI {

    /**
     * Non-instantiable class constructor
     */
    private DecksUI() {
    }

    public static Node create(ObservableValue<Tile> tileToPlaceO, ObservableValue<Integer> normalTileDeckSizeO,
                              ObservableValue<Integer> menhirTileDeckSizeO, ObservableValue<String> textToDisplayO,
                              Consumer<Occupant> occupantConsumer) {
        VBox scene = new VBox();
        scene.getStylesheets().add("/decks.css");
        scene.setId("decks");

        // TODO: Implement the decks UI
        return scene;
    }
}
