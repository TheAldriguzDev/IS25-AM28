package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

/*
 * ====== NOTE ======
 *  The EventCard "Sabotage" is a level 3 exclusive special event card
 *  therefore, since the game is developed only for level 2 difficulty, it
 *  will not be implemented.
 * ==================
 */

public class Sabotage extends EventCard {

    public Sabotage(String name, int cardLevel) {
        super(name, cardLevel);
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
