package it.polimi.ingsw.is25am28.Client.UI.GUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.Network.Socket.Client.TCPClient;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.*;

public class GUIHandler extends Application implements ClientUI {
    private static GUIHandler instance;

    // connectionType is used to create the RMI or the Socket client
    private static int connectionType;
    // model is the reference to the clientModel
    private static ClientModel model;
    // virtualClient is the reference to the client network protocol
    private static VirtualView virtualClient;

    private static CommandCTX ctx;

    // ========== ATTRIBUTES NEEDED TO HANDLE THE GUI ========== //
    private Stage stage;

    // Stores each page root to avoid page reloads when switching scenes
    private final Map<GuiScenes, Parent> roots = new HashMap<>();
    // Stores the controller instance for each page, when created
    private final Map<GuiScenes, Object> controllers = new HashMap<>();
    // Stores the current page scene
    private GuiScenes currentScene;

    public static GUIHandler getInstance() {
        return instance;
    }

    public static void setConnectionType(int connectionType) {
        GUIHandler.connectionType = connectionType;
    }

    public static void setClientModel(ClientModel model) {
        GUIHandler.model = model;
    }

    public ClientModel getClientModel() {
        return GUIHandler.model;
    }

    public static VirtualView getVirtualClient() {
        return GUIHandler.virtualClient;
    }

    public static CommandCTX getCommandCTX() {
        return GUIHandler.ctx;
    }

    public static void setCommandCTX(CommandCTX ctx) {
        GUIHandler.ctx = ctx;
    }

    public static void clearCommandCTX() {
        GUIHandler.ctx = null;
    }

    public Stage getStage() {
        return this.stage;
    }

    /**
     * Handles the quit action when the user attempts to close the application window.
     * Displays a confirmation dialog to the user, prompting them to confirm or cancel the quit action.
     *
     * @param windowEvent the event triggered when the window close request is initiated.
     *                    If the user cancels the quit action, the event is consumed to prevent the window from closing.
     */
    public static void onQuitHandler(WindowEvent windowEvent) {
        Alert quitConfirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

        quitConfirmationAlert.setTitle("Quit Galaxy Trucker");
        quitConfirmationAlert.setHeaderText("You're about to quit Galaxy Trucker");
        quitConfirmationAlert.setContentText("Do you want to proceed?");

        Optional<ButtonType> result = quitConfirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("here");
            GUIHandler.getInstance().getStage().close();
            System.exit(0);
        } else {
            windowEvent.consume();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;

        if (connectionType == 1) {
            this.virtualClient = new RMIClient("127.0.0.1", 7777, UUID.randomUUID(), this, model);
        } else {
            this.virtualClient = new TCPClient("127.0.0.1", 8888, this, model);
        }

        // ========== BUILD THE INITIAL SCREEN OF THE GAME ========== //

        this.stage = stage;
        this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/GUI/FXML/login.fxml")));
        stage.setTitle("Galaxy Trucker");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);

