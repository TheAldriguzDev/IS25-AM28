package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import javafx.util.Pair;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final List<List<Integer>> shootingSequence;
    private final Random random;
    private int playerUseCount;
    private int diceThrowResult;
    private int plasmashotIndex;
    Pair<Integer, Integer> currentPlasmaShot;
    private int shotSize;
    private int shotDirection;

    private boolean firstRound;
    List<Player> playersToHit;
    private boolean hasBeenDefeated;

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, List<List<Integer>> shootingSequence, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.shootingSequence = shootingSequence;

        random = new Random();
        playerUseCount = 0;
        diceThrowResult = -1;
        plasmashotIndex = 0;
        firstRound = true;
        playersToHit = new ArrayList<>();
        hasBeenDefeated = false;
    }
    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            if (firstRound) {
                this.players = new ArrayList<>(this.getBoard().getPlayers());
            } else {
                if (!playersToHit.isEmpty()) {
                    this.players = new ArrayList<>(this.playersToHit);
                }
            }
            currentPlayer = Optional.of(players.getFirst());
        }
    }

    // Override necessary to not set the card as used when the last index of the player's list is reached
    @Override
    protected Optional<Player> getNextPlayer() {
        if (players == null || players.isEmpty()) {
            throw new Error("Players are not set, you must call startUsingCard method before");
        }

        if (currentPlayer.isPresent()) {
            int currentIndex = players.indexOf(currentPlayer.get());
            if (currentIndex == players.size() - 1) {
                return Optional.empty();
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

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        PiratesJSON piratesData;
        try {
            piratesData = (PiratesJSON) data;
        } catch (ClassCastException e) {
            throw new ClassCastException("Card data type in invalid");
        }

        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresentOrElse(
                (Player player) -> {
                    String playerNickname = piratesData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }
                    // if the first round of meteors has passed, this block won't be executed, assuring that no players will get the same reward twice (or activate the cannons twice)
                    if (firstRound) {
                        float playerFirepower = player.getShip().getFirePower(piratesData.getDoubleCannonsToActivateCoordinates());
                        if (playerFirepower > requiredFirepower && !hasBeenDefeated) {
                            // Pirates defeated, even if the player who defeated them does not take the credits, the card won't be used by other players
                            //cardUsed();
                            this.hasBeenDefeated = true;
                            if (piratesData.getTakeCredits()) {
                                bonusEffect();
                                getBoard().movePlayerBackwards(player, movementSteps);
                                getBoard().validatePlayersPosition();
                            }
                        } else if (playerFirepower < requiredFirepower && !hasBeenDefeated) {
                            //malusEffect(piratesData);
                            playersToHit.add(player);
                        }
                    }
                    playerUseCount++;
                    // if the first round is finished, if the player is among tye defeated players, he will be exposed to the plasmashots
                    if (!firstRound) {
                        if (playersToHit.contains(player)) {
                            malusEffect(piratesData);
                        }
                    }
                    if (this.playerUseCount % this.players.size() == 0) {
                        // flag to make sure  players do not get rewards or have to use cannons twice
                        if (firstRound) {
                            firstRound = false;
                            // Necessary for the first round of shots (without this the descriptor would not be included in the state generated)
                            shotSize = shootingSequence.get(plasmashotIndex).getFirst();  // 1 -> small, 2 -> big
                            shotDirection = shootingSequence.get(plasmashotIndex).getLast();  // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left
                            currentPlasmaShot = new Pair<>(shotSize, shotDirection);
                            // If there are no defeated players at the end of the first round the card is set as used
                            if (playersToHit.isEmpty()) {
                                cardUsed();
                            }
                        } else {
                            this.plasmashotIndex++;
                            this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
                        }
                        this.initCardPlayers();
                    } else {
                        this.getNextPlayer();
                    }
                    // The card gets marked as completed only when all players
                    // have encountered all the plasmashots
                    if (this.plasmashotIndex == shootingSequence.size()) {
                        this.cardUsed();
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        return this;
    }

    protected void bonusEffect() {
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    player.setCredits(player.getCredits() + this.givenCredits);
                }
        );
    }

    protected void malusEffect(ActionJSON data) throws ClassCastException {
        Optional<Player> playerOptional = getCurrentPlayer();
        PiratesJSON piratesData = (PiratesJSON) data;
        playerOptional.ifPresent(
                (Player player) -> {

                    Boolean[] shieldedSides = new Boolean[] {false, false, false, false};

                    //player.getShip().consumeEnergy(piratesData.getShieldsActivatedCoordinates().size()); // Il controllo sulle batterie disponibili è fatto dal client

                    // Moved to the bottom
//                    int shotSize = shootingSequence.get(plasmashotIndex).getFirst();  // 1 -> small, 2 -> big
//                    int shotDirection = shootingSequence.get(plasmashotIndex).getLast();  // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left
//                    Pair<Integer, Integer> currentPlasmaShot = new Pair<>(shotSize, shotDirection);

                    // Impostazione dei lati protetti della ship
                    for (int[] coordinates : piratesData.getShieldsActivatedCoordinates()) {
                        Shield shield = (Shield) player.getShip().getComponent(coordinates[0], coordinates[1]);
                        if (player.getShip().getAvailableEnergy() > 0) {
                            player.getShip().consumeEnergy(1);
                            switch (shield.getCoveredSide()[0]) {
                                case 0:
                                    shieldedSides[0] = true;
                                    shieldedSides[1] = true;
                                    break;
                                case 1:
                                    shieldedSides[1] = true;
                                    shieldedSides[2] = true;
                                    break;
                                case 2:
                                    shieldedSides[2] = true;
                                    shieldedSides[3] = true;
                                    break;
                                case 3:
                                    shieldedSides[3] = true;
                                    shieldedSides[0] = true;
                            }
                        } else {
                            // Non possono essere attivati ulteriori scudi
                            break;
                        }
                    }

                    if ((shotSize == 1 && !shieldedSides[shotDirection]) || shotSize == 2) {
                        switch (shotDirection) {
                            case 0: {
                                int column = diceThrowResult;
                                for (int row = 4; row < 9; row++) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                        } catch (CoreDeletionAttemptException e) {
                                            getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                int row = diceThrowResult;
                                for (int column = 3; column < 10; column++) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                        } catch (CoreDeletionAttemptException e) {
                                            getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                int column = diceThrowResult;
                                for (int row = 8; row > 3; row--) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                        } catch (CoreDeletionAttemptException e) {
                                            getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            case 3: {
                                int row = diceThrowResult;
                                for (int column = 9; column > 2; column--) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                        } catch (CoreDeletionAttemptException e) {
                                            getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    shotSize = shootingSequence.get(plasmashotIndex).getFirst();  // 1 -> small, 2 -> big
                    shotDirection = shootingSequence.get(plasmashotIndex).getLast();  // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left
                    currentPlasmaShot = new Pair<>(shotSize, shotDirection);
                }
        );

    }

    protected void malusEffect() {}

    // TODO: fix pirates' generate state to include the individual shot data instead of the full sequence at once

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON piratesStateJSON = new CardStateJSON();
        if(playerOptional.isPresent()) {
            // The dice throw is performed by generateState only at the beginning
            // since the card hasn't been used yet
            if (this.diceThrowResult == -1) {
                this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
            }
            piratesStateJSON.setPlayerNickname(playerOptional.get().getNickname());
            piratesStateJSON.setCardName(getCardName());
            piratesStateJSON.setCardLevel(getCardLevel());
            piratesStateJSON.setCardIsUsable(!hasFinished());
            piratesStateJSON.setRequiredFirepower(this.requiredFirepower);
            piratesStateJSON.setGivenCredits(this.givenCredits);
            piratesStateJSON.setMovementSteps(this.movementSteps);
            piratesStateJSON.setCurrPlasmaShotDescriptor(currentPlasmaShot);
            piratesStateJSON.setDiceThrowResult(this.diceThrowResult);
            piratesStateJSON.setFirstRound(this.firstRound);
            if (!firstRound) {
                ArrayList<String> defeatedPlayers = new ArrayList<>();
                for (Player player : playersToHit) {
                    defeatedPlayers.add(player.getNickname());
                }
                piratesStateJSON.setDefeatedPlayers(defeatedPlayers);
            }
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }
        return piratesStateJSON;
    }

    // Only for testing
    void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    // TODO : Currently does not work, a merge with yìthe mai dev tree is necessary to fix the generateState
    @Override
    public WidgetTUI generateWidget(CardStateJSON piratesState) {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + piratesState.getCardName().toUpperCase() + "====");

        if (this.firstRound) {
            cardInfoWidget.appendString("Level: " + piratesState.getCardLevel());
            cardInfoWidget.appendString("Given credits: " + piratesState.getGivenCredits());
            cardInfoWidget.appendString("Days: " + piratesState.getMovementSteps());
            // TODO : does the shootingSequence need to be shown to the clients as a whole?
            cardInfoWidget.appendString("Required Firepower: " + piratesState.getRequiredFirepower());
        } else {
            cardInfoWidget.appendString("Target player is: " + piratesState.getPlayerNickname());
            cardInfoWidget.appendString("Current PlasmaShot size: " + piratesState.getCurrPlasmaShotDescriptor().getKey());
            cardInfoWidget.appendString("Current PlasmaShot direction: " + piratesState.getCurrPlasmaShotDescriptor().getValue());
            // TODO : when does the dice throw need to be shown to the client?
        }
        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public WidgetTUI generateWidget() {
        return null;
    }
}
