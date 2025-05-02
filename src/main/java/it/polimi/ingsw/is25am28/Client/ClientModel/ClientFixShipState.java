package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;

public class ClientFixShipState extends ClientState {
    private final FixShipDTO fixShipDTO;

    public ClientFixShipState(ClientModel model, FixShipDTO fixShipDTO) {
        super(model);
        this.fixShipDTO = fixShipDTO;
    }

    @Override
    public FixShipDTO getFixShipDTO() {
        return this.fixShipDTO;
    }

    @Override
    public void removePlayerFromFixList(String playerNickname) {
        this.fixShipDTO.getPlayerWithInvalidShip().remove(playerNickname);
    }

    @Override
    public void removeComponentFromShip(int i, int j) {
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> { ship.removeComponent(i, j); }
        );
    }
}
