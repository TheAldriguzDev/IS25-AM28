package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SocketClientHandler is needed to handle correctly each client socket to execute the command with the Controller
 * */

public class SocketClientHandler implements VirtualViewSocket {
    private final GameController gameController;
    private final TCPServer tcpServer;

    // Channel used to read from the socket
    private final BufferedReader input;
    // Channel used to write in the socket
    private final PrintWriter output;

    // Mapper used to serialize and deserialize JSON used in the communication
    private final ObjectMapper mapper;

    public SocketClientHandler(GameController gameController, TCPServer tcpServer, BufferedReader input, PrintWriter output) {
        this.gameController = gameController;
        this.tcpServer = tcpServer;
        this.input = input;
        this.output = output;

        this.mapper = new ObjectMapper();
    }

    /**
     * This method will listen for Serialized Messages from the client
     *
     * We need to implement the deserialization protocol
     * */
    public void run() throws IOException {
        String line;

        while ((line = input.readLine()) != null) {

            // Reflection
            // Deserialization protocol
        }
    }

    @Override
    public void configureGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {

    }

    @Override
    public void newPlayer(String playerNickname, PlayerColor playerColor) throws Exception {

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
