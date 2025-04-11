package it.polimi.ingsw.is25am28.GameModelv2;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction.PlayerEndedShipDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.PlayerColor;

import java.util.List;

public abstract sealed class State permits CardRoundState, CreateGameState, EndGameState, FixShipState, PopulateShipState, ShipContructionState, WaitPlayersState {
    protected GameModel model;

    public State(GameModel model) {
        this.model = model;
    }

    public void gameConfig(String playerNickname, PlayerColor playerColor, int level, int numPlayers) throws IllegalStateException {
        throw new IllegalStateException("The 'gameConfig' command is not allowed in the " + this + " state");
    }

    public void addNewPlayer(String playerNickname, PlayerColor playerColor) throws IllegalStateException {
        throw new IllegalStateException("The 'addNewPlayer' command is not allowed in the " + this + " state");
    }

    public synchronized ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        throw new IllegalStateException("The 'selectTile' command is not allowed in the " + this + " state");
    }

    public synchronized ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        throw new IllegalStateException("The 'deselectTile' command is not allowed in the " + this + " state");
    }

    public synchronized PlayerEndedShipDTO playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles) throws IllegalStateException {
        throw new IllegalStateException("The 'playerEndedSendShip' command is not allowed in the " + this + " state");
    }

    public synchronized TimerDTO flipTimer(String player) throws IllegalStateException, TimerFlipException {
        throw new IllegalStateException("The 'flipTimer' command is not allowed in the " + this + " state");
    }

    public FixShipDTO fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws IllegalArgumentException, FixNotRequiredError {
        throw new IllegalStateException("The 'fixShip' command is not allowed in the " + this + " state");
    }

    public PopulateShipDTO populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws IllegalArgumentException {
        throw new IllegalStateException("The 'populateShip' command is not allowed in the " + this + " state");
    }

    public CardRoundDTO playCard(ActionJSON action) throws IllegalArgumentException {
        throw new IllegalStateException("The 'playCard' command is not allowed in the " + this + " state");
    }

    public abstract void onComplete();

    // State generation
    public StateJSON generateState() {
        StateJSON state = new StateJSON();

        state.setStateName(this.toString());

        return state;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
