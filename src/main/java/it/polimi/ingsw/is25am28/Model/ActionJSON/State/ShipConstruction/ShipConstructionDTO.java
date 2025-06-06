package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ShipConstructionDTO extends StateDTO {
    private List<Map<String, Object>> all_components;
    private List<Integer> flipped_components;
    private List<Integer> selected_components;
    private List<String> playerFinished;
    private TimerDTO timerDTO;

    // Card list that contains the information about the deck in the game
    private List<CardStateJSON> cards;

    public ShipConstructionDTO() {}

    public ShipConstructionDTO(
            @JsonProperty("all_components") List<Map<String, Object>> all_components,
            @JsonProperty("flipped_components") List<Integer> flipped_components,
            @JsonProperty("selected_components") List<Integer> selected_components,
            @JsonProperty("cards") List<CardStateJSON> cards,
            @JsonProperty("playerFinished") List<String> playerFinished,
            @JsonProperty("timerDTO") TimerDTO timerDTO
    ) {
        this.all_components = all_components;
        this.flipped_components = flipped_components;
        this.selected_components = selected_components;
        this.cards = cards;
        this.playerFinished = playerFinished;
        this.timerDTO = timerDTO;
    }

    @JsonGetter("all_components")
    public List<Map<String, Object>> getAllComponents() {
        return all_components;
    }

    @JsonSetter("all_components")
    public ShipConstructionDTO setAllComponents(List<Map<String, Object>> all_components) {
        this.all_components = all_components;
        return this;
    }

    @JsonGetter("flipped_components")
    public List<Integer> getFlippedComponents() {
        return flipped_components;
    }

    @JsonSetter("flipped_components")
    public ShipConstructionDTO setFlippedComponents(List<Integer> flipped_components) {
        this.flipped_components = flipped_components;
        return this;
    }

    @JsonGetter("selected_components")
    public List<Integer> getSelectedComponents() {
        return selected_components;
    }

    @JsonSetter("selected_components")
    public ShipConstructionDTO setSelectedComponents(List<Integer> selected_components) {
        this.selected_components = selected_components;
        return this;
    }

    @JsonGetter("cards")
    public List<CardStateJSON> getCards() {
        return cards;
    }

    @JsonSetter("cards")
    public ShipConstructionDTO setCards(List<CardStateJSON> cards) {
        this.cards = cards;
        return this;
    }

    @JsonGetter("playerFinished")
    public List<String> getPlayerFinished() {
        return this.playerFinished;
    }

    @JsonSetter("playerFinished")
    public ShipConstructionDTO setPlayerFinished(List<String> playerFinished) {
        this.playerFinished = playerFinished;
        return this;
    }

    @JsonGetter("timerDTO")
    public TimerDTO getTimerDTO() {
        return this.timerDTO;
    }

    @JsonSetter("timerDTO")
    public ShipConstructionDTO setTimerDTO(TimerDTO timerDTO) {
        this.timerDTO = timerDTO;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
