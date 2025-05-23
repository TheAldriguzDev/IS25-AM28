package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.is25am28.Model.EventCards.CardTypes.*;

public abstract class EventCard {
    protected final int cardTypeId;
    protected final String cardName;
    protected final int cardLevel;
    protected final Board board;
    protected final int uniqueCardId;
    protected final String path;
    protected List<Player> players;
    protected Optional<Player> currentPlayer;

    protected boolean hasBeenUsed;
    protected boolean hasBeenActivated;

    /**
     * General constructor shared between the classes
     */
    protected EventCard(String cardName, int cardLevel, Board board, int uniqueCardId, String path) {
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.board = board;
        this.uniqueCardId = uniqueCardId;
        this.path = path;

        this.cardTypeId = this.getCardTypeId();
        this.currentPlayer = Optional.empty();
        this.hasBeenUsed = false;
        this.hasBeenActivated = false;
    }

    /**
     * @return This event card's unique identifier
     */
    public int getCardTypeId(){
        return switch (this) {
            case AbandonedShip _    -> 0;
            case AbandonedStation _ -> 1;
            case Epidemy _          -> 2;
            case MeteorShower _     -> 3;
            case OpenSpace _        -> 4;
            case Pirates _          -> 5;
            case Slavers _          -> 6;
            case Smugglers _        -> 7;
            case Stardust _         -> 8;
            case VisitPlanets _     -> 9;
            case WarZone _          -> 10;
            default -> throw new IllegalStateException("ERROR: Unexpected EventCard instance \"" + this + "\"");
        };
    }

    /**
     * @return This card's name
     */
    public String getCardName() {
        return this.cardName;
    }

    /**
     * @return This card's level
     */
    public int getCardLevel() {
        return this.cardLevel;
    }

    /**
     * @return The pointer to the board
     */
    protected Board getBoard() {
        return board;
    }

    /**
     * @return The card's ID (given by the CardLoader)
     */
    public int getUniqueCardId() {
        return uniqueCardId;
    }

    /**
     * Marks the card as used. In this way the game model
     * can understand when to get the next card.
     */
    protected void cardUsed() {
        this.hasBeenUsed = true;
    }

    /**
     * This method is immediately invoked when the card a new card is extracted.
     * Can be overridden to specify different initialization modes (like reverse player order)
     * We do not use the board players list since in some cards the players order could be different
     */
    public void initCardPlayers() throws IllegalArgumentException {
        if (
               (this.board.getPlayers() == null)
            || (this.board.getPlayers().isEmpty())
            || (this.board.getPlayers().size() < 2)
        ) {
            throw new IllegalArgumentException("The player list is null or contains less than two players.");
        }
        else {
            this.players = new ArrayList<>(this.board.getPlayers());
            this.currentPlayer = Optional.of(this.players.getFirst());
        }

        this.activateCard();
    }

    /**
     * Bonus and malus effects are left empty and will be implemented only
     * by those event cards that actually benefit from this distinction.
     */
    protected void bonusEffect() {}
    protected void malusEffect() {}
    protected void bonusEffect(ActionJSON data) {}
    protected void malusEffect(ActionJSON data) {}

    /**
     * Sets the currentPlayer to the next player in the game's turn order.
     * If there are no more players left, set the attribute to an empty optional.
     */
    protected Optional<Player> getNextPlayer() {
        if (players == null || players.isEmpty()) {
            throw new Error("Players are not set, you must call initCardPlayers method before");
        }

        if (currentPlayer.isPresent()) {
            int currentIndex = players.indexOf(currentPlayer.get());

            if (currentIndex == players.size() - 1) {
                this.cardUsed();
                return Optional.empty();
            }
            else {
                Player nextPlayer = players.get(currentIndex + 1);
                currentPlayer = Optional.of(nextPlayer);

                // If the current player is disconnected, then get the next one in line
                if ( !currentPlayer.get().isConnected()) {
                    currentPlayer = this.getNextPlayer();
                }

                return currentPlayer;
            }
        }
        else {
            currentPlayer = Optional.of(players.getFirst());

            // If the first player is disconnected, then get the next one in line
            if ( !currentPlayer.get().isConnected()) {
                currentPlayer = this.getNextPlayer();
            }

            return currentPlayer;
        }
    }

