package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class VisitPlanets extends EventCard {
    private final Map<Integer, List<Item>> itemsPerPlanet;
    private final int movementSteps;
    // private final String imagePath;

    // Constructor
    public VisitPlanets(
            String cardName,
            int cardLevel,
            int movementSteps,
            JSONArray data
            // String imagePath
    ) {
        super(cardName, cardLevel);
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
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

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

        if (
                (playerName != null) && (!playerName.isEmpty())
                        && (selectedPlanetID >= 0) && (selectedPlanetID <= 3)
        ) {
            if (playerWantsToLand) {

            }
            else {

            }
        }
        else {
            throw new IllegalArgumentException("ERROR: Some fields are empty; cannot apply card to player");
        }

        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}
