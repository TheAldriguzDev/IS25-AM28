package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.is25am28.Network.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * SocketClientHandler is needed to handle correctly each client socket to execute the command with the Controller
 * */

public class SocketClientHandler implements VirtualViewSocket {
    private final Server gameController;
    private final TCPServer tcpServer;

    // Channel used to read from the socket
    private final BufferedReader input;
    // Channel used to write in the socket
    private final PrintWriter output;

    // Mapper used to serialize and deserialize JSON used in the communication
    private final ObjectMapper mapper;

    public SocketClientHandler(Server gameController, TCPServer tcpServer, BufferedReader input, PrintWriter output) throws JsonProcessingException {
        this.gameController = gameController;
        this.tcpServer = tcpServer;
        this.input = input;
        this.output = output;

        this.mapper = new ObjectMapper();

        this.onClientConnection();
    }

    private void onClientConnection() throws JsonProcessingException {
        StateDTO state = this.gameController.onClientConnection();

        this.updateState(state);
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
                    this.gameConfig(data.getPlayerNickname(), data.getPlayerColor(), data.getGameLevel(), data.getTotalPlayers());
                }
                case NewPlayer data -> {
                    this.newPlayer(data.getPlayerNickname(), data.getPlayerColor());
                }
                default -> {
                    throw new Exception("The given Message is not supported");
                }
            }
        }
    }

    /**
     * Method used to configure the game, if an error is caught we notify the specific client, otherwise the new state will be
     * sent to each client
     * */
    private void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers) throws Exception {
        try {
            this.gameController.configGame(nickname, playerColor, level, numPlayers);
        } catch (Exception e) {
            // TODO: Handle exeception
            throw new Exception("[gameConfig] An error occurred: " + e.getMessage());
        }
    }

    private void newPlayer(String playerNickname, PlayerColor playerColor) throws Exception {
//        List<StateDTO> states = new ArrayList<>();
//
//        try {
//            states = this.gameController.addNewPlayer(playerNickname, playerColor);
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            this.reportError("Some details", this.gameController.getCurrentState());
//            return;
//        }
//
//        if (!states.isEmpty()) {
//            this.tcpServer.broadCastUpdateState(states.getFirst());
//        }
//
//        if (states.size() == 2) {
//            this.tcpServer.broadCastUpdateState(states.getLast());
//        }
    }

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