    /**
     * @return An optional wrapper of the current player.
     */
    protected Optional<Player> getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Activate the card so that the generate state can send only a restricted amount of data to the client,
     * instead that sending every time all the static information (has to be done only when first created)
     */
    protected void activateCard() {
        this.hasBeenActivated = true;
    }

    /**
     * @return TRUE if the current card was activated,
     *         FALSE otherwise.
     */
    protected boolean hasBeenActivated() {
        return this.hasBeenActivated;
    }

    /**
     * @return TRUE if the current card has finished being used,
     *         FALSE otherwise.
     */
    public boolean hasFinished() {
        return this.hasBeenUsed;
    }

    /**
     * @param data An ActionJSON containing the data about all actions that the player
     *             decided to take when faced with the current event card.
     *
     * @throws IllegalArgumentException If some data inside the given ActionJSON is incorrect
     *                                  or invalid for the actions the player specified.
     */
    public abstract EventCard useCard(ActionJSON data) throws IllegalArgumentException;

    /**
     * @return Any information that is dynamically updated
     *         throughout the event card's lifecycle.
     *         (this is what enables support for client-side differential updates)
     */
    public abstract CardStateJSON generateState();

    /**
     * @return The generic information that remains static
     *         throughout the event card's lifecycle.
     */
    public abstract CardStateJSON generateStaticState();

    /**
     * Initializes the flags needed to know which fields need to be updated
     * in order to perform a differential update of the card's state.
     */
    protected void initStateFlags(CardStateJSON cardState) {
        // Initializing the three main flags
        cardState.setNeedsShipUpdate(false);
        cardState.setNeedsPlayerUpdate(false);
        cardState.setNeedsBoardUpdate(false);

        // Also initializing lesser flags (to avoid NullPointerExceptions when these are checked)
        // (1) - Lesser flags relative to updateShip (clientSide)
        cardState.setNeedsUpdatedDroppedResources(false);
        cardState.setNeedsUpdatedTakenResources(false);
        cardState.setNeedsUpdatedRemovedLifeforms(false);
        cardState.setNeedsUpdatedBatteries(false);
        cardState.setNeedsUpdatedRemovedComponents(false);

        // (2) - Lesser flags relative to updateBoard (clientSide)
        cardState.setNeedsUpdatedPositions(false);
        cardState.setNeedsUpdatedEliminatedPlayers(false);

        // (3) - Lesser flags relative to updateClient (clientSide)
        cardState.setNeedsUpdatedCredits(false);
    }

