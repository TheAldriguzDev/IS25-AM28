package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenSpaceJSON extends ActionJSON {
    private int usedEnergy;

    /**
     * Default constructor
     * */
    public OpenSpaceJSON() {
        this.usedEnergy = 0;
    }

    public OpenSpaceJSON(@JsonProperty("playerNickname") String playerNickname,
                         @JsonProperty("usedEnergy") int usedEnergy) {
        super(playerNickname);
        this.usedEnergy = usedEnergy;
    }

    public int getUsedEnergy() {
        return this.usedEnergy;
    }

    public void setUsedEnergy(int usedEnergy) {
        this.usedEnergy = usedEnergy;
    }
}
