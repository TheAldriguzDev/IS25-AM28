package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.NoSuchElementException;
import java.util.Optional;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;

    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
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

                    if (player.getShip().getFirePower(slaversData.getNumberOfDoubleCannonsActivated()) >= requiredFirepower) {
                        cardUsed();
                        if (slaversData.getTakeCredits()) {
                            bonusEffect();
                            getBoard().movePlayerBackwards(player, movementSteps);
                            getBoard().validatePlayersPosition();
                        }
                    } else {
                        malusEffect(data);
                    }
                    if (player.equals(this.players.getLast())) {
                        this.cardUsed(); // Mark the card as used
                        this.getBoard().validatePlayersPosition();
                    } else {
                        this.getNextPlayer();
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
        playerOptional.ifPresent(
                (Player player) -> {
//                    for (Cabin cabin : slaversData.getCrewToRemove()) {
//                        cabin.removeInhabitant(cabin.getInhabitants().getFirst());
//                    }
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
        CardStateJSON slaversStateJSON;
        if(playerOptional.isPresent()) {
            slaversStateJSON = new CardStateJSON(
                    playerOptional.get().getNickname(),
                    getCardName(),
                    getCardLevel(),
                    !hasFinished(),
                    this.requiredFirepower,
                    this.givenCredits,
                    this.movementSteps,
                    this.takenCrew);
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }
        return slaversStateJSON;
    }
}
