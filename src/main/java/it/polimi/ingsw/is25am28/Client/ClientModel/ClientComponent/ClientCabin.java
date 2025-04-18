package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;

import java.util.ArrayList;
import java.util.List;

public final class ClientCabin extends ClientComponent {
    private final boolean isCore;
    private final ArrayList<Lifeform> inhabitants;

    public ClientCabin(int id, List<Integer> sides, boolean isCore) {
        super(id, sides);
        this.isCore = isCore;
        this.inhabitants = new ArrayList<>();
    }
}
