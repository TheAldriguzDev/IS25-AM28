package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.EventCards.Epidemy;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class EpidemyJSON extends ActionJSON {
    public EpidemyJSON(
            @JsonProperty("playerNickname") String playerNickname
    ) {
        super(playerNickname);
    }
}
