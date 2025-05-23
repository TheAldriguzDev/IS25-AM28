package it.polimi.ingsw.is25am28.Loader;

import it.polimi.ingsw.is25am28.Loader.Tiles.Tiles;
import it.polimi.ingsw.is25am28.Model.Components.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TileLoader extends Loader<Tiles> {

    public TileLoader() throws IOException {
        super(TileLoader.class.getResourceAsStream("/json/tiles.json"), Tiles.class);
    }

    public List<Component> getTiles() {
        List<Component> tiles = new ArrayList<>();

        Tiles tilesData = this.getReadJSON();

        tilesData.getCannon().forEach((c) -> {
            tiles.add(
                    new Cannon(
                            c.getConnectors(),
                            c.getForce(),
                            c.getPath()
                    )
            );
        });

        tilesData.getShield().forEach((c) -> {
            tiles.add(
                    new Shield(
                            c.getConnectors(),
                            c.getPath()
                    )
            );
        });

        tilesData.getStructural().forEach((c) -> {
            tiles.add(
                    new Structural(
                            c.getConnectors(),
                            c.getPath()
                    )
            );
        });

        tilesData.getCabin().forEach((c) -> {
            tiles.add(
                    new Cabin(
                            c.getConnectors(),
                            false,
                            c.getPath()
                    )
            );
        });

        tilesData.getEngine().forEach((c) -> {
           tiles.add(
                   new Engine(
                           c.getConnectors(),
                           c.getSpeed(),
                           c.getPath()
                   )
           );
        });

        tilesData.getBattery().forEach((c) -> {
            tiles.add(
                new Battery(
                        c.getConnectors(),
                        c.getCapacity(),
                        c.getPath()
                )
            );
        });

        tilesData.getVital().forEach((c) -> {
           tiles.add(
                   new Vital(
                           c.getConnectors(),
                           c.getType(),
                           c.getPath()
                   )
           );
        });

        tilesData.getStorage().forEach((c) -> {
            tiles.add(
                    new Storage(
                            c.getConnectors(),
                            c.getCapacity(),
                            c.getSpecial(),
                            c.getPath()
                    )
            );
        });

        for( int i = 0; i < tiles.size(); i++ ){
            tiles.get(i).setId(i);
        }

        return tiles;
    }
}
