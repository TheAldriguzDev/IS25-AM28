package it.polimi.ingsw.is25am28.Controller;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitingForGameConfigurationDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.GameModelv2.CreateGameState;
import it.polimi.ingsw.is25am28.Model.GameModelv2.GameModel;
import it.polimi.ingsw.is25am28.Model.GameModelv2.WaitPlayersState;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;

public class GameController {
    private final GameModel model;

    // Number of connected clients, will be used to understand if a new connection is from a leader or a command player
    // and to prevent connections when a game is already full
    private int connectedClients;

    public GameController() {
        this.model = new GameModel();
        this.connectedClients = 0;
    }

    public StateDTO getCurrentState() {
        synchronized (this.model) {
            return this.model.getCurrentState().generateState();
        }
    }

    // TODO: Method that accepts clients connection. In this state we need to generate the state and send it back to the client. If the gameIsReady for newPlayer
    //  we need to notify the new clients with the current model state, otherwise we need to send a temp state that indicates that the game is still in configuration
    public StateDTO onClientConnection() {
        StateDTO state;

        this.connectedClients++;

        if (this.connectedClients > 4) {
            throw new IllegalStateException("The lobby cannot accept more players");
        }

        // When the leader tries to connect, we need to send the state that identifies the game configuration state
        if (this.connectedClients == 1) {
            return this.model.generateState(); // The state will be CreateGameStateDTO
        }

        if (this.connectedClients > 1) {
            switch (this.model.getCurrentState()) {
                // If the game is in configState
                case CreateGameState ignored -> {
                    state = new WaitingForGameConfigurationDTO();
                    state.setStateName("WaitingForGameConfiguration"); // The state will be WaitingForGameConfigurationDTO

                    return state;
                }
                // If the game is waiting for player to join the lobby
                case WaitPlayersState currState -> {
                    return currState.generateState(); // The state will be WaitingForPlayersDTO
                }
                default -> throw new IllegalStateException("Unexpected value: " + this.model.getCurrentState());
            }
        }

        throw new IllegalStateException("Unexpected connection in invalid state: " + this.model.getCurrentState());
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

    public List<StateDTO> playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles) {
        synchronized (this.model) {
            return this.model.playerEndedSendShip(player, playerShip, reservedTiles);
        }
    }

    public TimerDTO flipTimer(String player) {
        synchronized (this.model) {
            return this.model.flipTimer(player);
        }
    }

    public List<StateDTO> fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws IllegalArgumentException, FixNotRequiredError {
        synchronized (this.model) {
            return this.model.fixShip(player, componentsToRemove);
        }
    }

    public List<StateDTO> populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws IllegalArgumentException {
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