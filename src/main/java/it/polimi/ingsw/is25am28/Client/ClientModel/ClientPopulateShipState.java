package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

public class ClientPopulateShipState extends ClientState {
    private final PopulateShipDTO populateShipDTO;

    // Constructor
    public ClientPopulateShipState(ClientModel model, PopulateShipDTO populateShipDTO) {
        super(model);

        this.populateShipDTO = populateShipDTO;

        // Generating the component sublist when the ship is fixed
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            ClientShip::generateComponentSubLists
        );
    }

    @Override
    public PopulateShipDTO getPopulateShipDTO() {
        return this.populateShipDTO;
    }

    @Override
    public void addPlayerToPopulateList(String playerNickname) {
        this.populateShipDTO.getPlayersReady().add(playerNickname);
    }

    @Override
    public void addLifeFormToShip(int i, int j, LifeformType lifeform) {
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> ship.addLifeformToCabin(i, j, lifeform)
        );
    }
}
