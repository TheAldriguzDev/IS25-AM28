package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Network.Messages.Reconnect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ReconnectGameController {
    @FXML private TextField nicknameTextField;
    @FXML private Button submitReconnectGameButton;

    public void onSubmitReconnectGameButtonClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            GUIHandler.setCommandCTX(
                new CommandCTX(
                    "reconnectGame",
                    () -> {
                        // TODO: Determine if something needs to be added here
                        System.out.println("reconnectGame -> onSuccess");
                    },
                    () -> {
                        // TODO: Determine if something needs to be added here
                        System.out.println("reconnectGame -> onError");
                    }
                )
            );

            try {
                GUIHandler.getVirtualClient().sendMessage(
                    new Reconnect(this.nicknameTextField.getText().trim().strip())
                );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
