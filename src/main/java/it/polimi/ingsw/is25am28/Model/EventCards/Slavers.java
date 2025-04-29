package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cabin;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
    private boolean hasBeenDefeated;
    ArrayList<String> defeatedPlayers;
    private boolean firstRound;
    ArrayList<Player> playersToTakeCrewFrom;
    private List<String> eliminatedPlayers;
    private Map<String, Integer> updatedPositions;
    private Map<String, Integer> updatedCredits;

    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
        this.hasBeenDefeated = false;
        this.defeatedPlayers = new ArrayList<>();
        this.firstRound = true;
        this.playersToTakeCrewFrom = new ArrayList<>();
        this.updatedPositions = new HashMap<>();
        this.updatedCredits = new HashMap<>();
    }

    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            if (firstRound) {
                this.players = new ArrayList<>(this.getBoard().getPlayers());
            } else {
                if (!playersToTakeCrewFrom.isEmpty()) {
                    this.players = new ArrayList<>(this.playersToTakeCrewFrom);
                }
            }
            currentPlayer = Optional.of(players.getFirst());
        }
        cardActivated();
    }


    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        SlaversJSON slaversData;
        try {
            slaversData = (SlaversJSON) data;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Card data type in invalid");
        }
        Optional<Player> playerOptional = getCurrentPlayer();

        playerOptional.ifPresentOrElse(
                (Player player) -> {
                    String playerNickname = slaversData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }
                    if (firstRound) {
                        float playerFirepower = player.getShip().getFirePower(slaversData.getDoubleCannonsToActivateCoordinates());
                        if (playerFirepower > requiredFirepower && !hasBeenDefeated) {
                            hasBeenDefeated = true;
                            //cardUsed();
                            if (slaversData.getTakeCredits()) {
                                bonusEffect();
                                getBoard().movePlayerBackwards(player, movementSteps);
                                this.updatedPositions.put(playerNickname, player.getCursor());
                                getBoard().validatePlayersPosition();
                            }
                        } else if (playerFirepower < requiredFirepower && !hasBeenDefeated) {
                            playersToTakeCrewFrom.add(player);
                            //malusEffect(data);
                        }
                    }
                    if (!firstRound) {
                        if (playersToTakeCrewFrom.contains(player)) { // Redundant, since only affected players will send the data
                            malusEffect(slaversData);
                        }
                    }
                    if (player.equals(players.getLast())) {
                        if (firstRound) {
                            firstRound = false;
                            if (playersToTakeCrewFrom.isEmpty()) {
                                cardUsed();
                            } else {
                                initCardPlayers();
                            }
                        } else {
                            cardUsed();
                        }
                    } else {
                        getNextPlayer();
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

    /*
     * La lista crewToRemove indica le cabine dalle quali verrà rimosso il primo elemento
     * dell'equipaggio, nel caso il giocatore abbia scelto di rimovere 2 esseri umani da una cabina,
     * la cabina dovrà essere presente 2 volte nella lista
     * */

    /*
     * Il metodo parte client che il player utilizzerà scegliere chi rimuovere si assicurerà che la lista non abbia
     * configurazioni non valide (vuota, equipaggio rimosso non sufficiente, 3 volte la stessa cabina...)
     * */

    protected void malusEffect(ActionJSON data) {
        Optional<Player> playerOptional = getCurrentPlayer();
        SlaversJSON slaversData = (SlaversJSON) data;
        this.eliminatedPlayers = new ArrayList<>();
        playerOptional.ifPresent(
                (Player player) -> {
                    // Remove the crew members from the given cabins
                    for (ComponentHelper<LifeformType> lifeform : slaversData.getCrewToRemove()) {
                        Cabin tmpCabin;

                        try {
                            tmpCabin = (Cabin) player.getShip().getComponent(lifeform.getI(), lifeform.getJ());
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
                    if (player.getShip().getCabinList().stream().flatMap(c -> c.getInhabitants().stream()).noneMatch(i -> i.getLifeformType().equals(LifeformType.ASTRONAUT))) {
                        this.eliminatedPlayers.add(player.getNickname());
                        this.getBoard().eliminatePlayer(player);
                    }

                }
        );
    }


    @Override
    protected void malusEffect() {}


    //
    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON slaversStateJSON = new CardStateJSON();

        if (hasBeenActivated()) {
            slaversStateJSON.setNeedsBoardUpdate(false);
            slaversStateJSON.setNeedsPlayerUpdate(false);
            slaversStateJSON.setNeedsShipUpdate(false);

            playerOptional.ifPresent(player -> slaversStateJSON.setPlayerNickname(player.getNickname()));

            // The clients need to know when to update the right parameters
            slaversStateJSON.setFirstRound(this.firstRound);

            // If the first round is finished, send the dynamic info to the players
            if (!firstRound) {
                ArrayList<String> defeatedPlayers = new ArrayList<>();
                for (Player player : playersToTakeCrewFrom) {
                    defeatedPlayers.add(player.getNickname());
                }
                slaversStateJSON.setDefeatedPlayers(defeatedPlayers);
                if (!this.eliminatedPlayers.isEmpty()) {
                    slaversStateJSON.setNeedsBoardUpdate(true);
                    slaversStateJSON.setNeedsUpdatedEliminatedPlayers(true);
                    slaversStateJSON.setEliminatedPlayers(this.eliminatedPlayers);
                }
            }
            // if the smugglers have been defeated we need to set the rewards (if taken)
            if (hasBeenDefeated && !updatedPositions.isEmpty()) {
                slaversStateJSON.setNeedsBoardUpdate(true);
                slaversStateJSON.setNeedsUpdatedPositions(true);
                slaversStateJSON.setUpdatedPositions(this.updatedPositions);
                if (!this.updatedCredits.isEmpty()) {
                    slaversStateJSON.setNeedsPlayerUpdate(true);
                    slaversStateJSON.setNeedsUpdatedCredits(true);
                    slaversStateJSON.setUpdatedCredits(this.updatedCredits);
                } else {
                    slaversStateJSON.setNeedsUpdatedCredits(false);
                }
            }
        } else {
            slaversStateJSON.setId(this.id);
            slaversStateJSON.setCardName(this.getCardName());
            slaversStateJSON.setCardLevel(this.getCardLevel());
            slaversStateJSON.setRequiredFirepower(requiredFirepower);
            slaversStateJSON.setGivenCredits(this.givenCredits);
            slaversStateJSON.setMovementSteps(this.movementSteps);
            slaversStateJSON.setTakenCrew(this.takenCrew);
        }
        return slaversStateJSON;
    }


    public WidgetTUI generateWidget(CardStateJSON slaversState) {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + slaversState.getCardName().toUpperCase() + "====");

        if (this.firstRound) {
            cardInfoWidget.appendString("Level: " + slaversState.getCardLevel());
            cardInfoWidget.appendString("Given Credits: " + slaversState.getGivenCredits());
            cardInfoWidget.appendString("Days: " + slaversState.getMovementSteps());
            cardInfoWidget.appendString("Required Firepower: " + slaversState.getRequiredFirepower());
            cardInfoWidget.appendString("Taken Crew: " + slaversState.getTakenCrew());
        } else {
            cardInfoWidget.appendString("Player: " + slaversState.getPlayerNickname() + " has to give up " + slaversState.getTakenCrew() + " crew members");
        }
        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
