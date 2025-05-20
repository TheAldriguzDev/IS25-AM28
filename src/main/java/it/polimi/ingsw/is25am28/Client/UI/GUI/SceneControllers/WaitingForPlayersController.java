package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Map;

public class WaitingForPlayersController extends GUIController {
    @FXML private VBox connectedPlayers;
    @FXML private Label playerCounters;
    @FXML private Label loadingLabel;

    private String base = "Waiting";

    public void initialize() {
        startLoadingAnimation();
    }

    public void showConnectedPlayers(WaitPlayersStateDTO state) {
        int i = 1;

        if (state.getUsedNicknames().size() == state.getLobbyTotalSpot()) {
            playerCounters.setText("All the players are connected. Starting the game!");
            this.base = "Initializing game";
        } else {
            playerCounters.setText("Waiting for more players to connect (" + state.getUsedNicknames().size() + "/" + state.getLobbyTotalSpot() + ")");
        }

        connectedPlayers.getChildren().clear();

        for (Map.Entry<String, PlayerColor> player : state.getUsedNicknames().entrySet()) {
            Label playerLabel = new Label(player.getKey());
            playerLabel.getStyleClass().add("player-label");

            String fxColor = mapColor(player.getValue());
            playerLabel.setStyle("-fx-text-fill: " + fxColor + ";");

            connectedPlayers.getChildren().add(playerLabel);
        }
    }

    private void startLoadingAnimation() {
        Timeline loadingAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> loadingLabel.setText(base + ".")),
                new KeyFrame(Duration.seconds(0.5), e -> loadingLabel.setText(base + "..")),
                new KeyFrame(Duration.seconds(1), e -> loadingLabel.setText(base + "...")),
                new KeyFrame(Duration.seconds(1.5), e -> loadingLabel.setText(base + "...."))
        );
        loadingAnimation.setCycleCount(Animation.INDEFINITE);
        loadingAnimation.play();
    }
}
