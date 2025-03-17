package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;

import javafx.util.Pair;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.events.Event;

import java.util.*;

import static it.polimi.ingsw.is25am28.Connector.ZERO_PIPES;

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence;

    public MeteorShower(
            String cardName,
            int cardLevel,
            JSONArray meteorsConfigs
    ) {
        super(cardName, cardLevel);
        meteorSequence = new ArrayList<Meteor>();

        for (Object meteor : meteorsConfigs) {
            JSONArray meteorDescriptor = (JSONArray) meteor;

            meteorSequence.add(
                new Meteor(
                    (int) meteorDescriptor.get(0),
                    (int) meteorDescriptor.get(1)
                )
            );
        }
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
        return null;
    }

    public void useCard(MeteorShowerJSON data) throws IllegalArgumentException, IllegalStateException {
        int diceResult, inboundDirection, sideToHit;
        boolean threatDestroyed;
        Component[] gridRow;
        Component[] gridColumn;
        List<Player> players;
        Random random;
        Ship shipPtr;
        JSONObject playerChoices;

        // Initializing variables
        players = this.board.getPlayers();
        random = new Random();
        playerChoices = data.getData();

        for (Meteor currMeteor : this.meteorSequence) {
            // TODO: How do I verify that it's the leader that throws the dices?
            // TODO: Maybe each player must have its own RNG inside and call it
            // TODO: In this case, if each player had its own RNG, the card would always call
            // TODO: the RNG inside the leader (ofc the card needs to keep a reference to the leader to do so)
            // Retrieving the leader's dice throw result
            // diceResult = (int) data.get("diceResult");

            // Two dice are thrown, result is between 2 and 12
            diceResult = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);

            // Adding +2 to the currMeteor's pointing direction gets the
            // side from where the ship will see it arrive from
            inboundDirection = (currMeteor.getOrientation() + 2) % 4;

            for (Player player : players) {
                // Initializations
                Component toHit = null;
                threatDestroyed = false;
                sideToHit = -1;
                shipPtr = player.getShip();
                JSONObject playerChoice = (JSONObject) playerChoices.get(player.getNickname());

                switch (inboundDirection) {
                    // Case 1.1 - Meteor arrives from the TOP
                    case 0 -> {
                        gridColumn = shipPtr.getGridColumn(diceResult - 1);
                        int row = 0;

                        // Iterating the column in search of the side where the meteor hits
                        // If found, then check for the next components in the column
                        // to see if there are any cannons and/or shields
                        while (toHit == null && row < gridColumn.length) {
                            toHit = gridColumn[row];
                            row++;
                        }

                        if (toHit == null) break;
                        sideToHit = toHit.getTopSide();
                    }

                    // Case 1.2 - Meteor arrives from the RIGHT
                    case 1 -> {
                        gridRow = shipPtr.getGridRow(diceResult - 1);
                        int column = 0;

                        // Iterating the column in search of the side where the meteor hits
                        // If found, then check for the next components in the column
                        // to see if there are any cannons and/or shields
                        while (toHit == null && column < gridRow.length) {
                            toHit = gridRow[column];
                            column++;
                        }

                        if (toHit == null) break;
                        sideToHit = toHit.getRightSide();
                    }

                    // Case 1.3 - Meteor arrives from the BOTTOM
                    case 2 -> {
                        gridColumn = shipPtr.getGridColumn(diceResult - 1);
                        int row = gridColumn.length - 1;

                        // Iterating the column in search of the side where the meteor hits
                        // If found, then check for the next components in the column
                        // to see if there are any cannons and/or shields
                        while (toHit == null && row >= 0) {
                            toHit = gridColumn[row];
                            row--;
                        }

                        if (toHit == null) break;
                        sideToHit = toHit.getBottomSide();
                    }

                    // Case 1.4 - Meteor arrives from the LEFT
                    case 3 -> {
                        gridRow = shipPtr.getGridRow(diceResult - 1);
                        int column = gridRow.length - 1;

                        // Iterating the column in search of the side where the meteor hits
                        // If found, then check for the next components in the column
                        // to see if there are any cannons and/or shields
                        while (toHit == null && column >= 0) {
                            toHit = gridRow[column];
                            column--;
                        }

                        if (toHit == null) break;
                        sideToHit = toHit.getLeftSide();
                    }

                    default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
                }

                // If there's a component in the path of the meteor, then elaborate further
                if (toHit != null && sideToHit != -1) {
                    if (currMeteor.getSize() == 1) {
                        // Case 1 - Small Meteor
                        // => Check if it can bounce on toHit or a shield is required
                        if (sideToHit != ZERO_PIPES.ordinal()) {
                            Component component = shipPtr.getComponent(
                                    (int) ((JSONArray) playerChoice.get("shield")).get(0),
                                    (int) ((JSONArray) playerChoice.get("shield")).get(1)
                            );

                            // Safe cast of Component to Shield
                            switch (component) {
                                case Shield shield -> {
                                    // Checking if the shield selected for activation
                                    // can actually defend the ship from the small meteor
                                    // by checking if it's correctly oriented towards the threat
                                    int[] shieldCoverage = shield.getCoveredSide();

                                    for (int j : shieldCoverage) {
                                        if (j == inboundDirection) {
                                            threatDestroyed = true;
                                            shipPtr.consumeEnergy(1);
                                            break;
                                        }
                                    }
                                }
                                case null, default -> {}
                            }
                        }
                        // else SMALL METEOR BOUNCES OFF
                    }
                    else {
                        // Case 2 - Big Meteor
                        // => Check if there are cannons that can destroy it
                        Component component = shipPtr.getComponent(
                                (int) ((JSONArray) playerChoice.get("shield")).get(0),
                                (int) ((JSONArray) playerChoice.get("shield")).get(1)
                        );

                        // Safe cast of Component to Cannon
                        switch (component) {
                            case Cannon cannon -> {
                                if (cannon.getDirection() == inboundDirection) {
                                    threatDestroyed = true;
                                    if (cannon.getFirePower() == 2) {
                                        // Consume energy only if the selected cannon is a double cannon
                                        shipPtr.consumeEnergy(1);
                                    }
                                }
                            }
                            case null, default -> {}
                        }
                    }
                }
                // else METEOR MISSES THE SHIP

                // If the meteor wasn't destroyed, then remove the component that was hit
                // from the current player's ship
                if (toHit != null && !threatDestroyed) {
                    shipPtr.removeComponent(
                            toHit.getPosition()[0],
                            toHit.getPosition()[1]
                    );
                }
            }
        }
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}
