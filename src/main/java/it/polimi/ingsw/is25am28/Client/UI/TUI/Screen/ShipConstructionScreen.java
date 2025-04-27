package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;

public class ShipConstructionScreen extends Screen {
    // TODO: added needed data

    public ShipConstructionScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws InterruptedException {
        System.out.println("Showing ship construction state --> TEST NEEDS TUI FIXES");
        System.out.println("WILL WAIT ON ENTER CMD");

        String result = inputThread.waitForInput();
        if (result == null) {
            System.out.println("FORCE QUIT FOUND");
            return;
        } else {
            System.out.println("NORMAL FLOW " + result);
        }
    }
}
