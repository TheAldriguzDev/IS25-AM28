package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.Model.Items.Item;

import java.util.ArrayList;
import java.util.List;

public final class ClientStorage extends ClientComponent {
    private final int capacity;
    private final boolean isSpecialStorage;
    private final List<Item> storedItems;

    public ClientStorage(int id, List<Integer> sides, int capacity, boolean isSpecialStorage) {
        super(id, sides);
        this.capacity = capacity;
        this.isSpecialStorage = isSpecialStorage;
        this.storedItems = new ArrayList<>();
    }
}
