package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cabin;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

public class Epidemy extends EventCard {
    private List<ComponentHelper<LifeformType>> previousPlayeRemovedLifeforms;
    private Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms;

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

    /**
     * Calls the useCard method but without passing an ActionJSON to it
     * (since this card doesn't require user input to work)
     */
    public EventCard useCard() {
        return this.useCard(null);
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        Set<Cabin> alreadyQuarantined;
        List<Cabin> cabinList;
        Component[] neighbours;
        Ship shipPtr;

        // Skips any player marked as disconnected during their turn
        if (this.currentPlayer.isPresent() && this.currentPlayer.get().isConnected()) {
            alreadyQuarantined = new HashSet<>();
            shipPtr = this.currentPlayer.get().getShip();
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
            previousPlayeRemovedLifeforms = new ArrayList<>();
            // Removing a lifeform for each cabin placed in quarantine
            for (Cabin cabin : alreadyQuarantined) {
                previousPlayeRemovedLifeforms.add(new ComponentHelper<LifeformType>(cabin.getPosition()[0], cabin.getPosition()[1]).addItem(cabin.getInhabitants().getFirst().getLifeformType()));
                shipPtr.removeLifeformFromCabin(
                        cabin.getPosition()[0],
                        cabin.getPosition()[1],
                        cabin.getInhabitants().getFirst().getLifeformType()
                );
            }
            removedLifeforms = new HashMap<>();
            removedLifeforms.put(this.getCurrentPlayer().get().getNickname(), previousPlayeRemovedLifeforms);
        }

        // Getting the next player (in order of leaderboard placements)
        this.currentPlayer = this.getNextPlayer();

        // Set this card as used only if all players have used it
        if (this.currentPlayer.isEmpty()) {
            this.cardUsed();
        }

        return this;
    }

    /**
     * @return The card's current state
     */
    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON cardState = new CardStateJSON();

        if (hasBeenActivated()) {
            initStateFlags(cardState);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));

            setUpdatedRemovedLifeformsIfNecessary(cardState, this.removedLifeforms);
        } else {
            cardState.setId(this.id);
            cardState.setCardName(this.getCardName());
            cardState.setCardLevel(this.cardLevel);
        }
        return cardState;
    }

    /**
     * @return The card's widget
     */
    public WidgetTUI generateWidget(CardStateJSON epidemyStateJSON) {
        return null;
    }
}
