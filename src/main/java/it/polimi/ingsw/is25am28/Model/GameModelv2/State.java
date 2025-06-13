package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FastShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;

public abstract sealed class State permits CardRoundState, CreateGameState, EndGameState, FixShipState, InsufficientPlayerState, PopulateShipState, ShipContructionState, WaitPlayersState {
    protected GameModel model;

    public State(GameModel model) {
        this.model = model;
    }

    public void gameConfig(String playerNickname, PlayerColor playerColor, int level, int numPlayers) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'gameConfig' command is not allowed in the " + this + " state");
    }

    public void addNewPlayer(String playerNickname, PlayerColor playerColor) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'addNewPlayer' command is not allowed in the " + this + " state");
    }

    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        throw new IllegalStateException("The 'selectDeselectSubdeck' command is not allowed in the " + this + " state");
    }

    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'selectTile' command is not allowed in the " + this + " state");
    }

    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'deselectTile' command is not allowed in the " + this + " state");
    }

    public ReservedComponentDTO reserveTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'reserveTile' command is not allowed in the " + this + " state");
    }

    public FastShipDTO fastShip(String playerNickname) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'reserveTile' command is not allowed in the " + this + " state");
    };

    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        throw new IllegalStateException("The 'placeTile' command is not allowed in the " + this + " state");
    }

    public PlayerEndedShipDTO playerEndedSendShip(String player, int reservedTiles) throws IllegalStateException {
        throw new IllegalStateException("The 'playerEndedSendShip' command is not allowed in the " + this + " state");
    }

    public TimerDTO flipTimer(String player) throws IllegalStateException {
        throw new IllegalStateException("The 'flipTimer' command is not allowed in the " + this + " state");
    }

    public FixedComponentDTO fixShip(String player, Integer i, Integer j) throws IllegalArgumentException {
        throw new IllegalStateException("The 'fixShip' command is not allowed in the " + this + " state");
    }

    public PopulateShipComponentDTO populateShip(String player, ComponentHelper<LifeformType> lifeformToAdd) throws IllegalArgumentException {
        throw new IllegalStateException("The 'populateShip' command is not allowed in the " + this + " state");
    }

    public List<CardRoundDTO> playCard(ActionJSON action) throws IllegalArgumentException {
        throw new IllegalStateException("The 'playCard' command is not allowed in the " + this + " state");
    }

    // This method is used exclusively in the ShipConstructionState to handle disconnection to free the locked resources by a player
    // If a player has selected a subdeck, it must be deselected to allow other players to use it.
    public void handlePlayerDisconnection(String player) {
        // TODO
    }

    public abstract void onComplete();

    // State generation
    public StateDTO generateState() {
        StateDTO state = new StateDTO();

        state.setStateName(this.toString());

        return state;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
