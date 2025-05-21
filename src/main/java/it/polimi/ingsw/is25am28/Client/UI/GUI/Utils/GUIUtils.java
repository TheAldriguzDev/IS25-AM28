package it.polimi.ingsw.is25am28.Client.UI.GUI.Utils;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

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
}
