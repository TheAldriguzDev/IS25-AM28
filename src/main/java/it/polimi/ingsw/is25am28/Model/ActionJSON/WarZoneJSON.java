package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class WarZoneJSON extends ActionJSON {
    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldList;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cannonList;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> engineList;

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
            @JsonProperty("shieldList") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldList,
            @JsonProperty("cannonList") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cannonList,
            @JsonProperty("cannonList") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> engineList
    ) {
        this.playerNickname = playerNickname;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.shieldList = shieldList;
        this.cannonList = cannonList;
        this.engineList = engineList;
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
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getShieldList() {
        return this.shieldList;
    }

    @JsonSetter("shieldList")
    public void setShieldList(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldList) {
        this.shieldList = shieldList;
    }

    @JsonGetter("cannonList")
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getCannonList() {
        return this.cannonList;
    }

    @JsonSetter("cannonList")
    public void setCannonList(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cannonList) {
        this.cannonList = cannonList;
    }

    @JsonGetter("engineList")
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getEngineList() {
        return this.engineList;
    }

    @JsonSetter("engineList")
    public void setEngineList(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> engineList) {
        this.engineList = engineList;
    }
}