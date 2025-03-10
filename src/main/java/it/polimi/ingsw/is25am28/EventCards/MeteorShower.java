package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.EventCard;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeteorShower extends EventCard {
    private final List<Meteor> meteorSequence = new ArrayList<>();

    protected void useCard(Player[] players) {
        Random dice = new Random();
        int strikePath; // Colonna o riga che il meteorite percorrerà
        for (Meteor meteor : meteorSequence) {
            strikePath = dice.nextInt(6) + 1 + dice.nextInt(6) + 1; // Doppio lancio di dadi
            for (Player player : players) {
                // Verifica miss
                // Verifica componente colpito/scudi/cannone
                // Caso di size grande
            }
        }
    }

    protected void bonusEffect(Player player) {}

    protected void malusEffect(Player player) {
        // Logica distruzione
    }
}
