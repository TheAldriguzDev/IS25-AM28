package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ConstructionDeckDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private Integer subDeck;
    private boolean isSelected;

    public ConstructionDeckDTO() {}

    public ConstructionDeckDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("subDeck") Integer subDeck,
            @JsonProperty("isSelected")boolean isSelected) {
        this.playerNickname = playerNickname;
        this.subDeck = subDeck;
        this.isSelected = isSelected;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public ConstructionDeckDTO setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("subDeck")
    public Integer getSubDeck() {
        return subDeck;
    }

    @JsonSetter("subDeck")
    public ConstructionDeckDTO setSubDeck(Integer subDeck) {
        this.subDeck = subDeck;
        return this;
    }

    @JsonGetter("isSelected")
    public boolean isSelected() {
        return isSelected;
    }

    @JsonSetter("isSelected")
    public ConstructionDeckDTO setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }
}
