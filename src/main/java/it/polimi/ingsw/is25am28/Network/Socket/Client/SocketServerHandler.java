package it.polimi.ingsw.is25am28.Network.Socket.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
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
    private final ObjectMapper mapper;

    private final PrintWriter output;

    public SocketServerHandler(BufferedWriter output) {
        this.output = new PrintWriter(output);

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new Jdk8Module());
        this.mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    }

    @Override
    public void sendMessage(Message message) throws Exception {
        String json = mapper.writeValueAsString(message);
        synchronized (this.output) {
            output.println(json);
            output.flush();
        }
    }
}
