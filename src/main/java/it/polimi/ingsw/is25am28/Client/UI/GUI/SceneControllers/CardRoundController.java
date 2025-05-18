package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.List;

public class CardRoundController {
    @FXML private ImageView cardImageView;
    @FXML private ImageView shipBoardImageView;
    @FXML private VBox resourceBankVBox;
    @FXML private VBox statsVBox;
    @FXML private VBox actionsVBox;

    public void init(CardRoundDTO state) {
        ClientModel model = GUIHandler.getInstance().getClientModel();

        List<ClientEventCard> cards = model.getClientEventCards();

        ClientEventCard currentCard = cards.stream()
                .filter(card -> card.getCardID() == state.getCardInfo().getCardID())
                .findFirst()
                .orElse(null);

        // Setting the card's image
        this.cardImageView.setImage(new Image(currentCard.getCardPath()));

        // Setting the Ship's board image
        String shipBoardImagePath;
        switch (model.getDifficultyLevel()) {
            case 0 -> this.shipBoardImageView.setImage(new Image("src/main/resources/imgs/cardboard/test.ship-bridge.jpg"));
            case 2 -> this.shipBoardImageView.setImage(new Image("src/main/resources/imgs/cardboard/level-2-ship-bridge.jpg"));
        }

        /* SHIP SETTING */
        /*
         .........
        */

        // Setting the resourceBank's VBox
        ResourceBank bank = model.getResourceBank();
        // ...

        // Setting the stats VBox
        // ...
    }


}
