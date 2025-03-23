package it.polimi.ingsw.is25am28.EventCards;
    
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Storage;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;

import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class VisitPlanets extends EventCard {
    private final Map<Integer, Map<ItemColor, Integer>> itemsPerPlanet;
    private final Map<String, Pair<Integer, Boolean>> selectedPlanetAndDecision;
    private final int movementSteps;
    private final ResourceBank resourceBank;

    private List<ComponentHelper<ItemColor>> resourceToDropOff;
    private List<ComponentHelper<ItemColor>> resourceToTake;

    /*
    public VisitPlanets(
            String cardName,
            int cardLevel,
            int movementSteps,
            JSONArray data,
            ResourceBank resourceBank,
            Board board
    ) throws RuntimeException
    {
        super(cardName, cardLevel, board);
        this.movementSteps = movementSteps;
        this.resourceBank = resourceBank;
        this.itemsPerPlanet = new HashMap<Integer, Map<ItemColor, Integer>>();
        this.selectedPlanetAndDecision = new HashMap<>();
        int planetCount = 0;

        Map<ItemColor, Integer> planetItemsMap;

        try {
            for (Object obj : data) {
                JSONObject planet = (JSONObject) obj;
                planetItemsMap = new HashMap<>();

                // Putting the amount of blue items on the planet
                planetItemsMap.put(ItemColor.BLUE, ((int) planet.get("blue")));

                // Putting the amount of green items on the planet
                planetItemsMap.put(ItemColor.GREEN, ((int) planet.get("green")));

                // Putting the amount of yellow items on the planet
                planetItemsMap.put(ItemColor.YELLOW, ((int) planet.get("yellow")));

                // Putting the amount of red items on the planet
                planetItemsMap.put(ItemColor.RED, ((int) planet.get("red")));

                // Finally, adding the planet item list to the map
                this.itemsPerPlanet.put(planetCount, planetItemsMap);
                planetCount++;
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: JSON parsing error in VisitPlanet constructor");
        }
    }
     */

    public VisitPlanets(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("movementSteps") int movementSteps,
            @JsonProperty("itemsPerPlanet") Map<Integer, Map<ItemColor, Integer>> itemsPerPlanet,
            @JsonProperty("selectedPlanetAndDecisions") Map<String, Pair<Integer, Boolean>> selectedPlanetAndDecision,
            ResourceBank resourceBank,
            Board board
    ) {
        super(cardName, cardLevel, board);

        this.movementSteps = movementSteps;
        this.itemsPerPlanet = itemsPerPlanet;
        this.selectedPlanetAndDecision = selectedPlanetAndDecision;
        this.resourceBank = resourceBank;
    }

    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            this.cardUsed();

            // Add the resources from the player to the bank
            for ( ComponentHelper<ItemColor> resourceDrop : this.resourceToDropOff ) {
                resourceDrop.getItem().ifPresent( i ->
                        this.resourceBank.addResourceToBankFromPlayer(
                                this.getCurrentPlayer().get(),
                                i,
                                resourceDrop.getI(),
                                resourceDrop.getJ()));
            }

            // Add the resources from the bank to the player
            for ( ComponentHelper<ItemColor> resourceTake : this.resourceToTake ) {
                resourceTake.getItem().ifPresent( i ->
                        this.resourceBank.addResourceToPlayerFromBank(
                                this.getCurrentPlayer().get(),
                                i,
                                resourceTake.getI(),
                                resourceTake.getJ()));
            }
        }
    }

    @Override
    protected void malusEffect() {
        Board board = this.getBoard();
        List<Player> players = board.getPlayers();
        int len = players.size();

        // Applying the backwards movement if the player's corresponding
        // choice to land on the selected planet is set to true
        for (int i = len - 1; i >= 0; i--) {
            if (selectedPlanetAndDecision.get(players.get(i).getNickname()).getValue()) {
                board.movePlayerBackwards(
                    players.get(i),
                    movementSteps
                );
            }
        }
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        VisitPlanetsJSON visitPlanets;

        try {
             visitPlanets = (VisitPlanetsJSON) data;
        } catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid visitPlanet JSON");
        }

        List<ComponentHelper<ItemColor>> itemsToBeRemoved;
        List<ComponentHelper<ItemColor>> itemsToBeTaken;

        for (Player player : players) {
            if (visitPlanets.getPlayerLandingDecision(player)) {
                itemsToBeRemoved = visitPlanets.getItemsToBeRemovedFromPlayer(player);
                itemsToBeTaken = visitPlanets.getItemsToBeTakenFromPlayer(player);

                this.bonusEffect();
                this.malusEffect();
            }
        }

        // Set this card as used
        this.cardUsed();

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        return null;
    }
}
