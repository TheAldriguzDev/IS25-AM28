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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static it.polimi.ingsw.is25am28.Model.Ship.AbstractShip.shipOffsets;
import static it.polimi.ingsw.is25am28.Model.Ship.AbstractShip.shipProfiles;

//TODO: for now use colors to indicate full cabins

public class PopulateShipController extends GUIController {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private TextFlow populateShipLabel;

    private Pair<Integer, Integer> shipOffsets;
    private boolean isShipFull;

    // Map of the component's clickable regions
    private Map<String, Region> componentsRegionMap;

    public void init(PopulateShipDTO state) {

        this.clientModel = GUIHandler.getInstance().getClientModel();

        ClientShip ship = this.clientModel.getShipOfPlayer(this.clientModel.getNickname()).orElse(null);
        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [FixShipController] ClientShip is null", ANSIColors.RED));
            return;
        }

        this.componentsRegionMap = new HashMap<>();

        // Sets the populateShipLabel and the isShipFull flag
        this.setShipLabelText(state.getPlayersReady().contains(this.clientModel.getNickname()));

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

                        if(!isShipFull) {this.addCellEventListener(row, col);}
                    }
                }
            }
        }




    }

    /**
     * Method used to add the event listener to the clickable cells of the player ship
     * */
    public void addCellEventListener(int row, int col) {

        int ofsRow = row - shipOffsets.getKey();
        int ofsCol = this.clientModel.getDifficultyLevel() == 0 ? col - shipOffsets.getValue() + 1 : col - shipOffsets.getValue();

        if (ofsRow == 2 && ofsCol == 3) {

            // TODO: Add visual representation of full core
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
//        cell.setOnMouseClicked(_ -> handleRemoveComponent(ofsRow, ofsCol));
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

    private void setShipLabelText(boolean isShipFull) {
        String validityLabel;
        this.populateShipLabel.getChildren().clear();
        if (!isShipFull) {
            this.isShipFull = false;
            Text text1 = new Text("Your ship is ");
            text1.setFill(Color.WHITE);
            text1.setFont(Font.font(20));
            Text text2 = new Text("NOT FULL! ");
            text2.setFill(Color.RED);
            text2.setFont(Font.font(20));
            Text text3 = new Text("please select the lifeForms to add to the ship");
            text3.setFill(Color.WHITE);
            text3.setFont(Font.font(20));
            this.populateShipLabel.getChildren().addAll(text1, text2, text3);
        } else {
            this.isShipFull = true;
            Text text1 = new Text("Your ship is ");
            text1.setFill(Color.WHITE);
            text1.setFont(Font.font(20));
            Text text2 = new Text("FULL! ");
            text2.setFill(Color.GREEN);
            text2.setFont(Font.font(20));
            Text text3 = new Text("please wait for the other players to fill their ships");
            text3.setFill(Color.WHITE);
            text3.setFont(Font.font(20));
            this.populateShipLabel.getChildren().addAll(text1, text2, text3);
        }
    }
}
