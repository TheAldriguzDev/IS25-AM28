package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Battery;
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
    private final Random random;
    private int currMeteorIndex;
    private int diceThrowResult;

    private List<Component> prevPlayerRemovedComponents;
    private String prevPlayer;

    private Map<String, List<Map<String, Object>>> removedComponents;
    private final Map<String, Integer> removedBatteries; // TODO: Implement in the state
    private final List<String> eliminatedPlayers;


    public MeteorShower(
            String cardName,
            int cardLevel,
            List<List<Integer>> meteorSequence,
            Board board,
            int cardID
    ) {
        super(cardName, cardLevel, board, cardID);

        this.meteorSequence = new ArrayList<Meteor>();
        this.random = new Random();
        this.currMeteorIndex = 0;
        this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        this.prevPlayerRemovedComponents = new ArrayList<>();
        this.prevPlayer = null;
        this.removedComponents = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();

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
                // TODO: Take the players not from the board (which updates continuously), but from a fixed list
                Player nextPlayer = this.players.get(currentIndex + 1);
                this.currentPlayer = Optional.of(nextPlayer);

                if ( !this.currentPlayer.get().isConnected()) {
                    this.currentPlayer = this.getNextPlayer();
                }

                return this.currentPlayer;
            }
        }
        else {
            this.currentPlayer = Optional.of(this.players.getFirst());

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
        List<ComponentHelper<Void>> shieldCoordsList;
        List<ComponentHelper<Void>> cannonCoordsList;
        Component toHit;
        Ship shipPtr;

        // Initializing variables
        toHit = null;
        sideToHit = -1;

        try {
            // ActionJSON unpacking
            meteorShowerJSON = ((MeteorShowerJSON) data);
            shieldCoordsList = meteorShowerJSON.getShieldsCoordinates();
            cannonCoordsList = meteorShowerJSON.getCannonsCoordinates();

            if (this.currentPlayer.isEmpty()) {
                throw new IllegalArgumentException("ERROR: Given player is not present in the current game");
            }
            if ( !this.currentPlayer.get().getNickname().equals(meteorShowerJSON.getPlayerNickname())) {
                throw new IllegalArgumentException("ERROR: Current player and player in meteorShowerJSON do not match (wrong arguments)");
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
                    sideToHit = toHit.getRightSide().ordinal();
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
                    sideToHit = toHit.getRightSide().ordinal();
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
                    sideToHit = toHit.getLeftSide().ordinal();
                }

                default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
            }

            // If a small meteor will hit a smooth side, then it'll bounce
            threatDestroyed = ((currMeteor.getSize() == 1) && (sideToHit == ZERO_PIPES.ordinal()));

            // Activate all shields chosen by the player (even if unnecessary)
            if (!shieldCoordsList.isEmpty()) {
                if (currMeteor.getSize() != 1) {
                    // If it's a big meteor, then just activate all shields but
                    // the meteor will not be stopped as per game rules, therefore
                    // any shields activated in this case are simply wasted energy.
                    shipPtr.consumeEnergy(Math.min(shipPtr.getAvailableEnergy(), (int) shieldCoordsList.stream().filter(Objects::nonNull).count()));
                }
                else {
                    for (ComponentHelper<Void> shieldCoords : shieldCoordsList) {
                        if (shieldCoords != null) {
                            Component component = shipPtr.getComponent(
                                    shieldCoords.getI(),
                                    shieldCoords.getJ()
                            );

                            // Safe cast of Component to Shield
                            switch (component) {
                                case Shield shield -> {
                                    int[] shieldCoverage = shield.getCoveredSide();
                                    try {
                                        // Consume energy only if there's enough energy available
                                        shipPtr.consumeEnergy(1);

                                        for (int j : shieldCoverage) {
                                            if (j == inboundDirection) {
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
                                    }
                                }
                                case null, default -> {}
                            }
                        }
                    }
                }
            }

            // Considering all single cannons on the ship in
            // addition to the double cannons that the player
            // chose to activate
            List<Cannon> mixedCannons = new ArrayList<>(
                shipPtr.getCannonList().stream()
                    .filter(c -> !c.requireEnergy())
                    .toList()
            );

            // Adding all double cannons selected by the player to
            // the mixed cannons list
            for (ComponentHelper<Void> cannonCoords : cannonCoordsList) {
                if (cannonCoords != null) {
                    Component component = shipPtr.getComponent(
                            cannonCoords.getI(),
                            cannonCoords.getJ()
                    );

                    switch (component) {
                        case Cannon cannon -> mixedCannons.add(cannon);
                        case null, default -> {}
                    }
                }
            }

            // Check if the meteor can be destroyed by at least one of the
            // cannons found in the mixed cannons list, and also activate all
            // double cannons chosen by the player (even if unnecessary)
            if (!mixedCannons.isEmpty() && !mixedCannons.contains(null)) {
                for (Cannon cannon : mixedCannons) {
                    // If the current cannon is a DoubleCannon, then consume a battery
                    if (cannon.requireEnergy()) {
                        try {
                            // Consume energy only if the selected cannon is a double cannon
                            // and if there's energy available
                            shipPtr.consumeEnergy(1);
                        }
                        catch (InsufficientEnergyException e) {
                            // Otherwise the ship depleted its energy reserve and the selected cannons
                            // cannot be activated, therefore the meteor will not be destroyed by this cannon
                            continue;
                        }
                    }

                    // NOTE: Only big meteors can be destroyed by a cannon, thus there's no need to
                    //       perform the following checks if it's a small meteor.
                    if (!threatDestroyed && currMeteor.getSize() == 2 && cannon.getDirection() == inboundDirection) {
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

                    this.removedComponents.put(this.prevPlayer, this.prevPlayerRemovedComponents.stream().map(Component::toMap).toList());
                }
                catch (CoreDeletionAttemptException e) {
                    this.eliminatedPlayers.add(this.currentPlayer.get().getNickname());
                    this.getBoard().eliminatePlayer(this.currentPlayer.get());
                }
            }
            else {
                // Otherwise, if the player did not get hit, just express it
                // by setting the prevPlayerRemovedComponents list to null
                // (since, again, the current player wasn't hit by any meteors)
                this.prevPlayer = this.currentPlayer.get().getNickname();
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
        cardState.setCardID(this.getCardID());

        if (hasBeenActivated()) {
            initStateFlags(cardState);

            // If the current player is present, then add it to the card state
            this.currentPlayer.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));

            cardState.setCurrMeteorIndex(this.currMeteorIndex);
            cardState.setDiceThrowResult(this.diceThrowResult);
            if (this.currMeteorIndex < this.meteorSequence.size()) {
                cardState.setCurrMeteorDescriptor(Map.of("meteorSize", this.meteorSequence.get(this.currMeteorIndex).getSize(), "meteorDirection", this.meteorSequence.get(this.currMeteorIndex).getOrientation()));
            }
            // The differential update happens always except when the card is
            // first picked (since no one has been hit with a meteor yet)

            // Setting which components were removed from the previous player, thus
            // performing a differential update on what changed before the card
            // transitioned to the next state
            setUpdatedRemovedComponentsIfNecessary(cardState, this.removedComponents);

            // Setting the eliminated players (if there are any)
            setUpdatedEliminatedPlayersIfNecessary(cardState, this.eliminatedPlayers);

            // Setting the batteries consumed by the shields and the doubleCannons
            setUpdatedRemovedBatteriesIfNecessary(cardState, this.removedBatteries);
        } else {
            cardState.setId(this.id);
            cardState.setCardName(this.getCardName());
            cardState.setCardLevel(this.cardLevel);
        }

        cardState.setCardEnded(this.hasFinished());

        return cardState;
    }

    // Only for testing
    void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    public WidgetTUI generateWidget(CardStateJSON meteorShowerJSON) {
        return null;
    }
}
