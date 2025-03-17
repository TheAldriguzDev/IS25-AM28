package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
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


    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
    }

    public EventCard useCard(ActionJSON data) throws ClassCastException {
        //SlaversResponse slaversResponse = (SlaversResponse) response;
        SlaversJSON slaversData = (SlaversJSON) data;
        Optional<Player> playerOptimal = getCurrentPlayer();
        playerOptimal.ifPresent(
                (Player player) -> {
                    if (player.getShip().getFirePower() >= requiredFirepower) {
                        if (slaversData.getTakeCredits()) {
                            bonusEffect();
                            player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect(data);
                    }
                }
        );
        getNextPlayer();
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

    protected void malusEffect() {}

    //
    @Override
    public JSONObject generateState() {
        return null;
    }
}
