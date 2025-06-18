package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the players' actions in the {@code meteorShower} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class OpenSpaceJSON extends ActionJSON {
    private List<Pair<CoordinatePair, CoordinatePair>> doubleEnginesToActivate;

    @JsonCreator
    public OpenSpaceJSON() {
        this.doubleEnginesToActivate = new ArrayList<>();
    }

    @JsonCreator
    public OpenSpaceJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("doubleEnginesToActivate") List<Pair<CoordinatePair, CoordinatePair>> doubleEnginesToActivate
    ) {
        super(playerNickname);
        this.doubleEnginesToActivate = doubleEnginesToActivate;
    }

    @JsonGetter("doubleEnginesToActivate")
    public List<Pair<CoordinatePair, CoordinatePair>> getDoubleEnginesToActivate() {
        return this.doubleEnginesToActivate;
    }

    @JsonSetter("doubleEnginesToActivate")
    public void setDoubleEnginesToActivate(List<Pair<CoordinatePair, CoordinatePair>> doubleEnginesToActivate) {
        this.doubleEnginesToActivate = doubleEnginesToActivate;
    }
}
