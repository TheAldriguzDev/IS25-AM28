package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public final class SelectSubdeck implements Message {
    private String playerNickname;
    private Integer subdeck;

    @JsonCreator
    public SelectSubdeck(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("subdeck") Integer subdeck
    ) {
        this.playerNickname = playerNickname;
        this.subdeck = subdeck;
    }

    @Override
    public boolean validate() {
        return this.playerNickname != null
                && !this.playerNickname.isEmpty()
                && this.subdeck != null;
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (this.playerNickname == null || this.playerNickname.isEmpty()) {
            errors.add("ERROR: The 'playerNickname' field cannot be null or empty.");
        }

        if (this.subdeck == null) {
            errors.add("ERROR: The 'subdeck' field cannot be null.");
        }

        return errors;
    }
}
