package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConfigGame.class, name = "ConfigGame"),
        @JsonSubTypes.Type(value = NewPlayer.class, name = "NewPlayer"),
        @JsonSubTypes.Type(value = SelectTile.class, name = "SelectTile"),
        @JsonSubTypes.Type(value = DeselectTile.class, name = "DeselectTile"),
        @JsonSubTypes.Type(value = Ping.class, name = "Ping"),
        @JsonSubTypes.Type(value = RefreshGames.class, name = "RefreshGames"),
        @JsonSubTypes.Type(value = Reconnect.class, name = "Reconnect"),
        @JsonSubTypes.Type(value = PlaceTile.class, name = "PlaceTile"),
        @JsonSubTypes.Type(value = SendShipConfirmation.class, name = "SendShipConfirmation")
})

public sealed interface Message extends Serializable permits ConfigGame, DeselectTile, NewPlayer, Ping, PlaceTile, Reconnect, RefreshGames, SelectTile, SendShipConfirmation {

    /**
     * @return true if the message it's correct, otherwise it will return false
     * */
    public boolean validate();

    /**
     * @return a list of strings that will indicate the errors in the message
     * */
    public List<String> getErrors();
}
