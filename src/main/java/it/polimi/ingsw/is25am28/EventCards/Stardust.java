package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.StardustJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Ship.Ship;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.simple.JSONObject;

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

                    AtomicInteger movementSteps = new AtomicInteger();
                    Ship ship = player.getShip();
                    ship.traverse(
                            (Component c) -> {
                                // For each exposed side movementsSteps++;
                                Component[] otherC = ship.getNearestComponents(c);
                                if (otherC[0] == null) {
                                    if (c.getTopSide() != 0) {
                                        movementSteps.getAndIncrement();
                                    }
                                }
                                if (otherC[1] == null) {
                                    if (c.getRightSide() != 0) {
                                        movementSteps.getAndIncrement();
                                    }
                                }
                                if (otherC[2] == null) {
                                    if (c.getBottomSide() != 0) {
                                        movementSteps.getAndIncrement();
                                    }
                                }
                                if (otherC[3] == null) {
                                    if (c.getLeftSide() != 0) {
                                        movementSteps.getAndIncrement();
                                    }
                                }
                            }
                    );
                    player.setCursor(player.getCursor() - movementSteps.get());
                },
                () -> {
                    throw new IllegalArgumentException("here is no player playing in this moment");
                }
        );
        getNextPlayer();
        return this;
    }

    @Override
    protected void bonusEffect() {}

    @Override
    protected void malusEffect() {}

    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( getBoard().getPlayers() == null || getBoard().getPlayers().isEmpty() || getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            this.players = getBoard().getPlayers();
            Collections.reverse(this.players);
            currentPlayer = Optional.of(players.getFirst());
        }
    }

    @Override
    public JSONObject generateState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardLevel(getCardLevel());
        cardState.setCardName(getCardName());



        return null;
    }
}
