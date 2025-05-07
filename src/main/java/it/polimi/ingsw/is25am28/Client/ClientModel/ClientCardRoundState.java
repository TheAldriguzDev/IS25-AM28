package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard.ClientBoard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;

import java.util.Map;

public class ClientCardRoundState extends ClientState {
    private CardRoundDTO cardRoundDTO;

    public ClientCardRoundState(ClientModel model, CardRoundDTO cardRoundDTO) {
        super(model);

        this.cardRoundDTO = cardRoundDTO;

    }

    @Override
    public CardRoundDTO getCardRoundDTO() {
        return this.cardRoundDTO;
    }
}
