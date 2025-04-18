package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;

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
}
