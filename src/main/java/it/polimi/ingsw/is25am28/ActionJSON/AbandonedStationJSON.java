package it.polimi.ingsw.is25am28.ActionJSON;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class AbandonedStationJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public AbandonedStationJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public AbandonedStationJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public AbandonedStationJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    /**
     * Returns the visitShip that should be set in the JSON, otherwise it throws an Exception
     * */
    public boolean getVisitStation() throws IllegalStateException{
        if (!data.containsKey("visitStation")) {
            throw new IllegalStateException("Key 'visitStation' is missing in JSON data");
        }

        return (boolean) data.get("visitStation");
    }

    /**
     * Set the visitShip to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setVisitStation(boolean visitStation) throws IllegalArgumentException {
        data.put("visitStation", visitStation);
    }

    /**
     * Get the list of resources that the user want to drop off
     * */

    @SuppressWarnings("unchecked")
    public List<Item> getResourcesToDropOff() throws IllegalStateException {
        if (!data.containsKey("resourcesToDropOff")) {
            throw new IllegalStateException("Key 'resourcesToDropOff' is missing in JSON data");
        }

        Object obj = data.get("resourcesToDropOff");
        if (!(obj instanceof JSONArray jsonArray)) {
            throw new IllegalStateException("Expected 'resourcesToDropOff' to be a JSON array");
        }

        List<Item> resourcesToDropOff = new ArrayList<>();

        for (Object o : jsonArray) {
            if (!(o instanceof String resourceName)) {
                throw new IllegalStateException("Invalid resource name type in 'resourcesToDropOff'");
            }

            try {
                ItemColor color = ItemColor.valueOf(resourceName);
                resourcesToDropOff.add(new Item(color));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Unexpected resource name: " + resourceName);
            }
        }

        return resourcesToDropOff;
    }

    @SuppressWarnings("unchecked")
    public void setResourcesToDropOff(List<Item> items) {
        JSONArray jsonArray = new JSONArray();

        for (Item item : items) {
            jsonArray.add(item.toString()); // We used the to string to obtain the correct value
        }

        data.put("resourcesToDropOff", jsonArray);
    }

    @SuppressWarnings("unchecked")
    public List<Item> getResourcesToTake() throws IllegalStateException {
        if (!data.containsKey("resourcesToTake")) {
            throw new IllegalStateException("Key 'resourcesToTake' is missing in JSON data");
        }

        Object obj = data.get("resourcesToTake");
        if (!(obj instanceof JSONArray jsonArray)) {
            throw new IllegalStateException("Expected 'resourcesToTake' to be a JSON array");
        }

        List<Item> resourcesToTake = new ArrayList<>();

        for (Object o : jsonArray) {
            if (!(o instanceof String resourceName)) {
                throw new IllegalStateException("Invalid resource name type in 'resourcesToTake'");
            }

            try {
                ItemColor color = ItemColor.valueOf(resourceName);
                resourcesToTake.add(new Item(color));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Unexpected resource name: " + resourceName);
            }
        }

        return resourcesToTake;
    }

    @SuppressWarnings("unchecked")
    public void setResourcesToTake(List<Item> items) {
        JSONArray jsonArray = new JSONArray();

        for (Item item : items) {
            jsonArray.add(item.toString()); // We used the toString to obtain the correct color value
        }

        data.put("resourcesToTake", jsonArray);
    }
}
