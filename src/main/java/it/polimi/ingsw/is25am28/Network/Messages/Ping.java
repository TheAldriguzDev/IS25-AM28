package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class Ping implements Message {
    @JsonCreator
    public Ping() {}
}
