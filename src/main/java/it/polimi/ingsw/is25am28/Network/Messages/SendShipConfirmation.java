package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;

import java.util.ArrayList;
import java.util.List;

public final class SendShipConfirmation implements Message {
    private final String playerNickname;
    private final Integer reservedTiles;

    @JsonCreator
    public SendShipConfirmation(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("reservedTiles") Integer reservedTiles
    ) {
        this.playerNickname = playerNickname;
        this.reservedTiles = reservedTiles;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonGetter("reservedTiles")
    public Integer getReservedTiles() {
        return this.reservedTiles;
    }

    @Override
    public boolean validate() {
        return this.playerNickname != null
                && !this.playerNickname.isEmpty()
                && this.reservedTiles != null;
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (playerNickname == null || playerNickname.isEmpty()) {
            errors.add("ERROR: The 'playerNickname' field cannot be null or empty.'");
        }

        if (this.reservedTiles == null) {
            errors.add("ERROR: The 'reservedTiles' field cannot be null'");
        }

        return errors;
    }
}
