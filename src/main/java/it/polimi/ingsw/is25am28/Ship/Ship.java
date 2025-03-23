package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ShipJSON;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Connector;
import it.polimi.ingsw.is25am28.Exceptions.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;

import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static it.polimi.ingsw.is25am28.Connector.*;

public class Ship {
    private final static Map<Integer, int[][]> shipProfiles = new HashMap<>();

    static {
        int[][] matrix;
        int[][] levelOneMatrix;
        int row, col;

        // (1) - Difficulty level 1 ship layout
        // Starting from scratch
        matrix = new int[12][12];

        // Zeroing the matrix
        for (row = 0; row < 12; row++) {
            for (col = 0; col < 12; col++) {
                matrix[row][col] = 0;
            }
        }

        // Filling the level 1 ship profile by hand
        // Starting from the top
        matrix[4][6] = 1;   // Row #5

        matrix[5][5] = 1;   // Row #6
        matrix[5][6] = 1;   // Row #6
        matrix[5][7] = 1;   // Row #6

        matrix[6][4] = 1;   // Row #7
        matrix[6][5] = 1;   // Row #7
        matrix[6][6] = 1;   // Row #7
        matrix[6][7] = 1;   // Row #7
        matrix[6][8] = 1;   // Row #7

        matrix[7][4] = 1;   // Row #8
        matrix[7][5] = 1;   // Row #8
        matrix[7][6] = 1;   // Row #8
        matrix[7][7] = 1;   // Row #8
        matrix[7][8] = 1;   // Row #8

        matrix[8][4] = 1;   // Row #9
        matrix[8][5] = 1;   // Row #9
        matrix[8][7] = 1;   // Row #9
        matrix[8][8] = 1;   // Row #9

        shipProfiles.put(1, matrix);

        // Saving the level 1 matrix as a baseline for
        // building the other 2 ship profiles
        levelOneMatrix = shipProfiles.get(1);

        // (2) - Difficulty level 2 ship layout
        // Creating the level 2 layout by starting from the level 1 layout
        matrix = new int[12][12];

        // Initializing the level 2 ship profile with the level 1
        // ship profile as a starting point
        for (row = 0; row < 12; row++) {
            for (col = 0; col < 12; col++) {
                matrix[row][col] = levelOneMatrix[row][col];
            }
        }

        // Shaping the level 2 ship profile starting from the
        // level 1 ship profile as the baseline
        // Starting from the top
        matrix[4][5] = 1;   // Row #5
        matrix[4][6] = 0;   // Row #5
        matrix[4][7] = 1;   // Row #5

        matrix[5][4] = 1;   // Row #6
        matrix[5][8] = 1;   // Row #6

        matrix[6][3] = 1;   // Row #7
        matrix[6][9] = 1;   // Row #7

        matrix[7][3] = 1;   // Row #8
        matrix[7][9] = 1;   // Row #8

        matrix[8][3] = 1;   // Row #9
        matrix[8][9] = 1;   // Row #9

        shipProfiles.put(2, matrix);

        // (3) - Difficulty level 3 ship layout
        // Creating the level 3 layout by starting from the level 1 layout
        matrix = new int[12][12];

        // Initializing the level 3 ship profile with the level 1
        // ship profile as a starting point
        for (row = 0; row < 12; row++) {
            for (col = 0; col < 12; col++) {
                matrix[row][col] = levelOneMatrix[row][col];
            }
        }

        // Shaping the level 3 ship profile starting from the
        // level 1 ship profile as the baseline
        // Starting from the top
        matrix[3][6] = 1;   // Row #4

        matrix[4][5] = 1;   // Row #5
        matrix[4][7] = 1;   // Row #5

        matrix[5][2] = 1;   // Row #6
        matrix[5][4] = 1;   // Row #6
        matrix[5][8] = 1;   // Row #6
        matrix[5][10] = 1;  // Row #6

        matrix[6][2] = 1;   // Row #7
        matrix[6][3] = 1;   // Row #7
        matrix[6][9] = 1;   // Row #7
        matrix[6][10] = 1;  // Row #7

        matrix[7][2] = 1;   // Row #8
        matrix[7][3] = 1;   // Row #8
        matrix[7][9] = 1;   // Row #8
        matrix[7][10] = 1;  // Row #8

        matrix[8][2] = 1;   // Row #9
        matrix[8][3] = 1;   // Row #9
        matrix[8][4] = 0;   // Row #9
        matrix[8][8] = 0;   // Row #9
        matrix[8][9] = 1;   // Row #9
        matrix[8][10] = 1;  // Row #9

        shipProfiles.put(3, matrix);
    }

