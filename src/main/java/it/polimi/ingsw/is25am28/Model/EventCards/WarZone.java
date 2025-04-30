package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cannon;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Shield;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Model.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Model.Exceptions.InsufficientEnergyException;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;


import java.util.*;
import java.util.stream.Collectors;

public class WarZone extends EventCard {
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

    private List<Component> previousPlayerRemovedComponents;
    private String prevPlayer;
    private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
    private Map<String, Integer> removedBatteries;
    private Map<String, Integer> updatedPositions;
    private Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms;
    private List<String> eliminatedPlayers;

    private final Random random;
    private int diceResult;

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
            List<WarZoneActionConsequencePair> cardActions
    ) {
        super(name, level, board);
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
                } else {
                    // Revalidate the board position
                    this.getBoard().validatePlayersPosition();
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
            } else {
                Player nextPlayer = players.get(currentIndex + 1);
                this.currentPlayer = Optional.of(nextPlayer);

                // If the new player is not connected we skip it to grab the next one
                if ( !this.currentPlayer.get().isConnected() ) {
                    this.currentPlayer = this.getNextPlayer();
                }

                return currentPlayer;
            }
        } else {
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
        } catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid warZoneJSON");
        }

        // Check if the card can be used by matching the player
        String playerNickname = warZoneJSON.getPlayerNickname();
        if ( playerNickname == null ||
                playerNickname.isEmpty() ||
                !playerNickname.equals(this.getCurrentPlayer().get().getNickname()) ) {
            throw new IllegalArgumentException("The given player does not match with the current one!");
        }

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

        return null;
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
        } else {
            // If we do not have the affected player yet, it means that we need to store the given inputs

            if (this.getCurrentPlayer().isPresent()) {
                Player p = this.getCurrentPlayer().get();

                float totalPower = 0;

                // Compute the power of the normal cannons
                totalPower += p.getShip().getFirePower(new ArrayList<>()); // replaced 0 with an empty arrayList to now work with the new version of getFirepower

                // Get the cannonList (double cannons that gets activated) to compute the power
                List<ComponentHelper<Integer>> cannonList = warZoneJSON.getCannonList();
                for (ComponentHelper<Integer> c : cannonList) {
                    Cannon tmpCannon = (Cannon) p.getShip().getComponent(c.getI(), c.getJ());

                    totalPower += tmpCannon.getFirePower();
                    p.getShip().consumeEnergy(1);
                }

                //  float totalPower = p.getShip().getFirePower(usedEnergy);
                this.playersFirePower.put(p, totalPower);

                // Check if we are already arrived to the last player --> In case we need to grab the affected player
                if (this.players.getLast().equals(p)) {
                    Player tmpPlayer = null;
                    float minValue = Integer.MAX_VALUE;

                    // Iterate to get the affected player
                    for (Map.Entry<Player, Float> entry : this.playersFirePower.entrySet()) {
                        if (entry.getValue() < minValue) {
                            tmpPlayer = entry.getKey();
                            minValue = entry.getValue();
                        } else if (tmpPlayer != null && entry.getValue() == minValue && tmpPlayer.getCursor() < entry.getKey().getCursor()) {
                            tmpPlayer = entry.getKey();
                        }
                    }
                    if (tmpPlayer != null) {
                        this.affectedPlayer = Optional.of(tmpPlayer);
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
                    } else {
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
        } else {
            // If we do not have the affected player yet, it means that we need to store the given inputs

            if (this.getCurrentPlayer().isPresent()) {
                Player p = this.getCurrentPlayer().get();

                // Get the total power of the player and store it
                int usedEnergy = warZoneJSON.getUsedEnergy();
                int totalEnginePower = p.getShip().getEnginePower(usedEnergy);
                this.playersEnginePower.put(p, totalEnginePower);

                // Check if we are already arrived to the last player --> In case we need to grab the affected player
                if (this.players.getLast().equals(p)) {
                    Player tmpPlayer = null;
                    float minValue = Integer.MAX_VALUE;

                    // Iterate to get the affected player
                    for (Map.Entry<Player, Integer> entry : this.playersEnginePower.entrySet()) {
                        if (entry.getValue() < minValue) {
                            tmpPlayer = entry.getKey();
                            minValue = entry.getValue();
                        } else if (tmpPlayer != null && entry.getValue() == minValue && tmpPlayer.getCursor() < entry.getKey().getCursor()) {
                            tmpPlayer = entry.getKey();
                        }
                    }

                    if (tmpPlayer != null) {
                        this.affectedPlayer = Optional.of(tmpPlayer);
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
                    } else {
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
        } else {
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
            } else {
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
                this.getBoard().movePlayerBackwards(player, this.movementSteps);
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
        this.droppedResources = Map.of(player.getNickname(), itemsToBeRemoved);

        // This check cannot be made, if the list sent by the player is smaller than requiredItems, the player's batteries must be taken instead
//        if (itemsToBeRemoved.size() != this.requiredItems) {
//            throw new IllegalArgumentException("The itemsToBeRemoved size does not match with the card requirements!");
//        }

        // Remove the resources from the player to the bank
        for ( ComponentHelper<ItemColor> resourceDrop : itemsToBeRemoved ) {
            resourceDrop.getItem().ifPresent( i ->
                    this.resourceBank.addResourceToBankFromPlayer(
                            player,
                            i,
                            resourceDrop.getI(),
                            resourceDrop.getJ()));
        }

        // Le batterie da rimuovere solo nel caso la lista di elementi da rimuovere non isa abbastanza grande, il client farà il controllo di fare la lista di elementi da togliore il piu grande possibile se non è possibile raggiungere una grandezza pari a takenItems
        if (player.getShip().getAvailableEnergy() >= (requiredItems - itemsToBeRemoved.size())) {
            this.removedBatteries = Map.of(player.getNickname(),requiredItems - itemsToBeRemoved.size());
            player.getShip().consumeEnergy(requiredItems - itemsToBeRemoved.size());
        } else {
            this.removedBatteries = Map.of(player.getNickname(),player.getShip().getAvailableEnergy());
            player.getShip().consumeEnergy(player.getShip().getAvailableEnergy());
        } // Se viene presa più energia di quanta ne è disponibile semplicemente va a 0

        return this;
    }

    /**
     * Removes the lifeform of the given player from the given Cabin component.
     * It also checks if the player hash finished all his Astronauts --> if yes it will be eliminated
     * */
    private WarZone handleRequiredCrew(Player player, WarZoneJSON warZoneJSON) {
        // Get the list of components where we need to remove the lifeform of the given player
        List<ComponentHelper<LifeformType>> lifeFormToBeRemoved = new ArrayList<>(warZoneJSON.getLifeformsToBeRemoved());
        this.removedLifeforms.put(player.getNickname(), lifeFormToBeRemoved);

        if (lifeFormToBeRemoved.size() != this.requiredCrew) {
            throw new IllegalArgumentException("The lifeformsToBeRemoved size does not match with the card requirements!");
        }

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

        int inboundDirection, sideToHit;
        boolean threatDestroyed;
        Component[] gridRow;
        Component[] gridColumn;
        List<ComponentHelper<Integer>> shieldList;
        Component toHit;
        Ship shipPtr;
        PlasmaShot currPlasmaShot;

        // Initializing variables
        toHit = null;
        threatDestroyed = false;
        sideToHit = -1;

        // Grab for the affected player the input about the cannon and shield to activate
        shieldList = warZoneJSON.getShieldList();
        shipPtr = player.getShip();
        currPlasmaShot = this.shootingSequence.get(this.current_plasmaShot);

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

                if (toHit == null) break;
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

                if (toHit == null) break;
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

                if (toHit == null) break;
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

                if (toHit == null) break;
            }

            default -> throw new IllegalStateException("ERROR: Only 4 directions allowed");
        }

        // If a component has been found
        if (toHit != null) {
            if (shieldList != null) {
                for (ComponentHelper<Integer> shieldCoords : shieldList) {
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
                                        if (currPlasmaShot.getSize() == 1 && j == inboundDirection) {
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

        // If the meteor wasn't destroyed, then remove the component
        // that was hit from the current player's ship
        if (toHit != null && !threatDestroyed) {
            try {
                shipPtr.removeComponent(
                        toHit.getPosition()[0],
                        toHit.getPosition()[1]
                );
                this.prevPlayer = player.getNickname();
                this.previousPlayerRemovedComponents = new ArrayList<>();
            } catch (CoreDeletionAttemptException e) {
                this.eliminatedPlayers.add(player.getNickname());
                this.getBoard().eliminatePlayer(player);
            }
        }

        return this;
    }

    /**
     * Generate the dice result
     * */
    private int generateDiceResult() {
        return (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        //return 7; // Column 6
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    /**
     * When there is the affected player we need to retrieve information about the consequence
     * Instead, when there is no affectedPlayer we need to retrieve information about the action
     * */
    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setNeedsBoardUpdate(false);
        cardState.setNeedsPlayerUpdate(false);
        cardState.setNeedsShipUpdate(false);

        if (this.hasBeenActivated()) {
            cardState.setApplyMovementStepsConsequence(false);
            cardState.setApplyRequiredCrewConsequences(false);
            cardState.setApplyLossItemsConsequence(false);
            cardState.setApplyShootingSequenceConsequence(false);

            if (this.getCurrentPlayer().isPresent()) {
                cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
            }
            cardState.setCurrActionIndex(this.current_action); // Need a way to set this only when necessary, but it might not be worth it // should now be obsolete since there are flags
            // If present set the current player (the one that needs to play the game)
            if (this.affectedPlayer != null && this.affectedPlayer.isPresent()) {
                cardState.setAffectedPlayer(this.affectedPlayer.get().getNickname());
                switch (this.cardActions.get(current_action).getConsequence()) {
                    case REQUIREDCREW -> {
                        if (!this.removedLifeforms.isEmpty()) {
                            cardState.setNeedsShipUpdate(true);
                            cardState.setNeedsUpdatedRemovedLifeforms(true);
                            cardState.setRemovedLifeforms(this.removedLifeforms);
                        }
                        if (!this.eliminatedPlayers.isEmpty()) {
                            cardState.setNeedsBoardUpdate(true);
                            cardState.setNeedsUpdatedEliminatedPlayers(true);
                            cardState.setEliminatedPlayers(this.eliminatedPlayers);
                            this.eliminatedPlayers.clear();
                        }

                    }
                    case MOVEMENTSTEPS -> {
                        if(!this.updatedPositions.isEmpty()) {
                            cardState.setNeedsBoardUpdate(true);
                            cardState.setNeedsUpdatedPositions(true);
                            cardState.setUpdatedPositions(this.updatedPositions);
                        }
                    }
                    case SHOOTINGSEQUENCE -> {
                        if(!this.previousPlayerRemovedComponents.isEmpty()) {
                            cardState.setNeedsShipUpdate(true);
                            cardState.setPreviousPlayerRemovedComponents(Map.of(this.prevPlayer, this.previousPlayerRemovedComponents.stream().map(Component::toMap).toList()));
                            // TODO: NEW FLAG SYSTEM NEEDED (needsComponentsUpdate)
                        }
                        if (!this.eliminatedPlayers.isEmpty()) {
                            cardState.setNeedsBoardUpdate(true);
                            cardState.setNeedsUpdatedEliminatedPlayers(true);
                            cardState.setEliminatedPlayers(this.eliminatedPlayers);
                            this.eliminatedPlayers.clear();
                        }
                    }
                    case LOSSITEMS -> {
                        // TODO: neede revision on the use of isEmpty on maps/mapsOf, it should might (probably) be wrong // can fix it with get(nickname).isEmpty
                        if (!this.droppedResources.isEmpty()) {
                            cardState.setNeedsShipUpdate(true);
                            cardState.setNeedsUpdatedDroppedResources(true);
                            cardState.setDroppedResources(droppedResources);
                        } else {
                            cardState.setNeedsUpdatedDroppedResources(false);
                        }
                        if (!this.removedBatteries.isEmpty()) {
                            cardState.setNeedsShipUpdate(true);
                            cardState.setNeedsUpdatedBatteries(true);
                            cardState.setRemovedBatteries(removedBatteries);
                        }
                    }
                }
            } else {
                cardState.setAffectedPlayer("");
            }


        } else {
            cardState.setId(this.id);
            // Set the card name
            cardState.setCardName(this.getCardName());
            // Set the card level
            cardState.setCardLevel(this.cardLevel);

            Map<String, String> actionsAndConsequences = new HashMap<>();
            for (WarZoneActionConsequencePair pair : this.cardActions) {
                actionsAndConsequences.put(pair.getAction().toString(), pair.getConsequence().toString());
            }
            cardState.setRequiredCrewMembers(this.requiredCrew);
            cardState.setMovementSteps(this.movementSteps);
            cardState.setRequiredResources(this.requiredItems);


        }

        PlasmaShot currPlasmaShot = this.shootingSequence.get(this.current_plasmaShot);
        cardState.setCurrPlasmaShotDescriptor(Map.of("shotSize", currPlasmaShot.getSize(), "shotDirection", currPlasmaShot.getOrientation()));//<>(currPlasmaShot.getSize(), currPlasmaShot.getOrientation()));
        cardState.setDiceThrowResult(this.diceResult);

        Map<String, Float> playersFirePowerMap = this.playersFirePower.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getNickname(),
                        Map.Entry::getValue
                ));

        Map<String, Integer> playersEnginePowerMap = this.playersEnginePower.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getNickname(),
                        Map.Entry::getValue
                ));

        cardState.setPlayersFirePower(playersFirePowerMap);
        cardState.setPlayersEnginePower(playersEnginePowerMap);

        return cardState;
    }

    public WidgetTUI generateWidget(CardStateJSON warZoneJSON) {
        return null;
    }

    //Only for testing purposes
    public void forceDiceThrow(int result) {
        this.diceResult = result + 1;
    }
}
