package it.polimi.ingsw.is25am28.RMI.Client;

import it.polimi.ingsw.is25am28.RMI.Server.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI {
    final VirtualServerRMI server;

    public RMIClient(VirtualServerRMI server) throws RemoteException {
        super();
        this.server = server;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameRMIServer";

        Registry registry = LocateRegistry.getRegistry(args[0], 7777); // args[0] is the IP address that identify the RMI registry

        VirtualServerRMI server = (VirtualServerRMI) registry.lookup(serverName);

        new RMIClient(server).run();
    }

    private void run() throws RemoteException {
        this.server.connectClient(this);

        // this.runCli();
    }

    @Override
    public void updateView() throws Exception {

    }

    @Override
    public void updateState() throws Exception {

    }

    @Override
    public void reportError(String details) throws Exception {

    }
}
