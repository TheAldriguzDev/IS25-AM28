package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Network.Messages.SelectTile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShipConstructionController extends GUIController {

    // Attributes for
    @FXML private Label timerLabel;
    @FXML private Button flipTimerButton;
    @FXML private HBox timerContainer;

    // ========== FXML ATTRIBUTES ========== //
    @FXML private VBox shipContainer;
    @FXML private Button deselectButton;
    @FXML private Button rotateRightButton;
    @FXML private Button rotateLeftButton;
    @FXML private Button reserveButton;
    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
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

    public void initShipConstruction() {
        // TODO: Init the players ships --> Useful to update the specific client ship in real time
        this.clientModel = GUIHandler.getInstance().getClientModel();

        // Init the component
        this.initComponents();

        // INIT THE NAVBAR

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
        // Timer --> display only if the level is != 0
        if (this.clientModel.getDifficultyLevel() != 0) {

        }
    }

    private void initShipPage() {
        int rows = 5;
        int cols = 7;

        // Test level configuration
        if (this.clientModel.getDifficultyLevel() == 0) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    // Skip the cells that are not selectable
                    boolean exclude =
                            ((col == 0 || col == cols - 1)) ||
                            ((col == 1 || col == cols - 2) && (row == 0 || row == 1)) ||
                            ((col == 2 || col == cols - 3) && (row == 0)) ||
                            ((col == 3) && (row == rows - 1));

                    this.addCellEventListener(exclude, row, col);
                }
            }
        }

        // Level two configuration
        if (this.clientModel.getDifficultyLevel() == 2) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    // Skip the cells that are not selectable
                    boolean exclude =
                            ((col == 0 || col == cols - 1) && (row == 0 || row == 1)) ||
                            ((col == 1 || col == cols - 2) && row == 0) ||
                            (col == 3 && (row == 0 || row == rows - 1));

                    this.addCellEventListener(exclude, row, col);
                }
            }
        }
    }

    private void addCellEventListener(boolean exclude, int row, int col) {
        if (exclude) return;

        Region cell = new Region(); // Place holder node
        cell.setPrefSize(100, 100);
        cell.setStyle("-fx-background-color: transparent;");

        cell.setOnMouseClicked(e -> handlePlaceTile(row, col));
        this.shipGrid.add(cell, col, row);
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
                    this.selectedComponent = selectedComponent;
                    this.tileScrollPane.setVisible(false);
                    this.tileScrollPane.setManaged(false);

                    // Before displaying the dynamic page --> set the tile info etc
                    this.shipContainer.setVisible(true);
                    this.shipContainer.setManaged(true);

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

    private void handlePlaceTile(int i, int j) {
        System.out.println("Component: " + this.selectedComponent.getID() + " | Placed at: " + i + ", " + j);
    }
}
