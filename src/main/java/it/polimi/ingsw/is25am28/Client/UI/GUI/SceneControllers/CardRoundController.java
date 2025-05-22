package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

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

    @FXML private VBox statsBox;
    @FXML private HBox commandsBox;

    ToggleGroup commandsToggleGroup = new ToggleGroup();

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


        /*ADD the ICONS, or implement their addition in the initShip*/
        /*...
        * ...
        * ...
        * */

        this.initStatsBox();




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

    private void initStatsBox() {
        // Getting the ship
        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        // Getting the necessary data to assemble the box

        List<Item> storedItems = ship.getAllItems();
        long totalRedItems = storedItems.stream().filter(i -> i.getColor().equals(ItemColor.RED)).count();
        long totalYellowItems = storedItems.stream().filter(i -> i.getColor().equals(ItemColor.YELLOW)).count();
        long totalGreenItems = storedItems.stream().filter(i -> i.getColor().equals(ItemColor.GREEN)).count();
        long totalBlueItems = storedItems.stream().filter(i -> i.getColor().equals(ItemColor.BLUE)).count();

        List<CoordinatePair> activatedDoubleCannons = null;
        List<CoordinatePair> activatedDoubleEngines = null;

        // Clearing the previous statsBox
        this.statsBox.getChildren().clear();

        try {
            // Gets the doubleCannons/doubleEngines activated in the card's ActionJSON
            activatedDoubleCannons = this.currEventCard.getDoubleCannonsToActivate().stream().map(Pair::getKey).toList();
        } catch (UnsupportedOperationException e) {
            // If the card does not support the operation the lists is set to null, so that the getFirepower() computes the baselinePower (since it cannot be activated in the card)
        }

        try {
            // Gets the doubleCannons/doubleEngines activated in the card's ActionJSON
            activatedDoubleEngines = this.currEventCard.getDoubleEnginesToActivate().stream().map(Pair::getKey).toList();
        } catch (UnsupportedOperationException e) {
            // If the card does not support the operation the lists is set to null, so that the getEnginePower computes the baselinePower (since it cannot be activated in the card)
        }

        float currentFirePower = ship.getFirePower(activatedDoubleCannons);
        int doubleEnginePower = ship.getEnginePower(activatedDoubleEngines);

        List<CoordinatePair> allDoubleCannons = ship.getDoubleCannons().stream()
                .map(cannon -> new CoordinatePair(cannon.getI(), cannon.getJ()))
                .toList();
        float maxFirePower = ship.getFirePower(allDoubleCannons);

        List<CoordinatePair> allDoubleEngines = ship.getDoubleEngines().stream()
                .map(engine -> new CoordinatePair(engine.getI(), engine.getJ()))
                .toList();
        int maxEnginePower = ship.getEnginePower(allDoubleEngines);

        ClientPlayer player = this.clientModel.getAllClientPlayers().get(this.clientModel.getNickname());

        // Creating all the labels and adding them to the statsBox

        this.statsBox.getChildren().add(new Label("Total credits: " + player.getCredits()));
        this.statsBox.getChildren().add(new Label("Total Crew: " + ship.getAllLifeforms().size()));
        this.statsBox.getChildren().add(new Label("FirePower: " + currentFirePower + " (Max= " + maxFirePower + ")"));
        this.statsBox.getChildren().add(new Label("EnginePower: " + doubleEnginePower + " (Max= " + maxEnginePower + ")"));
        this.statsBox.getChildren().add(new Label("Total Batteries: " + ship.getAvailableEnergy()));
        this.statsBox.getChildren().add(new Label("Total items: " + totalRedItems + "🟥 " + totalYellowItems + "🟨 " + totalGreenItems + "🟩 " + totalBlueItems + "🟦 "));
        this.statsBox.getChildren().add(new Label("Lost Components: " + player.getLostComponents()));
    }

    private void initCommandBox() {
        // Clearing the commands box
        this.commandsBox.getChildren().clear();

        //TODO: no need to delete it, only change the text, the buttons are removed from the preset grid, which is never removed
        // Creating and adding the command description box
        VBox commandsDescriptionBox = new VBox();
        commandsDescriptionBox.getStyleClass().add("generic-Hbox");
        commandsDescriptionBox.setAlignment(Pos.CENTER);
        Label commandsDescriptionLabel = new Label();
        // TODO: set other types of texd based on selected command! (or turn)
        commandsDescriptionLabel.setText("PLACEHOLDER");
        commandsDescriptionLabel.setStyle("-fx-font-weight: bold;");
        commandsDescriptionBox.getChildren().add(commandsDescriptionLabel);
        this.commandsBox.getChildren().add(commandsDescriptionBox);

        //G

        // Generating the toggles
        String toggleLabel;
        for (String command : this.currEventCard.getAvailableCommands()) {
            switch (command) {
                case "playCard" -> {toggleLabel = "Play Card";}
                case "setCrewToRemove" -> {toggleLabel = "Set Crew To Remove";}
                case "setItemsToBeRemoved" -> {toggleLabel = "Set Items To Be Removed";}
                case "setItemsToBeTaken" -> {toggleLabel = "Set Items To Be Taken";}
                case "setTakeReward" -> {toggleLabel = "Take the Reward?";}
                case "setChosenPlanetIndex" -> {toggleLabel = "Chose the planet to visit";}
                case "setWantsToVisit" -> {toggleLabel = "Visit the POI?";}
                case "setShieldsToActivate" -> {toggleLabel = "Activate Shields";}
                case "setDoubleCannonsToActivate" -> {toggleLabel = "Activate Double Cannons";}
                case "setDoubleEnginesToActivate" -> {toggleLabel = "Activate Double Engines";}
                case "batteriesToBeStolen" -> {toggleLabel = "Batteries to Give Up";}
            }

            // Creates a toggle with the assigned label
            ToggleButton toggleCommand = new ToggleButton();
            toggleCommand.setToggleGroup(toggleCommand.getToggleGroup());
            toggleCommand.getStyleClass().add("blue");


        }
    }




}
