package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class EpidemyJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public EpidemyJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public EpidemyJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public EpidemyJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }
}
