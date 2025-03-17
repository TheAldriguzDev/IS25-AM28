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
        int diceResult, randomlySelectedRow, randomlySelectedCol;
        Pair<Integer, Integer> gridDimensions;
        List<Player> players;
        Random random;
        Ship shipPtr;

        // Initializing variables
        players = board.getPlayers();
        random = new Random();

        for (Meteor currMeteor : this.meteorSequence) {
            // TODO: How do I verify that it's the leader that throws the dices?
            diceResult = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);

            for (Player player : players) {
                shipPtr = player.getShip();
                randomlySelectedRow = diceResult - shipPtr.getOffsets().getKey();
                randomlySelectedCol = diceResult - shipPtr.getOffsets().getValue();
                gridDimensions = shipPtr.getGridDimensions();

                if (randomlySelectedRow < gridDimensions.getKey() && randomlySelectedRow >= 0) {
                    if (randomlySelectedCol < gridDimensions.getValue() && randomlySelectedCol >= 0) {
                        // Case 1 - Both row and column pass through the ship's grid
                        // => The meteor can come from any direction

                        // All 4 directions
                    }
                    else {
                        // Case 2 - Only the row passes through the ship's grid
                        // => The meteor can come from only the RIGHT and LEFT directions

                        // Only 2 directions - Right, Left
                    }
                }
                else {
                    if (randomlySelectedCol < gridDimensions.getValue() && randomlySelectedCol >= 0) {
                        // Case 3 - Only the column passes through the ship's grid
                        // => The meteor can come from only the TOP and BOTTOM directions

                        // Only 2 directions - Right, Left
                    }
                    else {
                        // Case 4 - Neither the row nor the column pass through the ship's grid
                        // => The meteor misses the ship
                    }
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
