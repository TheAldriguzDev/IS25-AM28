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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FixShipController extends GUIController {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private TextFlow fixShipLabel;
    @FXML private StackPane imagePane;
    @FXML private VBox fixShipVBox;

    private Pair<Integer, Integer> shipOffsets;
    private boolean isShipValid;

    // Map of the components' images
    private Map<String, ImageView> componentsImagesMap;

    // Map of the component's clickable regions
    private Map<String, Region> componentsRegionMap;

    public void init(FixShipDTO state) {

//        System.out.println("Prima (init)");
//        for (Map.Entry<String, GridPane> entry : this.playersShipGridPane.entrySet()) {
//            System.out.println("Name: " + entry.getKey() + ", gridReference: " + entry.getValue());
//        }
//        System.out.println("Dopo (init)");

        this.clientModel = GUIHandler.getInstance().getClientModel();

        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        this.componentsImagesMap = new HashMap<>();
        this.componentsRegionMap = new HashMap<>();

        // Sets the fixShipLabel and the isShipValid flag
        this.setShipLabelText(!state.getPlayerWithInvalidShip().contains(this.clientModel.getNickname()));

        // Initializing the ship to display
        String path = "/imgs/cardboard/level_" + this.clientModel.getDifficultyLevel() + ".jpg";
        URL resource = Objects.requireNonNull(getClass().getResource(path));

        this.shipImageView.setImage(new Image(resource.toExternalForm()));
        this.shipImageView.setFitWidth(816.0);
        this.shipImageView.setPreserveRatio(true);
//        this.shipImageView.fitWidthProperty().bind(imagePane.widthProperty().subtract(40));
//        this.imagePane.setMaxSize(1000, 1000);

        Pair<Integer, Integer> shipOffsets = AbstractShip.shipOffsets.get(this.clientModel.getDifficultyLevel());
        this.shipOffsets = shipOffsets;
        Pair<Integer, Integer> shipDimensions = AbstractShip.shipDimensions.get(this.clientModel.getDifficultyLevel());
        int[][] shipProfiles = AbstractShip.shipProfiles.get(this.clientModel.getDifficultyLevel());

        int endRow = shipDimensions.getKey() + shipOffsets.getKey();
        int endCol = shipDimensions.getValue() + shipOffsets.getValue();

        // Add the core's image
        this.addCoreImg();

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

                        int ofsRow = row - shipOffsets.getKey();
                        int ofsCol = this.clientModel.getDifficultyLevel() == 0 ? col - shipOffsets.getValue() + 1 : col - shipOffsets.getValue();
                        this.shipGrid.add(componentImgView, ofsCol, ofsRow);

                        // Adds the component to the images map, so the reference can be easily retrieve in case of removal of the component
                        this.componentsImagesMap.put(this.keyFromCoords(row, col), componentImgView);

                        if(!isShipValid) {this.addCellEventListener(row, col);}
                    }
                }
            }
        }

        // Sets the main ship to the calculated one
//        this.playersShipGridPane.replace(this.clientModel.getNickname(), this.shipGrid);


        // The player cannot remove the core
//        this.componentsImagesMap.remove(keyFromCoords(7, 7)); // TODO: check component coordinates

    }

    /**
     * Method used to add the event listener to the clickable cells of the player ship
     * */
    public void addCellEventListener(int row, int col) {

        int ofsRow = row - shipOffsets.getKey();
        int ofsCol = this.clientModel.getDifficultyLevel() == 0 ? col - shipOffsets.getValue() + 1 : col - shipOffsets.getValue();

        if (ofsRow == 2 && ofsCol == 3) {
            // The player cannot remove the core
            return;
        }

        Region cell = new Region(); // Place holder node
        cell.setPrefSize(100, 100);
        cell.setStyle("-fx-background-color: transparent;");
        cell.setCursor(Cursor.HAND);
        cell.setPickOnBounds(true);

        // Adds the component to the regionss map, so the reference can be easily retrieve in case of removal of the component
        this.componentsRegionMap.put(this.keyFromCoords(row, col), cell);

        this.shipGrid.add(cell, ofsCol, ofsRow);
        cell.setOnMouseClicked(_ -> handleRemoveComponent(ofsRow, ofsCol));
    }

    /**
     * Removes the selected component
     */
    public void handleRemoveComponent(int row, int col) {
        GUIHandler.setCommandCTX(new CommandCTX(
                "removeTile", //
                () -> {
                    this.clientModel.getState().removeComponentFromShip(row, col);

                },
                () -> {}
        ));

        // TODO: can be simplified
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

    // ===== METHOD USED BY THE VIEW UPDATER TO UPDATE THE VIEW IN REAL TIME ===== //

    public void removeComponent(int row, int col, boolean isShipValid) {
        Platform.runLater(() -> {

            // Remove the clickable region
            this.shipGrid.getChildren().remove(this.componentsRegionMap.get(keyFromCoords(row, col)));
            this.componentsRegionMap.remove(keyFromCoords(row, col));

            // removing the component's image
            this.shipGrid.getChildren().remove(this.componentsImagesMap.get(keyFromCoords(row, col)));
            this.componentsImagesMap.remove(keyFromCoords(row, col));

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
        });
    }

    private void addCoreImg() {
        String playerColor = this.clientModel.getAllClientPlayers().get(this.clientModel.getNickname()).getColor().getPlayerColorString();
        URL resource = Objects.requireNonNull(getClass().getResource("/imgs/tiles/core_" + playerColor + ".jpg"));
        Image img = new Image(resource.toExternalForm(), 105, 105, true, true);

        ImageView imgView = new ImageView(img);
        imgView.setImage(img);

        this.shipGrid.add(imgView, 3, 2);
    }

    private String keyFromCoords(int row, int col) {
        return row + "_" + col;
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
}
