package it.polimi.ingsw.is25am28.Network.Socket.Client;

import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.VirtualServer;

/**
 * This interface specializes the VirtualView interface for the Socket technology
 */
public interface VirtualServerSocket extends VirtualServer {
    /**
     * Sends a message to the server socket. The messages are specialized instances
     * of the {@link Message} interface, each representing specific types of information
     * to be communicated to the server.
     *
     * @param message the {@link Message} object to be sent to the server. It encapsulates
     *                the data required for client-to-server communication.
     * @throws Exception if any error occurs during the communication process.
     */
    void sendMessage(Message message) throws Exception;
}
