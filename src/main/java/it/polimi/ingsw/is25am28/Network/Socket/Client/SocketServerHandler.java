package it.polimi.ingsw.is25am28.Network.Socket.Client;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import it.polimi.ingsw.is25am28.Network.Messages.Message;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public class SocketServerHandler implements VirtualServerSocket {
    private final ObjectMapper mapper;
    private final PrintWriter output;

    /**
     * Creates a new instance of SocketServerHandler with the specified output stream.
     * The handler is responsible for serializing messages into JSON format and sending
     * them through the provided output stream.
     *
     * @param output the {@code BufferedWriter} used to send serialized data to the server
     */
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
