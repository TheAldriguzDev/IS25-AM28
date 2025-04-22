package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.Model.Connector;

import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract sealed class Component implements WidgetTUIGenerator permits Cannon, Cabin, Storage, Vital, Engine, Battery, Shield, Structural {
    private int row;
    private int col;

    protected Connector[] sides;

    /**
     * Value between 0 and 3 that indicates the direction the component is currently facing
     * (the values are: 0 (top), 1 (right), 2 (bottom), 3 (left))
     */
    protected int direction;

    /**
     * Unique tile identifier
     */
    private int id = 0;

    /**
     * @return This component's unique identifier
     */
    public int getTypeId(){
        switch (this){
            case Cannon _:    return 0;
            case Cabin _:     return 1;
            case Storage _:   return 2;
            case Vital _:     return 3;
            case Engine _:    return 4;
            case Battery _:   return 5;
            case Shield _:    return 6;
            case Structural _:return 7;
        }
    }

    public Component(List<Integer> connectors) {
        sides = new Connector[4];

        for (int i = 0; i < sides.length; i++) {
            sides[i] = Connector.fromOrdinal(connectors.get(i));
        }
    }

    /**
     * @param nearest This component's adjacent neighbours
     * @return TRUE if this component's connectors are all compatible with their neighbours', FALSE otherwise
     */
    public boolean check(Component[] nearest) {
        if (
                nearest[0] != null && (
                        (getTopSide() == Connector.ZERO_PIPES && nearest[0].getBottomSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                (getTopSide() != Connector.THREE_PIPES && nearest[0].getBottomSide() != Connector.THREE_PIPES && getTopSide() != nearest[0].getBottomSide()) // they are not equals (even with 3 piped conjunction)
                )
        ) {
            return false;
        }

        if (
                nearest[1] != null && (
                        (getRightSide() == Connector.ZERO_PIPES && nearest[1].getLeftSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                (getRightSide() != Connector.THREE_PIPES && nearest[1].getLeftSide() != Connector.THREE_PIPES && getRightSide() != nearest[1].getLeftSide()) // they are not equals (even with 3 piped conjunction)
                )
        ) {
            return false;
        }

        if (
                nearest[2] != null && (
                        (getBottomSide() == Connector.ZERO_PIPES && nearest[2].getTopSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                (getBottomSide() != Connector.THREE_PIPES && nearest[2].getTopSide() != Connector.THREE_PIPES && getBottomSide() != nearest[2].getTopSide()) // they are not equals (even with 3 piped conjunction)
                )
        ) {
            return false;
        }

        if (
                nearest[3] != null && (
                        (getLeftSide() == Connector.ZERO_PIPES && nearest[3].getRightSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                (getLeftSide() != Connector.THREE_PIPES && nearest[3].getRightSide() != Connector.THREE_PIPES && getLeftSide() != nearest[3].getRightSide()) // they are not equals (even with 3 piped conjunction)
                )
        ) {
            return false;
        }

        return true;
    }

    /**
     * @return The direction that this component is currently facing
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * @return A coordinate pair [row, col] describing this component's position in the ship's grid
     */
    public int[] getPosition() {
        int[] position = new int[2];

        position[0] = row;
        position[1] = col;

        return position;
    }

    /**
     * The position of this component, which is set when it gets placed in the ship's grid
     * @param row The row where this component is located at
     * @param col The column where this component is located at
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Component setRotation(int rotation) {
        direction = rotation % 4;
        return this;
    }

    public Component rotateLeft() {
        direction--;
        if (direction < 0) { direction = 3; }
        return this;
    }

    public Component rotateRight() {
        direction++;
        if (direction > 3) { direction = 0; }
        return this;
    }

    public Connector getLeftSide() {
        int normalizedPos = 3 - direction;
        if (normalizedPos < 0) normalizedPos += 4;
        return sides[normalizedPos % 4];
    }

    public Connector getRightSide() {
        int normalizedPos = 1 - direction;
        if (normalizedPos < 0) normalizedPos += 4;
        return sides[normalizedPos % 4];
    }

    public Connector getTopSide() {
        int normalizedPos = 4 - direction;
        return sides[normalizedPos % 4];
    }

    public Connector getBottomSide() {
        int normalizedPos = 2 - direction;
        if (normalizedPos < 0) normalizedPos += 4;
        return sides[normalizedPos % 4];
    }

    /**
     * @return This component's unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * NEVER CALL THIS METHOD. USED TO NOT BREAK ANYTHING
     */
    public Component setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Method used to transform the component in a sendable way.
     * The result is similar to the "Tile.json" file.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        List<Integer> connectors = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            connectors.add(sides[i].ordinal());
        }

        map.put("id", getId());
        map.put("tid", getTypeId());
        map.put("connectors", connectors);
        map.put("row", row);
        map.put("col", col);

        return map;
    }

    /**
     * @return This component's screen
     */
    public abstract List<String> getComponentScreen();

    /**
     * @return A TUI border-wrapped widget containing the component's text representation
     *         as well as other information about itself (e.g.: energy left inside a battery)
     */
    public WidgetTUI generateWidget() {
        WidgetTUI componentWidget = new WidgetTUI();
        List<String> customBorderScheme = new ArrayList<String>();

//            // TODO: Understand better these indexes
//            int scale = 3;
//            int height = scale;
//            int width = 3 * height + 2;
//
//            componentWidget.setHeight(height);
//            componentWidget.setWidth(width);

//        // Creating the custom border character list that will be
//        // used by the wrapper to create the border
//        for (int i = 0; i < 8; i++) {
//            customBorderScheme.add(WidgetTUI.defaultBorderCharacters.get(i));
//        }
//
//        // Adding this component's connectors as the center side symbols in the border schema
//        for (Connector connector : this.sides) {
//            customBorderScheme.add("" + connector.ordinal());
//        }

        // Finally, setting the screen of this component
        componentWidget.setScreen(this.getComponentScreen());

        return componentWidget;
    }

    /**
     * @return A custom border made specifically to show the actual
     *         connector pipes on each side of the ship's components
     */
    protected List<String> generateComponentCustomBorder() {
        List<String> customBorderScheme = new ArrayList<>(WidgetTUI.defaultBorderCharacters);

        // Adding this component's connectors to the border scheme
        // Top Connector
        switch (this.getTopSide().ordinal()) {
            case 1 -> {
                customBorderScheme.set(9, UnicodeCharacters.CONNECTOR_TOP_CENTER);
            }
            case 2 -> {
                customBorderScheme.set(8, UnicodeCharacters.CONNECTOR_TOP_CENTER_LEFT);
                customBorderScheme.set(10, UnicodeCharacters.CONNECTOR_TOP_CENTER_RIGHT);
            }
            case 3 -> {
                customBorderScheme.set(8, UnicodeCharacters.CONNECTOR_TOP_CENTER_LEFT);
                customBorderScheme.set(9, UnicodeCharacters.CONNECTOR_TOP_CENTER);
                customBorderScheme.set(10, UnicodeCharacters.CONNECTOR_TOP_CENTER_RIGHT);
            }
        }

        // Right Connector
        switch (this.getRightSide().ordinal()) {
            case 1 -> {
                customBorderScheme.set(12, UnicodeCharacters.CONNECTOR_RIGHT_CENTER);
            }
            case 2 -> {
                customBorderScheme.set(11, UnicodeCharacters.CONNECTOR_RIGHT_CENTER_TOP);
                customBorderScheme.set(13, UnicodeCharacters.CONNECTOR_RIGHT_CENTER_BOTTOM);
            }
            case 3 -> {
                customBorderScheme.set(11, UnicodeCharacters.CONNECTOR_RIGHT_CENTER_TOP);
                customBorderScheme.set(12, UnicodeCharacters.CONNECTOR_RIGHT_CENTER);
                customBorderScheme.set(13, UnicodeCharacters.CONNECTOR_RIGHT_CENTER_BOTTOM);
            }
        }

        // Bottom Connector
        switch (this.getBottomSide().ordinal()) {
            case 1 -> {
                customBorderScheme.set(15, UnicodeCharacters.CONNECTOR_BOTTOM_CENTER);
            }
            case 2 -> {
                customBorderScheme.set(14, UnicodeCharacters.CONNECTOR_BOTTOM_CENTER_RIGHT);
                customBorderScheme.set(16, UnicodeCharacters.CONNECTOR_BOTTOM_CENTER_LEFT);
            }
            case 3 -> {
                customBorderScheme.set(14, UnicodeCharacters.CONNECTOR_BOTTOM_CENTER_RIGHT);
                customBorderScheme.set(15, UnicodeCharacters.CONNECTOR_BOTTOM_CENTER);
                customBorderScheme.set(16, UnicodeCharacters.CONNECTOR_BOTTOM_CENTER_LEFT);
            }
        }

        // Left Connector
        switch (this.getLeftSide().ordinal()) {
            case 1 -> {
                customBorderScheme.set(18, UnicodeCharacters.CONNECTOR_LEFT_CENTER);
            }
            case 2 -> {
                customBorderScheme.set(17, UnicodeCharacters.CONNECTOR_LEFT_CENTER_BOTTOM);
                customBorderScheme.set(19, UnicodeCharacters.CONNECTOR_LEFT_CENTER_TOP);
            }
            case 3 -> {
                customBorderScheme.set(17, UnicodeCharacters.CONNECTOR_LEFT_CENTER_BOTTOM);
                customBorderScheme.set(18, UnicodeCharacters.CONNECTOR_LEFT_CENTER);
                customBorderScheme.set(19, UnicodeCharacters.CONNECTOR_LEFT_CENTER_TOP);
            }
        }

        return customBorderScheme;
    }
}
