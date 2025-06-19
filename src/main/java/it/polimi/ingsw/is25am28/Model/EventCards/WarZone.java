package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Model.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;


import java.util.*;
import java.util.stream.Collectors;

public class WarZone extends EventCard {
    // Variables necessary for the card
    private final ResourceBank resourceBank;
    private final int movementSteps;
    private final int requiredCrew;
    private final int requiredItems;
    private final List<PlasmaShot> shootingSequence;
    private final List<WarZoneActionConsequencePair> cardActions;
    private int current_action;

    private Optional<Player> affectedPlayer;
    private Map<Player, Integer> playersEnginePower;
    private Map<Player, Float> playersFirePower;
    private int current_plasmaShot;
    private final Random random;
    private int diceResult;

    // Variables necessary for the generation of the card's state
    private List<Component> previousPlayerRemovedComponents;
    private String prevPlayer;
    private Map<String, List<Map<String, Object>>> removedComponents;
    private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
    private Map<String, List<CoordinatePair>> removedBatteries;
    private Map<String, Integer> updatedPositions;
    private Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms;
    private List<String> eliminatedPlayers;
    private Map<String, Integer> lostPieces;
    private String targetPlayer;

    private String prevPlayerNickname;
    private boolean skipCrewUpdate;

    /**
     * WarZone constructor that sets:
     * - General information about the card (name, level, board)
     * - Specific information about the specs of the card (movementSteps, requiredCrew, shootingSequence)
     * - The order of the action of the card
     * */
    public WarZone(
            String name,
            int level,
            Board board,
            ResourceBank resourceBank,
            int movementSteps,
            int requiredCrew,
            int requiredItems,
            List<PlasmaShot> shootingSequence,
            List<WarZoneActionConsequencePair> cardActions,
            int uniqueCardId,
            String path
    ) {
        super(name, level, board, uniqueCardId, path);

        this.resourceBank = resourceBank;
        this.movementSteps = movementSteps;
        this.requiredCrew = requiredCrew;
        this.requiredItems = requiredItems;
        this.shootingSequence = shootingSequence;
        this.cardActions = cardActions;
        this.current_action = 0;

        this.playersEnginePower = new HashMap<>();
        this.playersFirePower = new HashMap<>();
        this.current_plasmaShot = 0;

        this.random = new Random();
        this.diceResult = generateDiceResult();

        this.affectedPlayer = Optional.empty();

        this.updatedPositions = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.removedLifeforms = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.lostPieces = new HashMap<>();
        this.previousPlayerRemovedComponents = new ArrayList<>();
        this.droppedResources = new HashMap<>();
        this.removedComponents = new HashMap<>();
        this.skipCrewUpdate = true;
    }

    /**
     * Set the currentPlayer to the next player in the game's turn order. If there are no more players left, set the attribute to an empty optional.
     * */
    @Override
    protected Optional<Player> getNextPlayer() {
        if (players == null || players.isEmpty()) {
            throw new Error("Players are not set, you must call initCardPlayers method before");
        }

        if (currentPlayer.isPresent()) {
            int currentIndex = players.indexOf(currentPlayer.get());

            // Handle when the current player is the last one of the playerList
            if (currentIndex == players.size() - 1) {
                // When we played all the possible actions of the card --> mark the card as used
                // Otherwise revalidate the players positions and reset the playerList since the order could be different
                if (current_action == cardActions.size() - 1) {
                    this.cardUsed();
                    return Optional.empty();
                }
                else {
                    // Revalidate the board position and add the lapped players to the eliminated players
                    this.eliminatedPlayers.addAll(this.getBoard().validatePlayersPosition());

                    // Clear the current players and reset them and set the currentPlayer to the first one
                    this.initCardPlayers();
                    // Go to the next action
                    this.current_action++;

                    // If the new player is not connected we skip it to grab the next one
                    if ( !this.currentPlayer.get().isConnected() ) {
                        this.currentPlayer = this.getNextPlayer();
                    }

                    return this.currentPlayer;
                }
            }
            else {
                Player nextPlayer = players.get(currentIndex + 1);
                this.currentPlayer = Optional.of(nextPlayer);

                // If the new player is not connected we skip it to grab the next one
                if ( !this.currentPlayer.get().isConnected() ) {
                    this.currentPlayer = this.getNextPlayer();
                }

                return currentPlayer;
            }
        }
        else {
            this.currentPlayer = Optional.of(players.getFirst());

            // If the new player is not connected we skip it to grab the next one
            if ( !this.currentPlayer.get().isConnected() ) {
                this.currentPlayer = this.getNextPlayer();
            }

            return currentPlayer;
        }
    }

