package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShipConstructionScreen extends Screen {
    // TODO: added needed data
    private Map<String, Runnable> cmds;

    public ShipConstructionScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
        this.cmds = new HashMap<>();

        cmds.put("Select tile", () -> {
            try {
                this.selectTile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws InterruptedException {
        System.out.println("Showing ship construction state --> TEST NEEDS TUI FIXES");

        for (String cmd : cmds.keySet()) {
            System.out.println(cmd);
        }

        String result;
        do {
            result = inputThread.waitForInput();
            if (result == null) {
                return;
            }
        } while (cmds.containsKey(result));

        this.cmds.get(result).run();
    }

    private void selectTile() throws InterruptedException {
        System.out.println("Selected tile cmd");
        String result;
        do {
            result = inputThread.waitForInput();
            if (result == null) {
                return;
            }
        } while (cmds.containsKey(result));

    }

    /**
     * @return the options available when the player can select a tile in the shipConstructionState
     * */
    private static List<String> getShipConstructionBaseOptions(List<ClientComponent> reservedComponents) {
        List<String> options = new ArrayList<>();

        // If present, add the available games
        for (ClientComponent comp : reservedComponents) {
            options.add(
                    "Select reserved tile - " + comp.getClass().getSimpleName()
            );
        }

        // Extra options
        options.add("Select a new tile");
        options.add("Show deck");
        return options;
    }

}

/*
   | 1) XXXX
*  | 2) nkdsldksl
*  |
*  |
* */
