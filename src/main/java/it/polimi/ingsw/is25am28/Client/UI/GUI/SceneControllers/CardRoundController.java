package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardRoundController {
    @FXML private ImageView cardImageView;

    public void init(CardRoundDTO state) {

    }

    public void setCard() {
        cardImageView.setImage(new Image("card.png"));
    }

}
