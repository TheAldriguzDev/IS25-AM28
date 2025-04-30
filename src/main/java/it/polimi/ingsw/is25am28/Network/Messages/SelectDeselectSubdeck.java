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

    @Override
    public boolean validate() {
        return this.playerNickname != null
                && !this.playerNickname.isEmpty()
                && this.subdeck != null
                && this.isSelectAction != null;
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

        if (this.isSelectAction == null) {
            errors.add("ERROR: The 'isSelectAction' field cannot be null");
        }

        return errors;
    }
}
