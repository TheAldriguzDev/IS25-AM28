package it.polimi.ingsw.is25am28.Network.Messages;

import java.util.List;
import java.util.UUID;

public final class Ping implements Message {
    public Ping() {}

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public List<String> getErrors() {
        return List.of();
    }
}
