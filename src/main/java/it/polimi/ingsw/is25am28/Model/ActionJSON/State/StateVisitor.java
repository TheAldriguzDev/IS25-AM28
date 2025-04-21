package it.polimi.ingsw.is25am28.Model.ActionJSON.State;


import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlayerEndedShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;

import java.io.IOException;

public interface StateVisitor {
    void visit (StateDTO state);
    void visit (AvailableGamesDTO state) throws Exception;
    void visit (CreateGameStateDTO state) throws Exception;
    void visit (WaitingForGameConfigurationDTO state);
    void visit (WaitPlayersStateDTO state) throws Exception;
    void visit (ReconnectDTO state) throws Exception;
    void visit (ShipConstructionDTO state) throws Exception;
    void visit (ConstructionComponentDTO state) throws Exception;
    void visit (PlayerEndedShipDTO state) throws Exception;
    void visit (FixShipDTO state);
    void visit (PopulateShipDTO state);
    void visit (CardRoundDTO state);
    void visit (EndGameDTO state);
}
