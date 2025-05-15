package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cabin;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
    //private boolean hasBeenDefeated;
    //ArrayList<String> defeatedPlayers;
    //private boolean firstRound;
    private ArrayList<Player> playersToTakeCrewFrom;
    private List<String> eliminatedPlayers;
    private Map<String, Integer> updatedPositions;
    private Map<String, Integer> updatedCredits;
    private Map<String, Integer> removedBatteries;
    private Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms;
    private boolean isPlayerDefeated;

    private String prevPlayerNickname;

    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board, int cardID, String path) {
        super(name, cardLevel, board, cardID, path);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
        //this.hasBeenDefeated = false;
        //this.defeatedPlayers = new ArrayList<>();
        //this.firstRound = true;
        this.playersToTakeCrewFrom = new ArrayList<>();
        this.updatedPositions = new HashMap<>();
        this.updatedCredits = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.removedLifeforms = new HashMap<>();
        this.isPlayerDefeated = false;
    }

//    @Override
//    public void initCardPlayers() throws IllegalArgumentException {
//        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
//            throw new IllegalArgumentException("The player list is null or contains less than two player");
//        } else {
//            if (firstRound) {
//                this.players = new ArrayList<>(this.getBoard().getPlayers());
//            } else {
//                if (!playersToTakeCrewFrom.isEmpty()) {
//                    this.players = new ArrayList<>(this.playersToTakeCrewFrom);
//                }
//            }
//            currentPlayer = Optional.of(players.getFirst());
//        }
//        cardActivated();
//    }


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
                    this.prevPlayerNickname = playerNickname;
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }
                    if (!this.isPlayerDefeated) { // If the player has not been set as defeated it means its the first time he uses the card
                        // Power consumed by the DoubleCannons
                        if (!slaversData.getDoubleCannonsToActivateCoordinates().isEmpty()) {
                            this.removedBatteries.put(playerNickname, slaversData.getDoubleCannonsToActivateCoordinates().size());
                        }
                        float playerFirepower = player.getShip().getFirePower(slaversData.getDoubleCannonsToActivateCoordinates());
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
                        } else if (playerFirepower < requiredFirepower) {
                            //malusEffect(data);
                            this.isPlayerDefeated = true;
                        }
                    } else { // The player should've sent the info regarding the removedCrew
                        malusEffect(data);
                        this.isPlayerDefeated = false;
                    }
                    if (!isPlayerDefeated) { // If the player has been defeated the current player does not change, and the game does not end
                        if (player.equals(players.getLast())) {
                            cardUsed();
                        } else {
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
                    if (slaversData.getCrewToRemove().size() != this.takenCrew && slaversData.getCrewToRemove().size() != player.getShip().getAllLifeforms().size()) {
                        throw new IllegalArgumentException("You didn't remove the right amount of crew members, please try again");
                    }
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
    protected void malusEffect() {}


    //
    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON slaversStateJSON = new CardStateJSON();

        slaversStateJSON.setCardID(this.getCardID());

        if (hasBeenActivated()) {
            // Initializing the state flags
            initStateFlags(slaversStateJSON);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> slaversStateJSON.setPlayerNickname(player.getNickname()));
            slaversStateJSON.setPrevPlayerNickname(this.prevPlayerNickname);

            // If the first round is finished, send the dynamic info to the players

//                ArrayList<String> defeatedPlayers = new ArrayList<>();
//                for (Player player : playersToTakeCrewFrom) {
//                    defeatedPlayers.add(player.getNickname());
//                }
//                slaversStateJSON.setDefeatedPlayers(defeatedPlayers); // TODO: Need more thinking on this

                slaversStateJSON.setIsPlayerDefeated(this.isPlayerDefeated);

                setUpdatedRemovedLifeformsIfNecessary(slaversStateJSON, removedLifeforms);
                setUpdatedEliminatedPlayersIfNecessary(slaversStateJSON, eliminatedPlayers);

                // Batteries consumed due to the double cannons
                setUpdatedRemovedBatteriesIfNecessary(slaversStateJSON, removedBatteries);

            // if the smugglers have been defeated we need to set the rewards (if taken)

                setUpdatedPositionsIfNecessary(slaversStateJSON, updatedPositions);
                setUpdatedCreditsIfNecessary(slaversStateJSON, updatedCredits);

        } else {
            // Static info about the card
            slaversStateJSON.setId(this.id);
            slaversStateJSON.setCardName(this.getCardName());
            slaversStateJSON.setImagePath(this.path);
            slaversStateJSON.setCardLevel(this.getCardLevel());
            slaversStateJSON.setRequiredFirepower(requiredFirepower);
            slaversStateJSON.setGivenCredits(this.givenCredits);
            slaversStateJSON.setMovementSteps(this.movementSteps);
            slaversStateJSON.setTakenCrew(this.takenCrew);
        }

        slaversStateJSON.setCardEnded(this.hasFinished());

        return slaversStateJSON;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardID(this.getCardID());
        cardState.setId(this.id);
        cardState.setCardName(this.getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(this.getCardLevel());
        cardState.setRequiredFirepower(requiredFirepower);
        cardState.setGivenCredits(this.givenCredits);
        cardState.setMovementSteps(this.movementSteps);
        cardState.setTakenCrew(this.takenCrew);

        return cardState;
    }


    public WidgetTUI generateWidget(CardStateJSON slaversState) {
        return null;
    }
}
