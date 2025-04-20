package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Network.Server.Server;
import it.polimi.ingsw.is25am28.Network.Server.ServerLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {

    // Socket that waits for clients connections
    final ServerSocket listenSocket;

    // GameController used to interact with the GameModel
    final Server gameController;

    // Client list, used to broadcast the communication between all clients
    final List<SocketClientHandler> clients;

    /**
     * Constructor used to create a new Socket Server
     * */
    public TCPServer(String ipAddress, int port, Server controller) throws IOException {
        this.listenSocket = new ServerSocket(port);

        this.gameController = controller;
        this.clients = new ArrayList<>();

        ServerLogger.info("SERVER SOCKET", "Server socket listening on port: " + port);
        //System.out.println("Server socket listening on port: " + port);

        new Thread(() -> {
            try {
                this.runServer();
            } catch (IOException e) {
                throw new RuntimeException("TCP server failed", e);
            }
        }).start();
    }

    /**
     * Method used to listen for new client connections
     * */
    private void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = listenSocket.accept()) != null) {
            ServerLogger.info("SERVER SOCKET", "New client connected");

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
}