package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

public final class PlaceTile implements Message {
    private String nickname;
    private Integer componentID;
    private Integer i;
    private Integer j;
    private Integer rotation;

    @JsonCreator
    public PlaceTile(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("componentID") Integer componentID,
            @JsonProperty("i") Integer i,
            @JsonProperty("j") Integer j,
            @JsonProperty("rotation") Integer rotation ) {
        this.nickname = nickname;
        this.componentID = componentID;
        this.i = i;
        this.j = j;
        this.rotation = rotation;
    }

    @JsonGetter("nickname")
    public String getNickname() {
        return nickname;
    }

    @JsonSetter("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @JsonGetter("componentID")
    public Integer getComponentID() {
        return componentID;
    }

    @JsonSetter("componentID")
    public void setComponentID(Integer componentID) {
        this.componentID = componentID;
    }

    @JsonGetter("i")
    public Integer getI() {
        return i;
    }

    @JsonSetter("i")
    public void setI(Integer i) {
        this.i = i;
    }

    @JsonGetter("j")
    public Integer getJ() {
        return j;
    }

    @JsonSetter("j")
    public void setJ(Integer j) {
        this.j = j;
    }

    @JsonGetter("rotation")
    public Integer getRotation() {
        return rotation;
    }

    @JsonSetter("rotation")
    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    @Override
    public boolean validate() {
        return nickname != null &&
                componentID != null &&
                i != null &&
                j != null &&
                rotation != null &&
                rotation >= 0 &&
                rotation <= 3;
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();
        if (nickname == null) {
            errors.add("The 'nickname' cannot be null.");
        }

        if (componentID == null) {
            errors.add("The 'componentID' cannot be null.");
        }

        if (i == null) {
            errors.add("The 'i' cannot be null.");
        }

        if (j == null) {
            errors.add("The 'j' cannot be null.");
        }

        if (rotation == null) {
            errors.add("The 'rotation' cannot be null.");
        } else {
            if (rotation < 0 || rotation > 3) {
                errors.add("The 'rotation' must be between 0 and 3.");
            }
        }
        return errors;
    }
}
