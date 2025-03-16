package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Exceptions.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static it.polimi.ingsw.is25am28.Connector.*;

public class Ship {
    private Component[][] components;
    private final int grid_rows;
    private final int grid_cols;

    // All components are sorted into their matching category,
    // represented by one of the following lists
    private final List<Battery> batteryList;
    private final List<Cabin> cabinList;
    private final List<Cannon> cannonList;
    private final List<Engine> engineList;
    private final List<Shield> shieldList;
    private final List<Storage> storageList;
    private final List<Vital> vitalList;

    // Constructor
    public Ship(int grid_rows, int grid_cols) {
        this.grid_rows = grid_rows;
        this.grid_cols = grid_cols;
        this.components = initGrid(grid_rows, grid_cols);

        int[] coreConnectors = new int[4];

        coreConnectors[0] = coreConnectors[1] = coreConnectors[2] = coreConnectors[3] = THREE_PIPES.ordinal();

        // Creating the ship's core cabin
        Cabin core = new Cabin(
                this.grid_rows / 2,
                this.grid_cols / 2,
                0,
                coreConnectors,
                true
        );

        // Adding the core component as the first component in the ship's grid
        this.addComponent(core, core.getPosition()[0], core.getPosition()[1]);

        // Instantiating each component list as an empty list
        batteryList = new ArrayList<Battery>();
        cabinList = new ArrayList<Cabin>();
        cannonList = new ArrayList<Cannon>();
        engineList = new ArrayList<Engine>();
        shieldList = new ArrayList<Shield>();
        storageList = new ArrayList<Storage>();
        vitalList = new ArrayList<Vital>();
    }

    /**
     *  Uses an adapted version of the BFS algorithm to generate the sub-lists of
     *  each component type, which will be stored in this class for ease of use <br>
     *  <br>
     *  This method should <b style="color: rgb(8, 219, 205)">only</b> be used when the ship actually changes,
     *  otherwise it will iterate again over the ship's grid and generate the same lists.
     */
    public void generateComponentSubLists() throws IllegalStateException {
        traverse(
            (Component c) -> {
                switch (c) {
                    case Battery battery:   this.batteryList.add(battery);
                                            break;
                    case Cabin cabin:       this.cabinList.add(cabin);
                                            break;
                    case Cannon cannon:     this.cannonList.add(cannon);
                                            break;
                    case Engine engine:     this.engineList.add(engine);
                                            break;
                    case Shield shield:     this.shieldList.add(shield);
                                            break;
                    case Storage storage:   this.storageList.add(storage);
                                            break;
                    case Vital vital:       this.vitalList.add(vital);
                                            break;
                    case Structural struct: // Structural components are not sorted
                                            break;
                    default:
                        throw new IllegalStateException("Unexpected class type " + c.toString());
                }
            }
        );
    }

    /**
     * @return The list of Batteries present on the ship
     */
    public List<Battery> getBatteryList() { return this.batteryList; }

    /**
     * @return The list of Cabins present on the ship
     */
    public List<Cabin> getCabinList() { return this.cabinList; }

    /**
     * @return The list of Cannons present on the ship
     */
    public List<Cannon> getCannonList() { return this.cannonList; }

    /**
     * @return The list of Engines present on the ship
     */
    public List<Engine> getEngineList() { return this.engineList; }

    /**
     * @return The list of Shields present on the ship
     */
    public List<Shield> getShieldList() { return this.shieldList; }

    /**
     * @return The list of Storages present on the ship
     */
    public List<Storage> getStorageList() { return this.storageList; }

    /**
     * @return The list of Engines present on the ship
     */
    public List<Vital> getVitalList() { return this.vitalList; }

    /**
     * @return The ship's available energy
     */
    public int getAvailableEnergy() {
        return this.batteryList.stream()
                .mapToInt(Battery::getAvailability)
                .sum();
    }

    /**
     * @return The ship's total onboard <code>Lifeform</code>s
     *         (both humans and aliens)
     */
    public List<Lifeform> getAllLifeforms() {
        return this.cabinList.stream()
                .flatMap(cabin -> cabin.getInhabitants().stream())
                .collect(Collectors.toList());
    }

    /**
     * @return The ship's total firepower, including the
     *         double cannons that the user chooses to activate
     */
    public float getFirePower() {
        return (float) this.cannonList.stream()
                .mapToDouble(Cannon::getFirePower)
                .sum();
    }

    /**
     * @return The ship's total engine power, including the
     *         double engines that the user chooses to activate
     */
    public float getEnginePower() {
        return (float) this.engineList.stream()
                .mapToDouble(Engine::getSpeed)
                .sum();
    }

    /**
     * @return All the ship's stored <code>Item</code>s
     */
    public List<Item> getAllItems() {
        return this.storageList.stream()
                .flatMap(storage -> storage.getStoredItems().stream())
                .collect(Collectors.toList());
    }

