package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;

import java.util.List;

public final class ClientBattery extends ClientComponent {
    private final int maxAvailability;
    private int available;

    public ClientBattery(int id, List<Integer> sides, int maxAvailability) {
        super(id, sides);
        this.maxAvailability = maxAvailability;
        this.available = maxAvailability;
    }
}
