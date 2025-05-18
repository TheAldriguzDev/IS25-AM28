package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Network.Messages.Reconnect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ReconnectGameController extends GUIController {
    @FXML private TextField nicknameTextField;
    @FXML private Button submitReconnectGameButton;

    public void onSubmitReconnectGameButtonClick(ActionEvent actionEvent) {
        String nickname = this.nicknameTextField.getText() == null ? "" : this.nicknameTextField.getText().trim().strip();

        Platform.runLater(() -> {
            // Validate the input
            if (nickname.isBlank()) {
                showError("Nickname cannot be empty.");
                return;
            }

            GUIHandler.getInstance().getClientModel().setNickname(nickname);

            try {
                GUIHandler.getVirtualClient().sendMessage(
                    new Reconnect(nickname)
                );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
