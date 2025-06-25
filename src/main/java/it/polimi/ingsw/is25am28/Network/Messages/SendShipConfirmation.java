package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}