    private final int difficultyLevel;
    private final int grid_rows = 12;
    private final int grid_cols = 12;
    private Component[][] components;
    private final Cabin core;

    // All components are sorted into their matching category,
    // represented by one of the following sub-lists
    private final List<Battery> batteryList;
    private final List<Cabin> cabinList;
    private final List<Cannon> cannonList;
    private final List<Engine> engineList;
    private final List<Shield> shieldList;
    private final List<Storage> storageList;
    private final List<Vital> vitalList;

    // Constructor #1 - Generates one of the three possible grids, each for its level
    public Ship(int difficultyLevel) throws IllegalArgumentException {
        this.difficultyLevel = difficultyLevel;
        this.components = initGrid(this.grid_rows, this.grid_cols);

        // Initializing the connectors of the core cabin
        int[] coreConnectors = new int[4];
        coreConnectors[0] = THREE_PIPES.ordinal();
        coreConnectors[1] = THREE_PIPES.ordinal();
        coreConnectors[2] = THREE_PIPES.ordinal();
        coreConnectors[3] = THREE_PIPES.ordinal();

        // Creating the ship's core cabin
        this.core = new Cabin(coreConnectors,true);

        // Adding the core component as the first component in the ship's grid
        this.addComponent(this.core, this.grid_rows/2, this.grid_cols/2);

        // Instantiating each component list as an empty list
        this.batteryList = new ArrayList<Battery>();
        this.cabinList = new ArrayList<Cabin>();
        this.cannonList = new ArrayList<Cannon>();
        this.engineList = new ArrayList<Engine>();
        this.shieldList = new ArrayList<Shield>();
        this.storageList = new ArrayList<Storage>();
        this.vitalList = new ArrayList<Vital>();
    }

    /**
     *  Uses an adapted version of the BFS algorithm to generate the sub-lists of
     *  each component type, which will be stored in this class for ease of use <br>
     *  <br>
     *  This method should <b style="color: rgb(8, 219, 205)">only</b> be used when the ship actually changes,
     *  otherwise it will iterate again over the ship's grid and generate the same lists.
     */
    public void generateComponentSubLists() throws IllegalStateException {
        Set<Component> alreadySeen = new HashSet<Component>();

        // Clearing all the sub-lists
        this.batteryList.clear();
        this.cabinList.clear();
        this.cannonList.clear();
        this.engineList.clear();
        this.shieldList.clear();
        this.storageList.clear();
        this.vitalList.clear();

        traverse(
            (Component c) -> {
                if (!alreadySeen.contains(c)) {
                    alreadySeen.add(c);
                    switch (c) {
                        case Battery battery:       this.batteryList.add(battery);
                            break;
                        case Cabin cabin:           this.cabinList.add(cabin);
                            break;
                        case Cannon cannon:         this.cannonList.add(cannon);
                            break;
                        case Engine engine:         this.engineList.add(engine);
                            break;
                        case Shield shield:         this.shieldList.add(shield);
                            break;
                        case Storage storage:       this.storageList.add(storage);
                            break;
                        case Vital vital:           this.vitalList.add(vital);
                            break;
                        case Structural structural: // Structural components are not sorted
                            break;
                        default:
                            throw new IllegalStateException("Unexpected class type " + c.toString());
                    }
                }
            }
        );
    }

