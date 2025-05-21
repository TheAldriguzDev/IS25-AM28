package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientCabin;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientVital;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PopulateShipComponentDTO;
import it.polimi.ingsw.is25am28.Model.Components.VitalType;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Network.Messages.PopulateShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//TODO: for now use colors to indicate full cabins

public class PopulateShipController extends GUIController {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private TextFlow populateShipLabel;
    @FXML private StackPane imagePane;
    @FXML private GridPane viewOtherShipsGrid;

    @FXML private ToggleGroup lifeFormsToggles;
    @FXML private ToggleButton purpleToggle;
    @FXML private ToggleButton brownToggle;
    @FXML private ToggleButton whiteToggle;

    private boolean isShipFull;

    // Map of the component's clickable regions
    private Map<String, Region> cabinRegions;
    private Map<String, Region> purpleAlienCabinRegion;
    private Map<String, Region> brownAlienCabinRegion;
    private LifeformType currentSelectableLifeForm;
    private boolean canEnablePurple;
    private boolean canEnableBrown;

    // Handle the players ship
//    private Map<String, GridPane> playersShipGridPane;

    public void init(PopulateShipDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();
        this.guiUtils = new GUIUtils(this.clientModel);

        this.playersShipGridPane = new HashMap<>();
        this.componentsImagesMap = new HashMap<>();

        this.initLifeFormsToggles();

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
        }

        // Setting the current shipGrid to this client's ship
        this.imagePane.getChildren().remove(this.shipGrid);
        this.shipGrid = this.playersShipGridPane.get(this.clientModel.getNickname());
        this.imagePane.getChildren().add(this.shipGrid);

        // Sets the populateShipLabel and the isShipFull flag
        this.setShipLabelText(state.getPlayersReady().contains(this.clientModel.getNickname()));

        ship.generateComponentSubLists();
        List<ClientCabin> cabins = ship.getCabinList();
        // Removing the core from the list, since it's automatically filled with astronauts
        cabins.removeIf(ClientCabin::isCore);

