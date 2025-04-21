package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public final class Reconnect implements Message {
    private String nickname;

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

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public List<String> getErrors() {
        return List.of();
    }
}
