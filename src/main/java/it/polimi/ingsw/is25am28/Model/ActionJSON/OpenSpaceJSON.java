package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class OpenSpaceJSON extends ActionJSON {
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleEnginesToActivate;

    @JsonCreator
    public OpenSpaceJSON() {
        this.doubleEnginesToActivate = new ArrayList<>();
    }

    @JsonCreator
    public OpenSpaceJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("doubleEnginesToActivate") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleEnginesToActivate
    ) {
        super(playerNickname);
        this.doubleEnginesToActivate = doubleEnginesToActivate;
    }

    @JsonGetter("doubleEnginesToActivate")
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getDoubleEnginesToActivate() {
        return this.doubleEnginesToActivate;
    }

    @JsonSetter("doubleEnginesToActivate")
    public void setDoubleEnginesToActivate(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleEnginesToActivate) {
        this.doubleEnginesToActivate = doubleEnginesToActivate;
    }
}
