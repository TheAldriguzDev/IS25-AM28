package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Components.Cannon;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Engine;
import it.polimi.ingsw.is25am28.Model.Components.Shield;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Model.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Model.Exceptions.InsufficientEnergyException;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

import static it.polimi.ingsw.is25am28.Model.Connector.ZERO_PIPES;

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence;
    private final Random random;
    private int currMeteorIndex;
    private int diceThrowResult;

    private List<Component> prevPlayerRemovedComponents;
    private String prevPlayer;

    private final Map<String, List<Map<String, Object>>> removedComponents;
    private final Map<String, List<ComponentHelper<Void>>> removedBatteries;
    private final List<String> eliminatedPlayers;
    private final Map<String, Integer> lostPieces;
    private final Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms;

    public MeteorShower(
            String cardName,
            int cardLevel,
            List<List<Integer>> meteorSequence,
            Board board,
            int cardID,
            String path
    ) {
        super(cardName, cardLevel, board, cardID, path);

        this.meteorSequence = new ArrayList<>();
        this.random = new Random();
        this.currMeteorIndex = 0;
        this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        this.prevPlayerRemovedComponents = new ArrayList<>();
        this.prevPlayer = null;
        this.removedComponents = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.lostPieces = new HashMap<>();
        this.removedLifeforms = new HashMap<>();

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
        List<Pair<ComponentHelper<Void>, ComponentHelper<Void>>> shieldsToActivate;
        List<Pair<ComponentHelper<Void>, ComponentHelper<Void>>> doubleCannonsToActivate;
        List<Shield> activatedShieldsList;
        List<Cannon> activatedDoubleCannonsList;
        Component toHit;
        Ship shipPtr;

        // Initializing variables
        toHit = null;
        sideToHit = -1;

        try {
            // ActionJSON unpacking
            meteorShowerJSON = ((MeteorShowerJSON) data);

            shieldsToActivate = meteorShowerJSON.getShieldsCoordinates();
            doubleCannonsToActivate = meteorShowerJSON.getCannonsCoordinates();

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

            shieldsToActivate = shipPtr.activateComponents(shieldsToActivate);
            activatedShieldsList = shieldsToActivate.stream()
                    .map(Pair::getKey)
                    .map(
                        (ch) -> {
                            return shipPtr.getComponent(ch.getI(), ch.getJ());
                        }
                    )
                    .map(
                        (c) -> {
                            switch (c) {
                                case Shield shield -> {
                                    return shield;
                                }
                                case null, default -> {}
                            }
                            return null;
                        }
                    )
                    .filter(Objects::nonNull)
                    .toList();

            doubleCannonsToActivate = shipPtr.activateComponents(doubleCannonsToActivate);
            activatedDoubleCannonsList = doubleCannonsToActivate.stream()
                    .map(Pair::getKey)
                    .map(
                        (ch) -> {
                            return shipPtr.getComponent(ch.getI(), ch.getJ());
                        }
                    )
                    .map(
                        (c) -> {
                            switch (c) {
                                case Cannon cannon -> {
                                    if (cannon.requiresEnergy()) {
                                        return cannon;
                                    }
                                }
                                case null, default -> {}
                            }
                            return null;
                        }
                    )
                    .filter(Objects::nonNull)
                    .toList();

            List<ComponentHelper<Void>> usedBatteries = new ArrayList<>();

            usedBatteries.addAll(shieldsToActivate.stream().map(Pair::getValue).toList());
            usedBatteries.addAll(doubleCannonsToActivate.stream().map(Pair::getValue).toList());

            this.removedBatteries.put(
                    this.currentPlayer.get().getNickname(),
                    usedBatteries
            );

            // Activate all shields chosen by the player (even if unnecessary)
            if (!activatedShieldsList.isEmpty()) {
                for (Shield activeShield : activatedShieldsList) {
                    int[] shieldCoverage = activeShield.getCoveredSide();

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
            }

            // Considering all single cannons on the ship in
            // addition to the double cannons that the player
            // chose to activate
            List<Cannon> mixedCannons = new ArrayList<>(
                shipPtr.getCannonList().stream()
                    .filter(c -> !c.requiresEnergy())
                    .toList()
            );

            // Adding all double cannons selected by the player and that
            // were successfully activated to the mixed cannons list
            mixedCannons.addAll(activatedDoubleCannonsList);

            // Check if the meteor can be destroyed by at least one of the
            // cannons found in the mixed cannons list
            if (!mixedCannons.isEmpty()) {
                for (Cannon cannon : mixedCannons) {
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

                    Cabin tmpPurpleAlienPos = shipPtr.getPurpleAlienPosition();
                    Cabin tmpBrownAlienPos = shipPtr.getBrownAlienPosition();

                    this.prevPlayerRemovedComponents = shipPtr.removeComponent(
                        toHit.getPosition()[0],
                        toHit.getPosition()[1]
                    );

                    // If there were any aliens that have been removed, add them to the removed lifeForms
                    List<ComponentHelper<LifeformType>> removedAliensList = new ArrayList<>();
                    if (tmpPurpleAlienPos != null && shipPtr.getPurpleAlienPosition() == null) {
                        ComponentHelper<LifeformType> purpleAlienCH = new ComponentHelper<>(tmpPurpleAlienPos.getPosition()[0], tmpPurpleAlienPos.getPosition()[1]);
                        purpleAlienCH.addItem(LifeformType.PURPLE_ALIEN);
                        removedAliensList.add(purpleAlienCH);
                    }
                    if (tmpBrownAlienPos != null && shipPtr.getBrownAlienPosition() == null) {
                        ComponentHelper<LifeformType> brownAlienCH = new ComponentHelper<>(tmpBrownAlienPos.getPosition()[0], tmpBrownAlienPos.getPosition()[1]);
                        brownAlienCH.addItem(LifeformType.BROWN_ALIEN);
                        removedAliensList.add(brownAlienCH);
                    }
                    if (!removedAliensList.isEmpty()) {
                        this.removedLifeforms.put(this.getCurrentPlayer().get().getNickname(), removedAliensList);
                    }

                    this.removedComponents.put(this.prevPlayer, this.prevPlayerRemovedComponents.stream().map(Component::toMap).toList());
                    this.getCurrentPlayer().get().setLostPieces(this.getCurrentPlayer().get().getLostPieces());
                    this.lostPieces.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getLostPieces());
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
            if (this.getBoard().getPlayers().size() > 1) {
                this.currMeteorIndex++;
                this.initCardPlayers();
                this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
            }
            else {
                this.cardUsed();
            }
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
            setUpdatedLostPiecesIfNecessary(cardState, this.lostPieces);

            // Setting the eliminated players (if there are any)
            setUpdatedEliminatedPlayersIfNecessary(cardState, this.eliminatedPlayers);

            // Setting the batteries consumed by the shields and the doubleCannons
            setUpdatedRemovedBatteriesIfNecessary(cardState, this.removedBatteries);

            setUpdatedRemovedLifeformsIfNecessary(cardState, this.removedLifeforms);
        } else {
            cardState.setId(this.cardTypeId);
            cardState.setCardName(this.getCardName());
            cardState.setImagePath(this.path);
            cardState.setCardLevel(this.cardLevel);
        }

        cardState.setCardEnded(this.hasFinished());

        return cardState;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardID(this.getCardID());
        cardState.setId(this.cardTypeId);
        cardState.setCardName(this.getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(this.cardLevel);

        return cardState;
    }

    // Only for testing
    void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }
}
