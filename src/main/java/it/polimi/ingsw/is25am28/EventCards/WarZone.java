package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Ship.Ship;

import java.util.*;

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
                if (current_action == shootingSequence.size() - 1) {
                    this.cardUsed();
                    return Optional.empty();
                } else {
                    // Revalidate the board position
                    this.getBoard().validatePlayersPosition();
                    // Clear the current players and reset them and set the currentPlayer to the first one
                    this.players.clear();
                    this.players.addAll(this.getBoard().getPlayers());
                    this.currentPlayer = Optional.of(this.players.getFirst());

                    // Go to the next action
                    this.current_action++;
                    return this.currentPlayer;
                }
            } else {
                Player nextPlayer = players.get(currentIndex + 1);
                currentPlayer = Optional.of(nextPlayer);
                return currentPlayer;
            }
        } else {
            currentPlayer = Optional.of(players.getFirst());
            return currentPlayer;
        }
    }

    /**
     * Methods that needs to handle the user interaction, more precisely it handles the different type of action
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

        // Use the card with the specific action
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
     * 2. When the affected player is determined it will apply the effects to it
     * */
    private WarZone handleFirePower(WarZoneActionConsequencePair warZoneAction, WarZoneJSON warZoneJSON) {
        // If the affected player is present we can execute the effect (Will be used when the consequence are the plasma shots)
        if (this.affectedPlayer.isPresent()) {
            this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
        } else {
            // If we do not have the affected player yet, it means that we need to store the given inputs

            this.getCurrentPlayer().ifPresent( p -> {
                // Get the total power of the player and store it
                int usedEnergy = warZoneJSON.getUsedEnergy();
                float totalPower = p.getShip().getFirePower(usedEnergy);
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

                        // If the consequence is one of MOVEMENTSTEPS - REQUIREDCREW - LOSSITEMS --> We can apply them immediately,
                        // for the SHOOTINGSEQUENCE we need to wait for player inputs
                        switch (warZoneAction.getConsequence()) {
                            case MOVEMENTSTEPS, REQUIREDCREW, LOSSITEMS -> this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
                            case null, default -> { }
                        }
                    } else {
                        this.affectedPlayer = Optional.empty();
                    }
                }
            });
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
        if (this.affectedPlayer.isPresent()) {
            this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
        } else {
            // If we do not have the affected player yet, it means that we need to store the given inputs

            this.getCurrentPlayer().ifPresent( p -> {
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

                        // If the consequence is one of MOVEMENTSTEPS - REQUIREDCREW - LOSSITEMS --> We can apply them immediately,
                        // for the SHOOTINGSEQUENCE we need to wait for player inputs
                        switch (warZoneAction.getConsequence()) {
                            case MOVEMENTSTEPS, REQUIREDCREW, LOSSITEMS -> this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
                            case null, default -> { }
                        }
                    } else {
                        this.affectedPlayer = Optional.empty();
                    }
                }
            });
        }

        return this;
    }

    /**
     * Count all the players humans and apply the specific malus effect to the player that has fewer humans
     * */
    private WarZone handleHumans(WarZoneActionConsequencePair warZoneAction, WarZoneJSON warZoneJSON) {
        // If the affected player is present we can execute the effect (Will be used when the consequence are the plasma shots)
        if (this.affectedPlayer.isPresent()) {
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

                // If the consequence is one of MOVEMENTSTEPS - REQUIREDCREW - LOSSITEMS --> We can apply them immediately,
                // for the SHOOTINGSEQUENCE we need to wait for player inputs
                switch (warZoneAction.getConsequence()) {
                    case MOVEMENTSTEPS, REQUIREDCREW, LOSSITEMS -> this.applyConsequence(this.affectedPlayer.get(), warZoneJSON);
                    case null, default -> { }
                }
            } else {
                this.affectedPlayer = Optional.empty();
            }
        }

        return this;
    }

    /**
     * Apply the consequence of the card to a specific player
     * */
    private WarZone applyConsequence(Player player, WarZoneJSON warZoneJSON) throws IllegalStateException, NoSuchElementException {
        WarZoneActionConsequencePair currentAction = this.cardActions.get(this.current_action);

        switch (currentAction.getConsequence()) {
            case LOSSITEMS -> {
                this.handleLossItems(player, warZoneJSON);

                // Invoke the getNextPlayer with the currentPlayer as the last one to skip to the next action or use the card
                this.currentPlayer = Optional.of(this.players.getLast());
                this.getNextPlayer();
            }
            case REQUIREDCREW -> {
                this.handleRequiredCrew(player, warZoneJSON);

                // Invoke the getNextPlayer with the currentPlayer as the last one to skip to the next action or use the card
                this.currentPlayer = Optional.of(this.players.getLast());
                this.getNextPlayer();
            }
            case MOVEMENTSTEPS -> {
                this.getBoard().movePlayerBackwards(player, this.movementSteps);

                // Invoke the getNextPlayer with the currentPlayer as the last one to skip to the next action or to mark the card as used
                this.currentPlayer = Optional.of(this.players.getLast());
                this.getNextPlayer();
            }
            case SHOOTINGSEQUENCE -> {
                this.handlePlasmaShot(player, warZoneJSON);

                // When we have finished the shooting sequence we can invoke the get next player to skip to the next action or to mark the card as used
                if (this.current_plasmaShot == this.shootingSequence.size() - 1) {
                    this.currentPlayer = Optional.of(this.players.getLast());
                    this.getNextPlayer();
                }
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

        if (itemsToBeRemoved.size() != this.requiredItems) {
            throw new IllegalArgumentException("The itemsToBeRemoved size does not match with the card requirements!");
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

        return this;
    }

    /**
     * Removes the lifeform of the given player from the given Cabin component.
     * It also checks if the player hash finished all his Astronauts --> if yes it will be eliminated
     * */
    private WarZone handleRequiredCrew(Player player, WarZoneJSON warZoneJSON) {
        // Get the list of components where we need to remove the lifeform of the given player
        List<ComponentHelper<LifeformType>> lifeFormToBeRemoved = new ArrayList<>(warZoneJSON.getLifeformsToBeRemoved());

        if (lifeFormToBeRemoved.size() != this.requiredCrew) {
            throw new IllegalArgumentException("The lifeformsToBeRemoved size does not match with the card requirements!");
        }

        Ship playerShip = player.getShip();

        for (ComponentHelper<LifeformType> lifeform : lifeFormToBeRemoved) {

            Cabin tmpCabin;
            try {
                tmpCabin = (Cabin) playerShip.getComponent(lifeform.getI(), lifeform.getJ());
            } catch (Exception e) {
                throw new IllegalStateException("The given component is not a valid cabin");
            }

            lifeform.getItem().ifPresent( l -> {
                Lifeform tmpLifeFormToBeRemoved = tmpCabin.getInhabitants().stream()
                        .filter( i -> i.getLifeformType().equals(l))
                        .findFirst()
                        .orElseThrow( () -> new NoSuchElementException("The requested lifeform has not been found in the given cabin"));

                tmpCabin.removeInhabitant(tmpLifeFormToBeRemoved);
            });
        }

        // Check if the player has finished all of its astronauts --> if yes it needs to be eliminated from the game
        if (playerShip.getCabinList().stream().flatMap(c -> c.getInhabitants().stream()).noneMatch(i -> i.getLifeformType().equals(LifeformType.ASTRONAUT))) {
            this.getBoard().eliminatePlayer(this.getCurrentPlayer().get());
        }

        return this;
    }

    private WarZone handlePlasmaShot(Player player, WarZoneJSON warZoneJSON) {


        return this;
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public CardStateJSON generateState() {
        return null;
    }
}
