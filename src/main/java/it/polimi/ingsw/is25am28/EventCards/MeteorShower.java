package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;

import javafx.util.Pair;
import org.json.simple.JSONArray;

import java.util.*;

import static it.polimi.ingsw.is25am28.Connector.ZERO_PIPES;

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence;

    public MeteorShower(
            String cardName,
            int cardLevel,
            JSONArray meteorsConfigs,
            Board board
    ) {
        super(cardName, cardLevel, board);
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
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException, IllegalStateException {
        int diceResult, inboundDirection, sideToHit;
        boolean threatDestroyed;
        Component[] gridRow;
        Component[] gridColumn;
        List<Player> players;
        Pair<Integer, Integer> shieldCoords;
        Pair<Integer, Integer> cannonCoords;
        Random random;
        Ship shipPtr;
        MeteorShowerJSON playerChoices;

        try {
            // ActionJSON unpacking
            playerChoices = ((MeteorShowerJSON) data);

            // Initializing variables
            players = this.getBoard().getPlayers();
            random = new Random();

            for (Meteor currMeteor : this.meteorSequence) {
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

                    switch (inboundDirection) {
                        // Case 1 - Meteor arrives from the TOP
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
                            sideToHit = toHit.getTopSide().ordinal();
                        }

                        // Case 2 - Meteor arrives from the RIGHT
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
                            sideToHit = toHit.getRightSide().ordinal();
                        }

                        // Case 3 - Meteor arrives from the BOTTOM
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
                            sideToHit = toHit.getBottomSide().ordinal();
                        }

                        // Case 4 - Meteor arrives from the LEFT
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
                            sideToHit = toHit.getLeftSide().ordinal();
                        }

                        default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
                    }

                    // If there's a component in the path of the meteor, then elaborate further
                    if (toHit != null) {
                        if (currMeteor.getSize() == 1) {
                            // Case 1 - Small Meteor
                            // => Check if it can bounce on toHit or a shield is required
                            if (sideToHit != ZERO_PIPES.ordinal()) {
                                shieldCoords = playerChoices.getShieldCoordinatesPerPlayer(player);

                                Component component = shipPtr.getComponent(
                                        shieldCoords.getKey(),
                                        shieldCoords.getValue()
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
                                    case null, default -> {
                                    }
                                }
                            }
                            // else SMALL METEOR BOUNCES OFF
                        } else {
                            // Case 2 - Big Meteor
                            // => Check if there are cannons that can destroy it
                            cannonCoords = playerChoices.getCannonCoordinatesPerPlayer(player);

                            Component component = shipPtr.getComponent(
                                    cannonCoords.getKey(),
                                    cannonCoords.getValue()
                            );

                            // Safe cast of Component to Cannon
                            switch (component) {
                                case Cannon cannon -> {
                                    if (cannon.getDirection() == inboundDirection) {
                                        if (cannon.getFirePower() == 2) {
                                            // Consume energy only if the selected cannon is a double cannon
                                            shipPtr.consumeEnergy(1);
                                        }
                                        threatDestroyed = true;
                                    }
                                }
                                case null, default -> {
                                }
                            }
                        }
                    }
                    // else METEOR MISSES THE SHIP

                    // If the meteor wasn't destroyed, then remove the component
                    // that was hit from the current player's ship
                    if (toHit != null && !threatDestroyed) {
                        shipPtr.removeComponent(
                                toHit.getPosition()[0],
                                toHit.getPosition()[1]
                        );
                    }
                }
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: Parsing error occurred when unpacking the given ActionJSON");
        }

        // Set the hasBeenUsed flag to true
        this.cardUsed();

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.getCardLevel());

        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        return cardState;
    }
}
