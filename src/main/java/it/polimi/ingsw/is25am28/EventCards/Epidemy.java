package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player;

public class Epidemy extends EventCard {

    protected void useCard(Player[] players) {
        for (Player player : players) {
            player.getShip().traverse(//Lambda per identificare le cabine);
        }
    }

    protected void bonusEffect(Player player) {}

    protected void malusEffect(Player player) {}
}
