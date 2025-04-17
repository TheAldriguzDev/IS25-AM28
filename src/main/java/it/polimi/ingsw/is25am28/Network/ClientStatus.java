package it.polimi.ingsw.is25am28.Network;

import java.util.UUID;

/**
 * ClientStatus is a utility class that will be used to store the actual connectionStatus of each client.
 * This will help us to manage the player disconnection and re-connection.
 * */

public class ClientStatus {
    public String nickName;
    public int failedPings = 0;
    public boolean isConnected;

    public ClientStatus(String nickName) {
        this.nickName = "";
        this.isConnected = true;
    }

    public void resetPings() {
        this.failedPings = 0;
        this.isConnected = true;
    }
}
