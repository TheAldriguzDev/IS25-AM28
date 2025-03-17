package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class WarZoneJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public WarZoneJSON() {
        this.data = new JSONObject();

        // Fields in WarZoneJSON:
        // 1 - playerNickname = String of the player's nickname, to which the WarZoneJSON belongs
        // 2 - engines = the amount of engines to activate
        // 3 - cannons = the amount of cannons to activate
        // 4 - shieldsToActivate = a JSONArray of coordinates (row, col) of all the shields
        //                         that the player wants to activate
        this.data.put("playerNickname", null);
        this.data.put("engines", null);
        this.data.put("cannons", null);
        this.data.put("shieldsToActivate", new JSONArray());
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
