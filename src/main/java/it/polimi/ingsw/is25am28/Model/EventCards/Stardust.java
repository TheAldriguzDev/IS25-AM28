package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.StardustJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.*;

public class Stardust extends EventCard {
    private final Map<String, Integer> updatedPositions;
    private final List<String> eliminatedPlayers;

    // Constructor
    public Stardust(String name, int cardLevel, Board board, int uniqueCardId, String path) {
        super(name, cardLevel, board, uniqueCardId, path);
        this.updatedPositions = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
    }

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        StardustJSON stardustData;

        try {
            stardustData = (StardustJSON) data;
        }
        catch (ClassCastException e) {
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
                    getBoard().movePlayerBackward(player, movementSteps);

                    if (movementSteps != 0) {
                        this.updatedPositions.put(player.getNickname(), player.getCursor());
                    }

                    if (player.equals(this.players.getLast())) {
                        this.cardUsed(); // Mark the card as used

                        // Revalidate the board position and add the lapped players to the eliminated players
                        this.eliminatedPlayers.addAll(this.getBoard().validatePlayersPosition());
                    }
                    else {
                        this.getNextPlayer();
                    }
                },
                () -> {
                    throw new IllegalArgumentException("here is no player playing in this moment");
                }
        );

        return this;
    }

    /**
     * This method will be used in the specific class, but also from outside (game model).
     * It returns true if the current player is the last one of the card players or if there are no active players in the card
     * */

    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if (getBoard().getPlayers() == null || getBoard().getPlayers().isEmpty() || getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        }
        else {
            this.players = new ArrayList<>(getBoard().getPlayers());
            Collections.reverse(this.players);
            this.currentPlayer = Optional.of(this.players.getFirst());
        }

        activateCard();
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON stardustStateJSON = new CardStateJSON();

        stardustStateJSON.setUniqueCardId(this.uniqueCardId);

        if (hasBeenActivated()) {
            // Initializing the state flags
            initStateFlags(stardustStateJSON);

            playerOptional.ifPresent(player -> stardustStateJSON.setPlayerNickname(player.getNickname()));

            // Setting the JSON's fields only if necessary
            setUpdatedPositionsIfNecessary(stardustStateJSON, updatedPositions);

            if (this.hasFinished()) {
                setUpdatedEliminatedPlayersIfNecessary(stardustStateJSON, this.eliminatedPlayers);
            }
        }
        else {
            stardustStateJSON.setCardTypeId(this.cardTypeId);
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
        
        cardState.setCardTypeId(this.cardTypeId);
        cardState.setUniqueCardId(this.uniqueCardId);
        cardState.setCardName(getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(getCardLevel());
        cardState.setImagePath(this.path);

        return cardState;
    }
}