package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Component;
import org.json.simple.JSONObject;

public class Epidemy extends EventCard {

    /**
     * General constructor shared between the classes
     *
     * @param name
     * @param cardLevel
     */
    protected Epidemy(String name, int cardLevel) {
        super(name, cardLevel);
    }

    protected void useCard(Player[] players) {
        for (Player player : players) {
            player.getShip().traverse(
                    (Component c) -> {
                        // check type and use getNearest()
                    }
            );
        }
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }

}