package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    List<PlasmaShot> shootingSequence = new ArrayList<>();

    public Pirates(String name, int cardLevel, int requireFirepower, int givenCredits, int movementSteps) {
        super(name, cardLevel);
        this.requiredFirepower = requireFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
    }

    public void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getFirePower() >= requiredFirepower) {
//                if(getChoice()) {
//                    bonusEffect(player);
//                    player.setCursor(player.getCursor() - this.movementSteps);
//                }
                break;
            }
            malusEffect(player);
        }
    }

    protected void bonusEffect(Player player) {
        player.setCredits(player.getCredits() + this.givenCredits);
    }

    protected void malusEffect(Player player) {
        // Expose to ShootingSequence
    }


    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}
