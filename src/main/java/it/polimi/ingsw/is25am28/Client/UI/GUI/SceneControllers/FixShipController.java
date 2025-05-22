package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.FixedComponentDTO;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Network.Messages.FixShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;
import java.util.Map;

public class FixShipController extends GUIController {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private TextFlow fixShipLabel;
    @FXML private StackPane imagePane;
    @FXML private VBox fixShipVBox;
    @FXML private GridPane viewOtherShipsGrid;

    // Board visualization
    @FXML private VBox viewGameBoardContainer;
    @FXML private Pane viewGameBoardStackPaneLevel0;
    @FXML private Pane viewGameBoardStackPaneLevel2;
    @FXML private Button goBackToConstructionButtonFromViewBoard;

    ToggleGroup viewOtherShipsToggleGroup = new ToggleGroup();

    private Map<String, ImageView> playersRocketBoard = new HashMap<>();

    private boolean isShipValid;

    // Map of the component's clickable regions, only relevant for the client
    private Map<String, Region> componentsRegionMap;

    // Handle the players ship
//    private Map<String, GridPane> playersShipGridPane;

    public void init(FixShipDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();
        this.guiUtils = new GUIUtils(this.clientModel);

        this.componentsImagesMap = new HashMap<>();
        this.componentsRegionMap = new HashMap<>();
        this.playersShipGridPane = new HashMap<>();


        this.shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        // Setting the buttons to view other ships
        this.initViewOtherShipsGrid();

        // Initializes the board background image
        this.initViewGameBoard();

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

        // Setting both the shipLabelText and the isShipValid flag
        this.setShipLabelText(!state.getPlayerWithInvalidShip().contains(this.clientModel.getNickname()));

        // Gets the visited components from the componentsImagesMap, since there's the need to set a region for every ship's component
        for (Map.Entry<String, ImageView> entry : this.componentsImagesMap.get(this.clientModel.getNickname()).entrySet()) {
            Pair coords = this.guiUtils.coordsFromKey(entry.getKey());
            int row = (int) coords.getKey();
            int col = (int) coords.getValue();

            int ofsRow = row - shipOffsets.getKey();
            int ofsCol = col - shipOffsets.getValue();

            // The player must not be able to remove the core
            if (!(ofsRow == 2 && ofsCol == 3) && !isShipValid) {

                Region cell = new Region();
                cell.setPrefSize(100, 100);
                cell.setStyle("-fx-background-color: transparent;");
                cell.setCursor(Cursor.HAND);
                cell.setPickOnBounds(true);

                // Adds the component to the regions map, so the reference can be easily retrieve in case of removal of the component
                this.componentsRegionMap.put(this.guiUtils.keyFromCoords(row, col), cell);

                this.shipGrid.add(cell, ofsCol, ofsRow);
                cell.setOnMouseClicked(_ -> handleRemoveComponent(ofsRow, ofsCol));
            }
        }
    }

    /**
     * Sets the correct game board image based
     * on the current difficulty level.
     */
    private void initViewGameBoard() {
        if (this.clientModel.getDifficultyLevel() == 2) {
            this.setVisibility(this.viewGameBoardStackPaneLevel2, true);
        }
        else {
            this.setVisibility(this.viewGameBoardStackPaneLevel0, true);
        }
    }

