package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlacedComponentDTO;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ShipConstructionController extends GUIController {
    private static final int VIEWABLE_SUBDECK_AMOUNT = 3;
    private static final int TIMER_DURATION = 97; // seconds

    // Attributes to handle the others player ship
    @FXML private GridPane viewOtherShipsGrid; // Buttons to see the other players ship

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
    private Timeline timer;

    // ========== FXML ATTRIBUTES ========== //
    @FXML private Button deselectButton;
    @FXML private Button rotateRightButton;
    @FXML private Button rotateLeftButton;
    @FXML private Button reserveButton;
    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private GridPane reservedComponentGrid;
    @FXML private Button confirmShipButton;

    @FXML private FlowPane tileFlow;
    @FXML private VBox sidePanel;
    @FXML private ImageView subDeckOne;
    @FXML private ImageView subDeckTwo;
    @FXML private ImageView subDeckThree;
    @FXML private ImageView boardImageView;

    // MAIN SECTIONS --> USED TO DISPLAY THE CONTENT DYNAMICALLY
    @FXML private StackPane contentContainer;
    @FXML private ScrollPane tileScrollPane;
    @FXML private VBox shipContainer;
    @FXML private VBox viewShipContainer;
    @FXML private VBox viewGameBoardContainer;
    @FXML private VBox subdeckViewerContainer;

    // View other player ship attributes
    @FXML private Label viewPlayerShipLabel;
    @FXML private StackPane viewOtherShipStackPane;
    @FXML private ImageView viewOtherShipImage;

    // Subdeck Visualization
    @FXML private Label deselectSubdeckLabel;
    @FXML private Button deselectSubdeckButton;
    @FXML private HBox allSubdeckCardsContainer;

    @FXML private Button goBackToConstructionButton;

    private int selectedSubdeckId;
    private List<ImageView> subdeckImages;
    private List<Node> subdeckCards;

    // Attributes needed when the player has finished his ship
    private boolean hasFinishedShip = false;


    // ========== GAME ATTRIBUTES ========== //
    // Map used to target a specific component when an update arrives
    private final Map<Integer, ImageView> components = new HashMap<>();

    // Store the selected component to dynamically display the view
    private ClientComponent selectedComponent;

    // Method used to initialize the page information that needs to be displayed
    public void initShipConstruction() {
        // TODO: Init the players ships --> Useful to update the specific client ship in real time
        this.clientModel = GUIHandler.getClientModel();

        this.guiUtils = new GUIUtils(this.clientModel);

        // INIT THE NAVBAR
        this.initSidePanel();

        // Init the component
        this.initComponents();

        // Init the ship dynamic page
        this.initShipPage();

        // Init each subdeck
        this.initSubdecks();
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
        }
        else {
            this.timerContainer.setVisible(false);
        }

        this.populateViewShipButtons();
    }

    private void populateViewShipButtons() {
        // Clear all the nodes
        this.viewOtherShipsGrid.getChildren().clear();

        // Default positions
        int[][] positions = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };

        List<ClientPlayer> players = this.clientModel.getAllClientPlayers().values().stream().filter(p -> !p.getNickname().equals(this.clientModel.getNickname())).toList();
        for (int i = 0; i < players.size(); i++) {
            String name = players.get(i).getNickname();
            int col = positions[i][0];
            int row = positions[i][1];

            Button playerButton = new Button(name);
            playerButton.setMaxWidth(Double.MAX_VALUE);
            // playerButton.setMaxHeight(Double.MAX_VALUE);

            playerButton.setOnAction((_) -> handleViewShipRequest(name));

            this.viewOtherShipsGrid.add(playerButton, col, row);
        }
    }

    public void startCountDownTimer() {
        AtomicInteger countdown = new AtomicInteger(TIMER_DURATION);

        // Check if there is already an active timer
        if (this.timer != null) {
            this.timer.stop();
        }

        // Update the text every second
        this.timer = new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                _ -> {
                    if (countdown.get() <= 0) {
                        this.timer.stop();
                        return;
                    }

                    int minutes = countdown.get() / 60;
                    int seconds = countdown.get() % 60;

                    String timeFormatted = String.format("Flip available in %02d:%02d", minutes, seconds);
                    this.timerLabel.setText(timeFormatted);

                    countdown.getAndDecrement();
                }
            )
        );

        this.flipTimerButton.setDisable(true);
        this.timerLabel.setWrapText(true);

        this.timer.setCycleCount(Timeline.INDEFINITE);
        this.timer.play();
    }

    private void initShipPage() {
        String shipPath = "/imgs/cardboard/level_" + this.clientModel.getDifficultyLevel() + ".jpg";
        URL shipResource = Objects.requireNonNull(getClass().getResource(shipPath));

        String boardPath = "/imgs/cardboard/board_level_" + this.clientModel.getDifficultyLevel() + ".png";
        URL boardResource = Objects.requireNonNull(getClass().getResource(boardPath));

        // Set the image of the current level Ship
        this.shipImageView.setImage(new Image(shipResource.toExternalForm()));
        this.shipImageView.setFitWidth(816.0);
        this.shipImageView.setPreserveRatio(true);

        this.viewOtherShipImage.setImage(new Image(shipResource.toExternalForm()));
        this.viewOtherShipImage.setFitWidth(816.0);
        this.viewOtherShipImage.setPreserveRatio(true);

        this.boardImageView.setImage(new Image(boardResource.toExternalForm()));
        this.boardImageView.setFitWidth(816.0);
        this.boardImageView.setPreserveRatio(true);


        Pair<Integer, Integer> shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
        this.shipOffsets = shipOffsets;
        Pair<Integer, Integer> shipDimensions = AbstractShip.shipDimensions.get(this.clientModel.getDifficultyLevel());
        int[][] shipProfiles = AbstractShip.shipProfiles.get(this.clientModel.getDifficultyLevel());

        int endRow = shipDimensions.getKey() + shipOffsets.getKey();
        int endCol = shipDimensions.getValue() + shipOffsets.getValue();

        for (int row = shipOffsets.getKey(); row < endRow; row++) {
            for (int col = shipOffsets.getValue(); col < endCol; col++) {
                if (shipProfiles[row][col] == 1) {
                    this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue());
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
            // Create the new grid for each player different from the currentPlayer
            if (!this.clientModel.getNickname().equals(p.getNickname())) {
                this.playersShipGridPane.put(p.getNickname(), this.guiUtils.createEmptyShipGrid(p));
            }
        }
    }

    private void initSubdecks() {
        this.subdeckImages = new ArrayList<>();

        this.subdeckImages.add(this.subDeckOne);
        this.subdeckImages.add(this.subDeckTwo);
        this.subdeckImages.add(this.subDeckThree);

        this.allSubdeckCardsContainer.getChildren().clear();

        Platform.runLater(() -> {
            List<ClientEventCard> allCards = this.clientModel.getClientEventCards();
            int subdeckSize = allCards.size() / 4;
            int id = 0;

            allCards = allCards.subList(0, subdeckSize * VIEWABLE_SUBDECK_AMOUNT);

            for (ClientEventCard card : allCards) {
                try {
                    String cardImageURL =
                            Objects.requireNonNull(
                                    getClass().getResource(card.getCardPath())
                            ).toExternalForm();

                    Image cardImage = new Image(cardImageURL, 600, 300, true, true);
                    ImageView cardImageView = new ImageView(cardImage);

                    cardImageView.setId(Integer.toString(id));
                    cardImageView.setPreserveRatio(true);
                    id++;

                    this.setVisibility(cardImageView, false);
                    this.allSubdeckCardsContainer.getChildren().add(cardImageView);
                }
                catch (Exception e) {
                    System.out.println("CARD: " + card + " -> Missing path: " + card.getCardPath());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method used to add the event listener to the clickable cells of the player ship
     * */
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
        cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);
        cell.setOnMouseClicked(_ -> handlePlaceTile(row, col));

        this.shipGrid.add(cell, col, row);
    }

    private void initializePlayersShip() {
        // For each player init the ship view
        for (ClientPlayer p : this.clientModel.getAllClientPlayers().values()) {

        }
    }

    @FXML
    private void handleViewShipRequest(String requestedPlayerShip) {
        // Remove from the screen the main content and display the request ship
        this.setVisibility(this.shipContainer, false);
        this.setVisibility(this.tileScrollPane, false);
        this.setVisibility(this.viewGameBoardContainer, false);
        this.setVisibility(this.subdeckViewerContainer, false);

        if (this.subdeckCards != null && this.selectedSubdeckId != -1) {
            this.handleDeselectSubdeck();
        }

        // Set the label text dynamically
        this.viewPlayerShipLabel.setText("You are now viewing "+ requestedPlayerShip +"'s ship");

        // Remove the current grid
        this.viewOtherShipStackPane.getChildren().removeIf(node -> node instanceof GridPane);
        // Add the player grid
        GridPane newGrid = this.playersShipGridPane.get(requestedPlayerShip);
        StackPane.setAlignment(newGrid, Pos.CENTER);
        this.viewOtherShipStackPane.getChildren().add(newGrid);

        if (this.hasFinishedShip) {
            this.goBackToConstructionButton.setText("Go back");
            this.goBackToConstructionButton.setVisible(true);
        }

        this.setVisibility(this.viewShipContainer, true);
    }

    @FXML
    // Method used to return to the main screen when the player is viewing other ship
    private void handleGoBackToConstructionButton() {
        if (this.hasFinishedShip) {
            this.showEndedShipConstruction();
        }
        else {
            this.setVisibility(this.shipContainer, false);
            this.setVisibility(this.viewShipContainer, false);
            this.setVisibility(this.viewGameBoardContainer, false);
            this.setVisibility(this.subdeckViewerContainer, false);

            if (this.subdeckCards != null && this.selectedSubdeckId != -1) {
                this.handleDeselectSubdeck();
            }

            this.setVisibility(this.tileScrollPane, true);
        }
    }

    @FXML
    private void handleFlipTimer() {
        GUIHandler.setCommandCTX(
            new CommandCTX(
                "flipTimer",
                this::startCountDownTimer,
                () -> {}
            )
        );

        try {
            GUIHandler.getVirtualClient().sendMessage(
                new FlipTimer(this.clientModel.getNickname())
            );
        }
        catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    // Send the player ship to the server
    @FXML public void handleConfirmShip() {
        GUIHandler.setCommandCTX(new CommandCTX(
                "sendShip",
                () -> {
                    this.hasFinishedShip = true;

//                    // Sets the timer button as enabled
//                    if (this.flipTimerButton.isDisabled()) {
//                        this.resetTimer();
//                    }

                    Platform.runLater(this::showEndedShipConstruction);
                },
                this::handleConfirmShip
        ));

        try {
            GUIHandler.getVirtualClient().sendMessage(
                    new SendShipConfirmation(
                            this.clientModel.getNickname(),
                            this.clientModel.getState().getReservedComponents().size()
                    )
            );
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    private void showEndedShipConstruction() {
        this.setVisibility(this.shipContainer, false);
        this.setVisibility(this.tileScrollPane, false);
        this.setVisibility(this.viewGameBoardContainer, false);
        this.setVisibility(this.subdeckViewerContainer, false);

        if (this.subdeckCards != null && this.selectedSubdeckId != -1) {
            this.handleDeselectSubdeck();
        }

        // Set the label text dynamically
        this.viewPlayerShipLabel.setText("You have finished building your ship");
        this.goBackToConstructionButton.setVisible(false);
        this.confirmShipButton.setVisible(false);

        // Remove the current grid
        this.viewOtherShipStackPane.getChildren().removeIf(node -> node instanceof GridPane);

        // Add the player grid
        GridPane newGrid = this.playersShipGridPane.get(this.clientModel.getNickname());
        StackPane.setAlignment(newGrid, Pos.CENTER);
        this.viewOtherShipStackPane.getChildren().add(newGrid);

        this.setVisibility(this.viewShipContainer, true);
    }

    @FXML
    void handleViewSubDeck(MouseEvent event) {
        ImageView clickedSubdeck;
        int subdeckIndex;

        clickedSubdeck = (ImageView) event.getSource();
        subdeckIndex = 0;

        for (ImageView subdeckImg : this.subdeckImages) {
            if (clickedSubdeck.equals(subdeckImg)) break;
            subdeckIndex++;
        }

        if (subdeckIndex == this.subdeckImages.size()) return;
        this.selectedSubdeckId = subdeckIndex;

        GUIHandler.setCommandCTX(
            new CommandCTX(
                "selectSubdeck",
                () -> {
                    int subdeckSize = this.clientModel.getClientEventCards().size() / 4;
                    int start = (this.selectedSubdeckId * subdeckSize);
                    int end = (start + subdeckSize);

                    this.subdeckCards = this.allSubdeckCardsContainer.getChildren().subList(start, end);

                    Platform.runLater(() -> {
                        for (ImageView subdeckImg : this.subdeckImages) {
                            subdeckImg.setDisable(true);
                            subdeckImg.setOpacity(0.5);
                        }

                        for (Node n : this.contentContainer.getChildren()) {
                            this.setVisibility(n, false);
                        }

                        for (Node n : subdeckCards) {
                            this.setVisibility(n, true);
                        }

                        this.deselectSubdeckLabel.setText("You are now viewing subdeck #" + (this.selectedSubdeckId + 1));

                        this.setVisibility(this.subdeckViewerContainer, true);
                        this.subdeckViewerContainer.setAlignment(Pos.CENTER);

                        this.setVisibility(this.deselectSubdeckLabel, true);
                        this.setVisibility(this.deselectSubdeckButton, true);
                        this.setVisibility(this.allSubdeckCardsContainer, true);
                    });
                },
                () -> {}
            )
        );

        try {
            GUIHandler.getVirtualClient().sendMessage(
                new SelectDeselectSubdeck(
                    this.clientModel.getNickname(),
                    this.selectedSubdeckId,
                    true
                )
            );
        }
        catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    public void handleDeselectSubdeck() {
        GUIHandler.setCommandCTX(
            new CommandCTX(
                "deselectSubdeck",
                () -> {
                    Platform.runLater(() -> {
                        for (ImageView subdeckImg : this.subdeckImages) {
                            subdeckImg.setDisable(false);
                            subdeckImg.setOpacity(1);
                        }

                        for (Node n : this.subdeckCards) {
                            this.setVisibility(n, false);
                        }

                        this.subdeckCards = null;
                        this.selectedSubdeckId = -1;

                        this.setVisibility(this.subdeckViewerContainer, false);
                    });
                },
                () -> {}
            )
        );

        try {
            GUIHandler.getVirtualClient().sendMessage(
                new SelectDeselectSubdeck(
                    this.clientModel.getNickname(),
                    this.selectedSubdeckId,
                    false
                )
            );
        }
        catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    // Method used when a tile is selected by the user
    private void handleTileSelection(ClientComponent selectedComponent) {
        // Also add the transition to the new screen
        GUIHandler.setCommandCTX(new CommandCTX(
                "selectTile",
                () -> {
                    Platform.runLater(() -> {
                        this.selectedComponent = selectedComponent;
                        this.setVisibility(this.tileScrollPane, false);
                        this.setVisibility(this.viewShipContainer, false);
                        this.setVisibility(this.viewGameBoardContainer, false);
                        this.setVisibility(this.subdeckViewerContainer, false);

                        this.selectedComponentImage.setImage(
                                this.getImageFromPath(selectedComponent.getPath(), 105, 105)
                        );

                        // Before displaying the dynamic page --> set the tile info etc
                        this.setVisibility(this.shipContainer, true);
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
                        this.setVisibility(this.shipContainer, false);
                        this.setVisibility(this.viewShipContainer, false);
                        this.setVisibility(this.viewGameBoardContainer, false);
                        this.setVisibility(this.subdeckViewerContainer, false);

                        // Before displaying the dynamic page --> set the tile info etc
                        this.setVisibility(this.tileScrollPane, true);
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
        this.setVisibility(this.shipContainer, false);
        this.setVisibility(this.viewShipContainer, false);
        this.setVisibility(this.viewGameBoardContainer, false);
        this.setVisibility(this.subdeckViewerContainer, false);

        // Before displaying the dynamic page --> set the tile info etc
        this.setVisibility(this.tileScrollPane, true);
    }

    @FXML
    private void handlePlaceTile(int i, int j) {
        ImageView imgView = new ImageView(this.selectedComponentImage.getImage());
        imgView.setFitWidth(105);
        imgView.setFitHeight(105);
        imgView.setRotate(this.selectedComponentImage.getRotate());
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);

        // TODO: Send the message to the server and return to the other page of the screen
        GUIHandler.setCommandCTX(new CommandCTX(
                "placeTile",
                () -> {
                    this.clientModel.getState().getReservedComponents().remove(this.selectedComponent);

                    Platform.runLater(() -> {
                        // Before displaying the dynamic page --> set the tile info etc
                        this.setVisibility(this.shipContainer, false);
                        this.setVisibility(this.viewShipContainer, false);
                        this.setVisibility(this.viewGameBoardContainer, false);
                        this.setVisibility(this.subdeckViewerContainer, false);

                        this.selectedComponentImage.setRotate(0.0);

                        // Before displaying the dynamic page --> set the tile info etc
                        this.setVisibility(this.tileScrollPane, true);
                    });
                },
                () -> {}
        ));

        try {
            GUIHandler.getVirtualClient().sendMessage(
                new PlaceTile(
                    this.clientModel.getNickname(),
                    this.selectedComponent.getID(),
                    i + shipOffsets.getKey(),
                    j + shipOffsets.getValue(),
                    this.selectedComponent.getDirection()
                )
            );
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
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

        this.selectedComponent.rotateLeft();

        RotateTransition rotate = new RotateTransition(Duration.millis(200), selectedComponentImage);
        rotate.setByAngle(-90);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        rotate.setCycleCount(1);
        rotate.setOnFinished(e -> rotationInProgress = false);
        rotate.play();
    }

    // ========== UTILS METHODS ========== //
    // Method used to set the visibility of a certain node
    private <T extends Node> void setVisibility(T node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    // TODO: Understand if we need to move these methods to the GUIController class to share them
    private Image getImageFromPath(String path, int width, int height) {
        URL resource = Objects.requireNonNull(getClass().getResource(path));
        return new Image(resource.toExternalForm(), width, height, true, true);
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

    // ===== METHOD USED BY THE VIEW UPDATER TO UPDATE THE VIEW IN REAL TIME ===== //

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

    /**
     * Method used to update the player ship
     * */
    public void handlePlayerShipConstruction(PlacedComponentDTO data) {
        // Get the player gridPane
        GridPane playerGrid = this.playersShipGridPane.get(data.getPlayerNickname());
        if (playerGrid == null) {
            this.showError("No gridPane found for the given player");
            return;
        }

        // Load the component, get the image and save it to the grid in the correct position with the correct rotation
        this.clientModel.getState().getConstructionShipComponents().stream().filter(c -> c.getID() == data.getId()).findFirst().ifPresent(c -> {
            // Build the ImageView with the component image
            URL resource = getClass().getResource(c.getPath());
            if (resource == null) {
                this.showError("Component image not found: " + c.getPath());
                return;
            }

            Image img = new Image(resource.toExternalForm(), 85, 85, true, true);
            ImageView imgView = new ImageView(img);
            imgView.setRotate(data.getRotation() * 90.0);
            imgView.setFitWidth(105);
            imgView.setFitHeight(105);
            imgView.setPreserveRatio(true);
            imgView.setSmooth(true);

            // Add the image to the player board
            Platform.runLater(() -> {
                playerGrid.add(imgView, data.getJ() - this.shipOffsets.getValue(), data.getI() - shipOffsets.getKey());
            });
        });
    }

    public void resetTimer() {
        Platform.runLater(() -> {
            this.flipTimerButton.setDisable(false);
        });
    }
}
