package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class MeteorShowerJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public MeteorShowerJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public MeteorShowerJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public MeteorShowerJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }
}
