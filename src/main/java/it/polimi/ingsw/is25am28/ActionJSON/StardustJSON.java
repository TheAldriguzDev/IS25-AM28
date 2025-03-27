package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class StardustJSON extends ActionJSON {

    public StardustJSON (@JsonProperty("playerNickname") String playerNickname) {
        super(playerNickname);
    }

    /*
    public StardustJSON (JSONObject data) {
        super(data);
    }

    public StardustJSON (String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }*/
}