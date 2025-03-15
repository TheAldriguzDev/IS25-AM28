package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Ship.Ship;

import java.util.concurrent.atomic.AtomicInteger;

public class Stardust extends EventCard {

    public Stardust(String name, int cardLevel) {
        super(name, cardLevel);
    }

    public EventCard useCard(Object response) throws ClassCastException {
        Player player = getCurrent();
        AtomicInteger movementSteps = new AtomicInteger();
        Ship ship = player.getShip();
        ship.traverse(
                (Component c) -> {
                    // For each exposed side movementsSteps++;
                    Component[] otherC = ship.getNearestComponents(c);
                    if (otherC[0] == null) {
                        if (c.getTopSide() != 0) {
                            movementSteps.getAndIncrement();
                        }
                    }
                    if (otherC[1] == null) {
                        if (c.getRightSide() != 0) {
                            movementSteps.getAndIncrement();
                        }
                    }
                    if (otherC[2] == null) {
                        if (c.getBottomSide() != 0) {
                            movementSteps.getAndIncrement();
                        }
                    }
                    if (otherC[3] == null) {
                        if (c.getLeftSide() != 0) {
                            movementSteps.getAndIncrement();
                        }
                    }
                }
        );
        player.setCursor(player.getCursor() - movementSteps.get());
        getNext();
        return this;
    }

    protected void bonusEffect() {}

    protected void malusEffect() {}

    @Override
    public Object generateState() {
        return null;
    }
}
