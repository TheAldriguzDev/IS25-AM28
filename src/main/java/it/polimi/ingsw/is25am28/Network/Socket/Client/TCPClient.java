package it.polimi.ingsw.is25am28.Network.Socket.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Socket.Server.VirtualViewSocket;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient implements VirtualViewSocket {
    // Input channel to read from the socket
    private final BufferedReader input;

    // Output channel will be used to communicate with the server
    private final SocketServerHandler output;

    // Used to respond to msg from the Server
    private final ViewUpdater viewUpdater;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ExecutorService updateThread;

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
        this.updateThread = Executors.newSingleThreadExecutor();

        // Run the client TCPClient
        this.run();
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
     * This method will listen for Serialized Messages from the server
     *
     * We need to implement the deserialization protocol
     * */
    private void runVirtualServer() throws Exception {
        String line; // This will contain a JSON serialized object

        while ((line = this.input.readLine()) != null) {

            Answer state = mapper.readValue(line, Answer.class);

            if (Objects.requireNonNull(state) instanceof ErrorAnswer error) {
                this.reportError(error);
            } else {
                this.updateState(state);
            }
        }
    }

    // This will be the method used in the communication
    @Override
    public void sendMessage(Message message) throws Exception {
        this.output.sendMessage(message); // This will invoke the SocketServerHandler
    }



    // TODO: This methods are always the same --> Maybe it could be useful to also have a middleware that handles all
    //  this identical methods --> we avoid duplicated code
    @Override
    public void updateView(StateDTO state) throws JsonProcessingException {
        System.out.println("Update view client called");
    }

    @Override
    public void updateState(Answer answer) throws JsonProcessingException {
        // Try to commit the command execution to the client
        if (answer.getPlayerNickname() != null) {
            viewUpdater.commitCommand(answer.getPlayerNickname());
        }

        // Update all the clients with the first state (response to the executed command)
        if (answer.getState() != null) {
            this.updateThread.submit(() -> {
                try {
                    answer.getState().accept(viewUpdater);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Update all the clients with the secondo state (new game state)
        if (answer.getNextState() != null) {
            this.updateThread.submit(() -> {
                try {
                    answer.getNextState().accept(viewUpdater);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public void reportError(ErrorAnswer errorDTO) throws JsonProcessingException {
        viewUpdater.reportError(errorDTO.getError());
    }
}
