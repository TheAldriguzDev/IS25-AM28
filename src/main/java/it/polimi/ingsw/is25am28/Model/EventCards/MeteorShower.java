package it.polimi.ingsw.is25am28.Model.EventCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cannon;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Shield;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Model.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Model.Exceptions.InsufficientEnergyException;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

import static it.polimi.ingsw.is25am28.Model.Connector.ZERO_PIPES;

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence;
    private int currMeteorIndex;
    private int diceThrowResult;
    private final Random random;

    private List<Component> prevPlayerRemovedComponents;
    private String prevPlayer;

    public MeteorShower(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("meteorSequence") List<List<Integer>> meteorSequence,
            Board board
    ) {
        super(cardName, cardLevel, board);

        this.currMeteorIndex = 0;
        this.diceThrowResult = -1;
        this.meteorSequence = new ArrayList<Meteor>();
        this.random = new Random();
        this.prevPlayer = null;
        this.prevPlayerRemovedComponents = new ArrayList<>();

        try {
            for (List<Integer> meteorDescriptor : meteorSequence) {
                this.meteorSequence.add(
                    new Meteor(
                        meteorDescriptor.get(0),    // Meteor size
                        meteorDescriptor.get(1)     // Meteor orientation
                    )
                );
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: JSON parsing error in MeteorShower constructor -> " + e.getMessage() );
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
                Player nextPlayer = this.getBoard().getPlayers().get(currentIndex + 1);
                this.currentPlayer = Optional.of(nextPlayer);

                if ( !this.currentPlayer.get().isConnected()) {
                    this.currentPlayer = this.getNextPlayer();
                }

                return this.currentPlayer;
            }
        }
        else {
            this.currentPlayer = Optional.of(this.getBoard().getPlayers().getFirst());

            // If the first player is disconnected, then get the next one in line
            if ( !this.currentPlayer.get().isConnected()) {
                this.currentPlayer = this.getNextPlayer();
            }

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
        List<List<Integer>> shieldCoordsList;
        List<List<Integer>> cannonCoordsList;
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

        // Skips any player marked as disconnected during their turn
        if (this.currentPlayer.get().isConnected()) {
            // Other initializations
            shipPtr = this.currentPlayer.get().getShip();
            Meteor currMeteor = this.meteorSequence.get(this.currMeteorIndex);

            // The meteor descriptor already has as its orientation the
            // side from which the ship sees that meteor come from
            inboundDirection = currMeteor.getOrientation();

            // Adding +2 to the currMeteor's pointing direction gets the
            // side from where the ship will see it arrive from
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
                        for (List<Integer> shieldCoords : shieldCoordsList) {
                            if (shieldCoords != null) {
                                Component component = shipPtr.getComponent(
                                        shieldCoords.get(0),
                                        shieldCoords.get(1)
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
                    for (List<Integer> cannonCoords : cannonCoordsList) {
                        if (cannonCoords != null) {
                            Component component = shipPtr.getComponent(
                                    cannonCoords.get(0),
                                    cannonCoords.get(1)
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
                                                    case 0 -> {
                                                        // The front cannons can only destroy the meteor if it is directly on the same column
                                                        threatDestroyed = (cannon.getPosition()[1] == this.diceThrowResult - 1);
                                                    }
                                                    case 2 -> {
                                                        // For higher levels (>2), a meteor COMING FROM THE BOTTOM can also be destroyed
                                                        // if it's traveling on a COLUMN adjacent to the one where this cannon is places
                                                        if (this.getBoard().getLevel() > 2) {
                                                            threatDestroyed =
                                                                    (cannon.getPosition()[1] == this.diceThrowResult - 2)
                                                                             || (cannon.getPosition()[1] == this.diceThrowResult - 1)
                                                                             || (cannon.getPosition()[1] == this.diceThrowResult);
                                                        }
                                                        else {
                                                            threatDestroyed = (cannon.getPosition()[1] == this.diceThrowResult - 1);
                                                        }
                                                    }

                                                    // Case 2 - Cannon must be aligned to the ROW from which the currMeteor is coming from
                                                    //          in order to be able to shoot it (if it was enabled)
                                                    case 1, 3 -> {
                                                        // For higher levels (>1), a meteor COMING FROM THE SIDES can also be destroyed
                                                        // if it's traveling on a ROW adjacent to the one where this cannon is places
                                                        if (this.getBoard().getLevel() > 1) {
                                                            threatDestroyed =
                                                                    (cannon.getPosition()[0] == this.diceThrowResult - 2)
                                                                             || (cannon.getPosition()[0] == this.diceThrowResult - 1)
                                                                             || (cannon.getPosition()[0] == this.diceThrowResult);
                                                        }
                                                        else {
                                                            threatDestroyed = (cannon.getPosition()[0] == this.diceThrowResult - 1);
                                                        }
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
                                            case 0 -> {
                                                // The front cannons can only destroy the meteor if it is directly on the same column
                                                threatDestroyed = (cannon.getPosition()[1] == this.diceThrowResult - 1);
                                            }
                                            case 2 -> {
                                                // For higher levels (>2), a meteor COMING FROM THE BOTTOM can also be destroyed
                                                // if it's traveling on a COLUMN adjacent to the one where this cannon is places
                                                if (this.getBoard().getLevel() > 2) {
                                                    threatDestroyed =
                                                            (cannon.getPosition()[1] == this.diceThrowResult - 2)
                                                                    || (cannon.getPosition()[1] == this.diceThrowResult - 1)
                                                                    || (cannon.getPosition()[1] == this.diceThrowResult);
                                                }
                                                else {
                                                    threatDestroyed = (cannon.getPosition()[1] == this.diceThrowResult - 1);
                                                }
                                            }

                                            // Case 2 - Cannon must be aligned to the ROW from which the currMeteor is coming from
                                            //          in order to be able to shoot it (if it was enabled)
                                            case 1, 3 -> {
                                                // For higher levels (>1), a meteor COMING FROM THE SIDES can also be destroyed
                                                // if it's traveling on a ROW adjacent to the one where this cannon is places
                                                if (this.getBoard().getLevel() > 1) {
                                                    threatDestroyed =
                                                            (cannon.getPosition()[0] == this.diceThrowResult - 2)
                                                                    || (cannon.getPosition()[0] == this.diceThrowResult - 1)
                                                                    || (cannon.getPosition()[0] == this.diceThrowResult);
                                                }
                                                else {
                                                    threatDestroyed = (cannon.getPosition()[0] == this.diceThrowResult - 1);
                                                }
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
                    // The current player will become the previous player after the
                    // current meteor effects have been applied to him, therefore in
                    // the next state this player will become the previous player and to
                    // perform a differential update, we need to store the effects of the meteor
                    // on the current player before the card moves to the next player.
                    this.prevPlayer = this.currentPlayer.get().getNickname();
                    this.prevPlayerRemovedComponents = shipPtr.removeComponent(
                            toHit.getPosition()[0],
                            toHit.getPosition()[1]
                    );
                } catch (CoreDeletionAttemptException e) {
                    this.getBoard().eliminatePlayer(this.currentPlayer.get());
                }
            }
            else {
                // Otherwise, if the player did not get hit, just express it
                // by setting the prevPlayerRemovedComponents list to null
                // (since, again, the current player wasn't hit by any meteors)
                this.prevPlayer = this.currentPlayer.get().getNickname();
                //this.prevPlayerRemovedComponents = null;
            }
        }

        // Getting the next player (in order of leaderboard placements)
        this.currentPlayer = this.getNextPlayer();

        // After the current meteor was confronted with all players, do:
        //  1) Increment currMeteorIndex to iterate on the next meteor
        //  2) Re-initialize card players, since we need to loop over all players again for the next meteor
        //  3) Calculate the next dice throw for the next meteor
        if (this.currentPlayer.isEmpty()) {
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
        cardState.setNeedsBoardUpdate(false);
        cardState.setNeedsPlayerUpdate(false);
        cardState.setNeedsShipUpdate(false);

        // The dice throw is performed by generateState only at the beginning
        // since the card hasn't been used yet
        if (this.diceThrowResult == -1) {
            this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        }

        // If the current player is present, then add it to the card state
        this.currentPlayer.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));

        if (hasBeenActivated()) {
            cardState.setCurrMeteorIndex(this.currMeteorIndex);
            cardState.setDiceThrowResult(this.diceThrowResult);
            cardState.setCurrMeteorDescriptor(Map.of("meteorSize", this.meteorSequence.get(this.currMeteorIndex).getSize(), "meteorDirection", this.meteorSequence.get(this.currMeteorIndex).getOrientation()));
            // The differential update happens always except when the card is
            // first picked (since no one has been hit with a meteor yet)
            if (!this.prevPlayerRemovedComponents.isEmpty()) {
                // Setting which components were removed from the previous player, thus
                // performing a differential update on what changed before the card
                // transitioned to the next state
                cardState.setPreviousPlayerRemovedComponents(Map.of(this.prevPlayer, this.prevPlayerRemovedComponents.stream().map(Component::toMap).toList()));
                this.prevPlayerRemovedComponents.clear();
                this.prevPlayer = null;
            }
            else {
                // No components were removed, therefore the
                // prevPlayerRemovedComponents list is null
                cardState.setPreviousPlayerRemovedComponents(null);
            }

        } else {
            cardState.setId(this.id);
            cardState.setCardName(this.getCardName());
            cardState.setCardLevel(this.cardLevel);
        }
        return cardState;
    }

    public WidgetTUI generateWidget(CardStateJSON meteorShowerJSON) {
        return null;
    }
}
