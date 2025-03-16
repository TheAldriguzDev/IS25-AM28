package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

public class Sabotage extends EventCard {

    public Sabotage(String name, int cardLevel, Board board) {
        super(name, cardLevel, board);
    }

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
