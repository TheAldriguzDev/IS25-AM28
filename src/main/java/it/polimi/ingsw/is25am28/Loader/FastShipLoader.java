package it.polimi.ingsw.is25am28.Loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Loader.FastShipTiles.FastShipTiles;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FastShipLoader extends Loader<FastShipTiles> {
    private static int shipID = 0;
    private static int shipToLoad = 0;

    public FastShipLoader() throws IOException {
        super(FastShipLoader.class.getResourceAsStream("/json/fastShip.json"), FastShipTiles.class);
    }

    /**
     * @param ship
     * This method creates a JSON that can be used to recreate the given ship
     */
    public static void dumpShipJSON(Ship ship) {


        if (ship == null) {
            return;
        }

        Pair<Integer, Integer> startingCoords = AbstractShip.shipOffsets.get(ship.getDifficultyLevel());
        int startRow = startingCoords.getKey();
        int startCol = startingCoords.getValue();

        Pair<Integer, Integer> shipDim = AbstractShip.shipDimensions.get(ship.getDifficultyLevel());
        int endingRow = startRow + shipDim.getKey();
        int endingCol = startCol + shipDim.getValue();

        System.out.println("\"" + shipID + "\": [");
        for (int i = startRow; i < endingRow; i++) {

            for (int j = startCol; j < endingCol; j++) {
                if (i == 6 && j == 6) continue;

                Component component = ship.getComponent(i, j);
                if (component != null) {
                    System.out.println("        {");
                    Map<String, Object> map = (component.toMap());

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("id") || entry.getKey().equals("direction") || entry.getKey().equals("row") || entry.getKey().equals("col")) {
                            System.out.println("            \"" + entry.getKey() + "\": " + entry.getValue() + ",");
                        }
                    }
                    System.out.println("        },");
                }
            }
        }
        System.out.println("],");
        shipID++;
    }

    /**
     * @param ship
     * This method modifies the given ship to a pre-made one
     */
    public void loadShipFromJSON(Ship ship) {
        TileLoader loader;
        try {
            loader = new TileLoader();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while reading the json file: " + e);
        }

        List<Component> components = loader.getTiles();

        FastShipTiles fastShipTilesData = this.getReadJSON();

//        System.out.println("Adding components:");

        fastShipTilesData.getFastShipTilesInfo(shipToLoad).forEach(fastShipTilesInfo -> {
            int id = fastShipTilesInfo.getId();
            int direction = fastShipTilesInfo.getDirection();
            int row = fastShipTilesInfo.getRow();
            int col = fastShipTilesInfo.getCol();

            Component component = components.get(id);
            for(int i = 0; i < direction; i++) {
                component.rotateRight();
            }

            ship.addComponent(component, row, col);

//            System.out.println("id: " + id + ", direction: " + direction + ", row: " + row + ", col: " + col);
        });

//        System.out.println("Finished adding components");

        shipToLoad++;
    }

}
