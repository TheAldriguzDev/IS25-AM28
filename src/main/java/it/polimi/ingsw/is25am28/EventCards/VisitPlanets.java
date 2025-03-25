package it.polimi.ingsw.is25am28.EventCards;
    
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;

import javafx.util.Pair;

import java.util.*;

public class VisitPlanets extends EventCard {
    private final int movementSteps;
    private final Map<Integer, Map<ItemColor, Integer>> itemsPerPlanet;
    private final Map<Integer, Pair<Player, Boolean>> playersChosenPlanetAndLandingDecision;
    private final ResourceBank resourceBank;
    private int playerUseCount;
    private List<ComponentHelper<ItemColor>> itemsToDrop;
    private List<ComponentHelper<ItemColor>> itemsToTake;

    public VisitPlanets(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("movementSteps") int movementSteps,
            @JsonProperty("itemsPerPlanet") Map<Integer, Map<Integer, Integer>> itemsPerPlanet,
            ResourceBank resourceBank,
            Board board
    ) {
        super(cardName, cardLevel, board);

        this.movementSteps = movementSteps;
        this.itemsPerPlanet = new HashMap<>();
        this.resourceBank = resourceBank;

        // Parsing the incoming data and transforming the integer value
        // found in the map into the corresponding color
        for (Integer planetIndex : itemsPerPlanet.keySet()) {
            Map<ItemColor, Integer> planetResourceDescriptor = new HashMap<>();

            for (Integer itemColor : itemsPerPlanet.get(planetIndex).keySet()) {
                switch (itemColor) {
                    // Blue Item
                    case 1 -> {
                        planetResourceDescriptor.put(ItemColor.BLUE, itemsPerPlanet.get(planetIndex).get(1));
                    }
                    // Green Item
                    case 2 -> {
                        planetResourceDescriptor.put(ItemColor.GREEN, itemsPerPlanet.get(planetIndex).get(2));
                    }
                    // Yellow Item
                    case 3 -> {
                        planetResourceDescriptor.put(ItemColor.YELLOW, itemsPerPlanet.get(planetIndex).get(3));
                    }
                    // Red Item
                    case 4 -> {
                        planetResourceDescriptor.put(ItemColor.RED, itemsPerPlanet.get(planetIndex).get(4));
                    }
                    default -> throw new IllegalStateException("[VisitPlanets] ERROR: There cannon be more than 4 item colors");
                }
            }

            // Putting the transformed entry into the itemsPerPlanet map
            this.itemsPerPlanet.put(planetIndex, planetResourceDescriptor);
        }

        // Map containing all the chosen planets and the corresponding player
        // that chose it, as well as if that player decided to land or not
        this.playersChosenPlanetAndLandingDecision = new HashMap<Integer, Pair<Player, Boolean>>();
        this.playerUseCount = 0;
    }

    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            // (1) - Add the resources from the player to the bank
            for (ComponentHelper<ItemColor> itemToDrop : this.itemsToDrop) {
                itemToDrop.getItem().ifPresent(
                        (ItemColor color) -> {
                            this.resourceBank.addResourceToBankFromPlayer(
                                    this.getCurrentPlayer().get(),
                                    color,
                                    itemToDrop.getI(),
                                    itemToDrop.getJ()
                            );
                        }
                );
            }

            // (2) - Add the resources from the bank to the player
            for (ComponentHelper<ItemColor> itemToTake : this.itemsToTake) {
                itemToTake.getItem().ifPresent(
                        (ItemColor color) -> {
                            this.resourceBank.addResourceToBankFromPlayer(
                                    this.getCurrentPlayer().get(),
                                    color,
                                    itemToTake.getI(),
                                    itemToTake.getJ()
                            );
                        }
                );
            }
        }
    }

    @Override
    protected void malusEffect() {
        List<Player> activePlayers = this.getBoard().getPlayers();
        int i;

        // Moves each player that chose a planet and decided to land on it
        // backwards by the amount specified by the attribute movementSteps
        // NOTE: The players that landed are moved backwards starting from the
        //       player in last place to the player in first place (it's a rule)
        for (i = activePlayers.size() - 1; i >= 0; i--) {
            for (Pair<Player, Boolean> playerChoice : this.playersChosenPlanetAndLandingDecision.values()) {
                if (playerChoice.getKey().equals(activePlayers.get(i))) {
                    if (playerChoice.getValue()) {
                        this.getBoard().movePlayerBackwards(
                                activePlayers.get(i),
                                this.movementSteps
                        );
                    }
                }
            }
        }
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        VisitPlanetsJSON visitPlanetsJSON;
        int chosenPlanetIndex;
        boolean wantsToLand;

        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("[VisitPlanet::useCard] ERROR: No player is currently playing (Optional contains null)");
        }

        // ActionJSON unpacking
        try {
            visitPlanetsJSON = (VisitPlanetsJSON) data;
        } catch (Exception e) {
            throw new IllegalArgumentException("[VisitPlanets::useCard] ERROR: JSON data parsing error");
        }

        // Extracting the player's chosen planet and his landing decision
        chosenPlanetIndex = visitPlanetsJSON.getChosenPlanetIndex();
        wantsToLand = visitPlanetsJSON.getLandingDecision();

        // If the chosenPlanetIndex is already present as a key in the map, it
        // means that the specified planet was already chosen, therefore the player
        // must choose another planet among the remaining ones
        if (!this.playersChosenPlanetAndLandingDecision.containsKey(chosenPlanetIndex)) {
            // Removing the planet index from the list of available planets
            this.playersChosenPlanetAndLandingDecision.put(
                    chosenPlanetIndex,
                    new Pair<Player, Boolean>(
                            this.currentPlayer.get(),
                            wantsToLand
                    )
            );

            // Activating the resource handling routine only if
            // the player decided to land on his selected planet
            if (wantsToLand) {
                this.itemsToDrop = visitPlanetsJSON.getItemsToDrop();
                this.itemsToTake = visitPlanetsJSON.getItemsToTake();
                this.bonusEffect();
            }

            // Incrementing the use counter for each player that used it
            this.playerUseCount++;
        }

        // Set the "hasBeenUsed" flag to true iff all
        // the available planets have been chosen
        if (this.playerUseCount == this.itemsPerPlanet.size()) {
            this.malusEffect();
            this.cardUsed();
        }

        return this;
    }

    @Override
    public VisitPlanetsStateJSON generateState() {
        VisitPlanetsStateJSON visitPlanetsStateJSON;
        Map<Integer, Map<ItemColor, Integer>> availablePlanets;

        // Generating the map of all the remaining planets to choose from
        availablePlanets = new HashMap<>(this.itemsPerPlanet);

        for (Integer chosenPlanetIndex : this.playersChosenPlanetAndLandingDecision.keySet()) {
            availablePlanets.remove(chosenPlanetIndex);
        }

        if (this.getCurrentPlayer().isEmpty()) {
            this.currentPlayer = this.getNextPlayer();
        }

        visitPlanetsStateJSON = new VisitPlanetsStateJSON(
                this.getCurrentPlayer().get().getNickname(),
                this.getCardName(),
                this.getCardLevel(),
                (!this.hasFinished()), // ! hasFinished => the card is still usable
                availablePlanets
        );

        return visitPlanetsStateJSON;
    }
}
