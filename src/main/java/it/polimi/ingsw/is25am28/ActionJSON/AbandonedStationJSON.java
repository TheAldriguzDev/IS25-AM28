package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

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
}
