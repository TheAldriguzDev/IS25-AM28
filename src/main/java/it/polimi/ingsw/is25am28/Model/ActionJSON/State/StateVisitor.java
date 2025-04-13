package it.polimi.ingsw.is25am28.Model.ActionJSON.State;


import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;

public interface StateVisitor {
    void visit (StateDTO state);
    void visit (CreateGameStateDTO state) throws Exception;
    void visit (WaitingForGameConfigurationDTO state);
    void visit (WaitPlayersStateDTO state) throws Exception;
    void visit (ShipConstructionDTO state);
    void visit (FixShipDTO state);
    void visit (PopulateShipDTO state);
    void visit (CardRoundDTO state);
    void visit (EndGameDTO state);
}
