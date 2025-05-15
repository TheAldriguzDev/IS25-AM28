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

public class JoinGameController {
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
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource("/GUI/FXML/joinGame.fxml")
                )
            );

            GUIHandler.setCommandCTX(
                new CommandCTX(
                    "joinGame",
                    () -> {
                        // TODO: Determine if something needs to be added here
                        System.out.println("joinGame -> onSuccess");
                    },
                    () -> {
                        // TODO: Determine if something needs to be added here
                        System.out.println("joinGame -> onError");
                    }
                )
            );

            try {
                GUIHandler.getVirtualClient().sendMessage(
                    new NewPlayer(
                        this.nicknameTextField.getText().trim().strip(),
                        this.colorComboBox.getValue(),
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
