package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Response.PiratesResponse;

import java.util.ArrayList;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final int numberOfShots;
    /*
    * Liste che i PlasmaShot che arrivano dalle 4 direzioni:
    * 0 -> piccolo,
    * 1 -> grande
    * */
    ArrayList<Integer> shootingSequenceFromAbove = new ArrayList<>();
    ArrayList<Integer> shootingSequenceFromBelow = new ArrayList<>();
    ArrayList<Integer> shootingSequenceFromRigh = new ArrayList<>();
    ArrayList<Integer> shootingSequenceFromLeft = new ArrayList<>();

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, int numberOfShots) {
        super(name, cardLevel);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.numberOfShots = numberOfShots;
    }

    public EventCard useCard(Object response) throws ClassCastException {
        PiratesResponse piratesResponse = (PiratesResponse) response;
        Player player = getCurrent();
        if (player.getShip().getFirePower() >= requiredFirepower) {
            if (piratesResponse.getTakeCredits()) {
                bonusEffect();
                player.setCursor(player.getCursor() - this.movementSteps);
            }
        } else {
            malusEffect();
        }
        getNext();
        return this;
    }

    protected void bonusEffect() {
        Player player = getCurrent();
        player.setCredits(player.getCredits() + this.givenCredits);
    }

    protected void malusEffect(PiratesResponse piratesResponse) {
        Player player = getCurrent();
        int dicesResult = 0;
        Component component;
        ArrayList<Integer> dicesResults = piratesResponse.getDicesResults(); // La parte client si assicura di fare tiri pari al numero di PlasmaShots
        // side // 0->Above, 1->Below, 2->Right, 3->Left
        // Coordinates
        for (int plasmaShot : shootingSequenceFromAbove) {
            if ((plasmaShot == 0 && !piratesResponse.getShieldAbove()) || plasmaShot == 1) {
                int column = dicesResults.get(dicesResult);
                for (int row = 4; row < 9; row++) {
                    try {
                        player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                        player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                    } catch (NullComponentException e) {
                        // Nella casella non c'è un componente, non va fatto nulla
                    }
                }
            }
            dicesResult++;
        }

        for (int plasmaShot : shootingSequenceFromBelow) {
            if ((plasmaShot == 0 && !piratesResponse.getShieldAbove()) || plasmaShot == 1) {
                int column = dicesResults.get(dicesResult);
                for (int row = 8; row > 3; row--) {
                    try {
                        player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                        player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                    } catch (NullComponentException e) {
                        // Nella casella non c'è un componente, non va fatto nulla
                    }
                }
            }
            dicesResult++;
        }

        for (int plasmaShot : shootingSequenceFromRigh) {
            if ((plasmaShot == 0 && !piratesResponse.getShieldAbove()) || plasmaShot == 1) {
                int row = dicesResults.get(dicesResult);
                for (int column = 3; column < 10; column++) {
                    try {
                        player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                        player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                    } catch (NullComponentException e) {
                        // Nella casella non c'è un componente, non va fatto nulla
                    }
                }
            }
            dicesResult++;
        }

        for (int plasmaShot : shootingSequenceFromLeft) {
            if ((plasmaShot == 0 && !piratesResponse.getShieldAbove()) || plasmaShot == 1) {
                int row = dicesResults.get(dicesResult);
                for (int column = 9; column > 2; column--) {
                    try {
                        player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                        player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                    } catch (NullComponentException e) {
                        // Nella casella non c'è un componente, non va fatto nulla
                    }
                }
            }
            dicesResult++;
        }


    }

    protected void malusEffect() {}

    @Override
    public Object generateState() {
        return null;
    }
}
