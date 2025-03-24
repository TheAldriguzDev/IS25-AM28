package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;

import java.util.*;

public class Epidemy extends EventCard {

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
        return this;
    }

    public EventCard useCard() throws IllegalArgumentException {
        Set<Cabin> alreadyQuarantined;
        List<Cabin> cabinList;
        Component[] neighbours;
        Ship shipPtr;

        // Finding all neighbouring cabins and putting them into quarantine
        for (Player player : this.getBoard().getPlayers()) {
            alreadyQuarantined = new HashSet<>();
            shipPtr = player.getShip();
            cabinList = shipPtr.getCabinList();

            for (Cabin cabin : cabinList) {
                if (!alreadyQuarantined.contains(cabin)) {
                    neighbours = shipPtr.getNearestComponents(cabin);
                    for (Component neighbour : neighbours) {
                        switch (neighbour) {
                            case Cabin neighbourCabin -> {
                                // Assuming, like in the card's picture, that Epidemy strikes only
                                // when the cabin is FULLY occupied (i.e.: there's no available space)
                                // This means that cabins with 1 astronaut are SAFE from the Epidemy
                                if (cabin.getAvailableSpace() == 0 && neighbourCabin.getAvailableSpace() == 0) {
                                    alreadyQuarantined.add(cabin);
                                    alreadyQuarantined.add(neighbourCabin);
                                }
                            }
                            case null, default -> {}
                        }
                    }
                }
            }

            // Removing a lifeform for each cabin placed in quarantine
            for (Cabin cabin : alreadyQuarantined) {
                cabin.removeInhabitant(cabin.getInhabitants().getFirst());
            }
        }

        // Set this card as used
        this.cardUsed();

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.cardLevel);

        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        return cardState;
    }
}