    /**
     * Method that needs to handle the user interaction, more precisely it handles the different types of actions
     * */
    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        WarZoneJSON warZoneJSON;

        try {
            warZoneJSON = (WarZoneJSON) data;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid warZoneJSON");
        }

        // Check if the card can be used by matching the player
        String playerNickname = warZoneJSON.getPlayerNickname();
        if (
            (playerNickname == null)
                || (playerNickname.isEmpty())
                || (!playerNickname.equals(this.getCurrentPlayer().get().getNickname()))
        ) {
            throw new IllegalArgumentException("The given player does not match with the current one!");
        }
        this.prevPlayerNickname = playerNickname;

        // Switch the handling to the specific action
        WarZoneActionConsequencePair currentAction = this.cardActions.get(this.current_action);
        switch (currentAction.getAction()) {
            // Needs to handle multiple user input, and then we can apply the effect
            case FIREPOWER -> {
                this.handleFirePower(currentAction, warZoneJSON);
            }
            // Needs to handle multiple user input, and then we can apply the effect
            case ENGINEPOWER -> {
                this.handleEnginePower(currentAction, warZoneJSON);
            }
            // Does not need to handle the user input to start the game
            case HUMANS -> {
                this.handleHumans(currentAction, warZoneJSON);
            }
            case null, default -> {

            }
        }