        Platform.runLater(stage::show);
    }

    @Override
    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception {
        Platform.runLater(() -> {

            // Check if the LOBBY_SCENE is already loaded
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.LOBBY_SCENE)) {
                LobbyController controller = (LobbyController) this.controllers.get(GuiScenes.LOBBY_SCENE);
                controller.init(availableGames);

                return;
            }

            // Otherwise load the new page, store the root, store the controller and init the page content

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.LOBBY_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                LobbyController controller = loader.getController();

                // Store the root and the controller
                controllers.put(GuiScenes.LOBBY_SCENE, controller);
                roots.put(GuiScenes.LOBBY_SCENE, root);
                controller.init(availableGames);

                Scene scene = new Scene(root);
                this.stage.setScene(scene);
                this.stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.currentScene = GuiScenes.LOBBY_SCENE;
            }
        });
    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        Platform.runLater(() -> {

            if (this.currentScene != null && this.currentScene.equals(GuiScenes.WAITING_FOR_PLAYERS_SCENE)) {
                WaitingForPlayersController controller = (WaitingForPlayersController) this.controllers.get(GuiScenes.WAITING_FOR_PLAYERS_SCENE);
                controller.showConnectedPlayers(waitingForPlayers);

                return;
            }

            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource(GuiScenes.WAITING_FOR_PLAYERS_SCENE.getFxmlFile())
                )
            );

            try {
                Parent root = loader.load();
                WaitingForPlayersController controller = loader.getController();

                // Store the root and the controller
                controllers.put(GuiScenes.WAITING_FOR_PLAYERS_SCENE, controller);
                roots.put(GuiScenes.WAITING_FOR_PLAYERS_SCENE, root);

                controller.showConnectedPlayers(waitingForPlayers);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.currentScene = GuiScenes.WAITING_FOR_PLAYERS_SCENE;
            }
        });
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        Platform.runLater(() -> {
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
                ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);
                controller.initShipConstruction();

                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.SHIP_CONSTRUCTION_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                ShipConstructionController controller = loader.getController();

                // Store the root and the controller
                controllers.put(GuiScenes.SHIP_CONSTRUCTION_SCENE, controller);
                roots.put(GuiScenes.SHIP_CONSTRUCTION_SCENE, root);

                controller.initShipConstruction();

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.currentScene = GuiScenes.SHIP_CONSTRUCTION_SCENE;
            }
        });
    }

    // TODO: Understand if we need to create an interface for the GUI that is more specific than the TUI
    public void updateShipConstructionComponent(ConstructionComponentDTO component) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
            ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);
            controller.updateComponent(component.getId());
        }
    }

    // TODO: Understand if we need to create an interface for the GUI that is more specific than the TUI
    public void updateShipPlacedComponent(PlacedComponentDTO data) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
            ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);
            controller.handlePlayerShipConstruction(data);
        }
    }

    public void enableTimer(TimerDTO data) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
            ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);
            //controller.handlePlayerShipConstruction((data));
        }
    }

    public void updateShipRemovedComponent(FixedComponentDTO data) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.FIX_SHIP_SCENE)) {
            FixShipController controller = (FixShipController) this.controllers.get(GuiScenes.FIX_SHIP_SCENE);
            controller.removeComponent(data.getI(), data.getJ(), data.isShipFixed());
        }
    }

    @Override
    public void showShipFixing(FixShipDTO fixShip) throws Exception {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.FIX_SHIP_SCENE.getFxmlFile())
                    )
            );

            try {

                Parent root = loader.load();
                FixShipController controller = loader.getController();

                controller.init(fixShip);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.POPULATE_SHIP_SCENE.getFxmlFile())
                    )
            );

            try {

                Parent root = loader.load();
                FixShipController controller = loader.getController();

                //...

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void showCardRound(CardRoundDTO cardRound) throws Exception {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.POPULATE_SHIP_SCENE.getFxmlFile())
                    )
            );

            try {

                Parent root = loader.load();
                FixShipController controller = loader.getController();

                //...

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void showEndGame(EndGameDTO endGame) {
        System.out.println("END GAME DTO ARRIVED");

        Platform.runLater(() -> {

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.END_GAME_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                EndGameController controller = loader.getController();

                controller.setLeaderBoard(endGame);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        Platform.runLater(() -> {

            if (this.currentScene != null && this.currentScene.equals(GuiScenes.INSUFFICIENT_PLAYER_SCENE)) {
                InsufficientPlayersController controller = (InsufficientPlayersController) this.controllers.get(GuiScenes.INSUFFICIENT_PLAYER_SCENE);
                controller.setCountDown(insufficientPlayer);

                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.INSUFFICIENT_PLAYER_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                InsufficientPlayersController controller = loader.getController();

                // Store the root and the controller
                this.saveRootAndController(GuiScenes.INSUFFICIENT_PLAYER_SCENE, root, controller);

                controller.setCountDown(insufficientPlayer);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.currentScene = GuiScenes.INSUFFICIENT_PLAYER_SCENE;
            }
        });
    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {

    }

    @Override
    public void commitCommand(String playerNickname) {
        if (ctx != null && playerNickname.equals(model.getNickname())) {
            ctx.handleSuccess();
        }
    }

    @Override
    public void showError(ErrorAnswer error) {
        if (ctx != null) {
            ctx.handleError(error.getError());
        } else {
            // Terminal output
            System.err.println(error.getError());
        }

        GUIController controller = (GUIController) this.controllers.get(this.currentScene);

        if (controller == null) {
            System.err.println("No controller has been found for the current scene");
            return;
        }

        Platform.runLater(() -> controller.showError(error.getError()));
    }


    @Override
    public boolean isCTXAvailable() {
        return (ctx != null);
    }

    @Override
    public void setVirtualClient(VirtualView client) {
        // Not used in the GUI since the instance is handled by JavaFX
        // (no instance of GUIHandler is available to invoke the method before launching the GUI)
    }

    public void saveRootAndController(GuiScenes scene, Parent root, Object controller) {
        this.controllers.put(scene, controller);
        this.roots.put(scene, root);
    }

    public void switchScene(GuiScenes scene) {
        this.currentScene = scene;
    }
}