    /**
     * @return The total value of all the <code>Item</code> onboard the ship
     */
    public int getAllItemValue() {
        return this.storageList.stream()
                .flatMap(storage -> storage.getStoredItems().stream())
                .mapToInt(Item::getValue)
                .sum();
    }

    // TODO: Implemented by Andrea, ask what is its purpose
    /**
     * @return All components that fail the <code>check()</code> method
     */
    public List<Component> getWrongComponents(){
        List<Component> wrongs = new ArrayList<>();

        traverse(
            (Component c) -> {
                if(!c.check(getNearestComponents(c))){
                    wrongs.add(c);
                }
            }
        );

        return wrongs;
    }

    /**
     * Uses an adapted version of the BFS algorithm to validate that each component
     * is connected correctly with its neighbours, thus validating the ship
     *
     * @return <b style="color: green">TRUE</b> if all the ship's components are connected correctly, <br>
     * <b style="color: red">FALSE</b> otherwise
     */
    public boolean validateShip() {
        AtomicBoolean isShipValid = new AtomicBoolean(true);

        traverse(
            (Component c) -> {
                if (isShipValid.get() && !c.check(getNearestComponents(c))) {
                    isShipValid.set(false);
                }
            }
        );

        return isShipValid.get();
    }

    /**
     * Regenerates the ship's grid by using the <code>traverse()</code> method.<br>
     * This method is used only when deleting a component divides the ship into two
     * separate branches and, since the core must be kept, the branch that does not
     * contain the component will be the one to be deleted.
     */
    private void recreateShipGrid() {
        // Initializing all components of the grid to null
        Component[][] grid = initGrid(this.grid_rows, this.grid_cols);

        // Recreate the ship's grid by only considering components
        // that are connected to others, thus eliminating any branches
        // that would otherwise be left hanging
        // (i.e.: no path exists from the core to those components)
        traverse(
            (Component c) -> {
                int[] position = c.getPosition();
                grid[position[0]][position[1]] = c;
            }
        );

        // Finally, substitute the old grid with the new one
        this.components = grid;
    }

    /**
     * @return A grid of <code>Component</code> of the given dimensions with all values initialized to <code>null</code>
     */
    private Component[][] initGrid(int grid_rows, int grid_cols) {
        Component[][] grid = new Component[grid_rows][grid_cols];

        for (int i = 0; i < grid_rows; i++) {
            for (int j = 0; j < grid_cols; j++) {
                grid[i][j] = null;
            }
        }

        return grid;
    }

    /**
     * Uses an adapted version of the BFS algorithm to iterate over each component of the
     * ship's grid and also applies the given lambda function to each component that it encounters
     *
     * @param lambda The lambda function to apply to each component encountered.
     *               It must be of type <code>Consumer</code> of <code>Component</code> in order to return <code>void</code>
     */
    public void traverse(Consumer<Component> lambda) {
        List<Component> currLayer = new ArrayList<Component>();
        List<Component> nextLayer = new ArrayList<Component>();
        List<Component> alreadyChecked = new ArrayList<Component>();
        Component[] neighbours;
        boolean borderReached;

        // Starting the expansion from the core of the ship, which is
        // always placed at coordinates (grid_rows/2, grid_cols/2)
        currLayer.add(this.components[this.grid_rows / 2][this.grid_cols / 2]);
        borderReached = false;

        while (!borderReached) {
            borderReached = true;
            for (Component currComp : currLayer) {
                // Applying the lambda to currComp
                lambda.accept(currComp);

                neighbours = getNearestComponents(currComp);
                alreadyChecked.add(currComp);

                // Creating the nextLayer list of components for next iteration
                // by populating it with the neighbours of each component in
                // found in the currLayer list, except the ones that are already there
                // (avoids overlapping) or were already checked (avoids backtracking)
                for (int i = 0; i < 4; i++) {
                    //      !nextLayer.contains(neighbours[i]) ==> Avoids overlapping
                    // !alreadyChecked.contains(neighbours[i]) ==> Avoids backtracking
                    if (neighbours[i] != null) {
                        if (!nextLayer.contains(neighbours[i]) && !alreadyChecked.contains(neighbours[i])) {
                            nextLayer.add(neighbours[i]);
                            borderReached = false;
                        }
                    }
                }
            }

            currLayer = nextLayer;
            nextLayer = new ArrayList<Component>();
        }
    }

