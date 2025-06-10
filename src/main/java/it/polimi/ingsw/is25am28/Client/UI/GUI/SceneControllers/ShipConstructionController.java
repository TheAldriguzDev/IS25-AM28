package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.GUI.Utils.GUIUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlacedComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlayerEndedShipDTO;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import java.util.List;

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
    // @FXML private Label timerLabel;
    @FXML private Button flipTimerButton;
    @FXML private HBox timerContainer;
    private Timeline timer;

    // ========== FXML ATTRIBUTES ========== //
    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private GridPane reservedComponentGrid;
    @FXML private Button confirmShipButton;
    @FXML private Button fastShipButton;

    @FXML private FlowPane tileFlow;

    @FXML private VBox reservedVBOX;
    @FXML private FlowPane reservedTileFlow;

    @FXML private ImageView subDeckOne;
    @FXML private ImageView subDeckTwo;
    @FXML private ImageView subDeckThree;
    @FXML private ImageView boardImageView;

    // MAIN SECTIONS --> USED TO DISPLAY THE CONTENT DYNAMICALLY
    @FXML private StackPane contentContainer;
    @FXML private VBox tileVBOX;
    @FXML private ScrollPane tilesScrollPane;;
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
    @FXML private Pane viewGameBoardStackPaneLevel2;
    @FXML private Pane viewGameBoardStackPaneLevel0;

    @FXML private Button goBackToConstructionButton;
    @FXML private Button goBackToConstructionButtonFromViewBoard;

    private int selectedSubdeckId;
    private List<ImageView> subdeckImages;
    private List<Node> subdeckCards;

    // Attributes needed when the player has finished his ship
    private boolean hasFinishedShip = false;

    private Map<String, ImageView> playersRocketBoard = new HashMap<>();

    // ========== GAME ATTRIBUTES ========== //
    // Map used to target a specific component when an update arrives
    private final Map<Integer, ImageView> components = new HashMap<>();

    // Store the selected component to dynamically display the view
    private ClientComponent selectedComponent;
    private boolean isSelectedTileReserved;

    // Method used to initialize the page information that needs to be displayed
    public void initShipConstruction() {
        // TODO: Init the players ships --> Useful to update the specific client ship in real time
        this.clientModel = GUIHandler.getClientModel();

        this.guiUtils = new GUIUtils(this.clientModel);

        this.selectedComponent = null;
        this.isSelectedTileReserved = false;

        if (this.clientModel.getTimerDTO() == null) {
            this.disableTimerButton();
        }

        // INIT THE NAVBAR
        this.initSidePanel();

        // Init the component
        this.initComponents();

        // Init the ship dynamic page
        this.initShipPage();

        // Init each subdeck
        this.initSubdecks();

        // Init the board
        this.guiUtils.initViewGameBoard(
            this.viewGameBoardStackPaneLevel0,
            this.viewGameBoardStackPaneLevel2,
            this.boardImageView,
            this.playersRocketBoard
        );

        // The player has already sent his ship --> We need to modify the view
        if (this.clientModel.getState().getPlayerFinishedBuildingShip(this.clientModel.getNickname())) {
            this.hasFinishedShip = true;
            this.showEndedShipConstruction();
        }

        this.updateReservedComponents(); // Will init the reserved component in case of reconnection

        // Check if we need to display the reserved components container
        this.setVisibility(this.reservedVBOX, !this.clientModel.getState().getReservedComponents().isEmpty());
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
            imgView.setFitHeight(85);
            imgView.setFitWidth(85);
            imgView.setSmooth(true);
            imgView.setPreserveRatio(true);
            imgView.setPickOnBounds(true);

            this.components.put(c.getID(), imgView);
            tileFlow.getChildren().add(imgView);
        });
    }

    private void initSidePanel() {
        // Display and start the timer container only if the game level is != 0
        if (this.clientModel.getDifficultyLevel() != 0) {
            this.timerContainer.setVisible(true);
            // this.startCountDownTimer();
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

    private void initShipPage() {
        String shipPath = "/imgs/cardboard/level_" + this.clientModel.getDifficultyLevel() + ".jpg";
        URL shipResource = Objects.requireNonNull(getClass().getResource(shipPath));

        // Set the image of the current level Ship
        this.shipImageView.setImage(new Image(shipResource.toExternalForm()));
        this.shipImageView.setFitWidth(816.0);
        this.shipImageView.setPreserveRatio(true);

        this.viewOtherShipImage.setImage(new Image(shipResource.toExternalForm()));
        this.viewOtherShipImage.setFitWidth(816.0);
        this.viewOtherShipImage.setPreserveRatio(true);


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

        this.playersShipGridPane.put(this.clientModel.getNickname(), this.shipGrid);

        // Init the gridShipPane for each player
        for (ClientPlayer p : this.clientModel.getAllClientPlayers().values()) {
            String playerNickname = p.getNickname();

            // Create the new grid for each player different from the currentPlayer
            if (!this.clientModel.getNickname().equals(playerNickname)) {
                this.playersShipGridPane.put(playerNickname, this.guiUtils.createEmptyShipGrid(p));
            }

            GridPane playerGrid = this.playersShipGridPane.get(playerNickname);

            // Create, for each player, the graphic ship
            this.guiUtils.createShipVisuals(playerNickname, playerGrid);
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
            imgView.setFitHeight(105);
            imgView.setFitWidth(105);
            imgView.setPickOnBounds(true);
            imgView.setSmooth(true);
            imgView.setPreserveRatio(true);

            imgView.setImage(img);

            this.shipGrid.add(imgView, col, row);

            return;
        }

        Region cell = new Region(); // Place holder node

        cell.setPrefSize(105, 105);
        cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);
        cell.setOnMouseClicked(_ -> handlePlaceTile(row, col));

        this.shipGrid.add(cell, col, row);
    }

    @FXML
    private void handleViewShipRequest(String requestedPlayerShip) {
        // If we have a selected component, we need to deselect it first
        if (this.selectedComponent != null) {
            this.deselectTileCommand(() -> {
                this.selectedComponentImage.setRotate(0.0);
                this.selectedComponent.setRotation(0);
                this.selectedComponent = null;
                this.isSelectedTileReserved = false;
                this.selectedComponentImage.setImage(null);

                this.handleViewShipRequest(requestedPlayerShip);
            });

            return;
        }


        // Remove from the screen the main content and display the request ship
        this.setVisibility(this.shipContainer, false);
        this.setVisibility(this.tileVBOX, false);
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

            this.setVisibility(this.tileVBOX, true);
        }
    }

    @FXML
    private void handleFlipTimer() {
        GUIHandler.setCommandCTX(
//            new CommandCTX(
//                "flipTimer",
//                this::startCountDownTimer,
//                () -> {}
//            )
            new CommandCTX(
                "flipTimer",
                this::disableTimerButton,
                () -> {

                }
            )
        );

        try {
            GUIHandler.getVirtualClient().flipTimer(
                this.clientModel.getNickname()
            );
        }
        catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    // Send the player ship to the server
    @FXML public void handleConfirmShip() {
        if (!this.clientModel.getState().getPlayerFinishedBuildingShip(this.clientModel.getNickname())) {
            GUIHandler.setCommandCTX(new CommandCTX(
                    "sendShip",
                    () -> {
                        this.hasFinishedShip = true;

                        Platform.runLater(this::showEndedShipConstruction);
                    },
                    () -> {}
            ));

            try {
                GUIHandler.getVirtualClient().sendShipConfirmation(
                        this.clientModel.getNickname(),
                        this.clientModel.getState().getReservedComponents().size()
                );
            } catch (Exception e) {
                this.showToast(e.getMessage(), ToastType.ERROR);
            }
        }
    }

    private void showEndedShipConstruction() {
        this.setVisibility(this.shipContainer, false);
        this.setVisibility(this.tileVBOX, false);
        this.setVisibility(this.viewGameBoardContainer, false);
        this.setVisibility(this.subdeckViewerContainer, false);

        if (this.subdeckCards != null && this.selectedSubdeckId != -1) {
            this.handleDeselectSubdeck();
        }

        // Set the label text dynamically
        this.viewPlayerShipLabel.setText("You have finished building your ship");
        this.goBackToConstructionButton.setVisible(false);
        this.confirmShipButton.setVisible(false);
        this.fastShipButton.setVisible(false);

        // Remove the current grid
        this.viewOtherShipStackPane.getChildren().removeIf(node -> node instanceof GridPane);

        // Add the player grid
        GridPane newGrid = this.playersShipGridPane.get(this.clientModel.getNickname());
        StackPane.setAlignment(newGrid, Pos.CENTER);
        this.viewOtherShipStackPane.getChildren().add(newGrid);

        this.setVisibility(this.viewShipContainer, true);
    }

    @FXML
    private void handleViewSubDeck(MouseEvent event) {
        // If we have a selected component, we need to deselect it first
        if (this.selectedComponent != null) {
            this.deselectTileCommand(() -> {
                this.selectedComponentImage.setRotate(0.0);
                this.selectedComponent.setRotation(0);
                this.selectedComponent = null;
                this.isSelectedTileReserved = false;
                this.selectedComponentImage.setImage(null);

                this.handleViewSubDeck(event);
            });

            return;
        }


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
            GUIHandler.getVirtualClient().selectDeselectSubdeck(
                this.clientModel.getNickname(),
                this.selectedSubdeckId,
                true
            );
        }
        catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    private void handleDeselectSubdeck() {
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
            GUIHandler.getVirtualClient().selectDeselectSubdeck(
                this.clientModel.getNickname(),
                this.selectedSubdeckId,
                false
            );
        }
        catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    // Method used when a tile is selected by the user
    private void handleTileSelection(ClientComponent selectedComponent) {

        if (this.clientModel.getState().getReservedComponents().contains(selectedComponent)) {
            this.displaySelectedComponent(selectedComponent);
        } else {
            // Also add the transition to the new screen
            GUIHandler.setCommandCTX(new CommandCTX(
                    "selectTile",
                    () -> {
                        this.displaySelectedComponent(selectedComponent);
                    },
                    () -> {}
            ));

            try {
                GUIHandler.getVirtualClient().selectTile(
                        this.clientModel.getNickname(),
                        selectedComponent.getID()
                );
            } catch (Exception e) {
                this.showToast(e.getMessage(), ToastType.ERROR);
            }
        }
    }

    private void displaySelectedComponent(ClientComponent selectedComponent) {
        Platform.runLater(() -> {
            this.selectedComponent = selectedComponent;
            this.setVisibility(this.tileVBOX, false);
            this.setVisibility(this.viewShipContainer, false);
            this.setVisibility(this.viewGameBoardContainer, false);
            this.setVisibility(this.subdeckViewerContainer, false);

            this.selectedComponentImage.setImage(
                    this.getImageFromPath(selectedComponent.getPath(), 105, 105)
            );

            // Before displaying the dynamic page --> set the tile info etc
            this.setVisibility(this.shipContainer, true);
        });
    }

    @FXML
    private void handleDeselectTile() {
        // Check if the selected component is reserved
        if (this.clientModel.getState().getReservedComponents().contains(this.selectedComponent)) {
            this.showToast("You cannot deselect a reserved component!", ToastType.ERROR);
            return;
        }

        this.deselectTileCommand(() -> {
            // Before displaying the dynamic page --> set the tile info etc
            this.setVisibility(this.shipContainer, false);
            this.setVisibility(this.viewShipContainer, false);
            this.setVisibility(this.viewGameBoardContainer, false);
            this.setVisibility(this.subdeckViewerContainer, false);

            this.selectedComponentImage.setRotate(0.0);
            this.selectedComponent.setRotation(0);
            this.selectedComponent = null;
            this.isSelectedTileReserved = false;
            this.selectedComponentImage.setImage(null);

            // Before displaying the dynamic page --> set the tile info etc
            this.setVisibility(this.tileVBOX, true);
        });
    }

    private void deselectTileCommand(Runnable task) {
        GUIHandler.setCommandCTX(
            new CommandCTX(
                "deselectTile",
                () -> {
                    Platform.runLater(task);
                },
                () -> {}
            )
        );

        try {
            if (this.isSelectedTileReserved) {
                task.run();
            }
            else {
                GUIHandler.getVirtualClient().deselectTile(
                        this.clientModel.getNickname(),
                        this.selectedComponent.getID()
                );
            }
        } catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    @FXML
    private void handleReserveTile() {
        List<ClientComponent> reservedComp = this.clientModel.getState().getReservedComponents();
        if (!this.clientModel.getState().getReservedComponents().contains(this.selectedComponent) && reservedComp.size() >= 2) {
            this.showToast("You cannot reserve more than 2 components!", ToastType.ERROR);
            return;
        }

        Runnable task = () -> {
            // Hide all the other containers
            this.setVisibility(this.shipContainer, false);
            this.setVisibility(this.viewShipContainer, false);
            this.setVisibility(this.viewGameBoardContainer, false);
            this.setVisibility(this.subdeckViewerContainer, false);

            this.isSelectedTileReserved = true;

            // update the reserved visual elements
            this.updateReservedComponents();
            this.setVisibility(this.tileVBOX, true);
        };

        if (this.clientModel.getState().getReservedComponents().contains(this.selectedComponent)) {
            Platform.runLater(task);
            return;
        }

        GUIHandler.setCommandCTX(new CommandCTX(
                "reserveTile",
                () -> {
                    Platform.runLater(task);
                },
                () -> {}
        ));

        try {
            GUIHandler.getVirtualClient().reserveTile(
                    this.clientModel.getNickname(),
                    this.selectedComponent.getID()
            );
        } catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
        }
    }

    /**
     * This method is used to request a fast ship configuration.
     * The successCallback will mark the player as finished and will display the ended configuration screen
     * */
    @FXML void requestFastShip() {
        if (!this.clientModel.getState().getPlayerFinishedBuildingShip(this.clientModel.getNickname())) {
            GUIHandler.setCommandCTX(new CommandCTX(
                    "sendShip",
                    () -> {
                        this.hasFinishedShip = true;

                        Platform.runLater(this::showEndedShipConstruction);
                    },
                    () -> {}
            ));

            try {
                GUIHandler.getVirtualClient().fastShip(
                        this.clientModel.getNickname()
                );
            } catch (Exception e) {
                this.showToast(e.getMessage(), ToastType.ERROR);
            }
        }
    }

    @FXML
    private void handlePlaceTile(int i, int j) {
        URL resource = getClass().getResource(this.selectedComponent.getPath());
        if (resource == null) {
            this.showToast("Component image not found: " + this.selectedComponent.getPath(), ToastType.ERROR);
            return;
        }

        Image img = new Image(resource.toExternalForm(), 105, 105, true, true);
        ImageView imgView = new ImageView(img);
        imgView.setRotate(this.selectedComponent.getDirection() * 90.0);
        imgView.setFitWidth(105);
        imgView.setFitHeight(105);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);

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

                        // Reset the rotations
                        this.selectedComponentImage.setRotate(0.0);
                        this.selectedComponent = null;
                        this.isSelectedTileReserved = false;
                        this.selectedComponentImage.setImage(null);

                        // Before displaying the dynamic page --> set the tile info for both the normal and the reserved ones
                        this.updateReservedComponents();
                        this.setVisibility(this.tileVBOX, true);
                    });
                },
                () -> {}
        ));

        try {
            GUIHandler.getVirtualClient().placeTile(
                this.clientModel.getNickname(),
                this.selectedComponent.getID(),
                i + shipOffsets.getKey(),
                j + shipOffsets.getValue(),
                this.selectedComponent.getDirection()
            );
        } catch (Exception e) {
            this.showToast(e.getMessage(), ToastType.ERROR);
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

    @FXML
    // Method used to display the current game board
    private void handleViewGameBoard() {
        // If we have a selected component, we need to deselect it first
        if (this.selectedComponent != null) {
            this.deselectTileCommand(() -> {
                this.selectedComponentImage.setRotate(0.0);
                this.selectedComponent.setRotation(0);
                this.selectedComponent = null;
                this.isSelectedTileReserved = false;
                this.selectedComponentImage.setImage(null);

                this.handleViewGameBoard();
            });

            return;
        }

        // Disable all the previous containers
        this.setVisibility(this.tileVBOX, false);
        this.setVisibility(this.viewShipContainer, false);
        this.setVisibility(this.subdeckViewerContainer, false);
        this.setVisibility(this.shipContainer, false);

        if (this.hasFinishedShip) {
            this.goBackToConstructionButtonFromViewBoard.setText("Go back");
        }

        // Enable the board container
        this.setVisibility(this.viewGameBoardContainer, true);
    }

    // ========== UTILS METHODS ========== //

    // TODO: Understand if we need to move these methods to the GUIController class to share them
    private Image getImageFromPath(String path, int width, int height) {
        URL resource = Objects.requireNonNull(getClass().getResource(path));
        return new Image(resource.toExternalForm(), width, height, true, true);
    }

    private ImageView getComponentImageView(ClientComponent c, Image img) {
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(85);
        imgView.setFitHeight(85);
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

    private void updateReservedComponents() {
        this.reservedTileFlow.getChildren().clear();
        this.reservedComponentGrid.getChildren().clear();

        List<ClientComponent> updatedReserved = this.clientModel.getState().getReservedComponents();
        for (int i = 0; i < updatedReserved.size(); i++) {
            ClientComponent c = updatedReserved.get(i);

            // FlowPane
            ImageView view = buildComponentImageView(c);
            this.components.put(c.getID(), view);
            reservedTileFlow.getChildren().add(view);

            // GridPane
            URL resource = Objects.requireNonNull(getClass().getResource(c.getPath()));
            Image img = new Image(resource.toExternalForm(), 105, 105, true, true);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(105);
            imgView.setFitHeight(105);
            imgView.setPreserveRatio(true);
            imgView.setSmooth(true);
            reservedComponentGrid.add(imgView, i, 0);
        }

        this.setVisibility(this.reservedVBOX, !this.clientModel.getState().getReservedComponents().isEmpty());
    }

    private ImageView buildComponentImageView(ClientComponent c) {
        int size = this.clientModel.getState().getReservedComponents().contains(c) ? 85 : 105;

        URL resource = Objects.requireNonNull(getClass().getResource(c.getPath()));
        Image img = new Image(resource.toExternalForm(), size, size, true, true);
        ImageView imgView = new ImageView(img);
        imgView.setRotate(c.getDirection() * 90.0);
        imgView.setFitWidth(size);
        imgView.setFitHeight(size);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setPickOnBounds(true);
        imgView.setCursor(Cursor.HAND);
        imgView.setOnMouseClicked(_ -> handleTileSelection(c));
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
            imgView.setSmooth(true);
            imgView.setPreserveRatio(true);

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
            this.showToast("No gridPane has been found for the given player", ToastType.ERROR);
            return;
        }

        // Load the component, get the image and save it to the grid in the correct position with the correct rotation
        this.clientModel.getState().getConstructionShipComponents().stream().filter(c -> c.getID() == data.getId()).findFirst().ifPresent(c -> {
            // Build the ImageView with the component image
            URL resource = getClass().getResource(c.getPath());
            if (resource == null) {
                this.showToast("Component image not found: " + c.getPath(), ToastType.ERROR);
                return;
            }

            Image img = new Image(resource.toExternalForm(), 105, 105, true, true);
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

    /**
     * This method will be used to re-create the player ship visual when a fast ship is requested
     * */
    public void handlePlayerFastShip(String playerNickname) {
        GridPane playerGrid = this.playersShipGridPane.get(playerNickname);
        if (playerGrid == null) {
            this.showToast("No gridPane has been found for the given player", ToastType.ERROR);
            return;
        }

        Platform.runLater(() -> {
            playerGrid.getChildren().clear();
            this.guiUtils.createShipVisuals(playerNickname, playerGrid);
            
            // Disable all the components used from the given player
            this.clientModel.getShipOfPlayer(playerNickname).ifPresent((ship) -> {
                ship.traverse((comp) -> {
                    ImageView imgView = this.components.get(comp.getID());
                    imgView.setOpacity(0.0);
                    imgView.setOnMouseClicked(null);
                });
            });
        });
    }

    public void disableTimerButton() {
        Platform.runLater(() -> {
            this.flipTimerButton.setDisable(true);
        });
    }

    public void resetTimer() {
        Platform.runLater(() -> {
            this.flipTimerButton.setDisable(false);
        });
    }

    public void placePlayerInTheBoard(String playerNickname) {
        this.showToast(playerNickname + " has finished building his ship", ToastType.INFO);

        if (this.clientModel.getDifficultyLevel() == 2) {
            this.guiUtils.placePlayerInBoard(playerNickname, 2, 24, this.viewGameBoardStackPaneLevel2, this.playersRocketBoard);
        }
        else {
            this.guiUtils.placePlayerInBoard(playerNickname, 0, 18, this.viewGameBoardStackPaneLevel0, this.playersRocketBoard);
        }
    }
}
