package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.Component;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Lifeform;
import it.polimi.ingsw.is25am28.exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.exceptions.OutOfGridException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Ship {
    private int energy;
    private final boolean[] protectedSides;
    private final Component[][] components;
    // TODO: Find an optimal value for grid_size (I don't know how big the level II ship is, but this surely captures it)
    // NOTE: The grid MUST be a square matrix with ODD side length, otherwise
    //       the core position is ambiguous (must be chosen between two cells)
    private final int grid_size = 7;

    // Ship constructor
    public Ship() {
        this.components = new Component[grid_size][grid_size];
        // TODO: Implement the following instruction when the Component::Cabin is implemented
        // TODO: such that the first component inserted in the ship's grid is the core.
        // this.addComponent(new Cabin(inhabitants=[human, human], isCore=true));
        this.energy = 0;
        this.protectedSides = new boolean[4];
    }

    // Traverses the ship's grid in search of Component::Engine
    // and returns the total engine power of the ship
    public int getEnginePower() {
        int enginePower = 0;

        traverse(
            (Component c) -> {
                // TODO: Implement once Component::Engine is implemented
                // if (c.type == "engine") { enginePower += c.getEnginePower(); }
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
                // TODO: Implement once Component::Cannon is implemented
                // if (c.type == "cannon") { firePower += c.getFirePower(); }
            }
        );

        return firePower;
    }

    // TODO: Errors are due to the missing implementation of the Items class
    // Returns a list of all the Items onboard of the ship
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<Item>();

        traverse(
            (Component c) -> {
                // TODO: Implement once Component::Storage is implemented
                // if (c.type == "storage") { itemList.add(c.getItems()); }
            }
        );

        return itemList;
    }

    // TODO: Errors are due to the missing implementation of the Items class
    // Returns the total value of all the Items onboard the ship
    public int getAllItemsValue() {
        return this.getAllItems().stream()
                .mapToInt(c -> c.getValue())
                .sum();
    }

    // Traverses the ship's grid in search of Component::Shield
    // and stores the ship's sides that those shields are protecting
    // as booleans inside the protectedSides attribute array
    public void setProtectedSides() {
        traverse(
            (Component c) -> {
                // TODO: Implement once Component::Shield is implemented
                // if (c.type == "shield") { // Update protectedSides based on shield orientation }
            }
        );
    }

    // Traverses the ship's grid in search of Component::Battery
    // and stores the total amount of energy that all the batteries
    // combined are storing into the energy attribute
    public void setEnergy() {
        traverse(
            (Component c) -> {
                // TODO: Implement once Component::Battery is implemented
                // if (c.type == "battery") { this.energy += c.getStoredEnergy();}
            }
        );
    }

    // Traverses the ship's grid in search of Component::Cabin
    // and returns a list of all the Lifeforms present on the ship
    public List<Lifeform> getAllLifeforms() {
        List<Lifeform> lifeforms = new ArrayList<Lifeform>();

        traverse(
            (Component c) -> {
                // TODO: Implement once Component::Lifeform is implemented
                // if (c.type == "cabin") { lifeforms.addAll(c.getInhabitants()); }
            }
        );

        return lifeforms;
    }

    // TODO: Discuss about the return type of the "traverse" method (since it uses lambdas, it needs to output something)
    // Applies lambda function to apply to each component found in the ship
    // by exploring its grid using an adapted version of the BFS algorithm
    public void traverse(Consumer<Component> lambda) {
        List<Component> currLayer = new ArrayList<Component>();
        List<Component> nextLayer = new ArrayList<Component>();
        List<Component> alreadyChecked = new ArrayList<Component>();
        Component[] neighbours;
        boolean borderReached;

        // Starting the expansion from the core of the ship, which is
        // positioned at coordinates (grid_size/2, grid_size/2)
        currLayer.add(components[grid_size / 2][grid_size / 2]);
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

    // Returns a Component[] array of size 4 which contains the components neighbouring the given one
    // (CONVENTION) They are provided in the following order: North, East, South, West
    public Component[] getNearestComponents(Component component) throws NullComponentException, NullPointerException {
        Component[] neighbours = new Component[4];
        int[] positionInGrid;

        if (component == null) {
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

    // Adds the given component at the given coordinates (i, j) in the ship's component grid.
    public void addComponent(Component component, int i, int j) throws NullComponentException, OutOfGridException {
        if (component == null) {
            throw new NullComponentException("Given component to add is null");
        }
        if (i < 0 || j < 0 || i >= grid_size || j >= grid_size) {
            throw new OutOfGridException("Cannot insert a component outside of the ship's grid");
        }

        this.components[i][j] = component;
    }

    // Removes the component from the ship's grid at the given coordinates
    public void removeComponent(int i, int j) throws OutOfGridException {
        try {
            // Setting to null removes the reference to that component, thus
            // prompting the garbage collector to remove it
            components[i][j] = null;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new OutOfGridException("Cannot remove a component outside of the ship's grid");
        }
    }

    // Returns the component that is identified by the coordinates (i, j) in the
    // ship's component grid, where i is the row index and j is the column index
    public Component getComponent(int i, int j) throws OutOfGridException, NullComponentException {
        Component selectedComponent;

        // Checking if the wanted component is actually on the ship's grid
        if (i < 0 || j < 0 || i >= grid_size || j >= grid_size) {
            throw new OutOfGridException("Requested component is not in the Ship component grid");
        }
        else {
            selectedComponent = components[i][j];

            // If it's on the ship's grid, then check whether it could be null
            // (i.e.: empty space) or an actual component.
            if (selectedComponent == null) {
                throw new NullComponentException("Requested component is null");
            }
            else {
                return components[i][j];
            }
        }
    }
}
