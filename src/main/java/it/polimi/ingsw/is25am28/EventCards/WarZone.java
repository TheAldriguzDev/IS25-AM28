package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class WarZone extends EventCard {

    // Lowest crew conditions
    private final int takenCrewForLowestCrew;
    private final int takenStorageForLowestCrew;
    private final int movementStepsForLowestCrew;
    private final List<PlasmaShot> shootingSequenceForLowestCrew;

    // Lowest engine power conditions
    private final int takenCrewForLowestEnginePower;
    private final int takenStorageForLowestEnginePower;
    private final int movementStepsForLowestEnginePower;
    private final List<PlasmaShot> shootingSequenceForLowestEnginePower;

    // Lowest firepower conditions
    private final int takenCrewForLowestFirepower;
    private final int takenStorageForLowestFirepower;
    private final int movementStepsForLowestFirepower;
    private final List<PlasmaShot> shootingSequenceForLowestFirepower;

    public WarZone(
            String cardName,
            int cardLevel,
            JSONObject humans,
            JSONObject engines,
            JSONObject cannons,
            Board board
    ) {
        super(cardName, cardLevel, board);

        // Initializing the direction name to value map
        // Precalculated table that associates each direction name to its value
        Map<Integer, String> directionNameToValue = new HashMap<Integer, String>();

        directionNameToValue.put(0, "top");
        directionNameToValue.put(1, "right");
        directionNameToValue.put(2, "bottom");
        directionNameToValue.put(3, "left");

        // Variables
        JSONObject shootingSequenceJSON;
        JSONArray directionSequence;
        int totalDirections = directionNameToValue.size();

        // (1) - Initializing the conditions for the player with the lowest crew
        this.takenCrewForLowestCrew = (int) humans.get("humans");
        this.takenStorageForLowestCrew = (int) humans.get("storage");
        this.movementStepsForLowestCrew = (int) humans.get("days");
        this.shootingSequenceForLowestCrew = new ArrayList<PlasmaShot>();

        shootingSequenceJSON = (JSONObject) humans.get("shoot");

        for (int i = 0; i < totalDirections; i++) {
            directionSequence = (JSONArray) shootingSequenceJSON.get(directionNameToValue.get(i));
            for (Object sizeIndicator : directionSequence) {
                shootingSequenceForLowestCrew.add(new PlasmaShot((Integer) sizeIndicator, i));
            }
        }

        // (2) - Initializing the conditions for the player with the lowest engine power
        this.takenCrewForLowestEnginePower = (int) engines.get("humans");
        this.takenStorageForLowestEnginePower = (int) engines.get("storage");
        this.movementStepsForLowestEnginePower = (int) engines.get("days");
        this.shootingSequenceForLowestEnginePower = new ArrayList<PlasmaShot>();

        shootingSequenceJSON = (JSONObject) humans.get("shoot");

        for (int i = 0; i < totalDirections; i++) {
            directionSequence = (JSONArray) shootingSequenceJSON.get(directionNameToValue.get(i));
            for (Object sizeIndicator : directionSequence) {
                shootingSequenceForLowestEnginePower.add(new PlasmaShot((Integer) sizeIndicator, i));
            }
        }

        // (3) - Initializing the conditions for the player with the lowest firepower
        this.takenCrewForLowestFirepower = (int) cannons.get("humans");
        this.takenStorageForLowestFirepower = (int) cannons.get("storage");
        this.movementStepsForLowestFirepower = (int) cannons.get("days");
        this.shootingSequenceForLowestFirepower = new ArrayList<PlasmaShot>();

        shootingSequenceJSON = (JSONObject) humans.get("shoot");

        for (int i = 0; i < totalDirections; i++) {
            directionSequence = (JSONArray) shootingSequenceJSON.get(directionNameToValue.get(i));
            for (Object sizeIndicator : directionSequence) {
                shootingSequenceForLowestFirepower.add(new PlasmaShot((Integer) sizeIndicator, i));
            }
        }
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        List<Player> lowestCrewPlayers;
        List<Player> lowestEnginePowerPlayers;
        List<Player> lowestFirePowerPlayers;
        List<Pair<Integer, Integer>> shieldCoordsList;
        List<PlasmaShot> plasmaShotSequence;
        List<Cabin> currPlayerCabinList;
        List<Storage> currPlayerStorageList;
        int diceResult, inboundDirection, i;
        int takenCrew, takenStorage;
        boolean threatDestroyed;
        Component[] gridColumn, gridRow;
        Player currPlayer, playerNotAffected;
        Random random;
        Ship shipPtr;
        WarZoneJSON warZone;

        try {
            warZone = (WarZoneJSON) data;
            List<Player> players = new ArrayList<Player>(this.getBoard().getPlayers());

            lowestCrewPlayers = new ArrayList<>();

            for (Player player : players) {
                for (Player other : players) {
                    if (player != other) {
                        if (player.getShip().getAllLifeforms().size() < other.getShip().getAllLifeforms().size()) {
                            lowestCrewPlayers.add(player);
                        }
                    }
                }
            }

            lowestEnginePowerPlayers = new ArrayList<>();

            for (Player player : players) {
                for (Player other : players) {
                    if (player != other) {
                        if (
                            player.getShip().getEnginePower(warZone.getEngineAmountPerPlayer(player))
                                <
                            other.getShip().getEnginePower(warZone.getEngineAmountPerPlayer(other))
                        ) {
                            lowestEnginePowerPlayers.add(player);
                        }
                    }
                }
            }

            lowestFirePowerPlayers = new ArrayList<>();

            for (Player player : players) {
                for (Player other : players) {
                    if (player != other) {
                        if (
                            player.getShip().getFirePower(warZone.getCannonAmountPerPlayer(player))
                                <
                            other.getShip().getFirePower(warZone.getCannonAmountPerPlayer(other))
                        ) {
                            lowestFirePowerPlayers.add(player);
                        }
                    }
                }
            }

            // Initializing variables
            random = new Random();

            for (i = 0; i < 3; i++) {
                switch (i) {
                    // Case 0 - lowestCrewPlayer
                    case 0 -> {
                        plasmaShotSequence = shootingSequenceForLowestCrew;
                        takenCrew = takenCrewForLowestCrew;
                        // movementSteps = movementStepsForLowestCrew;
                        takenStorage = takenStorageForLowestCrew;
                        currPlayer = lowestCrewPlayers.getFirst();
                    }

                    // Case 1 - lowestEnginePowerPlayer
                    case 1 -> {
                        plasmaShotSequence = shootingSequenceForLowestEnginePower;
                        takenCrew = takenCrewForLowestEnginePower;
                        // movementSteps = movementStepsForLowestEnginePower;
                        takenStorage = takenStorageForLowestEnginePower;
                        currPlayer = lowestEnginePowerPlayers.getFirst();
                    }

                    // Case 2 - lowestFirePowerPlayer
                    case 2 -> {
                        plasmaShotSequence = shootingSequenceForLowestFirepower;
                        takenCrew = takenCrewForLowestFirepower;
                        // movementSteps = movementStepsForLowestFirepower;
                        takenStorage = takenStorageForLowestFirepower;
                        currPlayer = lowestFirePowerPlayers.getFirst();
                    }

                    default -> throw new IllegalStateException("ERROR: Card is applied to exactly 3 players");
                }

                // (CONDITION 1) - Take the given amount of crew from the currently selected player
                currPlayerCabinList = currPlayer.getShip().getCabinList();

                for (Cabin cabin : currPlayerCabinList) {
                    while (takenCrew > 0 && !cabin.getInhabitants().isEmpty()) {
                        cabin.removeInhabitant(cabin.getInhabitants().getFirst());
                        takenCrew--;
                    }

                    if (takenCrew == 0) {
                        break;
                    }
                }

                // (CONDITION 2) - Take the given amount of items from the currently selected player
                currPlayerStorageList = currPlayer.getShip().getStorageList();

                for (Storage storage : currPlayerStorageList) {
                    while (takenStorage > 0 && !storage.getStoredItems().isEmpty()) {
                        storage.removeItem(storage.getStoredItems().getFirst());
                        takenStorage--;
                    }

                    if (takenStorage == 0) {
                        break;
                    }
                }

                // (CONDITION 3) - Applying the PlasmaShot sequence to the currently selected player
                for (PlasmaShot currPlasmaShot : plasmaShotSequence) {
                    // Two dice are thrown, result is between 2 and 12
                    diceResult = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);

                    // Adding +2 to the currPlasmaShot's pointing direction gets the
                    // side from where the ship will see it arrive from
                    inboundDirection = (currPlasmaShot.getOrientation() + 2) % 4;

                    // Initializations
                    Component toHit = null;
                    threatDestroyed = false;
                    shipPtr = currPlayer.getShip();

                    switch (inboundDirection) {
                        // Case 1 - PlasmaShot arrives from the TOP
                        case 0 -> {
                            gridColumn = shipPtr.getGridColumn(diceResult - 1);
                            int row = 0;

                            // Iterating the column in search of the side where the PlasmaShot hits
                            // If found, then check for the next components in the column
                            // to see if there are any cannons and/or shields
                            while (toHit == null && row < gridColumn.length) {
                                toHit = gridColumn[row];
                                row++;
                            }
                        }

                        // Case 2 - PlasmaShot arrives from the RIGHT
                        case 1 -> {
                            gridRow = shipPtr.getGridRow(diceResult - 1);
                            int column = 0;

                            // Iterating the column in search of the side where the PlasmaShot hits
                            // If found, then check for the next components in the column
                            // to see if there are any cannons and/or shields
                            while (toHit == null && column < gridRow.length) {
                                toHit = gridRow[column];
                                column++;
                            }
                        }

                        // Case 3 - PlasmaShot arrives from the BOTTOM
                        case 2 -> {
                            gridColumn = shipPtr.getGridColumn(diceResult - 1);
                            int row = gridColumn.length - 1;

                            // Iterating the column in search of the side where the PlasmaShot hits
                            // If found, then check for the next components in the column
                            // to see if there are any cannons and/or shields
                            while (toHit == null && row >= 0) {
                                toHit = gridColumn[row];
                                row--;
                            }
                        }

                        // Case 4 - PlasmaShot arrives from the LEFT
                        case 3 -> {
                            gridRow = shipPtr.getGridRow(diceResult - 1);
                            int column = gridRow.length - 1;

                            // Iterating the column in search of the side where the PlasmaShot hits
                            // If found, then check for the next components in the column
                            // to see if there are any cannons and/or shields
                            while (toHit == null && column >= 0) {
                                toHit = gridRow[column];
                                column--;
                            }
                        }

                        default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
                    }

                    // If there's a component in the path of the PlasmaShot, then elaborate further
                    if (toHit != null) {
                        if (currPlasmaShot.getSize() == 1) {
                            // Case 1 - Small PlasmaShot
                            // => Check if the given shields

                            shieldCoordsList = warZone.getShieldCoordinatesPerPlayer(currPlayer);

                            for (Pair<Integer, Integer> shieldCoords: shieldCoordsList) {
                                Component component = shipPtr.getComponent(
                                        shieldCoords.getKey(),
                                        shieldCoords.getValue()
                                );

                                // Safe cast of Component to Shield
                                switch (component) {
                                    case Shield shield -> {
                                        // Checking if the shield selected for activation
                                        // can actually defend the ship from the small PlasmaShot
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
                        }
                        // else
                        // Case 2 - Big PlasmaShot
                        // => It's unavoidable
                    }
                    // else PLASMA SHOT MISSES THE SHIP

                    // If the PlasmaShot wasn't deflected, then remove the component
                    // that was hit from the current player's ship
                    if (toHit != null && !threatDestroyed) {
                        shipPtr.removeComponent(
                                toHit.getPosition()[0],
                                toHit.getPosition()[1]
                        );
                    }
                }
            }

            // (CONDITION 4) - Move backwards each player, but by starting from the player in last place
            // TODO: Verify if the order with which the players are moved matters
            this.getBoard().movePlayerBackwards(lowestCrewPlayers.getFirst(), movementStepsForLowestCrew);
            this.getBoard().movePlayerBackwards(lowestEnginePowerPlayers.getFirst(), movementStepsForLowestEnginePower);
            this.getBoard().movePlayerBackwards(lowestFirePowerPlayers.getFirst(), movementStepsForLowestFirepower);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: JSON parsing error in WarZone::useCard");
        }

        // Set the hasBeenUsed flag to true
        this.cardUsed();

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.cardLevel);

        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        return cardState;
    }
}
