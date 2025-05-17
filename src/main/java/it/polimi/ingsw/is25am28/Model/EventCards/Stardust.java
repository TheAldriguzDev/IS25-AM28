package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.StardustJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.*;

public class Stardust extends EventCard {
    private Map<String, Integer> updatedPositions;
    private List<String> eliminatedPlayers;

    public Stardust(String name, int cardLevel, Board board, int cardID, String path) {
        super(name, cardLevel, board, cardID, path);
        this.updatedPositions = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
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
                    if (movementSteps != 0) {
                        this.updatedPositions.put(player.getNickname(), player.getCursor());
                    }

                    if (player.equals(this.players.getLast())) {
                        this.cardUsed(); // Mark the card as used
                        int tmp = getBoard().getEliminatedPlayers().size();
                        this.getBoard().validatePlayersPosition();
                        for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // FIXME: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                            this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
                        }
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
        activateCard();
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON stardustStateJSON = new CardStateJSON();
        stardustStateJSON.setCardID(this.getCardID());

        if (hasBeenActivated()) {
            // Initializing the state flags
            initStateFlags(stardustStateJSON);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> stardustStateJSON.setPlayerNickname(player.getNickname()));

            // Sets the updatedPositions (if there are any)
            setUpdatedPositionsIfNecessary(stardustStateJSON, updatedPositions);

            // Sets the eliminatedPlayer (if there are any)
            setUpdatedEliminatedPlayersIfNecessary(stardustStateJSON, this.eliminatedPlayers);
        } else {
            stardustStateJSON.setId(this.cardTypeId);
            stardustStateJSON.setCardName(getCardName());
            stardustStateJSON.setImagePath(this.path);
            stardustStateJSON.setCardLevel(getCardLevel());
        }

        stardustStateJSON.setCardEnded(this.hasFinished());

        return stardustStateJSON;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardID(this.getCardID());
        cardState.setId(this.cardTypeId);
        cardState.setCardName(getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(getCardLevel());

        return cardState;
    }
}