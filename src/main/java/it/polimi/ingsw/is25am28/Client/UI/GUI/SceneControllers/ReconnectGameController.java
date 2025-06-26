package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * ReconnectGameController is responsible for handling the user interface logic
 * related to reconnecting to a game.
 */
public class ReconnectGameController extends GUIController {
    @FXML private TextField nicknameTextField;
    @FXML private Button submitReconnectGameButton;

    /**
     * Handles the action when the "Reconnect Game" button is clicked.
     * Validates the user's nickname input and initiates the reconnection process using the provided nickname.
     * If the nickname is invalid, an error toast message is displayed.
     *
     * @param actionEvent the event triggered by the "Reconnect Game" button click
     */
    public void onSubmitReconnectGameButtonClick(ActionEvent actionEvent) {
        String nickname = this.nicknameTextField.getText() == null ? "" : this.nicknameTextField.getText().trim().strip();

        Platform.runLater(() -> {
            // Validate the input
            if (nickname.isBlank()) {
                this.showToast("Nickname cannot be empty.", ToastType.ERROR);
                return;
            }

            GUIHandler.getClientModel().setNickname(nickname);

            try {
                GUIHandler.getVirtualClient().reconnectClient(
                    nickname
                );
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }
}
