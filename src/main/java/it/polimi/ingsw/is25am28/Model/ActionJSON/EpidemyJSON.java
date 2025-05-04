package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EpidemyJSON extends ActionJSON {

    public EpidemyJSON() {}

    public EpidemyJSON(
            @JsonProperty("playerNickname") String playerNickname
    ) {
        super(playerNickname);
    }
}
