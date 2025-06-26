package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.TAB;

/**
 * The CardRoundController class is responsible for managing the interaction
 * and behavior of all GUI elements during the card round phase of the game.
 * It includes methods to initialize and update graphical components, handle
 * user interactions, and manage player actions related to game cards.
 */

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
    @FXML private ScrollPane playerActionsScrollPane;
    @FXML private VBox turnBox;
    @FXML private VBox commandDescriptionBox;
    @FXML private VBox additionalInfoBox;
    @FXML private VBox resourceBankBox;

    @FXML private VBox statsBox;
    @FXML private HBox commandsBox;
    @FXML private GridPane commandsGrid;

    // Board visualization
    @FXML private VBox viewGameBoardContainer;
    @FXML private Pane viewGameBoardStackPaneLevel0;
    @FXML private Pane viewGameBoardStackPaneLevel2;
    @FXML private ImageView boardImageView;
    @FXML private Button goBackToCardRoundButtonFromViewBoard;

    private final Map<String, ImageView> playersRocketBoard = new HashMap<>();

    // Icons maps and interactable regions
    private final Map<String, Map<String, FlowPane>> lifeFormsMap = new HashMap<>();
    private final Map<String, Map<String, FlowPane>> itemsMap = new HashMap<>();
    private final Map<String, Map<String, FlowPane>> batteriesMap = new HashMap<>();

    // Temp icons maps for revert purposes
    private final Map<String, FlowPane> emptiedLifeforms = new HashMap<>();
    private final Map<String, FlowPane> emptiedItemsMap = new HashMap<>();
    private final Map<String, FlowPane> emptiedBatteriesMap = new HashMap<>();

    // Region maps
    private final Map<String, Region> doubleCannonsRegions = new HashMap<>();
    private final Map<String, Region> doubleEnginesRegions = new HashMap<>();
    private final Map<String, Region> shieldsRegions = new HashMap<>();
    private final Map<String, Region> cabinsRegions = new HashMap<>();
    private final Map<String, Region> storagesToFillRegions = new HashMap<>();
    private final Map<String, Region> storagesToEmptyRegions = new HashMap<>();
    private final Map<String, Region> batteriesRegions = new HashMap<>();

    // Temp region maps for revert purposes
    private final Map<String, Region> emptiedCabinsRegions = new HashMap<>();
    private final Map<String, Region> emptiedStoragesRegions = new HashMap<>();
    private final Map<String, Region> emptiedBatteriesRegions = new HashMap<>();

    private Map<String, Region> currentRegions = null;
    private Pair<EnergyConsumers, CoordinatePair> currEnergyConsumer = null;

    private final List<Map<String, Region>> allComponentMaps = List.of(
            this.doubleCannonsRegions,
            this.doubleEnginesRegions,
            this.shieldsRegions,
            this.cabinsRegions,
            this.storagesToFillRegions,
            this.storagesToEmptyRegions,
            this.batteriesRegions
    );

    private ToggleGroup commandsToggleGroup;
    private final ToggleGroup viewOtherShipsToggleGroup = new ToggleGroup();

    private List<ClientEventCard> cards;
    private ClientEventCard currEventCard;
    private ItemColor chosenItemColor;

    // All possible commands that can be selected during a cardRound (based on the card)
    private final static List<String> allCommands = List.of(
            "playCard",
            "setTakeReward",
            "setWantsToVisit",
            "setChosenPlanetIndex",
            "setCrewToRemove",
            "setItemsToBeRemoved",
            "setItemsToBeTaken",
            "setDoubleCannonsToActivate",
            "setDoubleEnginesToActivate",
            "setShieldsToActivate",
            "batteriesToBeStolen"
    );

    private List<String> availableCommands;

    private ClientShip mainShip;

    /**
     * Initializes all the clickable regions (and other attributes)
     * necessary for the interaction with the GUI, and other GUI elements
     * @param state
     */
    public void init(CardRoundDTO state) {

        this.clientModel = GUIHandler.getClientModel();
        this.guiUtils = new GUIUtils(this.clientModel);

        this.cards = this.clientModel.getClientEventCards();

        // Setting the card's image
        this.setCurrentEventCard(state.getCardInfo());

        this.componentsImagesMap = new HashMap<>();
        this.playersShipGridPane = new HashMap<>();


        this.shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
        this.mainShip = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (mainShip == null) {
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

        this.initStatsBox();

        this.initCommandBox();

        this.initTurnBox();

        this.initAdditionalInfoBox();

        this.visualizePlayerActions();

        // Initializes the board image to display on a view board request
        this.guiUtils.initViewGameBoard(
                this.viewGameBoardStackPaneLevel0,
                this.viewGameBoardStackPaneLevel2,
                this.boardImageView,
                this.playersRocketBoard
        );

        this.guiUtils.initPlayersOnGameBoard(
                this.viewGameBoardStackPaneLevel0,
                this.viewGameBoardStackPaneLevel2,
                this.playersRocketBoard
        );

        this.initResourceBankBox();

        List<ClientStorage> storages = this.mainShip.getStorageList();

        List<ClientStorage> nonEmptyStorages = storages.stream()
                .filter(storage -> !storage.getStoredItems().isEmpty())
                .toList();

        List<ClientStorage> nonFullStorages = storages.stream()
                .filter(storage -> storage.getStoredItems().size() < storage.getCapacity())
                .toList();

        List<ClientCabin> nonEmptyCabins = this.mainShip.getCabinList().stream()
                .filter(cabin -> !cabin.getInhabitants().isEmpty())
                .toList();

        List<ClientBattery> nonEmptyBatteries = this.mainShip.getBatteryList().stream()
                .filter(battery -> battery.getAvailability() > 0)
                .toList();

        List<ClientCannon> nonActivatedCannons = this.mainShip.getDoubleCannons();
        try {
            nonActivatedCannons.removeAll(this.currEventCard.getDoubleCannonsToActivate().stream()
                    .map(pair -> {
                        CoordinatePair cannonCoords = pair.getKey();
                        return (ClientCannon) this.mainShip.getComponent(cannonCoords.getI(), cannonCoords.getJ());
                    })
                    .toList());
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }

        List<ClientShield> nonActivatedShields = this.mainShip.getShieldList();
        try {
            nonActivatedShields.removeAll(this.currEventCard.getShieldsToActivate().stream()
                    .map(pair -> {
                        CoordinatePair shieldCoords = pair.getKey();
                        return (ClientShield) this.mainShip.getComponent(shieldCoords.getI(), shieldCoords.getJ());
                    })
                    .toList());
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }

        List<ClientEngine> nonActivatedEngines = this.mainShip.getDoubleEngines();
        try {
            nonActivatedEngines.removeAll(this.currEventCard.getDoubleEnginesToActivate().stream()
                    .map(pair -> {
                        CoordinatePair engineCoords = pair.getKey();
                        return (ClientEngine) this.mainShip.getComponent(engineCoords.getI(), engineCoords.getJ());
                    })
                    .toList());
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }


        // Setting all the regions with the corresponding listeners
//        this.initRegionMap(this.doubleCannonsRegions, new ArrayList<>(this.mainShip.getDoubleCannons()), this::handleDoubleCannonToActivate);
        this.initRegionMap(this.doubleCannonsRegions, new ArrayList<>(nonActivatedCannons), this::handleDoubleCannonToActivate);
//        this.initRegionMap(this.doubleEnginesRegions, new ArrayList<>(this.mainShip.getDoubleEngines()), this::handleDoubleEnginesToActivate);
        this.initRegionMap(this.doubleEnginesRegions, new ArrayList<>(nonActivatedEngines), this::handleDoubleEnginesToActivate);
//        this.initRegionMap(this.shieldsRegions, new ArrayList<>(this.mainShip.getShieldList()), this::handleShieldsToActivate);
        this.initRegionMap(this.shieldsRegions, new ArrayList<>(nonActivatedShields), this::handleShieldsToActivate);
        this.initRegionMap(this.cabinsRegions, new ArrayList<>(nonEmptyCabins), this::handleCrewToRemove);
        this.initRegionMap(this.storagesToFillRegions, new ArrayList<>(nonFullStorages), this::initAddColorCommands);
        this.initRegionMap(this.storagesToEmptyRegions, new ArrayList<>(nonEmptyStorages), this::initRemoveColorCommands);
        this.initRegionMap(this.batteriesRegions, new ArrayList<>(nonEmptyBatteries), (row, col) -> {
            if (this.currEnergyConsumer != null) {
                this.handleMandatoryBatteryCoords(row, col);
            } else {
                this.handleBatteriesToBeStolen(row, col);
            }
        });
    }

    /**
     *
     * @param componentsRegions Map to populate with the clickable regions
     * @param components List of components to determine which regions to create
     * @param onClick Function to apply on region click
     *
     */
    private void initRegionMap(Map<String, Region> componentsRegions, List<ClientComponent> components, BiConsumer<Integer, Integer> onClick) {
        // Sets all the regions of all the componentsRegions maps
        componentsRegions.clear();

        // setting the components maps
        for (ClientComponent component : components) {
            Region cell = guiUtils.generateDisabledRegion();

            int row = component.getI();
            int col = component.getJ();

            // Put the region in the map
            componentsRegions.put(guiUtils.keyFromCoords(row, col), cell);

            int ofsRow = component.getI() - shipOffsets.getKey();
            int ofsCol = component.getJ() - shipOffsets.getValue();
            this.shipGrid.add(cell, ofsCol, ofsRow);

            cell.setOnMouseClicked(e -> onClick.accept(ofsRow, ofsCol));
        }
    }

    /**
     * Sets the current card's image based on the ID
     */
    private void setCurrentEventCard(CardStateJSON cardInfo) {
        // Updates the image only if necessary
        if (this.currEventCard == null || this.currEventCard.getUniqueCardId() != cardInfo.getUniqueCardId()) {
            // Setting the current eventCard
            for(ClientEventCard card : this.cards) {
                if(card.getUniqueCardId() == cardInfo.getUniqueCardId()) {
                    this.currEventCard = card;
                }
            }

            // Setting the card's image
            URL resource;
            resource = Objects.requireNonNull(getClass().getResource(this.currEventCard.getCardPath()));
            Image img = new Image(resource.toExternalForm(), 235, 315, true, true);
            this.cardImageView.setImage(img);
        }

        // Updating the card
        this.currEventCard.updateCard(cardInfo);

        this.availableCommands = new ArrayList<>(this.currEventCard.getAvailableCommands());


    }

    /**
     * Creates a 0*1 grid, subsequently adding a number of rows (each one containing a toggleButton) equal to the number of players - 1 in the current game
     */
    private void initViewOtherShipsGrid() {
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
            toggleButton.setToggleGroup(this.viewOtherShipsToggleGroup);
            toggleButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            toggleButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            toggleButton.setText(playerNickname);
            toggleButton.getStyleClass().add("blue");
            this.viewOtherShipsGrid.add(toggleButton, 0, i);
            i++;
        }

        this.viewOtherShipsToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle == null) {
                // Go back to view the client's own ship
                this.setShipGrid(this.clientModel.getNickname());
                // Enables the commands
                this.initCommandBox();


            } else {

                this.commandsToggleGroup.selectToggle(null);

                // Exit the board visualization if the toggle
                // is pressed during that phase
                this.handleGoBackToCardRoundButton(new ActionEvent());

                // Disable the commands
                if (this.currEventCard.getPlayerNickname().equals(this.clientModel.getNickname())) {
                    for (Toggle toggleButtonCommand : this.commandsToggleGroup.getToggles()) {
                        ((ToggleButton) toggleButtonCommand).setDisable(true);
                    }
                }

                ToggleButton selected = (ToggleButton) newToggle;

                this.initCommandDescriptionBox("You are currently viewing\n" + selected.getText() + "'s ship");

                this.setShipGrid(selected.getText());
            }
        });
    }

    /**
     * Sets the shipGrid to display the ship of the given player
     */
    private void setShipGrid(String playerNickname) {
        if (this.shipGrid != null) {
            this.imagePane.getChildren().remove(this.shipGrid);
        }

        this.shipGrid = this.playersShipGridPane.get(playerNickname);
        this.imagePane.getChildren().add(this.shipGrid);
    }

    /**
     * Creates/updates the statsBox
     */
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

        Label titleLabel = new Label("YOUR Stats");
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

    /**
     * Creates/updates the turnBox
     */
    private void initTurnBox() {
        Label turnLabel = new Label();
        turnLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        this.turnBox.getChildren().clear();

        List<String> eliminatedPlayersNicknames = this.clientModel.getClientBoard().getEliminatedPlayers().stream()
                .map(ClientPlayer::getNickname)
                .toList();

        if (eliminatedPlayersNicknames.contains(this.clientModel.getNickname())) {
            turnLabel.setStyle("-fx-text-fill: red;");
            turnLabel.setText("You have been ELIMINATED!!!");

            this.showToast("You have been eliminated!", ToastType.INFO);
        } else if (this.currEventCard.getPlayerNickname().equals(this.clientModel.getNickname())) {
            turnLabel.setStyle("-fx-text-fill: #49d049;");
            turnLabel.setText("It's YOUR turn!!!");

            this.showToast("It's your turn!", ToastType.SUCCESS);
        } else {
            turnLabel.setText("It's NOT YOUR turn!!!");
            turnLabel.setStyle("-fx-text-fill: yellow;");
            this.showToast("It's not your turn!", ToastType.INFO);
        }
        this.turnBox.getChildren().add(turnLabel);
    }

    /**
     * Creates/updates the additionalInfoBox
     */
    private void initAdditionalInfoBox() {
        this.additionalInfoBox.getChildren().clear();

        String infoString = this.currEventCard.getAdditionalCardInfo();

        TextFlow infoText = new TextFlow();
        infoText.setTextAlignment(TextAlignment.CENTER);

        String[] splitText = infoString.split("\n");

        for(int i = 0; i < splitText.length - 1; i++) {
            Text textLine = new Text(splitText[i]);
            textLine.setFont(Font.font("System", FontWeight.BOLD, 13));
            textLine.setTextAlignment(TextAlignment.CENTER);
            textLine.setFill(Color.WHITE);
            infoText.getChildren().add(textLine);
            infoText.getChildren().add(new Text("\n"));
        }

        Text textLine = new Text(splitText[splitText.length - 1]);
        textLine.setFont(Font.font("System", FontWeight.BOLD, 13));
        textLine.setTextAlignment(TextAlignment.CENTER);
        textLine.setFill(Color.WHITE);
        infoText.getChildren().add(textLine);

        this.additionalInfoBox.getChildren().add(infoText);
    }

    /**
     * Creates/updates the resourceBankBox
     */
    private void initResourceBankBox() {
        this.resourceBankBox.getChildren().clear();
        Label resourceBankLabel = new Label("Resource Bank");
        resourceBankLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        this.resourceBankBox.getChildren().add(resourceBankLabel);
        Map<ItemColor, Integer> resources = this.clientModel.getResourceBank().getResources();

        int red = resources.get(ItemColor.RED);
        int yellow = resources.get(ItemColor.YELLOW);
        int green = resources.get(ItemColor.GREEN);
        int blue = resources.get(ItemColor.BLUE);

        this.resourceBankBox.getChildren().add(
                new Label((red < 100 ? String.valueOf(red) : UnicodeCharacters.INFINITY) + "🟥 "
                        + (yellow < 100 ? String.valueOf(yellow) : UnicodeCharacters.INFINITY) + "🟨 "
                        + (green < 100 ? String.valueOf(green) : UnicodeCharacters.INFINITY) + "🟩 "
                        + (blue < 100 ? String.valueOf(blue) : UnicodeCharacters.INFINITY) + "🟦 "
                )
        );
    }

    /**
     * Creates/updates the commandBox
     */
    private void initCommandBox() {

        this.commandsToggleGroup = new ToggleGroup();

        try {

            List<CoordinatePair> activatedCannonsCoords = this.currEventCard.getDoubleCannonsToActivate().stream()
                    .map(Pair::getKey)
                    .toList();

            if(this.mainShip.getFirePower(activatedCannonsCoords) > this.currEventCard.getFirepower() && !this.currEventCard.getTakeReward()) {
                // Enables the "setTakeReward" command if the baseline firepower is enough (only if not already used)
                this.availableCommands.add("setTakeReward");
            }

        } catch (UnsupportedOperationException e) {
            // Do nothing, the command will not be added
        }

        // Clearing the existing toggles from the grid
        this.commandsGrid.getChildren().clear();

        // Generating the toggles
        int col = 0;
        Label toggleLabel;
        for (String command : allCommands) {
            toggleLabel = new Label();
            // A command is added only if it's present in the available commands
            if (this.availableCommands.contains(command)) {
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
                toggleCommand.setId(command);

                this.commandsGrid.add(toggleCommand, col, 0);
                col++;
            }
        }

        // Disables the toggles if it's not this player's turn (Also sets the command's description)
        if (!this.currEventCard.getPlayerNickname().equals(this.clientModel.getNickname())) {
            this.initCommandDescriptionBox("No actions available\nit's NOT YOUR turn!");
            for (Toggle toggleButtonCommand : this.commandsToggleGroup.getToggles()) {
                ((ToggleButton) toggleButtonCommand).setDisable(true);
            }
        } else {
            this.initCommandDescriptionBox("Chose an action!");
        }

        // Adding the listener
        this.commandsToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle == null) {

                if (this.currEnergyConsumer != null) {
                    CoordinatePair energyConsumerCoords = this.currEnergyConsumer.getValue();
                    // Disabling the red highlight on the selected energyConsumer
                    switch(this.currEnergyConsumer.getKey()) {
                        case CANNON -> this.doubleCannonsRegions.get(guiUtils.keyFromCoords(energyConsumerCoords.getI(), energyConsumerCoords.getJ())).setStyle("-fx-background-color: transparent;");
                        case ENGINE -> this.doubleEnginesRegions.get(guiUtils.keyFromCoords(energyConsumerCoords.getI(), energyConsumerCoords.getJ())).setStyle("-fx-background-color: transparent;");
                        case SHIELD -> this.shieldsRegions.get(guiUtils.keyFromCoords(energyConsumerCoords.getI(), energyConsumerCoords.getJ())).setStyle("-fx-background-color: transparent;");
                    }


                    this.currEnergyConsumer = null;
                }

                disableRegion(this.currentRegions);

                this.initCommandDescriptionBox("Chose an action!");

            } else {

                ToggleButton selected = (ToggleButton) newToggle;

                switch (selected.getId()) {
                    case "playCard" -> {this.playCard(); this.commandsToggleGroup.selectToggle(null);}
                    case "setCrewToRemove" -> {this.enableRegion(this.cabinsRegions); if (this.cabinsRegions.isEmpty()) initCommandDescriptionBox("There are no available\ncabins to remove crew from!");}
                    case "setItemsToBeRemoved" -> {this.enableRegion(this.storagesToEmptyRegions); if (this.storagesToEmptyRegions.isEmpty()) initCommandDescriptionBox("There are no available storages\n to remove items from!");}
                    case "setItemsToBeTaken" -> {this.enableRegion(this.storagesToFillRegions); if (this.storagesToFillRegions.isEmpty()) initCommandDescriptionBox("There are no available storages\n to place items in!");}
                    case "setTakeReward" -> {this.handleTakeReward();}
                    case "setChosenPlanetIndex" -> {this.handleChosenPlanetIndex();}
                    case "setWantsToVisit" -> {this.handleWantsToVisit();}
                    case "setShieldsToActivate" -> {this.enableRegion(this.shieldsRegions); if (this.shieldsRegions.isEmpty()) initCommandDescriptionBox("There are no available\nshields to activate!");}
                    case "setDoubleCannonsToActivate" -> {this.enableRegion(this.doubleCannonsRegions); if (this.doubleCannonsRegions.isEmpty()) initCommandDescriptionBox("There are no available\ndouble cannons to activate!");}
                    case "setDoubleEnginesToActivate" -> {this.enableRegion(this.doubleEnginesRegions); if (this.doubleEnginesRegions.isEmpty()) initCommandDescriptionBox("There are no available\ndouble engines to activate!");}
                    case "batteriesToBeStolen" -> {this.enableRegion(this.batteriesRegions); if (this.batteriesRegions.isEmpty()) initCommandDescriptionBox("There are no available\nbatteries give up!");}
                }
            }
        });
    }

    /**
     * Creates/updates the commandDescriptionBox
     */
    private void initCommandDescriptionBox(String text) {
        // Adding the description to the commandDescriptionBox
        this.commandDescriptionBox.getChildren().clear();
        Label commandDescriptionLabel = new Label();
        commandDescriptionLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        commandDescriptionLabel.setTextAlignment(TextAlignment.CENTER);

        commandDescriptionLabel.setText(text);
        this.commandDescriptionBox.getChildren().add(commandDescriptionLabel);
    }

    /**
     * Substitutes the buttons in the commandBox with the ones relative to the itemColors to remove
     */
    private void initRemoveColorCommands(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        ClientStorage storage = (ClientStorage) this.mainShip.getComponent(row, col);
        this.commandsGrid.getChildren().clear();
        this.initCommandDescriptionBox("Choose an itemColor to remove!");

        this.chosenItemColor = null;
        this.disableRegion(this.storagesToEmptyRegions);
        // Highlighting the selectedStorage
        this.storagesToEmptyRegions.get(guiUtils.keyFromCoords(row, col)).setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");

        Button goBackButton = this.createBackToCommandsButton();
        goBackButton.setOnAction(event -> {
            this.storagesToEmptyRegions.get(guiUtils.keyFromCoords(row, col)).setStyle("-fx-background-color: transparent;");
            this.initCommandBox();
        });
        this.commandsGrid.add(goBackButton, 0, 0);

        int commandCol = 1;
        for (ItemColor itemColor: storage.getStoredItems().stream().map(Item::getColor).distinct().toList()) {
            Button itemColorButton = this.createColorButton(itemColor);
            itemColorButton.setOnAction(event -> {
                this.chosenItemColor = itemColor;
                this.storagesToEmptyRegions.get(guiUtils.keyFromCoords(row, col)).setStyle("-fx-background-color: transparent;");
                this.handleItemToRemove(ofsRow, ofsCol);
            });
            this.commandsGrid.add(itemColorButton, commandCol, 0);
            commandCol++;
        }
    }

    /**
     * Substitutes the buttons in the commandBox with the ones relative to the itemColors to add
     */
    private void initAddColorCommands(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        ClientStorage storage = (ClientStorage) this.mainShip.getComponent(row, col);
        this.commandsGrid.getChildren().clear();
        List<ItemColor> cardItemColors = new ArrayList<>(this.currEventCard.getAvailableItemColors());
        if (!cardItemColors.isEmpty()) {
            if (!storage.isSpecialStorage() && cardItemColors.stream().allMatch(color -> color.equals(ItemColor.RED))) {
                this.initCommandDescriptionBox("There are only red items available\nand this storage is not\nsuitable to store them!");
            } else {
                this.initCommandDescriptionBox("Choose an itemColor to add!");
            }
        } else {
            this.initCommandDescriptionBox("There are no available\nitem colors in the card!");
        }


        this.chosenItemColor = null;
        this.disableRegion(this.storagesToFillRegions);
        // Highlighting the selectedStorage
        this.storagesToFillRegions.get(guiUtils.keyFromCoords(row, col)).setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");

        Button goBackButton = this.createBackToCommandsButton();
        goBackButton.setOnAction(event -> {
            this.storagesToFillRegions.get(guiUtils.keyFromCoords(row, col)).setStyle("-fx-background-color: transparent;");
            this.initCommandBox();
        });
        this.commandsGrid.add(goBackButton, 0, 0);

        if (this.clientModel.getResourceBank().getResources().values().stream().allMatch(res -> res == 0)) {
            cardItemColors.clear();
            this.initCommandDescriptionBox("There are no available\nitem colors in the resource bank!");
        }

        int commandCol = 1;
        for(ItemColor itemColor : cardItemColors) {
            if (!(itemColor.equals(ItemColor.RED) && !storage.isSpecialStorage())) {
                Button itemColorButton = this.createColorButton(itemColor);
                itemColorButton.setOnAction(event -> {
                    this.chosenItemColor = itemColor;
                    this.storagesToFillRegions.get(guiUtils.keyFromCoords(row, col)).setStyle("-fx-background-color: transparent;");
                    this.handleItemToTake(ofsRow, ofsCol);
                });
                this.commandsGrid.add(itemColorButton, commandCol, 0);
                commandCol++;
            }
        }
    }

    /**
     * @return a button the same color as the passed itemColor (function added in other method)
     */
    private Button createColorButton(ItemColor itemColor) {
        Button itemColorButton = new Button();

        String colorHex;
        String labelText;

        switch (itemColor) {
            case BLUE -> {
                colorHex = "#1E88E5";
                labelText = "Blue";
            }
            case RED -> {
                colorHex = "#E53935";
                labelText = "Red";
            }
            case GREEN -> {
                colorHex = "#43A047";
                labelText = "Green";
            }
            case YELLOW -> {
                colorHex = "#FBC02D";
                labelText = "Yellow";
            }
            default -> {
                colorHex = "#FFFFFF";
                labelText = "Black";
            }
        }

        itemColorButton.setText(labelText);

        itemColorButton.getStyleClass().add("button");
        itemColorButton.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white;");
        itemColorButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        itemColorButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        itemColorButton.setAlignment(Pos.CENTER);

        return itemColorButton;
    }

    /**
     * @return a goBack button (function added in other method)
     */
    private Button createBackToCommandsButton() {
        Button goBackButton = new Button();
        goBackButton.setText("Go Back");
        goBackButton.getStyleClass().add("goBack");
        goBackButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        goBackButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        goBackButton.setAlignment(Pos.CENTER);
        return goBackButton;
    }

    /**
     * Sets disabled(true) for all the regions in the given map
     */
    private void disableRegion(Map<String, Region> regionMap) {
        if(regionMap == null) return;
        for (Region region : regionMap.values()) {
            region.setDisable(true);
            region.setStyle("-fx-background-color: transparent;");
            this.currentRegions = null;
        }
    }

    /**
     * Sets disabled(false) for all the regions in the given map, after disabling the previous regionMap
     */
    private void enableRegion(Map<String, Region> regionMap) {
        if (this.currentRegions != null) {
            this.disableRegion(this.currentRegions);
        }
        for (Region region : regionMap.values()) {
            region.setDisable(false);
            region.setStyle("-fx-background-color: rgba(160, 212, 104, 0.5);");
        }
        this.currentRegions = regionMap;
    }

    /**
     * Handles the click on the board's image
     */
    @FXML
    private void handleViewGameBoard() {

        this.commandsToggleGroup.selectToggle(null);
        this.viewOtherShipsToggleGroup.selectToggle(null);
        this.initCommandDescriptionBox("You are currently viewing\nthe game board");

        // Disable the commands
        if (this.currEventCard.getPlayerNickname().equals(this.clientModel.getNickname())) {
            for (Toggle toggleButtonCommand : this.commandsToggleGroup.getToggles()) {
                ((ToggleButton) toggleButtonCommand).setDisable(true);
            }
        }

        // Disable all the previous containers
        this.setVisibility(this.shipImageView, false);
        this.setVisibility(this.shipGrid, false);

        // Enable the board container
        this.setVisibility(this.viewGameBoardContainer, true);
    }

    /**
     * Handles the click of the goBack button
     */
    @FXML
    private void handleGoBackToCardRoundButton(ActionEvent actionEvent) {

        // Enable the commands
        this.initCommandBox();

        this.setVisibility(this.viewGameBoardContainer, false);

        this.setVisibility(this.shipImageView, true);
        this.setVisibility(this.shipGrid, true);

        actionEvent.consume();
    }

    /**
     * Prints the current ActionJSON giving the current player
     * an overview of the changes he's staging for submission when
     * he'll play the current event card.
     */
    private void visualizePlayerActions() {
        VBox actionsContainer;
        VBox scrollPaneContent;
        Label label;

        this.playerActionsScrollPane.setContent(null);

        // Actions recap not necessary if it's not the player's turn
        if (!this.clientModel.getNickname().equals(this.currEventCard.getPlayerNickname())) return;

        scrollPaneContent = new VBox();
        scrollPaneContent.setAlignment(Pos.TOP_CENTER);

        actionsContainer = new VBox();
        actionsContainer.setAlignment(Pos.TOP_LEFT);

        // (1) - Visit the POI?
        try {
            Boolean wantsToVisit = this.currEventCard.getWantsToVisit();

            if (wantsToVisit != null) {
                label = new Label();
                label.setText("Visit the POI?: " + (wantsToVisit ? "Yes" : "No"));
                actionsContainer.getChildren().add(label);
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (2) - Take reward?
        try {
            Boolean takeReward = this.currEventCard.getTakeReward();
            if (takeReward != null && !this.currEventCard.isPlayerDefeated()) {
                label = new Label();
                label.setText("Take reward?: " + (takeReward ? "Yes" : "No"));
                actionsContainer.getChildren().add(label);
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (3) - Chosen planet index
        try {
            Integer chosenPlanetIndex = this.currEventCard.getChosenPlanetIndex();

            if (chosenPlanetIndex != null && chosenPlanetIndex != -1) {
                label = new Label();
                label.setText("Chosen planet: " + (chosenPlanetIndex + 1));
                actionsContainer.getChildren().add(label);
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (4) - Crew to remove
        try {
            List<ComponentHelper<LifeformType>> crewToRemove = this.currEventCard.getCrewToRemove();

            if (crewToRemove != null && !crewToRemove.isEmpty()) {
                label = new Label();
                label.setText("Crew to remove:");
                actionsContainer.getChildren().add(label);

                for (ComponentHelper<LifeformType> lfToRemove : crewToRemove) {
                    lfToRemove.getItem().ifPresent(
                        (LifeformType lfType) -> {
                            Label l = new Label();
                            l.setText(TAB + lfType + " @ (row=" + (lfToRemove.getI() + 1) + ", col=" + (lfToRemove.getJ() + 1) + ")");
                            actionsContainer.getChildren().add(l);
                        }
                    );
                }
            }
        }
        catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (5) - Items to remove
        try {
            List<ComponentHelper<ItemColor>> itemsToRemove = this.currEventCard.getItemsToBeRemoved();

            if (itemsToRemove != null && !itemsToRemove.isEmpty()) {
                label = new Label();
                label.setText("Items to remove:");
                actionsContainer.getChildren().add(label);

                for (ComponentHelper<ItemColor> itemToRemove : itemsToRemove) {
                    itemToRemove.getItem().ifPresent(
                        (ItemColor itemColor) -> {
                            Label l = new Label();
                            l.setText(TAB + itemColor + " @ (row=" + (itemToRemove.getI() + 1) + ", col=" + (itemToRemove.getJ() + 1) + ")");
                            actionsContainer.getChildren().add(l);
                        }
                    );
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (6) - Items to take
        try {
            List<ComponentHelper<ItemColor>> itemsToTake = this.currEventCard.getItemsToBeTaken();

            if (itemsToTake != null && !itemsToTake.isEmpty()) {
                label = new Label();
                label.setText("Items to take:");
                actionsContainer.getChildren().add(label);

                for (ComponentHelper<ItemColor> itemToTake : itemsToTake) {
                    itemToTake.getItem().ifPresent(
                        (ItemColor itemColor) -> {
                            Label l = new Label();
                            l.setText(TAB + itemColor + " @ (row=" + (itemToTake.getI() + 1) + ", col=" + (itemToTake.getJ() + 1) + ")");
                            actionsContainer.getChildren().add(l);
                        }
                    );
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (7) - Shields to activate
        try {
            List<Pair<CoordinatePair, CoordinatePair>> shieldsToActivate = this.currEventCard.getShieldsToActivate();

            if (shieldsToActivate != null && !shieldsToActivate.isEmpty()) {
                label = new Label();
                label.setText("Shields to activate:");
                actionsContainer.getChildren().add(label);

                for (Pair<CoordinatePair, CoordinatePair> shieldToActivate : shieldsToActivate) {
                    Label componentLabel = new Label();
                    Label batteryLabel = new Label();

                    componentLabel.setText(TAB + "Shield @ (row=" + (shieldToActivate.getKey().getI() + 1) + ", col=" + (shieldToActivate.getKey().getJ() + 1) + ")");
                    batteryLabel.setText(TAB + TAB + "Battery @ (row=" + (shieldToActivate.getValue().getJ() + 1) + ", col=" + (shieldToActivate.getValue().getJ() + 1) + ")");

                    actionsContainer.getChildren().add(componentLabel);
                    actionsContainer.getChildren().add(batteryLabel);
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (8) - Double cannons to activate
        try {
            List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivate = this.currEventCard.getDoubleCannonsToActivate();

            if (doubleCannonsToActivate != null && !doubleCannonsToActivate.isEmpty()) {
                label = new Label();
                label.setText("Double cannons to activate:");
                actionsContainer.getChildren().add(label);

                for (Pair<CoordinatePair, CoordinatePair> doubleCannonToActivate : doubleCannonsToActivate) {
                    Label componentLabel = new Label();
                    Label batteryLabel = new Label();

                    componentLabel.setText(TAB + "Cannon @ (row=" + (doubleCannonToActivate.getKey().getI() + 1) + ", col=" + (doubleCannonToActivate.getKey().getJ() + 1) + ")");
                    batteryLabel.setText(TAB + TAB + "Battery @ (row=" + (doubleCannonToActivate.getValue().getI() + 1) + ", col=" + (doubleCannonToActivate.getValue().getJ() + 1) + ")");

                    actionsContainer.getChildren().add(componentLabel);
                    actionsContainer.getChildren().add(batteryLabel);
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (9) - Double engines to activate
        try {
            List<Pair<CoordinatePair, CoordinatePair>> doubleEnginesToActivate = this.currEventCard.getDoubleEnginesToActivate();

            if (doubleEnginesToActivate != null && !doubleEnginesToActivate.isEmpty()) {
                label = new Label();
                label.setText("Double engines to activate:");
                actionsContainer.getChildren().add(label);

                for (Pair<CoordinatePair, CoordinatePair> doubleEngineToActivate : doubleEnginesToActivate) {
                    Label componentLabel = new Label();
                    Label batteryLabel = new Label();

                    componentLabel.setText(TAB + "Engine @ (row=" + (doubleEngineToActivate.getKey().getI() + 1) + ", col=" + (doubleEngineToActivate.getKey().getJ() + 1) + ")");
                    batteryLabel.setText(TAB + TAB + "Battery @ (row=" + (doubleEngineToActivate.getValue().getI() + 1) + ", col=" + (doubleEngineToActivate.getValue().getJ() + 1) + ")");

                    actionsContainer.getChildren().add(componentLabel);
                    actionsContainer.getChildren().add(batteryLabel);
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (10) - Batteries to be stolen
        try {
            List<CoordinatePair> batteriesToBeStolen = this.currEventCard.getBatteriesToBeStolen();
            if (batteriesToBeStolen != null && !batteriesToBeStolen.isEmpty()) {
                label = new Label();
                label.setText("Batteries to give up:");
                actionsContainer.getChildren().add(label);

                for (CoordinatePair batteryToBeStolen : batteriesToBeStolen) {
                    label = new Label();
                    label.setText(TAB + "Battery @ (row=" + (batteryToBeStolen.getI() + 1) + ", col=" + (batteryToBeStolen.getJ() + 1) + ")");
                    actionsContainer.getChildren().add(label);
                }
            }
        }
        catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        if (actionsContainer.getChildren().isEmpty()) {
            label = new Label();
            label.setText("No actions selected");
            actionsContainer.getChildren().add(label);
        }

        label = new Label();
        label.setText("Your Actions:");

        scrollPaneContent.getChildren().add(label);
        scrollPaneContent.getChildren().add(actionsContainer);

        this.playerActionsScrollPane.setContent(scrollPaneContent);
    }

    /**
     * Invokes the virtualView's playCard(...) method with the ActionJSON constructed by the player through GUI interaction
     * Also handles onError() (or onSuccess()) operations to revert local changes in the case of an error (or to prepare the client for the next interaction)
     */
    public void playCard() {

        ActionJSON response = this.currEventCard.useCard();

        // If the current card supports the action, it removes
        // any take/remove operations that target the same storage (only if taken and removed in 1 turn from one card)
        // and the same item color
        try {
            List<ComponentHelper<ItemColor>>[][] matrix = new List[ClientShip.grid_rows][ClientShip.grid_cols];

            List<ComponentHelper<ItemColor>> itemsToTake = this.currEventCard.getItemsToBeTaken();
            List<ComponentHelper<ItemColor>> itemsToRemove = this.currEventCard.getItemsToBeRemoved();

            List<ComponentHelper<ItemColor>> itemsToTakeFinal = new ArrayList<>();
            List<ComponentHelper<ItemColor>> itemsToRemoveFinal = new ArrayList<>();

            for (int i = 0; i < ClientShip.grid_rows; i++) {
                for (int j = 0; j < ClientShip.grid_cols; j++) {
                    matrix[i][j] = new ArrayList<>();
                }
            }

            for (ComponentHelper<ItemColor> toTake : itemsToTake) {
                if (toTake.getItem().isPresent()) {
                    matrix[toTake.getI()][toTake.getJ()].add(toTake);
                }
            }

            for (ComponentHelper<ItemColor> toRemove : itemsToRemove) {
                if (toRemove.getItem().isPresent()) {
                    if (matrix[toRemove.getI()][toRemove.getJ()].isEmpty()) {
                        itemsToRemoveFinal.add(toRemove);
                    }
                    else {
                        ItemColor colorToRemove = toRemove.getItem().get();
                        boolean colorFound = false;

                        for (ComponentHelper<ItemColor> ch : matrix[toRemove.getI()][toRemove.getJ()]) {
                            if (ch.getItem().isPresent()) {
                                if (ch.getItem().get().equals(colorToRemove)) {
                                    matrix[toRemove.getI()][toRemove.getJ()].remove(ch);
                                    colorFound = true;
                                    break;
                                }
                            }
                        }
                        if (!colorFound) {
                            itemsToRemoveFinal.add(toRemove);
                        }
                    }
                }
            }

            for (int i = 0; i < ClientShip.grid_rows; i++) {
                for (int j = 0; j < ClientShip.grid_cols; j++) {
                    if (matrix[i][j] != null) {
                        itemsToTakeFinal.addAll(matrix[i][j]);
                        matrix[i][j] = null;
                    }
                }
            }

            this.currEventCard.setItemsToBeTaken(itemsToTakeFinal);
            this.currEventCard.setItemsToBeRemoved(itemsToRemoveFinal);
        }
        catch (UnsupportedOperationException e) {
            // Don't check for duplicate items if the current
            // card doesn't enable the relative commands
        }

        GUIHandler.setCommandCTX(
            new CommandCTX(
                "playCard",
                () -> {
                    GUIHandler.setCommandCTX(null);
                    this.emptiedLifeforms.clear();
                    this.emptiedCabinsRegions.clear();
                    this.emptiedItemsMap.clear();
                    this.emptiedStoragesRegions.clear();
                    this.emptiedBatteriesRegions.clear();
                    this.emptiedBatteriesMap.clear();

                    List<ClientCannon> doubleCannons = this.mainShip.getDoubleCannons();
                    List<ClientEngine> doubleEngines = this.mainShip.getDoubleEngines();
                    List<ClientShield> shields = this.mainShip.getShieldList();

                    Platform.runLater(
                            () -> {
                                this.initRegionMap(this.doubleCannonsRegions, new ArrayList<>(doubleCannons), this::handleDoubleCannonToActivate);
                                this.initRegionMap(this.doubleEnginesRegions, new ArrayList<>(doubleEngines), this::handleDoubleEnginesToActivate);
                                this.initRegionMap(this.shieldsRegions, new ArrayList<>(shields), this::handleShieldsToActivate);

                                this.visualizePlayerActions();
                            }
                    );

                    this.currEventCard.clearJSON();
                },
                () -> {
                    Platform.runLater(
                            () -> {

                                // Revert the changes to the dropped lifeForms
                                if(this.availableCommands.contains("setCrewToRemove") && this.currEventCard.getCrewToRemove() != null && !this.currEventCard.getCrewToRemove().isEmpty()) {
                                    for(ComponentHelper<LifeformType> lfch : this.currEventCard.getCrewToRemove()) {
                                        int row = lfch.getI();
                                        int col = lfch.getJ();

                                        LifeformType lfType = lfch.getItem().orElse(null);
                                        if (lfType != null) {
                                            this.mainShip.addLifeformToCabin(row, col, lfType);
                                        }
                                    }
                                    // Visual revert
                                    guiUtils.revertVisuals(this.shipGrid, this.emptiedLifeforms, this.emptiedCabinsRegions, this.cabinsRegions, ClientCabin.class, guiUtils::initCabinLifeFormIcons);
                                }

                                if (this.availableCommands.contains("setItemsToBeRemoved") && this.currEventCard.getItemsToBeRemoved() != null && !this.currEventCard.getItemsToBeRemoved().isEmpty()) {
                                    // Revert the changes to the dropped resources
                                    for (ComponentHelper<ItemColor> icch : this.currEventCard.getItemsToBeRemoved()) {
                                        ItemColor ic = icch.getItem().orElse(null);
                                        if (ic != null) {
                                            ClientStorage storage = (ClientStorage) mainShip.getComponent(icch.getI(), icch.getJ());
                                            storage.storeItem(new Item(ic));
                                            this.clientModel.getResourceBank().removeResourceFromBank(ic);
                                        }
                                    }
                                    // Visual revert
                                    // Reverts the storagesToEmptyRegions/icons
                                    guiUtils.revertVisuals(this.shipGrid, this.emptiedItemsMap, this.emptiedStoragesRegions, this.storagesToEmptyRegions,ClientStorage.class, guiUtils::initStorageItemIcons);
                                    // Reverts the storagesToFillRegions (in the removeItem the storagesToFillRegions can only increase, so a simple check on the available capacity of the storages is enough, since we do not have to create new regions but only to remove some)
                                    for (ClientStorage storage : this.mainShip.getStorageList()) {
                                        if (storage.getStoredItems().size() == storage.getCapacity()) {
                                            // If the region already does not exist, nothing happens
                                            this.storagesToFillRegions.remove(guiUtils.keyFromCoords(storage.getI(), storage.getJ()));
                                        }
                                    }
                                }

                                if (this.availableCommands.contains("batteriesToBeStolen") && this.currEventCard.getBatteriesToBeStolen() != null && !this.currEventCard.getBatteriesToBeStolen().isEmpty()) {
                                    // Revert the changes to the batteries
                                    if (!this.currEventCard.getBatteriesToBeStolen().isEmpty()) {
                                        for (CoordinatePair bch : this.currEventCard.getBatteriesToBeStolen()) {
                                            ClientBattery battery = (ClientBattery) mainShip.getComponent(bch.getI(), bch.getJ());
                                            battery.setAvailability(battery.getAvailability() + 1);
                                        }
                                    }
                                    // Visual revert
                                    guiUtils.revertVisuals(this.shipGrid, this.emptiedBatteriesMap, this.emptiedBatteriesRegions, this.batteriesRegions, ClientBattery.class, guiUtils::initBatteryIcons);
                                }

                                this.availableCommands = new ArrayList<>(this.currEventCard.getAvailableCommands());
                                this.currEventCard.clearJSON();
                                this.initStatsBox();
                                this.initCommandBox();
                                this.visualizePlayerActions();
                                this.initResourceBankBox();

                                GUIHandler.setCommandCTX(null);
                            }
                    );
                }
            )
        );

        try {
            GUIHandler.getVirtualClient().playCard(this.clientModel.getNickname(), response);
        } catch (Exception e) {
            Platform.runLater(
                    () -> {
                        this.showToast(
                                "There was an error while playing the card!!!",
                                ToastType.ERROR
                        );
                    }
            );
        }
    }

    // Methods to select the components to execute a command on (+ Visual Updates)
    // ofsRow, ofsCol relative to the gridPane

    /**
     * Updates the icons of the clicked cabin, disabling the region in case it is emptied
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleCrewToRemove(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        ClientCabin selectedCabin = (ClientCabin) this.mainShip.getComponent(row, col);
        LifeformType selectedLifeForm = selectedCabin.getInhabitants().getFirst().getLifeformType();
        this.addCrewToRemove(row, col, selectedLifeForm);

        // Updating the HBox containing the icons
        FlowPane boxToUpdate = this.lifeFormsMap.get(this.clientModel.getNickname()).get(guiUtils.keyFromCoords(row, col));
        guiUtils.initCabinLifeFormIcons(selectedCabin, boxToUpdate);


        // Populating the emptiedRegions/emptiedMaps with the cabin's data, in case we need to access it revert this changes


        // We add the boxToUpdate to the map (for revert purposes), but only if it's not already in it
        if (!this.emptiedLifeforms.containsValue(boxToUpdate)) {
            this.emptiedLifeforms.put(guiUtils.keyFromCoords(row, col), boxToUpdate);
        }

        // If the cabin has no more inhabitants, we remove the region
        if (selectedCabin.getInhabitants().isEmpty()) {
            Region regionToRemove = this.cabinsRegions.get(guiUtils.keyFromCoords(row, col));
            regionToRemove.setDisable(true);
            regionToRemove.setStyle("-fx-background-color: transparent;");

            this.emptiedCabinsRegions.put(guiUtils.keyFromCoords(row, col), regionToRemove);

            this.shipGrid.getChildren().remove(regionToRemove);
            this.lifeFormsMap.remove(guiUtils.keyFromCoords(row, col));
            this.cabinsRegions.remove(this.guiUtils.keyFromCoords(row, col));
        }

        this.initStatsBox();
        this.commandsToggleGroup.selectToggle(null);
    }

    /**
     * Updates the icons of the clicked storage, disabling the region in case it is emptied
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleItemToRemove(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        ClientStorage selectedStorage = (ClientStorage) this.mainShip.getComponent(row, col);
        this.addItemToRemove(row, col, this.chosenItemColor);

        // Updating the HBox containing the icons
        FlowPane boxToUpdate = this.itemsMap.get(this.clientModel.getNickname()).get(guiUtils.keyFromCoords(row, col));
        guiUtils.initStorageItemIcons(selectedStorage, boxToUpdate);


        // Populating the emptiedRegions/emptiedMaps with the storage's data, in case we need to access it revert this changes


        // We add the component to the storagesToFillRegions (only if the region is not present)
        if(this.storagesToFillRegions.get(guiUtils.keyFromCoords(row, col)) == null) {
//            System.out.println(PrintUtils.addColor("AGGIUNTO STORAGE DA RIEMPIRE ALLE REGIONI", ANSIColors.MAGENTA));
            Region newRegion = guiUtils.generateDisabledRegion();
            newRegion.setOnMouseClicked(e -> this.initAddColorCommands(ofsRow, ofsCol));
            this.storagesToFillRegions.put(guiUtils.keyFromCoords(row, col), newRegion);
            this.shipGrid.add(newRegion, ofsCol, ofsRow);
        }

        // We add the boxToUpdate to the map (for revert purposes), but only if it's not already in it
        if (!this.emptiedItemsMap.containsValue(boxToUpdate)) {
            this.emptiedItemsMap.put(guiUtils.keyFromCoords(row, col), boxToUpdate);
        }

        // If the storage is empty, we remove it from the storagesToEmptyRegions
        if (selectedStorage.getStoredItems().isEmpty()) {
            Region regionToRemove = this.storagesToEmptyRegions.get(guiUtils.keyFromCoords(row, col));
            regionToRemove.setDisable(true);
            regionToRemove.setStyle("-fx-background-color: transparent;");

            this.emptiedStoragesRegions.put(guiUtils.keyFromCoords(row, col), this.storagesToEmptyRegions.get(guiUtils.keyFromCoords(row, col)));
            this.storagesToEmptyRegions.remove(guiUtils.keyFromCoords(row, col));
            this.shipGrid.getChildren().remove(regionToRemove);
        }

        this.initStatsBox();
        this.initAdditionalInfoBox();
        this.initResourceBankBox();
        this.initCommandBox();
    }

    /**
     * Updates the icons of the clicked storage, disabling the region in case it is filled
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleItemToTake(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        ClientStorage selectedStorage = (ClientStorage) this.mainShip.getComponent(row, col);
        this.addItemToTake(row, col, this.chosenItemColor);

        // Updating the HBox containing the icons
        FlowPane boxToUpdate = this.itemsMap.get(this.clientModel.getNickname()).get(guiUtils.keyFromCoords(row, col));
        guiUtils.initStorageItemIcons(selectedStorage, boxToUpdate);

        // We add the component to the storagesToEmptyRegions (only if the region is not present)
        if(this.storagesToEmptyRegions.get(guiUtils.keyFromCoords(row, col)) == null) {
//            System.out.println(PrintUtils.addColor("AGGIUNTO STORAGE DA SVUOTARE ALLE REGIONI", ANSIColors.CYAN));
            Region newRegion = guiUtils.generateDisabledRegion();
            newRegion.setOnMouseClicked(e -> this.initRemoveColorCommands(ofsRow, ofsCol));
            this.storagesToEmptyRegions.put(guiUtils.keyFromCoords(row, col), newRegion);
            this.shipGrid.add(newRegion, ofsCol, ofsRow);
        }

        // If the storage is full, we remove it from the storagesToFillRegions
        if (selectedStorage.getStoredItems().size() == selectedStorage.getCapacity()) {
//            System.out.println(PrintUtils.addColor("RIMOSSO STORAGE DA RIEMPIRE DALLE REGIONI", ANSIColors.CYAN));
            this.shipGrid.getChildren().remove(this.storagesToFillRegions.get(guiUtils.keyFromCoords(row, col)));
            this.storagesToFillRegions.remove(guiUtils.keyFromCoords(row, col));
        }

        this.initStatsBox();
        this.initAdditionalInfoBox();
        this.initResourceBankBox();
        this.initCommandBox();
    }

    /**
     * Sets the current eventCard's JSON field relative to the player's decision on taking the reward to true, subsequently disabling the command (and enabling others if needed)
     */
    private void handleTakeReward() {
        this.addTakeReward(true);

        this.availableCommands.remove("setTakeReward");

        if (this.currEventCard.getClass().equals(ClientSmugglers.class)) {

            this.availableCommands.add("setItemsToBeTaken");
            this.availableCommands.add("setItemsToBeRemoved");

        }
        this.initCommandBox();
    }

    /**
     * Substitutes the commandBox commands with a set of buttons used to choose the planet to land on
     */
    private void handleChosenPlanetIndex() {
        Button planetButton;

        this.commandsGrid.getChildren().clear();

        List<Integer> availablePlanetIndexes =
                ((ClientVisitPlanets) this.currEventCard)
                        .getAvailablePlanets()
                        .keySet().stream().toList();

        int col = 0;
        for (Integer index : availablePlanetIndexes) {
            planetButton = new Button();
            planetButton.getStyleClass().add("button");
            planetButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            planetButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            planetButton.setAlignment(Pos.CENTER);
            Label planetButtonLabel = new Label((index + 1) + "");
            planetButtonLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
            planetButton.setGraphic(planetButtonLabel);
            planetButton.setOnAction(event -> {
                this.addChosenPlanetIndex(index);
                this.availableCommands.remove("setChosenPlanetIndex");
                this.availableCommands.add("setItemsToBeRemoved");
                this.availableCommands.add("setItemsToBeTaken");
                this.initAdditionalInfoBox();
                this.initCommandBox();
            });

            this.commandsGrid.add(planetButton, col, 0);
            col++;
        }


    }

    /**
     * Sets the current eventCard's JSON field relative to the player's decision on visiting the POI the to true, subsequently disabling the command (and enabling others if needed)
     */
    private void handleWantsToVisit() {
        this.addWantsToVisit(true);

        this.availableCommands.remove("setWantsToVisit");

        if (this.currEventCard.getClass().equals(ClientAbandonedShip.class)) {

            this.availableCommands.add("setCrewToRemove");

        } else if (this.currEventCard.getClass().equals(ClientAbandonedStation.class)) {

            this.availableCommands.add("setItemsToBeTaken");
            this.availableCommands.add("setItemsToBeRemoved");
        }

        this.initCommandBox();
    }

    /**
     * Highlights the clicked region a different color and enables the region relative to the batteries to chose from (needed to power the selected shield)
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleShieldsToActivate(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        // Coords of the doubleEngine to activate (relative to the ship)
        this.currEnergyConsumer = new Pair<>(EnergyConsumers.SHIELD, new CoordinatePair(row, col));
        this.handleEnergyConsumers(ofsRow, ofsCol);
    }

    /**
     * Highlights the clicked region a different color and enables the region relative to the batteries to chose from (needed to power the selected doubleCannon)
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleDoubleCannonToActivate(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        // Coords of the doubleEngine to activate (relative to the ship)
        this.currEnergyConsumer = new Pair<>(EnergyConsumers.CANNON, new CoordinatePair(row, col));
        this.handleEnergyConsumers(ofsRow, ofsCol);
    }

    /**
     * Highlights the clicked region a different color and enables the region relative to the batteries to chose from (needed to power the selected doubleEngine)
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleDoubleEnginesToActivate(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        // Coords of the doubleEngine to activate (relative to the ship)
        this.currEnergyConsumer = new Pair<>(EnergyConsumers.ENGINE, new CoordinatePair(row, col));
        this.handleEnergyConsumers(ofsRow, ofsCol);

    }

    /**
     * Updates the icons of the clicked battery, disabling the region in case it is emptied
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleBatteriesToBeStolen(int ofsRow, int ofsCol) {
        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        ClientBattery selectedBattery = (ClientBattery) this.mainShip.getComponent(row, col);
        this.addBatteryToBeStolen(new CoordinatePair(row, col));

        // Updating the HBox containing the icons
        FlowPane boxToUpdate = this.batteriesMap.get(this.clientModel.getNickname()).get(guiUtils.keyFromCoords(row, col));
        guiUtils.initBatteryIcons(selectedBattery, boxToUpdate);


        // Populating the emptiedRegions/emptiedMaps with the battery's data, in case we need to access it revert this changes


        // We add the boxToUpdate to the map (for revert purposes), but only if it's not already in it
        if (!this.emptiedBatteriesMap.containsValue(boxToUpdate)) {
            this.emptiedBatteriesMap.put(guiUtils.keyFromCoords(row, col), boxToUpdate);
        }

        // If the battery has no more charges, we remove the region
        if (selectedBattery.getAvailability() <= 0) {
            Region regionToRemove = this.batteriesRegions.get(guiUtils.keyFromCoords(row, col));
            regionToRemove.setDisable(true);
            regionToRemove.setStyle("-fx-background-color: transparent;");

            this.emptiedBatteriesRegions.put(guiUtils.keyFromCoords(row, col), this.batteriesRegions.get(guiUtils.keyFromCoords(row, col)));

            this.shipGrid.getChildren().remove(regionToRemove);
            this.batteriesRegions.remove(this.guiUtils.keyFromCoords(row, col));
        }

        this.initStatsBox();
        this.commandsToggleGroup.selectToggle(null);
    }

    /**
     * Updates the icons of the clicked battery, disabling the region in case it is emptied
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleMandatoryBatteryCoords(int ofsRow, int ofsCol) {
        int batteryRow = ofsRow + this.shipOffsets.getKey();
        int batteryCol = ofsCol + this.shipOffsets.getValue();

        int energyConsumerRow = this.currEnergyConsumer.getValue().getI();
        int energyConsumerCol = this.currEnergyConsumer.getValue().getJ();

        CoordinatePair batteryCoords = new CoordinatePair(batteryRow, batteryCol);

        Region energyConsumerRegion = null;

        // Updating the visuals
        switch (this.currEnergyConsumer.getKey()) {
            case CANNON -> {
                energyConsumerRegion = this.doubleCannonsRegions.get(guiUtils.keyFromCoords(energyConsumerRow, energyConsumerCol));
                this.doubleCannonsRegions.remove(guiUtils.keyFromCoords(energyConsumerRow, energyConsumerCol));
                this.addDoubleCannonToActivate(this.currEnergyConsumer.getValue(), batteryCoords);
                try { // Necessary to check if the firepower threshold has been reached if a double cannon has been activated
                    if (mainShip.getFirePower(this.currEventCard.getDoubleCannonsToActivate().stream().map(Pair::getKey).toList()) > this.currEventCard.getFirepower()) {
                        this.availableCommands.add("setTakeReward");
                    }
                } catch (UnsupportedOperationException e) {
                    // Do nothing
                }
            }
            case ENGINE -> {
                energyConsumerRegion = this.doubleEnginesRegions.get(guiUtils.keyFromCoords(energyConsumerRow, energyConsumerCol));
                this.doubleEnginesRegions.remove(guiUtils.keyFromCoords(energyConsumerRow, energyConsumerCol));
                this.addDoubleEngineToActivate(this.currEnergyConsumer.getValue(), batteryCoords);
            }
            case SHIELD -> {
                energyConsumerRegion = this.shieldsRegions.get(guiUtils.keyFromCoords(energyConsumerRow, energyConsumerCol));
                this.shieldsRegions.remove(guiUtils.keyFromCoords(energyConsumerRow, energyConsumerCol));
                this.addShieldToActivate(this.currEnergyConsumer.getValue(), batteryCoords);
            }
        }

        this.shipGrid.getChildren().remove(energyConsumerRegion);

        this.initStatsBox();

        // Updating the HBox containing the icons
        ClientBattery batteryToUpdate = (ClientBattery) this.mainShip.getComponent(batteryRow, batteryCol);
        FlowPane boxToUpdate = this.batteriesMap.get(this.clientModel.getNickname()).get(guiUtils.keyFromCoords(batteryRow, batteryCol));
        guiUtils.initBatteryIcons(batteryToUpdate, boxToUpdate);

        // If the battery has no more charges, we remove the region
        if (batteryToUpdate.getAvailability() == 0) {
            this.shipGrid.getChildren().remove(this.batteriesRegions.get(guiUtils.keyFromCoords(batteryRow, batteryCol)));
//            this.shipGrid.getChildren().remove(boxToUpdate);

            this.batteriesMap.remove(guiUtils.keyFromCoords(batteryRow, batteryCol));
            this.batteriesRegions.remove(this.guiUtils.keyFromCoords(batteryRow, batteryCol));
        }


        this.initStatsBox();
        this.currEnergyConsumer = null;
        this.commandsToggleGroup.selectToggle(null);
        this.initStatsBox();
        this.initCommandBox();
    }

    /**
     * Disables the selected component's region and changes the color to highlight the selection
     * It then enables the battery regions
     * @param ofsRow row of the component (with offset)
     * @param ofsCol col of the component (with offset)
     */
    private void handleEnergyConsumers(int ofsRow, int ofsCol) {

        int row = ofsRow + this.shipOffsets.getKey();
        int col = ofsCol + this.shipOffsets.getValue();

        // Get the current region, then enable the battery regions
        Region selectedRegion = this.currentRegions.get(guiUtils.keyFromCoords(row, col));
        this.enableRegion(this.batteriesRegions);
        if (this.batteriesRegions.isEmpty()) {
            this.initCommandDescriptionBox("There are no available\nbatteries consume!");
        }
        // Coloring the region red to highlight the selectedRegion
        selectedRegion.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
    }

    // Methods to modify the JSON (+ Local Updates)
    // row, col relative to the shipGrid

    /**
     * Adds the LifeForm's type to remove to the current eventCard's JSON (along with the coordinates of the corresponding cabin)
     * @param row row of the component
     * @param col col of the component
     * @param lifeformType type of the lifeForm to remove (Astronaut, Purple or Brown Alien)
     */
    private void addCrewToRemove(int row, int col, LifeformType lifeformType) {
        ComponentHelper<LifeformType> componentHelper;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [getCrewToRemove()] ClientShip is null", ToastType.ERROR);
            return;
        }

        // Local update
        try {
            ship.removeLifeformFromCabin(row, col, lifeformType);

            componentHelper = new ComponentHelper<>(row, col);
            componentHelper.addItem(lifeformType);

            this.currEventCard.getCrewToRemove().add(componentHelper);
            this.visualizePlayerActions();

            this.showToast(
                    "Successfully removed " + lifeformType + " item from Cabin @ (row=" + row + ", col=" + col + ")",
                    ToastType.SUCCESS
            );
        }
        catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    /**
     * Adds the itemColor to remove to the current eventCard's JSON (along with the coordinates of the corresponding storage)
     * @param row row of the component
     * @param col col of the component
     * @param itemColor color of the item to remove (Red, Yellow, Green, Blue)
     */
    private void addItemToRemove(int row, int col, ItemColor itemColor) {
        ComponentHelper<ItemColor> componentHelper;
        ClientComponent component;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [getCrewToRemove()] ClientShip is null", ToastType.ERROR);
            return;
        }

        try {
            component = ship.getComponent(row, col);
        }
        catch (OutOfGridException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
            return;
        }

        switch (component) {
            case ClientStorage storage -> {
                Optional<Item> foundItem = storage.getStoredItems().stream()
                        .filter(i -> i.getColor().equals(itemColor))
                        .findFirst();

                // Local update
                if (foundItem.isPresent()) {
                    try {
                        storage.removeItem(foundItem.get());
                        this.clientModel.getResourceBank().addResourceToBank(itemColor);

                        componentHelper = new ComponentHelper<>(row, col);
                        componentHelper.addItem(itemColor);

                        this.currEventCard.getItemsToBeRemoved().add(componentHelper);
                        this.visualizePlayerActions();

                        this.showToast(
                                "Successfully removed " + itemColor + " item in Storage @ (row=" + row + ", col=" + col + ")",
                                ToastType.SUCCESS
                        );
                    }
                    catch (UnsupportedOperationException e) {
                        this.showToast(e.getMessage(), ToastType.ERROR);
                    }
                }
                else {
                    this.showToast(
                            "Couldn't find " + itemColor + " item in Storage @ (row=" + row + ", col=" + col + ")",
                            ToastType.ERROR
                    );
                }
            }
            case null, default -> {
                this.showToast(
                    "[ERROR] Component @ (row=" + row + ", col=" + col + ") is not a storage",
                    ToastType.ERROR
                );
            }
        }
    }

    /**
     * Adds the itemColor to take to the current eventCard's JSON (along with the coordinates of the corresponding storage)
     * @param row row of the component
     * @param col col of the component
     * @param itemColor color of the item to remove (Red, Yellow, Green, Blue)
     */
    private void addItemToTake(int row, int col, ItemColor itemColor) {
        ComponentHelper<ItemColor> componentHelper;
        ClientComponent component;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [getCrewToRemove()] ClientShip is null", ToastType.ERROR);
            return;
        }

        try {
            component = ship.getComponent(row, col);
        }
        catch (OutOfGridException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
            return;
        }

        switch (component) {
            case ClientStorage storage -> {
                if (storage.availableSpace() > 0) {
                    try {
                        componentHelper = new ComponentHelper<>(row, col);
                        componentHelper.addItem(itemColor);

                        storage.storeItem(new Item(itemColor));
                        this.clientModel.getResourceBank().removeResourceFromBank(itemColor);

                        this.currEventCard.getItemsToBeTaken().add(componentHelper);
                        this.currEventCard.removeItem(itemColor);
                        this.visualizePlayerActions();

                        this.showToast(
                                "Successfully added " + itemColor + " item in Storage @ (row=" + row + ", col=" + col + ")",
                                ToastType.SUCCESS
                        );
                    }
                    catch (UnsupportedOperationException e) {
                        this.showToast(e.getMessage(), ToastType.ERROR);
                    }
                }
                else {
                    this.showToast(
                            "[ERROR] Storage @ (row=" + row + ", col=" + col + ") is full",
                            ToastType.ERROR
                    );
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + row + ", col=" + col + ") is not a storage",
                        ToastType.ERROR
                );
            }
        }
    }

    /**
     * @param choice Value that will be set in the JSON's field relative to the player's decision on taking the card's reward
     */
    private void addTakeReward(boolean choice) {
        try {
            this.currEventCard.setTakeReward(choice);
            this.visualizePlayerActions();
        }
        catch (UnsupportedOperationException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    /**
     * @param chosenPlanetIndex Value that will be set in the JSON's field relative to the player's decision on which planet to visit
     */
    private void addChosenPlanetIndex(int chosenPlanetIndex) {
        try {
            this.currEventCard.setChosenPlanetIndex(chosenPlanetIndex);
            this.visualizePlayerActions();
        }
        catch (UnsupportedOperationException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    /**
     * @param choice Value that will be set in the JSON's field relative to the player's decision on visiting the POI
     */
    private void addWantsToVisit(boolean choice) {
        try {
            this.currEventCard.setWantsToVisit(choice);
            this.visualizePlayerActions();
        }
        catch (UnsupportedOperationException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    /**
     * Adds to the JSON's field the pair of coordinates relative to the shield to activate, and the battery to power it
     * @param shieldToActivate Coordinates of the shield to activate
     * @param batteryToConsume Coordinates of the battery to consume
     */
    private void addShieldToActivate(CoordinatePair shieldToActivate, CoordinatePair batteryToConsume) {
        ClientComponent possibleShield, possibleBattery;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [addShieldToActivate()] ClientShip is null", ToastType.ERROR);
            return;
        }

        try {
            possibleShield = ship.getComponent(
                    shieldToActivate.getI(),
                    shieldToActivate.getJ()
            );

            possibleBattery = ship.getComponent(
                    batteryToConsume.getI(),
                    batteryToConsume.getJ()
            );
        }
        catch (OutOfGridException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
            return;
        }

        switch (possibleShield) {
            case ClientShield shield -> {}
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + shieldToActivate.getI() + ", col=" + shieldToActivate.getJ() + ") is not a shield",
                        ToastType.ERROR
                );
                return;
            }
        }

        switch (possibleBattery) {
            case ClientBattery battery -> {
                if (battery.getAvailability() <= 0) {
                    this.showToast(
                            "[ERROR] Given battery is depleted",
                            ToastType.ERROR
                    );
                    return;
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + batteryToConsume.getI() + ", col=" + batteryToConsume.getJ() + ") is not a battery",
                        ToastType.ERROR
                );
                return;
            }
        }

        this.currEventCard.getShieldsToActivate().add(
                new Pair<>(shieldToActivate, batteryToConsume)
        );

        this.visualizePlayerActions();

        ship.consumeEnergy(List.of(batteryToConsume));
    }

    /**
     * Adds to the JSON's field  the pair of coordinates relative to the doubleCannon to activate, and the battery to power it
     * @param doubleCannonToActivate Coordinates of the doubleCannon to activate
     * @param batteryToConsume Coordinates of the battery to consume
     */
    private void addDoubleCannonToActivate(CoordinatePair doubleCannonToActivate, CoordinatePair batteryToConsume) {
        ClientComponent possibleDoubleCannon, possibleBattery;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [addDoubleCannonToActivate()] ClientShip is null", ToastType.ERROR);
            return;
        }

        try {
            possibleDoubleCannon = ship.getComponent(
                    doubleCannonToActivate.getI(),
                    doubleCannonToActivate.getJ()
            );

            possibleBattery = ship.getComponent(
                    batteryToConsume.getI(),
                    batteryToConsume.getJ()
            );
        }
        catch (OutOfGridException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
            return;
        }

        switch (possibleDoubleCannon) {
            case ClientCannon cannon -> {
                if (!cannon.requiresEnergy()) {
                    this.showToast(
                            "[ERROR] Cannon @ (row=" + possibleDoubleCannon.getI() + ", col=" + possibleDoubleCannon.getJ() + ") is a single cannon",
                            ToastType.ERROR
                    );
                    return;
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + doubleCannonToActivate.getI() + ", col=" + doubleCannonToActivate.getJ() + ") is not a cannon",
                        ToastType.ERROR
                );
                return;
            }
        }

        switch (possibleBattery) {
            case ClientBattery battery -> {
                if (battery.getAvailability() <= 0) {
                    this.showToast(
                            "[ERROR] Given battery is depleted",
                            ToastType.ERROR
                    );
                    return;
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + batteryToConsume.getI() + ", col=" + batteryToConsume.getJ() + ") is not a battery",
                        ToastType.ERROR
                );
                return;
            }
        }

        this.currEventCard.getDoubleCannonsToActivate().add(
                new Pair<>(doubleCannonToActivate, batteryToConsume)
        );

        this.visualizePlayerActions();

        ship.consumeEnergy(List.of(batteryToConsume));
    }

    /**
     * Adds to the JSON's field  the pair of coordinates relative to the doubleEngine to activate, and the battery to power it
     * @param doubleEngineToActivate Coordinates of the doubleCannon to activate
     * @param batteryToConsume Coordinates of the battery to consume
     */
    private void addDoubleEngineToActivate(CoordinatePair doubleEngineToActivate, CoordinatePair batteryToConsume) {
        ClientComponent possibleDoubleEngine, possibleBattery;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [getCrewToRemove()] ClientShip is null", ToastType.ERROR);
            return;
        }

        try {
            possibleDoubleEngine = ship.getComponent(
                    doubleEngineToActivate.getI(),
                    doubleEngineToActivate.getJ()
            );

            possibleBattery = ship.getComponent(
                    batteryToConsume.getI(),
                    batteryToConsume.getJ()
            );
        }
        catch (OutOfGridException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
            return;
        }

        switch (possibleDoubleEngine) {
            case ClientEngine engine -> {
                if (!engine.requiresEnergy()) {
                    this.showToast(
                            "[ERROR] Engine @ (row=" + possibleDoubleEngine.getI() + ", col=" + possibleDoubleEngine.getJ() + ") is a single engine",
                            ToastType.ERROR
                    );
                    return;
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + doubleEngineToActivate.getI() + ", col=" + doubleEngineToActivate.getJ() + ") is not an engine",
                        ToastType.ERROR
                );
                return;
            }
        }

        switch (possibleBattery) {
            case ClientBattery battery -> {
                if (battery.getAvailability() <= 0) {
                    this.showToast(
                            "[ERROR] Given battery is depleted",
                            ToastType.ERROR
                    );
                    return;
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + batteryToConsume.getI() + ", col=" + batteryToConsume.getJ() + ") is not a battery",
                        ToastType.ERROR
                );
                return;
            }
        }

        this.currEventCard.getDoubleEnginesToActivate().add(
                new Pair<>(doubleEngineToActivate, batteryToConsume)
        );

        this.visualizePlayerActions();

        ship.consumeEnergy(List.of(batteryToConsume));
    }

    /**
     * Adds to the JSON's field the coordinates relative to the battery to be stolen
     * @param batteryToBeStolen Coordinates of the battery to consume
     */
    private void addBatteryToBeStolen(CoordinatePair batteryToBeStolen) {
        ClientComponent possibleBattery;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [addBatteryToBeStolen()] ClientShip is null", ToastType.ERROR);
            return;
        }

        try {
            possibleBattery = ship.getComponent(
                    batteryToBeStolen.getI(),
                    batteryToBeStolen.getJ()
            );
        }
        catch (OutOfGridException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
            return;
        }

        switch (possibleBattery) {
            case ClientBattery battery -> {
                if (battery.getAvailability() <= 0) {
                    this.showToast(
                            "[ERROR] Given battery is depleted",
                            ToastType.ERROR
                    );
                    return;
                }
            }
            case null, default -> {
                this.showToast(
                        "[ERROR] Component @ (row=" + batteryToBeStolen.getI() + ", col=" + batteryToBeStolen.getJ() + ") is not a battery",
                        ToastType.ERROR
                );
                return;
            }
        }

        this.currEventCard.getBatteriesToBeStolen().add(batteryToBeStolen);

        this.visualizePlayerActions();

        ship.consumeEnergy(List.of(batteryToBeStolen));
    }

    /**
     * Updates all the GUI elements
     * @param cardStateJSON - data used to update the GUI
     */
    public void updateCardRound(CardStateJSON cardStateJSON) {

        Platform.runLater(() -> {

            this.updateVisuals(cardStateJSON);

            this.setCurrentEventCard(cardStateJSON);

            this.initTurnBox();

            this.initStatsBox();

            this.initResourceBankBox();

            this.visualizePlayerActions();

            this.initCommandBox();

            this.initAdditionalInfoBox();

            if (this.currEventCard.getPlayerNickname().equals(this.clientModel.getNickname())) {
                if (this.viewGameBoardContainer.isVisible()) {
                    for (Toggle toggleButtonCommand : this.commandsToggleGroup.getToggles()) {
                        ((ToggleButton) toggleButtonCommand).setDisable(true);
                    }
                    this.initCommandDescriptionBox("You are currently viewing\nthe game board");
                } else if (this.viewOtherShipsToggleGroup.getSelectedToggle() != null) {
                    for (Toggle toggleButtonCommand : this.commandsToggleGroup.getToggles()) {
                        ((ToggleButton) toggleButtonCommand).setDisable(true);
                    }
                    this.initCommandDescriptionBox("You are currently viewing\n" + ((ToggleButton) this.viewOtherShipsToggleGroup.getSelectedToggle()).getText() + "'s ship");
                }
            }


        });
    }

    /**
     * Updates the GUI visuals relative to the ships
     * @param cardStateJSON data to used to update the visuals
     */
    public void updateVisuals(CardStateJSON cardStateJSON) {

        Platform.runLater(() -> {

            // Updating the lifeForms icons
            if (cardStateJSON.getNeedsUpdatedRemovedLifeforms()) {
                for (String playerNickname : cardStateJSON.getRemovedLifeforms().keySet()) {
                    if (!(this.clientModel.getNickname() != null && this.clientModel.getNickname().equals(cardStateJSON.getPrevPlayerNickname()) && cardStateJSON.getSkipCrewUpdate())) {
                        this.clientModel.getShipOfPlayer(playerNickname).ifPresent(
                                (ship) -> {
                                    for (ClientCabin cabin : ship.getCabinList()) {
                                        guiUtils.initCabinLifeFormIcons(cabin, this.lifeFormsMap.get(playerNickname).get(guiUtils.keyFromCoords(cabin.getI(), cabin.getJ())));
                                    }
                                }
                        );
                    }
                }
            }

            // Updating the storages icons
            if (cardStateJSON.getNeedsUpdatedDroppedResources() || cardStateJSON.getNeedsUpdatedTakenResources()) {
                Map<String, List<ComponentHelper<ItemColor>>> droppedResources = cardStateJSON.getDroppedResources();
                Map<String, List<ComponentHelper<ItemColor>>> takenResources = cardStateJSON.getTakenResources();
                for (String playerNickname : Stream.concat(droppedResources != null ? droppedResources.keySet().stream() : Stream.empty(), takenResources != null ? takenResources.keySet().stream() : Stream.empty()).distinct().toList()) {
                    if (!(this.clientModel.getNickname() != null && this.clientModel.getNickname().equals(cardStateJSON.getPrevPlayerNickname()) && cardStateJSON.getSkipStoragesUpdate())) {
                        this.clientModel.getShipOfPlayer(playerNickname).ifPresent(
                                (ship) -> {
                                    for (ClientStorage storage : ship.getStorageList()) {
                                        guiUtils.initStorageItemIcons(storage, this.itemsMap.get(playerNickname).get(guiUtils.keyFromCoords(storage.getI(), storage.getJ())));
                                    }
                                }
                        );
                    }
                }
            }

            // Updating the batteries icons
            if (cardStateJSON.getNeedsUpdatedBatteries()) {
                for (String playerNickname : cardStateJSON.getRemovedBatteries().keySet()) {
                    if (!(this.clientModel.getNickname() != null && this.clientModel.getNickname().equals(cardStateJSON.getPrevPlayerNickname()) && cardStateJSON.getSkipBatteriesUpdate())) {
                        this.clientModel.getShipOfPlayer(playerNickname).ifPresent(
                                (ship) -> {
                                    for (ClientBattery battery : ship.getBatteryList()) {
                                        guiUtils.initBatteryIcons(battery, this.batteriesMap.get(playerNickname).get(guiUtils.keyFromCoords(battery.getI(), battery.getJ())));
                                    }
                                }
                        );
                    }
                }
            }

            // Removing the destroyed components from the grid
            if (cardStateJSON.getNeedsUpdatedRemovedComponents()) {
                for (String playerNickname : cardStateJSON.getRemovedComponents().keySet()) {
                    for (Map<String, Object> componentToRemove : cardStateJSON.getRemovedComponents().get(playerNickname)) {

                        // Getting the player's ship
                        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
                        if (ship == null) {
                            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
                            return;
                        }

                        GridPane playerShipGrid = this.playersShipGridPane.get(playerNickname);

                        int row = (int) componentToRemove.get("row");
                        int col = (int) componentToRemove.get("col");

                        int ofsRow = row - this.shipOffsets.getKey();
                        int ofsCol = col - this.shipOffsets.getValue();

                        playerShipGrid.getChildren().removeIf(cell ->
                                GridPane.getRowIndex(cell) == ofsRow &&
                                        GridPane.getColumnIndex(cell) == ofsCol
                        );

                        // Removing the components from the componentMaps
                        this.batteriesMap.get(playerNickname).remove(guiUtils.keyFromCoords(row, col));
                        this.lifeFormsMap.get(playerNickname).remove(guiUtils.keyFromCoords(row, col));
                        this.itemsMap.get(playerNickname).remove(guiUtils.keyFromCoords(row, col));
                    }
                }
            }

            // Updating the position of all players
            if (cardStateJSON.getNeedsBoardUpdate()) {
                if (cardStateJSON.getNeedsUpdatedPositions()) {
                    if (this.clientModel.getDifficultyLevel() == 2) {
                        for (Map.Entry<String, Integer> entry : cardStateJSON.getUpdatedPositions().entrySet()) {
                            this.guiUtils.placePlayerInBoard(
                                    entry.getKey(),
                                    2,
                                    24,
                                    viewGameBoardStackPaneLevel2,
                                    playersRocketBoard
                            );
                        }
                    }
                    else {
                        for (Map.Entry<String, Integer> entry : cardStateJSON.getUpdatedPositions().entrySet()) {
                            this.guiUtils.placePlayerInBoard(
                                    entry.getKey(),
                                    2,
                                    18,
                                    viewGameBoardStackPaneLevel0,
                                    playersRocketBoard
                            );
                        }
                    }
                }
            }
        });

    }

    public void handleRemovePlayerFromBoard(String playerNickname) {
        this.guiUtils.removePlayerFromBoard(playerNickname, this.clientModel.getDifficultyLevel() == 0 ? viewGameBoardStackPaneLevel0 : viewGameBoardStackPaneLevel2, playersRocketBoard);
    }
}
