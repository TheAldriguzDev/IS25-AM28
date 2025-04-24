package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import java.io.Serializable;

/**
 * This state is only used to notify the clients that they need to wait for the game configuration by the leader
 * */

public final class WaitingForGameConfigurationDTO extends StateDTO implements Serializable {
    public WaitingForGameConfigurationDTO() {}
}
