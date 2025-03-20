package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.Optional;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
    private boolean hasBeenDefeated;


    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
        this.hasBeenDefeated = false;
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
                    System.out.println("Turn of:" + player.getNickname() + " -> is enemy Defeated?: " + hasFinished());
                    String playerNickname = slaversData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }

                    if (player.getShip().getFirePower(slaversData.getNumberOfDoubleCannonsActivated()) >= requiredFirepower) {
                        this.hasBeenDefeated = true;
                        if (slaversData.getTakeCredits()) {
                            bonusEffect();
                            getBoard().movePlayerBackwards(player, movementSteps);
                            //player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect(data);
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        //System.out.println("Player using card: " + getCurrentPlayer().get().getNickname() + "Player ID: " + getCurrentPlayer().get());
        //System.out.println(players);
        getNextPlayer();
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
                    for (Cabin cabin : slaversData.getCrewToRemove()) {
                        cabin.removeInhabitant(cabin.getInhabitants().getFirst());
                    }
                }
        );
    }

    @Override
    protected void malusEffect() {}

    @Override
    public boolean hasFinished() {
        return super.hasFinished() || this.hasBeenDefeated;
    }

    //
    @Override @SuppressWarnings("unchecked")
    public CardStateJSON generateState() {
        JSONObject slaversState = new JSONObject();

        if(getCurrentPlayer().isPresent()) {
            slaversState.put("playerNickname", getCurrentPlayer().get().getNickname());
        }
        slaversState.put("cardName", this.name);
        slaversState.put("cardLevel", cardLevel);
        slaversState.put("requiredFirepower", requiredFirepower);
        slaversState.put("movementSteps", movementSteps);
        slaversState.put("givenCredits", givenCredits);
        slaversState.put("takenCrew", takenCrew);
        slaversState.put("hasBeenDefeated", hasBeenDefeated);

        //return slaversState;
        return null;
    }
}
