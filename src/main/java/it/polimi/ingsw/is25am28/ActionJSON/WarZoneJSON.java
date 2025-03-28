package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;

import java.util.List;

public class WarZoneJSON extends ActionJSON {
    private int usedEnergy;
    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;

    /**
     * Default constructor
     * */
    public WarZoneJSON() {
        this.usedEnergy = 0;
    }

    public WarZoneJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("usedEnergy") int usedEnergy,
            @JsonProperty("lifeformsToBeRemoved") List<ComponentHelper<LifeformType>> lifeformsToBeRemoved,
            @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved
    ) {
        this.usedEnergy = usedEnergy;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
        this.itemsToBeRemoved = itemsToBeRemoved;
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
}
