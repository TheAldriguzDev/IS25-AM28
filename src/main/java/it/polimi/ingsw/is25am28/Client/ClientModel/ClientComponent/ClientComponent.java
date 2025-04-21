package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.Collections;
import java.util.List;

public sealed abstract class ClientComponent permits ClientBattery, ClientCabin, ClientCannon, ClientEngine, ClientShield, ClientStorage, ClientStructural, ClientVital {
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
}
