package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the players' actions in the {@code meteorShower} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class MeteorShowerJSON extends ActionJSON {
    private List<Pair<CoordinatePair, CoordinatePair>> shieldsCoordinates;
    private List<Pair<CoordinatePair, CoordinatePair>> cannonsCoordinates;

    @JsonCreator
    public MeteorShowerJSON() {
        this.shieldsCoordinates = new ArrayList<>();
        this.cannonsCoordinates = new ArrayList<>();
    }

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("shieldsCoordinates") List<Pair<CoordinatePair, CoordinatePair>> shieldsCoordinates,
            @JsonProperty("cannonsCoordinates") List<Pair<CoordinatePair, CoordinatePair>> cannonsCoordinates
    ) {
        super(playerNickname);
        this.shieldsCoordinates = shieldsCoordinates;
        this.cannonsCoordinates = cannonsCoordinates;
    }

    @JsonGetter("shieldsCoordinates")
    public List<Pair<CoordinatePair, CoordinatePair>> getShieldsCoordinates() {
        return this.shieldsCoordinates;
    }

    @JsonSetter("shieldsCoordinates")
    public void setShieldsCoordinates(List<Pair<CoordinatePair, CoordinatePair>> shieldsCoordinates) {
        this.shieldsCoordinates = shieldsCoordinates;
    }

    @JsonGetter("cannonsCoordinates")
    public List<Pair<CoordinatePair, CoordinatePair>> getCannonsCoordinates() {
        return this.cannonsCoordinates;
    }

    @JsonSetter("cannonsCoordinates")
    public void setCannonsCoordinates(List<Pair<CoordinatePair, CoordinatePair>> cannonsCoordinates) {
        this.cannonsCoordinates = cannonsCoordinates;
    }
}
