package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class OpenSpaceJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public OpenSpaceJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public OpenSpaceJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public OpenSpaceJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    /**
     * Returns the player nickname that should be set in the JSON, otherwise it throws an Exception
     * */
    public int getPlayerUsedEnergy() throws IllegalStateException{
        if (!data.containsKey("usedEnergy")) {
            throw new IllegalStateException("Key 'usedEnergy' is missing in JSON data");
        }

        return (int) data.get("usedEnergy");
    }

    /**
     * Set the player nickname to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setPlayerUsedEnergy(int usedEnergy) throws IllegalArgumentException {
        if (usedEnergy < 0) {
            throw new IllegalArgumentException("Used energy must be greater or equal to 0");
        }

        data.put("usedEnergy", usedEnergy);
    }
}
