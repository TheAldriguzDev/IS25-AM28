package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cabin;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
    private List<String> eliminatedPlayers;
    private final Map<String, Integer> updatedPositions;
    private final Map<String, Integer> updatedCredits;
    private final Map<String, List<CoordinatePair>> removedBatteries;
    private final Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms;
    private boolean isPlayerDefeated;

    private String prevPlayerNickname;

    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board, int uniqueCardId, String path) {
        super(name, cardLevel, board, uniqueCardId, path);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
        this.updatedPositions = new HashMap<>();
        this.updatedCredits = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.removedLifeforms = new HashMap<>();
        this.isPlayerDefeated = false;
    }

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        SlaversJSON slaversData;

        try {
            slaversData = (SlaversJSON) data;
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Card data type in invalid");
        }

        Optional<Player> playerOptional = getCurrentPlayer();

        playerOptional.ifPresentOrElse(
                (Player player) -> {
                    String playerNickname = slaversData.getPlayerNickname();
                    this.prevPlayerNickname = playerNickname;

                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }

                    if (!this.isPlayerDefeated) { // If the player has not been set as defeated it means it's the first time he uses the card
                        List<Pair<CoordinatePair, CoordinatePair>> activatedDoubleCannons
                                = player.getShip().activateComponents(slaversData.getDoubleCannonsToActivateCoordinates());

                        // Power consumed by the DoubleCannons
                        if (!activatedDoubleCannons.isEmpty()) {
                            this.removedBatteries.put(
                                    playerNickname,
                                    activatedDoubleCannons.stream()
                                            .map(Pair::getValue)
                                            .toList()
                            );
                        }

                        float playerFirepower = player.getShip().getFirePower(
                                activatedDoubleCannons.stream()
                                        .map(Pair::getKey)
                                        .toList()
                        );

                        System.out.println("FP: " + playerFirepower);

                        if (playerFirepower > requiredFirepower) {
                            cardUsed();
                            if (slaversData.getTakeCredits()) {
                                bonusEffect();
                                getBoard().movePlayerBackwards(player, movementSteps);
                                this.updatedPositions.put(playerNickname, player.getCursor());
                                int tmp = getBoard().getEliminatedPlayers().size();
                                this.getBoard().validatePlayersPosition();
                                for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // TODO: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                                    this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
                                }
                            }
                        }
                        else if (playerFirepower < requiredFirepower) {
                            //malusEffect(data);
                            this.isPlayerDefeated = true;
                        }
                    }
                    else { // The player should've sent the info regarding the removedCrew
                        malusEffect(data);
                        this.isPlayerDefeated = false;
                    }

                    if (!isPlayerDefeated) { // If the player has been defeated the current player does not change, and the game does not end
                        if (player.equals(players.getLast())) {
                            cardUsed();
                        }
                        else {
                            getNextPlayer();
                        }
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        return this;
    }

    @Override
    protected void bonusEffect() {
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    player.setCredits(player.getCredits() + this.givenCredits);
                    this.updatedCredits.put(player.getNickname(), player.getCredits());
                }
        );
    }

    @Override
    protected void malusEffect(ActionJSON data) {
        Optional<Player> playerOptional = getCurrentPlayer();
        SlaversJSON slaversData = (SlaversJSON) data;
        this.eliminatedPlayers = new ArrayList<>();

        playerOptional.ifPresent(
                (Player player) -> {

                    int numOfCrewToRemove = slaversData.getCrewToRemove().size();
                    int numOfTotalCrew = player.getShip().getAllLifeforms().size();

                    if (
                            (numOfCrewToRemove != this.takenCrew && numOfCrewToRemove != numOfTotalCrew) ||
                            numOfCrewToRemove > this.takenCrew
                    ) {
                        // Exception thrown if the removed crew members are too few or too much
                        throw new IllegalArgumentException("You didn't remove the right amount of crew members, please try again");
                    }

                    // Remove the crew members from the given cabins
                    for (ComponentHelper<LifeformType> lifeForm : slaversData.getCrewToRemove()) {
                        Cabin tmpCabin;

                        try {
                            tmpCabin = (Cabin) player.getShip().getComponent(lifeForm.getI(), lifeForm.getJ());
                        } catch (Exception e) {
                            throw new IllegalStateException("The given component is not a valid cabin");
                        }

                        lifeForm.getItem().ifPresent(l -> {

                            Lifeform tmpLifeFormToBeRemoved = tmpCabin.getInhabitants().stream()
                                    .filter( i -> i.getLifeformType().equals(l))
                                    .findFirst()
                                    .orElseThrow( () -> new NoSuchElementException("The requested lifeForm has not been found in the given cabin"));

                            tmpCabin.removeInhabitant(tmpLifeFormToBeRemoved);
                        });
                    }

                    this.removedLifeforms.put(player.getNickname(), slaversData.getCrewToRemove());

                    // Check if the player has finished all of its astronauts --> if yes it needs to be eliminated from the game
                    if (player.getShip().getCabinList().stream().flatMap(c -> c.getInhabitants().stream()).noneMatch(i -> i.getLifeformType().equals(LifeformType.ASTRONAUT))) {
                        this.eliminatedPlayers.add(player.getNickname());
                        this.getBoard().eliminatePlayer(player);
                    }
                }
        );
    }

    @Override
    public CardStateJSON generateState() {

        if (hasBeenActivated()) {
            Optional<Player> playerOptional = getCurrentPlayer();
            CardStateJSON slaversStateJSON = new CardStateJSON();
            slaversStateJSON.setUniqueCardId(this.uniqueCardId);

            // Initializing the state flags
            initStateFlags(slaversStateJSON);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> slaversStateJSON.setPlayerNickname(player.getNickname()));
            slaversStateJSON.setPrevPlayerNickname(this.prevPlayerNickname);
            // The prevPlayer's crew is always updated locally in this card
            slaversStateJSON.setSkipCrewUpdate(true);

            slaversStateJSON.setIsPlayerDefeated(this.isPlayerDefeated);

            // Setting the removed lifeForms
            setUpdatedRemovedLifeformsIfNecessary(slaversStateJSON, removedLifeforms);
            // Setting the eliminated players
            setUpdatedEliminatedPlayersIfNecessary(slaversStateJSON, eliminatedPlayers);
            // Setting the consumed batteries
            setUpdatedRemovedBatteriesIfNecessary(slaversStateJSON, removedBatteries);
            // Setting the Updated positions
            setUpdatedPositionsIfNecessary(slaversStateJSON, updatedPositions);
            // Setting the updated credits
            setUpdatedCreditsIfNecessary(slaversStateJSON, updatedCredits);

            slaversStateJSON.setCardEnded(this.hasFinished());
            return slaversStateJSON;

        } else {
            // Setting the static info about the card
            return this.generateStaticState();
        }
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardTypeId(this.cardTypeId);
        cardState.setUniqueCardId(this.uniqueCardId);
        cardState.setCardName(this.getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(this.getCardLevel());
        cardState.setRequiredFirepower(requiredFirepower);
        cardState.setGivenCredits(this.givenCredits);
        cardState.setMovementSteps(this.movementSteps);
        cardState.setTakenCrew(this.takenCrew);
        cardState.setImagePath(this.path);

        return cardState;
    }
}
