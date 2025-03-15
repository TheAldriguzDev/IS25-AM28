package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Response.SlaversResponse;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;


    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
    }

    public EventCard useCard(Object response) throws ClassCastException {
        SlaversResponse slaversResponse = (SlaversResponse) response;
        Player player = getCurrent();
        if (player.getShip().getFirePower() >= requiredFirepower) {
            if (slaversResponse.getTakeCredits()) {
                bonusEffect();
                player.setCursor(player.getCursor() - this.movementSteps);
            }
        } else {
            malusEffect(slaversResponse);
        }
        getNext();
        return this;
    }

    protected void bonusEffect() {
        Player player = getCurrent();
        player.setCredits(player.getCredits() + this.givenCredits);
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

    protected void malusEffect(SlaversResponse slaversResponse) {
        Player player = getCurrent();
        for (Cabin cabin : slaversResponse.getCrewToRemove()) {
            cabin.removeInhabitant(cabin.getInhabitants().getFirst());
        }
    }

    protected void malusEffect() {}

    //
    @Override
    public Object generateState() {
        return null;
    }
}
