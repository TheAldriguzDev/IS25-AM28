package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Network.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.Network.Socket.Client.TCPClient;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import javafx.application.Application;

import java.util.Scanner;
import java.util.UUID;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;

public class Client {

    public static void main(String[] args) throws Exception {
        // Will store the specific network implementation (RMI / Socket)
        VirtualView client;

        Scanner scanner = new Scanner(System.in);
        int connectionType = -1;
        int uiType = -1;

        System.out.flush();

        do {
            System.out.println("Choose a connection type: ");
            System.out.println("1 --> RMI");
            System.out.println("2 --> Socket");
            connectionType = scanner.nextInt();

            if (connectionType == -1) {
                System.err.println("Invalid connection type, the value must be 1 or 2!");
            }
        } while (connectionType == -1);


        do {
            System.out.println("Choose an ui type: ");
            System.out.println("1 --> TUI");
            System.out.println("2 --> GUI (Coming soon)");
            uiType = scanner.nextInt();

            if (uiType == -1) {
                System.err.println("Invalid ui type, the value must be 1 or 2!");
            }
        } while (uiType == -1);

        clearTerminal();
        System.out.flush();

        // ================= CREATE THE CLIENT ================= //
        ClientUI clientUI;
        VirtualView virtualClient;

        ClientModel model = new ClientModel();

        if (uiType == 1) {
            clientUI = new TUIHandler(model);
        } else {
            // Application.Launch is a blocking task
            GUIHandler.setConnectionType(connectionType);
            GUIHandler.setClientModel(model);
            Application.launch(GUIHandler.class, args);
            return;
        }

        if (connectionType == 1) {
            virtualClient = new RMIClient(args[0], 7777, UUID.randomUUID(), clientUI, model);
        } else {
            virtualClient = new TCPClient("127.0.0.1", 8888, clientUI, model);
        }

        clientUI.setVirtualClient(virtualClient);
    }
}
