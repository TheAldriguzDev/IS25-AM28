package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player;

import java.util.ArrayList;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private ArrayList<Cargo> planetItems = new ArrayList<Cargo>();

    public AbandonedStation(String name, int cardLevel, int requiredCrew, int movementStep, ArrayList<Cargo> planetItems) {
        this.name = name;
        this.cardLevel = cardLevel
        this.requiredCrew = requiredCrew;
        this.movementStep = movementStep;
        this.planetItems = planetItems;
    }

    public void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getLifeForms() > requiredCrew) {
                //method getChoice: ask player to make a choice
                if (getChoice()) {
                    bonusEffect(player);
                    player.setCursor(player.getCursor() - this.movementStep);
                    break;
                }
            }
        }
    }

    protected void bonusEffect(Player player) {
        // List<Cargo> = player.getShip().getCargo();
        // if()
        // Need Cargo class info to continue

    }

    protected void malusEffect(Player player) {}


}


