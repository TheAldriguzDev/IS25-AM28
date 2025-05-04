package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.FixedComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionType;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import java.util.List;

public final class FixShipState extends State {
    private final List<String> playersWithInvalidShip;

    public FixShipState(GameModel model, List<String> playersWithInvalidShip) {
        super(model);
        this.playersWithInvalidShip = playersWithInvalidShip;
    }

    @Override
    public FixedComponentDTO fixShip(String player, Integer i, Integer j) throws IllegalArgumentException, FixNotRequiredError {
        if (!this.playersWithInvalidShip.contains(player)) {
            throw new FixNotRequiredError(player);
        }

        Player p = this.model.getPlayers().get(player);
        Ship pShip = p.getShip();

        pShip.removeSingleComponent(i, j);
        p.addLostPieces(1);

        // Updating all components sublists
        pShip.generateComponentSubLists();

        FixedComponentDTO state = new FixedComponentDTO()
                .setPlayerNickname(player)
                .setI(i)
                .setJ(j);

        // Check if the player ship is not valid
        if (pShip.validateShip()) {
            this.playersWithInvalidShip.remove(player);
            state.setShipFixed(true);
        } else {
            state.setShipFixed(false);
        }

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TILE_EVENT.toString());
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
    public StateDTO generateState() {
        FixShipDTO state = new FixShipDTO()
                .setPlayerWithInvalidShip(playersWithInvalidShip);

        state.setStateName(this.toString());

        return state;
    }
}
