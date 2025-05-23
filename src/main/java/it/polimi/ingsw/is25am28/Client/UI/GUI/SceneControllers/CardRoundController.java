package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.TAB;

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

    @FXML private VBox statsBox;
    @FXML private HBox commandsBox;
    @FXML private GridPane commandsGrid;

    // Board visualization
    @FXML private VBox viewGameBoardContainer;
    @FXML private Pane viewGameBoardStackPaneLevel0;
    @FXML private Pane viewGameBoardStackPaneLevel2;
    @FXML private Button goBackToCardRoundButtonFromViewBoard;

    private final Map<String, ImageView> playersRocketBoard = new HashMap<>();

    // Icons maps and interactable regions
    private final Map<String, Map<String, HBox>> lifeFormsMap = new HashMap<>();
    private final Map<String, Map<String, HBox>> itemsMap = new HashMap<>();
    private final Map<String, Map<String, HBox>> batteriesMap = new HashMap<>();

    // Region maps
    private final Map<String, Region> doubleCannonsRegions = new HashMap<>();
    private final Map<String, Region> doubleEnginesRegions = new HashMap<>();
    private final Map<String, Region> shieldsRegions = new HashMap<>();
    private final Map<String, Region> cabinsRegions = new HashMap<>();
    private final Map<String, Region> storagesRegions = new HashMap<>();
    private final Map<String, Region> batteriesRegions = new HashMap<>();

    private Map<String, Region> currentRegions = null;
    private boolean isTakeAction = false;

    private final List<Map<String, Region>> allComponentMaps = List.of(
            this.doubleCannonsRegions,
            this.doubleEnginesRegions,
            this.shieldsRegions,
            this.cabinsRegions,
            this.storagesRegions,
            this.batteriesRegions
    );

    private final ToggleGroup commandsToggleGroup = new ToggleGroup();
    private final ToggleGroup viewOtherShipsToggleGroup = new ToggleGroup();

    private List<ClientEventCard> cards;
    private ClientEventCard currEventCard;

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
            "setShieldsToActivate",
            "batteriesToBeStolen"
    );

    public void init(CardRoundDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();
        this.guiUtils = new GUIUtils(this.clientModel);

        this.cards = this.clientModel.getClientEventCards();

        // Setting the card's image
        this.setCurrentEventCard(state.getCardInfo());

        this.componentsImagesMap = new HashMap<>();
        this.playersShipGridPane = new HashMap<>();


        this.shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
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

        this.initStatsBox();

        this.initCommandBox();

        // TODO: this.initResourceBank();

        // Setting all the regions with the corresponding listeners
        this.initRegionMap(this.doubleCannonsRegions, new ArrayList<>(ship.getDoubleCannons()), this::handleDoubleCannonToActivate);
        this.initRegionMap(this.doubleEnginesRegions, new ArrayList<>(ship.getDoubleEngines()), this::handleDoubleEnginesToActivate);
        this.initRegionMap(this.shieldsRegions, new ArrayList<>(ship.getShieldList()), this::handleShieldsToActivate);
        this.initRegionMap(this.cabinsRegions, new ArrayList<>(ship.getCabinList()), this::handleCrewToRemove);
        this.initRegionMap(this.storagesRegions, new ArrayList<>(ship.getStorageList()), (row, col) -> {
            if (this.isTakeAction) {
                this.handleItemToTake(row, col);
            } else {
                this.handleItemToRemove(row, col);
            }
        });
        this.initRegionMap(this.batteriesRegions, new ArrayList<>(ship.getBatteryList()), this::handleBatteriesToBeStolen);














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
     * Sets the correct game board image based
     * on the current difficulty level.
     */
    private void initViewGameBoard() {
        if (this.clientModel.getDifficultyLevel() == 2) {
            this.setVisibility(this.viewGameBoardStackPaneLevel2, true);

            for (String playerNickname : this.clientModel.getAllPlayersNicknames()) {
                this.guiUtils.placePlayerInBoard(
                        playerNickname,
                        2,
                        24,
                        this.viewGameBoardStackPaneLevel2,
                        this.playersRocketBoard
                );
            }
        }
        else {
            this.setVisibility(this.viewGameBoardStackPaneLevel0, true);

            for (String playerNickname : this.clientModel.getAllPlayersNicknames()) {
                this.guiUtils.placePlayerInBoard(
                        playerNickname,
                        0,
                        18,
                        this.viewGameBoardStackPaneLevel2,
                        this.playersRocketBoard
                );
            }
        }
    }

    private void initRegionMap(Map<String, Region> componentsRegions, List<ClientComponent> components, BiConsumer<Integer, Integer> onClick) {
        // Sets all the regions of all the componentsRegions maps

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
        // Setting the current eventCard
        for(ClientEventCard card : this.cards) {
            if(card.getUniqueCardId() == cardInfo.getUniqueCardId()) {
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
            } else {

                ToggleButton selected = (ToggleButton) newToggle;
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

        List<String> availableCommands = this.currEventCard.getAvailableCommands();

        try {
            ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
            if (ship == null) {
                System.out.println(PrintUtils.addColor("[ERROR] [CardRoundScene] ClientShip is null", ANSIColors.RED));
                return;
            }
            ship.generateComponentSubLists();

            if(ship.getFirePower(null) > this.currEventCard.getFirepower()) {

                // Enables the "setTakeReward" command if the baseline firepower is enough
                availableCommands.add("setTakeReward");
            }
        } catch (UnsupportedOperationException e) {
            // Do nothing, the command will not be added
        }

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

        System.out.println("Available commands: " + availableCommands);

        // Generating the toggles
        int col = 0;
        for (String command : allCommands) {
            Label toggleLabel = new Label();
            System.out.println("CONFRONTO: " + command + " RISULTATO: " + availableCommands.contains(command));
            // A command is added only if it's present in the available commands
            if (availableCommands.contains(command)) {
                System.out.println("CONTIENE: " + command);
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

                toggleLabel.setId(command);
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
        }

        // Add listener
        commandsToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle == null) {

                // TODO: see what to do

            } else {

                ToggleButton selected = (ToggleButton) newToggle;

                // Exit the board visualization if the toggle
                // is pressed during that phase
                this.handleGoBackToCardRoundButton(new ActionEvent());

//                this.disableRegion();

                switch (selected.getId()) {
                    case "playCard" -> {this.playCard();}
                    case "setCrewToRemove" -> {this.enableRegion(this.cabinsRegions);}
                    case "setItemsToBeRemoved", "setItemsToBeTaken" -> {this.enableRegion(this.storagesRegions);}
                    case "setTakeReward" -> {this.addTakeReward(true);} // TODO: add dynamic selection buttons
//                    case "setChosenPlanetIndex" -> {this.addChosenPlanetIndex();} // TODO: NEEDS dynamic selection buttons
                    case "setWantsToVisit" -> {this.addWantsToVisit(true);} // TODO: add dynamic selection buttons
                    case "setShieldsToActivate" -> {this.enableRegion(this.shieldsRegions);}
                    case "setDoubleCannonsToActivate" -> {this.enableRegion(this.doubleCannonsRegions);}
                    case "setDoubleEnginesToActivate" -> {this.enableRegion(this.doubleEnginesRegions);}
                    case "batteriesToBeStolen" -> {this.enableRegion(this.batteriesRegions);}
                }
            }
        });
    }

    /**
     * Sets disabled(true) for all the regions in the given map
     */
    private void disableRegion(Map<String, Region> regionMap) {
        for (Region region : regionMap.values()) {
            region.setDisable(true);
        }
    }

    /**
     * Sets disabled(false) for all the regions in the given map, after disabling the previous regionMap
     */
    private void enableRegion(Map<String, Region> regionMap) {
        if (this.currentRegions != null) {
            this.disableRegion(regionMap);
        }
        for (Region region : regionMap.values()) {
            region.setDisable(false);
        }
        this.currentRegions = regionMap;
    }

    @FXML
    private void handleViewGameBoard() {
        // Disable all the previous containers
        this.setVisibility(this.shipImageView, false);
        this.setVisibility(this.shipGrid, false);

        this.commandsToggleGroup.selectToggle(null);
        this.viewOtherShipsToggleGroup.selectToggle(null);

        // Enable the board container
        this.setVisibility(this.viewGameBoardContainer, true);
    }

    @FXML
    private void handleGoBackToCardRoundButton(ActionEvent actionEvent) {
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

            if (takeReward != null) {
                label = new Label();
                label.setText("Take reward?: " + (takeReward ? "Yes" : "No"));
                actionsContainer.getChildren().add(label);
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (3) - Crew to remove
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

        // (4) - Items to remove
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

        // (5) - Items to take
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

        // (6) - Chosen planet index
        try {
            Integer chosenPlanetIndex = this.currEventCard.getChosenPlanetIndex();

            if (chosenPlanetIndex != null) {
                label = new Label();
                label.setText("Chosen planet index: " + chosenPlanetIndex);
                actionsContainer.getChildren().add(label);
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

                    componentLabel.setText(TAB + "Double Cannon @ (row=" + (doubleCannonToActivate.getKey().getI() + 1) + ", col=" + (doubleCannonToActivate.getKey().getJ() + 1) + ")");
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

                    componentLabel.setText(TAB + "Double Engine @ (row=" + (doubleEngineToActivate.getKey().getI() + 1) + ", col=" + (doubleEngineToActivate.getKey().getJ() + 1) + ")");
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
                    this.visualizePlayerActions();
                    this.currEventCard.clearJSON();
                },
                () -> {
                    // TODO: Reset local change since the playCard failed
                    //       (@Filippo)

                    this.showToast(
                            "[ERROR] There was an error while playing the card. Please try again.",
                            ToastType.ERROR
                    );

                    GUIHandler.setCommandCTX(null);
                    this.currEventCard.clearJSON();
                }
            )
        );
    }

    // Methods to select the components to execute a command on (+ Visual Updates)
    private void handleCrewToRemove(int row, int col) {

    }

    private void handleItemToRemove(int row, int col) {

    }

    private void handleItemToTake(int row, int col) {}

    private void handleTakeReward(int row, int col) {}

    private void handleChosenPlanetIndex(int row, int col) {}

    private void handleWantsToVisit(int row, int col) {}

    private void handleShieldsToActivate(int row, int col) {}

    private void handleDoubleCannonToActivate(int row, int col) {}

    private void handleDoubleEnginesToActivate(int row, int col) {}

    private void handleBatteriesToBeStolen(int row, int col) {}


    // Methods to modify the JSON (+ Local Updates)
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

    private void addTakeReward(boolean choice) {
        try {
            this.currEventCard.setTakeReward(choice);
            this.visualizePlayerActions();
        }
        catch (UnsupportedOperationException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    private void addChosenPlanetIndex(int chosenPlanetIndex) {
        try {
            this.currEventCard.setChosenPlanetIndex(chosenPlanetIndex);
            this.visualizePlayerActions();
        }
        catch (UnsupportedOperationException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    private void addWantsToVisit(boolean choice) {
        try {
            this.currEventCard.setWantsToVisit(choice);
            this.visualizePlayerActions();
        }
        catch (UnsupportedOperationException e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    private void addShieldToActivate(CoordinatePair shieldToActivate, CoordinatePair batteryToConsume) {
        ClientComponent possibleShield, possibleBattery;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [getCrewToRemove()] ClientShip is null", ToastType.ERROR);
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

    private void addDoubleCannonToActivate(CoordinatePair doubleCannonToActivate, CoordinatePair batteryToConsume) {
        ClientComponent possibleDoubleCannon, possibleBattery;
        ClientShip ship;

        ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);

        if (ship == null) {
            this.showToast("[ERROR] [getCrewToRemove()] ClientShip is null", ToastType.ERROR);
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
}
