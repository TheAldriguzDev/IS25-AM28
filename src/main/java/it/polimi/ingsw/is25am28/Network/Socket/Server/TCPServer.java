package it.polimi.ingsw.is25am28.Network.Socket.Server;

import it.polimi.ingsw.is25am28.Network.Server.Server;
import it.polimi.ingsw.is25am28.Network.Server.ServerLogger;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * The TCPServer class creates a TCP socket server to manage client connections and communications.
 * It listens for incoming client connections, handles them via individual threads, and allows communication
 * between clients by maintaining a list of connected clients.
 */
public class TCPServer {
    // Socket that waits for clients connections
    final ServerSocket listenSocket;

    // GameController used to interact with the GameModel
    final Server gameController;

    // Client list, used to broadcast the communication between all clients
    final List<SocketClientHandler> clients;

    /**
     * Constructs a TCPServer instance that listens for client connections and manages their communication.
     * This class initializes a server socket, starts a thread to handle client connections,
     * and allows interaction with the provided controller.
     *
     * @param ipAddress The IP address to bind to.
     * @param port The port number on which the server will accept client connections.
     * @param controller The instance of Server that handles interactions with the game model and logic.
     * @throws IOException If an I/O error occurs when opening the server socket.
     */
    public TCPServer(String ipAddress, int port, Server controller) throws IOException {
        this.listenSocket = new ServerSocket(port, 100, InetAddress.getByName(ipAddress));

        this.gameController = controller;
        this.clients = new ArrayList<>();

        ServerLogger.info("SERVER SOCKET", "Server socket listening on port: " + port);

        new Thread(() -> {
            try {
                this.runServer();
            }
            catch (IOException e) {
                throw new RuntimeException("TCP server failed", e);
            }
        }).start();
    }

    /**
     * The runServer method starts the server's main loop to listen for incoming client connections
     * and manages those connections by creating and running client handler threads. Each accepted
     * client socket is processed in a separate thread to enable concurrent handling of multiple clients.
     *
     * @throws IOException if an I/O error occurs when waiting for or handling client connections
     */
    private void runServer() throws IOException {
        Socket clientSocket;

        while ((clientSocket = listenSocket.accept()) != null) {
            ServerLogger.info("SERVER SOCKET", "New client connected");

            InputStreamReader socketInputReader = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketOutputWriter = new OutputStreamWriter(clientSocket.getOutputStream());

            SocketClientHandler clientHandler = new SocketClientHandler(
                    this.gameController,
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
                } catch (SocketException e) {
                    System.out.println("Connection closed by the client: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO error with the client: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
