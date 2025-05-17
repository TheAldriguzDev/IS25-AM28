package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Objects;

public class JoinGameController extends GUIController {
    @FXML private Label joinGameTitleLabel;
    @FXML private TextField nicknameTextField;
    @FXML private ComboBox<PlayerColor> colorComboBox;

    private int gameID;

    public void init(GameInfoDTO state) {
        this.gameID = state.getId();

        this.joinGameTitleLabel.setText("Joining Game with ID=" + this.gameID);
        this.joinGameTitleLabel.setWrapText(true);

        this.nicknameTextField.setText(null);
        this.colorComboBox.getItems().clear();

        List<PlayerColor> availableColors = state.getAvailableColors().stream()
                .filter(Objects::nonNull)
                .map(PlayerColor::fromString)
                .filter(Objects::nonNull)
                .toList();

        for (PlayerColor color : availableColors) {
            this.colorComboBox.getItems().add(color);
        }
    }

    public void onSubmitJoinGameButtonClick(ActionEvent actionEvent) {
        String nickname = this.nicknameTextField.getText() == null ? "" : this.nicknameTextField.getText().trim().strip();
        PlayerColor color = this.colorComboBox.getValue();

        Platform.runLater(() -> {
            // Validate the input
            if (nickname.isBlank()) {
                showError("Nickname cannot be empty.");
                return;
            }
            if (color == null) {
                showError("Please select a color.");
                return;
            }

            try {
                GUIHandler.getVirtualClient().sendMessage(
                    new NewPlayer(
                        nickname,
                        color,
                        this.gameID
                    )
                );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
