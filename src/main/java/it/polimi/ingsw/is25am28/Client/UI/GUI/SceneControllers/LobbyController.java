package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Network.Messages.RefreshGames;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class LobbyController {
    @FXML private Button createGameButton;
    @FXML private Button reconnectGameButton;
    @FXML private Button quitButton;

    @FXML private Button refreshGamesButton;
    @FXML private ScrollPane lobbyListScrollPane;

    public void init(AvailableGamesDTO state) {
        VBox gameList = new VBox();

        gameList.setSpacing(10);

        this.lobbyListScrollPane.getChildrenUnmodifiable().clear();
        this.lobbyListScrollPane.setFitToWidth(true);
        this.lobbyListScrollPane.setFitToHeight(true);
        this.lobbyListScrollPane.setContent(gameList);

        for (GameInfoDTO game : state.getAvailableGames()) {
            Button joinGameButton = new Button();

            joinGameButton.setText("GameID=" + game.getId() + " (" + game.getActualPlayers() + "/" + game.getTotalPlayers() + ")");
            joinGameButton.setWrapText(true);
            joinGameButton.getStyleClass().add("button");

            joinGameButton.setOnAction(
                (event) -> {
                     this.onJoinGameButtonClick(event, game);
                }
            );

            gameList.getChildren().add(joinGameButton);
        }
    }

    public void onCreateGameButtonClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource("/GUI/FXML/createGame.fxml")
                )
            );

            try {
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                CreateGameController controller = loader.getController();
                controller.init();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onJoinGameButtonClick(ActionEvent actionEvent, GameInfoDTO state) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource("/GUI/FXML/joinGame.fxml")
                )
            );

            try {
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                JoinGameController controller = loader.getController();
                controller.init(state);

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onReconnectGameButtonClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource("/GUI/FXML/reconnectGame.fxml")
                )
            );

            try {
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onRefreshGamesButtonClick(ActionEvent actionEvent) {
        GUIHandler.setCommandCTX(
            new CommandCTX(
                "refreshGames",
                () -> {
                    // TODO: Determine if something needs to be added here
                    System.out.println("refreshGames -> onSuccess");
                },
                () -> {
                    // TODO: Determine if something needs to be added here
                    System.out.println("refreshGames -> onSuccess");
                }
            )
        );

        try {
            GUIHandler.getVirtualClient().sendMessage(new RefreshGames());
        }
        catch (Exception e) {
            // TODO: Determine if something needs to be added here
            System.out.println("refreshGames -> sendMessage threw an exception");
            e.printStackTrace();
        }
    }

    public void onQuitButtonClick(javafx.event.ActionEvent actionEvent) {
        GUIHandler.onQuitHandler(null);
    }
}