    /**
     * @return A pair of integers that represent the amount of rows and the amount
     *         of columns of the ship's grid. Result is (rows, cols)
     */
    public Pair<Integer, Integer> getGridDimensions() {
        return new Pair<Integer, Integer>(this.grid_rows, this.grid_cols);
    }

    /**
     * @return The ship's difficulty level
     */
    public int getDifficultyLevel() { return this.difficultyLevel; }

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
     * @return The list of Storage units present on the ship
     */
    public List<Storage> getStorageList() { return this.storageList; }

    /**
     * @return The list of Vital units present on the ship
     */
    public List<Vital> getVitalList() { return this.vitalList; }

    /**
     * @return The list of DoubleEngines present on the ship
     */
    public List<Engine> getDoubleEngines() {
        return this.engineList.stream()
                .filter(e -> e.getSpeed() == 2)
                .toList();
    }

    /**
     * @return The list of DoubleCannons present on the ship
     */
    public List<Cannon> getDoubleCannons() {
        return this.cannonList.stream()
                .filter(c -> c.getFirePower() == 2)
                .toList();
    }

    /**
     * @return The ship's available energy
     */
    public int getAvailableEnergy() {
        return this.batteryList.stream()
                .mapToInt(Battery::getAvailability)
                .sum();
    }

    /**
     * Consumes the given amount of energy from the ship's total energy
     *
     * @param energyToConsume The amount of energy to consume from the total available energy on the ship.<br>
     *                        The method doesn't do anything if <code>energyToConsume <= 0</code>.
     *
     * @throws InsufficientEnergyException If <code>energyToConsume</code> is greater than the energy currently available on the ship
     */
    public void consumeEnergy(int energyToConsume) throws InsufficientEnergyException {
        int availableEnergy;

        if (energyToConsume > 0) {
            if (energyToConsume <= this.getAvailableEnergy()) {
                // If there's enough energy, then consume the given amount
                for (Battery battery : this.batteryList) {
                    availableEnergy = battery.getAvailability();

                    if (availableEnergy < energyToConsume) {
                        energyToConsume -= availableEnergy;
                        battery.setAvailability(0);
                    }
                    else {
                        battery.setAvailability(availableEnergy - energyToConsume);
                        break;
                    }
                }
            }
            else {
                // Otherwise, throw an InsufficientEnergyException
                throw new InsufficientEnergyException("ERROR: Cannot consume more energy than available");
            }
        }
    }

    /**
     * @return The ship's total onboard <code>Lifeform</code>s
     *         (both humans and aliens)
     */
    public List<Lifeform> getAllLifeforms() {
        return this.cabinList.stream()
                .flatMap(cabin -> cabin.getInhabitants().stream())
                .toList();
    }

