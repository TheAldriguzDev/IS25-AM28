package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.StardustJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class Stardust extends EventCard {

    public Stardust(String name, int cardLevel, Board board) {
        super(name, cardLevel, board);

    }

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        StardustJSON stardustData;
        try {
            stardustData = (StardustJSON) data;
        } catch (ClassCastException e) {
            throw new ClassCastException("Card data type in invalid");
        }
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresentOrElse(
                (Player player) -> {
                    String playerNickname = stardustData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }

                    int movementSteps = player.getShip().getExposedConnectorAmount();
                    getBoard().movePlayerBackwards(player, movementSteps);
                    if (player.equals(this.players.getLast())) {
                        this.cardUsed(); // Mark the card as used
                        this.getBoard().validatePlayersPosition();
                    } else {
                        this.getNextPlayer();
                    }
                },
                () -> {
                    throw new IllegalArgumentException("here is no player playing in this moment");
                }
        );
        return this;
    }

    @Override
    protected void bonusEffect() {}

    @Override
    protected void malusEffect() {}

    /**
     * This method will be used in the specific class, but also from outside (game model).
     * It returns true if the current player is the last one of the card players or if there are no active players in the card
     * */

    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( getBoard().getPlayers() == null || getBoard().getPlayers().isEmpty() || getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            this.players = new ArrayList<>(getBoard().getPlayers());
            Collections.reverse(players);
            currentPlayer = Optional.of(players.getFirst());
        }
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON stardustStateJSON = new CardStateJSON();
        if(playerOptional.isPresent()) {
            stardustStateJSON.setPlayerNickname(playerOptional.get().getNickname());
        }
            stardustStateJSON.setCardName(getCardName());
            stardustStateJSON.setCardLevel(getCardLevel());
            stardustStateJSON.setCardIsUsable(!hasFinished());
        return stardustStateJSON;
    }

    public WidgetTUI generateWidget(CardStateJSON stardustJSON) {
        return null;
    }
}