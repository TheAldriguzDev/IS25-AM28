package it.polimi.ingsw.is25am28.Model.ActionJSON.State;


import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;

public interface StateVisitor {
    // ===== GENERAL STATE ===== //
    void visit (StateDTO state);

    // ===== LOBBY STATE ===== //
    void visit (AvailableGamesDTO state) throws Exception;
    void visit (WaitPlayersStateDTO state) throws Exception;
    void visit (ReconnectDTO state) throws Exception;

    // ===== SHIP CONSTRUCTION - FIX - POPULATE  ===== //
    void visit (ShipConstructionDTO state) throws Exception;
    void visit (ConstructionComponentDTO state) throws Exception;
    void visit (PlacedComponentDTO state) throws Exception;
    void visit (FixedComponentDTO state) throws Exception;
    void visit (PopulateShipComponentDTO state) throws Exception;
    void visit (PlayerEndedShipDTO state) throws Exception;
    void visit (TimerDTO state) throws Exception;
    void visit (FixShipDTO state) throws Exception;
    void visit (PopulateShipDTO state) throws Exception;

    // ===== CARD ROUND ===== //
    void visit (CardRoundDTO state) throws Exception;

    // ===== END GAME ===== //
    void visit (EndGameDTO state) throws Exception;

    // ===== UTILITY ===== //
    void visit (DisconnectedPlayerDTO state);
    void visit (InsufficientPlayerDTO state);
}
