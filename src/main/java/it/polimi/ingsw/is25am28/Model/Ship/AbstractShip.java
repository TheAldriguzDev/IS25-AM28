package it.polimi.ingsw.is25am28.Model.Ship;

import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

import static it.polimi.ingsw.is25am28.Model.Connector.*;
import static it.polimi.ingsw.is25am28.Model.Connector.ONE_PIPE;
import static it.polimi.ingsw.is25am28.Model.Connector.TWO_PIPES;

public abstract class AbstractShip {
    public static final Map<Integer, int[][]> shipProfiles = new HashMap<>();
    public static final Map<Integer, Pair<Integer, Integer>> shipDimensions = new HashMap<>();
    public static final Map<Integer, Pair<Integer, Integer>> shipOffsets = new HashMap<>();
    public static final int grid_rows = 12;
    public static final int grid_cols = 12;

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

    protected int difficultyLevel;

    /**
     * @return TRUE if the given connectors are compatible, FALSE otherwise.
     */
    protected boolean areSidesConnected(Connector a, Connector b) {
        return (a == THREE_PIPES && b != ZERO_PIPES)
                || (a != ZERO_PIPES && b == THREE_PIPES)
                || (a == ONE_PIPE && b == ONE_PIPE)
                || (a == TWO_PIPES && b == TWO_PIPES);
    }
}
