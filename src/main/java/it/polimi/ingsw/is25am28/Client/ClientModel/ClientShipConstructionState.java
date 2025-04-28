package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;

import java.util.*;

public class ClientShipConstructionState extends ClientState {
    // Components represent the tiles that the players can use to build their ship
    private final List<ClientComponent> components;
    private final List<ClientComponent> reservedComponents;

    // Map each player nickname to a flag that is TRUE if
    // the respective player has finished building his ship
    private Map<String, Boolean> playersFinishedBuildingShip;

    // List containing the components inside the current player's ship
    private List<ClientComponent> currentShip;
    private boolean isTimeRunning;

    // List containing all cards from all decks
    // (each sub-deck can be extracted from the difficulty level)
    private List<ClientEventCard> eventCards;

    // (NOT HERE --> IN THE CORRECT STATES
    // TODO: Add the list for the removedComponents to support the FIX SHIP PHASE
    // TODO: Add the list for the populateShipComponent to support the POPULATE SHIP PHASE

    /**
     * The constructor will set the model and then create all the components needed to build the player ship
     * */
    public ClientShipConstructionState(ClientModel model, ShipConstructionDTO shipConstructionDTO) {
        super(model);

        // Initializations
        this.components = new ArrayList<>();
        this.reservedComponents = new ArrayList<>();
        this.currentShip = new ArrayList<>();
        this.eventCards = new ArrayList<>();

        // Initialize the timer state at the beginning
        // of the ship building phase
        this.isTimeRunning = true;

        // Initialize the map with all players and set all of them to false
        this.initPlayersFinishedBuildingShip();

        // Initialize all client components
        this.generateClientComponents(shipConstructionDTO.getAllComponents());

        // Initialize all client cards
        this.generateClientEventCards(shipConstructionDTO.getCards());
    }

    /**
     * Generates the corresponding client components from the list
     * found in the ShipConstructionDTO
     */
    private void generateClientComponents(List<Map<String, Object>> componentList) {
        for (Map<String, Object> map : componentList) {
            int id = (int) map.get("id");
            int typeId = (int) map.get("tid");

            Object connectorsObj = map.get("connectors");
            List<Integer> connectorOrdinals = null;

            if (connectorsObj != null) {
                try {
                    connectorOrdinals = (List<Integer>) connectorsObj;
                } catch (ClassCastException e) {
                    throw new RuntimeException(e);
                }
            }

            // Create the components
            switch (typeId) {
                // Cannon
                case 0 -> {
                    int force = (int) map.get("force");
                    this.components.add(new ClientCannon(id, connectorOrdinals, force));
                }
                // Cabin
                case 1 -> {
                    this.components.add(new ClientCabin(id, connectorOrdinals, false));
                }
                // Storage
                case 2 -> {
                    int capacity = (int) map.get("capacity");
                    boolean isSpecial = (boolean) map.get("special");
                    this.components.add(new ClientStorage(id, connectorOrdinals, capacity, isSpecial));
                }
                // Vital
                case 3 -> {
                    int type = (int) map.get("type");
                    this.components.add(new ClientVital(id, connectorOrdinals, type));
                }
                // Engine
                case 4 -> {
                    int speed = (int) map.get("speed");
                    this.components.add(new ClientEngine(id, connectorOrdinals, speed));
                }
                // Battery
                case 5 -> {
                    int capacity = (int) map.get("capacity");
                    this.components.add(new ClientBattery(id, connectorOrdinals, capacity));
                }
                // Shield
                case 6 -> {
                    this.components.add(new ClientShield(id, connectorOrdinals));
                }
                // Structural
                case 7 -> {
                    this.components.add(new ClientStructural(id, connectorOrdinals));
                }
                default -> {
                    throw new RuntimeException("The given component is not recognised.");
                }
            }
        }
    }

    /**
     * Generates all client event cards from the given list
     * of card states sent by the server
     */
    private void generateClientEventCards(List<CardStateJSON> cards) {
        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Cannot construct all client event cards without the data");
        }

        if (this.eventCards == null) {
            this.eventCards = new ArrayList<>();
        }

