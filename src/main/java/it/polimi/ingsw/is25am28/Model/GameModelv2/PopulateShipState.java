package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfShipException;
import it.polimi.ingsw.is25am28.Model.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Model.Exceptions.TooManyAliensException;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

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
    public StateDTO generateState() {
        PopulateShipDTO state = new PopulateShipDTO()
                .setPlayersReady(playersReady);

        state.setStateName(this.toString());

        return state;
    }
}
