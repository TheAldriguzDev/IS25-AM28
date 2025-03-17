package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Random;

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence;

    public MeteorShower(
            String cardName,
            int cardLevel,
            List<Meteor> meteorSequence
    ) {
        super(cardName, cardLevel);
        this.meteorSequence = meteorSequence;
    }

    @Override
    protected void bonusEffect() {
        // Nothing
    }

    @Override
    protected void malusEffect() {
        // Nothing
    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        List<Player> players = board.getPlayers();
        Pair<Integer, Integer> gridDimensions;
        Ship shipPtr;
        Random random = new Random();
        int doubleDiceThrowResult;

        for (Meteor currMeteor : this.meteorSequence) {
            // TODO: How do I verify that it's the leader that throws the dices?
            // Two dice are thrown, each yield a result between 1 and 6, then since the
            // ship's grid is centered at (7,7) in the physical game, the dice throws need to be
            // offset by a value of 4 up and to the left, thus re-centering the ship with the
            // positioning of the grid, which is indexed as an array
            doubleDiceThrowResult = (random.nextInt(6) + 1) + (random.nextInt(6) + 1) - 4;

            for (Player player : players) {
                shipPtr = player.getShip();
                gridDimensions = shipPtr.getGridDimensions();

                if (doubleDiceThrowResult >= gridDimensions.getKey() && doubleDiceThrowResult <= gridDimensions.getValue()) {
                    // Case 1 - Both row and column pass through the ship's grid
                    // => The meteor can come from any direction
                }
                else if (true) {
                    // Case 2 - Only the row passes through the ship's grid
                    // => The meteor can come from only the RIGHT and LEFT directions
                }
                else if (true) {
                    // Case 3 - Only the column passes through the ship's grid
                    // => The meteor can come from only the TOP and BOTTOM directions
                }
                else {
                    // Case 4 - Neither the row nor the column pass through the ship's grid
                    // => The meteor misses the ship
                }

                if (doubleDiceThrowResult >= gridDimensions.getKey()) {
                    if (true) {

                    }
                }
                else {

                }
            }
        }

        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}
