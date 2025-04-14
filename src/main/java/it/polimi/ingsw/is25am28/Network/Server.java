package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Network.RMI.Server.RMIServer;
import it.polimi.ingsw.is25am28.Network.Socket.Server.TCPServer;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Class used to spawn both the RMI and Socket server
 * */

public class Server {
    public static final String RMIServerName = "GameRMIServer";
    public static final int RMIServerPort = 7777;
    public static final String TCPAddress = "127.0.0.1";
    public static final int TCPServerPort = 8888;

    private final GameController controller;
    private RMIServer rmiServer;
    private TCPServer tcpServer;

    public Server() {
        controller = new GameController();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();

        // Create the RMIServer
        server.rmiServer = new RMIServer(Server.RMIServerName, Server.RMIServerPort, server.controller);
        server.tcpServer = new TCPServer(Server.TCPAddress, Server.TCPServerPort, server.controller);
    }
}
