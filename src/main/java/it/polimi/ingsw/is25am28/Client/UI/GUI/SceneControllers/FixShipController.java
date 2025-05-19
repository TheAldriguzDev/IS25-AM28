package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Network.Messages.FixShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FixShipController extends GUIController {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;

    private Pair<Integer, Integer> shipOffsets;
    private boolean isShipValid;

    // Map of the components' images
    private Map<Pair<Integer, Integer>, ImageView> componentsImagesMap;

    // Map of the component's clickable regions
    private Map<Pair<Integer, Integer>, Region> componentsRegionMap;

    public void init(FixShipDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();

        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        this.componentsImagesMap = new HashMap<>();

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
        int endCol = shipDimensions.getValue() + shipOffsets.getValue();;

        // TODO: add the cellEvent only if there is actually a placed component
        for (int row = shipOffsets.getKey(); row < endRow; row++) {
            for (int col = shipOffsets.getValue(); col < endCol; col++) {
                if (shipProfiles[row][col] == 1) {
                    // The cellEventListener is added only if there is an actual component in the computed coordinates
                    ClientComponent component = ship.getComponent(row, col);
                    if(component != null) {

                        // Adding the component's image in the shipGrid
                        URL componentImagePath = Objects.requireNonNull(getClass().getResource(component.getPath()));
                        Image img = new Image(componentImagePath.toExternalForm(), 105, 105, true, true);
                        ImageView componentImgView = new ImageView(img);
                        componentImgView.setImage(img);
                        this.shipGrid.add(componentImgView, col, row);
                        // Adds the component to the images map, so the reference can be easily retrieve in case of removal of the component
                        this.componentsImagesMap.put(new Pair<>(row, col), componentImgView);

                        if (this.clientModel.getDifficultyLevel() == 0) {
                            this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue() + 1);
                        } else {
                            this.addCellEventListener(row - shipOffsets.getKey(), col - shipOffsets.getValue());
                        }
                    }
                }
            }
        }

//        for (int col = 0; col < 2; col++) {
//            Region cell = new Region(); // Place holder node
//            cell.setPrefSize(100, 100);
//            cell.setStyle("-fx-background-color: transparent;");
//            this.shipGrid.add(cell, col, 0);
//        }

    }

    /**
     * Method used to add the event listener to the clickable cells of the player ship
     * */
    public void addCellEventListener(int row, int col) {
        if (row == 2 && col == 3) {
            // The player cannot remove the core
            this.componentsImagesMap.remove(new Pair<>(row, col));
            return;
        }

        Region cell = new Region(); // Place holder node
        cell.setPrefSize(100, 100);
        cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);

        this.shipGrid.add(cell, col, row);
        cell.setOnMouseClicked(_ -> handleRemoveComponent(row, col));
    }

    /**
     * Removes the selected component
     */
    public void handleRemoveComponent(int row, int col) {
        // Write method
        GUIHandler.setCommandCTX(new CommandCTX(
                "removeTile", //
                () -> {
                this.clientModel.getState().removeComponentFromShip(row, col);

                    // TODO: move this in the removedComponent
//                    Platform.runLater(() -> {

//                        // Remove the clickable region
//                        this.shipGrid.getChildren().remove(cell);
//
//                        // removing the component's image
//                        this.shipGrid.getChildren().remove(this.componentsImagesMap.get(new Pair<>(row, col)));
//                        this.componentsImagesMap.remove(new Pair<>(row, col));
//                    });
                },
                () -> {}
        ));

        try {
            if (this.clientModel.getDifficultyLevel() == 0) {
                GUIHandler.getVirtualClient().sendMessage(
                        new FixShip(
                                this.clientModel.getNickname(),
                                row + shipOffsets.getKey(),
                                col + shipOffsets.getValue() -1
                        )
                );
            } else {
                GUIHandler.getVirtualClient().sendMessage(
                        new FixShip(
                                this.clientModel.getNickname(),
                                row + shipOffsets.getKey(),
                                col + shipOffsets.getValue()
                        )
                );
            }
        } catch (Exception e) {
            this.showError(e.getMessage());
        }
    }

    //TODO: label at the bottom of the ship stating the ship's validity
    //TODO: allow tile removal only if the ship is invalid
    //TODO: updateFunctions

    // ===== METHOD USED BY THE VIEW UPDATER TO UPDATE THE VIEW IN REAL TIME ===== //

    public void removeComponent(int row, int col) {}

}
