package it.polimi.ingsw.is25am28.Model.EventCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.VisitPlanetsJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Storage;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;

import java.util.*;

public class VisitPlanets extends EventCard {
    private final int movementSteps;
    private final Map<Integer, Map<ItemColor, Integer>> itemsPerPlanet;
    private final Map<Integer, Player> playersChosenPlanet;
    private final ResourceBank resourceBank;
    private int playerUseCount;
    private List<ComponentHelper<ItemColor>> itemsToDrop;
    private List<ComponentHelper<ItemColor>> itemsToTake;

    public VisitPlanets(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("movementSteps") int movementSteps,
            @JsonProperty("itemsPerPlanet") List<Map<String, Integer>> itemsPerPlanet,
            ResourceBank resourceBank,
            Board board
    ) {
        super(cardName, cardLevel, board);

        this.movementSteps = movementSteps;
        this.itemsPerPlanet = new HashMap<>();
        this.resourceBank = resourceBank;

        int planetIndex = 0;

        for (Map<String, Integer> planetDescriptor : itemsPerPlanet) {
            Map<ItemColor, Integer> formattedPlanetDescriptor = new HashMap<>();

            // Blue Items Initializer
            formattedPlanetDescriptor.put(
                ItemColor.BLUE,
                planetDescriptor.get("blue")
            );

            // Green Items Initializer
            formattedPlanetDescriptor.put(
                    ItemColor.GREEN,
                    planetDescriptor.get("green")
            );

            // Yellow Items Initializer
            formattedPlanetDescriptor.put(
                    ItemColor.YELLOW,
                    planetDescriptor.get("yellow")
            );

            // Red Items Initializer
            formattedPlanetDescriptor.put(
                    ItemColor.RED,
                    planetDescriptor.get("red")
            );

            // Putting the transformed entry into the itemsPerPlanet map
            this.itemsPerPlanet.put(planetIndex, formattedPlanetDescriptor);
            planetIndex++;
        }

        // Parsing the incoming data and transforming the integer value
        // found in the map into the corresponding color
//        for (Integer planetIndex : itemsPerPlanet.keySet()) {
//            Map<ItemColor, Integer> planetResourceDescriptor = new HashMap<>();
//
//            for (Integer itemColor : itemsPerPlanet.get(planetIndex).keySet()) {
//                switch (itemColor) {
//                    // Blue Item
//                    case 1 -> {
//                        planetResourceDescriptor.put(ItemColor.BLUE, itemsPerPlanet.get(planetIndex).get(1));
//                    }
//                    // Green Item
//                    case 2 -> {
//                        planetResourceDescriptor.put(ItemColor.GREEN, itemsPerPlanet.get(planetIndex).get(2));
//                    }
//                    // Yellow Item
//                    case 3 -> {
//                        planetResourceDescriptor.put(ItemColor.YELLOW, itemsPerPlanet.get(planetIndex).get(3));
//                    }
//                    // Red Item
//                    case 4 -> {
//                        planetResourceDescriptor.put(ItemColor.RED, itemsPerPlanet.get(planetIndex).get(4));
//                    }
//                    default -> throw new IllegalStateException("[VisitPlanets] ERROR: There cannon be more than 4 item colors");
//                }
//            }
//
//            // Putting the transformed entry into the itemsPerPlanet map
//            this.itemsPerPlanet.put(planetIndex, planetResourceDescriptor);
//        }

        // Map containing each player and its chosen planet to land on. If a player
        // is not present in this map, then it means that he didn't choose a planet to land on
        this.playersChosenPlanet = new HashMap<Integer, Player>();
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
                        this.resourceBank.addResourceToPlayerFromBank(
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

        // Moves each player that chose a planet to lan on backwards
        // by the amount specified by the attribute movementSteps
        // NOTE: The players that landed are moved backwards starting from the
        //       player in last place to the player in first place (it's a rule)
        for (i = activePlayers.size() - 1; i >= 0; i--) {
            for (Player player : this.playersChosenPlanet.values()) {
                if (player.equals(activePlayers.get(i))) {
                    this.getBoard().movePlayerBackwards(
                        player,
                        this.movementSteps
                    );
                }
            }
        }
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        VisitPlanetsJSON visitPlanetsJSON;
        List<ComponentHelper<ItemColor>> itemsToVerify;
        Map<ItemColor, Integer> planetConfig;
        ItemColor itemToVerify;
        int chosenPlanetIndex;

        // ActionJSON unpacking
        try {
            visitPlanetsJSON = (VisitPlanetsJSON) data;

            if (this.currentPlayer.isEmpty()) {
                throw new IllegalArgumentException("ERROR: Given player is not present in the current game");
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("[VisitPlanets::useCard] " + e.getMessage());
        }

        // If the given player's ActionJSON response is null, this means that
        // the player did not want to choose a planet, therefore he's skipped
        if (visitPlanetsJSON != null && this.currentPlayer.get().isConnected()) {
            // Check if there is a player playing the card
            if (this.currentPlayer.isEmpty()) {
                throw new IllegalArgumentException("[VisitPlanet::useCard] ERROR: No player is currently playing (Optional contains null)");
            }
            if ( !this.currentPlayer.get().getNickname().equals(visitPlanetsJSON.getPlayerNickname())) {
                throw new IllegalArgumentException("ERROR: Current player and player in visitPlanetJSON do not match (wrong arguments)");
            }

            // Extracting the player's chosen planet and his landing decision
            chosenPlanetIndex = visitPlanetsJSON.getChosenPlanetIndex();

            // If the given chosenPlanetIndex is not a valid planetID, then
            // the request will be interpreted as if the player did not want
            // to choose a planet to land on
            if (this.itemsPerPlanet.containsKey(chosenPlanetIndex)) {
                // If the chosenPlanetIndex is already present as a key in the map, it
                // means that the specified planet was already chosen, therefore the player
                // must choose another planet among the remaining ones
                if ( !this.playersChosenPlanet.containsKey(chosenPlanetIndex)) {
                    // Activating the resource handling routine only if
                    // the player decided to land on his selected planet

                    // (1) - Before depositing the selected resources on the planet,
                    //       verify that they are actually present on the ship, otherwise
                    //       consider the selected item as unavailable
                    itemsToVerify = visitPlanetsJSON.getItemsToDrop();
                    this.itemsToDrop = new ArrayList<ComponentHelper<ItemColor>>();

                    for (ComponentHelper<ItemColor> itemHelper : itemsToVerify) {
                        // Assume the item is not provided
                        itemToVerify = null;

                        if (itemHelper.getItem().isPresent()) {
                            itemToVerify = itemHelper.getItem().get();
                        }

                        // If an item is actually provided in the Optional container
                        // then check if it's present in the selected storage component
                        // before adding the itemHelper to the list of items to drop
                        if (itemToVerify != null) {
                            Component component = this.currentPlayer.get().getShip().getComponent(
                                    itemHelper.getI(), itemHelper.getJ()
                            );

                            // Safe cast
                            switch (component) {
                                case Storage storage -> {
                                    if (storage.getStoredItems().stream().map(Item::getColor).toList().contains(itemToVerify)) {
                                        // If the given storage component contains the given item color
                                        // to remove, then add its itemHelper to the itemsToDrop list
                                        this.itemsToDrop.add(itemHelper);
                                    }
                                }
                                case null, default -> {
                                    throw new IllegalArgumentException("ERROR: visitPlanetJSON contained a coordinate pair of a non-storage component");
                                }
                            }
                        }
                    }

                    // (2) - Before withdrawing the requested resources from the planet,
                    //       verify that they are actually present, otherwise consider
                    //       the requested item as unavailable
                    itemsToVerify = visitPlanetsJSON.getItemsToTake();
                    this.itemsToTake = new ArrayList<ComponentHelper<ItemColor>>();

                    for (ComponentHelper<ItemColor> itemHelper : itemsToVerify) {
                        // Assume the item is not provided
                        itemToVerify = null;

                        if (itemHelper.getItem().isPresent()) {
                            itemToVerify = itemHelper.getItem().get();
                        }

                        // If an item is actually provided in the Optional container
                        // then check if it's present on the selected planet before
                        // adding the itemHelper to the list of items to take
                        if (itemToVerify != null) {
                            planetConfig = this.itemsPerPlanet.get(visitPlanetsJSON.getChosenPlanetIndex());

                            // Establish whether the provided index is a valid planetID
                            // before querying the planet resource map (i.e.: planetConfig)
                            if (planetConfig != null && planetConfig.get(itemToVerify) > 0) {
                                this.itemsToTake.add(itemHelper);
                            }
                        }
                    }

                    // Finally, apply all the deposits and withdrawals
                    // that are now considered valid resource transfers
                    this.bonusEffect();

                    // Storing the chosen planet to avoid showing
                    // another player the same planetIDs
                    this.playersChosenPlanet.put(
                            chosenPlanetIndex,
                            this.currentPlayer.get()
                    );

                    // Incrementing the use counter for each player that
                    // actually used the card
                    this.playerUseCount++;
                }
                else {
                    throw new IllegalArgumentException("ERROR: Chosen planet index was already chosen by someone else");
                }
            }
        }

        // Getting the next active player
        this.currentPlayer = this.getNextPlayer();

        // Set the "hasBeenUsed" flag to true iff all the available planets
        // have been chosen or if all players have answered to the card (i.e.: currPlayer == players.getLast())
        if (this.playerUseCount == this.itemsPerPlanet.size() || this.currentPlayer.isEmpty()) {
            this.malusEffect();
            this.cardUsed();
        }

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();
        Map<Integer, Map<ItemColor, Integer>> availablePlanets;

        // Generating the map of all the remaining planets to choose from
        availablePlanets = new HashMap<>(this.itemsPerPlanet);

        for (Integer chosenPlanetIndex : this.playersChosenPlanet.keySet()) {
            availablePlanets.remove(chosenPlanetIndex);
        }

        // If the current player is present, then add it to the card state
        this.currentPlayer.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.getCardLevel());
        cardState.setCardIsUsable( !this.hasFinished());
        cardState.setAvailablePlanets(availablePlanets);

        return cardState;
    }
}
