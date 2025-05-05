package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

public class WarZoneJSON extends ActionJSON {
    private int usedEnergy;
    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<ComponentHelper<Void>> shieldList;
    private List<ComponentHelper<Void>> cannonList;

    /**
     * Default constructor
     * */
    public WarZoneJSON() {
        this.usedEnergy = 0;
        this.lifeformsToBeRemoved = new ArrayList<>();
        this.itemsToBeRemoved = new ArrayList<>();
        this.shieldList = new ArrayList<>();
        this.cannonList = new ArrayList<>();
    }

    public WarZoneJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("usedEnergy") int usedEnergy,
            @JsonProperty("lifeformsToBeRemoved") List<ComponentHelper<LifeformType>> lifeformsToBeRemoved,
            @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
            @JsonProperty("shieldList") List<ComponentHelper<Void>> shieldList,
            @JsonProperty("cannonList") List<ComponentHelper<Void>> cannonList
    ) {
        this.playerNickname = playerNickname;
        this.usedEnergy = usedEnergy;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.shieldList = shieldList;
        this.cannonList = cannonList;
    }

    @JsonGetter("usedEnergy")
    public int getUsedEnergy() {
        return this.usedEnergy;
    }

    @JsonSetter("usedEnergy")
    public void setUsedEnergy(int usedEnergy) {
        this.usedEnergy = usedEnergy;
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
    public List<ComponentHelper<Void>> getShieldList() {
        return this.shieldList;
    }

    @JsonSetter("shieldList")
    public void setShieldList(List<ComponentHelper<Void>> shieldList) {
        this.shieldList = shieldList;
    }

    @JsonGetter("cannonList")
    public List<ComponentHelper<Void>> getCannonList() {
        return this.cannonList;
    }

    @JsonSetter("cannonList")
    public void setCannonList(List<ComponentHelper<Void>> cannonList) {
        this.cannonList = cannonList;
    }
}