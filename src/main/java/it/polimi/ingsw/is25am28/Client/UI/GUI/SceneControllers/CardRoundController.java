package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CardRoundController extends GUIController {
    @FXML private ImageView cardImageView;
    @FXML private ImageView shipBoardImageView;
    @FXML private VBox resourceBankVBox;
    @FXML private VBox statsVBox;
    @FXML private VBox actionsVBox;
    @FXML private GridPane shipGrid;
    @FXML private ImageView shipImageView;
    @FXML private StackPane imagePane;
    @FXML private GridPane viewOtherShipsGrid;

    List<ClientEventCard> cards;
    ClientEventCard currEventCard;

    public void init(CardRoundDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();
        this.guiUtils = new GUIUtils(this.clientModel);

        this.cards = this.clientModel.getClientEventCards();

        // Setting the card's image
        this.setCurrentEventCard(state.getCardInfo().getCardID());

        this.componentsImagesMap = new HashMap<>();
        this.playersShipGridPane = new HashMap<>();


//        this.shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        // Setting the buttons to view other ships
        this.initViewOtherShipsGrid();

        // Setting the correct background
        this.guiUtils.setShipGridBackground(this.shipImageView);

        for (ClientPlayer player : this.clientModel.getAllClientPlayers().values()) {
            // Creating an empty ship grid
            GridPane shipGrid = this.guiUtils.createEmptyShipGrid(player);
            // Creating the ship's visuals
            this.componentsImagesMap.put(player.getNickname(), this.guiUtils.createShipVisuals(player.getNickname(), shipGrid));
            // Adding the shipGrid to the map
            this.playersShipGridPane.put(player.getNickname(), shipGrid);
        }

        // Setting the current shipGrid to this client's ship
        this.imagePane.getChildren().remove(this.shipGrid);
        this.shipGrid = this.playersShipGridPane.get(this.clientModel.getNickname());
        this.imagePane.getChildren().add(this.shipGrid);



        /* SHIP SETTING */
        /*
         .........
        */

        // Setting the resourceBank's VBox
//        ResourceBank bank = model.getResourceBank();
        // ...

        // Setting the stats VBox
        // ...
    }

    /**
     * Sets the current card's image based on the ID
     */
    private void setCurrentEventCard(int cardID) {
        // Setting the current eventCard
        for(ClientEventCard card : this.cards) {
            if(card.getCardID() == cardID) {
                this.currEventCard = card;
            }
        }

        // Setting the card's image
        URL resource;
        System.out.println(PrintUtils.addColor("Card's PATH: " + this.currEventCard.getCardPath(), ANSIColors.GREEN));
        resource = Objects.requireNonNull(getClass().getResource(this.currEventCard.getCardPath()));
        Image img = new Image(resource.toExternalForm(), 235, 315, true, true);
        this.cardImageView.setImage(img);

    }

    /**
     * Creates a 0*1 grid, subsequently adding a number of rows (each one containing a toggleButton) equal to the number of players - 1 in the current game
     */
    private void initViewOtherShipsGrid() {
        ToggleGroup viewOtherShipsToggleGroup = new ToggleGroup();
        int i = 0;
        for (String playerNickname : this.clientModel.getAllClientPlayers().keySet()) {
            if (playerNickname.equals(this.clientModel.getNickname())) {
                continue;
            }
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0);
            row.setVgrow(Priority.ALWAYS);
            this.viewOtherShipsGrid.getRowConstraints().add(row);

            ToggleButton toggleButton = new ToggleButton();
            toggleButton.setToggleGroup(viewOtherShipsToggleGroup);
            toggleButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            toggleButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            toggleButton.setText(playerNickname);
            toggleButton.getStyleClass().add("blue");
            this.viewOtherShipsGrid.add(toggleButton, 0, i);
            i++;
        }
    }

    private void initCommandBox() {

    }
}
