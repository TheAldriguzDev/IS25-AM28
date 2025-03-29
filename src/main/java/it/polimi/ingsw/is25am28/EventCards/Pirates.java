package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final ArrayList<ArrayList<Integer>> shootingSequence;
    private final Random random;
    private int playerUseCount;
    private int diceThrowResult;
    private int plasmashotIndex;

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, ArrayList<ArrayList<Integer>> shootingSequence, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.shootingSequence = shootingSequence;

        random = new Random();
        playerUseCount = 0;
        diceThrowResult = -1;
        plasmashotIndex = 0;
    }

    // Override necessary to not set the card as used when the last index of the player's list is reached
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
                    float playerFirepower = player.getShip().getFirePower(piratesData.getNumberOfDoubleCannonsActivated());
                    if (playerFirepower > requiredFirepower) {
                        // Pirates defeated, even if the player who defeated them does not take the credits, the card won't be used by other players
                        cardUsed();
                        if (piratesData.getTakeCredits()) {
                            bonusEffect();
                            getBoard().movePlayerBackwards(player, movementSteps);
                            getBoard().validatePlayersPosition();
                        }
                    } else if (playerFirepower < requiredFirepower) {
                        malusEffect(piratesData);
                    }
                    playerUseCount++;
                    if (this.playerUseCount % this.getBoard().getPlayers().size() == 0) {
                        this.plasmashotIndex++;
                        this.initCardPlayers();
                        this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
                    }
                    // The card gets marked as completed only when all players
                    // have encountered all the plasmashots
                    if (this.plasmashotIndex == shootingSequence.size()) {
                        this.cardUsed();
                    }
//                    if (player.equals(this.players.getLast())) {
//                        this.cardUsed(); // Mark the card as used
//                        this.getBoard().validatePlayersPosition();
//                    } else {
//                        this.getNextPlayer();
//                    }
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

                    int shotSize = shootingSequence.get(plasmashotIndex).getFirst();  // 1 -> small, 2 -> big
                    int shotDirection = shootingSequence.get(plasmashotIndex).getLast();  // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left

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
                                            // System.out.println("Eliminated " + player.getNickname());
                                        }
                                        //System.out.println("(U)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
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
                                        }
                                        //System.out.println("(R)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
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
                                        }
                                        //System.out.println("(D)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
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
                                        }
                                        //System.out.println("(L)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }

                    /*
                    for (ArrayList<Integer> pair : shootingSequence) {
                        int shotSize = (int) pair.getFirst(); // 1 -> small, 2 -> big
                        int shotDirection = (int) pair.getLast(); // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left



                        // Prima di ogni colpo resetta shieldedSides per poi ricalcolarli
                        for (int i = 0; i < 2; i++) {
                            shieldedSides[i] = false;
                        }
                        // Funzione prima di ogni colpo imposta i lati protetti
                        for (int[] coordinates : piratesData.getShieldsActivatedCoordinates()) {
                            try {
                                Shield shield = (Shield) player.getShip().getComponent(coordinates[0], coordinates[1]);
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
                            } catch (NullPointerException e) {
                                //Non attiva lo scudo in quanto è stato distrutto
                            }
                        }
                        if ((shotSize == 1 && !shieldedSides[shotDirection]) || shotSize == 2) {
                            switch (shotDirection) {
                                case 0: {
                                    int column = dicesResults.get(dicesResult);
                                    for (int row = 4; row < 9; row++) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                                // System.out.println("Eliminated " + player.getNickname());
                                            }
                                            //System.out.println("(U)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 1: {
                                    int row = dicesResults.get(dicesResult);
                                    for (int column = 3; column < 10; column++) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            }
                                            //System.out.println("(R)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    int column = dicesResults.get(dicesResult);
                                    for (int row = 8; row > 3; row--) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            }
                                            //System.out.println("(D)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                break;
                                }
                                case 3: {
                                    int row = dicesResults.get(dicesResult);
                                    for (int column = 9; column > 2; column--) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            }
                                            //System.out.println("(L)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        dicesResult++;
                    }*/
                }
        );
    }

    protected void malusEffect() {}

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON piratesStateJSON;
        if(playerOptional.isPresent()) {
            // The dice throw is performed by generateState only at the beginning
            // since the card hasn't been used yet
            if (this.diceThrowResult == -1) {
                this.diceThrowResult = (this.random.nextInt(6) + 1) + (this.random.nextInt(6) + 1);
            }
            piratesStateJSON = new CardStateJSON(
                    playerOptional.get().getNickname(),
                    getCardName(),
                    getCardLevel(),
                    !hasFinished(),
                    this.requiredFirepower,
                    this.givenCredits,
                    this.movementSteps,
                    this.shootingSequence,
                    this.diceThrowResult);
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        // Mettere nello stato il dado corrente ?


        return piratesStateJSON;
    }
}
