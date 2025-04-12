package it.polimi.ingsw.is25am28.Controller;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.GameModelv2.GameModel;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;

public class Controller {
    private final GameModel model;


    public Controller() {
        this.model = new GameModel();
    }

    // TODO: Method that accepts clients connection. In this state we need to generate the state and send it back to the client. If the gameIsReady for newPlayer
    //  we need to notify the new clients with the current model state, otherwise we need to send a temp state that indicates that the game is still in configuration
    public StateJSON onClientConnection() {


    }

    public StateJSON gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers) {
        synchronized (this.model) {
            return this.model.gameConfig(nickname, playerColor, level, numPlayers);
        }
    }

    public List<StateJSON> addNewPlayer(String nickname, PlayerColor playerColor) {
        synchronized (this.model) {
            return this.model.addNewPlayer(nickname, playerColor);
        }
    }

    public ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws SelectedConcurrencyException {
        synchronized (this.model) {
            return this.model.selectTile(player, i, j);
        }
    }

    public ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws SelectedConcurrencyException {
        synchronized (this.model) {
            return this.model.deselectTile(player, i, j);
        }
    }

    public List<StateJSON> playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles) {
        synchronized (this.model) {
            return this.model.playerEndedSendShip(player, playerShip, reservedTiles);
        }
    }

    public TimerDTO flipTimer(String player) {
        synchronized (this.model) {
            return this.model.flipTimer(player);
        }
    }

    public List<StateJSON> fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws IllegalArgumentException, FixNotRequiredError {
        synchronized (this.model) {
            return this.model.fixShip(player, componentsToRemove);
        }
    }

    public List<StateJSON> populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.populateShip(player, lifeFormToAdd);
        }
    }

    public List<StateJSON> playCard(ActionJSON action) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.playCard(action);
        }
    }
}