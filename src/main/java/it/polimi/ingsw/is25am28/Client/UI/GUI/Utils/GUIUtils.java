package it.polimi.ingsw.is25am28.Client.UI.GUI.Utils;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientBattery;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientCabin;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientStorage;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers.GUIController;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlayerEndedShipDTO;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GUIUtils {

    private ClientModel clientModel;
    private Pair<Integer, Integer> shipOffsets;

    public GUIUtils(ClientModel clientModel) {
        this.clientModel = clientModel;
        this.shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
    }

    /**
     * A map that holds predefined positions of rockets for different levels of a game board.
     * Each position is represented by a unique key and associated with a pair of coordinates
     * (X, Y), where the X-coordinate represents the horizontal position and the Y-coordinate
     * represents the vertical position.
     */
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

    /**
     * Sets the image in the given {@code imageView} with the correct background based on the game's difficulty level
     */
    public void setShipGridBackground(ImageView shipImageView) {
        // Initializing the ship to display
        String path = "/imgs/cardboard/level_" + this.clientModel.getDifficultyLevel() + ".jpg";
        URL resource = Objects.requireNonNull(getClass().getResource(path));

        shipImageView.setImage(new Image(resource.toExternalForm()));
        shipImageView.setFitWidth(816.0);
        shipImageView.setPreserveRatio(true);
    }

    /**
     * Places a player's rocket on the game board. If the player's rocket is already present on the board,
     * its position is updated based on the player's current cursor and the game's difficulty level.
     *
     * @param playerNickname The nickname of the player whose rocket is to be placed or updated on the board.
     * @param difficultyLevel The current difficulty level of the game, which determines the game board configuration.
     * @param boardSize The total number of cells on the board, determining the wrapping behavior of player positions.
     * @param gameBoardPane The container (Pane) representing the game board where the player's rocket is positioned.
     * @param playersRocketBoard A map that stores the association between player nicknames and their rockets (ImageView objects) on the board.
     */
    public void placePlayerInBoard(String playerNickname,
                                   int difficultyLevel,
                                   int boardSize,
                                   Pane gameBoardPane,
                                   Map<String, ImageView> playersRocketBoard) {

        ClientPlayer player = this.clientModel.getAllClientPlayers().get(playerNickname);

        int cellIdx = ((player.getCursor() % boardSize) + boardSize) % boardSize;
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
     * Removes a player's rocket from the game board.
     *
     * @param playerNickname The nickname of the player whose rocket is to be placed or updated on the board.
     * @param gameBoardPane The container (Pane) representing the game board where the player's rocket is positioned.
     * @param playersRocketBoard A map that stores the association between player nicknames and their rockets (ImageView objects) on the board.
     */
    public void removePlayerFromBoard(String playerNickname, Pane gameBoardPane, Map<String, ImageView> playersRocketBoard) {
        Platform.runLater(() -> {
            if (playersRocketBoard.get(playerNickname) != null) {
                gameBoardPane.getChildren().remove(playersRocketBoard.get(playerNickname));
                playersRocketBoard.remove(playerNickname);
            }
        });
    }

    /**
     * Adds the lifeForm icons to the shipGrid in input
     * @return A map containing the flowPanes containing the icons
     */
    public Map<String, FlowPane> initShipLifeFormIcons(String playerNickname, GridPane shipGrid) {
        Map<String, FlowPane> lifeFormsMap = new HashMap<>();

        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        ship.generateComponentSubLists();

        for(ClientCabin cabin : ship.getCabinList()) {
            FlowPane cabinBox = new FlowPane();
            cabinBox.setAlignment(Pos.CENTER);
            initCabinLifeFormIcons(cabin, cabinBox);

            int row = cabin.getI();
            int col = cabin.getJ();

            lifeFormsMap.put(keyFromCoords(cabin.getI(), cabin.getJ()), cabinBox);

            int ofsRow = row - shipOffsets.getKey();
            int ofsCol = col - shipOffsets.getValue();

            shipGrid.add(cabinBox, ofsCol, ofsRow);

        }
        return lifeFormsMap;
    }

    /**
     * Inits/updates the given ClientCabin's icons
     */
    public void initCabinLifeFormIcons(ClientCabin cabin, FlowPane cabinBox) {
        cabinBox.getChildren().clear();
        URL resource;

        if (!cabin.getInhabitants().isEmpty()) {
            for (Lifeform  lifeform : cabin.getInhabitants()) {
                resource = Objects.requireNonNull(getClass().getResource(lifeform.getLifeformType().getImagePath()));
                ImageView icon = new ImageView(new Image(resource.toExternalForm(), 40, 40, true, true));
                cabinBox.getChildren().add(icon);
            }
        }
    }

    /**
     * Adds the item icons to the shipGrid in input
     * @return A map containing the flowPanes containing the icons
     */
    public Map<String, FlowPane> initShipItemIcons(String playerNickname, GridPane shipGrid) {
        Map<String, FlowPane> itemsMap = new HashMap<>();

        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        ship.generateComponentSubLists();

        for (ClientStorage storage : ship.getStorageList()) {
            FlowPane storageBox = new FlowPane();
            storageBox.setAlignment(Pos.CENTER);
            initStorageItemIcons(storage, storageBox);

            int row = storage.getI();
            int col = storage.getJ();

            itemsMap.put(keyFromCoords(row, col), storageBox);

            int ofsRow = row - shipOffsets.getKey();
            int ofsCol = col - shipOffsets.getValue();

            shipGrid.add(storageBox, ofsCol, ofsRow);

        }
        return itemsMap;
    }

    /**
     * Inits/updates the given ClientStorage's icons
     */
    public void initStorageItemIcons(ClientStorage storage, FlowPane storageBox) {
        storageBox.getChildren().clear();
        if (storage.getCapacity() == 3) {
            storageBox.setAlignment(Pos.TOP_CENTER);
            storageBox.setPadding(new Insets(10, 0, 0, 0));
        }
        storageBox.setRotate(storage.getDirection() * 90 + 90);
        URL resource;
        if (!storage.getStoredItems().isEmpty()) {
            for (Item item : storage.getStoredItems()) {
                resource = Objects.requireNonNull(getClass().getResource(item.getColor().getImagePath()));
                ImageView icon = new ImageView(new Image(resource.toExternalForm(), 40, 40, true, true));
                icon.setRotate(storage.getDirection() * -90 - 90);
                storageBox.getChildren().add(icon);
            }
        }
    }

    /**
     * Adds the battery icons to the shipGrid in input
     * @return A map containing the flowPanes containing the icons
     */
    public Map<String, FlowPane> initShipBatteryIcons(String playerNickname, GridPane shipGrid) {
        Map<String, FlowPane> batteryMap = new HashMap<>();

        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(playerNickname).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return null;
        }

        ship.generateComponentSubLists();

        for(ClientBattery battery : ship.getBatteryList()) {
            FlowPane batteryBox = new FlowPane();
            batteryBox.setAlignment(Pos.CENTER);
            initBatteryIcons(battery, batteryBox);

            int row = battery.getI();
            int col = battery.getJ();

            batteryMap.put(keyFromCoords(row, col), batteryBox);

            int ofsRow = row - shipOffsets.getKey();
            int ofsCol = col - shipOffsets.getValue();

            shipGrid.add(batteryBox, ofsCol, ofsRow);

        }
        return batteryMap;
    }

    /**
     * Inits/updates the given ClientBattery's icons
     */
    public void initBatteryIcons(ClientBattery battery, FlowPane batteryBox) {
        batteryBox.getChildren().clear();
        URL resource;
        batteryBox.setRotate(battery.getDirection() * 90);
        if (battery.getAvailability() > 0) {
            for (int i = 0; i < battery.getAvailability(); i++) {
                resource = Objects.requireNonNull(getClass().getResource("/imgs/icons/batteries/Battery.png"));
                ImageView icon = new ImageView(new Image(resource.toExternalForm(), 40, 40, true, true));
                if (((i + 1) % 2) == 0) {
                    icon.setRotate(0);
                    icon.setTranslateY(-3);
                } else {
                    icon.setRotate(180);
                    icon.setTranslateY(3);

                }
                batteryBox.getChildren().add(icon);
            }
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

        return cell;
    }

    // TODO: have the main player ship and grid reference


    /**
     * Restores the visual state of the ship grid, including icons and regions, based on the {@code emptiedIcons} and the {@code emptiedRegions}, using the passed function
     *
     * @param shipGrid The gridPane representing the ship grid where visuals should be restored.
     * @param emptiedIcons A map containing keys associated with FlowPane objects that were previously removed.
     * @param emptiedRegions A map containing keys associated with Region objects that were previously removed.
     * @param currentRegions A map representing the regions in which to add the regions to restore
     * @param castType The class type used to cast ship components during restoration.
     * @param initIcons A function used to initialize the icons for specific components in the grid.
     *                  Accepts a type parameter T and a FlowPane for the initialization logic.
     */
    public <T> void revertVisuals(GridPane shipGrid, Map<String, FlowPane> emptiedIcons, Map<String, Region> emptiedRegions, Map<String, Region> currentRegions,  Class<T> castType, BiConsumer<T, FlowPane> initIcons) {
        // Getting the player's ship
        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [GuiController] ClientShip is null", ANSIColors.RED));
            return;
        }

        for (Map.Entry<String, FlowPane> entry : emptiedIcons.entrySet()) {

            Pair<Integer, Integer> boxCoords = this.coordsFromKey(entry.getKey());
            int row = boxCoords.getKey();
            int col = boxCoords.getValue();

            String restoreKey = entry.getKey();
            FlowPane boxToRestore = emptiedIcons.get(restoreKey);

            T componentToRestore = castType.cast(ship.getComponent(row, col));
            initIcons.accept(componentToRestore, boxToRestore);

            Region regionToRestore = emptiedRegions.get(restoreKey);
//            System.out.println(PrintUtils.addColor("(1) : " + currentRegions, ANSIColors.RED));
//            System.out.println(PrintUtils.addColor("(emptiedRegions) : " + regionToRestore, ANSIColors.MAGENTA));
//            System.out.println(PrintUtils.addColor("(restoreKey) " + restoreKey, ANSIColors.YELLOW));
//            System.out.println(PrintUtils.addColor("(toRestore) : " + regionToRestore, ANSIColors.GREEN));
            if (regionToRestore != null) {
                currentRegions.put(restoreKey, regionToRestore);
            }
//            System.out.println(PrintUtils.addColor("(2) : " + currentRegions, ANSIColors.CYAN));

            if (regionToRestore != null) {

                int ofsRow = row - this.shipOffsets.getKey();
                int ofsCol = col - this.shipOffsets.getValue();

                shipGrid.add(regionToRestore, ofsCol, ofsRow);
            }
        }
        emptiedIcons.clear();
        emptiedRegions.clear();
    }

    /**
     * Sets the correct game board image based
     * on the current difficulty level.
     */
    public void initViewGameBoard(
            Pane viewGameBoardStackPaneLevel0,
            Pane viewGameBoardStackPaneLevel2,
            ImageView boardImageView,
            Map<String, ImageView> playersRocketBoard
    ) {
        String boardPath = "/imgs/cardboard/board_level_" + this.clientModel.getDifficultyLevel() + ".png";
        URL boardResource = Objects.requireNonNull(getClass().getResource(boardPath));

        boardImageView.setImage(new Image(boardResource.toExternalForm()));
        boardImageView.setFitWidth(816.0);
        boardImageView.setPreserveRatio(true);

        if (this.clientModel.getDifficultyLevel() == 2) {
            viewGameBoardStackPaneLevel2.setVisible(true);
            viewGameBoardStackPaneLevel2.setManaged(true);
        }
        else {
            viewGameBoardStackPaneLevel0.setVisible(true);
            viewGameBoardStackPaneLevel0.setManaged(true);
        }
    }

    /**
     * Sets the player rockets on an initialized
     * board based on the difficulty level
     */
    public void initPlayersOnGameBoard(
            Pane viewGameBoardStackPaneLevel0,
            Pane viewGameBoardStackPaneLevel2,
            Map<String, ImageView> playersRocketBoard
    ) {
        List<ClientPlayer> players = this.clientModel.getClientBoard() != null ? this.clientModel.getClientBoard().getPlayers() : this.clientModel.getAllClientPlayers().values().stream().toList();

        if (this.clientModel.getDifficultyLevel() == 2) {
            viewGameBoardStackPaneLevel2.setVisible(true);
            viewGameBoardStackPaneLevel2.setManaged(true);
            viewGameBoardStackPaneLevel0.setVisible(false);
            viewGameBoardStackPaneLevel0.setManaged(false);

            for (ClientPlayer player : players) {
                this.placePlayerInBoard(
                        player.getNickname(),
                        2,
                        24,
                        viewGameBoardStackPaneLevel2,
                        playersRocketBoard
                );
            }
        }
        else {
            viewGameBoardStackPaneLevel0.setVisible(true);
            viewGameBoardStackPaneLevel0.setManaged(true);
            viewGameBoardStackPaneLevel2.setVisible(false);
            viewGameBoardStackPaneLevel2.setManaged(false);

            for (ClientPlayer player : players) {
                this.placePlayerInBoard(
                        player.getNickname(),
                        0,
                        18,
                        viewGameBoardStackPaneLevel0,
                        playersRocketBoard
                );
            }
        }
    }
}
