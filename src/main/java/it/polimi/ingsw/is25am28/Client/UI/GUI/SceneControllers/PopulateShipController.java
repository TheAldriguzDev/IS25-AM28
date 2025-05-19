package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientCabin;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static it.polimi.ingsw.is25am28.Model.Ship.AbstractShip.shipOffsets;
import static it.polimi.ingsw.is25am28.Model.Ship.AbstractShip.shipProfiles;

public class PopulateShipController extends GUIController {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private Label populateShipLabel;

    private Pair<Integer, Integer> shipOffsets;
    private boolean isShipFull;

    // Map of the component's clickable regions
    private Map<Pair<Integer, Integer>, Region> componentsRegionMap;

    public void init(PopulateShipDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();

        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        this.componentsRegionMap = new HashMap<>();

        //TODO:  Sets the populateShipLabel and the isShipFull flag
//        this.setShipLabelText(state.getPlayerWithInvalidShip().contains(this.clientModel.getNickname()));

        // Initializing the ship to display
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
                        componentImgView.setImage(img);
                        this.shipGrid.add(componentImgView, col, row);

                        // If the ship is valid there's no need to add the cellEventListeners
                        if (!isShipFull || component.getClass().equals(ClientCabin.class)) { // TODO: can be optimized
                            if (this.clientModel.getDifficultyLevel() == 0) {
                                this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue() + 1);
                            } else {
                                this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method used to add the event listener to the clickable cells of the player ship
     * */
    public void addCellEventListener(int row, int col) {
        Region cell = new Region(); // Place holder node
        cell.setPrefSize(100, 100);
        cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);

        this.shipGrid.add(cell, col, row);
//        cell.setOnMouseClicked(_ -> handleRemoveComponent(row, col)); // Methods that opens up a pop-up window
    }


}
