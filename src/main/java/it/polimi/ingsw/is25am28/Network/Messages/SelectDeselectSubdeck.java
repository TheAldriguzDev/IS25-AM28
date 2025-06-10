package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

public final class SelectDeselectSubdeck implements Message {
    private String playerNickname;
    private Integer subdeck;
    private Boolean isSelectAction;

    @JsonCreator
    public SelectDeselectSubdeck(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("subdeck") Integer subdeck,
            @JsonProperty("isSelectAction") boolean isSelectAction
    ) {
        this.playerNickname = playerNickname;
        this.subdeck = subdeck;
        this.isSelectAction = isSelectAction;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    @JsonGetter("subdeck")
    public Integer getSubdeck() {
        return this.subdeck;
    }

    @JsonSetter("subdeck")
    public void setSubdeck(Integer subdeck) {
        this.subdeck = subdeck;
    }

    @JsonGetter("isSelectAction")
    public Boolean isSelectAction() {
        return this.isSelectAction;
    }

    @JsonSetter("isSelectAction")
    public void setSelectAction(Boolean isSelectAction) {
        this.isSelectAction = isSelectAction;
    }
}
