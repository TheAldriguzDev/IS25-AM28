package it.polimi.ingsw.is25am28.Controller;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ReconnectDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionDeckDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlacedComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.GameModelv2.*;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.VirtualView;

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
    public List<StateDTO> disconnectClient(String nickname) {
        synchronized (this.model) {
            return this.model.disconnectClient(nickname);
        }
    }

    public List<StateDTO> reconnectClient(String nickname, VirtualView clientView) throws Exception {
        synchronized (this.model) {
            return this.model.reconnectClient(nickname, clientView);
        }
    }

    public StateDTO gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, VirtualView clientView) throws IllegalStateException, IllegalArgumentException {
        synchronized (this.model) {
            return this.model.gameConfig(nickname, playerColor, level, numPlayers, clientView);
        }
    }

    public List<StateDTO> addNewPlayer(String nickname, PlayerColor playerColor, VirtualView clientView) throws IllegalStateException, IllegalArgumentException {
        synchronized (this.model) {
            return this.model.addNewPlayer(nickname, playerColor, clientView);
        }
    }

    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        synchronized (this.model) {
            return this.model.selectDeselectSubdeck(player, selectedDeck, isSelectAction);
        }
    }

    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.selectTile(player, id);
        }
    }

    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.deselectTile(player, id);
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

    public TimerDTO flipTimer(String player) throws IllegalStateException {
        synchronized (this.model) {
            return this.model.flipTimer(player);
        }
    }

    public List<StateDTO> fixShip(String player, Integer i, Integer j) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.fixShip(player, i, j);
        }
    }

    public List<StateDTO> populateShip(String player, ComponentHelper<LifeformType> lifeFormToAdd) throws IllegalArgumentException {
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