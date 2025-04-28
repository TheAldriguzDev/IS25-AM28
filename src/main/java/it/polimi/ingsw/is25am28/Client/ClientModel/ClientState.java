package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.List;

public class ClientState {
    protected ClientModel model;

    public ClientState(ClientModel model) {
        this.model = model;
    }

    public List<ClientComponent> getConstructionShipComponents() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'getConstructionShipComponents' is not supported in the " + this + " state");
    }

    public List<ClientComponent> getReservedComponents() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'getReservedComponents' is not supported in the " + this + " state");
    }

    /**
     * Method used to return all client event cards that players can see
     * during the ship construction phase
     */
    public List<ClientEventCard> getEventCards() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'getEventCards' is not supported in the " + this + " state");
    }

    public boolean getPlayerFinishedBuildingShip(String playerNickname) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'getPlayerFinishedBuildingShip' is not supported in the " + this + " state");
    }

    public void setPlayerFinishedBuildingShip(String playerNickname) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'setPlayerFinishedBuildingShip' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to select a Tile from the table in the ShipConstructionState
     * */
    public ClientComponent selectTile(int i, int j) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'selectTile' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to deselect a Tile from the table in the ShipConstructionState
     * */
    public void deselectTile(ClientComponent component, int i, int j) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'deselectTile' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to reserve a Tile to build his Ship
     * */
    public void reserveTile(ClientComponent component) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'reserveTile' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to flip the timer
     * */
    public void flipTimer() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'flipTimer' is not supported in the " + this + " state");
    }

    /**
     * Command used when the TimerDTO arrives and the players need
     * to know that the timer could now be flippable
     */
    public void setIsTimeRunning(TimerDTO timerState) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'setIsTimeRunning' is not supported in the " + this + " state");
    }

    /**
     * Command used when the TimerDTO arrives and the players need
     * to know that the timer could now be flippable
     */
    public boolean isTimeRunning() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'isTimeRunning' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to remove a component in the FixShipState
     * */
    public void removeComponentFromShip(int i, int j) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'removeComponentFromShip' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to send to the server the removed components in the FixShipState
     * */
    public void confirmFix() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'confirmShip' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to add a LifeForm in the given component
     * */
    public void addLifeFormToShip(int i, int j, LifeformType lifeform) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'deselectTile' is not supported in the " + this + " state");
    }

    /**
     * Command used by the player when he wants to send to the server the added LifeForm in the PopulateShipState
     * */
    public void confirmPopulation() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The 'confirmPopulation' is not supported in the " + this + " state");
    }
}
