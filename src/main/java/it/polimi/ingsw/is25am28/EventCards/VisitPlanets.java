package it.polimi.ingsw.is25am28.EventCards;
    
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class VisitPlanets extends EventCard {
    private final Map<Integer, List<Item>> itemsPerPlanet;
    private final Map<Player, Integer> planetSelectedByPlayer;
    private final int movementSteps;
    private final int planetCount;
    // private final String imagePath;

    // Constructor
    public VisitPlanets(
            String cardName,
            int cardLevel,
            int movementSteps,
            JSONArray data
            // String imagePath
    ) throws RuntimeException
    {
        super(cardName, cardLevel);
        this.planetSelectedByPlayer = new HashMap<Player, Integer>();
        this.movementSteps = movementSteps;
        this.itemsPerPlanet = new HashMap<Integer, List<Item>>();
        // this.imagePath = imagePath;

        // Parsing the given JSONArray data and filling up
        // each planet's respective Item list
        int len = data.size();
        int amount, i, j;

        for (i = 0; i < len; i++) {
            // This list will be populated with all the Items
            // declared in the given JSONArray
            List<Item> itemList = new ArrayList<Item>();
            JSONObject JSONItemList = (JSONObject) data.get(i);

            // (1) - Initializing the Blue Items
            amount = (int) JSONItemList.get("blue");
            for (j = 0; j < amount; j++) {
                itemList.add(new Item(ItemColor.BLUE));
            }

            // (2) - Initializing the Green Items
            amount = (int) JSONItemList.get("green");
            for (j = 0; j < amount; j++) {
                itemList.add(new Item(ItemColor.GREEN));
            }

            // (3) - Initializing the Yellow Items
            amount = (int) JSONItemList.get("yellow");
            for (j = 0; j < amount; j++) {
                itemList.add(new Item(ItemColor.YELLOW));
            }

            // (4) - Initializing the Red (Special) Items
            amount = (int) JSONItemList.get("red");
            for (j = 0; j < amount; j++) {
                itemList.add(new Item(ItemColor.RED));
            }

            // Finally, the list of the currently iterated
            // planet is added to the map
            this.itemsPerPlanet.put(i, itemList);
        }

        // Storing the amount of planets available such that
        // there upper limit on the planet IDs is always known
        this.planetCount = this.itemsPerPlanet.size();

        // Throwing a RuntimeException if for some reason the data received for
        // initialization contains less than 2 or more than 4 planets to initialize
        if (this.planetCount < 2 || this.planetCount > 4) {
            throw new RuntimeException("ERROR: VisitPlanets can only have between 2 and 4 distinct planets (boundaries included).");
        }
    }

    @Override
    protected void bonusEffect() {
        // Implemented in useCard (too many local variable dependencies)
    }

    @Override
    protected void malusEffect() {
        this.currentPlayer.ifPresent(
            (Player player) -> {
                this.board.movePlayerBackwards(this.currentPlayer.get(), this.movementSteps);
            }
        );
    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        String playerName;
        int selectedPlanetID;
        boolean playerWantsToLand;

        try {
            playerName = (String) data.get("playerName");
            selectedPlanetID = (int) data.get("selectedPlanetID");
            playerWantsToLand = (boolean) data.get("playerWantsToLand");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error while parsing the user requested action: " + e.getMessage());
        }

        // Throwing an exception if the previous data was parsed correctly
        // but contains wrong arguments
        if (playerName == null) {
            throw new IllegalArgumentException("ERROR: playerName is null");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("ERROR: playerName is empty");
        }
        if (selectedPlanetID < 0 || selectedPlanetID > this.planetCount) {
            throw new IllegalArgumentException("ERROR: selectedPlanetID is an illegal value");
        }
        if (this.planetSelectedByPlayer.containsValue(selectedPlanetID)) {
            // If player tries to select an already selected planet, the method
            // waits again for a correct answer
            return this.useCard(null /*NEW RESPONSE NEEDED*/);
        }

        // Storing the current player's chosen planet
        this.currentPlayer.ifPresent(
                (Player player) -> {
                    this.planetSelectedByPlayer.put(player, selectedPlanetID);
                }
        );

        // Apply card effects if the player lands
        if (playerWantsToLand) {
            // bonusEffect();
        }

        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}
