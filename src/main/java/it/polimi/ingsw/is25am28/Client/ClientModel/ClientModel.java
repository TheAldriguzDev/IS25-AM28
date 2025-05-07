package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard.ClientBoard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientStorage;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.Components.Storage;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.*;

/**
 * This class represent the client-side model. It will contain all the data that are needed to handle the game from the
 * client perspective.
 * */
public class ClientModel {
    // Nickname of the client
    private String nickname;
    private int difficultyLevel;
    private ClientState currState;
    private TimerDTO timerDTO;

    // Map that stores the client nicknames with their ClientPlayer data structure
    private final Map<String, ClientPlayer> players;
    private final List<ClientEventCard> eventCards;
    private ClientBoard board;

    // TODO: ClientBoard - ClientShip - ClientComponent --> For ships and playerColor i would store them inside Maps to identify each user data

    public ClientModel() {
        this.players = new HashMap<>();
        this.eventCards = new ArrayList<>();
        this.board = null;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getDifficultyLevel() {
        return this.difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * @return the current client state
     * */
    public ClientState getState() {
        return this.currState;
    }

    /**
     * Sets the current state of the model
     * */
    public void setState(ClientState state) {
        this.currState = state;
    }

    /**
     * Add the given player to the game
     * */
    public void addNewPlayer(String nickname, PlayerColor color) {
        synchronized (this.players) {
            if (!this.players.containsKey(nickname)) {
                this.players.put(nickname, new ClientPlayer(nickname, color, this.difficultyLevel));
            }
        }
    }

    /**
     * Add the given player to the game
     * */
    public void addNewPlayer(String nickname, PlayerColor color, int credits, int lostPieces, List<Map<String, Object>> ship) {
        synchronized (this.players) {
            if (!this.players.containsKey(nickname)) {
                this.players.put(nickname, new ClientPlayer(nickname, color, this.difficultyLevel, credits, lostPieces, ship));
            }
        }
    }

    /**
     * @return The ship belonging to the given player
     */
    public Optional<ClientShip> getShipOfPlayer(String playerNickname) {
        synchronized (this.players) {
            return Optional.ofNullable(this.players.get(playerNickname)).map(ClientPlayer::getShip);
        }
    }

    /**
     * @return A list of all players by their nickname
     */
    public List<String> getAllPlayersNicknames() {
        return this.players.keySet().stream().toList();
    }

    /**
    * @return Returns the clientPlayers map(nickname, data)
    * */
    public Map<String, ClientPlayer> getAllClientPlayers() {
        return players;
    }

    /**
     * @param timerDTO The timerDTO containing all the data about the
     *                 last received timer event from the server-side
     *                 ShipConstructionState
     */
    public void setTimerDTO(TimerDTO timerDTO) {
        this.timerDTO = timerDTO;
    }

    /**
     * @return The earliest timerDTO arrived to this client
     */
    public TimerDTO getTimerDTO() {
        return this.timerDTO;
    }

    /**
     * @return All client event cards
     */
    public List<ClientEventCard> getClientEventCards() {
        return this.eventCards;
    }

    /**
     * @return The client model board
     */
    public ClientBoard getClientBoard() {
        return this.board;
    }

    /**
     * Sets the current client board to the given one
     */
    public void setClientBoard(ClientBoard board) {
        this.board = board;
    }

    /**
     * Updates the current clientPlayer stats (only the credits, the other stats are updated by the clientShip
     * (Batteries, lostComponents, dropped/taken resources, removedLifeforms) or clientBoard
     * (Cursor, eliminatedPlayers)) (if needed)
     * */
    public void updatePlayers(CardStateJSON cardStateJSON) {
        if (cardStateJSON.getNeedsUpdatedCredits()) {
            for (String playerNickname : cardStateJSON.getUpdatedCredits().keySet()) {
                this.players.get(playerNickname).setCredits(cardStateJSON.getUpdatedCredits().get(playerNickname));
            }
        }
    }

    public void updateShips(CardStateJSON cardStateJSON) {
        // Removes the destroyed components from the specified ship
        if (cardStateJSON.getNeedsUpdatedRemovedComponents()) {
            for (String playerNickname : cardStateJSON.getRemovedComponents().keySet()) {
                for (Map<String, Object> componentToRemove : cardStateJSON.getRemovedComponents().get(playerNickname)) {
                    this.getShipOfPlayer(playerNickname).ifPresent(
                            ship -> {
                                ship.removeComponent((int) componentToRemove.get("row"), (int) componentToRemove.get("col"));
                            }
                    );
                }
            }
        }

        // Removes the specified lifeForms from the specified ships
        if (cardStateJSON.getNeedsUpdatedRemovedLifeforms()) {
            Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms = cardStateJSON.getRemovedLifeforms();
            for (String playerNickname : removedLifeforms.keySet()) {
                for (ComponentHelper<LifeformType> lifeFormToRemove : removedLifeforms.get(playerNickname)) {
                    this.getShipOfPlayer(playerNickname).ifPresent(
                            ship -> {
                                ship.removeLifeformFromCabin(lifeFormToRemove.getI(), lifeFormToRemove.getJ(), lifeFormToRemove.getItem().orElse(null));
                            }
                    );
                }
            }
        }

        // Removes the specified resources from the specified ships
        if (cardStateJSON.getNeedsUpdatedDroppedResources()) {
            for (String playerNickname : cardStateJSON.getDroppedResources().keySet()) {
                for(ComponentHelper<ItemColor> itemToDrop : cardStateJSON.getDroppedResources().get(playerNickname)) {
                    this.getShipOfPlayer(playerNickname).ifPresent(
                            ship -> {
                                ClientStorage s = (ClientStorage) ship.getComponent(itemToDrop.getI(), itemToDrop.getJ());
                                ItemColor color = itemToDrop.getItem().orElse(null);
                                Optional<Item> foundItem = s.getStoredItems().stream()
                                        .filter( item -> item.getColor().equals(color))
                                        .findFirst();
                                // Remove the resource from the player
                                foundItem.ifPresent(s::removeItem);
                            }
                    );
                }
            }
        }

        // Adds the specified resources to the specified ships
        if (cardStateJSON.getNeedsUpdatedTakenResources()) {
            for (String playerNickname : cardStateJSON.getTakenResources().keySet()) {
                for(ComponentHelper<ItemColor> itemToTake : cardStateJSON.getTakenResources().get(playerNickname)) {
                    this.getShipOfPlayer(playerNickname).ifPresent(
                            ship -> {
                                ClientStorage s = (ClientStorage) ship.getComponent(itemToTake.getI(), itemToTake.getJ());
                                ItemColor color = itemToTake.getItem().orElse(null);
                                // Add resource to the player
                                s.storeItem(new Item(color));
                            }
                    );
                }
            }
        }

        // Removes the specified amount of batteries form the specified ships
        if (cardStateJSON.getNeedsUpdatedBatteries()) {
            for (String playerNickname : cardStateJSON.getRemovedBatteries().keySet()) {
                this.getShipOfPlayer(playerNickname).ifPresent(
                    ship -> {
                        ship.consumeEnergy(cardStateJSON.getRemovedBatteries().get(playerNickname));
                    }
                );
            }
        }

    }
}
