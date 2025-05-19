package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class WarZoneJSON extends ActionJSON {
    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<Pair<CoordinatePair, CoordinatePair>> shieldList;
    private List<Pair<CoordinatePair, CoordinatePair>> cannonList;
    private List<Pair<CoordinatePair, CoordinatePair>> engineList;
    private List<CoordinatePair> batteriesToBeStolen;

    /**
     * Default constructor
     * */
    public WarZoneJSON() {
        this.lifeformsToBeRemoved = new ArrayList<>();
        this.itemsToBeRemoved = new ArrayList<>();
        this.shieldList = new ArrayList<>();
        this.cannonList = new ArrayList<>();
        this.engineList = new ArrayList<>();
    }

    public WarZoneJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("lifeformsToBeRemoved") List<ComponentHelper<LifeformType>> lifeformsToBeRemoved,
            @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
            @JsonProperty("shieldList") List<Pair<CoordinatePair, CoordinatePair>> shieldList,
            @JsonProperty("cannonList") List<Pair<CoordinatePair, CoordinatePair>> cannonList,
            @JsonProperty("cannonList") List<Pair<CoordinatePair, CoordinatePair>> engineList,
            @JsonProperty("batteriesToBeStolen") List<CoordinatePair> batteriesToBeStolen
    ) {
        this.playerNickname = playerNickname;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.shieldList = shieldList;
        this.cannonList = cannonList;
        this.engineList = engineList;
        this.batteriesToBeStolen = batteriesToBeStolen;
    }

    @JsonGetter("lifeformsToBeRemoved")
    public List<ComponentHelper<LifeformType>> getLifeformsToBeRemoved() {
        return this.lifeformsToBeRemoved;
    }

    @JsonSetter("lifeformsToBeRemoved")
    public void setLifeformsToBeRemoved(List<ComponentHelper<LifeformType>> lifeformsToBeRemoved) {
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
    }

    public void addLifeformsToBeRemoved(ComponentHelper<LifeformType> lifeformsToBeRemoved) {
        this.lifeformsToBeRemoved.add(lifeformsToBeRemoved);
    }

    @JsonGetter("itemsToBeRemoved")
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return this.itemsToBeRemoved;
    }

    @JsonSetter("itemsToBeRemoved")
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) {
        this.itemsToBeRemoved = itemsToBeRemoved;
    }

    @JsonGetter("shieldList")
    public List<Pair<CoordinatePair, CoordinatePair>> getShieldList() {
        return this.shieldList;
    }

    @JsonSetter("shieldList")
    public void setShieldList(List<Pair<CoordinatePair, CoordinatePair>> shieldList) {
        this.shieldList = shieldList;
    }

    @JsonGetter("cannonList")
    public List<Pair<CoordinatePair, CoordinatePair>> getCannonList() {
        return this.cannonList;
    }

    @JsonSetter("cannonList")
    public void setCannonList(List<Pair<CoordinatePair, CoordinatePair>> cannonList) {
        this.cannonList = cannonList;
    }

    @JsonGetter("engineList")
    public List<Pair<CoordinatePair, CoordinatePair>> getEngineList() {
        return this.engineList;
    }

    @JsonSetter("engineList")
    public void setEngineList(List<Pair<CoordinatePair, CoordinatePair>> engineList) {
        this.engineList = engineList;
    }

    @JsonGetter("batteriesToBeStolen")
    public List<CoordinatePair> getBatteriesToBeStolen() {
        return this.batteriesToBeStolen;
    }

    @JsonSetter("batteriesToBeStolen")
    public void setBatteriesToBeStolen(List<CoordinatePair> batteriesToBeStolen) {
        this.batteriesToBeStolen = batteriesToBeStolen;
    }
}