package it.polimi.ingsw.is25am28.GameModel;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import it.polimi.ingsw.is25am28.GameModel.FileLoader.TileLoader;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.Components.Component;

public class FileLoaderTest {

      /*
      @Test
      void load_tiles_correctly(){
            FileLoader fl = TileLoader.get().read();
            assertDoesNotThrow(()->{
                  fl.getAllComponents();
            });
      }

      @Test
      void load_correct_tiles(){
            FileLoader fl = new FileLoader("./json/tiles.json");
            List<Component> l = fl.getAllComponents();

            for( int i = 0; i < l.size(); i++ ){
                  for( int j = i + 1; j < l.size() && l.get(i).getClass() == l.get(j).getClass(); j++ ){
                        assertNotEquals(l.get(i), l.get(j));
                  }
            }
      }
       */
}
