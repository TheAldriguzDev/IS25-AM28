package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

public final class PopulateShip implements Message {
    private String playerNickname;
    private ComponentHelper<LifeformType> lifeformToAdd;

    @JsonCreator
    public PopulateShip(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("lifeformToAdd") ComponentHelper<LifeformType> lifeformToAdd
    ) {
        this.playerNickname = playerNickname;
        this.lifeformToAdd = lifeformToAdd;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public PopulateShip setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("lifeformToAdd")
    public ComponentHelper<LifeformType> getLifeformToAdd() {
        return this.lifeformToAdd;
    }

    @JsonSetter("lifeformToAdd")
    public PopulateShip setLifeformToAdd(ComponentHelper<LifeformType> lifeformToAdd) {
        this.lifeformToAdd = lifeformToAdd;
        return this;
    }
}
