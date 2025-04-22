package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public sealed abstract class ClientComponent implements WidgetTUIGenerator permits ClientBattery, ClientCabin, ClientCannon, ClientEngine, ClientShield, ClientStorage, ClientStructural, ClientVital {
    // The id represent the coordinate of the component in the shipConstructionPhase. It's calculated with (19 * i) + j
    private int id;

    protected Connector[] sides;
    protected int direction;
    private int row;
    private int col;

    // isFlipped is used to decide if the tile needs to be shown in the shipConstructionState phase
    private boolean isFlipped;
    // isVisible is used to decide if the tile is "present" on the table where the user can decide which tile to select
    // when is set to true we will render an invisible component
    private boolean isVisible;

    public ClientComponent(int id, List<Integer> connectors) {
        this.id = id;

        sides = new Connector[4];
        for (int i = 0; i < sides.length; i++) {
            sides[i] = Connector.fromOrdinal(connectors.get(i));
        }
        this.isFlipped = false;
        this.isVisible = true;
    }

    /**
     * @return The direction that this component is currently facing
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Sets the component 'i' coordinate
     * */
    public void setI(int i) {
        this.row = i;
    }

    /**
     * @return the 'i' coordinate of the component
     * */
    public int getI() {
        return this.row;
    }

    /**
     * @return the 'j' coordinate of the component
     * */
    public int getJ() {
        return this.col;
    }

    /**
     * Sets the component 'j' coordinate
     * */
    public void setJ(int j) {
        this.col = j;
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

    public ClientComponent rotateLeft(){
        direction--;
        if (direction < 0) { direction = 3; }
        return this;
    }

    public ClientComponent rotateRight(){
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
     * @return true if a component is flipped on the board. The player will be able to see the component data
     * */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * This method will be used when a component has been deselected from any player.
     * In this way the component data will be visible in the board
     * */
    public void setAsFlipped() {
        this.isFlipped = true;
    }

    /**
     * @return true if the component is not selected from any other player, otherwise it returns false.
     * When its true the component can be displayed to the player in order to select from the player.
     * */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * This method will be used in response to TILE_EVENT to set the tile as visible (a player deselects it) or not visible
     * when a player selects it.
     * */
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public int getID() {
        return this.id;
    }

    public WidgetTUI generateWidget() {
        return null;
    }

    /**
     * @return This component's screen
     */
    public abstract List<String> getComponentScreen();

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

