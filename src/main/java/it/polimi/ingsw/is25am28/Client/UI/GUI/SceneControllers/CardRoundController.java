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
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @FXML private GridPane commandsGrid;

    // Icons maps and interactable regions
    Map<String, Map<String, HBox>> lifeFormsMap = new HashMap<>();
    Map<String, Map<String, HBox>> itemsMap = new HashMap<>();
    Map<String, Map<String, HBox>> batteriesMap = new HashMap<>();


    ToggleGroup commandsToggleGroup = new ToggleGroup();

    List<ClientEventCard> cards;
    ClientEventCard currEventCard;

    public void init(CardRoundDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();
        this.guiUtils = new GUIUtils(this.clientModel);

        this.cards = this.clientModel.getClientEventCards();

        // Setting the card's image
        this.setCurrentEventCard(state.getCardInfo());

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

            // Setting the ship's icons
            this.lifeFormsMap.put(player.getNickname(), this.guiUtils.initShipLifeFormIcons(player.getNickname(), shipGrid));
            this.itemsMap.put(player.getNickname(), this.guiUtils.initShipItemIcons(player.getNickname(), shipGrid));
            this.batteriesMap.put(player.getNickname(), this.guiUtils.initShipBatteryIcons(player.getNickname(), shipGrid));
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



        // Setting thi single ship lifeform
        this.lifeFormsMap.put(this.clientModel.getNickname(), guiUtils.initShipLifeFormIcons(this.clientModel.getNickname(), this.playersShipGridPane.get(this.clientModel.getNickname())));

        this.itemsMap.put(this.clientModel.getNickname(), guiUtils.initShipItemIcons(this.clientModel.getNickname(), this.playersShipGridPane.get(this.clientModel.getNickname())));

        this.initStatsBox();

        this.initCommandBox();




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
    private void setCurrentEventCard(CardStateJSON cardInfo) {
        // Setting the current eventCard
        for(ClientEventCard card : this.cards) {
            if(card.getCardID() == card.getCardID()) {
                this.currEventCard = card;
            }
        }

        // Setting the card's image
        URL resource;
        System.out.println(PrintUtils.addColor("Card's PATH: " + this.currEventCard.getCardPath(), ANSIColors.GREEN));
        resource = Objects.requireNonNull(getClass().getResource(this.currEventCard.getCardPath()));
        Image img = new Image(resource.toExternalForm(), 235, 315, true, true);
        this.cardImageView.setImage(img);

        // Updating the card
//        this.currEventCard.updateCard(cardInfo);
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

        Label titleLabel = new Label("Stats");
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Creating all the labels and adding them to the statsBox

        this.statsBox.getChildren().add(titleLabel);
        this.statsBox.getChildren().add(new Label("Total credits: " + player.getCredits()));
        this.statsBox.getChildren().add(new Label("Total Crew: " + ship.getAllLifeforms().size()));
        this.statsBox.getChildren().add(new Label("FirePower: " + currentFirePower + " (Max= " + maxFirePower + ")"));
        this.statsBox.getChildren().add(new Label("EnginePower: " + doubleEnginePower + " (Max= " + maxEnginePower + ")"));
        this.statsBox.getChildren().add(new Label("Total Batteries: " + ship.getAvailableEnergy()));
        this.statsBox.getChildren().add(new Label("Total items: " + totalRedItems + "🟥 " + totalYellowItems + "🟨 " + totalGreenItems + "🟩 " + totalBlueItems + "🟦 "));
        this.statsBox.getChildren().add(new Label("Lost Components: " + player.getLostComponents()));
    }

    private void initCommandBox() {

        // Clearing the existing toggles from the grid
        this.commandsGrid.getChildren().clear();

        // Creating and adding the command description box
//        VBox commandsDescriptionBox = new VBox();
//        commandsDescriptionBox.getStyleClass().add("generic-Hbox");
//        commandsDescriptionBox.setAlignment(Pos.CENTER);
//        Label commandsDescriptionLabel = new Label();
        // TODO: set other types of text based on selected command! (or turn)
//        commandsDescriptionLabel.setText("PLACEHOLDER");
//        commandsDescriptionLabel.setStyle("-fx-font-weight: bold;");
//        commandsDescriptionBox.getChildren().add(commandsDescriptionLabel);
//        this.commandsBox.getChildren().add(commandsDescriptionBox);

        //G

        // Generating the toggles
        int col = 0;
        for (String command : this.currEventCard.getAvailableCommands()) {
            Label toggleLabel = new Label();
            System.out.println(command);
            switch (command) {
                case "playCard" -> {toggleLabel.setText("Play Card");}
                case "setCrewToRemove" -> {toggleLabel.setText("Set Crew\nTo Remove");}
                case "setItemsToBeRemoved" -> {toggleLabel.setText("Set Items\nTo Be Removed");}
                case "setItemsToBeTaken" -> {toggleLabel.setText("Set Items\nTo Be Taken");}
                case "setTakeReward" -> {toggleLabel.setText("Take the Reward?");}
                case "setChosenPlanetIndex" -> {toggleLabel.setText("Chose the\nplanet to visit");}
                case "setWantsToVisit" -> {toggleLabel.setText("Visit the POI?");}
                case "setShieldsToActivate" -> {toggleLabel.setText("Activate Shields");}
                case "setDoubleCannonsToActivate" -> {toggleLabel.setText("Activate\nDouble Cannons");}
                case "setDoubleEnginesToActivate" -> {toggleLabel.setText("Activate\nDouble Engines");}
                case "batteriesToBeStolen" -> {toggleLabel.setText("Batteries to Give Up");}
            }

            toggleLabel.setTextAlignment(TextAlignment.CENTER);
            toggleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

            // Creates a toggle with the assigned label
            ToggleButton toggleCommand = new ToggleButton();
            toggleCommand.setToggleGroup(this.commandsToggleGroup);
            toggleCommand.getStyleClass().add("blue");
            toggleCommand.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            toggleCommand.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            toggleCommand.setAlignment(Pos.CENTER);
            toggleCommand.setGraphic(toggleLabel);

            this.commandsGrid.add(toggleCommand, col, 0);
            col++;
        }

        // Add listener
        commandsToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle != null) {
                ToggleButton selected = (ToggleButton) newToggle;
                System.out.println("Selezionato: " + selected.getText());
            } else {

                ToggleButton selected = (ToggleButton) newToggle;

                switch (selected.getId()) {
                    case "playCard" -> {}
                    case "setCrewToRemove" -> {}
                    case "setItemsToBeRemoved" -> {}
                    case "setItemsToBeTaken" -> {}
                    case "setTakeReward" -> {}
                    case "setChosenPlanetIndex" -> {}
                    case "setWantsToVisit" -> {}
                    case "setShieldsToActivate" -> {}
                    case "setDoubleCannonsToActivate" -> {}
                    case "batteriesToBeStolen" -> {}
                }

            }
        });
    }



}
