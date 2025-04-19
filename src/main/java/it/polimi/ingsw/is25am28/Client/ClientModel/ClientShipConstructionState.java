package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.Components.Shield;
import it.polimi.ingsw.is25am28.Model.Components.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientShipConstructionState extends ClientState {
    // Components represent the tiles that the players can use to build their ship
    private final List<ClientComponent> components;
    private final List<ClientComponent> reservedComponents;

    private final List<ComponentHelper<ConstructionComponentDTO>> ship;

    // TODO: Add the list that represent the ShipCreation --> In this way we can then send the created ship to the server

    // (NOT HERE --> IN THE CORRECT STATES
    // TODO: Add the list for the removedComponents to support the FIX SHIP PHASE
    // TODO: Add the list for the populateShipComponent to support the POPULATE SHIP PHASE

    // TODO ADD THE VIRTUAL CLIENT TO SEND THE MESSAGES TO THE SERVER IN THIS STATES

    /**
     * The constructor will set the model and then create all the components needed to build the player ship
     * */
    public ClientShipConstructionState(ClientModel model, List<Map<String, Object>> componentList) {
        super(model);

        // Create the component list
        this.components = new ArrayList<>();
        this.reservedComponents = new ArrayList<>();
        this.ship = new ArrayList<>();

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

        ClientComponent component = this.components.get(idx);

        return this.components.get(idx);
    }

    /**
     * Command used by the player when he wants to deselect a Tile from the table in the ShipConstructionState
     * */
    @Override
    public void deselectTile(ClientComponent component, int i, int j) throws UnsupportedOperationException {

    }

    /**
     * Command used by the player when he wants to place a Tile to build his Ship
     * */
    @Override
    public void placeTile(ClientComponent component, int i, int j) throws UnsupportedOperationException {
        int id = component.getID();
        int construction_i = id / 19;
        int construction_j = id % 19;

        // Set the component coordinate
        component.setI(i);
        component.setJ(j);

        // Add the tile to the list of components that will be sent to the server to build the player ship
        this.ship.add(
                new ComponentHelper<ConstructionComponentDTO>(construction_i, construction_j)
                        .addItem(new ConstructionComponentDTO().setI(component.getI()).setJ(component.getJ()).setRotation(component.getDirection())));

        // TODO: Add the tile to the player ship (so that we can already save it locally)
    }

    /**
     * Command used by the player when he wants to flip the timer
     * */
    @Override
    public void flipTimer() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'flipTimer' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to send the created Ship to the server
     * */
    @Override
    public void confirmShip() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'confirmShip' is not supported in the " + this + " state");
    }
}
