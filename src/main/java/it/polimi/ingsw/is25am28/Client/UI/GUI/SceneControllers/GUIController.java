package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.Map;

public abstract class GUIController {
    protected ClientModel clientModel;
    protected GUIUtils guiUtils;

    Pair<Integer, Integer> shipOffsets;

    // Map of every ship's component images
    protected Map<String, Map<String, ImageView>> componentsImagesMap;

    // Map of every ship's grid
    protected Map<String, GridPane> playersShipGridPane;

    @FXML
    protected StackPane rootPane;


    /**
     * @return the root pane of the GUI.
     */
    public StackPane getRootPane() {
        return rootPane;
    }


    /**
     * Displays a toast message with the given text and type. The toast fades in,
     * remains visible for a short duration, and then fades out before being removed
     * from the user interface.
     *
     * @param text the text content to be displayed in the toast message
     * @param type the {@link ToastType} indicating the style or category of the toast
     */
    public void showToast(String text, ToastType type) {
        Label errorLabel = new Label(text);
        errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox container = new HBox(errorLabel);
        container.getStyleClass().add("toast-" + type.toString() + "-container");
        container.setPrefWidth(Region.USE_COMPUTED_SIZE);
        container.setMinWidth(Region.USE_PREF_SIZE);
        container.setMaxWidth(Region.USE_PREF_SIZE);

        container.setPrefHeight(Region.USE_COMPUTED_SIZE);
        container.setMinHeight(Region.USE_PREF_SIZE);
        container.setMaxHeight(Region.USE_PREF_SIZE);
        container.setOpacity(0);

        StackPane.setAlignment(container, Pos.TOP_RIGHT);
        StackPane.setMargin(container, new Insets(20, 20, 0, 0));

        Platform.runLater(() -> {
            rootPane.getChildren().add(container);

            SequentialTransition sequence = getTransition(container);
            sequence.play();
        });
    }


    /**
     * Creates a sequential transition that applies a fade-in effect, a pause, and a fade-out effect
     * to the specified error box. Once the fade-out is complete, the error box is removed from the root pane.
     *
     * @param errorBox the HBox instance representing the error box to which the transitions will be applied
     * @return a {@link SequentialTransition} consisting of a fade-in, pause, and fade-out animation on the given error box
     */
    private SequentialTransition getTransition(HBox errorBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), errorBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), errorBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(errorBox));

        return new SequentialTransition(fadeIn, pause, fadeOut);
    }

    /**
     * @param color to be mapped to a hex code
     * @return a hex code based on the input playerColor
     */
    protected String mapColor(PlayerColor color) {
        return switch (color) {
            case BLUE -> "#4da6ff";
            case RED -> "#ff4d4d";
            case GREEN -> "#66ff66";
            case YELLOW -> "#ffeb3b";
        };
    }

    // Method used to set the visibility of a certain node

    /**
     * Sets the visibility and managed state of a given JavaFX Node.
     *
     * @param <T>      the type of the JavaFX Node, which extends {@link Node}
     * @param node     the Node whose visibility is to be set
     * @param visible  true to make the Node visible and managed, false to make it invisible and unmanaged
     */
    protected <T extends Node> void setVisibility(T node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
