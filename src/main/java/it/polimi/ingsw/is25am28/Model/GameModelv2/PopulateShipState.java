package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PopulateShipComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionType;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.*;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import java.util.ArrayList;
import java.util.List;

public final class PopulateShipState extends State {
    private final List<String> playersReady;

    public PopulateShipState(GameModel model) {
        super(model);
        this.playersReady = new ArrayList<>();

        for (Player p : model.getPlayers().values()) {
            p.getShip().generateComponentSubLists();

            if (p.getShip().isShipPopulated()) {
                this.playersReady.add(p.getNickname());
            }
        }
    }

    public PopulateShipComponentDTO populateShip(String player, ComponentHelper<LifeformType> lifeformToAdd) throws IllegalArgumentException, ShipPopulationFailException {
        if (this.playersReady.contains(player)) {
            throw new IllegalArgumentException("The given player has already populated the ship");
        }

        Player p = this.model.getPlayers().get(player);
        if (p == null) {
            throw new IllegalArgumentException("The given player does not exist");
        }

        Ship pShip = p.getShip();

        // Add the lifeform to the cabin
        if (lifeformToAdd.getItem().isPresent()) {
            try {
                pShip.addLifeformToCabin(lifeformToAdd.getI(), lifeformToAdd.getJ(), lifeformToAdd.getItem().get());
            }
            catch (TooManyAliensException | OutOfGridException | IllegalArgumentException | OutOfShipException |
                   NoSupportVitalFoundException e){
                throw new ShipPopulationFailException(player);
            }
        }

        PopulateShipComponentDTO state = new PopulateShipComponentDTO()
                .setPlayerNickname(player)
                .setComponent(lifeformToAdd);

        if (pShip.isShipPopulated()) {
            this.playersReady.add(player);
            state.setIsShipPopulated(true);
        } else {
            state.setIsShipPopulated(false);
        }

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.POPULATE_EVENT.toString());
        return state;
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
                .setPlayersReady(this.playersReady);

        state.setStateName(this.toString());

        return state;
    }
}
