package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player;

/*
 * ====== NOTE ======
 *  The EventCard "Sabotage" is a level 3 exclusive special event card
 *  therefore, since the game is developed only for level 2 difficulty, it
 *  will not be implemented.
 * ==================
 */

public class Sabotage extends EventCard {

    public Sabotage(String name, int cardLevel) {
        super(name, cardLevel);
    }

    @Override
    void useCard(Player[] players) {

    }

    @Override
    void bonusEffect(Player player) {

    }

    @Override
    void malusEffect(Player player) {

    }
}
