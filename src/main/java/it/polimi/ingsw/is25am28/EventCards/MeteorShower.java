package it.polimi.ingsw.is25am28.EventCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Exceptions.InsufficientEnergyException;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;

import javafx.util.Pair;

import java.util.*;

import static it.polimi.ingsw.is25am28.Connector.ZERO_PIPES;

// TODO: Change the reference frame (0 != meteor POINTS to the top, but 0 == meteor COMES from the top)

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence;
    private int currMeteorIndex;
    private int playerUseCount;
    private int diceThrowResult;
    private final Random random;
    
    public MeteorShower(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("meteorSequence") List<Pair<Integer, Integer>> meteorSequence,
            Board board
    ) {
        super(cardName, cardLevel, board);

        this.currMeteorIndex = 0;
        this.playerUseCount = 0;
        this.diceThrowResult = -1;
        this.meteorSequence = new ArrayList<Meteor>();
        this.random = new Random();

        try {
            for (Pair<Integer, Integer> meteorDescriptor : meteorSequence) {
                this.meteorSequence.add(
                    new Meteor(
                        meteorDescriptor.getKey(),  // Meteor size
                        meteorDescriptor.getValue() // Meteor orientation
                    )
                );
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: JSON parsing error in MeteorShower constructor");
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
    public Optional<Player> getNextPlayer() {
        if (this.players == null || this.players.isEmpty()) {
            throw new Error("Players are not set, you must call startUsingCard method before");
        }

        if (this.currentPlayer.isPresent()) {
            int currentIndex = this.players.indexOf(this.currentPlayer.get());

            // If the current player is the last one return null,
            // otherwise return the next player
            if (currentIndex == this.players.size() - 1) {
                return Optional.empty();
            }
            else {
                return Optional.of(this.getBoard().getPlayers().get(currentIndex + 1));
            }
        }
        else {
            this.currentPlayer = Optional.of(this.getBoard().getPlayers().getFirst());
            return this.currentPlayer;
        }
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException, IllegalStateException {
        MeteorShowerJSON meteorShowerJSON;
        int inboundDirection, sideToHit;
        boolean threatDestroyed;
        Component[] gridRow;
        Component[] gridColumn;
        List<Pair<Integer, Integer>> shieldCoordsList;
        List<Pair<Integer, Integer>> cannonCoordsList;
        Component toHit;
        Ship shipPtr;

        // Initializing variables
        toHit = null;
        threatDestroyed = false;
        sideToHit = -1;

        try {
            // ActionJSON unpacking
            meteorShowerJSON = ((MeteorShowerJSON) data);
            this.diceThrowResult = meteorShowerJSON.getDiceThrowResult();
            shieldCoordsList = meteorShowerJSON.getShieldsCoordinates();
            cannonCoordsList = meteorShowerJSON.getCannonsCoordinates();

            if (this.currentPlayer.isEmpty()) {
                throw new IllegalArgumentException("ERROR: Given player is not present in the current game");
            }
            if ( !this.currentPlayer.get().getNickname().equals(meteorShowerJSON.getPlayerNickname())) {
                throw new IllegalArgumentException("ERROR: Current player and player in meteorShowerJSON do not match (wrong arguments)");
            }
            if (this.diceThrowResult < 2 || this.diceThrowResult > 12) {
                throw new IllegalArgumentException("ERROR: Dice throw result cannot be outside of the range [2, 12]");
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("[MeteorShower::useCard] " + e.getMessage());
        }

        // Other initializations
        shipPtr = this.currentPlayer.get().getShip();
        Meteor currMeteor = this.meteorSequence.get(this.currMeteorIndex);

        // Adding +2 to the currMeteor's pointing direction gets the
        // side from where the ship will see it arrive from
        inboundDirection = currMeteor.getOrientation();

        // inboundDirection = (currMeteor.getOrientation() + 2) % 4;

        switch (inboundDirection) {
            // Case 1 - Meteor arrives from the TOP
            case 0 -> {
                gridColumn = shipPtr.getGridColumn(this.diceThrowResult - 1);
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
                gridRow = shipPtr.getGridRow(this.diceThrowResult - 1);
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

            // Case 3 - Meteor arrives from the BOTTOM
            case 2 -> {
                gridColumn = shipPtr.getGridColumn(this.diceThrowResult - 1);
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
                gridRow = shipPtr.getGridRow(this.diceThrowResult - 1);
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

            default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
        }

        // If there's a component in the path of the meteor, then elaborate further
        if (toHit != null) {
            // Case 1 - Small Meteor
            // => Check if it can bounce on toHit or a shield is required
            if (sideToHit != ZERO_PIPES.ordinal()) {
                if (shieldCoordsList != null) {
                    for (Pair<Integer, Integer> shieldCoords : shieldCoordsList) {
                        if (shieldCoords != null) {
                            Component component = shipPtr.getComponent(
                                    shieldCoords.getKey(),
                                    shieldCoords.getValue()
                            );

                            // Safe cast of Component to Shield
                            switch (component) {
                                case Shield shield -> {
                                    int[] shieldCoverage = shield.getCoveredSide();
                                    try {
                                        // Consume energy only if there's enough energy available
                                        shipPtr.consumeEnergy(1);
                                        for (int j : shieldCoverage) {
                                            if (currMeteor.getSize() == 1 && j == inboundDirection) {
                                                // Checking if the shield selected for activation
                                                // can actually defend the ship from the small meteor
                                                // by checking if it's correctly oriented towards the threat
                                                threatDestroyed = true;
                                                break;
                                            }
                                        }
                                    }
                                    catch (InsufficientEnergyException e) {
                                        // Otherwise the ship depleted its energy reserve and the selected shields
                                        // cannot be activated, therefore the meteor will not be deflected
                                        threatDestroyed = false;
                                    }
                                }
                                case null, default -> {}
                            }
                        }
                    }
                }
            }

            // Case 2 - Big Meteor
            // => Check if there are cannons that can destroy it
            if (cannonCoordsList != null) {
                for (Pair<Integer, Integer> cannonCoords : cannonCoordsList) {
                    if (cannonCoords != null) {
                        Component component = shipPtr.getComponent(
                                cannonCoords.getKey(),
                                cannonCoords.getValue()
                        );

                        // Safe cast of Component to Cannon
                        switch (component) {
                            case Cannon cannon -> {
                                if ((cannon.getFirePower() == 2 && cannon.getDirection() == 0) || (cannon.getFirePower() == 1 && cannon.getDirection() != 0)) {
                                    try {
                                        // Consume energy only if the selected cannon is a double cannon
                                        // and if there's energy available
                                        shipPtr.consumeEnergy(1);
                                        if (currMeteor.getSize() == 2 && cannon.getDirection() == inboundDirection) {
                                            switch (inboundDirection) {
                                                // Case 1 - Cannon must be aligned to the COLUMN from which the currMeteor is coming from
                                                //          in order to be able to shoot it (if it was enabled)
                                                case 0, 2 -> {
                                                    threatDestroyed = (cannon.getPosition()[1] == this.diceThrowResult - 1);
                                                }
                                                // Case 2 - Cannon must be aligned to the ROW from which the currMeteor is coming from
                                                //          in order to be able to shoot it (if it was enabled)
                                                case 1, 3 -> {
                                                    threatDestroyed = (cannon.getPosition()[0] == this.diceThrowResult - 1);
                                                }
                                                default -> throw new IllegalStateException("ERROR: inboundDirection must be between 0 and 3 (extremes included)");
                                            }
                                        }
                                    }
                                    catch (InsufficientEnergyException e) {
                                        // Otherwise the ship depleted its energy reserve and the selected cannons
                                        // cannot be activated, therefore the meteor will not be destroyed
                                        threatDestroyed = false;
                                    }
                                }
                                else if (currMeteor.getSize() == 2 && cannon.getDirection() == inboundDirection) {
                                    switch (inboundDirection) {
                                        // Case 1 - Cannon must be aligned to the COLUMN from which the currMeteor is coming from
                                        //          in order to be able to shoot it (if it was enabled)
                                        case 0, 2 -> {
                                            threatDestroyed = (cannon.getPosition()[1] == this.diceThrowResult - 1);
                                        }
                                        // Case 2 - Cannon must be aligned to the ROW from which the currMeteor is coming from
                                        //          in order to be able to shoot it (if it was enabled)
                                        case 1, 3 -> {
                                            threatDestroyed = (cannon.getPosition()[0] == this.diceThrowResult - 1);
                                        }
                                        default -> throw new IllegalStateException("ERROR: inboundDirection must be between 0 and 3 (extremes included)");
                                    }
                                }
                            }
                            case null, default -> {}
                        }
                    }
                }
            }
        }

        // If the meteor wasn't destroyed, then remove the component
        // that was hit from the current player's ship
        if (toHit != null && !threatDestroyed) {
            try {
                shipPtr.removeComponent(
                        toHit.getPosition()[0],
                        toHit.getPosition()[1]
                );
            } catch (CoreDeletionAttemptException e) {
                this.getBoard().eliminatePlayer(this.currentPlayer.get());
            }
        }

        // Counting how many times the card has been
        // used among all active players
        this.playerUseCount++;

        // Getting the next player (in order of leaderboard placements)
        this.currentPlayer = this.getNextPlayer();

        // After the current meteor was confronted with all players, do:
        //  1) Increment currMeteorIndex to iterate on the next meteor
        //  2) Re-initialize card players, since we need to loop over all players again for the next meteor
        //  3) Calculate the next dice throw for the next meteor
        if (this.playerUseCount % this.getBoard().getPlayers().size() == 0) {
            this.currMeteorIndex++;
            this.initCardPlayers();
            this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        }

        // The card gets marked as completed only when all players
        // have encountered all the meteors in meteorSequence
        if (this.currMeteorIndex == this.meteorSequence.size()) {
            this.cardUsed();
        }

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();

        // If the current player is present, then add it to the card state
        this.currentPlayer.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));

        // The dice throw is performed by generateState only at the beginning
        // since the card hasn't been used yet
        if (this.diceThrowResult == -1) {
            this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        }

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.cardLevel);
        cardState.setCardIsUsable(! this.hasFinished());
        cardState.setCurrMeteorIndex(this.currMeteorIndex);
        cardState.setDiceThrowResult(this.diceThrowResult);

        cardState.setCurrMeteorDescriptor(
            new Pair<Integer, Integer>(
                this.meteorSequence.get(this.currMeteorIndex).getSize(),
                this.meteorSequence.get(this.currMeteorIndex).getOrientation()
            )
        );

        return cardState;
    }
}
