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

    private boolean firstRound;
    ArrayList<Player> playersToHit;
    private boolean hasBeenDefeated;

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
                //System.out.println("Initialised with option 1");
                this.players = new ArrayList<>(this.getBoard().getPlayers());
            } else {
                //System.out.println("Initialised with option 2");
                //System.out.println("Players to hit:");
//                for(Player p : playersToHit) {
//                    System.out.println(p.getNickname());
//                }
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

//        System.out.println("Gocatori: ");
//        for(Player p : this.players) {
//            System.out.println(p.getNickname());
//        }

        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresentOrElse(
                (Player player) -> {
                    //System.out.println("Appena iniziato " + player.getNickname());
                    String playerNickname = piratesData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }
                    // if the first round of meteors has passed, this block won't be executed, assuring that no players will get the same reward twice (or activate the cannons twice)
                    if (firstRound) {
                        float playerFirepower = player.getShip().getFirePower(piratesData.getNumberOfDoubleCannonsActivated());
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
                        //System.out.println("Fine round");
                        // flag to make sure  players do not get rewards or have to use cannons twice
                        if (firstRound) {
                            firstRound = false;
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
                        System.out.println("Plasmashot index finished");
                        this.cardUsed();
                    }

                    //System.out.println("Appena finito " + player.getNickname());
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
                                            System.out.println(player.getNickname() + " sostenuto il colpo in [r,c] : " + row + "" + column);
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                        } catch (CoreDeletionAttemptException e) {
                                            getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            System.out.println(player.getNickname() + " eliminato");
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
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
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
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
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
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
                                            playersToHit.remove(player); // Further shots must not be headed to the player's ship since it has been destroyed
                                            if (playersToHit.isEmpty()) {
                                                cardUsed();
                                            }
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
                    this.diceThrowResult,
                    firstRound);
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }
        return piratesStateJSON;
    }

    // Only for testing
    void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

}
