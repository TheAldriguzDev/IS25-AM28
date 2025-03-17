package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class SmugglersStateJSON extends ActionJSON {

    public SmugglersStateJSON() {
        super();
    }

    public SmugglersStateJSON(JSONObject data) {
        super(data);
    }

    public SmugglersStateJSON (String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }




}
