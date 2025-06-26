package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Objects;

/**
 * This controller class is responsible for handling the "Join Game" interface of the application.
 */
public class JoinGameController extends GUIController {
    @FXML private Label joinGameTitleLabel;
    @FXML private TextField nicknameTextField;
    @FXML private ComboBox<PlayerColor> colorComboBox;

    private int gameID;
    private int gameLevel;

    /**
     * Initializes the game interface with the provided game state information.
     * Sets up the game ID, game level, label text, and populates the color selection dropdown.
     * Clears any previous data from the nickname field and color dropdown.
     *
     * @param state The GameInfoDTO containing the game data to be used for initialization.
     */
    public void init(GameInfoDTO state) {
        this.gameID = state.getId();
        this.gameLevel = state.getLevel();

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

    /**
     * Handles the event triggered when the "Join Game" button is clicked.
     * Validates user input and performs the process of joining a game using the provided nickname
     * and selected player color. Displays appropriate error messages for invalid inputs.
     *
     * @param actionEvent The ActionEvent associated with the button click.
     */
    public void onSubmitJoinGameButtonClick(ActionEvent actionEvent) {
        String nickname = this.nicknameTextField.getText() == null ? "" : this.nicknameTextField.getText().trim().strip();
        PlayerColor color = this.colorComboBox.getValue();

        Platform.runLater(() -> {
            // Validate the input
            if (nickname.isBlank()) {
                this.showToast("Nickname cannot be empty.", ToastType.ERROR);
                return;
            }
            if (color == null) {
                this.showToast("Please select a color.", ToastType.ERROR);
                return;
            }

            GUIHandler.getInstance().getClientModel().setNickname(nickname);
            GUIHandler.getInstance().getClientModel().setDifficultyLevel(this.gameLevel);

            try {
                GUIHandler.getVirtualClient().joinGame(
                    nickname,
                    color,
                    this.gameID
                );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
