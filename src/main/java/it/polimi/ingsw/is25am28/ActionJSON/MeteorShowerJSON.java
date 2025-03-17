package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class MeteorShowerJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public MeteorShowerJSON() {
        // Fields that it needs to contain:
        // "diceThrow" - to specify row and column that are potentially targeted
        // "shield" - The coordinates (row, col) of the shield to activate to defend from a small meteor
        // "shoot" - The coordinates (row, col) of the cannon to activate to defend from a big meteor

        // Constructing the JSONObject fields
        // -1 is a placeholder that will be overwritten with real data
        JSONObject container = new JSONObject();

        container.put("diceResult", -1);
        container.put("shield", -1);
        container.put("shoot", -1);

        this.setData(container);
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
