package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.Server.Server;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
                default -> {
                    throw new Exception("The given Message is not supported");
                }
            }
        }
    }

    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {
        this.playerNickname = playerNickname;

        try {
            this.controller.createNewGame(playerNickname, playerColor, gameLevel, totalPlayers, this);
        } catch (Exception e) {
            // TODO: FIX THE reportError method
            this.reportError(e.getMessage(), null);
        }
    }

    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID) throws Exception {
        this.playerNickname = playerNickname;

        try {
            this.controller.joinGame(playerNickname, playerColor, gameID, this);
        } catch (Exception e) {
            // TODO: FIX THE reportError method
            this.reportError(e.getMessage(), null);
        }
    }

    // TODO: WE NEED TO IMPLEMENT THE CONTROLLER INTERFACE TO EXPOSE ALL THE METHODS

    // TODO: Capisci come togliere sto metodo da qui
    @Override
    public void sendMessage(Message message) throws Exception {

    }

    @Override
    public void updateView(StateDTO state) throws JsonProcessingException {
        String stateString = this.mapper.writeValueAsString(state);;

        this.output.println(stateString);
        this.output.flush();
    }

    @Override
    public void updateState(StateDTO state) throws JsonProcessingException {
        String stateString = this.mapper.writeValueAsString(state);;

        this.output.println(stateString);
        this.output.flush();
    }

    // TODO: Rework this method, should be better to have a DTO to report the error and if needed that it also
    //  includes the StateDTO in it
    @Override
    public void reportError(String details, StateDTO state) throws JsonProcessingException {
        String stateString = this.mapper.writeValueAsString(state);;

        this.output.println(details);
        this.output.println(stateString);
        this.output.flush();
    }
}