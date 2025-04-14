package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {

    // Socket that waits for clients connections
    final ServerSocket listenSocket;

    // GameController used to interact with the GameModel
    final GameController gameController;

    // Client list, used to broadcast the communication between all clients
    final List<SocketClientHandler> clients;

    /**
     * Constructor used to create a new Socket Server
     * */
    public TCPServer(String ipAddress, int port, GameController gameController) throws IOException {
        this.listenSocket = new ServerSocket(port);

        this.gameController = gameController;
        this.clients = new ArrayList<>();

        System.out.println("Server socket listening on port: " + port);
        this.runServer();
    }

    /**
     * Method used to listen for new client connections
     * */
    private void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = listenSocket.accept()) != null) {
            System.out.println("New socket client connected");

            InputStreamReader socketInputReader = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketOutputWriter = new OutputStreamWriter(clientSocket.getOutputStream());

            SocketClientHandler clientHandler = new SocketClientHandler(
                    this.gameController,
                    this,
                    new BufferedReader(socketInputReader),
                    new PrintWriter(socketOutputWriter)
            );

            // Add the clients to the clientList
            synchronized (this.clients) {
                this.clients.add(clientHandler);
            }

            new Thread(() -> {
                try {
                    clientHandler.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    // TODO: Implements this utility method to update all the clients --> will be triggered in the specific thread of each user
    public void broadCastUpdateView(StateDTO state) throws JsonProcessingException {
        synchronized (this.clients) {
            for (SocketClientHandler clientHandler : this.clients) {
                clientHandler.updateView(state);
            }
        }
    }

    public void broadCastUpdateState(StateDTO state) throws JsonProcessingException {
        synchronized (this.clients) {
            for (SocketClientHandler clientHandler : this.clients) {
                clientHandler.updateState(state);
            }
        }
    }

    public void reportError(String details, StateDTO state) throws JsonProcessingException {
        synchronized (this.clients) {
            for (SocketClientHandler clientHandler : this.clients) {
                clientHandler.reportError(details, state);
            }
        }
    }
}
