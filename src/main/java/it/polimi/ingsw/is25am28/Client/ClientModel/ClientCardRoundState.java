package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;

public class ClientCardRoundState extends ClientState {
    private CardRoundDTO cardRoundDTO;

    // Constructor
    public ClientCardRoundState(ClientModel model, CardRoundDTO cardRoundDTO) {
        super(model);

        this.cardRoundDTO = cardRoundDTO;

        // Generating the component sublist when the ship is fixed
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
                ClientShip::generateComponentSubLists
        );
    }

    @Override
    public CardRoundDTO getCardRoundDTO() {
        return this.cardRoundDTO;
    }
}
