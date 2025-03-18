package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Components.Cabin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;




class SlaversTest {

    @Test
    public void removeCrewTest(){

        int[] connectors = {0,0,0,0};

        Cabin cabin_1 = new Cabin(connectors, false);
        Cabin cabin_2 = new Cabin(connectors, false);
        Cabin cabin_3 = new Cabin( connectors, false);
        Cabin cabin_4 = new Cabin( connectors, true);

        System.out.println("Halo");
    }
}