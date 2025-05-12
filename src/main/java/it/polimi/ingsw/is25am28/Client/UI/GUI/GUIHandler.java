package it.polimi.ingsw.is25am28.Client.UI.GUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.Network.Socket.Client.TCPClient;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.UUID;

public class GUIHandler extends Application implements ClientUI {
    private static GUIHandler instance;
    private static int connectionType;
    private static ClientModel model;
    private VirtualView virtualClient;

    public static GUIHandler getInstance() {
        return instance;
    }

    public static void setConnectionType(int connectionType) {
        GUIHandler.connectionType = connectionType;
    }

    public static void setClientModel(ClientModel model) {
        GUIHandler.model = model;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;

        if (connectionType == 1) {
            this.virtualClient = new RMIClient("127.0.0.1", 7777, UUID.randomUUID(), this, model);
        } else {
            this.virtualClient = new TCPClient("127.0.0.1", 8888, this, model);
        }

        Label loadingLabel = new Label("Loading...");
        StackPane root = new StackPane(loadingLabel);
        Scene scene = new Scene(root, 720, 720);

        stage.setTitle("Galaxy Trucker");
        stage.setResizable(false);
        stage.setScene(scene);

        Platform.runLater(stage::show);
    }

    @Override
    public void setVirtualClient(VirtualView client) {

    }

    @Override
    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception {

    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {

    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {

    }

    @Override
    public void showShipFixing(FixShipDTO fixShip) throws Exception {

    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {

    }

    @Override
    public void showCardRound(CardRoundDTO cardRound) throws Exception {

    }

    @Override
    public void showEndGame(EndGameDTO endGame) {

    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {

    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {

    }

    @Override
    public void commitCommand(String playerNickname) {

    }

    @Override
    public void showError(ErrorAnswer error) {

    }

    @Override
    public boolean isCTXAvailable() {
        return false;
    }
}
