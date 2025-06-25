package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * The Message interface represents a socket message sent from a client to the server.
 * It serves as the base type for all client-to-server communication over sockets.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConfigGame.class, name = "ConfigGame"),
        @JsonSubTypes.Type(value = NewPlayer.class, name = "NewPlayer"),
        @JsonSubTypes.Type(value = SelectTile.class, name = "SelectTile"),
        @JsonSubTypes.Type(value = DeselectTile.class, name = "DeselectTile"),
        @JsonSubTypes.Type(value = ReserveTile.class, name = "ReserveTile"),
        @JsonSubTypes.Type(value = SelectDeselectSubdeck.class, name = "SelectDeselectSubdeck"),
        @JsonSubTypes.Type(value = Ping.class, name = "Ping"),
        @JsonSubTypes.Type(value = RefreshGames.class, name = "RefreshGames"),
        @JsonSubTypes.Type(value = Reconnect.class, name = "Reconnect"),
        @JsonSubTypes.Type(value = FastShip.class, name = "FastShip"),
        @JsonSubTypes.Type(value = PlaceTile.class, name = "PlaceTile"),
        @JsonSubTypes.Type(value = SendShipConfirmation.class, name = "SendShipConfirmation"),
        @JsonSubTypes.Type(value = FlipTimer.class, name = "FlipTimer"),
        @JsonSubTypes.Type(value = FixShip.class, name = "FixShip"),
        @JsonSubTypes.Type(value = PopulateShip.class, name = "PopulateShip"),
        @JsonSubTypes.Type(value = PlayCard.class, name = "PlayCard")
})
public sealed interface Message extends Serializable permits ConfigGame, DeselectTile, FastShip, FixShip, FlipTimer, NewPlayer, Ping, PlaceTile, PlayCard, PopulateShip, Reconnect, RefreshGames, ReserveTile, SelectDeselectSubdeck, SelectTile, SendShipConfirmation {
    // Nothing
}
