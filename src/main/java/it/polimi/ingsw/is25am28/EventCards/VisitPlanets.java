package it.polimi.ingsw.is25am28.EventCards;


import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class VisitPlanets extends EventCard {
    private ArrayList<Item> planetItems = new ArrayList<Item>();
    private final int movementStep;
    //private final int givenCredits; ???
    public VisitPlanets(String name, int cardLevel, int movementStep, Board board) {
        super(name, cardLevel, board);
        this.movementStep = movementStep;
    }
    public void useCard(Player[] players) {

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
