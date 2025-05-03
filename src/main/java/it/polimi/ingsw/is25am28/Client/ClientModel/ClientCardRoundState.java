package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;

public class ClientCardRoundState extends ClientState {
    private CardRoundDTO cardRoundDTO;

    public ClientCardRoundState(ClientModel model, CardRoundDTO cardRoundDTO) {
        super(model);
        this.cardRoundDTO = cardRoundDTO;
    }
}
