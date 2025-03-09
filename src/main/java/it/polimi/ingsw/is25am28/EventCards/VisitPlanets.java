package it.polimi.ingsw.is25am28.EventCards;


import java.util.ArrayList;

public class VisitPlanets extends EventCard {
    private ArrayList<Cargo> planetItems = new ArrayList<Cargo>();
    private final int movementStep;
    //private final int givenCredits; ???
    public VisitPlanets(String nome, int cardLevel, int movementStep) {
        super(nome, cardLevel);
        this.movementStep = movementStep;
    }
    protected void useCard(Player[] players) {

    }

    protected void bonusEffect() {

    }

    protected void malusEffect() {

    }

}
