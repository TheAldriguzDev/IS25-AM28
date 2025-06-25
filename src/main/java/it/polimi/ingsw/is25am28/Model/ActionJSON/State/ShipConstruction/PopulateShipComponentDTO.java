package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

/**
 * Represents a data transfer data object that holds information about a cabin and the lifeForm it has been filled with
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
public final class PopulateShipComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private ComponentHelper<LifeformType> component;
    private boolean isShipPopulated;

    // Constructor #1
    public PopulateShipComponentDTO() {}

    // Constructor #2
    public PopulateShipComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("component") ComponentHelper<LifeformType> component,
            @JsonProperty("isShipPopulated") boolean isShipPopulated
    ) {
        this.playerNickname = playerNickname;
        this.component = component;
        this.isShipPopulated = isShipPopulated;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public PopulateShipComponentDTO setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("component")
    public ComponentHelper<LifeformType> getComponent() {
        return component;
    }

    @JsonSetter("component")
    public PopulateShipComponentDTO setComponent(ComponentHelper<LifeformType> component) {
        this.component = component;
        return this;
    }

    @JsonGetter("isShipPopulated")
    public boolean isShipPopulated() {
        return isShipPopulated;
    }

    @JsonSetter("isShipPopulated")
    public PopulateShipComponentDTO setIsShipPopulated(boolean shipPopulated) {
        isShipPopulated = shipPopulated;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
