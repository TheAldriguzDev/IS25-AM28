package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;

import java.util.List;

public final class ClientCannon extends ClientComponent {
    private final int force;

    public ClientCannon(int id, List<Integer> sides, int force) {
        super(id, sides);
        this.force = force;
    }
}
