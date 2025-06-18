package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

/**
 * Represents a data transfer data object that holds information about the subDecks in the construction phase
 *
 * * Annotations from the Jackson library are used for JSON serialization and deserialization,
 *  * ensuring that only non-null values are included in the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ConstructionDeckDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private Integer subDeck;
    private boolean isSelected;

    public ConstructionDeckDTO() {}

    public ConstructionDeckDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("subDeck") Integer subDeck,
            @JsonProperty("isSelected") boolean isSelected) {
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

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
