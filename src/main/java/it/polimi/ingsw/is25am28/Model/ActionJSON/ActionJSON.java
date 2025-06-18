package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;

import java.io.Serializable;

/**
 * Represents the base class for the players' actions for evey eventCard in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AbandonedShipJSON.class, name = "AbandonedShipJSON"),
        @JsonSubTypes.Type(value = AbandonedStationJSON.class, name = "AbandonedStationJSON"),
        @JsonSubTypes.Type(value = EpidemyJSON.class, name = "EpidemyJSON"),
        @JsonSubTypes.Type(value = MeteorShowerJSON.class, name = "MeteorShowerJSON"),
        @JsonSubTypes.Type(value = OpenSpaceJSON.class, name = "OpenSpaceJSON"),
        @JsonSubTypes.Type(value = PiratesJSON.class, name = "PiratesJSON"),
        @JsonSubTypes.Type(value = SlaversJSON.class, name = "SlaversJSON"),
        @JsonSubTypes.Type(value = SmugglersJSON.class, name = "SmugglersJSON"),
        @JsonSubTypes.Type(value = StardustJSON.class, name = "StardustJSON"),
        @JsonSubTypes.Type(value = VisitPlanetsJSON.class, name = "VisitPlanetsJSON"),
        @JsonSubTypes.Type(value = WarZoneJSON.class, name = "WarZoneJSON")
})

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionJSON implements Serializable {
    protected String playerNickname;

    /**
     * Default constructor used client side
     */
    @JsonCreator
    public ActionJSON() {}

    /**
     * Constructor that initialize the JSON with a nickname, used mainly serverside
     */
    @JsonCreator
    public ActionJSON(
            @JsonProperty("playerNickname") String playerNickname
    ) {
        this.playerNickname = playerNickname;
    }

    /**
     * Returns the player Nickname
     */
    @JsonGetter("playerNickname")
    public String getPlayerNickname() throws IllegalStateException {
        if (this.playerNickname == null || this.playerNickname.isEmpty()) {
            return null;
        }

        return this.playerNickname;
    }

    /**
     * Set the playerNickname to the given data
     */
    public void setPlayerNickname(String playerNickname) throws IllegalStateException {
        if (playerNickname == null || playerNickname.isEmpty()) {
            throw new IllegalStateException("playerNickname cannot be null or empty");
        }

        this.playerNickname = playerNickname;
    }
}