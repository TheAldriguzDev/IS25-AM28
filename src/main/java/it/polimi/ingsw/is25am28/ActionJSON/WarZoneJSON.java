package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class WarZoneJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public WarZoneJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public WarZoneJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public WarZoneJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }
}
