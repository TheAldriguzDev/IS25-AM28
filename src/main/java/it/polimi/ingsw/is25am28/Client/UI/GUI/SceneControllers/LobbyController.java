package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GuiScenes;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * The LobbyController class manages the lobby screen of the GUI application, allowing users
 * to interact with available games, create new games, reconnect to a game, or exit the application.
 */
public class LobbyController extends GUIController {
    @FXML private Button createGameButton;
    @FXML private Button reconnectGameButton;
    @FXML private Button quitButton;

    @FXML private Button refreshGamesButton;
    @FXML private ScrollPane lobbyListScrollPane;


    /**
     * Initializes the lobby view by populating a list of available games and configuring the UI elements.
     * Each game is displayed with its details as a button, allowing the user to join the game.
     *
     * @param state The current state containing the list of available games to display in the lobby.
     */
    public void init(AvailableGamesDTO state) {
        VBox gameList = new VBox();
        gameList.setSpacing(10);
        gameList.setPadding(new Insets(10));
        gameList.setAlignment(Pos.TOP_CENTER);

        this.lobbyListScrollPane.setFitToWidth(true);
        this.lobbyListScrollPane.setFitToHeight(true);
        this.lobbyListScrollPane.setContent(gameList);

        for (GameInfoDTO game : state.getAvailableGames()) {
            Label gameIdLabel = new Label("🎮 Game ID: " + game.getId());
            Label playersLabel = new Label("👥 " + game.getActualPlayers() + "/" + game.getTotalPlayers() + " players");
            Label levelLabel = new Label("⭐ Level: " + game.getLevel());

            Stream.of(gameIdLabel, playersLabel, levelLabel).forEach(label -> {
                label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
                HBox.setHgrow(label, Priority.ALWAYS);
            });

            HBox content = new HBox(gameIdLabel, playersLabel, levelLabel);
            content.setSpacing(20);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(5, 10, 5, 10));
            content.setPrefWidth(Double.MAX_VALUE);

            Button joinGameButton = new Button();
            joinGameButton.setGraphic(content);
            joinGameButton.setPrefHeight(60);
            joinGameButton.setMaxWidth(Double.MAX_VALUE);
            joinGameButton.getStyleClass().add("game-button");

            joinGameButton.setOnAction(event -> this.onJoinGameButtonClick(event, game));

            VBox.setMargin(joinGameButton, new Insets(5, 20, 5, 20));
            gameList.getChildren().add(joinGameButton);
        }
    }


    /**
     * Handles the event triggered by clicking the "Create Game" button.
     * This method loads the CreateGame scene, initializes its controller,
     * and updates the GUI
     * @param actionEvent The action event triggered by clicking the "Create Game" button.
     */
    public void onCreateGameButtonClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource(GuiScenes.CREATE_GAME_SCENE.getFxmlFile())
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

                // Save the root and the controller
                GUIHandler handler = GUIHandler.getInstance();
                handler.saveRootAndController(GuiScenes.CREATE_GAME_SCENE, root, controller);
                handler.switchScene(GuiScenes.CREATE_GAME_SCENE);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Handles the event triggered when the "Join Game" button is clicked.
     * This method transitions the user to the JoinGame scene, initializes its controller with
     * the provided game information and updates the GUI
     *
     * @param actionEvent The action event triggered by clicking the "Join Game" button.
     * @param state The information related to the selected game, such as game ID, level,
     *              total players, actual players, and available colors.
     */
    public void onJoinGameButtonClick(ActionEvent actionEvent, GameInfoDTO state) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource(GuiScenes.JOIN_GAME_SCENE.getFxmlFile())
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

                // Save the root and the controller
                GUIHandler handler = GUIHandler.getInstance();
                handler.saveRootAndController(GuiScenes.JOIN_GAME_SCENE, root, controller);
                handler.switchScene(GuiScenes.JOIN_GAME_SCENE);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Handles the event triggered by clicking the "Reconnect Game" button.
     * This method transitions to the ReconnectGame scene, initializes its controller and
     * updates the GUI
     *
     * @param actionEvent The action event triggered by clicking the "Reconnect Game" button.
     */
    public void onReconnectGameButtonClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource(GuiScenes.RECONNECT_GAME_SCENE.getFxmlFile())
                )
            );

            try {
                Parent root = loader.load();
                ReconnectGameController controller = loader.getController();

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                // Save the root and the controller
                GUIHandler handler = GUIHandler.getInstance();
                handler.saveRootAndController(GuiScenes.RECONNECT_GAME_SCENE, root, controller);
                handler.switchScene(GuiScenes.RECONNECT_GAME_SCENE);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                GUIHandler.getInstance().switchScene(GuiScenes.RECONNECT_GAME_SCENE);
            }
        });
    }

    /**
     * Handles the event triggered by clicking the "Refresh Games" button.
     * This method requests the virtual client to refresh the list of available games.
     *
     * @param actionEvent The action event triggered by clicking the "Refresh Games" button.
     */
    public void onRefreshGamesButtonClick(ActionEvent actionEvent) {
        try {
            GUIHandler.getVirtualClient().refreshGames();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the event triggered by clicking the "Quit" button.
     * This method closes the application window and terminates the program.
     *
     * @param actionEvent The action event triggered by clicking the "Quit" button.
     */
    public void onQuitButtonClick(ActionEvent actionEvent) {
        GUIHandler.getInstance().getStage().close();
        System.exit(0);
    }
}
