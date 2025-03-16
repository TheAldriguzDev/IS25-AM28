package it.polimi.ingsw.is25am28.ActionJSON;

import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.List;

public class AbandonedShipJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public AbandonedShipJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public AbandonedShipJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public AbandonedShipJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    /**
     * Returns the visitShip that should be set in the JSON, otherwise it throws an Exception
     * */
    public boolean getVisitShip() throws IllegalStateException{
        if (!data.containsKey("visitShip")) {
            throw new IllegalStateException("Key 'visitShip' is missing in JSON data");
        }

        return (boolean) data.get("visitShip");
    }

    /**
     * Set the visitShip to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setVisitShip(boolean visitShip) throws IllegalArgumentException {
        data.put("visitShip", visitShip);
    }

    /**
     * Returns the remove from JSON, otherwise throws an exception.
     */
    public List<Lifeform> getLifeFormToBeRemoved() throws IllegalStateException {
        if (!data.containsKey("lifeFormToBeRemoved")) {
            throw new IllegalStateException("Key 'lifeFormToBeRemoved' is missing in JSON data");
        }

        // Cast the value to JSONArray and convert it to a List<String>
        return (JSONArray) data.get("lifeFormToBeRemoved");
    }

    /**
     * Sets the visitShipList to the given value in JSON.
     */
    public void setLifeFormToBeRemoved(List<Lifeform> lifeFormToBeRemoved) throws IllegalArgumentException {
        if (lifeFormToBeRemoved == null) {
            throw new IllegalArgumentException("lifeFormToBeRemoved cannot be null");
        }

        JSONArray lifeFormArray = new JSONArray();
        lifeFormArray.addAll(lifeFormToBeRemoved);

        data.put("lifeFormToBeRemoved", lifeFormArray);
    }
}