        // Create the 3 clickable regions maps
        // After placing an alien disable the toggle
        // If there are no suitable cabins for aliens, disable the toggle
        if(!isShipFull && !cabins.isEmpty()) {
            this.whiteToggle.setDisable(false);
            this.cabinRegions = new HashMap<>();
            this.purpleAlienCabinRegion = new HashMap<>();
            this.brownAlienCabinRegion = new HashMap<>();

            for(ClientCabin cabin : cabins) {

                // Create a clickable region for every cabin, with clicks disabled, then put it in the general cabinRegions map
                Region cell = new Region(); // Place holder node
                cell.setPrefSize(100, 100);
                cell.setStyle("-fx-background-color: transparent;");
                cell.setCursor(Cursor.DEFAULT);
                cell.setPickOnBounds(false);
                this.cabinRegions.put(this.guiUtils.keyFromCoords(cabin.getI(), cabin.getJ()), cell);

                for(ClientComponent component : ship.getNearestReachableComponents(cabin)) {
                    if (component != null && component.getClass().equals(ClientVital.class)) {
                        ClientVital vital = (ClientVital) component;
                        if (vital.getVitalType().equals(VitalType.PURPLE_VITAL)) {
                            this.purpleToggle.setDisable(false);
                            // Add the cabin to the purpleAlienRegion
                            this.purpleAlienCabinRegion.put(this.guiUtils.keyFromCoords(cabin.getI(), cabin.getJ()), cell);
                        } else { // Purple vital
                            this.brownToggle.setDisable(false);
                            // Add the cabin to the brownAlienRegion
                            this.brownAlienCabinRegion.put(this.guiUtils.keyFromCoords(cabin.getI(), cabin.getJ()), cell);
                        }
                    }
                }

                // Places the regions on the shipGrid
                int ofsRow = cabin.getI() - shipOffsets.getKey();
                int ofsCol = this.clientModel.getDifficultyLevel() == 0 ? cabin.getJ() - shipOffsets.getValue() + 1 : cabin.getJ() - shipOffsets.getValue();
                this.shipGrid.add(cell, ofsCol, ofsRow);
                cell.setOnMouseClicked(_ -> handlePlacedLifeform(ofsRow, ofsCol));
            }
        }
    }

    public void handlePlacedLifeform(int row, int col) {

        if (this.currentSelectableLifeForm == null) { return; }

        GUIHandler.setCommandCTX(new CommandCTX(
                "addLifeform",
                () -> {
                    this.clientModel.getState().addLifeFormToShip(row, col, this.currentSelectableLifeForm);
                },
                () -> {}
        ));

        // TODO: can be simplified
        try {
            if (this.clientModel.getDifficultyLevel() == 0) {
                ComponentHelper<LifeformType> lifeFormToAdd = new ComponentHelper<>(row + shipOffsets.getKey(), col + shipOffsets.getValue() -1);
                lifeFormToAdd.addItem(this.currentSelectableLifeForm);
                GUIHandler.getVirtualClient().sendMessage(
                        new PopulateShip(
                                this.clientModel.getNickname(),
                                lifeFormToAdd
                        )
                );
            } else {
                ComponentHelper<LifeformType> lifeFormToAdd = new ComponentHelper<>(row + shipOffsets.getKey(), col + shipOffsets.getValue());
                lifeFormToAdd.addItem(this.currentSelectableLifeForm);
                GUIHandler.getVirtualClient().sendMessage(
                        new PopulateShip(
                                this.clientModel.getNickname(),
                                lifeFormToAdd
                        )
                );
            }
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    public void placeLifeform(PopulateShipComponentDTO data) {
//        System.out.println("INFO: {isShipFull: " + data.isShipPopulated() + "}, {Coordinates: (" + data.getComponent().getI() + "," + data.getComponent().getJ() + ")}, {LF: " + data.getComponent().getItem().orElse(null) + "}");

        Platform.runLater(() -> {

            ComponentHelper<LifeformType> lfch = data.getComponent();

            int row = lfch.getI();
            int col = lfch.getJ();

            String targetPlayer = data.getPlayerNickname();

            if (targetPlayer.equals(this.clientModel.getNickname())) {
                // If an alien is added, disable the corresponding button since there can be only one alien per type onboard
                LifeformType lf = lfch.getItem().orElse(null);
                if (lf != null) {
                    if (lf.equals(LifeformType.PURPLE_ALIEN)) {
                        // Added purple alien
                        this.purpleToggle.setDisable(true);
                        this.currentSelectableLifeForm = null;
                        this.disableRegion();
                    } else if (lf.equals(LifeformType.BROWN_ALIEN)) {
                        // Added brown alien
                        this.brownToggle.setDisable(true);
                        this.currentSelectableLifeForm = null;
                        this.disableRegion();
                    }
                }

                Region region = this.cabinRegions.get(this.guiUtils.keyFromCoords(row, col));

                // Remove the clickable region from the 3 maps, and set the color to red (to signal that it is now occupied) // TODO: little icons would be far better, or simply do not highlight anymore
                this.cabinRegions.remove(this.guiUtils.keyFromCoords(row, col));
                // If it's not present in these maps, nothing happens
                this.purpleAlienCabinRegion.remove(this.guiUtils.keyFromCoords(row, col));
                this.brownAlienCabinRegion.remove(this.guiUtils.keyFromCoords(row, col));

                // If an astronaut occupied a valid alien cabin we check if there are valid alien cabins left
                if (this.purpleAlienCabinRegion.isEmpty()) {
                    this.purpleToggle.setDisable(true);
                }
                if (this.brownAlienCabinRegion.isEmpty()) {
                    this.brownToggle.setDisable(true);
                }

                // Deactivating the region and setting its background color
                region.setDisable(true);
                region.setPickOnBounds(false);
                region.setCursor(Cursor.DEFAULT);
                region.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");

                // TODO: if someone has finished the colored regions will disappear, will be resolved with the addition of custom pawns
                // Setting the shipLabelText
                this.setShipLabelText(data.isShipPopulated());
                if (this.isShipFull) {
                    if (!this.cabinRegions.isEmpty()) {
                        for (Map.Entry<String, Region> entry : this.cabinRegions.entrySet()) {
                            // Remove all the clickable regions
//                            this.shipGrid.getChildren().remove(entry.getValue());
                            this.playersShipGridPane.get(this.clientModel.getNickname()).getChildren().add(entry.getValue());
                        }
                        this.cabinRegions.clear();
                        this.purpleAlienCabinRegion.clear();
                        this.brownAlienCabinRegion.clear();
                    }
                }
            } else {
                // If a secondary ship is being updated, the only necessary action is to set the background by adding a region
                int ofsRow = row - shipOffsets.getKey();
                int ofsCol = this.clientModel.getDifficultyLevel() == 0 ? col - shipOffsets.getValue() + 1 : col - shipOffsets.getValue();

                Region cell = new Region();
                cell.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");

                this.playersShipGridPane.get(targetPlayer).add(cell, ofsCol, ofsRow);
            }


        });
    }

    private void setShipLabelText(boolean isShipFull) {
        String validityLabel;
        this.populateShipLabel.getChildren().clear();
        if (!isShipFull) {
            this.isShipFull = false;
            Text text1 = new Text("Your ship is ");
            text1.setFill(Color.WHITE);
            text1.setFont(Font.font(20));
            Text text2 = new Text("NOT FULL! ");
            text2.setFill(Color.RED);
            text2.setFont(Font.font(20));
            Text text3 = new Text("please select the lifeForms to add to the ship");
            text3.setFill(Color.WHITE);
            text3.setFont(Font.font(20));
            this.populateShipLabel.getChildren().addAll(text1, text2, text3);
        } else {
            this.isShipFull = true;
            Text text1 = new Text("Your ship is ");
            text1.setFill(Color.WHITE);
            text1.setFont(Font.font(20));
            Text text2 = new Text("FULL! ");
            text2.setFill(Color.GREEN);
            text2.setFont(Font.font(20));
            Text text3 = new Text("please wait for the other players to fill their ships");
            text3.setFill(Color.WHITE);
            text3.setFont(Font.font(20));
            this.populateShipLabel.getChildren().addAll(text1, text2, text3);
        }
    }

    private void initLifeFormsToggles() {

        //Will be reactivated only if the ship is not full
        this.purpleToggle.setDisable(true);
        this.brownToggle.setDisable(true);
        this.whiteToggle.setDisable(true);

        this.lifeFormsToggles.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle == null) {
                // If no toggle is selected, disable all the regions
                this.currentSelectableLifeForm = null;
                this.disableRegion();
                return;
            }

            ToggleButton selected = (ToggleButton) newToggle;

            switch (selected.getId()) {
                case "purpleToggle" -> {
                    this.currentSelectableLifeForm = LifeformType.PURPLE_ALIEN;
                    enableRegion(this.purpleAlienCabinRegion);
                }
                    case "brownToggle"-> {
                    this.currentSelectableLifeForm = LifeformType.BROWN_ALIEN;
                    enableRegion(this.brownAlienCabinRegion);
                    }
                case "whiteToggle" -> {
                    this.currentSelectableLifeForm = LifeformType.ASTRONAUT;
                    enableRegion(this.cabinRegions);
                }
            }
        });
    }

    /**
     * @param regionMap to activate
     */
    private void enableRegion(Map<String, Region> regionMap) {
        // Deactivates the old regions
        this.disableRegion();
        // Activates the new regions
        for(Region region : regionMap.values()) {
            region.setPickOnBounds(true);
            region.setCursor(Cursor.HAND);
            region.setStyle("-fx-background-color: rgba(160, 212, 104, 0.5);");
        }
    }

    /**
     * Deactivates all the regions
     */
    private void disableRegion() {
        for(Region region : this.cabinRegions.values()) {
            region.setPickOnBounds(false);
            region.setCursor(Cursor.DEFAULT);
            region.setStyle("-fx-background-color: transparent;");
        }
    }

    // TODO: create method in upper class to remove duplicate code
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
        viewOtherShipsToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle == null) {

                // Enable the lifeFormToggles (only the ones that have not been disabled in other methods)
                this.whiteToggle.setDisable(false);

                if (this.canEnablePurple) { this.purpleToggle.setDisable(false); }
                if (this.canEnableBrown) { this.brownToggle.setDisable(false); }

                // Go back to view the client's own ship
                this.setShipGrid(this.clientModel.getNickname());
            } else {

                // Deselect the lifeFormToggles (if any is active)
                this.lifeFormsToggles.selectToggle(null);
                // Disable the lifeForm toggles after saving the flags that determine if the alien buttons are to be reactivated (won't be done if they were already disabled)
                this.canEnablePurple = !this.purpleToggle.isDisabled();
                this.canEnablePurple = !this.purpleToggle.isDisabled();

                this.whiteToggle.setDisable(true);
                this.purpleToggle.setDisable(true);
                this.brownToggle.setDisable(true);

                ToggleButton selected = (ToggleButton) newToggle;
                this.setShipGrid(selected.getText());
            }



            // Todo: change method, this is the populateships version, could also do half method in upper class and override invoking super, adding this part

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
}
