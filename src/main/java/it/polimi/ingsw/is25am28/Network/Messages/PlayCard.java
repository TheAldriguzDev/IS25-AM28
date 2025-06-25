package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;

public final class PlayCard implements Message {
    private String playerNickname;
    private ActionJSON actionJSON;

    @JsonCreator
    public PlayCard(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("actionJSON") ActionJSON actionJSON
    ) {
        this.playerNickname = playerNickname;
        this.actionJSON = actionJSON;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    @JsonGetter("actionJSON")
    public ActionJSON getActionJSON() {
        return this.actionJSON;
    }

    @JsonSetter("actionJSON")
    public void setActionJSON(ActionJSON actionJSON) {
        this.actionJSON = actionJSON;
    }
}
