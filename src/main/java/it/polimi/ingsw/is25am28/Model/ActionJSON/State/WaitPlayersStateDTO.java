package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents the state of the game lobby where players are waiting to join.
 *
 * This state can be handled by the {@link StateVisitor} interface, allowing
 * for visitor-based processing in the application.
 *
 * This class is immutable and serializable.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class WaitPlayersStateDTO extends StateDTO implements Serializable {
    private List<String> availableColors;
    private Map<String, PlayerColor> usedNicknames;
    private int lobbyTotalSpot;
    private int availableSpots;

    public WaitPlayersStateDTO() {}

    public WaitPlayersStateDTO(
                @JsonProperty("availableColors") List<String> availableColors,
                @JsonProperty("usedNicknames") Map<String, PlayerColor> usedNicknames,
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
    public Map<String, PlayerColor> getUsedNicknames() {
        return this.usedNicknames;
    }

    @JsonSetter("usedNicknames")
    public void setUsedNicknames(Map<String, PlayerColor> usedNicknames) {
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
