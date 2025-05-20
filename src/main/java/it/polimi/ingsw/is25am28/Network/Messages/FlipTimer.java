package it.polimi.ingsw.is25am28.Network.Messages;

import java.util.ArrayList;
import java.util.List;

public final class FlipTimer implements Message {
    private String playerNickname;

    public FlipTimer(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    @Override
    public boolean validate() {
        return playerNickname != null && !playerNickname.isEmpty();
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();
        if (playerNickname == null || playerNickname.isEmpty()) {
            errors.add("playerNickname cannot be null or empty");
        }

        return errors;
    }
}
