package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;

import java.util.List;

public final class ClientEngine extends ClientComponent {
    private final int power;

    public ClientEngine(int id, List<Integer> sides, int power) {
        super(id, sides);
        this.power = power;
    }
}
