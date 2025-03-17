package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    //private ArrayList<Item> planetItem = new ArrayList<Item>();

    public AbandonedStation(String name, int cardLevel, int requiredCrew, int movementStep, int red, int green, int blue, int yellow ) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.requiredCrew = requiredCrew;
        this.movementStep = movementStep;


    }

    public void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getAllLifeforms().stream().count() > requiredCrew) {
                //method getChoice: ask player to make a choice
                if (getChoice()) {
                    bonusEffect(player);
                    player.setCursor(player.getCursor() - this.movementStep);
                    break;
                }
            }
        }
    }

    protected void bonusEffect(Player player) {
        // List<Item> = player.getShip().getItem();
        // if()
        // Need Item class info to continue

    }

    protected void malusEffect(Player player) {}


}

