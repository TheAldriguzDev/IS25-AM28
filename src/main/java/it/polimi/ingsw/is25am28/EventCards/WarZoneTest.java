package it.polimi.ingsw.is25am28.EventCards;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;

import javafx.util.Pair;

import java.util.*;

public class WarZoneTest extends EventCard {
    /**
     * General constructor shared between the classes
     *
     * @param name
     * @param cardLevel
     * @param board
     */
    protected WarZoneTest(String name, int cardLevel, Board board) {
        super(name, cardLevel, board);
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public CardStateJSON generateState() {
        return null;
    }
//    // Lowest crew conditions
//    private final int takenCrewForLowestCrew;
//    private final int takenStorageForLowestCrew;
//    private final int movementStepsForLowestCrew;
//    private final List<PlasmaShot> shootingSequenceForLowestCrew;
//
//    // Lowest engine power conditions
//    private final int takenCrewForLowestEnginePower;
//    private final int takenStorageForLowestEnginePower;
//    private final int movementStepsForLowestEnginePower;
//    private final List<PlasmaShot> shootingSequenceForLowestEnginePower;
//
//    // Lowest firepower conditions
//    private final int takenCrewForLowestFirepower;
//    private final int takenStorageForLowestFirepower;
//    private final int movementStepsForLowestFirepower;
//    private final List<PlasmaShot> shootingSequenceForLowestFirepower;
//
//    // Other attributes
//    private int currPlasmaShotIndex;
//    private int diceThrowResult;
//    private Map<Player, Pair<Integer, Float>> playerStats;
//    private Pair<Player, Integer> lowestCrewPlayer;
//    private Pair<Player, Integer> lowestEnginePowerPlayer;
//    private Pair<Player, Integer> lowestFirePowerPlayer;
//
//    public WarZoneTest(
//            @JsonProperty("cardName") String cardName,
//            @JsonProperty("cardLevel") int cardLevel,
//            @JsonProperty("humans") Map<String, Object> humans,
//            @JsonProperty("engines") Map<String, Object> engines,
//            @JsonProperty("cannons") Map<String, Object> cannons,
//            Board board
//    ) {
//        super(cardName, cardLevel, board);
//
//        this.currPlasmaShotIndex = 0;
//        this.players = this.getBoard().getPlayers();
//        this.playerStats = new HashMap<>();
//
//        List<Pair<Integer, Integer>> shootingSequence;
//
//        // (1) - Initializing the conditions for the player with the lowest crew
//        this.takenCrewForLowestCrew = (int) humans.get("humans");
//        this.takenStorageForLowestCrew = (int) humans.get("storage");
//        this.movementStepsForLowestCrew = (int) humans.get("days");
//        this.shootingSequenceForLowestCrew = new ArrayList<PlasmaShot>();
//
//        try {
//            // Cast is technically safe since the data provided with label "shoots" is actually
//            // a list of pairs of integers that describe the plasmaShots in the sequence
//            shootingSequence = (List<Pair<Integer, Integer>>) humans.get("shoots");
//
//            for (Pair<Integer, Integer> plasmaShotDescriptor : shootingSequence) {
//                this.shootingSequenceForLowestCrew.add(
//                    new PlasmaShot(
//                        plasmaShotDescriptor.getKey(),  // PlasmaShot size
//                        plasmaShotDescriptor.getValue() // PlasmaShot orientation
//                    )
//                );
//            }
//        }
//        catch (Exception e) {
//            throw new IllegalArgumentException("ERROR: JSON parsing error of \"shootingSequenceForLowestCrew\" in WarZone constructor");
//        }
//
//        // (2) - Initializing the conditions for the player with the lowest engine power
//        this.takenCrewForLowestEnginePower = (int) engines.get("humans");
//        this.takenStorageForLowestEnginePower = (int) engines.get("storage");
//        this.movementStepsForLowestEnginePower = (int) engines.get("days");
//        this.shootingSequenceForLowestEnginePower = new ArrayList<PlasmaShot>();
//
//        try {
//            // Cast is technically safe since the data provided with label "shoots" is actually
//            // a list of pairs of integers that describe the plasmaShots in the sequence
//            shootingSequence = (List<Pair<Integer, Integer>>) engines.get("shoots");
//
//            for (Pair<Integer, Integer> plasmaShotDescriptor : shootingSequence) {
//                this.shootingSequenceForLowestEnginePower.add(
//                    new PlasmaShot(
//                        plasmaShotDescriptor.getKey(),  // PlasmaShot size
//                        plasmaShotDescriptor.getValue() // PlasmaShot orientation
//                    )
//                );
//            }
//        }
//        catch (Exception e) {
//            throw new IllegalArgumentException("ERROR: JSON parsing error of \"shootingSequenceForLowestEnginePower\" in WarZone constructor");
//        }
//
//        // (3) - Initializing the conditions for the player with the lowest firepower
//        this.takenCrewForLowestFirepower = (int) cannons.get("humans");
//        this.takenStorageForLowestFirepower = (int) cannons.get("storage");
//        this.movementStepsForLowestFirepower = (int) cannons.get("days");
//        this.shootingSequenceForLowestFirepower = new ArrayList<PlasmaShot>();
//
//        try {
//            // Cast is technically safe since the data provided with label "shoots" is actually
//            // a list of pairs of integers that describe the plasmaShots in the sequence
//            shootingSequence = (List<Pair<Integer, Integer>>) cannons.get("shoots");
//
//            for (Pair<Integer, Integer> plasmaShotDescriptor : shootingSequence) {
//                this.shootingSequenceForLowestFirepower.add(
//                    new PlasmaShot(
//                        plasmaShotDescriptor.getKey(),  // PlasmaShot size
//                        plasmaShotDescriptor.getValue() // PlasmaShot orientation
//                    )
//                );
//            }
//        }
//        catch (Exception e) {
//            throw new IllegalArgumentException("ERROR: JSON parsing error of \"shootingSequenceForLowestFirepower\" in WarZone constructor");
//        }
//    }
//
//    @Override
//    protected void bonusEffect() {
//        // Nothing
//    }
//
//    @Override
//    protected void malusEffect() {
//        List<Player> playerList = new ArrayList<>(this.getBoard().getPlayers());
//        Player[] reversedPlayerArr;
//        int len;
//
//        // Now, after having activated some engines and/or cannons, the players
//        // referenced in lowestEnginePowerPlayer and lowestFirePowerPlayer could
//        // have changed, therefore they must be reevaluated
//
//        // NOTE: lowestCrewPlayer can't change in the meantime
//        // (there are no actions that can reduce the crew amount during this period)
//
//        // (0) - Removing the lowestCrewPlayer from the player list
//        playerList.remove(this.lowestCrewPlayer);
//
//        // (1) - Recalculating the lowestEnginePowerPlayer
//        for (Player player : playerList) {
//            if (
//                this.playerStats.get(player).getKey()
//                    <
//                this.playerStats.get(this.lowestEnginePowerPlayer).getKey()
//            ) {
//                this.lowestEnginePowerPlayer = new Pair<>(
//                    player,
//                    this.lowestEnginePowerPlayer.getValue()
//                );
//            }
//        }
//        playerList.remove(this.lowestEnginePowerPlayer);
//
//        // (2) - Recalculating the lowestFirePowerPlayer
//        for (Player player : playerList) {
//            if (
//                this.playerStats.get(player).getValue()
//                    <
//                this.playerStats.get(this.lowestFirePowerPlayer).getValue()
//            ) {
//                this.lowestFirePowerPlayer = new Pair<>(
//                    player,
//                    this.lowestCrewPlayer.getValue()
//                );
//            }
//        }
//        playerList.remove(this.lowestFirePowerPlayer);
//
//        // After categorizing again the players, the next step is to apply the
//        // malus effects for each corresponding category
//
//        // TODO
//
//        // Finally, move the players that need to be moved backwards by
//        // the amount indicated in the card
//        // First, create the list in reverse order
//        len = this.playerStats.size();
//        reversedPlayerArr = new Player[len];
//        len--;
//
//        for (Player player : this.playerStats.keySet()) {
//            reversedPlayerArr[len] = player;
//            len--;
//        }
//
//        // Then iterate on the reversed list and move each player
//        // by the amount specified by its category
//        for (Player player : reversedPlayerArr) {
//            if (this.playerStats.containsKey(player)) {
//                if (player == this.lowestCrewPlayer.getKey()) {
//                    // Case 1 - lowestCrewPlayer must be moved by the amount movementStepsForLowestCrew
//                    this.getBoard().movePlayerBackwards(player, this.movementStepsForLowestCrew);
//                }
//                else if (player == this.lowestEnginePowerPlayer.getKey()) {
//                    // Case 2 - lowestEnginePowerPlayer must be moved by the amount movementStepsForLowestEnginePower
//                    this.getBoard().movePlayerBackwards(player, this.movementStepsForLowestEnginePower);
//                }
//                else {
//                    // Case 3 - lowestFirePowerPlayer must be moved by the amount movementStepsForLowestFirePower
//                    this.getBoard().movePlayerBackwards(player, this.movementStepsForLowestFirepower);
//                }
//            }
//        }
//    }
//
//    @Override
//    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
//        WarZoneJSON warZoneJSON;
//        List<Pair<Integer, Integer>> enginesToActivate;
//        List<Pair<Integer, Integer>> cannonsToActivate;
//        List<Pair<Integer, Integer>> shieldsToActivate;
//        Player currPlayer;
//        Ship shipPtr;
//        int energyForEngines;
//        int energyForCannons;
//
//        try {
//            warZoneJSON = (WarZoneJSON) data;
//
//            this.diceThrowResult = warZoneJSON.getDiceThrowResult();
//            enginesToActivate = warZoneJSON.getEnginesToActivate();
//            cannonsToActivate = warZoneJSON.getCannonsToActivate();
//            shieldsToActivate = warZoneJSON.getShieldsToActivate();
//
//            // Getting the next player from the board that matches the username
//            // passed with the MeteorShowerJSON
//            currPlayer = this.getBoard().getPlayers().stream()
//                    .filter((Player p) -> (p.getNickname().equals(warZoneJSON.getPlayerNickname())))
//                    .toList().getFirst();
//
//            if (currPlayer == null) {
//                throw new IllegalArgumentException("ERROR: Given player is not present in the current game");
//            }
//            if (this.diceThrowResult < 2 || this.diceThrowResult > 12) {
//                throw new IllegalArgumentException("ERROR: Dice throw result cannot be outside of the range [2, 12]");
//            }
//        }
//        catch (Exception e) {
//            throw new IllegalArgumentException("[WarZone::useCard] " + e.getMessage());
//        }
//
//        shipPtr = currPlayer.getShip();
//
//        // The card is finished iff all players have answered and, because of
//        // this, the malus effects must be applied after all players responded
//        if (this.playerStats.size() < this.getBoard().getPlayers().size()) {
//            // (1) - Calculating the current player's engine power after
//            //       factoring in every double engine he chose to activate
//            energyForEngines = 0;
//
//            for (Pair<Integer, Integer> engineCoords : enginesToActivate) {
//                Component component = shipPtr.getComponent(
//                    engineCoords.getKey(),
//                    engineCoords.getValue()
//                );
//
//                // Safe cast to engine
//                switch (component) {
//                    case Engine engine -> {
//                        if (engine.getSpeed() == 2) {
//                            // The given engine coordinates correspond to a double
//                            // engine, thus it will be activated as requested by the user
//                            energyForEngines++;
//                        }
//                    }
//                    case null, default -> {}
//                }
//            }
//
//            // (2) - Calculating the current player's firepower after
//            //       factoring in every double cannon he chose to activate
//            energyForCannons = 0;
//
//            for (Pair<Integer, Integer> cannonCoords : cannonsToActivate) {
//                Component component = shipPtr.getComponent(
//                        cannonCoords.getKey(),
//                        cannonCoords.getValue()
//                );
//
//                // Safe cast to engine
//                switch (component) {
//                    case Cannon cannon -> {
//                        if (
//                            (cannon.getFirePower() == 2 && cannon.getDirection() == 0)
//                                    ||
//                            (cannon.getFirePower() == 1 && cannon.getDirection() != 0)
//                        ) {
//                            // The given cannon coordinates correspond to a double
//                            // cannon, thus it will be activated as requested by the user
//                            energyForCannons++;
//                        }
//                    }
//                    case null, default -> {}
//                }
//            }
//
//            // (3) - Storing the player's chosen stats for later when all players answered
//            this.playerStats.put(
//                currPlayer,
//                new Pair<Integer, Float>(
//                    shipPtr.getEnginePower(energyForEngines),
//                    shipPtr.getFirePower(energyForCannons)
//                )
//            );
//        }
//        else {
//            // Applying all malus effects on all selected players
//            // and finally mark this card as used
//            this.malusEffect();
//            this.cardUsed();
//        }
//
//        return this;
//    }
//
//    @Override
//    public CardStateJSON generateState() {
//        CardStateJSON cardState = new CardStateJSON();
//        List<Player> players;
//
//        // Calculating the lowestCrewPlayer, lowestEnginePowerPlayer and lowestFirePowerPlayer
//        // so that these can be shown as the current state before each player will answer and, perhaps,
//        // activate some engines and/or cannons and thus change the player for each condition
//        players = new ArrayList<>(this.players);
//
//        // (1) - lowestCrewPlayers
//        this.lowestCrewPlayer = new Pair<>(
//            players.getFirst(),
//            0
//        );
//
//        for (Player player : players) {
//            if (player != this.lowestCrewPlayer.getKey()) {
//                if (
//                    player.getShip().getAllLifeforms().size()
//                        <
//                    this.lowestCrewPlayer.getKey().getShip().getAllLifeforms().size()
//                ) {
//                    this.lowestCrewPlayer = new Pair<>(
//                        players.getFirst(),
//                        this.lowestCrewPlayer.getValue()
//                    );
//                }
//            }
//        }
//        players.remove(this.lowestCrewPlayer.getKey());
//
//        // (2) - lowestEnginePowerPlayers
//        this.lowestEnginePowerPlayer = new Pair<>(
//            players.getFirst(),
//            1
//        );
//
//        for (Player player : players) {
//            if (player != this.lowestEnginePowerPlayer.getKey()) {
//                if (
//                    player.getShip().getEnginePower(0)
//                        <
//                    this.lowestEnginePowerPlayer.getKey().getShip().getEnginePower(0)
//                ) {
//                    this.lowestEnginePowerPlayer = new Pair<>(
//                        player,
//                        this.lowestEnginePowerPlayer.getValue()
//                    );
//                }
//            }
//        }
//        players.remove(this.lowestEnginePowerPlayer.getKey());
//
//        // (3) - lowestFirePowerPlayers
//        this.lowestFirePowerPlayer = new Pair<>(
//            players.getFirst(),
//            2
//        );
//
//        for (Player player : players) {
//            if (player != this.lowestFirePowerPlayer.getKey()) {
//                if (
//                    player.getShip().getFirePower(0)
//                        <
//                    this.lowestFirePowerPlayer.getKey().getShip().getFirePower(0)
//                ) {
//                    this.lowestFirePowerPlayer = new Pair<>(
//                        player,
//                        this.lowestCrewPlayer.getValue()
//                    );
//                }
//            }
//        }
//        players.remove(this.lowestFirePowerPlayer.getKey());
//
//        if (this.getCurrentPlayer().isEmpty()) {
//            this.currentPlayer = this.getNextPlayer();
//        }
//
//        // Setting the card state
//        if (this.getCurrentPlayer().isPresent()) {
//            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
//        }
//
//        cardState.setCardName(this.getCardName());
//        cardState.setCardLevel(this.cardLevel);
//        cardState.setCardIsUsable( !this.hasFinished());
//
//        // Specific fields relative to the WarZone card
//        cardState.setLowestCrewPlayer(this.lowestCrewPlayer.getKey());
//        cardState.setLowestEnginePowerPlayer(this.lowestEnginePowerPlayer.getKey());
//        cardState.setLowestFirePowerPlayer(this.lowestFirePowerPlayer.getKey());
//
//        return cardState;
//    }
}