package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Connector;

public class ClientComponent {
    private int id;

    protected Connector[] sides;
    protected int direction;
    private int row;
    private int col;

    private boolean isFlipped;

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
