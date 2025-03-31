package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
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
        Lifeform lifeformToRemove;
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
                                // If the neighbouring cabins have at least one lifeform inside, then
                                // both cabins must be placed in quarantine
                                if (cabin.getAvailableSpace() != 2 && neighbourCabin.getAvailableSpace() != 2) {
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
                lifeformToRemove = cabin.getInhabitants().getFirst();

                switch (lifeformToRemove.getLifeformType()) {
                    case ASTRONAUT -> {
                        cabin.removeInhabitant(lifeformToRemove);
                    }
                    case PURPLE_ALIEN, BROWN_ALIEN -> {
                        shipPtr.removeAlienOfType(lifeformToRemove.getLifeformType());
                    }
                    case null, default -> throw new IllegalArgumentException("ERROR: Unidentified lifeform type");
                }
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
