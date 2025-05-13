package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * This class will be used to control the LobbyScene that is defined in the FXML folder. This is needed to handle events
 * in the SceneBuilder. For example, is possible to listen and react to events like button clicks, and so on...
 * */
public class LobbyController {

    @FXML
    private Button reconnectButton;

    @FXML
    private Button createButton;

    @FXML
    private Button refreshButton;

    public void handleReconnect(ActionEvent e) {
        System.out.println("Reconnect player");
    }

    public void handleCreate(ActionEvent e) {
        System.out.println("Creating game");
    }

    public void handleRefresh(ActionEvent e) {
        System.out.println("Refreshing games");
    }
}
