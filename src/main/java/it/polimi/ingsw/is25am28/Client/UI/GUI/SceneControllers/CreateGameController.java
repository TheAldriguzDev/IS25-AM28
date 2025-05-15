package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.util.Objects;

public class CreateGameController {
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
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource("/GUI/FXML/lobby.fxml")
                )
            );

            GUIHandler.setCommandCTX(
                new CommandCTX(
                    "configGame",
                    () -> {
                        // TODO: Determine if something needs to be added here
                        System.out.println("createGame -> onSuccess");
                    },
                    () -> {
                        System.out.println("createGame -> onError");
                    }
                )
            );

            try {
                GUIHandler.getVirtualClient().sendMessage(
                    new ConfigGame(
                        this.parseNickname(this.nicknameTextField.getText()),
                        this.colorComboBox.getValue(),
                        this.levelComboBox.getValue(),
                        this.lobbySizeComboBox.getValue()
                    )
                );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
