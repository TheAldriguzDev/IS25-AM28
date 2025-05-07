package it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Model.Exceptions.ExistingComponentException;
import it.polimi.ingsw.is25am28.Model.Exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfShipException;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Exceptions.*;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;

import javafx.util.Pair;

import java.util.*;
import java.util.function.Consumer;

import static it.polimi.ingsw.is25am28.Model.Connector.THREE_PIPES;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class ClientShip implements WidgetTUIGenerator {
    public final static Map<Integer, int[][]> shipProfiles = new HashMap<>();
    public final static Map<Integer, Pair<Integer, Integer>> shipDimensions = new HashMap<>();
    public final static Map<Integer, Pair<Integer, Integer>> shipOffsets = new HashMap<>();

    static {
        // (1) - Setting the Ship Profile Matrices
        int[][] matrix;
        int[][] levelOneMatrix;
        int row, col;

        // (1.1) - Difficulty level 0 and 1 ship layout
        // Starting from scratch
        matrix = new int[12][12];

        // Zeroing the matrix
        for (row = 0; row < 12; row++) {
            for (col = 0; col < 12; col++) {
                matrix[row][col] = 0;
            }
        }

        // Filling the level 0 and 1 ship profile by hand
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

        // NOTE: Both level 0 (test flight) and level 1 have the same ship profile
        shipProfiles.put(0, matrix);
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
        shipDimensions.put(0, new Pair<Integer, Integer>(5, 5));
        shipDimensions.put(1, new Pair<Integer, Integer>(5, 5));
        shipDimensions.put(2, new Pair<Integer, Integer>(5, 7));
        shipDimensions.put(3, new Pair<Integer, Integer>(6, 9));

        // (3) - Setting the Ship's offsets per difficultyLevel
        // --> Offsets are between the 12x12 grid and the actual ship placement (just like in the cardboard version)
        // --> When scanning the 12x12 grid, you add these values to the respective row and column iterators
        //     to start scanning the ship from the top-left corner of the square/rectangle that wraps the entire ship
        shipOffsets.put(0, new Pair<Integer, Integer>(4, 4));
        shipOffsets.put(1, new Pair<Integer, Integer>(4, 4));
        shipOffsets.put(2, new Pair<Integer, Integer>(4, 3));
        shipOffsets.put(3, new Pair<Integer, Integer>(3, 2));
    }

    private final int difficultyLevel;
    private static final int grid_rows = 12;
    private static final int grid_cols = 12;
    private ClientComponent[][] components;
    private final ClientCabin core;
    private ClientCabin purpleAlienPosition;
    private ClientCabin brownAlienPosition;

    // All components are sorted into their matching category,
    // represented by one of the following sub-lists
    private final List<ClientBattery> batteryList;
    private final List<ClientCabin> cabinList;
    private final List<ClientCannon> cannonList;
    private final List<ClientEngine> engineList;
    private final List<ClientShield> shieldList;
    private final List<ClientStorage> storageList;
    private final List<ClientVital> vitalList;

    // Constructor
    public ClientShip(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        this.components = initGrid(grid_rows, grid_cols);

        // Initializing the connectors of the core cabin
        List<Integer> coreConnectors = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            coreConnectors.add(THREE_PIPES.ordinal());
        }

        // Creating the ship's core cabin
        this.core = new ClientCabin(1, coreConnectors,true);

        // No aliens are present at the beginning
        this.purpleAlienPosition = null;
        this.brownAlienPosition = null;

        // Adding the core component as the first component in the ship's grid
        this.addComponent(this.core, grid_rows/2, grid_cols/2);

        // Instantiating each component list as an empty list
        this.batteryList = new ArrayList<ClientBattery>();
        this.cabinList = new ArrayList<ClientCabin>();
        this.cannonList = new ArrayList<ClientCannon>();
        this.engineList = new ArrayList<ClientEngine>();
        this.shieldList = new ArrayList<ClientShield>();
        this.storageList = new ArrayList<ClientStorage>();
        this.vitalList = new ArrayList<ClientVital>();
    }

    /**
     * Constructor used when a player reconnect to the game --> in this way we will be able to re-create the client
     * ship
     * */
    public ClientShip(int difficultyLevel, List<Map<String, Object>> initialShip) {
        this.difficultyLevel = difficultyLevel;
        this.components = initGrid(grid_rows, grid_cols);

        // Initializing the connectors of the core cabin
        List<Integer> coreConnectors = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            coreConnectors.add(THREE_PIPES.ordinal());
        }

        this.core = new ClientCabin(-1, coreConnectors,true);

        // No aliens are present at the beginning
        this.purpleAlienPosition = null;
        this.brownAlienPosition = null;

        // Adding the core component as the first component in the ship's grid
        this.addComponent(this.core, grid_rows/2, grid_cols/2);

        // Instantiating each component list as an empty list
        this.batteryList = new ArrayList<ClientBattery>();
        this.cabinList = new ArrayList<ClientCabin>();
        this.cannonList = new ArrayList<ClientCannon>();
        this.engineList = new ArrayList<ClientEngine>();
        this.shieldList = new ArrayList<ClientShield>();
        this.storageList = new ArrayList<ClientStorage>();
        this.vitalList = new ArrayList<ClientVital>();

        for (Map<String, Object> map : initialShip) {
            int id = (int) map.get("id");
            int typeId = (int) map.get("tid");

            int i = (int) map.get("row");
            int j = (int ) map.get("col");

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

                    ClientCannon component = new ClientCannon(id, connectorOrdinals, force);

                    this.addComponent(component, i, j);
                }
                // Cabin
                case 1 -> {
                    List<LifeformType> lifeform = (List<LifeformType>) map.get("inhabitants");
                    ClientCabin component = new ClientCabin(id, connectorOrdinals, false);

                    for (LifeformType lifeformType : lifeform) {
                        component.addInhabitant(new Lifeform(lifeformType));
                    }

                    this.addComponent(component, i, j);
                }
                // Storage
                case 2 -> {
                    int capacity = (int) map.get("capacity");
                    boolean isSpecial = (boolean) map.get("special");

                    List<Integer> storedItems = (List<Integer>) map.get("storedItems");
                    ClientStorage component = new ClientStorage(id, connectorOrdinals, capacity, isSpecial);

                    for (Integer storedItem : storedItems) {
                        switch (storedItem) {
                            case 1 -> {
                                component.storeItem(new Item(ItemColor.BLUE));
                            }
                            case 2 -> {
                                component.storeItem(new Item(ItemColor.GREEN));
                            }
                            case 3 -> {
                                component.storeItem(new Item(ItemColor.YELLOW));
                            }
                            case 4 -> {
                                component.storeItem(new Item(ItemColor.RED));
                            }
                        }
                    }

                    this.addComponent(component, i, j);
                }
                // Vital
                case 3 -> {
                    int type = (int) map.get("type");
                    this.addComponent(new ClientVital(id, connectorOrdinals, type), i, j);
                }
                // Engine
                case 4 -> {
                    int speed = (int) map.get("speed");
                    this.addComponent(new ClientEngine(id, connectorOrdinals, speed), i, j);
                }
                // Battery
                case 5 -> {
                    int capacity = (int) map.get("capacity");
                    int available = (int) map.get("available");

                    ClientBattery component = new ClientBattery(id, connectorOrdinals, capacity);
                    component.setAvailability(available);

                    this.addComponent(component, i, j);
                }
                // Shield
                case 6 -> {
                    this.addComponent(new ClientShield(id, connectorOrdinals), i, j);
                }
                // Structural
                case 7 -> {
                    this.addComponent(new ClientStructural(id, connectorOrdinals), i, j);
                }
                default -> {
                    throw new RuntimeException("The given component is not recognised.");
                }
            }
        }

        this.generateComponentSubLists();
    }

    /**
     * @return The ship's core cabin object
     */
    public ClientCabin getCore() {
        return this.core;
    }

    /**
     * @return A pointer to the cabin that is housing the Purple Alien (if onboard)
     */
    public ClientCabin getPurpleAlienPosition() {
        return this.purpleAlienPosition;
    }

    /**
     * @return A pointer to the cabin that is housing the Brown Alien (if onboard)
     */
    public ClientCabin getBrownAlienPosition() {
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
            (ClientComponent c) -> {
                switch (c) {
                    case ClientBattery clientBattery:       this.batteryList.add(clientBattery);
                        break;
                    case ClientCabin clientCabin:           this.cabinList.add(clientCabin);
                        break;
                    case ClientCannon clientCannon:         this.cannonList.add(clientCannon);
                        break;
                    case ClientEngine clientEngine:         this.engineList.add(clientEngine);
                        break;
                    case ClientShield clientShield:         this.shieldList.add(clientShield);
                        break;
                    case ClientStorage clientStorage:       this.storageList.add(clientStorage);
                        break;
                    case ClientVital clientVital:           this.vitalList.add(clientVital);
                        break;
                    case ClientStructural clientStructural: // Structural components are not sorted
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
    public List<ClientBattery> getBatteryList() { return new ArrayList<>(this.batteryList); }

    /**
     * @return The list of Cannons present on the ship
     */
    public List<ClientCannon> getCannonList() { return new ArrayList<>(this.cannonList); }

    /**
     * @return The list of Engines present on the ship
     */
    public List<ClientEngine> getEngineList() { return new ArrayList<>(this.engineList); }

    /**
     * @return The list of Cabins present on the ship
     */
    public List<ClientCabin> getCabinList() { return new ArrayList<>(this.cabinList); }

    /**
     * @return The list of Shields present on the ship
     */
    public List<ClientShield> getShieldList() { return new ArrayList<>(this.shieldList); }

    /**
     * @return The list of Storage units present on the ship
     */
    public List<ClientStorage> getStorageList() { return new ArrayList<>(this.storageList); }

    /**
     * @return The list of Vital units present on the ship
     */
    public List<ClientVital> getVitalList() { return new ArrayList<>(this.vitalList); }

    /**
     * @return The list of DoubleEngines present on the ship
     */
    public List<ClientEngine> getDoubleEngines() {
        return new ArrayList<ClientEngine>(
            this.engineList.stream()
                .filter(e -> e.getSpeed() == 2)
                .toList()
        );
    }

    /**
     * @return The list of DoubleCannons present on the ship
     */
    public List<ClientCannon> getDoubleCannons() {
        return new ArrayList<ClientCannon>(
            this.cannonList.stream()
                .filter(c -> c.getFirePower() == 2)
                .toList()
        );
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
     * client components are considered as neighbours of the given client component.
     *
     * @param clientComponent The client component of which we want to get its direct neighbours
     * @return A <code>ClientComponent[]</code> array of size 4 with the given client component's neighbours
     * @throws NullComponentException If the given client component is <code>null</code>
     */
    public ClientComponent[] getNearestComponents(ClientComponent clientComponent) throws NullComponentException {
        ClientComponent[] neighbours = new ClientComponent[4];

        if (clientComponent == null) {
            // If passed component is null, there's no need to find its neighbours
            throw new NullComponentException("Passed component is null");
        }
        else {
            // NORTH neighbour
            try {
                neighbours[0] = this.components[clientComponent.getI() - 1][clientComponent.getJ()];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                neighbours[0] = null;
            }

            // EAST neighbour
            try {
                neighbours[1] = this.components[clientComponent.getI()][clientComponent.getJ() + 1];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                neighbours[1] = null;
            }

            // SOUTH neighbour
            try {
                neighbours[2] = this.components[clientComponent.getI() + 1][clientComponent.getJ()];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                neighbours[2] = null;
            }

            // WEST neighbour
            try {
                neighbours[3] = this.components[clientComponent.getI()][clientComponent.getJ() - 1];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                neighbours[3] = null;
            }
        }

        return neighbours;
    }

    /**
     * Uses an adapted version of the BFS algorithm to iterate over each component of the
     * ship's grid and also applies the given lambda function to each component that it encounters
     *
     * @param lambda The lambda function to apply to each component encountered.
     *               It must be of type <code>Consumer</code> of <code>ClientComponent</code> in order to return <code>void</code>
     */
    public void traverse(Consumer<ClientComponent> lambda) {
        List<ClientComponent> currLayer = new ArrayList<ClientComponent>();
        List<ClientComponent> nextLayer = new ArrayList<ClientComponent>();
        List<ClientComponent> alreadyChecked = new ArrayList<ClientComponent>();
        ClientComponent[] neighbours;
        boolean borderReached;

        // Starting the expansion from the core of the ship, which is
        // always placed at coordinates (grid_rows/2, grid_cols/2)
        currLayer.add(this.core);
        borderReached = false;

        while (!borderReached) {
            borderReached = true;
            for (ClientComponent currComp : currLayer) {
                // Applying the lambda to currComp
                lambda.accept(currComp);

                neighbours = this.getNearestComponents(currComp);
                alreadyChecked.add(currComp);

                // Creating the nextLayer list of components for next iteration
                // by populating it with the neighbours of each component in
                // found in the currLayer list, except the ones that are already there
                // (avoids overlapping) or were already checked (avoids backtracking)
                for (ClientComponent neighbour : neighbours) {
                    //      !nextLayer.contains(neighbours[i]) ==> Avoids overlapping
                    // !alreadyChecked.contains(neighbours[i]) ==> Avoids backtracking
                    if (neighbour != null) {
                        if (!nextLayer.contains(neighbour) && !alreadyChecked.contains(neighbour)) {
                            nextLayer.add(neighbour);
                            borderReached = false;
                        }
                    }
                }
            }

            currLayer = nextLayer;
            nextLayer = new ArrayList<ClientComponent>();
        }
    }

    /**
     * Adds the given client component at the given coordinates (i, j) in the ship's component grid.
     *
     * @param clientComponent The client component to add to the ship's grid
     * @param i The index of the row
     * @param j The index of the column
     * @throws NullComponentException If the given client component is <code>null</code>
     * @throws OutOfGridException If the given coordinates (i, j) fall outside the ship's grid
     * @throws ExistingComponentException If the component at coordinates (i, j) is already occupied
     * @throws OutOfShipException If the given coordinates (i, j) fall outside the ship profile, determined by the current difficulty level
     */
    public void addComponent(ClientComponent clientComponent, int i, int j)
            throws NullComponentException, OutOfGridException,
            ExistingComponentException, OutOfShipException
    {
        if (clientComponent == null) {
            throw new NullComponentException("ERROR: Given client component to add is null");
        }
        if (i < 0 || j < 0 || i >= grid_rows || j >= grid_cols) {
            throw new OutOfGridException("ERROR: Cannot insert given client component outside of the ship's grid");
        }
        if (this.components[i][j] != null) {
            throw new ExistingComponentException("ERROR: Cannot insert given client component on top of an already existing one");
        }
        if (shipProfiles.containsKey(this.difficultyLevel)) {
            if (shipProfiles.get(this.difficultyLevel)[i][j] == 0) {
                throw new OutOfShipException("ERROR: Cannot insert given client component outside the ship");
            }
        }

        // Setting the current client component's position
        clientComponent.setPosition(i, j);

        // Finally, adding the client component
        this.components[i][j] = clientComponent;
    }

    /**
     * Removes the ClientComponent at the given coordinates (i, j) from this ship's components grid
     */
    public void removeComponent(int i, int j) {
        this.components[i][j] = null;
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
    public ClientComponent getComponent(int i, int j) throws OutOfGridException {
        if (i < 0 || j < 0 || i >= grid_rows || j >= grid_cols) {
            throw new OutOfGridException("Requested component is not in the ship component grid");
        }

        return this.components[i][j];
    }

    /**
     * @return A grid of <code>ClientComponent</code> of the given dimensions with all values initialized to <code>null</code>
     */
    private ClientComponent[][] initGrid(int grid_rows, int grid_cols) {
        ClientComponent[][] grid = new ClientComponent[grid_rows][grid_cols];

        for (int i = 0; i < grid_rows; i++) {
            for (int j = 0; j < grid_cols; j++) {
                grid[i][j] = null;
            }
        }

        return grid;
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
     * @return The ship's available energy
     */
    public int getAvailableEnergy() {
        return this.batteryList.stream()
                .mapToInt(ClientBattery::getAvailability)
                .sum();
    }

    /**
     * @return TRUE if this ship cannot host any other lifeforms, FALSE otherwise
     */
    public boolean isShipPopulated() {
        boolean allCabinsFull = true;
        int cabinAmount = this.cabinList.size();

        for (int i = 0; allCabinsFull && (i < cabinAmount); i++) {
            List<Lifeform> inhabitants = this.cabinList.get(i).getInhabitants();

            allCabinsFull = (((inhabitants.size() == 1) && (inhabitants.getFirst().getRequiredSpace() == 2)) || (inhabitants.size() == 2));
        }

        return allCabinsFull;
    }

    /**
     * @return TRUE if the given lifeform can be added at the given coordinates (i, j),
     *         FALSE otherwise.
     */
    public boolean addLifeformVerifier(int i, int j, LifeformType lifeformType)
            throws IllegalArgumentException, OutOfGridException, OutOfShipException
    {
        ClientComponent[] neighbours;
        ClientComponent component;

        if (shipProfiles.containsKey(this.difficultyLevel)) {
            if (shipProfiles.get(this.difficultyLevel)[i][j] == 0) {
                throw new OutOfShipException("ERROR: Cannot select a component that is outside the ship");
            }
        }

        component = this.getComponent(i, j);

        switch (component) {
            case ClientCabin cabin -> {
                // Cannot add any lifeforms to the core
                // (it already has 2 astronauts, populated automatically)
                if (cabin.isCore()) {
                    return false;
                }

                switch (lifeformType) {
                    // LifeformType.ASTRONAUT.ordinal() == 0
                    case ASTRONAUT -> {
                        if (cabin.getAvailableSpace() > 0) {
                            return true;
                        }
                    }
                    // LifeformType.PURPLE_ALIEN.ordinal() == 1
                    case PURPLE_ALIEN -> {
                        if (this.purpleAlienPosition == null) {
                            if (cabin.getAvailableSpace() == LifeformType.PURPLE_ALIEN.getRequiredSpace()) {
                                neighbours = this.getNearestComponents(cabin);

                                for (ClientComponent neighbour : neighbours) {
                                    switch (neighbour) {
                                        case ClientVital vital -> {
                                            if (vital.getVitalType() == VitalType.PURPLE_VITAL) {
                                                return true;
                                            }
                                        }
                                        case null, default -> {}
                                    }
                                }
                            }
                        }
                    }
                    // LifeformType.BROWN_ALIEN.ordinal() == 2
                    case BROWN_ALIEN -> {
                        if (this.brownAlienPosition == null) {
                            if (cabin.getAvailableSpace() == LifeformType.BROWN_ALIEN.getRequiredSpace()) {
                                neighbours = this.getNearestComponents(cabin);

                                for (ClientComponent neighbour : neighbours) {
                                    switch (neighbour) {
                                        case ClientVital vital -> {
                                            if (vital.getVitalType() == VitalType.BROWN_VITAL) {
                                                return true;
                                            }
                                        }
                                        case null, default -> {}
                                    }
                                }
                            }
                        }
                    }
                    case null, default -> throw new IllegalArgumentException("ERROR: Given lifeform type is null or invalid");
                }
            }
            case null, default -> throw new IllegalArgumentException("ERROR: Given component is not a cabin");
        }

        return false;
    }

    /**
     * Adds the given lifeform to the given cabin.
     */
    public void addLifeformToCabin(int i, int j, LifeformType lifeformType) throws IllegalArgumentException {
        switch (this.components[i][j]) {
            case ClientCabin cabin -> {
                cabin.addInhabitant(new Lifeform(lifeformType));

                if (lifeformType == LifeformType.PURPLE_ALIEN) {
                    this.purpleAlienPosition = cabin;
                }
                else if (lifeformType == LifeformType.BROWN_ALIEN) {
                    this.brownAlienPosition = cabin;
                }
            }
            case null, default -> {
                throw new IllegalArgumentException("ERROR: No ClientCabin present at coordinates (" + i + ", " + j + ")");
            }
        }
    }

    /**
     * Removes the given lifeform type from the given cabin at coordinates (i, j)
     */
    public void removeLifeformFromCabin(int i, int j, LifeformType lifeformType) throws IllegalArgumentException {
        switch (this.components[i][j]) {
            case ClientCabin cabin -> {
                int index = cabin.getInhabitants().stream()
                        .map(Lifeform::getLifeformType)
                        .toList()
                        .indexOf(lifeformType);

                cabin.removeInhabitant(cabin.getInhabitants().get(index));

                if (lifeformType == LifeformType.PURPLE_ALIEN) {
                    this.purpleAlienPosition = null;
                }
                else if (lifeformType == LifeformType.BROWN_ALIEN) {
                    this.brownAlienPosition = null;
                }
            }
            case null, default -> {
                throw new IllegalArgumentException("ERROR: No ClientCabin present at coordinates (" + i + ", " + j + ")");
            }
        }
    }

    /**
     * @return This ship's baseline firepower
     */
    public float getBaselineFirepower() {
        // Adding the firepower of only the single cannons
        return (float) this.cannonList.stream()
                .filter((ClientCannon c) -> ((c.getFirePower() < 1 && c.getDirection() != 0) || (c.getFirePower() == 1 && c.getDirection() == 0)))
                .mapToDouble(ClientCannon::getFirePower)
                .sum();
    }

    /**
     * @return This ship's baseline engine power
     */
    public int getBaselineEnginePower() {
        // Adding the engine power of only the single engines
        return (int) this.engineList.stream()
                .filter(e -> (e.getSpeed() == 1))
                .count();
    }

    public void consumeEnergy(int energyToConsume) throws InsufficientEnergyException {
        int availableEnergy;

        // Consuming the given amount of energy
        for (ClientBattery battery : this.batteryList) {
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

    /**
     * @return The widget containing all of this ship's component's widgets
     *         as they are put inside this ship's grid
     */
    public WidgetTUI getShipGridWidget() {
        WidgetTUI shipGridWidget;
        WidgetTUI tmpComponentWidget;
        List<List<String>> screenRowList;
        List<List<String>> mergedWidgetRowList;

        int scale = 3;
        int height = scale;
        int width = 3 * scale + 2;

        int shipRows, shipCols;
        int rowOffset, colOffset;
        int shipRowRange, shipColRange;

        // Initializations
        shipGridWidget = new WidgetTUI();
        tmpComponentWidget = new WidgetTUI();
        mergedWidgetRowList = new ArrayList<>();

        tmpComponentWidget.setHeight(height);
        tmpComponentWidget.setWidth(width);

        if (shipDimensions.containsKey(this.difficultyLevel) && shipOffsets.containsKey(this.difficultyLevel)) {
            shipRows = shipDimensions.get(this.difficultyLevel).getKey();
            shipCols = shipDimensions.get(this.difficultyLevel).getValue();

            rowOffset = shipOffsets.get(this.difficultyLevel).getKey();
            colOffset = shipOffsets.get(this.difficultyLevel).getValue();
        }
        else {
            shipRows = grid_rows;
            shipCols = grid_cols;

            rowOffset = 0;
            colOffset = 0;
        }

        shipRowRange = rowOffset + shipRows;
        shipColRange = colOffset + shipCols;

        // Generating the ship's widget screen by composing each row of the ship
        // horizontally first, and then compose each row horizontally, thus
        // creating the ship's grid screen
        for (int i = rowOffset; i < shipRowRange; i++) {
            screenRowList = new ArrayList<>();

            for (int j = colOffset; j < shipColRange; j++) {
                ClientComponent component = this.components[i][j];

                if (shipProfiles.get(this.difficultyLevel)[i][j] == 1) {
                    if (component != null) {
                        // If the component is not null AND its coordinates are on the actual ship,
                        // then go ahead and generate its widget
                        tmpComponentWidget.setScreen(this.components[i][j].getComponentScreen());
                        screenRowList.add(tmpComponentWidget.getScreen());
                    }
                    else {
                        // Otherwise, this tile is a placeholder, thus generate the placeholder screen
                        screenRowList.add(this.generateComponentPlaceholderWidget().getScreen());
                    }
                }
                else {
                    // Otherwise, this is actual empty space1
                    screenRowList.add(generateEmptySpaceScreen(3)); // Performance hit with style
                }
            }

            // Appending the row widget's screen to the ship's grid screen
            mergedWidgetRowList.add(WidgetTUI.composeScreensHorizontally(screenRowList));
        }

        // Merging all rows together into the final widget
        shipGridWidget.setScreen(WidgetTUI.composeScreensVertically(mergedWidgetRowList));

        // Adding right-side padding
        shipGridWidget.addPadding(0, 1, 0, 0);

        // Wrapping the ship's grid widget with the default border
        shipGridWidget.wrapWidgetWithBorder();

        if (ClientShip.shipOffsets.containsKey(this.difficultyLevel)) {
            // Wrapping the ship's grid widget with the indexed border
            shipGridWidget = ClientShip.wrapShipWidgetWithCoordinatesBorder(
                    shipGridWidget,
                    ClientShip.shipOffsets.get(this.difficultyLevel).getKey() + 1,
                    ClientShip.shipOffsets.get(this.difficultyLevel).getValue() + 1
            );
        }
        else {
            shipGridWidget = ClientShip.wrapShipWidgetWithCoordinatesBorder(shipGridWidget, 1, 1);
        }

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

        // TODO: See if there's the need to reimplement/modify getFirePower and getEnginePower for client-side
        //       use or they can just be ported from Ship into ClientShip without changing anything
        // Getting all the ship's stats
        shipStatsScreen.add("Total Crew: " + this.getAllLifeforms().size());
        shipStatsScreen.add("Firepower: " + this.getBaselineFirepower());
        shipStatsScreen.add("EnginePower: " + this.getBaselineEnginePower());

        shipStatsWidget.appendScreen(shipStatsScreen);
        shipStatsWidget.centerWidgetScreen();
        shipStatsWidget.wrapWidgetWithBorder();

        return shipStatsWidget;
    }

    /**
     * Creates a Widget containing this ship's grid, its owner and its statistics
     *
     * @return This ship's TUIPage widget, which will be composed alongside other widgets
     *         in the final TUIPage
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
    private List<String> generateEmptySpaceScreen(int scale) {
        List<String> emptySpaceScreen = new ArrayList<>();
        List<String> colorPool = new ArrayList<>();
        Random rand = new Random();
        StringBuilder spaceString;
        int randIndex, randColor;

        // TODO: Figure out where to put these values
        // int scale = 3;
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

    /**
     * @return A widget of the same dimensions of any client component widget
     *         but with its screen filled with Space.
     *         This is needed to outline the ship profile for the current level
     */
    private WidgetTUI generateComponentPlaceholderWidget() {
        WidgetTUI componentPlaceholder = new WidgetTUI();

        componentPlaceholder.setScreen(this.generateEmptySpaceScreen(1));
        componentPlaceholder.setHeight(3);
        componentPlaceholder.setWidth(3 * 3 + 2);
        componentPlaceholder.wrapWidgetWithBorder();

        return componentPlaceholder;
    }

    /**
     * @return The widget containing all of this ship's component's widgets
     *         as they are put inside this ship's grid and also adds a custom
     *         border with row and column indexes to locate a component with ease
     *         (just like in the actual board game)
     */
    private static WidgetTUI wrapShipWidgetWithCoordinatesBorder(WidgetTUI shipGridWidget, int startRowIndex, int startColIndex) {
        if (shipGridWidget != null) {
            StringBuilder tmpString;
            int index, i;

            // Storing the old screen and clearing the previous one
            // since it's not wrapped
            List<String> unwrappedScreen = shipGridWidget.unwrapWidgetFromBorder().getScreen();;

            // Generating a mockup component to get its dimensions
            ClientStructural clientStructural = new ClientStructural(-1, Arrays.asList(0, 0, 0, 0));
            WidgetTUI widget = clientStructural.generateWidget();
            int componentHeight = widget.getHeight();
            int componentWidth = widget.getWidth();

            int middleSidesStrlen = 2;
            int shipHeight = shipGridWidget.getHeight();
            int shipWidth = shipGridWidget.getWidth();

            // Resetting the ship widget's screen
            shipGridWidget.resetScreenAndDimensions();

            // Increase the border counter by one
            shipGridWidget.setBorderCount(shipGridWidget.getBorderCount() + 1);

            // Top Left Corner
            tmpString = new StringBuilder(UnicodeCharacters.SINGLE_LINE_TL_CORNER);

            // Upper border
            tmpString.append(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE.repeat(middleSidesStrlen - 1));
            index = startColIndex;
            i = 0;
            while (i < shipWidth) {
                if ((i % componentWidth) == (componentWidth / 2)) {
                    tmpString.append(index);
                    index++;

                    i += Integer.toString(index).length();
                }
                else {
                    tmpString.append(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
                    i++;
                }
            }
            tmpString.append(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE.repeat(middleSidesStrlen - 1));

            // Top Right Corner
            tmpString.append(UnicodeCharacters.SINGLE_LINE_TR_CORNER);
            shipGridWidget.appendString(tmpString.toString());

            // Middle
            index = startRowIndex;
            for (i = 0; i < shipHeight; i++) {
                tmpString = new StringBuilder();

                if (i % componentHeight == (componentHeight / 2)) {
                    String leftAlignedIndexString = String.format("%-" + middleSidesStrlen + "d", index);

                    // Left side index
                    tmpString.append(leftAlignedIndexString);

                    // Old unwrapped screen goes in the middle
                    String oldLine = unwrappedScreen.get(i);
                    tmpString.append(oldLine);

                    int oldLineLen = PrintUtils.removeUnicodeFromString(oldLine).length();

                    // Adding right-side padding
                    if (oldLineLen < shipWidth - 2) {
                        tmpString.append(SPACE.repeat(shipWidth - (2 * middleSidesStrlen) - oldLineLen));
                    }

                    // Right side index
                    tmpString.append(leftAlignedIndexString);
                    index++;
                }
                else {
                    // Left side
                    tmpString.append(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE + SPACE);

                    // Old unwrapped screen goes in the middle
                    String oldLine = unwrappedScreen.get(i);
                    tmpString.append(oldLine);

                    int oldLineLen = PrintUtils.removeUnicodeFromString(oldLine).length();

                    // Adding right-side padding
                    if (oldLineLen < shipWidth - 2) {
                        tmpString.append(SPACE.repeat(shipWidth - (2 * middleSidesStrlen) - oldLineLen));
                    }

                    // Right side
                    tmpString.append(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE + SPACE);
                }

                // Finally, add the wrapped line to the new screen
                shipGridWidget.appendString(tmpString.toString());
            }

            // Bottom Left Corner
            tmpString = new StringBuilder(UnicodeCharacters.SINGLE_LINE_BL_CORNER);

            // Lower border
            tmpString.append(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE.repeat(middleSidesStrlen - 1));
            index = startColIndex;
            i = 0;
            while (i < shipWidth) {
                if ((i % componentWidth) == (componentWidth / 2)) {
                    tmpString.append(index);
                    index++;

                    i += Integer.toString(index).length();
                }
                else {
                    tmpString.append(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
                    i++;
                }
            }
            tmpString.append(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE.repeat(middleSidesStrlen - 1));

            // Bottom Right Corner
            tmpString.append(UnicodeCharacters.SINGLE_LINE_BR_CORNER);
            shipGridWidget.appendString(tmpString.toString());

            // Adding the thickness of the borders
            shipGridWidget.setHeight(shipHeight);
            shipGridWidget.setWidth(shipWidth);
        }

        return shipGridWidget;
    }

//    public void updateShip(CardStateJSON cardStateJSON) {
//
//    }
}
