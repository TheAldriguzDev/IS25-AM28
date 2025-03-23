package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.List;
import java.util.Map;

public class VisitPlanetsJSON extends ActionJSON {
    private Map<Player, Integer> playerToPlanet;
    private Map<Player, Boolean> landingDecisions;
    private Map<Player, List<ComponentHelper<ItemColor>>> itemsToBeRemoved;
    private Map<Player, List<ComponentHelper<ItemColor>>> itemsToBeTaken;

    @JsonCreator
    public VisitPlanetsJSON(
            @JsonProperty("playerToPlanet") Map<Player, Integer> playerToPlanet,
            @JsonProperty("wantsToLand") Map<Player, Boolean> landingDecisions,
            @JsonProperty("itemsToBeRemoved") Map<Player, List<ComponentHelper<ItemColor>>> itemsToBeRemoved,
            @JsonProperty("itemsToBeTaken") Map<Player, List<ComponentHelper<ItemColor>>> itemsToBeTaken
    ) {
        this.playerToPlanet = playerToPlanet;
        this.landingDecisions = landingDecisions;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.itemsToBeTaken = itemsToBeTaken;
    }

    @JsonSetter("playerToPlanet")
    public void setAllPlayersChosenPlanets(Map<Player, Integer> playerToPlanet) {
        this.playerToPlanet = playerToPlanet;
    }

    @JsonGetter("playerToPlanet")
    public int getPlayerChosenPlanet(Player player) {
        return this.playerToPlanet.get(player);
    }

    @JsonSetter("landingDecisions")
    public void setAllPlayersLandingDecisions(Map<Player, Boolean> landingDecisions) {
        this.landingDecisions = landingDecisions;
    }

    @JsonGetter("landingDecisions")
    public boolean getPlayerLandingDecision(Player player) {
        return this.landingDecisions.get(player);
    }

    @JsonSetter("itemsToBeRemoved")
    public void setItemsToBeRemoved(Map<Player, List<ComponentHelper<ItemColor>>> itemsToBeRemoved) {
        this.itemsToBeRemoved = itemsToBeRemoved;
    }

    @JsonGetter("itemsToBeRemoved")
    public List<ComponentHelper<ItemColor>> getItemsToBeRemovedFromPlayer(Player player) {
        return this.itemsToBeRemoved.get(player);
    }

    @JsonSetter("itemsToBeTaken")
    public void setItemsToBeTaken(Map<Player, List<ComponentHelper<ItemColor>>> itemsToBeTaken) {
        this.itemsToBeTaken = itemsToBeTaken;
    }

    @JsonGetter("itemsToBeTaken")
    public List<ComponentHelper<ItemColor>> getItemsToBeTakenFromPlayer(Player player) {
        return this.itemsToBeTaken.get(player);
    }
}
