package it.polimi.ingsw.is25am28.Model.ActionJSON.State;


import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;

public interface StateVisitor {
    void visit (StateDTO state);
    void visit (AvailableGamesDTO state) throws Exception;
    void visit (WaitPlayersStateDTO state) throws Exception;
    void visit (ReconnectDTO state) throws Exception;
    void visit (ShipConstructionDTO state) throws Exception;
    void visit (ConstructionComponentDTO state) throws Exception;
    void visit (PlacedComponentDTO state) throws Exception;
    void visit (PlayerEndedShipDTO state) throws Exception;
    void visit (TimerDTO state) throws Exception;
    void visit (FixShipDTO state);
    void visit (PopulateShipDTO state);
    void visit (CardRoundDTO state);
    void visit (EndGameDTO state);
}