        for (CardStateJSON cardState : cards) {
            switch (cardState.getId()) {
                case 0 -> this.eventCards.add(new ClientAbandonedShip(cardState));
                case 1 -> this.eventCards.add(new ClientAbandonedStation(cardState));
                case 2 -> this.eventCards.add(new ClientEpidemy(cardState));
                case 3 -> this.eventCards.add(new ClientMeteorShower(cardState));
                case 4 -> this.eventCards.add(new ClientOpenSpace(cardState));
                case 5 -> this.eventCards.add(new ClientPirates(cardState));
                case 6 -> this.eventCards.add(new ClientSlavers(cardState));
                case 7 -> this.eventCards.add(new ClientSmugglers(cardState));
                case 8 -> this.eventCards.add(new ClientStardust(cardState));
                case 9 -> this.eventCards.add(new ClientVisitPlanets(cardState));
                case 10 -> this.eventCards.add(new ClientWarZone(cardState));
                
                default -> throw new IllegalArgumentException("ERROR: Illegal event card ID");
            }
        }
    }

    @Override
    public List<ClientComponent> getConstructionShipComponents() throws UnsupportedOperationException {
        return this.components;
    }

    @Override
    public List<ClientComponent> getReservedComponents() throws UnsupportedOperationException {
        return this.reservedComponents;
    }

    /**
     * @return The list of all client event cards that compose
     *         the 4 decks that the player can see during the
     *         ship construction phase
     */
    @Override
    public List<ClientEventCard> getEventCards() throws UnsupportedOperationException {
        return this.eventCards;
    }

    /**
     * Command used by the player when he wants to select a Tile from the table in the ShipConstructionState
     * */
    @Override
    public ClientComponent selectTile(int i, int j) throws UnsupportedOperationException {
        int idx = i * 19 + j;

        return this.components.get(idx);
    }

    /**
     * Command used by the player when he wants to deselect a Tile from the table in the ShipConstructionState
     * */
    @Override
    public void deselectTile(ClientComponent component, int i, int j) throws UnsupportedOperationException {

    }

    /**
     * Command used by the player when he wants to reserve a Tile to build his Ship
     * */
    @Override
    public void reserveTile(ClientComponent component) throws UnsupportedOperationException {
        if (this.reservedComponents.size() < 2) {
            this.reservedComponents.add(component);
        }
    }

    /**
     * Command used by the player when he wants to flip the timer
     * */
    @Override
    public void flipTimer() throws UnsupportedOperationException {
        if (!this.isTimeRunning) {
            // Flips the timer if a TimerDTO arrived
            // before this method was invoked
            this.isTimeRunning = true;
        }
    }

    /**
     * Sets the flag to what the TimerDTO
     * sent from the server is saying about it
     */
    @Override
    public void setIsTimeRunning(TimerDTO timerState) throws UnsupportedOperationException {
        // Always false since the TimerDTO is always returned
        // when the server-side timer ends.
        this.isTimeRunning = false;
    }

    /**
     * Returns TRUE if the timer can be flipped, FALSE otherwise
     */
    @Override
    public boolean isTimeRunning() throws UnsupportedOperationException {
        return this.isTimeRunning;
    }

    /**
     * Flags all players as "has NOT finished building his ship"
     */
    private void initPlayersFinishedBuildingShip() {
        if (this.playersFinishedBuildingShip == null) {
            this.playersFinishedBuildingShip = new HashMap<>();

            for (String player : this.model.getAllPlayersNicknames()) {
                this.playersFinishedBuildingShip.put(player, false);
            }
        }
    }

    /**
     * @return TRUE if the current player was marked as "has finished building his ship", FALSE otherwise.
     */
    @Override
    public boolean getPlayerFinishedBuildingShip(String playerNickname) throws IllegalArgumentException {
        if (playerNickname != null && !playerNickname.isEmpty()) {
            if (this.playersFinishedBuildingShip != null) {
                return this.playersFinishedBuildingShip.getOrDefault(playerNickname, null);
            }
            else {
                throw new IllegalArgumentException("ERROR: \"" + playerNickname + "\" is not in the map (init failed)");
            }
        }
        else {
            throw new IllegalArgumentException("ERROR: Given playerNickname is either empty or null");
        }
    }

    /**
     * @param playerNickname The player who finished building his ship and whose
     *                       corresponding flag will be set to true.
     */
    @Override
    public void setPlayerFinishedBuildingShip(String playerNickname) {
        if (playerNickname != null && !playerNickname.isEmpty()) {
            if (this.playersFinishedBuildingShip != null) {
                this.playersFinishedBuildingShip.put(playerNickname, true);
            }
            else {
                throw new IllegalArgumentException("ERROR: \"" + playerNickname + "\" is not in the map (init failed)");
            }
        }
        else {
            throw new IllegalArgumentException("ERROR: Given playerNickname is either empty or null");
        }
    }

    /**
     * Adds the given component to the player's list of components that compose the ship.
     * <br>
     * (NOTE: the coordinates (i, j) are indexes, therefore, for example, adding
     *        a component in (row=5, col=7) actually places it at coordinates (4, 6))
     *
     * @param component The component that the player wants to place
     * @param i The row where the player wants to place the given component
     * @param j The column where the player wants to place the given component
     */
    public void addComponent(ClientComponent component, int i, int j) {
        if (component != null) {
            // Finalizing the position of this component in the grid
            component.setI(i);
            component.setJ(j);
            this.currentShip.add(component);
        }
    }
}
