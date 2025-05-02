package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;

public class PopulateShipScreen extends Screen {

    public PopulateShipScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {
        System.out.println("Populate screen");
    }

}
