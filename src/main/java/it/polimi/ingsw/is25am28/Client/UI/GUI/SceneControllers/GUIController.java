package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public StackPane getRootPane() {
        return rootPane;
    }

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

    protected String mapColor(PlayerColor color) {
        return switch (color) {
            case BLUE -> "#4da6ff";
            case RED -> "#ff4d4d";
            case GREEN -> "#66ff66";
            case YELLOW -> "#ffeb3b";
        };
    }










}