        return this;
    }

    /**
     * This method handle two different possible behavior:
     * 1. When it needs the user input it will store them in the map
     * 2. When the affected player is present it will apply the effects to it
     * */
    private WarZone handleFirePower(WarZoneActionConsequencePair warZoneAction, WarZoneJSON warZoneJSON) {
        // If the affected player is present we can execute the effect
        if (this.affectedPlayer != null && this.affectedPlayer.isPresent()) {
            this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
        }
        else {
            // If we do not have the affected player yet, it means that we need to store the given inputs

            if (this.getCurrentPlayer().isPresent()) {
                Player p = this.getCurrentPlayer().get();

                List<Pair<CoordinatePair, CoordinatePair>> activatedDoubleCannons
                        = p.getShip().activateComponents(warZoneJSON.getCannonList());

                float totalFirePower = p.getShip().getFirePower(
                        activatedDoubleCannons.stream()
                                .map(Pair::getKey)
                                .toList()
                );

                this.playersFirePower.put(p, totalFirePower);

                this.removedBatteries.put(
                        this.getCurrentPlayer().get().getNickname(),
                        activatedDoubleCannons.stream()
                                .map(Pair::getValue).toList()
                );

                // Check if we are already arrived to the last player --> In case we need to grab the affected player
                if (this.players.getLast().equals(p)) {
                    Player tmpPlayer = null;
                    float minValue = Integer.MAX_VALUE;

                    // Iterate to get the affected player
                    for (Map.Entry<Player, Float> entry : this.playersFirePower.entrySet()) {
                        if (entry.getValue() < minValue) {
                            tmpPlayer = entry.getKey();
                            minValue = entry.getValue();
                        }
                        else if (tmpPlayer != null && entry.getValue() == minValue && tmpPlayer.getCursor() < entry.getKey().getCursor()) {
                            tmpPlayer = entry.getKey();
                        }
                    }

                    if (tmpPlayer != null) {
                        this.affectedPlayer = Optional.of(tmpPlayer);
//                        this.targetPlayer = this.affectedPlayer.orElse(null).getNickname();
                        this.currentPlayer = Optional.of(tmpPlayer);

                        // If the consequence is MOVEMENTSTEPS --> We can apply them immediately,
                        // for the others we need to wait for user inputs
                        switch (warZoneAction.getConsequence()) {
                            case MOVEMENTSTEPS -> {
                                this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
                                return this;
                            }
                            case null, default -> { }
                        }
                    }
                    else {
                        this.affectedPlayer = Optional.empty();
                    }
                }

                // If we do not have the affected player yet --> we are collecting players inputs
                if (this.affectedPlayer.isEmpty()) {
                    this.getNextPlayer();
                }
            }
        }

        return this;
    }

    /**
     * This method handle two different possible behavior:
     * 1. When it needs the user input it will store them in the map
     * 2. When the affected player is determined it will apply the effects to it
     * */
    private WarZone handleEnginePower(WarZoneActionConsequencePair warZoneAction, WarZoneJSON warZoneJSON) {
        // If the affected player is present we can execute the effect (Will be used when the consequence are the plasma shots)
        if (this.affectedPlayer != null && this.affectedPlayer.isPresent()) {
            this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
        }
        else {
            // If we do not have the affected player yet, it means that we need to store the given inputs
            if (this.getCurrentPlayer().isPresent()) {
                Player p = this.getCurrentPlayer().get();

                List<Pair<CoordinatePair, CoordinatePair>> activatedDoubleEngines
                        = p.getShip().activateComponents(warZoneJSON.getEngineList());

                // Get the total power of the player and store it
                int totalEnginePower = p.getShip().getEnginePower(
                        activatedDoubleEngines.stream()
                                .map(Pair::getKey)
                                .toList()
                );

                this.playersEnginePower.put(p, totalEnginePower);

                this.removedBatteries.put(
                        this.getCurrentPlayer().get().getNickname(),
                        activatedDoubleEngines.stream()
                                .map(Pair::getValue)
                                .toList()
                );

                // Check if we are already arrived to the last player --> In case we need to grab the affected player
                if (this.players.getLast().equals(p)) {
                    Player tmpPlayer = null;
                    float minValue = Integer.MAX_VALUE;

                    // Iterate to get the affected player
                    for (Map.Entry<Player, Integer> entry : this.playersEnginePower.entrySet()) {
                        if (entry.getValue() < minValue) {
                            tmpPlayer = entry.getKey();
                            minValue = entry.getValue();
                        }
                        else if (tmpPlayer != null && entry.getValue() == minValue && tmpPlayer.getCursor() < entry.getKey().getCursor()) {
                            tmpPlayer = entry.getKey();
                        }
                    }

                    if (tmpPlayer != null) {
                        this.affectedPlayer = Optional.of(tmpPlayer);
//                        this.targetPlayer = this.affectedPlayer.orElse(null).getNickname();
                        this.currentPlayer = Optional.of(tmpPlayer);

                        // If the consequence is MOVEMENTSTEPS --> We can apply them immediately,
                        // for the others we need to wait for user inputs
                        switch (warZoneAction.getConsequence()) {
                            case MOVEMENTSTEPS -> {
                                this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
                                return this;
                            }
                            case null, default -> { }
                        }
                    }
                    else {
                        this.affectedPlayer = Optional.empty();
                    }
                }

                // If we do not have the affected player yet --> we are collecting players inputs
                if (this.affectedPlayer.isEmpty()) {
                    this.getNextPlayer();
                }
            }
        }

        return this;
    }

    /**
     * Count all the players humans and apply the specific malus effect to the player that has fewer humans
     * */
    private WarZone handleHumans(WarZoneActionConsequencePair warZoneAction, WarZoneJSON warZoneJSON) {
        // If the affected player is present we can execute the effect (Will be used when the consequence are the plasma shots)
        if (this.affectedPlayer != null && this.affectedPlayer.isPresent()) {
            this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
        }
        else {
            // Get the min lifeform available in the players
            int minLifeForm = this.players.stream()
                    .mapToInt(p -> p.getShip().getAllLifeforms().size())
                    .min()
                    .orElse(0);
            // Get the player that has the fewer lifeform in the game or in case of parity get the leader
            Optional<Player> tmpPlayer = this.players.stream()
                    .filter( p -> p.getShip().getAllLifeforms().size() == minLifeForm)
                    .max( Comparator.comparingInt( Player::getCursor ));

            if (tmpPlayer.isPresent()) {
                this.affectedPlayer = tmpPlayer;
//                this.targetPlayer = this.affectedPlayer.orElse(null).getNickname();
                this.currentPlayer = tmpPlayer;

                // If the consequence is MOVEMENTSTEPS --> We can apply them immediately,
                // for the others we need to wait for user inputs
                switch (warZoneAction.getConsequence()) {
                    case MOVEMENTSTEPS -> {
                        this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
                        return this;
                    }
                    case null, default -> { }
                }
            }
            else {
                this.affectedPlayer = Optional.empty();
            }

            // If we do not have the affected player yet --> we are collecting the users input
            if (this.affectedPlayer.isEmpty()) {
                this.getNextPlayer();
            }
        }

        return this;
    }

    /**
     * Apply the consequence of the card to the affected player.
     * The method getNextPlayer is used to go to the next action or the mark the card as used.
     * */
    private WarZone applyConsequence(Player player, WarZoneJSON warZoneJSON) throws IllegalStateException, NoSuchElementException {
        WarZoneActionConsequencePair currentAction = this.cardActions.get(this.current_action);

        this.targetPlayer = this.affectedPlayer.orElse(null).getNickname();

        switch (currentAction.getConsequence()) {
            case LOSSITEMS -> {
                this.handleLossItems(player, warZoneJSON);

                // Invoke the getNextPlayer with the currentPlayer as the last one to skip to the next action or use the card
                this.affectedPlayer = Optional.empty();
                this.currentPlayer = Optional.of(this.players.getLast());
                this.getNextPlayer();
            }
            case REQUIREDCREW -> {
                this.handleRequiredCrew(player, warZoneJSON);

                // Invoke the getNextPlayer with the currentPlayer as the last one to skip to the next action or use the card
                this.affectedPlayer = Optional.empty();
                this.currentPlayer = Optional.of(this.players.getLast());
                this.getNextPlayer();
            }
            case MOVEMENTSTEPS -> {
                this.getBoard().movePlayerBackward(player, this.movementSteps);

                this.updatedPositions.put(player.getNickname(), player.getCursor());

                // Invoke the getNextPlayer with the currentPlayer as the last one to skip to the next action or to mark the card as used
                this.affectedPlayer = Optional.empty();
                this.currentPlayer = Optional.of(this.players.getLast());
                this.getNextPlayer();
            }
            case SHOOTINGSEQUENCE -> {
                this.handlePlasmaShot(player, warZoneJSON);

                // When we have finished the shooting sequence we can invoke the get next player to skip to the next action or to mark the card as used
                if (this.current_plasmaShot == this.shootingSequence.size() - 1 || this.affectedPlayer.get().isEliminated()) {
                    this.affectedPlayer = Optional.empty();
                    this.currentPlayer = Optional.of(this.players.getLast());
                    this.getNextPlayer();
                } else {
                    this.diceResult = this.generateDiceResult();
                }
                this.current_plasmaShot++;
            }
        }

        return this;
    }

    /**
     * Removes the resources from the given player and add them to the bank.
     * It expects that the input is valid, and it is made by the most precious resources of the player
     * */
    private WarZone handleLossItems(Player player, WarZoneJSON warZoneJSON) throws IllegalArgumentException {
        // Get the list of components and resources to be dropped
        List<ComponentHelper<ItemColor>> itemsToBeRemoved = new ArrayList<>(warZoneJSON.getItemsToBeRemoved());

        // Creates a tmp List of the n=takenItems most valuable item colors in the ship
        List<ItemColor> mostValuableItems = player.getShip().getAllItems().stream()
                .sorted(Comparator.comparingInt(Item::getValue).reversed())
                .limit(this.requiredItems)
                .map(Item::getColor)
                .toList();

        List<ComponentHelper<ItemColor>> resourcesToDrop = warZoneJSON.getItemsToBeRemoved();
        // Extracts the colors form the resourcesToDrop
        List<ItemColor> colorsToDrop = resourcesToDrop.stream()
                .map(item -> item.getItem().orElse(null))
                .toList();

        List<CoordinatePair> stolenBatteries = warZoneJSON.getBatteriesToBeStolen();
        int batteriesToTake = this.requiredItems - resourcesToDrop.size();

        // This covers also the case in which there are not enough resources on board
        if (resourcesToDrop.size() != mostValuableItems.size()) {
            throw new IllegalArgumentException("The dropped items are not enough");
        }
        else if (this.countOccurrencies(mostValuableItems).equals(colorsToDrop)) {
            this.targetPlayer = this.affectedPlayer.orElse(null).getNickname();
            throw new IllegalArgumentException("The dropped items do not correspond to the most valuable items on board");
        }
        else if ((stolenBatteries.size() != batteriesToTake) && player.getShip().getAvailableEnergy() != stolenBatteries.size()) {
            // This exception is triggered only if a wrong number of batteries is sent, the case in which the player cannot select the required number of batteries is checked
            throw new IllegalArgumentException("The given up batteries are not enough!");
        }
        else if (stolenBatteries.size() > batteriesToTake) {
            throw new IllegalArgumentException("You didn't remove the right amount of batteries, please try again");
        }

        // This check cannot be made, if the list sent by the player is smaller than requiredItems, the player's batteries must be taken instead
//        if (itemsToBeRemoved.size() != this.requiredItems) {
//            throw new IllegalArgumentException("The itemsToBeRemoved size does not match with the card requirements!");
//        }

        if (!itemsToBeRemoved.isEmpty()) {
            this.droppedResources.put(player.getNickname(), itemsToBeRemoved);
        }

        if (!stolenBatteries.isEmpty()) {
            this.removedBatteries.put(player.getNickname(), stolenBatteries);
        }

        // Remove the resources from the player to the bank
        for ( ComponentHelper<ItemColor> resourceDrop : itemsToBeRemoved ) {
            resourceDrop.getItem().ifPresent( i ->
                    this.resourceBank.addResourceToBankFromPlayer(
                            player,
                            i,
                            resourceDrop.getI(),
                            resourceDrop.getJ()));
        }

        List<CoordinatePair> consumedBatteries = new ArrayList<>();

        if (batteriesToTake > 0) {
            // Removing 1 unit of charge from each battery selected by the player
            for (CoordinatePair coords : warZoneJSON.getBatteriesToBeStolen()) {
                Component component = player.getShip().getComponent(
                        coords.getI(),
                        coords.getJ()
                );

                switch (component) {
                    case Battery battery -> {
                        consumedBatteries.add(coords);
                        batteriesToTake--;
                    }
                    case null, default -> {}
                }
            }

            // Consuming each battery by 1 unit of charge
            player.getShip().consumeEnergy(consumedBatteries);

            // Logging the consumed batteries for the current player
            this.removedBatteries.put(player.getNickname(), consumedBatteries);
        }

        return this;
    }

    private Map<ItemColor, Integer> countOccurrencies(List<ItemColor> colors) {
        Map<ItemColor, Integer> occurrencies = new HashMap<>();

        for(ItemColor itemColor : colors) {
            occurrencies.put(itemColor, occurrencies.getOrDefault(itemColor, 0) + 1);
        }

        return occurrencies;
    }

    /**
     * Removes the lifeform of the given player from the given Cabin component.
     * It also checks if the player hash finished all his Astronauts --> if yes it will be eliminated
     * */
    private WarZone handleRequiredCrew(Player player, WarZoneJSON warZoneJSON) {
        // Get the list of components where we need to remove the lifeForm of the given player
        List<ComponentHelper<LifeformType>> lifeFormToBeRemoved = new ArrayList<>(warZoneJSON.getLifeformsToBeRemoved());

        if (lifeFormToBeRemoved.size() != this.requiredCrew && lifeFormToBeRemoved.size() != player.getShip().getAllLifeforms().size()) {
            throw new IllegalArgumentException("The removed crew members are not enough!");
        } else if (lifeFormToBeRemoved.size() > this.requiredCrew) {
            throw new IllegalArgumentException("You didn't remove the right amount of crew members, please try again");
        }

        this.removedLifeforms.put(player.getNickname(), lifeFormToBeRemoved);

        Ship playerShip = player.getShip();

        for (ComponentHelper<LifeformType> lifeform : lifeFormToBeRemoved) {
            playerShip.removeLifeformFromCabin(
                lifeform.getI(),
                lifeform.getJ(),
                lifeform.getItem().get()
            );
        }

        // Check if the player has finished all of its astronauts --> if yes it needs to be eliminated from the game
        if (playerShip.getCabinList().stream().flatMap(c -> c.getInhabitants().stream()).noneMatch(i -> i.getLifeformType().equals(LifeformType.ASTRONAUT))) {
            this.eliminatedPlayers.add(player.getNickname());
            this.getBoard().eliminatePlayer(player);
        }

        return this;
    }

    private WarZone handlePlasmaShot(Player player, WarZoneJSON warZoneJSON) {
        int inboundDirection;
        boolean threatDestroyed;
        Component[] gridRow;
        Component[] gridColumn;
        List<Pair<CoordinatePair, CoordinatePair>> shieldsToActivate;
        List<Shield> activatedShieldsList;
        Component toHit;
        Ship shipPtr;
        PlasmaShot currPlasmaShot;

        // Initializing variables
        toHit = null;
        threatDestroyed = false;

        shipPtr = player.getShip();
        currPlasmaShot = this.shootingSequence.get(this.current_plasmaShot);
        shieldsToActivate = warZoneJSON.getShieldList();

        // Filtering out all coordinates that don't point to a shield.
        shieldsToActivate = shieldsToActivate.stream()
                .filter(Objects::nonNull)
                .filter(
                    (pair) -> {
                        CoordinatePair shieldCoords = pair.getKey();
                        Component component = shipPtr.getComponent(shieldCoords.getI(), shieldCoords.getJ());

                        return switch (component) {
                            case Shield shield -> true;
                            case null, default -> false;
                        };
                    }
                ).toList();

        // Activating shields (which consumes 1 energy unit from the battery each shield is paired with)
        shieldsToActivate = shipPtr.activateComponents(shieldsToActivate);

        // NOTE: The cast is safe thanks to the previous check
        activatedShieldsList = shieldsToActivate.stream()
                .map(Pair::getKey)
                .map(p -> (Shield) shipPtr.getComponent(p.getI(), p.getJ()))
                .toList();

        this.removedBatteries.put(
                player.getNickname(),
                shieldsToActivate.stream()
                        .map(Pair::getValue).toList()
        );

        // Adding +2 to the currMeteor's pointing direction gets the
        // side from where the ship will see it arrive from
        inboundDirection = (currPlasmaShot.getOrientation());

        // Determine if and what component will be hit by the shot
        switch (inboundDirection) {
            // Case 1 - Meteor arrives from the TOP
            case 0 -> {
                gridColumn = shipPtr.getGridColumn(this.diceResult - 1);
                int row = 0;

                // Iterating the column in search of the side where the meteor hits
                // If found, then check for the next components in the column
                // to see if there are any cannons and/or shields
                while (toHit == null && row < gridColumn.length) {
                    toHit = gridColumn[row];
                    row++;
                }

                if (toHit == null) return this;
            }

            // Case 2 - Meteor arrives from the RIGHT
            case 1 -> {
                gridRow = shipPtr.getGridRow(this.diceResult - 1);
                int column = gridRow.length - 1;

                // Iterating the column in search of the side where the meteor hits
                // If found, then check for the next components in the column
                // to see if there are any cannons and/or shields
                while (toHit == null && column >= 0) {
                    toHit = gridRow[column];
                    column--;
                }

                if (toHit == null) return this;
            }

            // Case 3 - Meteor arrives from the BOTTOM
            case 2 -> {
                gridColumn = shipPtr.getGridColumn(this.diceResult - 1);
                int row = gridColumn.length - 1;

                // Iterating the column in search of the side where the meteor hits
                // If found, then check for the next components in the column
                // to see if there are any cannons and/or shields
                while (toHit == null && row >= 0) {
                    toHit = gridColumn[row];
                    row--;
                }

                if (toHit == null) return this;
            }

            // Case 4 - Meteor arrives from the LEFT
            case 3 -> {
                gridRow = shipPtr.getGridRow(this.diceResult - 1);
                int column = 0;

                // Iterating the column in search of the side where the meteor hits
                // If found, then check for the next components in the column
                // to see if there are any cannons and/or shields
                while (toHit == null && column < gridRow.length) {
                    toHit = gridRow[column];
                    column++;
                }

                if (toHit == null) return this;
            }

            default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
        }

        // Check if the shields can stop the threat
        if (currPlasmaShot.getSize() == 1) {
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
        }

        // that was hit from the current player's ship
        if (!threatDestroyed) {
            try {
                Cabin tmpPurpleAlienPos = shipPtr.getPurpleAlienPosition();
                Cabin tmpBrownAlienPos = shipPtr.getBrownAlienPosition();

                this.previousPlayerRemovedComponents = shipPtr.removeComponent(toHit.getPosition()[0], toHit.getPosition()[1]);

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
                    this.skipCrewUpdate = false;
                }

                this.prevPlayer = player.getNickname();
                this.removedComponents.put(this.prevPlayer, this.previousPlayerRemovedComponents.stream().map(Component::toMap).toList());
                this.getCurrentPlayer().get().setLostPieces(this.getCurrentPlayer().get().getLostPieces() + previousPlayerRemovedComponents.size());
                this.lostPieces.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getLostPieces());
            }
            catch (CoreDeletionAttemptException e) {
                this.eliminatedPlayers.add(player.getNickname());
                this.getBoard().eliminatePlayer(player);
            }
        }

        return this;
    }

    /**
     * @return A number between [2, 12] representing the result
     *         of throwing two D6 (D6 = 6-faced dice)
     */
    private int generateDiceResult() {
        return (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
    }

    /**
     * When there is the affected player we need to retrieve information about the consequence
     * Instead, when there is no affectedPlayer we need to retrieve information about the action
     * */
    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON cardState = new CardStateJSON();

        cardState.setUniqueCardId(this.uniqueCardId);

        if (this.hasBeenActivated()) {
            initStateFlags(cardState);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));
            cardState.setPrevPlayerNickname(this.prevPlayerNickname);
            // The prevPlayer's batteries are always updated locally in this card
            cardState.setSkipBatteriesUpdate(true);
            // The prevPlayer's storages are always updated locally in this card
            cardState.setSkipStoragesUpdate(true);
            // The prevPlayer's storages are always updated locally,
            // except for when an alien is eliminated following the destruction of the corresponding vital
            cardState.setSkipCrewUpdate(this.skipCrewUpdate);
            this.skipCrewUpdate = true;

            cardState.setCurrActionIndex(this.current_action); // Need a way to set this only when necessary, but it might not be worth it // should now be obsolete since there are flags
            // If present set the current player (the one that needs to play the game)

            if(this.affectedPlayer.orElse(null) != null) {
                cardState.setAffectedPlayer(this.affectedPlayer.get().getNickname());

                if (this.cardActions.get(this.current_action).getConsequence().equals(WarZoneConsequence.SHOOTINGSEQUENCE)) {
                    PlasmaShot currPlasmaShot = this.shootingSequence.get(this.current_plasmaShot);
                    cardState.setCurrPlasmaShotDescriptor(Map.of("shotSize", currPlasmaShot.getSize(), "shotDirection", currPlasmaShot.getOrientation()));
                    cardState.setDiceThrowResult(this.diceResult);
                }
            }

            if (this.targetPlayer != null && !this.targetPlayer.isEmpty()) {
                this.targetPlayer = null;

                setUpdatedRemovedBatteriesIfNecessary(cardState, this.removedBatteries);
                setUpdatedPositionsIfNecessary(cardState, this.updatedPositions);
                setUpdatedRemovedLifeformsIfNecessary(cardState, removedLifeforms);
                setUpdatedDroppedResourcesIfNecessary(cardState, this.droppedResources);
                setUpdatedEliminatedPlayersIfNecessary(cardState, this.eliminatedPlayers);
                setUpdatedRemovedComponentsIfNecessary(cardState, this.removedComponents);
                setUpdatedLostPiecesIfNecessary(cardState, this.lostPieces);
            }
            else {
                setUpdatedRemovedBatteriesIfNecessary(cardState, this.removedBatteries);
            }
        }
        else {
            cardState.setCardTypeId(this.cardTypeId);
            cardState.setCardName(this.getCardName());
            cardState.setCardLevel(this.cardLevel);
            cardState.setImagePath(this.path);

            List<List<String>> actionsAndConsequences = new ArrayList<>();
            for (WarZoneActionConsequencePair pair : this.cardActions) {
                actionsAndConsequences.add(Arrays.asList(pair.getAction().toString(), pair.getConsequence().toString()));
            }
            cardState.setActionsAndConsequences(actionsAndConsequences);
            cardState.setRequiredCrewMembers(this.requiredCrew);
            cardState.setMovementSteps(this.movementSteps);
            cardState.setRequiredResources(this.requiredItems);

            if (current_plasmaShot < this.shootingSequence.size()) {
                PlasmaShot currPlasmaShot = this.shootingSequence.get(this.current_plasmaShot);
                cardState.setCurrPlasmaShotDescriptor(Map.of("shotSize", currPlasmaShot.getSize(), "shotDirection", currPlasmaShot.getOrientation()));
            }
            cardState.setDiceThrowResult(this.diceResult);
        }

        cardState.setCardEnded(this.hasFinished());

        return cardState;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardTypeId(this.cardTypeId);
        cardState.setUniqueCardId(this.uniqueCardId);
        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.cardLevel);
        cardState.setImagePath(this.path);

        List<List<String>> actionsAndConsequences = new ArrayList<>();

        for (WarZoneActionConsequencePair pair : this.cardActions) {
            actionsAndConsequences.add(Arrays.asList(pair.getAction().toString(), pair.getConsequence().toString()));
        }

        cardState.setActionsAndConsequences(actionsAndConsequences);
        cardState.setRequiredCrewMembers(this.requiredCrew);
        cardState.setMovementSteps(this.movementSteps);
        cardState.setRequiredResources(this.requiredItems);
        cardState.setImagePath(this.path);

        return cardState;
    }

    //Only for testing purposes
    public void forceDiceThrow(int result) {
        this.diceResult = result + 1;
    }
}
