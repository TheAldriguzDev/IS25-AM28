package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class GUIController {
    protected ClientModel clientModel;

    @FXML
    protected StackPane rootPane;

    public StackPane getRootPane() {
        return rootPane;
    }

    public void showError(String error) {
        Label errorLabel = new Label(error);
        errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox container = new HBox(errorLabel);
        container.getStyleClass().add("toast-error-container");
        container.setPrefWidth(Region.USE_COMPUTED_SIZE);
        container.setMinWidth(Region.USE_PREF_SIZE);
        container.setMaxWidth(Region.USE_PREF_SIZE);

        container.setPrefHeight(Region.USE_COMPUTED_SIZE);
        container.setMinHeight(Region.USE_PREF_SIZE);
        container.setMaxHeight(Region.USE_PREF_SIZE);
        container.setOpacity(0);

        StackPane.setAlignment(container, Pos.TOP_RIGHT);
        StackPane.setMargin(container, new Insets(20, 20, 0, 0));

        rootPane.getChildren().add(container);

        SequentialTransition sequence = getTransition(container);
        sequence.play();
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
