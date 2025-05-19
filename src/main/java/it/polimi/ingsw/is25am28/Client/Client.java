package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.ConnectionType;
import it.polimi.ingsw.is25am28.Client.UI.UIType;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.Network.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.Network.Socket.Client.TCPClient;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.is25am28.Client.UI.ConnectionType.*;
import static it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.Screen.DEFAULT_COMMAND_PREFIX;
import static it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.Screen.UNKNOWN_COMMAND_ERROR;
import static it.polimi.ingsw.is25am28.Client.UI.UIType.*;
import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;

public class Client {
    public static String IP_LOOPBACK_ADDRESS = "127.0.0.1";
    public static int TCP_SOCKET_PORT = 8888;
    public static int RMI_PORT = 7777;

    private static InputWidgetTUI connectionTypeSelectorWidget;
    private static InputWidgetTUI uiTypeSelectorWidget;
    private static InputThread inputThread;

    /**
     * Starts the input thread for the input widgets.
     * If the input thread is null, then it'll also instantiate it
     * right before starting it.
     */
    private static void startInputThread() {
        if (inputThread == null || !inputThread.isAlive()) {
            inputThread = new InputThread();
            inputThread.start();
        }
    }

    /**
     * Stops and dereferences the currently available input thread
     * (only if there is one instantiated and alive)
     */
    private static void stopInputThread() {
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interrupt();
            inputThread = null;
        }
    }

    /**
     * Generates the input widget containing all the available connection types
     * that each player can choose from.
     *
     * @param connectionType An atomic reference passed externally
     *                       to store the chosen connection type
     */
    private static void generateConnectionTypeSelectorWidget(AtomicReference<ConnectionType> connectionType) throws IllegalArgumentException {
        CommandWidgetTUI command;

        if (connectionType == null) {
            throw new IllegalArgumentException("[ERROR] connectionType atomic reference cannot be null (can't store the result).");
        }

        startInputThread();
        connectionTypeSelectorWidget = new InputWidgetTUI(inputThread);

        // (1) - RMI
        command = new CommandWidgetTUI(
                "" + RMI.ordinal(),
                () -> {
                    connectionType.set(RMI);
                }
        );
        command.appendString(RMI.name());
        connectionTypeSelectorWidget.addCommand(command);

        // (2) - TCP/Socket
        command = new CommandWidgetTUI(
                "" + TCP_SOCKET.ordinal(),
                () -> {
                    connectionType.set(TCP_SOCKET);
                }
        );
        command.appendString(TCP_SOCKET.name());
        connectionTypeSelectorWidget.addCommand(command);

        connectionTypeSelectorWidget.setColumnGroupingAmount(
                connectionTypeSelectorWidget.getCommandMap().size()
        );
    }

    /**
     * Generates the input widget containing all the available UI types
     * that each player can choose from
     *
     * @param uiType An atomic reference passed externally
     *               to store the chosen UI type
     */
    private static void generateUITypeSelectorWidget(AtomicReference<UIType> uiType) throws IllegalArgumentException {
        CommandWidgetTUI command;

        if (uiType == null) {
            throw new IllegalArgumentException("[ERROR] UIType atomic reference cannot be null (can't store the result).");
        }

        uiTypeSelectorWidget = new InputWidgetTUI(inputThread);

        // (1) - TUI
        command = new CommandWidgetTUI(
                "" + TUI.ordinal(),
                () -> {
                    uiType.set(TUI);
                }
        );
        command.appendString(TUI.name());
        uiTypeSelectorWidget.addCommand(command);

        // (2) - GUI
        command = new CommandWidgetTUI(
                "" + GUI.ordinal(),
                () -> {
                    uiType.set(GUI);
                }
        );
        command.appendString(GUI.name());
        uiTypeSelectorWidget.addCommand(command);

        uiTypeSelectorWidget.setColumnGroupingAmount(
                uiTypeSelectorWidget.getCommandMap().size()
        );
    }

    /**
     * Keeps asking the user for a valid connection type
     */
    private static void getConnectionTypeCommand() {
        boolean commandSelected;

        do {
            try {
                System.out.println();
                System.out.println("Choose a connection type:");
                commandSelected = connectionTypeSelectorWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (!commandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);
    }

    /**
     * Keeps asking the user for a valid UI type
     */
    private static void getUITypeCommand() {
        boolean commandSelected;

        do {
            try {
                System.out.println();
                System.out.println("Choose an UI type:");
                commandSelected = uiTypeSelectorWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (!commandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);
    }

//    // Main
//    public static void main(String[] args) throws Exception {
//        ClientUI clientUI;
//        VirtualView virtualClient;
//        ClientModel model;
//
//        // Initializations
//        AtomicReference<ConnectionType> connectionType = new AtomicReference<>(null);
//        AtomicReference<UIType> uiType = new AtomicReference<>(null);
//        model = new ClientModel();
//
//        startInputThread();
//        generateConnectionTypeSelectorWidget(connectionType);
//        generateUITypeSelectorWidget(uiType);
//
//        clearTerminal();
//        System.out.flush();
//
//        getConnectionTypeCommand();
//        getUITypeCommand();
//        stopInputThread();
//
//        // Instantiating the clientUI chosen by the player
//        switch (uiType.get()) {
//            case TUI -> {
//                clientUI = new TUIHandler(model);
//            }
//            case GUI -> {
//                clientUI = new GUIHandler(model);
//            }
//            case null, default -> {
//                System.err.println("[ERROR] UI type not selected");
//                return;
//            }
//        }
//
//        // Instantiating the virtualClient chosen by the player
//        switch (connectionType.get()) {
//            case RMI -> {
//                virtualClient = new RMIClient(args[0], RMI_PORT, UUID.randomUUID(), clientUI, model);
//            }
//            case TCP_SOCKET -> {
//                virtualClient = new TCPClient(args[0], TCP_SOCKET_PORT, clientUI, model);
//            }
//            case null, default -> {
//                System.err.println("[ERROR] Connection type not selected");
//                return;
//            }
//        }
//
//        clientUI.setVirtualClient(virtualClient);
//
//        if (uiType.get().equals(GUI)) {
//            Application app;
//
//            switch (clientUI) {
//                case Application application -> app = application;
//                case null, default -> {
//                    throw new IllegalArgumentException("[ERROR] clientUI is not an instance of Application");
//                }
//            }
//
//            Platform.startup(
//                () -> {
//                    try {
//                        app.start(new Stage());
//                    }
//                    catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            );
//        }
//    }


    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        int connectionType;
        int uiType;

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
            System.out.println("2 --> GUI");
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

        // Instantiating the clientUI chosen by the player
        switch (uiType) {
            case 1 -> {
                clientUI = new TUIHandler(model);
            }
            case 2 -> {
                clientUI = new GUIHandler(model);
            }
            default -> {
                System.err.println("[ERROR] UI type not selected");
                return;
            }
        }

        // Instantiating the virtualClient chosen by the player
        switch (connectionType) {
            case 1 -> {
                virtualClient = new RMIClient(args[0], RMI_PORT, UUID.randomUUID(), clientUI, model);
            }
            case 2 -> {
                virtualClient = new TCPClient(args[0], TCP_SOCKET_PORT, clientUI, model);
            }
            default -> {
                System.err.println("[ERROR] Connection type not selected");
                return;
            }
        }

        clientUI.setVirtualClient(virtualClient);

        if (uiType == GUI.ordinal()) {
            Application app = (Application) clientUI;

            Platform.startup(
                    () -> {
                        try {
                            app.start(new Stage());
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    }
}
