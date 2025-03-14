package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class Smugglers extends EventCard {
    private final int requiredFirepower;
    private final int movementStep;
    private List<Cargo> givenCargo = new ArrayList<>();
    private List<Cargo> takenCargo = new ArrayList<>();

    public Smugglers(String name, int cardLevel, int requiredFirepower, int movementStep) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.requiredFirepower = requiredFirepower;
        this.movementStep = movementStep;
    }

    public void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getFirePower() >= requiredFirepower) {
                if(getChoice()) {
                    bonusEffect(player);
                    player.setCursor(player.getCursor() - this.movementStep);
                }
                break;
            }
            malusEffect(player);
        }
    }


    protected void bonusEffect(Player player) {
        //GiveCargo
    }

    protected void malusEffect(Player player) {
        //TakeCargo
    }


}
