package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.ConnectionType;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.UIType;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.Network.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.Network.Socket.Client.TCPClient;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.Utils.ValidateIP;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
     * @return The player's selected IPv4 server address.
     */
    private static String getIpAddress() {
        String ipAddress;
        boolean validIPAddress;

        new WidgetTUI()
                .appendString("[CONNECT TO SERVER]")
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder()
                .printWidget();

        do {
            System.out.println();
            System.out.print("Insert server's IPv4 address to connect to: ");

            try {
                ipAddress = inputThread.waitForInput();

                if (ipAddress == null) {
                    // A forced interrupt arrived
                    return null;
                }

                validIPAddress = ValidateIP.validateIPAddress(ipAddress);

                if (!validIPAddress) {
                    System.out.println(
                            PrintUtils.addColor(
                                    "[ERROR] Given string does not represent an IPv4 address.",
                                    ANSIColors.RED
                            )
                    );

                    System.out.println("\t(IPv4 format is: x.y.z.w -> [0-255].[0.255].[0-255].[0-255])");
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return null;
            }
        }
        while (!validIPAddress);

        return ipAddress;
    }

    /**
     * Starts the input thread for the input widgets.
     * If the input thread is null, then it'll also instantiate it
     * right before starting it.
     */
    private static void startInputThread() {
        if (inputThread == null || !inputThread.isAlive()) {
            inputThread = new InputThread();
            inputThread.setDaemon(true);
            inputThread.start();
        }
    }

    /**
     * Stops and dereferences the currently available input thread
     * (only if there is one instantiated and alive)
     */
    private static void stopInputThread() {
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interruptInputReader();
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

        for (ConnectionType connection : ConnectionType.values()) {
            command = new CommandWidgetTUI(
                    "" + connection.ordinal(),
                    () -> {
                        connectionType.set(connection);
                    }
            );
            command.appendString(connection.name());
            connectionTypeSelectorWidget.addCommand(command);
        }

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

        for (UIType ui : UIType.values()) {
            command = new CommandWidgetTUI(
                    "" + ui.ordinal(),
                    () -> {
                        uiType.set(ui);
                    }
            );
            command.appendString(ui.name());
            uiTypeSelectorWidget.addCommand(command);
        }

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

    // Main
    public static void main(String[] args) throws Exception {
        ClientUI clientUI;
        VirtualView virtualClient;
        ClientModel model;
        String ipAddress;

        // Initializations
        AtomicReference<ConnectionType> connectionType = new AtomicReference<>(null);
        AtomicReference<UIType> uiType = new AtomicReference<>(null);
        model = new ClientModel();

        startInputThread();

        generateConnectionTypeSelectorWidget(connectionType);
        generateUITypeSelectorWidget(uiType);

        clearTerminal();
        System.out.flush();

        ipAddress = getIpAddress();
        getConnectionTypeCommand();
        getUITypeCommand();

        // Instantiating the clientUI chosen by the player
        switch (uiType.get()) {
            case TUI -> {
                clientUI = new TUIHandler(model, inputThread);
            }
            case GUI -> {
                clientUI = new GUIHandler(model);
            }
            case null, default -> {
                System.err.println("[ERROR] UI type not selected");
                return;
            }
        }

        // Instantiating the virtualClient chosen by the player
        switch (connectionType.get()) {
            case RMI -> {
                virtualClient = new RMIClient(ipAddress, RMI_PORT, UUID.randomUUID(), clientUI, model);
            }
            case TCP_SOCKET -> {
                virtualClient = new TCPClient(ipAddress, TCP_SOCKET_PORT, clientUI, model);
            }
            case null, default -> {
                System.err.println("[ERROR] Connection type not selected");
                return;
            }
        }

        clientUI.setVirtualClient(virtualClient);

        if (uiType.get().equals(GUI)) {
            Application app;

            // Stop and dereference the input thread since the GUI doesn't use it
            stopInputThread();

            switch (clientUI) {
                case Application application -> app = application;
                case null, default -> {
                    throw new IllegalArgumentException("[ERROR] clientUI is not an instance of Application");
                }
            }

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
