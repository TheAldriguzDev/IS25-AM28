package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Shield;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final List<List<Integer>> shootingSequence;
    private final Random random;
    private int playerUseCount;
    private int diceThrowResult;
    private int plasmashotIndex;
    Map<String, Integer> currentPlasmaShot;
    private int shotSize;
    private int shotDirection;

    private boolean firstRound;
    List<Player> playersToHit;
    private boolean hasBeenDefeated;
    private List<Component> previousPlayerRemovedComponents;
    private List<String> eliminatedPlayers;
    private Map<String, Integer> updatedPositions;
    private Map<String, Integer> updatedCredits;
    private Map<String, List<Map<String, Object>>> removedComponents;
    private Map<String, Integer> removedBatteries; // TODO: Implement in the state (both firepower and shields)
    private final Map<String, Integer> lostPieces;

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, List<List<Integer>> shootingSequence, Board board, int cardID, String path) {
        super(name, cardLevel, board, cardID, path);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.shootingSequence = shootingSequence;

        this.random = new Random();
        this.playerUseCount = 0;
        this.diceThrowResult = -1;
        this.plasmashotIndex = 0;
        this.firstRound = true;
        this.playersToHit = new ArrayList<>();
        this.hasBeenDefeated = false;
        this.updatedPositions = new HashMap<>();
        this.updatedCredits = new HashMap<>();
        this.removedComponents = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.lostPieces = new HashMap<>();
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
        cardActivated();
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
                        // Power consumed by the DoubleCannons
                        if (!piratesData.getDoubleCannonsToActivateCoordinates().isEmpty()) {
                            this.removedBatteries.put(playerNickname, piratesData.getDoubleCannonsToActivateCoordinates().size());
                        }
                        float playerFirepower = player.getShip().getFirePower(piratesData.getDoubleCannonsToActivateCoordinates());
                        if (playerFirepower > requiredFirepower && !hasBeenDefeated) {
                            // Pirates defeated, even if the player who defeated them does not take the credits, the card won't be used by other players
                            //cardUsed();
                            this.hasBeenDefeated = true;
                            if (piratesData.getTakeCredits()) {
                                bonusEffect();
                                getBoard().movePlayerBackwards(player, movementSteps);
                                this.updatedPositions.put(playerNickname, player.getCursor());
                                int tmp = getBoard().getEliminatedPlayers().size();
                                this.getBoard().validatePlayersPosition();
                                for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // TODO: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                                    this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
                                }
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
                            //currentPlasmaShot = new ArrayList<>(Arrays.asList(shotSize, shotDirection));
                            currentPlasmaShot = Map.of("shotSize", shotSize, "shotDirection", shotDirection);
                            // If there are no defeated players at the end of the first round the card is set as used
                            if (playersToHit.isEmpty()) {
                                cardUsed();
                            }
                        } else {
                            this.plasmashotIndex++;
                            // In the case the index advances, we need to set the new info about the plasmaShot, but only if there is at least one left
                            if (this.plasmashotIndex < shootingSequence.size()) {
                                shotSize = shootingSequence.get(plasmashotIndex).getFirst();  // 1 -> small, 2 -> big
                                shotDirection = shootingSequence.get(plasmashotIndex).getLast();  // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left
                                currentPlasmaShot = Map.of("shotSize", shotSize, "shotDirection", shotDirection);
                            }
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
                    this.updatedCredits.put(player.getNickname(), player.getCredits());
                }
        );
    }

    protected void malusEffect(ActionJSON data) throws ClassCastException {
        Optional<Player> playerOptional = getCurrentPlayer();
        PiratesJSON piratesData = (PiratesJSON) data;
        previousPlayerRemovedComponents = new ArrayList<>();
        eliminatedPlayers = new ArrayList<>();
        playerOptional.ifPresent(
                (Player player) -> {

                    Boolean[] shieldedSides = new Boolean[] {false, false, false, false};

                    //player.getShip().consumeEnergy(piratesData.getShieldsActivatedCoordinates().size()); // Il controllo sulle batterie disponibili è fatto dal client

                    // Moved to the bottom
//                    int shotSize = shootingSequence.get(plasmashotIndex).getFirst();  // 1 -> small, 2 -> big
//                    int shotDirection = shootingSequence.get(plasmashotIndex).getLast();  // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left
//                    Pair<Integer, Integer> currentPlasmaShot = new Pair<>(shotSize, shotDirection);

                    // Impostazione dei lati protetti della ship
                    for (ComponentHelper<Void> coordinates : piratesData.getShieldsActivatedCoordinates()) {
                        Shield shield = (Shield) player.getShip().getComponent(coordinates.getI(), coordinates.getJ());
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
                    // Batteries comsumed by the shields, might need a check on avaiability
                    this.removedBatteries.put(player.getNickname(), piratesData.getShieldsActivatedCoordinates().size());

                    if ((shotSize == 1 && !shieldedSides[shotDirection]) || shotSize == 2) {
                        switch (shotDirection) {
                            case 0: {
                                int column = diceThrowResult - 1;
                                for (int row = 4; row < 9; row++) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            previousPlayerRemovedComponents = player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componente
                                            this.removedComponents.put(player.getNickname(), previousPlayerRemovedComponents.stream().map(Component::toMap).toList());
                                            this.getCurrentPlayer().get().setLostPieces(this.getCurrentPlayer().get().getLostPieces());
                                            this.lostPieces.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getLostPieces());
                                        } catch (CoreDeletionAttemptException e) {
                                            eliminatedPlayers.add(player.getNickname());
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
                                int row = diceThrowResult - 1;
                                for (int column = 3; column < 10; column++) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            previousPlayerRemovedComponents = player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componente
                                            this.removedComponents.put(player.getNickname(), previousPlayerRemovedComponents.stream().map(Component::toMap).toList());
                                            this.getCurrentPlayer().get().setLostPieces(this.getCurrentPlayer().get().getLostPieces());
                                            this.lostPieces.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getLostPieces());
                                        } catch (CoreDeletionAttemptException e) {
                                            eliminatedPlayers.add(player.getNickname());
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
                                int column = diceThrowResult - 1;
                                for (int row = 8; row > 3; row--) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            previousPlayerRemovedComponents = player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componente
                                            this.removedComponents.put(player.getNickname(), previousPlayerRemovedComponents.stream().map(Component::toMap).toList());
                                            this.getCurrentPlayer().get().setLostPieces(this.getCurrentPlayer().get().getLostPieces());
                                            this.lostPieces.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getLostPieces());
                                        } catch (CoreDeletionAttemptException e) {
                                            eliminatedPlayers.add(player.getNickname());
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
                                int row = diceThrowResult - 1;
                                for (int column = 9; column > 2; column--) {
                                    if (player.getShip().getComponent(row, column) != null) {
                                        try {
                                            previousPlayerRemovedComponents = player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componente
                                            this.removedComponents.put(player.getNickname(), previousPlayerRemovedComponents.stream().map(Component::toMap).toList());
                                            this.getCurrentPlayer().get().setLostPieces(this.getCurrentPlayer().get().getLostPieces());
                                            this.lostPieces.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getLostPieces());
                                        } catch (CoreDeletionAttemptException e) {
                                            eliminatedPlayers.add(player.getNickname());
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
                    currentPlasmaShot = Map.of("shotSize", shotSize, "shotDirection", shotDirection);
                    //currentPlasmaShot = new ArrayList<>(Arrays.asList(shotSize, shotDirection));
//                    System.out.println("Parametri shot cambiati! -> pS index : " + plasmashotIndex);
//                    System.out.println("SIZE: " + shotSize);
//                    System.out.println("Direction: " + shotDirection);

                }
        );
    }

    protected void malusEffect() {}

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON piratesStateJSON = new CardStateJSON();
        piratesStateJSON.setCardID(this.getCardID());

        // The dice throw is performed by generateState only at the beginning
        // since the card hasn't been used yet
        if (this.diceThrowResult == -1) {
            this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
        }

        //piratesStateJSON.setHasBeenActivated(hasBeenActivated());
        if (hasBeenActivated()) {
            initStateFlags(piratesStateJSON);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> piratesStateJSON.setPlayerNickname(player.getNickname()));

            // The clients need to know when to update the right parameters
            piratesStateJSON.setFirstRound(this.firstRound);

            // If the first round is finished, send the dynamic info to the players
            if (!firstRound) {
                // Send information on the players that are going to be hit, along with the plasmaShot's data and the dice result
                ArrayList<String> defeatedPlayers = new ArrayList<>();
                for (Player player : playersToHit) {
                    defeatedPlayers.add(player.getNickname());
                }
                piratesStateJSON.setDefeatedPlayers(defeatedPlayers);  // TODO: Need more thinking on this

                piratesStateJSON.setCurrPlasmaShotDescriptor(currentPlasmaShot);
                piratesStateJSON.setDiceThrowResult(this.diceThrowResult);

                setUpdatedRemovedComponentsIfNecessary(piratesStateJSON, this.removedComponents);
                setUpdatedLostPiecesIfNecessary(piratesStateJSON, this.lostPieces);
                setUpdatedEliminatedPlayersIfNecessary(piratesStateJSON, this.eliminatedPlayers);

                // Batteries consumed due to the shield
                setUpdatedRemovedBatteriesIfNecessary(piratesStateJSON, removedBatteries);

            } else {
                // Batteries consumed due to the double cannons
                setUpdatedRemovedBatteriesIfNecessary(piratesStateJSON, removedBatteries);
            }
            // If the pirates have been defeated, set the rewards
            if (this.hasBeenDefeated) {
                setUpdatedPositionsIfNecessary(piratesStateJSON, this.updatedPositions);
                setUpdatedCreditsIfNecessary(piratesStateJSON, this.updatedCredits);
            }
        } else {
            // This static info will be sent to the clients only when the card has not been activated yet
            piratesStateJSON.setId(this.id);
            piratesStateJSON.setCardName(getCardName());
            piratesStateJSON.setImagePath(this.path);
            piratesStateJSON.setCardLevel(getCardLevel());
            piratesStateJSON.setRequiredFirepower(this.requiredFirepower);
            piratesStateJSON.setGivenCredits(this.givenCredits);
            piratesStateJSON.setMovementSteps(this.movementSteps);
        }

        piratesStateJSON.setCardEnded(this.hasFinished());

        return piratesStateJSON;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardID(this.getCardID());
        cardState.setId(this.id);
        cardState.setCardName(getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(getCardLevel());
        cardState.setRequiredFirepower(this.requiredFirepower);
        cardState.setGivenCredits(this.givenCredits);
        cardState.setMovementSteps(this.movementSteps);

        return cardState;
    }

    // Only for testing
    void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    public WidgetTUI generateWidget(CardStateJSON piratesState) {
        return null;}
}
