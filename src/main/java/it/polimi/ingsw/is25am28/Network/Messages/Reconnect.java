package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public final class Reconnect implements Message {
    private String nickname;

    @JsonCreator
    public Reconnect(@JsonProperty("nickname") String nickname) {
        this.nickname = nickname;
    }

    @JsonGetter("nickname")
    public String getNickname() {
        return nickname;
    }

    @JsonSetter("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
