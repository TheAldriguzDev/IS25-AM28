package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Objects;

public class CreateGameController extends GUIController {
    @FXML private TextField nicknameTextField;
    @FXML private ComboBox<PlayerColor> colorComboBox;
    @FXML private ComboBox<Integer> levelComboBox;
    @FXML private ComboBox<Integer> lobbySizeComboBox;

    public void init() {
        this.nicknameTextField.setText(null);

        this.colorComboBox.getItems().clear();
        for (PlayerColor color : PlayerColor.values()) {
            this.colorComboBox.getItems().add(color);
        }

        this.levelComboBox.getItems().clear();
        this.levelComboBox.getItems().addAll(0, 2);

        this.lobbySizeComboBox.getItems().clear();
        this.lobbySizeComboBox.getItems().addAll(2, 3, 4);
    }

    private String parseNickname(String nickname) {
        return nickname.trim().strip();
    }

    public void onSubmitGameConfigButtonClick(ActionEvent actionEvent) {
        String nickname = this.nicknameTextField.getText() == null ? "" : this.nicknameTextField.getText().trim().strip();
        PlayerColor color = this.colorComboBox.getValue();
        Integer level = this.levelComboBox.getValue();
        Integer lobbySize = this.lobbySizeComboBox.getValue();

        Platform.runLater(() -> {

            // Validate the input
            if (nickname.isBlank()) {
                this.showToast("Nickname cannot be empty.", ToastType.ERROR);
                return;
            }
            if (color == null) {
                this.showToast("You must select a color.", ToastType.ERROR);
                return;
            }
            if (level == null) {
                this.showToast("You must select a game level.", ToastType.ERROR);
                return;
            }
            if (lobbySize == null) {
                this.showToast("You must select a lobby size.", ToastType.ERROR);
                return;
            }
            if (lobbySize < 2 || lobbySize > 4) {
                this.showToast("Lobby size must be between 2 and 4.", ToastType.ERROR);
                return;
            }

            GUIHandler.getInstance().getClientModel().setNickname(nickname);
            GUIHandler.getInstance().getClientModel().setDifficultyLevel(level);

            try {
                GUIHandler.getVirtualClient().sendMessage(
                    new ConfigGame(
                        nickname,
                        color,
                        level,
                        lobbySize
                    )
                );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
