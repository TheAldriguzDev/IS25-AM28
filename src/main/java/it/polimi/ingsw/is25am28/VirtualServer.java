package it.polimi.ingsw.is25am28;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;

/**
 * Interface that defines the server's methods that needs to be accessible to run the game
 * */
public interface VirtualServer {

    public StateJSON gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers);

    public List<StateJSON> addNewPlayer(String nickname, PlayerColor playerColor);

    public ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws SelectedConcurrencyException;

    public ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws SelectedConcurrencyException;

    public List<StateJSON> playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles);

    public TimerDTO flipTimer(String player);

    public List<StateJSON> fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws IllegalArgumentException, FixNotRequiredError;

    public List<StateJSON> populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws IllegalArgumentException;

    public List<StateJSON> playCard(ActionJSON action) throws IllegalArgumentException;
}
