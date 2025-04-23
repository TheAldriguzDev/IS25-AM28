package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;

import java.util.ArrayList;
import java.util.List;

public final class SendShipConfirmation implements Message {
    private String playerNickname;
    private List<ComponentHelper<ConstructionComponentDTO>> playerShip;

    @JsonCreator
    public SendShipConfirmation(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("playerShip") List<ComponentHelper<ConstructionComponentDTO>> playerShip
    ) {
        this.playerNickname = playerNickname;
        this.playerShip = playerShip;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonGetter("playerShip")
    public List<ComponentHelper<ConstructionComponentDTO>> getPlayerShip() {
        return this.playerShip;
    }

    @Override
    public boolean validate() {
        return this.playerNickname != null
                && !this.playerNickname.isEmpty()
                && this.playerShip != null
                && !this.playerShip.isEmpty();
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (playerNickname == null || playerNickname.isEmpty()) {
            errors.add("ERROR: Your name cannot be null or empty");
        }

        if (this.playerShip == null || this.playerShip.isEmpty()) {
            errors.add("ERROR: Your ship cannot be null or empty");
        }

        return errors;
    }
}
