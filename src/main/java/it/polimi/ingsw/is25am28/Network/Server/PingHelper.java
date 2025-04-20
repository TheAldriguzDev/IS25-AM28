package it.polimi.ingsw.is25am28.Network.Server;

/**
 * Ping helper is a utility class that will be used to detect possible client disconnections.
 * */

public class PingHelper {
    private String nickname;
    private int failedPings;
    private boolean isConnected;

    public PingHelper(String nickname) {
        this.nickname = nickname;
        this.failedPings = 0;
        this.isConnected = true;
    }

    /**
     * @return the playerNickname
     * */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the value of failedPings
     * */
    public int getFailedPings() {
        return failedPings;
    }

    /**
     * @return the client connection status
     * */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Add a failed ping to the client
     * */
    public void incrementPing() {
        this.failedPings++;
    }

    /**
     * Reset the failed pings to the client and set the connection to true
     * */
    public void resetPings() {
        this.failedPings = 0;
        this.isConnected = true;
    }

    /**
     * Set the client connection to the given value
     * */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

}
