package it.polimi.ingsw.is25am28.Controller;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ReconnectDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionDeckDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlacedComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitingForGameConfigurationDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Model.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.Model.GameModelv2.*;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final GameModel model;

    public GameController() {
        this.model = new GameModel();
    }

    /**
     * @return the current gameModel state
     * */
    public StateDTO getCurrentState() {
        synchronized (this.model) {
            return this.model.getCurrentState().generateState();
        }
    }

    /**
     * @return the available colors in the game
     * */
    public List<String> getAvailableColors() {
        synchronized (this.model) {
            return this.model.getAvailableColors();
        }
    }

    /**
     * @return the nickname list of the disconnected players
     * */
    public List<String> getDisconnectedPlayers() {
        synchronized (this.model) {
            return this.model.getDisconnectedPlayers();
        }
    }

    /**
     * Marks the given player as not connected
     * */
    public void disconnectClient(String nickname) {
        synchronized (this.model) {
            this.model.disconnectClient(nickname);
        }
    }

    // TODO: THIS METHOD SHOULD RETURN THE STATE THAT THE CLIENT NEEDS TO CONFIGURE THE GAME SINCE HE LEFT IT
    public ReconnectDTO reconnectClient(String nickname) throws Exception {
        synchronized (this.model) {
            return this.model.reconnectClient(nickname);
        }
    }

    public StateDTO gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers) throws IllegalStateException, IllegalArgumentException {
        synchronized (this.model) {
            return this.model.gameConfig(nickname, playerColor, level, numPlayers);
        }
    }

    public List<StateDTO> addNewPlayer(String nickname, PlayerColor playerColor) throws IllegalStateException, IllegalArgumentException {
        synchronized (this.model) {
            return this.model.addNewPlayer(nickname, playerColor);
        }
    }

    public ConstructionDeckDTO selectSubDeck(String player, Integer selectedDeck) throws IllegalStateException {
        synchronized (this.model) {
            return this.model.selectSubDeck(player, selectedDeck);
        }
    }

    public ConstructionDeckDTO deselectSubDeck(String player, Integer selectedDeck) throws IllegalStateException {
        synchronized (this.model) {
            return this.model.deselectSubDeck(player, selectedDeck);
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

    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        synchronized (this.model) {
            return this.model.placeTile(player, componentID, i, j, rotation);
        }
    }

    public List<StateDTO> playerEndedSendShip(String player, int reservedTiles) {
        synchronized (this.model) {
            return this.model.playerEndedSendShip(player, reservedTiles);
        }
    }

    public TimerDTO flipTimer(String player) throws TimerFlipException {
        synchronized (this.model) {
            return this.model.flipTimer(player);
        }
    }

    public List<StateDTO> fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws IllegalArgumentException, FixNotRequiredError {
        synchronized (this.model) {
            return this.model.fixShip(player, componentsToRemove);
        }
    }

    public List<StateDTO> populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws IllegalArgumentException, ShipPopulationFailException {
        synchronized (this.model) {
            return this.model.populateShip(player, lifeFormToAdd);
        }
    }

    public List<StateDTO> playCard(ActionJSON action) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.playCard(action);
        }
    }
}