package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Network.Messages.DeselectTile;
import it.polimi.ingsw.is25am28.Network.Messages.PlaceTile;
import it.polimi.ingsw.is25am28.Network.Messages.SelectTile;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class ShipConstructionController extends GUIController {

    // Attributes to handle the selected component
    @FXML private ImageView selectedComponentImage;
    private boolean rotationInProgress = false;
    private Pair<Integer, Integer> shipOffsets;

    // Handle the players ship
    private final Map<String, GridPane> playersShipGridPane = new HashMap<>();

    // Attributes to handle the timer
    @FXML private Label timerLabel;
    @FXML private Button flipTimerButton;
    @FXML private HBox timerContainer;
    private int countDown = 97; // HourGlass timer
    private Timeline timer;

    // ========== FXML ATTRIBUTES ========== //
    @FXML private VBox shipContainer;
    @FXML private Button deselectButton;
    @FXML private Button rotateRightButton;
    @FXML private Button rotateLeftButton;
    @FXML private Button reserveButton;
    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private GridPane reservedComponentGrid;
    @FXML private Button confirmShipButton;
    @FXML private ScrollPane tileScrollPane;
    @FXML private FlowPane tileFlow;
    @FXML private VBox sidePanel;
    @FXML private ImageView subDeckOne;
    @FXML private ImageView subDeckTwo;
    @FXML private ImageView subDeckThree;
    @FXML private ImageView boardImageView;

    // ========== GAME ATTRIBUTES ========== //
    // Map used to target a specific component when an update arrives
    private final Map<Integer, ImageView> components = new HashMap<>();

    // Store the selected component to dynamically display the view
    private ClientComponent selectedComponent;

    // Method used to initialize the page information that needs to be displayed
    public void initShipConstruction() {
        // TODO: Init the players ships --> Useful to update the specific client ship in real time
        this.clientModel = GUIHandler.getInstance().getClientModel();

        // Init the component
        this.initComponents();

        // INIT THE NAVBAR
        this.initSidePanel();

        // Init the ship dynamic page
        this.initShipPage();
    }

    private void initComponents() {
        tileFlow.getChildren().clear();
        this.clientModel.getState().getConstructionShipComponents().forEach(c -> {
            Image img;
            URL resource;
            if (c.isFlipped()) {
                resource = Objects.requireNonNull(getClass().getResource(c.getPath()));
            } else {
                resource = Objects.requireNonNull(getClass().getResource("/imgs/tiles/unflipped.png"));
            }
            img = new Image(resource.toExternalForm(), 85, 85, true, true);

            ImageView imgView = getComponentImageView(c, img);

            this.components.put(c.getID(), imgView);
            tileFlow.getChildren().add(imgView);
        });
    }

    private void initSidePanel() {
        // Display and start the timer container only if the game level is != 0
        if (this.clientModel.getDifficultyLevel() != 0) {
            this.timerContainer.setVisible(true);
            this.startCountDownTimer();
        } else {
            this.timerContainer.setVisible(false);
        }

    }

    private void startCountDownTimer() {
        // Check if there is already an active timer
        if (timer != null) {
            timer.stop();
        }

        // Disable the button --> it will be enabled when the timerDTO arrives
        this.flipTimerButton.setDisable(true);

        // Update the text every second
        timer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            if (countDown <= 0) {
                timer.stop();
                return;
            }

            int minutes = countDown / 60;
            int seconds = countDown % 60;

            String timeFormatted = String.format("Flip available in %02d:%02d", minutes, seconds);
            this.timerLabel.setText(timeFormatted);

            countDown--;
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // TODO: Aggiungere a questo metodo un flow che permetta anche di inizializzare le navi dei player con i componenti che sono già presenti nelle loro navi (caso di riconnessione)
    private void initShipPage() {

        String path = "/imgs/cardboard/level_" + this.clientModel.getDifficultyLevel() + ".jpg";
        URL resource = Objects.requireNonNull(getClass().getResource(path));

        this.shipImageView.setImage(new Image(resource.toExternalForm()));
        this.shipImageView.setFitWidth(816.0);
        this.shipImageView.setPreserveRatio(true);

        Pair<Integer, Integer> shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
        this.shipOffsets = shipOffsets;
        Pair<Integer, Integer> shipDimensions = AbstractShip.shipDimensions.get(this.clientModel.getDifficultyLevel());
        int[][] shipProfiles = AbstractShip.shipProfiles.get(this.clientModel.getDifficultyLevel());

        int endRow = shipDimensions.getKey() + shipOffsets.getKey();
        int endCol = shipDimensions.getValue() + shipOffsets.getValue();;

        for (int row = shipOffsets.getKey(); row < endRow; row++) {
            for (int col = shipOffsets.getValue(); col < endCol; col++) {
                if (shipProfiles[row][col] == 1) {

                    if (this.clientModel.getDifficultyLevel() == 0) {
                        this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue() + 1);
                    } else {
                        this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue());
                    }
                }
            }
        }

        for (int col = 0; col < 2; col++) {
            Region cell = new Region(); // Place holder node
            cell.setPrefSize(100, 100);
            cell.setStyle("-fx-background-color: transparent;");
            this.shipGrid.add(cell, col, 0);
        }

        this.playersShipGridPane.put(this.clientModel.getNickname(), this.shipGrid);

        // Init the gridShipPane for each player
        for (ClientPlayer p : this.clientModel.getAllClientPlayers().values()) {
            this.playersShipGridPane.put(p.getNickname(), createEmptyShipGrid());
        }
    }

    private GridPane createEmptyShipGrid() {
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

        return grid;
    }


    private void addCellEventListener(int row, int col) {
        // Add the core to the ship
        if (row == 2 && col == 3) {
            String playerColor = this.clientModel.getAllClientPlayers().get(this.clientModel.getNickname()).getColor().getPlayerColorString();
            URL resource = Objects.requireNonNull(getClass().getResource("/imgs/tiles/core_" + playerColor + ".jpg"));
            Image img = new Image(resource.toExternalForm(), 105, 105, true, true);

            ImageView imgView = new ImageView(img);
            imgView.setImage(img);

            this.shipGrid.add(imgView, col, row);
            return;
        }

        Region cell = new Region(); // Place holder node
        cell.setPrefSize(100, 100);
        cell.setStyle("-fx-background-color: rgba(255, 0, 0, 0.2);"); // solo per test
        //cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);

        this.shipGrid.add(cell, col, row);
        cell.setOnMouseClicked(_ -> handlePlaceTile(row, col));
    }

    private ImageView getComponentImageView(ClientComponent c, Image img) {
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(90);
        imgView.setFitHeight(90);
        imgView.setPreserveRatio(true);

        // If the component is visible set the opacity to 1, otherwise it will be 0. Furthermore, if visible add the onClick handler
        if (c.isVisible()) {
            imgView.setOpacity(1.0);
            imgView.setOnMouseClicked(_ -> {
                handleTileSelection(c);
            });
        } else {
            imgView.setOpacity(0.0);
        }
        return imgView;
    }

    /**
     * Updates the visual representation of a specific component in the ship construction view
     * based on the given component ID. Updates include the component's image, visibility,
     * and behavior when interacted with, depending on its state.
     *
     * The method fetches the component from the current model's state, checks its properties
     * (such as flipped/unflipped state and visibility), and applies corresponding updates
     * to the associated {@code ImageView}.
     *
     * @param id The unique identifier of the component which needs to be updated.
     */
    public void updateComponent(Integer id) {
        ClientComponent component = this.clientModel.getState().getConstructionShipComponents().stream()
                .filter(c -> c.getID() == id).findFirst().orElse(null);

        if (component == null) return;

        ImageView imgView = this.components.get(id);

        if (imgView == null) return;

        Platform.runLater(() -> {
            URL resource;
            if (component.isFlipped()) {
                System.out.println(component.getPath());
                resource = Objects.requireNonNull(getClass().getResource(component.getPath()));
            } else {
                resource = Objects.requireNonNull(getClass().getResource("/imgs/tiles/unflipped.png"));
            }

            Image img = new Image(resource.toExternalForm(), 85, 85, true, true);
            imgView.setImage(img);

            if (component.isVisible()) {
                imgView.setOpacity(1.0);
                imgView.setOnMouseClicked(_ -> handleTileSelection(component));
            } else {
                imgView.setOpacity(0.0);
                imgView.setOnMouseClicked(null);
            }
        });
    }

    // Method used when a tile is selected by the user
    private void handleTileSelection(ClientComponent selectedComponent) {
        // Also add the transition to the new screen
        GUIHandler.setCommandCTX(new CommandCTX(
                "selectTIle",
                () -> {
                    Platform.runLater(() -> {
                        this.selectedComponent = selectedComponent;
                        this.tileScrollPane.setVisible(false);
                        this.tileScrollPane.setManaged(false);

                        this.selectedComponentImage.setImage(
                                this.getImageFromPath(selectedComponent.getPath(), 105, 105)
                        );

                        // Before displaying the dynamic page --> set the tile info etc
                        this.shipContainer.setVisible(true);
                        this.shipContainer.setManaged(true);
                    });
                },
                () -> {}
        ));


        try {
            GUIHandler.getVirtualClient().sendMessage(
                    new SelectTile(
                            this.clientModel.getNickname(),
                            selectedComponent.getID()
                    )
            );
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeselectTile() {
        // Check if the selected component is reserved
        if (this.clientModel.getState().getReservedComponents().contains(this.selectedComponent)) {
            this.showError("You cannot deselect a reserved component!");
            return;
        }

        // TODO: Send the message to the server and return to the other page of the screen
        GUIHandler.setCommandCTX(new CommandCTX(
                "deselectTile",
                () -> {
                    Platform.runLater(() -> {
                        // Before displaying the dynamic page --> set the tile info etc
                        this.shipContainer.setVisible(false);
                        this.shipContainer.setManaged(false);

                        this.tileScrollPane.setVisible(true);
                        this.tileScrollPane.setManaged(true);
                    });
                },
                () -> {}
        ));

        try {
            GUIHandler.getVirtualClient().sendMessage(
                    new DeselectTile(
                            this.clientModel.getNickname(),
                            this.selectedComponent.getID()
                    )
            );
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    @FXML
    private void handleReserveTile() {
        List<ClientComponent> reservedComp = this.clientModel.getState().getReservedComponents();
        if (reservedComp.size() >= 2) {
            this.showError("You cannot reserve more than 2 components!");
            return;
        }

        // Put the tile in the correct spot --> Need to understand how to place the grid in the correct position
        this.clientModel.getState().reserveTile(this.selectedComponent); // Reserve the tile

        ImageView imgView = new ImageView(this.selectedComponentImage.getImage());
        this.reservedComponentGrid.add(imgView, reservedComp.size() - 1, 0);

        // Before displaying the dynamic page --> set the tile info etc
        this.shipContainer.setVisible(false);
        this.shipContainer.setManaged(false);

        this.tileScrollPane.setVisible(true);
        this.tileScrollPane.setManaged(true);
    }

    private void handlePlaceTile(int i, int j) {
        ImageView imgView = new ImageView(this.selectedComponentImage.getImage());
        imgView.setFitWidth(105);
        imgView.setFitHeight(105);
        imgView.setPreserveRatio(true);

        // TODO: Send the message to the server and return to the other page of the screen
        GUIHandler.setCommandCTX(new CommandCTX(
                "placeTile",
                () -> {
                    this.clientModel.getState().getReservedComponents().remove(this.selectedComponent);

                    Platform.runLater(() -> {
                        // Add the component to the grid -- TODO: REMOVE FROM HERE SINCE SHOULD BE TRIGGERED FROM THE VIEW UPDATER
                        this.shipGrid.add(imgView, j, i);

                        // Before displaying the dynamic page --> set the tile info etc
                        this.shipContainer.setVisible(false);
                        this.shipContainer.setManaged(false);

                        this.tileScrollPane.setVisible(true);
                        this.tileScrollPane.setManaged(true);
                    });
                },
                () -> {}
        ));

        try {
            int temp = this.clientModel.getDifficultyLevel() == 0 ? 1 : 0;

            System.out.println("i " + (i + shipOffsets.getKey()));
            System.out.println("j " + (j + shipOffsets.getValue()));

            if (this.clientModel.getDifficultyLevel() == 0) {
                GUIHandler.getVirtualClient().sendMessage(
                        new PlaceTile(
                                this.clientModel.getNickname(),
                                this.selectedComponent.getID(),
                                i + shipOffsets.getKey(),
                                j + shipOffsets.getValue() - 1,
                                this.selectedComponent.getDirection()
                        )
                );
            } else {
                GUIHandler.getVirtualClient().sendMessage(
                        new PlaceTile(
                                this.clientModel.getNickname(),
                                this.selectedComponent.getID(),
                                i + shipOffsets.getKey(),
                                j + shipOffsets.getValue(),
                                this.selectedComponent.getDirection()
                        )
                );
            }
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    private void handlePlayerShipConstruction() {

    }

    @FXML
    private void handleRotateRight() {
        if (rotationInProgress) return;
        rotationInProgress = true;

        this.selectedComponent.rotateRight();

        RotateTransition rotate = new RotateTransition(Duration.millis(200), selectedComponentImage);
        rotate.setByAngle(90);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        rotate.setCycleCount(1);
        rotate.setOnFinished(e -> rotationInProgress = false);
        rotate.play();
    }

    @FXML
    private void handleRotateLeft() {
        if (rotationInProgress) return;
        rotationInProgress = true;

        this.selectedComponent.rotateRight();

        RotateTransition rotate = new RotateTransition(Duration.millis(200), selectedComponentImage);
        rotate.setByAngle(-90);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        rotate.setCycleCount(1);
        rotate.setOnFinished(e -> rotationInProgress = false);
        rotate.play();
    }


    // ========== UTILS METHODS ========== //
    // TODO: Understand if we need to move these methods to the GUIController class to share them
    private Image getImageFromPath(String path, int width, int height) {
        URL resource = Objects.requireNonNull(getClass().getResource(path));
        return new Image(resource.toExternalForm(), width, height, true, true);
    }
}
