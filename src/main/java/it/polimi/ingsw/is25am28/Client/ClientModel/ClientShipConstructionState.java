package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;

import java.util.*;

public class ClientShipConstructionState extends ClientState {
    public static final int SELECTABLE_SUBDECK_AMOUNT = 3;

    // Components represent the tiles that the players can use to build their ship
    private final List<ClientComponent> components;
    private final List<ClientComponent> reservedComponents;
    private final Map<Integer, Boolean> currentlySelectedSubdecks;

    // Map each player nickname to a flag that is TRUE if
    // the respective player has finished building his ship
    private Map<String, Boolean> playersFinishedBuildingShip;

    // List containing the components inside the current player's ship
    private List<ClientComponent> currentShip;

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
        this.currentlySelectedSubdecks = new HashMap<>();
        this.currentShip = new ArrayList<>();

        // Initialize the map that stores for each subdeck a flag tha
        // is TRUE if the corresponding deck can be viewed, or FALSE
        // if there's currently a player that's viewing the subdeck
        this.initCurrentlySelectedSubdecksMap();

        // Initialize the map with all players and set all of them to false
        this.initPlayersFinishedBuildingShip();

        // Initialize all client components
        this.generateClientComponents(shipConstructionDTO.getAllComponents(), shipConstructionDTO.getFlippedComponents(), shipConstructionDTO.getSelectedComponents());

        // Initialize all client cards
        this.model.generateClientEventCards(shipConstructionDTO.getCards());
    }

    /**
     * Initializes the map that stores for each subdeck a boolean flag
     * indicating if that particular deck is currently being selected by
     * a player or not, enabling client-side evaluation thus saving the
     * trouble of asking the server directly
     */
    private void initCurrentlySelectedSubdecksMap() {
        this.currentlySelectedSubdecks.clear();

        for (int i = 0; i < SELECTABLE_SUBDECK_AMOUNT; i++) {
            this.currentlySelectedSubdecks.put(i, false);
        }
    }

    /**
     * @return TRUE if the subdeck with the given index is selected by
     *         a player, FALSE otherwise (i.e.: if it's "up for grabs").
     */
    public boolean isSubdeckSelected(Integer subdeckIndex) {
        return this.currentlySelectedSubdecks.get(subdeckIndex);
    }

    /**
     * Enables an external actor to set the given subdeck status
     * to the given boolean flag.
     */
    public void setSubdeckStatus(Integer subdeckIndex, boolean selected) {
        this.currentlySelectedSubdecks.put(subdeckIndex, selected);
    }

    /**
     * Generates the corresponding client components from the list
     * found in the ShipConstructionDTO
     */
    private void generateClientComponents(List<Map<String, Object>> componentList, List<Integer> flippedComponents, List<Integer> selectedComponents) {
        for (Map<String, Object> map : componentList) {
            int id = (int) map.get("id");
            int typeId = (int) map.get("tid");
            String path = (String) map.get("path");

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
                    this.components.add(new ClientCannon(id, connectorOrdinals, force, path));
                }
                // Cabin
                case 1 -> {
                    this.components.add(new ClientCabin(id, connectorOrdinals, false, path));
                }
                // Storage
                case 2 -> {
                    int capacity = (int) map.get("capacity");
                    boolean isSpecial = (boolean) map.get("special");
                    this.components.add(new ClientStorage(id, connectorOrdinals, capacity, isSpecial, path));
                }
                // Vital
                case 3 -> {
                    int type = (int) map.get("type");
                    this.components.add(new ClientVital(id, connectorOrdinals, type, path));
                }
                // Engine
                case 4 -> {
                    int speed = (int) map.get("speed");
                    this.components.add(new ClientEngine(id, connectorOrdinals, speed, path));
                }
                // Battery
                case 5 -> {
                    int capacity = (int) map.get("capacity");
                    this.components.add(new ClientBattery(id, connectorOrdinals, capacity, path));
                }
                // Shield
                case 6 -> {
                    this.components.add(new ClientShield(id, connectorOrdinals, path));
                }
                // Structural
                case 7 -> {
                    this.components.add(new ClientStructural(id, connectorOrdinals, path));
                }
                default -> {
                    throw new RuntimeException("The given component is not recognised.");
                }
            }

            // Update the components that are already either selected or flipped
            ClientComponent component = this.components.getLast();

            if (flippedComponents.contains(component.getID())) {
                component.setAsFlipped();
            }

            if (selectedComponents.contains(component.getID())) {
                component.setIsVisible(false);
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
        if (this.model.getTimerDTO() != null) {
            // If the timerDTO arrived in the past, it means that
            // the timer ended and thus it's now flippable

            // Setting it to null so that it means
            // "A new TimerDTO will arrive <==> The timer ends"
            this.model.setTimerDTO(null);
        }
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
