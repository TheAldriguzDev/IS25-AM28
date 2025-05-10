package it.polimi.ingsw.is25am28.Network.Socket.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Messages.Ping;
import it.polimi.ingsw.is25am28.Network.Socket.Server.VirtualViewSocket;
import it.polimi.ingsw.is25am28.Network.UpdateHandler.UpdateHandler;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class TCPClient implements VirtualViewSocket {
    // Input channel to read from the socket
    private final BufferedReader input;

    // Output channel will be used to communicate with the server
    private final SocketServerHandler output;

    // Used to respond to msg from the Server
    private final ViewUpdater viewUpdater;

    private final ObjectMapper mapper = new ObjectMapper();

    private final UpdateHandler updateHandler;

    private final ScheduledExecutorService pingScheduler;

    /**
     * Constructor used to create the TCPClient and starts it
     * */
    public TCPClient(String ipAddress, int port, ClientUI ui, ClientModel model) throws IOException {
        // Args validation
        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port OutOfBound [0, 65535]: " + port);
        }

        Socket serverSocket = new Socket(ipAddress, port);

        InputStreamReader socketInputReader = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketOutputWriter = new OutputStreamWriter(serverSocket.getOutputStream());

        this.input = new BufferedReader(socketInputReader);

        // This will be a thread that will listen to the messages coming from the server
        this.output = new SocketServerHandler(new BufferedWriter(socketOutputWriter));

        this.viewUpdater = new ViewUpdater(ui, model);

        this.updateHandler = new UpdateHandler(viewUpdater);

        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();

        // Run the client TCPClient
        this.run();
        this.pingServer();
    }

    /**
     * Run the TCPClient in a new Thread. In this new thread we will listen the server messages
     * */
    private void run() {
        new Thread(() -> {
            try {
                this.runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * This method will listen for server messages to the client
     * */
    private void runVirtualServer() throws IOException {
        String line;

        while ((line = this.input.readLine()) != null) {
            System.out.println(line);
            Answer state = mapper.readValue(line, Answer.class);

            if (Objects.requireNonNull(state) instanceof ErrorAnswer error) {
                this.reportError(error);
            } else {
                this.updateState(state);
            }
        }

        // Once the loop has been exited means that the server is offline
        System.out.println("\n[Server offline] The connection with the server has been lost");
        System.exit(1);
    }

    // This will be the method used in the communication
    @Override
    public void sendMessage(Message message) throws Exception {
        this.output.sendMessage(message); // This will invoke the SocketServerHandler
    }

    /**
     * This method will ping every 5 seconds the server
     * */
    private void pingServer() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            try {
                this.sendMessage(new Ping());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    // TODO: This methods are always the same --> Maybe it could be useful to also have a middleware that handles all
    //  this identical methods --> we avoid duplicated code
    @Override
    public void updateView(StateDTO state) throws JsonProcessingException {
        System.out.println("Update view client called");
    }

    @Override
    public void updateState(Answer answer) {
        this.updateHandler.processUpdate(answer);
    }

    @Override
    public void reportError(ErrorAnswer error) {
        this.updateHandler.reportErrorUpdate(error);
    }
}
