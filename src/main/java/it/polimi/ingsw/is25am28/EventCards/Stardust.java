package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Ship.Ship;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.simple.JSONObject;

public class Stardust extends EventCard {

    public Stardust(String name, int cardLevel, Board board) {
        super(name, cardLevel, board);
    }

    public EventCard useCard(ActionJSON data) throws ClassCastException {
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
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
                }
        );
        getNextPlayer();
        return this;
    }

    protected void bonusEffect() {}

    protected void malusEffect() {}

    @Override
    public JSONObject generateState() {
        return null;
    }
}
