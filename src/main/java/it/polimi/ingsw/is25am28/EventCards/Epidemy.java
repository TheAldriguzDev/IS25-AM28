package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.json.simple.JSONObject;

import java.util.*;

public class Epidemy extends EventCard {

    // Constructor
    public Epidemy(String name, int cardLevel, Board board) {
        super(name, cardLevel, board);
    }

    @Override
    protected void bonusEffect() {
        // Nothing
    }

    @Override
    protected void malusEffect() {
        // Nothing
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        return null;
    }

    public void useCard() {
        List<Player> players = this.getBoard().getPlayers();
        Set<Cabin> alreadyQuarantined;
        List<Cabin> cabinList;
        Component[] neighbours;
        Ship shipPtr;

        // Finding all neighbouring cabins and putting them into quarantine
        // ready to delete
        for (Player player : players) {
            alreadyQuarantined = new HashSet<>();
            shipPtr = player.getShip();
            cabinList = shipPtr.getCabinList();

            for (Cabin cabin : cabinList) {
                if (!alreadyQuarantined.contains(cabin)) {
                    neighbours = shipPtr.getNearestComponents(cabin);
                    for (Component neighbour : neighbours) {
                        switch (neighbour) {
                            case Cabin neighbourCabin -> {
                                alreadyQuarantined.add(cabin);
                                alreadyQuarantined.add(neighbourCabin);
                            }
                            case null, default -> {
                            }
                        }
                    }
                }
            }

            // Removing a lifeform for each cabin placed in quarantine
            for (Cabin cabin : alreadyQuarantined) {
                cabin.removeInhabitant(cabin.getInhabitants().getFirst());
            }
        }
    }

    @Override
    public CardStateJSON generateState() {
        return null;
    }
}