package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Component;

public class Epidemy extends EventCard {

    protected void useCard(Player[] players) {
        for (Player player : players) {
            player.getShip().traverse(
                    (Component c) -> {
                        // check type and use getNearest()
                    }
            );
        }
    }

    // protected void bonusEffect(Player player) {}

    // protected void malusEffect(Player player) {}

    @Override
    protected void bonusEffect() {

    }

    @Override
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