    // TODO: IMPORTANT: Instead of new HashMap there should be clear(), but this empties the data set
    //                  in the state, a copy of the data is needed, the states are currently not usable
    /**
     * If the map of updatedPositions is not empty, it means that there is something
     * to send to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so that the
     * old data is not sent back to the clients another time
     */
    protected void setUpdatedPositionsIfNecessary(
            CardStateJSON cardState,
            Map<String, Integer> updatedPositions
    ) {
        if (!updatedPositions.isEmpty()) {
            cardState.setNeedsBoardUpdate(true);
            cardState.setNeedsUpdatedPositions(true);
            cardState.setUpdatedPositions(
                new HashMap<>(
                    updatedPositions.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() % getBoard().getSize()))
                )
            );
            updatedPositions.clear();
        }
    }

    /**
     * If the list of eliminatedPlayers is not empty, it means that there is something
     * to send to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedEliminatedPlayersIfNecessary(
            CardStateJSON cardState,
            List<String> eliminatedPlayers
    ) {
        if (!eliminatedPlayers.isEmpty()) {
            cardState.setNeedsBoardUpdate(true);
            cardState.setNeedsUpdatedEliminatedPlayers(true);
            cardState.setEliminatedPlayers(new ArrayList<>(eliminatedPlayers));
            eliminatedPlayers.clear();
        }
    }

    /**
     * If the map of updatedCredits, it means that there is something to send to the clients, so
     * we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedCreditsIfNecessary(
            CardStateJSON cardState,
            Map<String, Integer> updatedCredits
    ) {
        if (!updatedCredits.isEmpty()) {
            cardState.setNeedsPlayerUpdate(true);
            cardState.setNeedsUpdatedCredits(true);
            cardState.setUpdatedCredits(new HashMap<>(updatedCredits));
            updatedCredits.clear();
        }
    }

    /**
     * If the map of droppedResources is not empty, it means that there is something to send
     * to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedDroppedResourcesIfNecessary(
            CardStateJSON cardState,
            Map<String, List<ComponentHelper<ItemColor>>> droppedResources
    ) {
        if (!droppedResources.isEmpty()) {
            cardState.setNeedsShipUpdate(true);
            cardState.setNeedsUpdatedDroppedResources(true);
            cardState.setDroppedResources(new HashMap<>(droppedResources));
            droppedResources.clear();
        }
    }

    /**
     * If the map of takenResources is not empty, it means that there is something to send
     * to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedTakenResourcesIfNecessary(
            CardStateJSON cardState,
            Map<String, List<ComponentHelper<ItemColor>>> takenResources
    ) {
        if (!takenResources.isEmpty()) {
            cardState.setNeedsShipUpdate(true);
            cardState.setNeedsUpdatedTakenResources(true);
            cardState.setTakenResources(new HashMap<>(takenResources));
            takenResources.clear();
        }
    }

    /**
     * If the map of removedComponents is not empty, it means that there is something to send
     * to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedRemovedComponentsIfNecessary(
            CardStateJSON cardState,
            Map<String, List<Map<String, Object>>> removedComponents
    ) {
        if (!removedComponents.isEmpty()) {
            cardState.setNeedsShipUpdate(true);
            cardState.setNeedsUpdatedRemovedComponents(true);
            cardState.setRemovedComponents(new HashMap<>(removedComponents));
            removedComponents.clear();
        }
    }

    /**
     * If the map of removedLifeforms is not empty, it means that there is something to send
     * to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedRemovedLifeformsIfNecessary(
            CardStateJSON cardState,
            Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms
    ) {
        if (!removedLifeforms.isEmpty()) {
            cardState.setNeedsShipUpdate(true);
            cardState.setNeedsUpdatedRemovedLifeforms(true);
            cardState.setRemovedLifeforms(new HashMap<>(removedLifeforms));
            removedLifeforms.clear();
        }
    }

    // TODO: The name should be UpdatedBatteries since it contains the new batteries
    //       count, but i will be changed in the future to contain the coordinates of the
    //       actual components from which to remove the batteries, so in theory a name
    //       change is not necessary.
    /**
     * If the map of removedBatteries is not empty, it means that there is something to send
     * to the clients, so we set the field in the cardState.
     * <br>
     * This method clears the list after being used (if it wasn't empty to begin with), so
     * that the old data is not sent back to the clients another time.
     */
    protected void setUpdatedRemovedBatteriesIfNecessary(
            CardStateJSON cardState,
            Map<String, List<CoordinatePair>> removedBatteries
    ) {
        if (!removedBatteries.isEmpty()) {
            cardState.setNeedsShipUpdate(true);
            cardState.setNeedsUpdatedBatteries(true);
            cardState.setRemovedBatteries(new HashMap<>(removedBatteries));
            removedBatteries.clear();
        }
    }

    // TODO: Add JavaDoc just like the other methods above
    protected void setUpdatedLostPiecesIfNecessary(
            CardStateJSON cardState,
            Map<String, Integer> lostPieces
    ) {
        if (!lostPieces.isEmpty()) {
            cardState.setNeedsPlayerUpdate(true);
            cardState.setNeedsUpdatedLostPieces(true);
            cardState.setUpdatedLostPieces(new HashMap<>(lostPieces));
            lostPieces.clear();
        }
    }

    // TODO: eventCards should also set the lapped eliminated players

    // TODO: method: unlock additional commands
}
