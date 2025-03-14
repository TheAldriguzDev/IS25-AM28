package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player;

public class OpenSpace extends EventCard {

    public void useCard(Player[] players) {
        for (Player player : players) {
            bonusEffect(player);
            malusEffect(player);
            player.setCursor(player.getCursor() + player.getShip().getEnginePower());
        }
    }

    protected void bonusEffect(Player player) {}

    protected void malusEffect(Player player) {}

}