    // TODO: Test this feature
    /**
     * Sets the cabins at their respective coordinates (row, col) to the
     * chosen alien type that they are matched with in the map inside data
     *
     * @param data The ShipJSON containing the mapping between an alien type and which cabin the user wants to put it in. <br>
     *             Each cabin is distinguished by a set of coordinates (row, col) found inside this parameter
     *
     * @throws IllegalArgumentException If the given data is badly formatted when compared to the expected ShipJSON formatting
     */
    public void setChosenAliensForEligibleCabins(ActionJSON data) throws IllegalArgumentException {
        ShipJSON shipJSON;
        Map<Integer, Pair<Integer, Integer>> chosenAliens;

        try {
            shipJSON = (ShipJSON) data;
            chosenAliens = shipJSON.getChosenAliens();

            for (Map.Entry<Integer, Pair<Integer, Integer>> entry : chosenAliens.entrySet()) {
                int alienType = entry.getKey();

                Component component = this.getComponent(
                    entry.getValue().getKey(),
                    entry.getValue().getValue()
                );

                switch (component) {
                    case Cabin cabin -> {
                        switch (alienType) {
                            case 1 -> {
                                // LifeformType.PURPLE_ALIEN.ordinal() == 1
                                cabin.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));
                            }
                            case 2 -> {
                                // LifeformType.BROWN_ALIEN.ordinal() == 2
                                cabin.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));
                            }
                            default -> throw new IllegalStateException();
                        }
                    }
                    case null, default -> throw new IllegalArgumentException();
                }
            }
        }
        catch (OutOfGridException e) {
            System.out.printf("[Ship::setChosenAliensForEligibleCabins] " + e.getMessage());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: JSON parsing error in Ship::setChosenAliensForEligibleCabins");
        }
    }

    /**
     * Returns the real firepower by considering the baseline firepower (given by single cannons) and
     * the additional firepower (given by activating the given amount of double cannons)<br>
     *
     * Also, <code>doubleCannonsToActivate</code> corresponds to the amount of batteries to consume, but if that value
     * exceeds the actual amount of double cannons present on the ship, then the amount of batteries consumed will be
     * equal to the amount of all the double cannons present on the ship, thus preserving the difference.
     *
     * @param doubleCannonsToActivate The amount of double cannons to activate.<br>
     *          In particular, we have two distinct cases:
     *          <ul>
     *              <li>
     *                  If its value is set to <code>0</code>, then no double cannons are activated and thus the
     *                  resulting firepower is the baseline firepower given only by the single cannons
     *              </li>
     *              <li>
     *                  If its value is <code>> 0</code>, then it returns the baseline firepower plus <code>2 * doubleCannonsToActivate</code>
     *                  and, in case <code>doubleCannonsToActivate</code> is greater than the actual amount of double cannons, then
     *                  the resulting firepower is the baseline firepower plus all the available double cannons activated.
     *              </li>
     *          </ul>
     *
     * @return The ship's total firepower of both single and double cannons
     */
    public float getFirePower(int doubleCannonsToActivate) {
        List<Cannon> doubleCannonList;
        int doubleCannonAmount;
        float totalFirePower;

        doubleCannonList = this.getDoubleCannons();
        doubleCannonAmount = doubleCannonList.size();

        // Calculating the firepower of only the single cannons
        float singleCannonsFirePower = (float) this.cannonList.stream()
                    .filter((Cannon c) -> ((c.getFirePower() < 1 && c.getDirection() != 0) || (c.getFirePower() == 1 && c.getDirection() == 0)))
                    .mapToDouble(Cannon::getFirePower)
                    .sum();

        // Calculating the contribution of purple aliens onboard the ship
        // to the overall firepower
        int purpleAlienFirepowerAddon = this.cabinList.stream()
                .flatMap((Cabin c) -> (c.getInhabitants().stream()))
                .mapToInt(Lifeform::getAttackBoost)
                .sum();

        if (doubleCannonAmount > 0) {
            if (doubleCannonsToActivate > doubleCannonAmount) {
                // If the requested double cannons to activate surpass the
                // actual amount of double cannons available, then activate
                // all those double cannons and avoid consuming the excess energy
                doubleCannonsToActivate = doubleCannonAmount;
            }

            // Calculating the totalFirePower
            totalFirePower = singleCannonsFirePower
                    + (doubleCannonsToActivate * (int) doubleCannonList.getFirst().getFirePower());

            // Consuming the amount of batteries required to activate
            // the given amount of double cannons
            this.consumeEnergy(doubleCannonsToActivate);
        }
        else {
            // No double engines to activate means that the total firepower
            // is the result of all and only the single cannons onboard the ship
            totalFirePower = singleCannonsFirePower;
        }

        return totalFirePower + purpleAlienFirepowerAddon;
    }

    /**
     * Returns the real firepower by considering the baseline firepower (given by single cannons) and
     * the additional firepower (given by activating the given amount of double cannons)<br>
     *
     * Also, <code>doubleEnginesToActivate</code> corresponds to the amount of batteries to consume, but if that value
     * exceeds the actual amount of double engines present on the ship, then the amount of batteries consumed will be
     * equal to the amount of all the double engines present on the ship, thus preserving the difference.
     *
     * @param doubleEnginesToActivate The amount of double engines to activate.<br>
     *          In particular, we have two distinct cases:
     *          <ul>
     *              <li>
     *                  If its value is set to <code>0</code>, then no double engines are activated and thus the
     *                  resulting engine power is the baseline engine power given only by the single engines
     *              </li>
     *              <li>
     *                  If its value is <code>> 0</code>, then it returns the baseline engine power plus <code>2 * doubleEnginesToActivate</code>
     *                  and, in case <code>doubleEnginesToActivate</code> is greater than the actual amount of double engines, then
     *                  the resulting engine power is the baseline engine power plus all the available double engines activated.
     *              </li>
     *          </ul>
     *
     * @return The ship's total engine power of both single and double engines
     */
    public int getEnginePower(int doubleEnginesToActivate) {
        List<Engine> doubleEngineList;
        int doubleEngineAmount;
        int totalEnginePower;

        doubleEngineList = this.getDoubleEngines();
        doubleEngineAmount = doubleEngineList.size();

        // Calculating the engine power of only the single engines
        int singleEnginesEnginePower = (int) this.engineList.stream()
                    .filter((Engine c) -> (c.getSpeed() == 1))
                    .mapToDouble(Engine::getSpeed)
                    .sum();

        // Calculating the contribution of brown aliens onboard the ship
        // to the overall engine power
        int brownAlienEnginePowerAddon = this.cabinList.stream()
                .flatMap((Cabin c) -> (c.getInhabitants().stream()))
                .mapToInt(Lifeform::getPowerBoost)
                .sum();

        if (doubleEngineAmount > 0) {
            if (doubleEnginesToActivate > doubleEngineAmount) {
                // If the requested double engines to activate surpass the
                // actual amount of double engines available, then activate
                // all those double engine and avoid consuming the excess energy
                doubleEnginesToActivate = doubleEngineAmount;
            }

            // Calculating the totalEnginePower
            totalEnginePower = singleEnginesEnginePower
                    + (doubleEnginesToActivate * (int) doubleEngineList.getFirst().getSpeed());

            // Consuming the amount of batteries required to activate
            // the given amount of double engines
            this.consumeEnergy(doubleEnginesToActivate);
        }
        else {
            // No double engines to activate means that the total engine power
            // is the result of all and only the single engines onboard the ship
            totalEnginePower = singleEnginesEnginePower;
        }

        return totalEnginePower + brownAlienEnginePowerAddon;
    }

    /**
     * @return The number of exposed connectors on the entire ship
     */
    public int getExposedConnectorAmount(){
        AtomicInteger exposedConnectors = new AtomicInteger();

        traverse(
            (Component component) -> {
                Component[] neighbours = this.getNearestComponents(component);
                boolean sideIsZeroPipes = false;

                for (int i = 0; i < neighbours.length; i++) {
                    switch (i) {
                        case 0 -> { sideIsZeroPipes = (component.getTopSide() == ZERO_PIPES); }     // Top
                        case 1 -> { sideIsZeroPipes = (component.getRightSide() == ZERO_PIPES); }   // Right
                        case 2 -> { sideIsZeroPipes = (component.getBottomSide() == ZERO_PIPES); }  // Bottom
                        case 3 -> { sideIsZeroPipes = (component.getLeftSide() == ZERO_PIPES); }    // Left
                    }

                    // The component's currently checked side is exposed iff the
                    // neighbour on that side is null and the connector is NOT a ZERO_PIPE
                    if (!sideIsZeroPipes && neighbours[i] == null) {
                        exposedConnectors.getAndIncrement();
                    }
                }
            }
        );

        return exposedConnectors.get();
    }

    /**
     * @return The total (normal + special) storage space that is available (i.e.: not occupied)
     */
    public int getAvailableStorageSpace() {
        return this.getStorageList().stream()
                .mapToInt(Storage::availableSpace)
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
     * @param index The index of the row to extract
     *
     * @return The grid's row with the given index
     */
    public Component[] getGridRow(int index) throws OutOfGridException {
        Component[] row;

        if (index < 0 || index >= this.grid_rows) {
            throw new OutOfGridException("ERROR: Given index is out of grid");
        }

        row = new Component[this.grid_cols];

        for (int i = 0; i < this.grid_cols; i++) {
            row[i] = this.components[index][i];
        }

        return row;
    }

    /**
     * @param index The index of the column to extract
     *
     * @return The grid's column with the given index
     */
    public Component[] getGridColumn(int index) throws OutOfGridException {
        Component[] column;

        if (index < 0 || index >= this.grid_cols) {
            throw new OutOfGridException("ERROR: Given index is out of grid");
        }

        column = new Component[this.grid_rows];

        for (int i = 0; i < this.grid_rows; i++) {
            column[i] = this.components[i][index];
        }

        return column;
    }

    /**
     * Uses an adapted version of the BFS algorithm to validate that each component
     * is connected correctly with its neighbours, thus validating the ship
     *
     * @return <b style="color: green">TRUE</b> if all the ship's components are connected correctly,
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
     * This method is useful only when deleting a component divides the ship into two
     * separate branches and, since the core must be kept, the branch that does not
     * contain the component will be the one to be deleted.
     */
    public void recreateShipGrid() {
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
        currLayer.add(this.core);
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
            throws NullComponentException, OutOfGridException,
                   ExistingComponentException, OutOfShipException
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
        if (shipProfiles.containsKey(this.difficultyLevel)) {
            if (shipProfiles.get(this.difficultyLevel)[i][j] == 0) {
                throw new OutOfShipException("ERROR: Cannot insert given component outside the ship");
            }
        }

        // Setting the current component's position
        component.setPosition(i, j);

        // Finally, adding the component
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
     *
     * @return The components removed from the ship, which are the selected one at coordinates (i, j) and any components
     *         that were left hanging from the ship as a consequence of the removal of the selected component.
     */
    public List<Component> removeComponent(int i, int j) throws OutOfGridException, CoreDeletionAttemptException {
        Component[][] previousShip;
        List<Component> removedComponents;

        if (i < 0 || j < 0 || i >= this.grid_rows || j >= this.grid_cols) {
            throw new OutOfGridException("Requested component is not in the ship component grid");
        }
        if (i == this.core.getPosition()[0] && j == this.core.getPosition()[1]) {
            throw new CoreDeletionAttemptException("ERROR: Cannot delete core cabin from the ship");
        }

        previousShip = new Component[this.grid_rows][this.grid_cols];
        removedComponents = new ArrayList<Component>();

        // Copying the current ship
        for (int row = 0; row < this.grid_rows; row++) {
            for (int col = 0; col < this.grid_cols; col++) {
                previousShip[row][col] = this.components[row][col];
            }
        }

        // Removing the component and regenerating the new ship
        this.components[i][j] = null;
        this.recreateShipGrid();
        this.generateComponentSubLists();

        // If the component at coordinates (row, col) in the previousShip
        // was not null, then it means that it got removed and thus needs to
        // be added to the removed components list
        for (int row = 0; row < this.grid_rows; row++) {
            for (int col = 0; col < this.grid_cols; col++) {
                if (previousShip[row][col] != null && this.components[row][col] == null) {
                    removedComponents.add(previousShip[row][col]);
                }
            }
        }

        return removedComponents;
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
        if (i < 0 || j < 0 || i >= grid_rows || j >= grid_cols) {
            throw new OutOfGridException("Requested component is not in the ship component grid");
        }

        return this.components[i][j];
    }
}
