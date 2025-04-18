package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;

import java.util.List;

public final class ClientShield extends ClientComponent {
    public ClientShield(int id, List<Integer> sides) {
        super(id, sides);
    }

    /**
     * return the two sides that are covered by the
     * shield. they are returned with the usual standard,
     * 0: top
     * 1: right
     * 2: bottom
     * 3: left
     */
    public int[] getCoveredSide() {
        int[] covered = new int[2];

        for (int i = 0; i < 2; i++) {
            covered[i] = (direction + i) % 4;
        }

        return covered;
    }
}
