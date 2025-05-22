package it.polimi.ingsw.is25am28.Client.UI.GUI.Utils;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientBattery;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientCabin;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientStorage;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlayerEndedShipDTO;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GUIUtils {

    private ClientModel clientModel;
    private Pair<Integer, Integer> shipOffsets;

    public GUIUtils(ClientModel clientModel) {
        this.clientModel = clientModel;
        this.shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
    }

    private final Map<String, Pair<Double, Double>> rocketPositions = Map.ofEntries(
            // Level 2 board
            Map.entry("level_2_0", new Pair<>(156.0, 81.0)),
            Map.entry("level_2_1", new Pair<>(215.0, 59.0)),
            Map.entry("level_2_2", new Pair<>(276.0, 47.0)),
            Map.entry("level_2_3", new Pair<>(337.0, 40.0)),
            Map.entry("level_2_4", new Pair<>(399.0, 41.0)),
            Map.entry("level_2_5", new Pair<>(461.0, 48.0)),
            Map.entry("level_2_6", new Pair<>(521.0, 61.0)),
            Map.entry("level_2_7", new Pair<>(578.0, 85.0)),
            Map.entry("level_2_8", new Pair<>(632.0, 121.0)),
            Map.entry("level_2_9", new Pair<>(673.0, 181.0)),
            Map.entry("level_2_10", new Pair<>(665.0, 252.0)),
            Map.entry("level_2_11", new Pair<>(624.0, 306.0)),
            Map.entry("level_2_12", new Pair<>(569.0, 343.0)),
            Map.entry("level_2_13", new Pair<>(510.0, 363.0)),
            Map.entry("level_2_14", new Pair<>(451.0, 376.0)),
            Map.entry("level_2_15", new Pair<>(389.0, 381.0)),
            Map.entry("level_2_16", new Pair<>(327.0, 382.0)),
            Map.entry("level_2_17", new Pair<>(265.0, 376.0)),
            Map.entry("level_2_18", new Pair<>(203.0, 361.0)),
            Map.entry("level_2_19", new Pair<>(145.0, 335.0)),
            Map.entry("level_2_20", new Pair<>(94.0, 297.0)),
            Map.entry("level_2_21", new Pair<>(60.0, 239.0)),
            Map.entry("level_2_22", new Pair<>(65.0, 170.0)),
            Map.entry("level_2_23", new Pair<>(102.0, 116.0)),

            // Level 0 board
            Map.entry("level_0_0", new Pair<>(174.0, 67.0)),
            Map.entry("level_0_1", new Pair<>(251.0, 44.0)),
            Map.entry("level_0_2", new Pair<>(330.0, 34.0)),
            Map.entry("level_0_3", new Pair<>(405.0, 36.0)),
            Map.entry("level_0_4", new Pair<>(483.0, 47.0)),
            Map.entry("level_0_5", new Pair<>(559.0, 71.0)),
            Map.entry("level_0_6", new Pair<>(627.0, 118.0)),
            Map.entry("level_0_7", new Pair<>(659.0, 201.0)),
            Map.entry("level_0_8", new Pair<>(621.0, 277.0)),
            Map.entry("level_0_9", new Pair<>(552.0, 320.0)),
            Map.entry("level_0_10", new Pair<>(478.0, 342.0)),
            Map.entry("level_0_11", new Pair<>(401.0, 352.0)),
            Map.entry("level_0_12", new Pair<>(322.0, 352.0)),
            Map.entry("level_0_13", new Pair<>(244.0, 340.0)),
            Map.entry("level_0_14", new Pair<>(167.0, 316.0)),
            Map.entry("level_0_15", new Pair<>(100.0, 269.0)),
            Map.entry("level_0_16", new Pair<>(68.0, 188.0)),
            Map.entry("level_0_17", new Pair<>(107.0, 108.0))
    );



    /**
     * Creates and returns an empty ship grid represented as a {@code GridPane}.
     * The grid is configured with specific row and column constraints,
     * including spacing between grid elements and center alignment.
     * Each cell in the grid has predefined dimensions.
     *
     * @return A {@code GridPane} instance configured as an empty ship grid.
     */
    public GridPane createEmptyShipGrid(ClientPlayer player) {
        GridPane grid = new GridPane();
        grid.setHgap(2.5);
        grid.setVgap(2.5);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefSize(659, 459);

        for (int i = 0; i < 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(105);
            grid.getRowConstraints().add(row);
        }

        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(105);
            grid.getColumnConstraints().add(col);
        }

        // Get the player color to initialize the core cabin
        String playerColor = player.getColor().getPlayerColorString();
        URL resource = Objects.requireNonNull(getClass().getResource("/imgs/tiles/core_" + playerColor + ".jpg"));
        Image img = new Image(resource.toExternalForm(), 105, 105, true, true);

        ImageView imgView = new ImageView(img);
        imgView.setImage(img);

        grid.add(imgView, 3, 2);

        return grid;
    }

    public String keyFromCoords(int row, int col) {
        return row + "_" + col;
    }

    public Pair<Integer, Integer> coordsFromKey(String key) {
        String[] parts = key.split("_");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        return new Pair<>(row, col);
    }

    /**
     * Takes an empty shipGrid and adds the specified player's ship componentImages to it,
     * @return A map containing the references to the image containers, along with their coordinates
     */
    public Map<String, ImageView> createShipVisuals(String playerNickname, GridPane shipGrid) {
        Map<String, ImageView> imagesMap = new HashMap<>();

        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        Pair<Integer, Integer> shipDimensions = AbstractShip.shipDimensions.get(this.clientModel.getDifficultyLevel());
        int[][] shipProfiles = AbstractShip.shipProfiles.get(this.clientModel.getDifficultyLevel());

        int endRow = shipDimensions.getKey() + shipOffsets.getKey();
        int endCol = shipDimensions.getValue() + shipOffsets.getValue();

        for (int row = shipOffsets.getKey(); row < endRow; row++) {
            for (int col = shipOffsets.getValue(); col < endCol; col++) {
                if (shipProfiles[row][col] == 1) {

                    ClientComponent component = ship.getComponent(row, col);
                    if(component != null) {

                        // Adding the component's image in the shipGrid
                        URL componentImagePath = Objects.requireNonNull(getClass().getResource(component.getPath()));
                        Image img = new Image(componentImagePath.toExternalForm(), 105, 105, true, true);
                        ImageView componentImgView = new ImageView(img);
                        componentImgView.setRotate(component.getDirection() * 90);
                        componentImgView.setImage(img);

                        int ofsRow = row - shipOffsets.getKey();
                        int ofsCol = col - shipOffsets.getValue();
                        shipGrid.add(componentImgView, ofsCol, ofsRow);

                        // Adds the image to the images map, so that the reference can be easily retrieve in case of removal of the component
                        imagesMap.put(this.keyFromCoords(row, col), componentImgView);
                    }
                }
            }
        }
        return imagesMap;
    }

    public void setShipGridBackground(ImageView shipImageView) {
        // Initializing the ship to display
        String path = "/imgs/cardboard/level_" + this.clientModel.getDifficultyLevel() + ".jpg";
        URL resource = Objects.requireNonNull(getClass().getResource(path));

        shipImageView.setImage(new Image(resource.toExternalForm()));
        shipImageView.setFitWidth(816.0);
        shipImageView.setPreserveRatio(true);
    }

    public void placePlayerInBoard(String playerNickname,
                                   int difficultyLevel,
                                   int boardSize,
                                   Pane gameBoardPane,
                                   Map<String, ImageView> playersRocketBoard) {

        ClientPlayer player = this.clientModel.getAllClientPlayers().get(playerNickname);

        int cellIdx = (player.getCursor() / boardSize) + (player.getCursor() % boardSize);
        String cellID = "level_" + difficultyLevel + "_" + cellIdx;

        Pair<Double, Double> positions = this.rocketPositions.get(cellID);
        if (positions == null) {
            System.err.println("No position found for cell " + cellIdx + " on level " + difficultyLevel);
            return;
        }

        ImageView existingRocket = playersRocketBoard.get(playerNickname);

        Platform.runLater(() -> {
            // If the rocket already exists, then we can just update its position
            // otherwise, we need to create and place the rocket
            if (existingRocket != null) {
                TranslateTransition transition = new TranslateTransition(Duration.millis(300), existingRocket);
                transition.setToX(positions.getKey() - existingRocket.getLayoutX());
                transition.setToY(positions.getValue() - existingRocket.getLayoutY());
                transition.setOnFinished(e -> {
                    existingRocket.setLayoutX(positions.getKey());
                    existingRocket.setLayoutY(positions.getValue());
                    existingRocket.setTranslateX(0);
                    existingRocket.setTranslateY(0);
                });
                transition.play();
            } else {
                String path = "/imgs/rocket/rocket_" + player.getColor().getPlayerColorString() + ".png";
                URL resource = Objects.requireNonNull(getClass().getResource(path), "Rocket image not found: " + path);

                ImageView newRocket = new ImageView(new Image(resource.toExternalForm()));
                newRocket.setFitWidth(75);
                newRocket.setPreserveRatio(true);
                newRocket.setSmooth(true);
                newRocket.setLayoutX(positions.getKey());
                newRocket.setLayoutY(positions.getValue());

                gameBoardPane.getChildren().add(newRocket);
                playersRocketBoard.put(playerNickname, newRocket);
            }
        });
    }

    /**
     * Adds the lifeForm icons to the shipGrid in input
     * @return A map containing the panes containing the icons
     */
    public Map<String, HBox> initShipLifeFormIcons(String playerNickname, GridPane shipGrid) {
        Map<String, HBox> lifeFormsMap = new HashMap<>();

        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        ship.generateComponentSubLists();

        for(ClientCabin cabin : ship.getCabinList()) {
            HBox cabinBox = initCabinLifeFormIcons(cabin);
            if (cabinBox != null) {
                int row = cabin.getI();
                int col = cabin.getJ();

                lifeFormsMap.put(keyFromCoords(cabin.getI(), cabin.getJ()), cabinBox);

                int ofsRow = row - shipOffsets.getKey();
                int ofsCol = col - shipOffsets.getValue();

                shipGrid.add(cabinBox, ofsCol, ofsRow);
            }
        }
        return lifeFormsMap;
    }

    /**
     * @return A HBox containing the cabin's lifeForms icons
     */
    public HBox initCabinLifeFormIcons(ClientCabin cabin) {
        HBox cabinBox = new HBox();
        cabinBox.setAlignment(Pos.CENTER);
        URL resource;

        if (!cabin.getInhabitants().isEmpty()) {
            for (Lifeform  lifeform : cabin.getInhabitants()) {
                resource = Objects.requireNonNull(getClass().getResource(lifeform.getLifeformType().getImagePath()));
                ImageView icon = new ImageView(new Image(resource.toExternalForm(), 40, 40, true, true));
                cabinBox.getChildren().add(icon);
            }
            return cabinBox;
        } else {
            return null;
        }
    }

    /**
     * Adds the item icons to the shipGrid in input
     * @return A map containing the HBoxes containing the icons
     */
    public Map<String, HBox> initShipItemIcons(String playerNickname, GridPane shipGrid) {
        Map<String, HBox> itemsMap = new HashMap<>();

        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        ship.generateComponentSubLists();

        for (ClientStorage storage : ship.getStorageList()) {
            HBox storageBox = initStorageItemIcons(storage);
            if (storageBox != null) {
                int row = storage.getI();
                int col = storage.getJ();

                itemsMap.put(keyFromCoords(row, col), storageBox);

                int ofsRow = row - shipOffsets.getKey();
                int ofsCol = col - shipOffsets.getValue();

                shipGrid.add(storageBox, ofsCol, ofsRow);
            }
        }
        return itemsMap;
    }

    /**
     * @return A HBox containing the storage's items icons
     */
    public HBox initStorageItemIcons(ClientStorage storage) {
        HBox storageBox = new HBox();
        storageBox.setAlignment(Pos.CENTER);
        URL resource;

        if (!storage.getStoredItems().isEmpty()) {
            for (Item item : storage.getStoredItems()) {
                resource = Objects.requireNonNull(getClass().getResource(item.getColor().getImagePath()));
                ImageView icon = new ImageView(new Image(resource.toExternalForm(), 40, 40, true, true));
                storageBox.getChildren().add(icon);
            }
            return storageBox;
        } else {
            return null;
        }
    }

    public Map<String, HBox> initShipBatteryIcons(String playerNickname, GridPane shipGrid) {
        Map<String, HBox> batteryMap = new HashMap<>();

        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        ship.generateComponentSubLists();

        for(ClientBattery battery : ship.getBatteryList()) {
            HBox batteryBox = initBatteryIcons(battery);
            if (batteryBox != null) {
                int row = battery.getI();
                int col = battery.getJ();

                batteryMap.put(keyFromCoords(row, col), batteryBox);

                int ofsRow = row - shipOffsets.getKey();
                int ofsCol = col - shipOffsets.getValue();

                shipGrid.add(batteryBox, ofsCol, ofsRow);
            }
        }
        return batteryMap;
    }

    public HBox initBatteryIcons(ClientBattery battery) {
        HBox batteryBox = new HBox();
        batteryBox.setAlignment(Pos.CENTER);
        URL resource;

        if (battery.getAvailability() > 0) {
            for (int i = 0; i < battery.getAvailability(); i++) {
                resource = Objects.requireNonNull(getClass().getResource("/imgs/icons/batteries/Battery.png"));
                ImageView icon = new ImageView(new Image(resource.toExternalForm(), 40, 40, true, true));
                batteryBox.getChildren().add(icon);
            }
            return batteryBox;
        } else {
            return null;
        }


    }

    /**
     * @return a clickable region (in the disabled state) without a click listener
     */
    public Region generateDisabledRegion() {
        // Generate a region without clickListener
        Region cell = new Region();
        cell.setPrefSize(100, 100);
        cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);
        cell.setDisable(true);
    }
}
