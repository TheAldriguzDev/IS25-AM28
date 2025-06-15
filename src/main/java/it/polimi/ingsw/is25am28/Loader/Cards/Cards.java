package it.polimi.ingsw.is25am28.Loader.Cards;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

/**
 * Defines the JSON structure used to parse and load saved cards into the game.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "abandonedShip",
        "abandonedStation",
        "meteors",
        "pirates",
        "planets",
        "space",
        "epidemic",
        "smugglers",
        "slavers",
        "stardust",
        "warzone"
})
public class Cards {
    @JsonProperty("abandonedShip")
    private List<AbandonedShip> abandonedShip;
    @JsonProperty("abandonedStation")
    private List<AbandonedStation> abandonedStation;
    @JsonProperty("meteors")
    private List<Meteor> meteors;
    @JsonProperty("pirates")
    private List<Pirate> pirates;
    @JsonProperty("planets")
    private List<Planet> planets;
    @JsonProperty("space")
    private List<OpenSpace> openSpace;
    @JsonProperty("epidemic")
    private List<Epidemic> epidemic;
    @JsonProperty("smugglers")
    private List<Smuggler> smugglers;
    @JsonProperty("slavers")
    private List<Slaver> slavers;
    @JsonProperty("stardust")
    private List<Stardust> stardust;
    @JsonProperty("warzone")
    private List<Warzone> warzone;

    @JsonGetter("abandonedShip")
    public List<AbandonedShip> getAbandonedShip() {
        return abandonedShip;
    }

    @JsonSetter("abandonedShip")
    public void setAbandonedShip(List<AbandonedShip> abandonedShip) {
        this.abandonedShip = abandonedShip;
    }

    @JsonGetter("abandonedStation")
    public List<AbandonedStation> getAbandonedStation() {
        return abandonedStation;
    }

    @JsonSetter("abandonedStation")
    public void setAbandonedStation(List<AbandonedStation> abandonedStation) {
        this.abandonedStation = abandonedStation;
    }

    @JsonGetter("meteors")
    public List<Meteor> getMeteors() {
        return meteors;
    }

    @JsonSetter("meteors")
    public void setMeteors(List<Meteor> meteors) {
        this.meteors = meteors;
    }

    @JsonGetter("pirates")
    public List<Pirate> getPirates() {
        return pirates;
    }

    @JsonSetter("pirates")
    public void setPirates(List<Pirate> pirates) {
        this.pirates = pirates;
    }

    @JsonGetter("planets")
    public List<Planet> getPlanets() {
        return planets;
    }

    @JsonSetter("planets")
    public void setPlanets(List<Planet> planets) {
        this.planets = planets;
    }

    @JsonGetter("space")
    public List<OpenSpace> getOpenSpace() {
        return openSpace;
    }

    @JsonSetter("space")
    public void setSpace(List<OpenSpace> space) {
        this.openSpace = space;
    }

    @JsonGetter("epidemic")
    public List<Epidemic> getEpidemic() {
        return epidemic;
    }

    @JsonSetter("epidemic")
    public void setEpidemic(List<Epidemic> epidemic) {
        this.epidemic = epidemic;
    }

    @JsonGetter("smugglers")
    public List<Smuggler> getSmugglers() {
        return smugglers;
    }

    @JsonSetter("smugglers")
    public void setSmugglers(List<Smuggler> smugglers) {
        this.smugglers = smugglers;
    }

    @JsonGetter("slavers")
    public List<Slaver> getSlavers() {
        return slavers;
    }

    @JsonSetter("slavers")
    public void setSlavers(List<Slaver> slavers) {
        this.slavers = slavers;
    }

    @JsonGetter("stardust")
    public List<Stardust> getStardust() {
        return stardust;
    }

    @JsonSetter("stardust")
    public void setStardust(List<Stardust> stardust) {
        this.stardust = stardust;
    }

    @JsonGetter("warzone")
    public List<Warzone> getWarzone() {
        return warzone;
    }

    @JsonSetter("warzone")
    public void setWarzone(List<Warzone> warzone) {
        this.warzone = warzone;
    }
}
