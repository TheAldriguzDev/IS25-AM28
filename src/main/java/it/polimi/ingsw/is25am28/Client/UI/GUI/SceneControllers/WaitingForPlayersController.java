package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Map;

public class WaitingForPlayersController {
    @FXML private VBox connectedPlayers;
    @FXML private Label playerCounters;

    public void showConnectedPlayers(WaitPlayersStateDTO state) {
        int i = 1;

        this.playerCounters.setText("Waiting for players... (" + state.getUsedNicknames().size() + "/" + state.getLobbyTotalSpot() + ")");
        this.playerCounters.setWrapText(true);

        this.connectedPlayers.getChildren().clear();

        for (Map.Entry<String, PlayerColor> player : state.getUsedNicknames().entrySet()) {
            Label playerLabel = new Label();

            playerLabel.setText("(" + (i++) + ") - " + player.getKey());
            playerLabel.setWrapText(true);
            playerLabel.setStyle("-fx-font-size: 18; -fx-text-fill: " + player.getValue().toString().toLowerCase());

            this.connectedPlayers.getChildren().add(playerLabel);
        }
    }
}
