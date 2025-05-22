package it.polimi.ingsw.is25am28.Client.UI.GUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers.*;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
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
    public static final String GALAXY_TRUCKER = "Galaxy Trucker";

    private static GUIHandler instance;
    private static ClientModel model;
    private static VirtualView virtualClient;
    private static CommandCTX ctx;

    // ========== ATTRIBUTES NEEDED TO HANDLE THE GUI ========== //

    /**
     * The stage where each GUI screen will live
     */
    private Stage stage;

    /**
     * Stores each page root to avoid page reloads when switching scenes
     */
    private final Map<GuiScenes, Parent> roots = new HashMap<>();

    /**
     * Stores the controller instance for each page, when created
     */
    private final Map<GuiScenes, GUIController> controllers = new HashMap<>();

    /**
     * Stores the current page scene
     */
    private GuiScenes currentScene;

    public static GUIHandler getInstance() {
        return GUIHandler.instance;
    }

    public static ClientModel getClientModel() {
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
            GUIHandler.getInstance().getStage().close();
            System.exit(0);
        }
        else {
            windowEvent.consume();
        }
    }

    // Constructor
    public GUIHandler(ClientModel model) {
        GUIHandler.instance = this;
        GUIHandler.model = model;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;

        // Builds the initial screen of the game
        this.stage = stage;
        this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getResource(GuiScenes.LOBBY_SCENE.getFxmlFile())
                )
        );

        Scene scene = new Scene(root);
        stage.setTitle(GALAXY_TRUCKER);
        stage.setScene(scene);

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

            // Otherwise load the new page, store the root, store the
            // controller and init the page content
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.LOBBY_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                LobbyController controller = loader.getController();

                // Store the root and the controller
                this.saveRootAndController(GuiScenes.LOBBY_SCENE, root, controller);

                controller.init(availableGames);

                Scene scene = new Scene(root);
                this.stage.setScene(scene);
                this.stage.show();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                this.currentScene = GuiScenes.LOBBY_SCENE;
            }
        });
    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        // Check if the WAITING_FOR_PLAYERS_SCENE is already loaded
        Platform.runLater(() -> {
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.WAITING_FOR_PLAYERS_SCENE)) {
                WaitingForPlayersController controller = (WaitingForPlayersController) this.controllers.get(GuiScenes.WAITING_FOR_PLAYERS_SCENE);
                controller.showConnectedPlayers(waitingForPlayers);
                return;
            }

            // Otherwise load the new page, store the root, store the
            // controller and init the page content
            FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                    getClass().getResource(GuiScenes.WAITING_FOR_PLAYERS_SCENE.getFxmlFile())
                )
            );

            try {
                Parent root = loader.load();
                WaitingForPlayersController controller = loader.getController();

                // Store the root and the controller
                this.saveRootAndController(GuiScenes.WAITING_FOR_PLAYERS_SCENE, root, controller);

                controller.showConnectedPlayers(waitingForPlayers);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                this.currentScene = GuiScenes.WAITING_FOR_PLAYERS_SCENE;
            }
        });
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        // Check if the SHIP_CONSTRUCTION_SCENE is already loaded
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
            ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);
            Platform.runLater(controller::initShipConstruction);
            return;
        }

        // Otherwise load the new page, store the root, store the
        // controller and init the page content
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                        getClass().getResource(GuiScenes.SHIP_CONSTRUCTION_SCENE.getFxmlFile())
                )
        );

        try {
            Parent root = loader.load();
            ShipConstructionController controller = loader.getController();

            // Store the root and the controller
            this.saveRootAndController(GuiScenes.SHIP_CONSTRUCTION_SCENE, root, controller);

            controller.initShipConstruction();

            Platform.runLater(() -> {
                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                stage.show();
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            this.currentScene = GuiScenes.SHIP_CONSTRUCTION_SCENE;
        }
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

    // TODO: Make a public interface shared between the TUI and GUI (UI) that implements this methods --> in the TUI this methods will be empty
    public void placePlayerInTheBoard(PlayerEndedShipDTO data) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
            ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);
            controller.placePlayerInTheBoard(data);
        }
    }

    public void updateShipRemovedComponent(FixedComponentDTO data) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.FIX_SHIP_SCENE)) {
            FixShipController controller = (FixShipController) this.controllers.get(GuiScenes.FIX_SHIP_SCENE);
            controller.removeComponent(data);
        }
    }

    public void updateShipPlacedLifeForm(PopulateShipComponentDTO data) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.POPULATE_SHIP_SCENE)) {
            PopulateShipController controller = (PopulateShipController) this.controllers.get(GuiScenes.POPULATE_SHIP_SCENE);
            controller.placeLifeform(data);
        }
    }

    @Override
    public void showShipFixing(FixShipDTO fixShip) throws Exception {
        Platform.runLater(() -> {
            // Check if the FIX_SHIP_SCENE is already loaded
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.FIX_SHIP_SCENE)) {

                return;
            }

            // Otherwise load the new page, store the root, store the
            // controller and init the page content
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.FIX_SHIP_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                FixShipController controller = loader.getController();

                // Store the root and the controller
                this.saveRootAndController(GuiScenes.FIX_SHIP_SCENE, root, controller);

                controller.init(fixShip);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                this.currentScene = GuiScenes.FIX_SHIP_SCENE;
            }
        });

    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {
        Platform.runLater(() -> {
            // Check if the POPULATE_SHIP_SCENE is already loaded
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.POPULATE_SHIP_SCENE)) {

                return;
            }

            // Otherwise load the new page, store the root, store the
            // controller and init the page content
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.POPULATE_SHIP_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                PopulateShipController controller = loader.getController();

                // Store the root and the controller
                this.saveRootAndController(GuiScenes.POPULATE_SHIP_SCENE, root, controller);

                controller.init(populateShip);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                this.currentScene = GuiScenes.POPULATE_SHIP_SCENE;
            }
        });
    }

    @Override
    public void showCardRound(CardRoundDTO cardRound) throws Exception {
        Platform.runLater(() -> {
            // Check if the CARD_ROUND_SCENE is already loaded
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.CARD_ROUND_SCENE)) {

                return;
            }

            // Otherwise load the new page, store the root, store the
            // controller and init the page content
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(GuiScenes.CARD_ROUND_SCENE.getFxmlFile())
                    )
            );

            try {
                Parent root = loader.load();
                CardRoundController controller = loader.getController();

                // Store the root and controller
                this.saveRootAndController(GuiScenes.CARD_ROUND_SCENE, root, controller);

                controller.init(cardRound);

                Scene newScene = new Scene(root);
                this.stage.setOnCloseRequest(GUIHandler::onQuitHandler);
                this.stage.setScene(newScene);
                this.stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.currentScene = GuiScenes.CARD_ROUND_SCENE;
            }
        });
    }

    @Override
    public void showEndGame(EndGameDTO endGame) {
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
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        Platform.runLater(() -> {
            // Check if the INSUFFICIENT_PLAYER_SCENE is already loaded
            if (this.currentScene != null && this.currentScene.equals(GuiScenes.INSUFFICIENT_PLAYER_SCENE)) {
                InsufficientPlayersController controller = (InsufficientPlayersController) this.controllers.get(GuiScenes.INSUFFICIENT_PLAYER_SCENE);
                controller.setCountDown(insufficientPlayer);
                return;
            }

            // Otherwise load the new page, store the root, store the
            // controller and init the page content
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
            }
            finally {
                this.currentScene = GuiScenes.INSUFFICIENT_PLAYER_SCENE;
            }
        });
    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {
        if (this.currentScene != null && this.currentScene.equals(GuiScenes.SHIP_CONSTRUCTION_SCENE)) {
            ShipConstructionController controller = (ShipConstructionController) this.controllers.get(GuiScenes.SHIP_CONSTRUCTION_SCENE);

            if (timerDTO.getHasEnded()) {
                controller.handleConfirmShip();
            }
            else {
                if (!timerDTO.getIsServerAction()) {
                    controller.startCountDownTimer();
                }
                else {
                    controller.resetTimer();
                }
            }
        }
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
        }
        else {
            // Terminal output
            System.err.println(error.getError());
        }

        GUIController controller = (GUIController) this.controllers.get(this.currentScene);

        if (controller == null) {
            System.err.println("No controller has been found for the current scene");
            return;
        }

        Platform.runLater(() -> controller.showToast(error.getError(), ToastType.ERROR));
    }

    @Override
    public boolean isCTXAvailable() {
        return (ctx != null);
    }

    @Override
    public void setVirtualClient(VirtualView virtualClient) {
        GUIHandler.virtualClient = virtualClient;
    }

    /**
     * Stores the given root and controller belonging to the given GUI scene
     * to be able to reuse them, thus avoiding reconstructing a specific scene.
     *
     * @param scene The scene type to which the given root and controller belong.
     * @param root The root of the scene to store.
     * @param controller The controller of the scene to store.
     */
    public void saveRootAndController(GuiScenes scene, Parent root, Object controller) {
        this.controllers.put(scene, (GUIController) controller);
        this.roots.put(scene, root);
    }

    public void switchScene(GuiScenes scene) {
        this.currentScene = scene;
    }
}
