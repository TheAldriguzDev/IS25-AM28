package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class SmugglersJSON extends ActionJSON {
    private final boolean takeLoot;
    private ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken;
    private ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private final int numberOfDoubleCannonsActivated;
    private final int takenBatteries;


    public SmugglersJSON(@JsonProperty("takeLoot") boolean takeLoot,
                         @JsonProperty("itemsToBeTaken") ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken,
                         @JsonProperty("itemsToBeRemoved") ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved,
                         @JsonProperty("numberOfDoubleCannonsActivated") int numberOfDoubleCannonsActivated,
                         @JsonProperty("takenBatteries") int takenBatteries) {
        this.takeLoot = takeLoot;
        this.itemsToBeTaken = itemsToBeTaken;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.numberOfDoubleCannonsActivated = numberOfDoubleCannonsActivated;
    }

    public boolean getTakeLoot() {
        return takeLoot;
    }

    public ArrayList<ComponentHelper<ItemColor>> getItemsToBeTaken() {
        return itemsToBeTaken;
    }

    public ArrayList<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return itemsToBeRemoved;
    }

    public int getNumberOfDoubleCannonsActivated() {
        return numberOfDoubleCannonsActivated;
    }

    public int getTakenBatteries() {
        return takenBatteries;
    }

    //Sevono variabili diverse per le cose da droppare
    /*
    public SmugglersJSON (JSONObject data) {
        super(data);
    }

    public SmugglersJSON (String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    // {color}ToTake: numero di item di qual colore che il player prende dalla carta
    // {color}ToDrop: numero di item di quel colore che il player rimuove dalla nave

    // Getters
    public boolean getTakeLoot() throws IllegalStateException {
        if (!data.containsKey("takeCredits")) {
            throw new IllegalStateException("Key 'takeCredits' is missing in JSON data");
        }
        return (boolean)data.get("takeLoot");
    }

    public int getRedToLoad() throws IllegalStateException {
        if (!data.containsKey("redToLoad")) {
            throw new IllegalStateException("Key 'redToLoad' is missing in JSON data");
        }
        return (int)data.get("redToLoad");
    }

    public int getYellowToLoad() throws IllegalStateException {
        if (!data.containsKey("yellowToLoad")) {
            throw new IllegalStateException("Key 'yellowToLoad' is missing in JSON data");
        }
        return (int)data.get("yellowToLoad");
    }

    public int getBlueToLoad() throws IllegalStateException {
        if (!data.containsKey("blueToLoad")) {
            throw new IllegalStateException("Key 'blueToLoad' is missing in JSON data");
        }
        return (int)data.get("blueToLoad");
    }

    public int getGreenToLoad() throws IllegalStateException {
        if (!data.containsKey("greenToLoad")) {
            throw new IllegalStateException("Key 'greenToLoad' is missing in JSON data");
        }
        return (int)data.get("greenToLoad");
    }

    public int getRedToDrop() throws IllegalStateException {
        if (!data.containsKey("redToDrop")) {
            throw new IllegalStateException("Key 'redToDrop' is missing in JSON data");
        }
        return (int)data.get("redToDrop");
    }

    public int getYellowToDrop() throws IllegalStateException {
        if (!data.containsKey("yellowToDrop")) {
            throw new IllegalStateException("Key 'yellowToDrop' is missing in JSON data");
        }
        return (int)data.get("yellowToDrop");
    }

    public int getBlueToDrop() throws IllegalStateException {
        if (!data.containsKey("blueToDrop")) {
            throw new IllegalStateException("Key 'blueToDrop' is missing in JSON data");
        }
        return (int)data.get("blueToDrop");
    }

    public int getGreenToDrop() throws IllegalStateException {
        if (!data.containsKey("greenToDrop")) {
            throw new IllegalStateException("Key 'greenToDrop' is missing in JSON data");
        }
        return (int)data.get("greenToDrop");
    }

    public int getNumberOfDoubleCannonsActivated() throws IllegalStateException {
        if (!data.containsKey("numberOfDoubleCannonsActivated")) {
            throw new IllegalStateException("Key 'numberOfDoubleCannonsActivated' is missing in JSON data");
        }
        return (int)data.get("numberOfDoubleCannonsActivated");
    }


    // Setters
    @SuppressWarnings("unchecked")
    public void setTakeLoot (boolean takeLoot) throws IllegalArgumentException {
        data.put("takeLoot", takeLoot);
    }
    @SuppressWarnings("unchecked")
    public void setRedToLoad (int redToLoad) throws IllegalArgumentException {
        data.put("redToLoad", redToLoad);
    }
    @SuppressWarnings("unchecked")
    public void setYellowToLoad (int redToLoad) throws IllegalArgumentException {
        data.put("redToLoad", redToLoad);
    }
    @SuppressWarnings("unchecked")
    public void setBlueToLoad (int redToLoad) throws IllegalArgumentException {
        data.put("redToLoad", redToLoad);
    }
    @SuppressWarnings("unchecked")
    public void setGreenToLoad (int redToLoad) throws IllegalArgumentException {
        data.put("redToLoad", redToLoad);
    }
    @SuppressWarnings("unchecked")
    public void setRedToDrop (int redToDrop) throws IllegalArgumentException {
        data.put("redToDrop", redToDrop);
    }
    @SuppressWarnings("unchecked")
    public void setYellowToDrop (int yellowToDrop) throws IllegalArgumentException {
        data.put("yellowToDrop", yellowToDrop);
    }
    @SuppressWarnings("unchecked")
    public void setBlueToDrop (int blueToDrop) throws IllegalArgumentException {
        data.put("blueToDrop", blueToDrop);
    }
    @SuppressWarnings("unchecked")
    public void setGreenToDrop (int greenToDrop) throws IllegalArgumentException {
        data.put("greenToDrop", greenToDrop);
    }
    @SuppressWarnings("unchecked")
    public void setNumOfDoubleCannonsActivated (int numOfDoubleCannonsActivated) throws IllegalArgumentException {
        data.put("numOfDoubleCannonsActivated", numOfDoubleCannonsActivated);
    }*/
}