    /**
     * Returns the direct neighbours of the given component in the following order:
     * <ul>
     *     <li>Index 0 - Top</li>
     *     <li>Index 1 - Right</li>
     *     <li>Index 2 - Bottom</li>
     *     <li>Index 3 - Left</li>
     * </ul>
     * <br>
     * NOTE: The direct neighbours are NOT diagonal, thus only the top, right, bottom and left adjacent
     * components are considered as neighbours of the given component.
     *
     * @param component The component of which we want to get its direct neighbours
     * @return A <code>Component[]</code> array of size 4 with the given component's neighbours
     * @throws NullComponentException If the given component is <code>null</code>
     * @throws NullPointerException If the position of the given component fails to yield legal coordinates
     */
    public Component[] getNearestComponents(Component component) throws NullComponentException, NullPointerException {
        Component[] neighbours = new Component[4];
        int[] positionInGrid;

        if (component == null) {
            // If passed component is null, there's no need to find its neighbours
            throw new NullComponentException("Passed component is null");
        }
        else {
            // Getting the passed component's position in the grid
            positionInGrid = component.getPosition();

            // After checking if the given component is in a legal position, each neighbouring position
            // is tested to check if it has a component or is illegal (in the latter case, that neighbour is null)
            if (positionInGrid != null) {
                // NORTH neighbour
                try {
                    neighbours[0] = this.components[positionInGrid[0] - 1][positionInGrid[1]];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[0] = null;
                }

                // EAST neighbour
                try {
                    neighbours[1] = this.components[positionInGrid[0]][positionInGrid[1] + 1];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[1] = null;
                }

                // SOUTH neighbour
                try {
                    neighbours[2] = this.components[positionInGrid[0] + 1][positionInGrid[1]];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[2] = null;
                }

                // WEST neighbour
                try {
                    neighbours[3] = this.components[positionInGrid[0]][positionInGrid[1] - 1];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[3] = null;
                }
            }
            else {
                throw new NullPointerException("Array \"positionInGrid\" is null, implying that the component is in an illegal position");
            }
        }

        return neighbours;
    }

    /**
     * Adds the given component at the given coordinates (i, j) in the ship's component grid.
     *
     * @param component The component to add to the ship's grid
     * @param i The index of the row
     * @param j The index of the column
     * @throws NullComponentException If the given component is <code>null</code>
     * @throws OutOfGridException If the given coordinates (i, j) fall outside the ship's grid
     * @throws ExistingComponentException If the component at coordinates (i, j) is already occupied
     */
    public void addComponent(Component component, int i, int j)
            throws NullComponentException, OutOfGridException, ExistingComponentException
    {
        if (component == null) {
            throw new NullComponentException("Given component to add is null");
        }
        if (i < 0 || j < 0 || i >= this.grid_rows || j >= this.grid_cols) {
            throw new OutOfGridException("Cannot insert given component outside of the ship's grid");
        }
        if (this.components[i][j] != null) {
            throw new ExistingComponentException("Cannot insert given component on top of an already existing one");
        }

        this.components[i][j] = component;
    }

    /**
     * Removes the component at coordinates (i, j) from the ship's grid.<br>
     * If that component, when removed, divides the ship into 2 or more branches, then the
     * method keeps the branch that also has the core component and discards others
     * (as if they were left hanging without support).
     *
     * @param i The index of row that contains the component to delete
     * @param j The index of column that contains the component to delete
     * @throws OutOfGridException If the given coordinates (i, j) fall outside the grid
     */
    public void removeComponent(int i, int j) throws OutOfGridException {
        if (i < 0 || j < 0 || i >= this.grid_rows || j >= this.grid_cols) {
            throw new OutOfGridException("Requested component is not in the ship component grid");
        }

        /*
         *  Now the component removal consists of 3 steps:
         *
         *  (1) - Remove the selected component from the ship's grid
         *  (2) - recreateShipGrid, which removes any component that was left hanging:
         *         - This step takes care of any hanging branches that might have been generated
         *           from the removal of the component.
         *         - Basically, the method returns a grid that contains only the components that
         *           can be reached with at least one path starting from the core of the ship.
         *         - If there are any components that cannot be reached by starting from the core,
         *           then these are part of "hanging branches" and thus must be eliminated.
         *  (3) - Recreate all the sub-lists:
         *         - Since the ship's grid was modified in the previous steps, all its sub-lists
         *           must be recalculated to ensure that all the removed components do not appear
         *           in those lists.
         *         - Finally, by doing so, any reference to those removed components is deleted and thus
         *           the garbage collector can deallocate them, effectively erasing them from memory.
         */
        this.components[i][j] = null;
        recreateShipGrid();
        this.generateComponentSubLists();
    }

    /**
     * Returns the component that is identified by the coordinates (i, j) in the
     * ship's component grid, where i is the row index and j is the column index
     *
     * @param i The index of the row where the component to retrieve is located
     * @param j The index of the column where the component to retrieve is located
     * @return The component at coordinates (i, j)
     * @throws OutOfGridException If the given coordinates (i, j) fall outside the ship's grid
     * @throws NullComponentException If the selected component is <code>null</code>
     */
    public Component getComponent(int i, int j) throws OutOfGridException {
        Component selectedComponent;

        if (i < 0 || j < 0 || i >= grid_rows || j >= grid_cols) {
            throw new OutOfGridException("Requested component is not in the ship component grid");
        }

        return this.components[i][j];
    }
}
