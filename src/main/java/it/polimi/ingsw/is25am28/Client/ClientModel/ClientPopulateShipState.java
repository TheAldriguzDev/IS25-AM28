package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PopulateShipComponentDTO;

public class ClientPopulateShipState extends ClientState {

    public ClientPopulateShipState(ClientModel model, PopulateShipDTO populateShipDTO) {
        super(model);
    }
}
