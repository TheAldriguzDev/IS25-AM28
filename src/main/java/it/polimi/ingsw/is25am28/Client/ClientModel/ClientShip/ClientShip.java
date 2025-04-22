package it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip;

import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Exceptions.*;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static it.polimi.ingsw.is25am28.Model.Connector.THREE_PIPES;
import static it.polimi.ingsw.is25am28.Model.Connector.ZERO_PIPES;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class ClientShip {
    private final static Map<Integer, int[][]> shipProfiles = new HashMap<>();
    private final static Map<Integer, Pair<Integer, Integer>> shipDimensions = new HashMap<>();
    private final static Map<Integer, Pair<Integer, Integer>> shipOffsets = new HashMap<>();

    static {
        // (1) - Setting the Ship Profile Matrices
        int[][] matrix;
        int[][] levelOneMatrix;
        int row, col;

        // (1.1) - Difficulty level 1 ship layout
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

        // (1.2) - Difficulty level 2 ship layout
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

        // (1.3) - Difficulty level 3 ship layout
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

        // (2) - Setting the Ship dimensions per difficultyLevel
        // --> Dimensions per difficultyLevel represent the smallest square/rectangle that wraps the entire ship
        shipDimensions.put(1, new Pair<Integer, Integer>(5, 5));
        shipDimensions.put(2, new Pair<Integer, Integer>(5, 7));
        shipDimensions.put(3, new Pair<Integer, Integer>(6, 9));

        // (3) - Setting the Ship's offsets per difficultyLevel
        // --> Offsets are between the 12x12 grid and the actual ship placement (just like in the cardboard version)
        // --> When scanning the 12x12 grid, you add these values to the respective row and column iterators
        //     to start scanning the ship from the top-left corner of the square/rectangle that wraps the entire ship
        shipOffsets.put(1, new Pair<Integer, Integer>(4, 4));
        shipOffsets.put(2, new Pair<Integer, Integer>(4, 3));
        shipOffsets.put(3, new Pair<Integer, Integer>(3, 2));
    }

    private final int difficultyLevel;
    private final int grid_rows = 12;
    private final int grid_cols = 12;
    private Component[][] components;
    private final Cabin core;
    private Cabin purpleAlienPosition;
    private Cabin brownAlienPosition;

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
    public ClientShip(int difficultyLevel) throws IllegalArgumentException {
        this.difficultyLevel = difficultyLevel;
        this.components = initGrid(this.grid_rows, this.grid_cols);

        // Initializing the connectors of the core cabin
        List<Integer> coreConnectors = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            coreConnectors.add(THREE_PIPES.ordinal());
        }

        // Creating the ship's core cabin
        this.core = new Cabin(coreConnectors,true);

        // No aliens are present at the beginning
        this.purpleAlienPosition = null;
        this.brownAlienPosition = null;

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
     * @return The ship's grid amount of rows
     */
    public int getGridRows() {
        return this.grid_rows;
    }

    /**
     * @return The ship's grid amount of columns
     */
    public int getGridCols() {
        return this.grid_cols;
    }

    /**
     * @return The ship's core cabin object
     */
    public Cabin getCore() {
        return this.core;
    }

    /**
     * @return A pointer to the cabin that is housing the Purple Alien (if onboard)
     */
    public Cabin getPurpleAlienPosition() {
        return this.purpleAlienPosition;
    }

    /**
     * @return A pointer to the cabin that is housing the Brown Alien (if onboard)
     */
    public Cabin getBrownAlienPosition() {
        return this.brownAlienPosition;
    }

    /**
     *  Uses an adapted version of the BFS algorithm to generate the sub-lists of
     *  each component type, which will be stored in this class for ease of use <br>
     *  <br>
     *  This method should <b style="color: rgb(8, 219, 205)">only</b> be used when the ship actually changes,
     *  otherwise it will iterate again over the ship's grid and generate the same lists.
     */
    public void generateComponentSubLists() throws IllegalStateException {
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
    public List<Battery> getBatteryList() { return new ArrayList<Battery>(this.batteryList); }

    /**
     * @return The list of Cannons present on the ship
     */
    public List<Cannon> getCannonList() { return new ArrayList<Cannon>(this.cannonList); }

    /**
     * @return The list of Engines present on the ship
     */
    public List<Engine> getEngineList() { return new ArrayList<Engine>(this.engineList); }

    /**
     * @return The list of Cabins present on the ship
     */
    public List<Cabin> getCabinList() { return new ArrayList<Cabin>(this.cabinList); }

    /**
     * @return The list of Shields present on the ship
     */
    public List<Shield> getShieldList() { return new ArrayList<Shield>(this.shieldList); }

    /**
     * @return The list of Storage units present on the ship
     */
    public List<Storage> getStorageList() { return new ArrayList<Storage>(this.storageList); }

    /**
     * @return The list of Vital units present on the ship
     */
    public List<Vital> getVitalList() { return new ArrayList<Vital>(this.vitalList); }

    /**
     * @return The list of DoubleEngines present on the ship
     */
    public List<Engine> getDoubleEngines() {
        return new ArrayList<Engine>(
                this.engineList.stream()
                        .filter(e -> e.getSpeed() == 2)
                        .toList()
        );
    }

    /**
     * @return The list of DoubleCannons present on the ship
     */
    public List<Cannon> getDoubleCannons() {
        return new ArrayList<Cannon>(
                this.cannonList.stream()
                        .filter(c -> c.getFirePower() == 2)
                        .toList()
        );
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
        // ClientSide implementation required
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

    /**
     * Adds the given lifeform to the given cabin. If an alien is added, it also
     * checks whether the given cabin has a matching vital unit attached
     *
     * @param i Row where the given cabin is located
     * @param j Col where the given cabin is located
     * @param lifeformType Type of lifeform to add to the ship
     * @throws IllegalArgumentException If the ship can have the given alien type onboard but the given cabin is not empty,
     *                                  or if the given alien type is null or unrecognised
     * @throws TooManyAliensException If a player tries to add a second alien of the same type
     * @throws OutOfGridException If the given coordinates fall out of the grid
     * @throws OutOfShipException If the given coordinates fall out of the ship
     */
    public void addLifeformToCabin(int i, int j, LifeformType lifeformType)
            throws IllegalArgumentException, TooManyAliensException,
            OutOfGridException, OutOfShipException
    {// ClientSide implementation required
    }

    /**
     * Removes the given lifeform type from the given cabin at coordinates (i, j)
     *
     * @param i The row where the cabin is located
     * @param j The column where the cabin is located
     * @param type The type of lifeform to remove from such cabin
     *
     * @throws IllegalArgumentException If either the component at coordinates (i, j) is not a cabin or
     *                                  if the given lifeform type is not recognized
     */
    public void removeLifeformFromCabin(int i, int j, LifeformType type) throws IllegalArgumentException {
        // ClientSide implementation required

    }

    /**
     * Returns the real firepower by considering the baseline firepower (given by single cannons) and
     * the additional firepower (given by activating the given amount of double cannons) and also
     * takes into account the bonus given by the purple alien (if present)
     *
     * @param doubleCannonsToActivate The list of double cannons to activate. If it's set to <code>null</code>
     *                                or it's given empty, then it returns the baseline firepower
     *
     * @return The current ship's total firepower
     */
    public float getFirePower(List<Pair<Integer, Integer>> doubleCannonsToActivate) {
        // ClientSide implementation required
        return 0;
    }


    /**
     * Returns the real engine power by considering the baseline engine power (given by single engines)
     * and the additional engine power (given by activating the given amount of double engines) and also
     * takes into account the bonus given by the brown alien (if present)
     *
     * @param doubleEnginesToActivate The amount of double engines to activate.
     *                                If set to 0, the method returns the baseline engine power
     *                                (+ the contribution of the brown alien (if present))
     *
     * @return The current ship's total engine power
     */
    public int getEnginePower(int doubleEnginesToActivate) {
        // ClientSide implementation required
        return 0;
    }

    /**
     * @return The number of exposed connectors on the entire ship
     */
    public int getExposedConnectorAmount(){
        // ClientSide implementation required
        return 0;
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
     * @param index The index of the row to extract
     *
     * @return The grid's row with the given index
     */
    public Component[] getGridRow(int index) throws OutOfGridException {
        // ClientSide implementation required
        return null;
    }

    /**
     * Regenerates the ship's grid by using the <code>traverse()</code> method.<br>
     * This method is useful only when deleting a component divides the ship into two
     * separate branches and, since the core must be kept, the branch that does not
     * contain the core component will be the one to be deleted.
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
        // ClientSide implementation required
        return null;
    }

    /**
     * Uses an adapted version of the BFS algorithm to iterate over each component of the
     * ship's grid and also applies the given lambda function to each component that it encounters
     *
     * @param lambda The lambda function to apply to each component encountered.
     *               It must be of type <code>Consumer</code> of <code>Component</code> in order to return <code>void</code>
     */
//    public void traverse(Consumer<Component> lambda) {
//        List<Component> currLayer = new ArrayList<Component>();
//        List<Component> nextLayer = new ArrayList<Component>();
//        List<Component> alreadyChecked = new ArrayList<Component>();
//        Component[] neighbours;
//        boolean borderReached;
//
//        // Starting the expansion from the core of the ship, which is
//        // always placed at coordinates (grid_rows/2, grid_cols/2)
//        currLayer.add(this.core);
//        borderReached = false;
//
//        while (!borderReached) {
//            borderReached = true;
//            for (Component currComp : currLayer) {
//                // Applying the lambda to currComp
//                lambda.accept(currComp);
//
//                neighbours = this.getNearestComponents(currComp);
//                alreadyChecked.add(currComp);
//
//                // Creating the nextLayer list of components for next iteration
//                // by populating it with the neighbours of each component in
//                // found in the currLayer list, except the ones that are already there
//                // (avoids overlapping) or were already checked (avoids backtracking)
//                for (Component neighbour : neighbours) {
//                    //      !nextLayer.contains(neighbours[i]) ==> Avoids overlapping
//                    // !alreadyChecked.contains(neighbours[i]) ==> Avoids backtracking
//                    if (neighbour != null) {
//                        if (!nextLayer.contains(neighbour) && !alreadyChecked.contains(neighbour)) {
//                            nextLayer.add(neighbour);
//                            borderReached = false;
//                        }
//                    }
//                }
//            }
//
//            currLayer = nextLayer;
//            nextLayer = new ArrayList<Component>();
//        }
//    }

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
        // ClientSide implementation required
        return null;
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
     * @throws OutOfShipException If the given coordinates (i, j) fall outside the ship profile, determined by the current difficulty level
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
     * Removes ONLY the component at coordinates (i, j) from the ship's grid<br>
     * When compared with the other method <code>removeComponent()</code>, the former doesn't check for
     * any hanging branches that resulted from the removal of that particular component, whereas the latter does so.
     * Also, this method is intended to be used mainly by the player, since he can specify precisely the component
     * to remove, in the context of fixing the ship when the method <code>validateShip()</code> returns FALSE.
     *
     * @param i The index of row that contains the component to delete
     * @param j The index of column that contains the component to delete
     * @throws OutOfGridException If the given coordinates (i, j) fall outside the grid
     * @throws OutOfShipException If the given coordinates (i, j) fall outside the ship profile, determined by the current difficulty level
     * @throws CoreDeletionAttemptException If the given coordinates (i, j) correspond to the ones of the core
     *
     */
    public void removeSingleComponent(int i, int j) throws OutOfGridException, OutOfShipException, CoreDeletionAttemptException {
        // ClientSide implementation required
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
     * @throws OutOfShipException If the given coordinates (i, j) fall outside the ship profile, determined by the current difficulty level
     * @throws CoreDeletionAttemptException If the given coordinates (i, j) correspond to the ones of the core
     *
     * @return The components removed from the ship, which are the selected one at coordinates (i, j) and any components
     *         that were left hanging from the ship as a consequence of the removal of the selected component.
     */
    public List<Component> removeComponent(int i, int j) throws OutOfGridException, CoreDeletionAttemptException, CoreDeletionAttemptException {
        // ClientSide implementation required


        return null;
    }

    /**
     * Returns the component that is identified by the coordinates (i, j) in the
     * ship's component grid, where i is the row index and j is the column index
     *
     * @param i The index of the row where the component to retrieve is located
     * @param j The index of the column where the component to retrieve is located
     * @return The component at coordinates (i, j)
     * @throws OutOfGridException If the given coordinates (i, j) fall outside the ship's grid
     * @throws OutOfShipException If the given coordinates (i, j) fall outside the ship's profile,
     *
     */
    public Component getComponent(int i, int j) throws OutOfGridException {
        // ClientSide implementation required
        return null;
    }

    /**
     * @return A description of the state of the ship
     */
    public List<Map<String, Object>> generateState() {
        List<Map<String, Object>> shipState = new ArrayList<Map<String, Object>>();
        int row, col;

        for (row = 0; row < this.grid_rows; row++) {
            for (col = 0; col < this.grid_cols; col++) {
                if (this.components[row][col] != null) {
                    shipState.add(this.components[row][col].toMap());
                }
            }
        }

        return shipState;
    }

    /**
     * @return The widget containing all of this ship's component's widgets
     *         as they are put inside this ship's grid
     */
    public WidgetTUI getShipGridWidget() {
        WidgetTUI shipGridWidget;
        WidgetTUI tmpComponentWidget;
        List<List<String>> screenRowList;
        List<List<String>> mergedWidgetRowList;

        // Initializations
        shipGridWidget = new WidgetTUI();
        tmpComponentWidget = new WidgetTUI();
        mergedWidgetRowList = new ArrayList<>();

        int scale = 3;
        int height = scale;
        int width = 3 * scale + 2;

        tmpComponentWidget.setHeight(height);
        tmpComponentWidget.setWidth(width);

        int shipRows= ClientShip.shipDimensions.get(this.difficultyLevel).getKey();
        int shipCols= ClientShip.shipDimensions.get(this.difficultyLevel).getValue();

        int rowOffset = ClientShip.shipOffsets.get(this.difficultyLevel).getKey();
        int colOffset = ClientShip.shipOffsets.get(this.difficultyLevel).getValue();

        int shipRowRange = rowOffset + shipRows;
        int shipColRange = colOffset + shipCols;

        // Generating the ship's widget screen by composing each row of the ship
        // horizontally first, and then compose each row horizontally, thus
        // creating the ship's grid screen
        for (int i = rowOffset; i < shipRowRange; i++) {
            screenRowList = new ArrayList<>();

            for (int j = colOffset; j < shipColRange; j++) {
                Component component = this.components[i][j];

                if (component != null) {
                    // If the component is not null, then generate its screen
                    tmpComponentWidget.setScreen(this.components[i][j].getComponentScreen());
                    screenRowList.add(tmpComponentWidget.getScreen());
                }
                else {
                    screenRowList.add(generateEmptySpaceScreen()); // Performance hit with style
                }
            }

            // Appending the row widget's screen to the ship's grid screen
            mergedWidgetRowList.add(WidgetTUI.composeScreensHorizontally(screenRowList));
        }

        // Merging all rows together into the final widget
        shipGridWidget.setScreen(WidgetTUI.composeScreensVertically(mergedWidgetRowList));

        // Wrapping the ship's grid widget with the default border
        shipGridWidget.wrapWidgetWithBorder();

        return shipGridWidget;
    }

    /**
     * @return The widget containing this ship's statistics
     */
    public WidgetTUI getShipStatsWidget() {
        // Creating the ship's stats widget with the correct dimensions
        WidgetTUI shipStatsWidget = new WidgetTUI();
        List<String> shipStatsScreen = new ArrayList<String>();

        WidgetTUI shipStatsTitle = new WidgetTUI();
        shipStatsTitle.appendString("SHIP STATS");
        shipStatsTitle.wrapWidgetWithBorder();
        shipStatsWidget.setScreen(shipStatsTitle.getScreen());

        // Getting all the ship's stats
        shipStatsScreen.add("Total Crew: " + this.getAllLifeforms().size());
        shipStatsScreen.add("Firepower: " + this.getFirePower(null));
        shipStatsScreen.add("EnginePower: " + this.getEnginePower(0));

        shipStatsWidget.appendScreen(shipStatsScreen);
        shipStatsWidget.centerWidgetScreen();
        shipStatsWidget.wrapWidgetWithBorder();

        return shipStatsWidget;
    }

    /**
     * Creates a Widget containing this ship's grid, its owner and its statistics
     *
     * @return This ship's TUI widget, which will be composed alongside other widgets
     *         in the final TUI
     */
    public WidgetTUI generateWidget() {
        List<WidgetTUI> shipWidgets = new ArrayList<>();

        // Stats + Grid
        shipWidgets.add(this.getShipStatsWidget());
        shipWidgets.add(this.getShipGridWidget());

        // Ship's stats on the left, ship's grid on the right
        return WidgetTUI.composeWidgetsHorizontally(shipWidgets);
    }

    /**
     * @return A screen containing a randomly selected star pattern
     *         which come in a few colors, that will make the ship look
     *         like it is actually traversing an actual cosmic scenario
     */
    private List<String> generateEmptySpaceScreen() {
        List<String> emptySpaceScreen = new ArrayList<>();
        List<String> colorPool = new ArrayList<>();
        Random rand = new Random();
        StringBuilder spaceString;
        int randIndex, randColor;

        int scale = 3;
        int height = scale;
        int width = 3 * scale + 2;

        // Aggregates all the possible colors that the space symbols can have
        colorPool.add(ANSIColors.MAGENTA);
        colorPool.add(ANSIColors.RED);
        colorPool.add(ANSIColors.YELLOW);
        colorPool.add(ANSIColors.CYAN);

        // Indicates how much the stars should be spread apart
        int spreadFactor = 80;
        int symbolPoolSize = UnicodeCharacters.SPACE_SYMBOLS.length + spreadFactor;

        height += 2;
        width += 2;

        // Generating the empty screen that will act as a spacer
        // when the current component at coords (i, j) is null
        for (int i = 0; i < height; i++) {
            spaceString = new StringBuilder();

            for (int j = 0; j < width; j++) {
                randIndex = rand.nextInt(0, symbolPoolSize);
                randColor = rand.nextInt(0, colorPool.size());

                if (randIndex < UnicodeCharacters.SPACE_SYMBOLS.length) {
                    spaceString.append(
                            PrintUtils.addColor(
                                    UnicodeCharacters.SPACE_SYMBOLS[randIndex],
                                    colorPool.get(randColor)
                            )
                    );
                }
                else {
                    spaceString.append(SPACE);
                }
            }

            emptySpaceScreen.add(spaceString.toString());
        }

        return emptySpaceScreen;
    }
}
