package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;

public class OpenSpace extends EventCard {

    public void useCard(Player[] players) {
        for (Player player : players) {
            bonusEffect();
            malusEffect();
            player.setCursor(player.getCursor() + player.getShip().getEnginePower());
        }
    }

    protected void bonusEffect() {

    }

    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(Object response) {
        return null;
    }

    @Override
    public Object generateState() {
        return null;
    }

}