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
        CardStateJSON cardState = new CardStateJSON();
        Map<String, PlayerJSON> playerInfo;

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.cardLevel);
        cardState.setCardIsUsable( !this.hasFinished());

        if (this.hasFinished()) {
            // Generate the player info that also includes the ship
//            playerInfo = new HashMap<>();
//
//            for (Player player : this.players) {
//                playerInfo.put(player.getNickname(), PlayerJSON.fromPlayer(player, false));
//            }
//
//            cardState.setPlayersInfo(playerInfo);

        }
        else {
            if (this.getCurrentPlayer().isPresent()) {
                cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
            }
            cardState.setLifeformsToRemove(this.previousPlayeRemovedLifeforms);
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
