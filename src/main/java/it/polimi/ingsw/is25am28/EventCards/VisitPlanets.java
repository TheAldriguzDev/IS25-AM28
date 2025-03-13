package it.polimi.ingsw.is25am28.EventCards;


import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Player;

import java.util.ArrayList;

public class VisitPlanets extends EventCard {
    private ArrayList<Item> planetItems = new ArrayList<Item>();
    private final int movementStep;
    //private final int givenCredits; ???
    public VisitPlanets(String nome, int cardLevel, int movementStep) {
        super(nome, cardLevel);
        this.movementStep = movementStep;
    }
    public void useCard(Player[] players) {

    }

    protected void bonusEffect() {

    }

    protected void malusEffect() {

    }

}
