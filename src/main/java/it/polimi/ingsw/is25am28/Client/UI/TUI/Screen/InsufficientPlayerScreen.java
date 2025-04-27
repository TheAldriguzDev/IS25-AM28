package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;

public class InsufficientPlayerScreen extends Screen {
    public InsufficientPlayerScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        System.out.println("Insufficient player was forced");
    }
}
