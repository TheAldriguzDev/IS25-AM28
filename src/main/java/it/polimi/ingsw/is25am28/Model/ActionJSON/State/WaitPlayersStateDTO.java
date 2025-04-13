package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class WaitPlayersStateDTO extends StateDTO implements Serializable {
    List<String> availableColors;
    List<String> usedNicknames;
    int lobbyTotalSpot;
    int availableSpots;

    public WaitPlayersStateDTO() {}

    public WaitPlayersStateDTO(
                @JsonProperty("availableColors") List<String> availableColors,
                @JsonProperty("usedNicknames") List<String> usedNicknames,
                @JsonProperty("lobbyTotalSpot") int lobbyTotalSpot,
                @JsonProperty("availableSpots") int availableSpots
            ) {
        this.availableColors = availableColors;
        this.usedNicknames = usedNicknames;
        this.lobbyTotalSpot = lobbyTotalSpot;
        this.availableSpots = availableSpots;
    }

    @JsonGetter("availableColors")
    public List<String> getAvailableColors() {
        return this.availableColors;
    }

    @JsonSetter("availableColors")
    public void setAvailableColors(List<String> availableColors) {
        this.availableColors = availableColors;
    }

    @JsonGetter("usedNicknames")
    public List<String> getUsedNicknames() {
        return this.usedNicknames;
    }

    @JsonSetter("usedNicknames")
    public void setUsedNicknames(List<String> usedNicknames) {
        this.usedNicknames = usedNicknames;
    }

    @JsonGetter("lobbyTotalSpot")
    public int getLobbyTotalSpot() {
        return this.lobbyTotalSpot;
    }

    @JsonSetter("lobbyTotalSpot")
    public void setLobbyTotalSpot(int lobbyTotalSpot) {
        this.lobbyTotalSpot = lobbyTotalSpot;
    }

    @JsonGetter("availableSpots")
    public int getAvailableSpots() {
        return this.availableSpots;
    }

    @JsonSetter("availableSpots")
    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
