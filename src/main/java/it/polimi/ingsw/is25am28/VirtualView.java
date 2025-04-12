package it.polimi.ingsw.is25am28;

/**
 * Interface that defines the methods that the controller calls to update the content on the clients
 * */

public interface VirtualView {

    // TODO: make overload of showUpdate to match the different states of the model
    void updateView() throws Exception;

    // TODO: make overload of updateState to match the different states of the model
    void updateState() throws Exception;

    void reportError(String details) throws Exception;
}
