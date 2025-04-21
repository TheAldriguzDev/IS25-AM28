package it.polimi.ingsw.is25am28.Network.Socket.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.Message;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

public class SocketServerHandler implements VirtualServerSocket {
    private final ObjectMapper mapper = new ObjectMapper();

    private final PrintWriter output;

    public SocketServerHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    // TODO: Complete those messages

    @Override
    public void sendMessage(Message message) throws Exception {
        String json = mapper.writeValueAsString(message);
        synchronized (this.output) {
            output.println(json);
            output.flush();
        }
    }
}
