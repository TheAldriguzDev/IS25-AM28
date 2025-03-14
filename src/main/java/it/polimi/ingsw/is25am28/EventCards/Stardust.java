package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Component;
import java.util.ArrayList;

public class Stardust extends EventCard {

    public Stardust(String name, int cardLevel) {
        this.name = name;
        this.cardLevel = cardLevel;
    }

    public void useCard(Player[] players) {
        ArrayList<Integer> offsetsList = new ArrayList<>();
        for (Player player : players) {
//            offsetsList.add(player.getShip().traverse(
//                    (Component c) -> {
//                        // Checks for each component the number of exposed sides
//                        // Return
//                    }
//            ));
        }
        // Inverse iteration necessary for the positions update
        for (int i = players.length - 1; i >= 0; i--) {
            players[i].setCursor(players[i].getCursor() - offsetsList.get(i));
        }
    }

    protected void bonusEffect(Player player) {}

    protected void malusEffect(Player player) {}

}
