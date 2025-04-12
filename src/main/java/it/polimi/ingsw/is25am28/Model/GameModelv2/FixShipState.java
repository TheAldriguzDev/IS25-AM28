package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import java.util.List;

public final class FixShipState extends State {
    private List<String> playersWithInvalidShip;

    public FixShipState(GameModel model, List<String> playersWithInvalidShip) {
        super(model);
        this.playersWithInvalidShip = playersWithInvalidShip;
    }

    @Override
    public FixShipDTO fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws IllegalArgumentException, FixNotRequiredError {
        if (!playersWithInvalidShip.contains(player)) {
            throw new FixNotRequiredError(player);
        }

        Player p = model.getPlayers().get(player);
        Ship pShip = p.getShip();

        for (ComponentHelper<Integer> c : componentsToRemove) {
            pShip.removeSingleComponent(c.getI(), c.getJ());
        }

        p.addLostPieces(componentsToRemove.size());

        if (pShip.validateShip()) {
            playersWithInvalidShip.remove(player);
        }

        FixShipDTO state = new FixShipDTO()
                .setPlayerWithInvalidShip(this.playersWithInvalidShip);

        state.setStateName(this.toString());
        return state;
    }

    @Override
    public void onComplete() {
        // If all the players have fixed their ship we can go to the PopulateShipState
        if (playersWithInvalidShip.isEmpty()) {
            this.model.setCurrentState(new PopulateShipState(model));
        }
    }

    @Override
    public StateJSON generateState() {
        FixShipDTO state = new FixShipDTO()
                .setPlayerWithInvalidShip(playersWithInvalidShip);

        state.setStateName(this.toString());

        return state;
    }
}
