package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.Network.Server.Server;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.UUID;

/**
 * SocketClientHandler is needed to handle correctly each client socket to execute the command with the Controller
 * */

public class SocketClientHandler implements VirtualViewSocket {
    private final Server controller;
    private final TCPServer tcpServer;

    // Channel used to read from the socket
    private final BufferedReader input;
    // Channel used to write in the socket
    private final PrintWriter output;

    // Mapper used to serialize and deserialize JSON used in the communication
    private final ObjectMapper mapper;

    private String playerNickname;

    public SocketClientHandler(Server gameController, TCPServer tcpServer, BufferedReader input, PrintWriter output) throws JsonProcessingException {
        this.controller = gameController;
        this.tcpServer = tcpServer;
        this.input = input;
        this.output = output;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new Jdk8Module());
        this.mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        try {
            this.onClientConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onClientConnection() throws Exception {
        this.controller.onClientConnection(this);
    }

    /**
     * This method will listen for Serialized Messages from the client
     *
     * We need to implement the deserialization protocol
     * */
    public void run() throws IOException, Exception {
        String line;

        while ((line = input.readLine()) != null) {
            Message message = mapper.readValue(line, Message.class);
            switch (message) {
                case ConfigGame data -> {
                    this.createNewGame(data.getPlayerNickname(), data.getPlayerColor(), data.getGameLevel(), data.getTotalPlayers());
                }
                case NewPlayer data -> {
                    this.joinGame(data.getPlayerNickname(), data.getPlayerColor(), data.getGameID());
                }
                case SelectTile data -> {
                    this.selectTile(data.getPlayerNickname(), data.getId());
                }
                case DeselectTile data -> {
                    this.deselectTile(data.getPlayerNickname(), data.getId());
                }
                case Ping ignored -> {
                    this.ping();
                }
                case Reconnect data -> {
                    this.reconnectClient(data.getNickname());
                }
                case RefreshGames ignored -> {
                    this.refreshGames();
                }
                case PlaceTile data -> {
                    this.placeTile(data.getNickname(), data.getComponentID(), data.getI(), data.getJ(), data.getRotation());
                }
                case SendShipConfirmation data -> {
                    this.sendShipConfirmation(data.getPlayerNickname(), data.getReservedTiles());
                }
                case FlipTimer data -> {
                    this.flipTimer(data.getPlayerNickname());
                }
                case SelectDeselectSubdeck data -> {
                    this.selectDeselectSubdeck(data.getPlayerNickname(), data.getSubdeck(), data.isSelectAction());
                }
                case FixShip data -> {
                    this.fixShip(data.getPlayerNickname(), data.getI(), data.getJ());
                }
                case PopulateShip data -> {
                    this.populateShip(data.getPlayerNickname(), data.getLifeformToAdd());
                }
                case PlayCard data -> {
                    this.playCard(data.getPlayerNickname(), data.getActionJSON());
                }
                default -> {
                    throw new Exception("The given Message is not supported");
                }
            }
        }
    }

    @Override
    public void refreshGames() throws Exception {
        try {
            this.controller.onClientConnection(this);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {
        this.playerNickname = playerNickname;

        try {
            this.controller.createNewGame(playerNickname, playerColor, gameLevel, totalPlayers, this);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID) throws Exception {
        this.playerNickname = playerNickname;

        try {
            this.controller.joinGame(playerNickname, playerColor, gameID, this);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void selectTile(String playerNickname, int id) throws Exception {
        try {
            this.controller.selectTile(playerNickname, id);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void deselectTile(String playerNickname, int id) throws Exception {
        try {
            this.controller.deselectTile(playerNickname, id);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        try {
            this.controller.placeTile(playerNickname, componentID, i, j, rotation);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void sendShipConfirmation(String playerNickname, int reservedTiles) throws Exception {
        try {
            this.controller.playerEndedSendShip(playerNickname, reservedTiles);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void flipTimer(String playerNickname) throws Exception {
        try {
            this.controller.flipTimer(playerNickname);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction) throws Exception {
        try {
            this.controller.selectDeselectSubdeck(playerNickname, subdeck, isSelectAction);
        }
        catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void fixShip(String playerNickname, Integer i, Integer j) throws Exception {
        try {
            this.controller.fixShip(playerNickname, i, j);
        }
        catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception {
        try {
            this.controller.populateShip(playerNickname, lifeFormToAdd);
        }
        catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void playCard(String playerNickname, ActionJSON action) throws Exception {
        try {
            this.controller.playCard(playerNickname, action);
        }
        catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    // ===== PING UTILITY METHODS ===== //
    public void ping() throws Exception {
        this.controller.clientPing(this);
    }

    @Override
    public void reconnectClient(String nickname) throws Exception {
        try {
            this.controller.reconnectClient(nickname, this);
        } catch (Exception e) {
            this.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void updateState(Answer answer) throws JsonProcessingException {
        String stateString = this.mapper.writeValueAsString(answer);

        this.output.println(stateString);
        this.output.flush();
    }

    @Override
    public void reportError(ErrorAnswer error) throws JsonProcessingException {
        String stateString = this.mapper.writeValueAsString(error);

        this.output.println(stateString);
        this.output.flush();
    }
}