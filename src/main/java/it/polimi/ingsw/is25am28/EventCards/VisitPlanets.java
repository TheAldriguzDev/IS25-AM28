package it.polimi.ingsw.is25am28.EventCards;


import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Player;

import java.util.ArrayList;

public class VisitPlanets extends EventCard {
    private ArrayList<Item> planetItems = new ArrayList<Item>();
    private final int movementStep;
    //private final int givenCredits; ???
    public VisitPlanets(String name, int cardLevel, int movementStep) {
        super(name, cardLevel);
        this.movementStep = movementStep;
    }
    public void useCard(Player[] players) {

    }

    @Override
    void bonusEffect(Player player) {

    }

    @Override
    void malusEffect(Player player) {

    }
}
