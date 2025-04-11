package it.polimi.ingsw.is25am28.GameModelv2;

import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.Exceptions.OutOfShipException;
import it.polimi.ingsw.is25am28.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Exceptions.TooManyAliensException;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;

import java.util.ArrayList;
import java.util.List;

public final class PopulateShipState extends State {
    private List<String> playersReady;

    public PopulateShipState(GameModel model) {
        super(model);
        playersReady = new ArrayList<>();
    }

    public PopulateShipDTO populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws IllegalArgumentException {
        if (playersReady.contains(player)) {
            throw new IllegalArgumentException("The given player has already populated the ship");
        }

        Player p = this.model.getPlayers().get(player);
        if (p == null) {
            throw new IllegalArgumentException("The given player does not exist");
        }

        Ship pShip = p.getShip();

        // Add the lifeform to the cabin
        for (ComponentHelper<LifeformType> c : lifeFormToAdd) {
            if (c.getItem().isPresent()) {
                try {
                    pShip.addLifeformToCabin(c.getI(), c.getJ(), c.getItem().get());
                } catch(TooManyAliensException | OutOfGridException | IllegalArgumentException | OutOfShipException e){
                    throw new ShipPopulationFailException(player);
                }
            }
        }

        this.playersReady.add(player);

        return new PopulateShipDTO().setPlayersReady(playersReady);
    }

    @Override
    public void onComplete() {
        // If all the players have populated their ship the new state will be CardRoundSession
        if (this.playersReady.size() == this.model.getNumPlayers()) {
            this.model.setCurrentState(new CardRoundState(model));
        }
    }

    @Override
    public StateJSON generateState() {
        PopulateShipDTO state = new PopulateShipDTO()
                .setPlayersReady(playersReady);

        state.setStateName(this.toString());

        return state;
    }
}
