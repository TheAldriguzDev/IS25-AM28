package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShipConstructionState;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlayerEndedShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionType;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class use the VisitorPattern to save useful information of each state and then show this information in
 * the given UI
 * */

public class ViewUpdater implements StateVisitor {
    private final ClientUI ui;
    private final ClientModel model;

    // TODO: In this class we need to update the model before invoking the ui show methods

    public ViewUpdater(ClientUI ui, ClientModel model) {
        this.ui = ui;
        this.model = model;
    }

    @Override
    public void visit(StateDTO state) {
        System.out.println(state.getStateName());
        System.out.println(state);
    }

    @Override
    public void visit(AvailableGamesDTO state) throws Exception {
        this.ui.showLobbies(state);
    }

    // TODO: Remove this method since it's not used anymore
    @Override
    public void visit(CreateGameStateDTO state) throws Exception {

    }

    // TODO: Remove this method since it's not used anymore
    @Override
    public void visit(WaitingForGameConfigurationDTO state) {

    }

    @Override
    public void visit(WaitPlayersStateDTO state) throws Exception {
        this.ui.showWaitingForPlayers(state);
    }

    @Override
    public void visit(ShipConstructionDTO state) throws Exception {
        // Set the model state to the ShipConstructionState that will initialize all the components
        synchronized (this.model) {
            this.model.setState(new ClientShipConstructionState(this.model, state.getAllComponents()));
        }

        this.ui.showShipConstruction(state);
    }

    /**
     * This method will be used to set the component as Visible or not Visible. In addition, the component will be marked
     * as flipped.
     * */
    @Override
    public void visit(ConstructionComponentDTO state) throws Exception {
        if (state.getEventType().equals(ShipConstructionType.TILE_EVENT.toString())) {
            int idx = (state.getI() * 19) + state.getJ();

            ClientComponent comp = this.model.getState().getConstructionShipComponents().get(idx);
            comp.setAsFlipped();
            comp.setIsVisible(!state.isSelected());
        }
    }

    @Override
    public void visit(PlayerEndedShipDTO state) throws Exception {

    }

    @Override
    public void visit(FixShipDTO state) {

    }

    @Override
    public void visit(PopulateShipDTO state) {

    }

    @Override
    public void visit(CardRoundDTO state) {

    }

    @Override
    public void visit(EndGameDTO state) {

    }

    // TODO Change from String message to ErrorDTO
    public void reportError(String message) {
        this.ui.showError(new ErrorAnswer(message));
    }

    public void commitCommand(String playerNickname) {
        this.ui.commitCommand(playerNickname);
    }
}
