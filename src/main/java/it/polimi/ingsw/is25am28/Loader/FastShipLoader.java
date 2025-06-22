package it.polimi.ingsw.is25am28.Loader;

import it.polimi.ingsw.is25am28.Loader.FastShipTiles.FastShipTiles;
import it.polimi.ingsw.is25am28.Loader.FastShipTiles.FastShipTilesInfo;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Ship.AbstractShip;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for handling the loading and dumping of ship configurations
 * using a JSON data source.
 */

public class FastShipLoader extends Loader<FastShipTiles> {
    private int shipToDump = 0;
    private int shipToLoad = 0;
    List<Component> components;

    public FastShipLoader() throws IOException {
        super(FastShipLoader.class.getResourceAsStream("/json/fastShip.json"), FastShipTiles.class);

        try {
            TileLoader tileLoader = new TileLoader();
            this.components = tileLoader.getTiles();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while reading the json file: " + e);
        }
    }

    /**
     * Dumps the JSON representation of the given ship to the standard output.
     * This method generates a JSON-like structure for the specified ship object
     * based on its components, dimensions, and positioning.
     *
     * @param ship the ship object to be dumped into JSON format; if null, the method returns without performing any operation
     */
    public void dumpShipJSON(Ship ship) {


        if (ship == null) {
            return;
        }

        Pair<Integer, Integer> startingCoords = AbstractShip.shipOffsets.get(ship.getDifficultyLevel());
        int startRow = startingCoords.getKey();
        int startCol = startingCoords.getValue();

        Pair<Integer, Integer> shipDim = AbstractShip.shipDimensions.get(ship.getDifficultyLevel());
        int endingRow = startRow + shipDim.getKey();
        int endingCol = startCol + shipDim.getValue();

        System.out.println("\"" + this.shipToDump + "\": [");
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
        this.shipToDump++;
    }

    /**
     * Loads ship components and configurations from the JSON data source
     * and populates the given ship object.
     *
     * @param ship the ship object to be populated with components read from JSON
     *
     * @return the List of the id's of the used components
     */
    public List<Integer> loadShipFromJSON(Ship ship) {

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

        List<Integer> usedItems = this.getReadJSON().getFastShipTilesInfo(shipToLoad)
                .stream()
                .map(FastShipTilesInfo::getId)
                .toList();

        shipToLoad++;

        return usedItems;
    }

}
