package it.polimi.ingsw.is25am28.Network.Server;

/**
 * Utility class that manages a player's connection state and failed ping attempts.
 * It is used to monitor the reliability of a client's connection through ping attempts
 * and determine whether the client is still connected.
 */


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
     * Retrieves the nickname of the player associated with this instance.
     *
     * @return the nickname of the player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Retrieves the number of failed ping attempts for the client.
     *
     * @return the total count of failed pings
     */
    public int getFailedPings() {
        return failedPings;
    }

    /**
     * Checks if the client associated with this instance is currently connected.
     *
     * @return true if the client is connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Increments the count of failed ping attempts for the client associated with this instance.
     */
    public void incrementPing() {
        this.failedPings++;
    }

    /**
     * Resets the ping state for a client by setting the number of failed ping attempts to zero
     * and marking the client as connected.
     *
     * This method is used to restore the connection state of a client to its initial values,
     * typically after a successful ping or reconnection. It ensures that the client is no
     * longer flagged as disconnected and begins with a fresh ping count.
     */
    public void resetPings() {
        this.failedPings = 0;
        this.isConnected = true;
    }

    /**
     * Updates the connection status of the client associated with this instance.
     *
     * @param connected the new connection status to set; true indicates that the client
     *                  is connected, while false indicates the client is disconnected
     */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }
}
