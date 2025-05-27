package it.polimi.ingsw.is25am28.Network.Messages;

import java.util.List;

public final class RefreshGames implements Message {
    public RefreshGames() {}

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public List<String> getErrors() {
        return List.of();
    }
}
