package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Board.Board;
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
    protected Epidemy(String name, int cardLevel, Board board) {
        super(name, cardLevel, board);
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

    protected void bonusEffect(Player player) {}

    protected void malusEffect(Player player) {}

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }

}