package it.polimi.ingsw.is25am28;

import it.polimi.ingsw.is25am28.exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.exceptions.OutOfGridException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Ship {
    private int energy;
    private boolean[] protectedSides;
    final private Component[][] components;
    private Component core;
    // TODO: Read the TODOs below in "getComponent" method about the use of these two attributes
    private int grid_rows;
    private int grid_cols;

    // Ship constructor
    public Ship(Component[][] components, Component core) {
        this.components = components;
        this.core = core;
        this.setEnergy();
        this.setProtectedSides();
    }

    // Traverses the ship's grid in search of Component::Engine
    // and returns the total engine power of the ship
    public int getEnginePower() {
        int enginePower = 0;

        traverse(
                (Component c) -> {
                    // if (c.type == "engine") { enginePower += c.getEnginePower(); }
                    return 0;   // Here just to remove errors
                }
        );

        return enginePower;
    }

    // Traverses the ship's grid in search of Component::Cannon
    // and returns the total firepower of the ship
    public float getFirePower() {
        int firePower = 0;

        traverse(
                (Component c) -> {
                    // if (c.type == "cannon") { firePower += c.getFirePower(); }
                    return 0;   // Here just to remove errors
                }
        );

        return firePower;
    }

    // Traverses the ship's grid in search of Component::Shield
    // and stores the ship's sides that those shields are protecting
    // as booleans inside the protectedSides attribute array
    public void setProtectedSides() {
        traverse(
                (Component c) -> {
                    // if (c.type == "shield") { // Update protectedSides based on shield orientation }
                    return 0;   // Here just to remove errors
                }
        );
    }

    // Traverses the ship's grid in search of Component::Battery
    // and stores the total amount of energy that all the batteries
    // combined are storing into the energy attribute
    public void setEnergy() {
        traverse(
                (Component c) -> {
                    // if (c.type == "battery") { this.energy += c.getStoredEnergy();}
                    return 0;   // Here just to remove errors
                }
        );
    }

    // TODO: Discuss about the return type of the "traverse" method (since it uses lambdas, it needs to output something)
    // Applies lambda function to apply to each component found in the ship
    // by exploring its grid using an adapted version of the BFS algorithm
    public <R> void traverse(Function<Component, R> lambda) throws NullPointerException {
        List<Component> currLayer = new ArrayList<Component>();
        List<Component> nextLayer = new ArrayList<Component>();
        List<Component> alreadyChecked = new ArrayList<Component>();
        Component[] neighbours;
        boolean borderReached;

        if (components == null) {
            throw new NullPointerException("Ship's component grid is null. Can't apply given lambda function.");
        }
        else {
            // Starting the expansion from the core of the ship
            currLayer.add(core);
            borderReached = false;

            while (!borderReached) {
                borderReached = true;
                for (Component currComp : currLayer) {
                    lambda.apply(currComp);
                    neighbours = getNearestComponents(currComp);
                    alreadyChecked.add(currComp);

                    // Creating the nextLayer list of components for next iteration
                    // by populating it with the neighbours of each component in
                    // found in the currLayer list, except the ones that are already there
                    // (avoids overlapping) or were already checked (avoids backtracking)
                    for (int i = 0; i < 3; i++) {
                        //      !nextLayer.contains(neighbours[i]) ==> Avoids overlapping
                        // !alreadyChecked.contains(neighbours[i]) ==> Avoids backtracking
                        if (!nextLayer.contains(neighbours[i]) && !alreadyChecked.contains(neighbours[i])) {
                            nextLayer.add(neighbours[i]);
                            borderReached = false;
                        }
                    }
                }

                if (!borderReached) {
                    currLayer = nextLayer;
                    nextLayer = new ArrayList<Component>();
                }
            }
        }
    }

    // Returns a Component[] array of size 4 which contains the components neighbouring the given one
    // (CONVENTION) They are provided in the following order: North, East, South, West
    public Component[] getNearestComponents(Component component) throws NullComponentException, NullPointerException {
        Component[] neighbours = new Component[4];
        int[] positionInGrid;

        if (component == null)
        {
            // If passed component is null, there's no need to find its neighbours
            throw new NullComponentException("Passed component is null");
        }
        else
        {
            // Getting the passed component's position in the grid
            positionInGrid = component.getComponentPosition();

            // TODO: Discuss if a different, more elegant, approach is better than this first implementation
            // After checking if the given component is in a legal position, each neighbouring position
            // is tested to check if it has a component or is illegal (in the latter case, that neighbour is null)
            if (positionInGrid != null) {
                // NORTH neighbour
                try {
                    neighbours[0] = components[positionInGrid[0] - 1][positionInGrid[1]];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[0] = null;
                }

                // EAST neighbour
                try {
                    neighbours[1] = components[positionInGrid[0]][positionInGrid[1] + 1];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[1] = null;
                }

                // SOUTH neighbour
                try {
                    neighbours[2] = components[positionInGrid[0] + 1][positionInGrid[1]];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    neighbours[2] = null;
                }

                // WEST neighbour
                try {
                    neighbours[3] = components[positionInGrid[0]][positionInGrid[1] - 1];
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

    // Returns the component that is identified by the coordinates (i, j) in the
    // ship's component grid, where i is the row index and j is the column index
    public Component getComponent(int i, int j) throws OutOfGridException, NullComponentException {
        Component selectedComponent;

        // TODO: Clarify if there's the need to introduce the private attribute "grid_rows" and "grid_cols"
        // TODO: to indicate the grid size, or maybe if components is a square matrix then a private attribute
        // TODO: (for example, called "") is sufficient, with the sole purpose of determining the amount of components per row.

        if (i < 0 || j < 0 || i >= grid_rows || j >= grid_cols) {
            throw new OutOfGridException("Requested component is not in the Ship component grid");
        }
        else {
            selectedComponent = components[i][j];

            if (selectedComponent == null) {
                throw new NullComponentException("Requested component is null");
            }
            else {
                return components[i][j];
            }
        }
    }
}