    /**
     * Removes the selected component
     */
    public void handleRemoveComponent(int row, int col) {
        GUIHandler.setCommandCTX(new CommandCTX(
                "removeTile", //
                () -> {},
                () -> {}
        ));

        // TODO: can be simplified
        try {

            GUIHandler.getVirtualClient().sendMessage(
                    new FixShip(
                            this.clientModel.getNickname(),
                            row + shipOffsets.getKey(),
                            col + shipOffsets.getValue()
                    )
            );

        } catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    // ===== METHOD USED BY THE VIEW UPDATER TO UPDATE THE VIEW IN REAL TIME ===== //

    public void removeComponent(FixedComponentDTO data) {

        int row = data.getI();
        int col = data.getJ();
        boolean isShipValid = data.isShipFixed();
        String targetPlayer = data.getPlayerNickname();

        Platform.runLater(() -> {

            // removing the component's image
            this.playersShipGridPane.get(targetPlayer).getChildren().remove(this.componentsImagesMap.get(targetPlayer).get(this.guiUtils.keyFromCoords(row, col)));
            this.componentsImagesMap.remove(this.guiUtils.keyFromCoords(row, col));

            // Update the label and regions only if the targetPlayer is the client himself
            if (targetPlayer.equals(this.clientModel.getNickname())) {
                // Remove the clickable region
                this.playersShipGridPane.get(this.clientModel.getNickname()).getChildren().remove(this.componentsRegionMap.get(this.guiUtils.keyFromCoords(row, col)));
                this.componentsRegionMap.remove(this.guiUtils.keyFromCoords(row, col));

                // Setting the shipLabelText
                this.setShipLabelText(isShipValid);

                // If the ship is valid all clickable regions must be deactivated/removed
                if (this.isShipValid) {
                    if (!this.componentsRegionMap.isEmpty()) {
                        for (Map.Entry<String, Region> entry : this.componentsRegionMap.entrySet()) {
                            // Remove all the clickable regions
                            this.shipGrid.getChildren().remove(entry.getValue());
                        }
                        this.componentsRegionMap.clear();
                    }
                }
            }
        });
    }

    private void setShipLabelText(boolean isShipValid) {
        String validityLabel;
        this.fixShipLabel.getChildren().clear();
        if (!isShipValid) {
            this.isShipValid = false;
            Text text1 = new Text("Your ship is ");
            text1.setFill(Color.WHITE);
            text1.setFont(Font.font(20));
            Text text2 = new Text("INVALID! ");
            text2.setFill(Color.RED);
            text2.setFont(Font.font(20));
            Text text3 = new Text("please select the components to remove");
            text3.setFill(Color.WHITE);
            text3.setFont(Font.font(20));
            this.fixShipLabel.getChildren().addAll(text1, text2, text3);
        } else {
            this.isShipValid = true;
            Text text1 = new Text("Your ship is ");
            text1.setFill(Color.WHITE);
            text1.setFont(Font.font(20));
            Text text2 = new Text("VALID! ");
            text2.setFill(Color.GREEN);
            text2.setFont(Font.font(20));
            Text text3 = new Text("please wait for the other players to fix their ships");
            text3.setFill(Color.WHITE);
            text3.setFont(Font.font(20));
            this.fixShipLabel.getChildren().addAll(text1, text2, text3);
        }
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

                // If no toggle is selected, enable all teh regions
                if (newToggle == null) {
                    // Enabling clickable areas
                    for (Region cell: this.componentsRegionMap.values()) {
                        cell.setDisable(false);
                    }
                    // Go back to view the client's own ship
                    this.setShipGrid(this.clientModel.getNickname());
                } else {
                    ToggleButton selected = (ToggleButton) newToggle;

                    this.handleGoBackToFixButton(new ActionEvent());

                    // Disabling clickable areas
                    for (Region cell: this.componentsRegionMap.values()) {
                        cell.setDisable(true);
                    }
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

    @FXML
    public void handleGoBackToFixButton(ActionEvent actionEvent) {
        this.setVisibility(this.viewGameBoardContainer, false);
        this.setVisibility(this.shipImageView, true);
        this.setVisibility(this.shipGrid, true);
        actionEvent.consume();
    }

    @FXML
    // Method used to display the current game board
    private void handleViewGameBoard() {
        // Disable all the previous containers
        this.viewOtherShipsToggleGroup.selectToggle(null);

        this.setVisibility(this.shipImageView, false);
        this.setVisibility(this.shipGrid, false);

        if (this.isShipValid) {
            this.goBackToConstructionButtonFromViewBoard.setText("Go back");
        }

        // Enable the board container
        this.setVisibility(this.viewGameBoardContainer, true);
    }
}
