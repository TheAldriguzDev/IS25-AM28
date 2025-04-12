package it.polimi.ingsw.is25am28.GameModel;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;

import it.polimi.ingsw.is25am28.GameModel.FileLoader.CardLoader;
import it.polimi.ingsw.is25am28.GameModel.FileLoader.TileLoader;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;

import org.junit.jupiter.api.Test;

public class FileLoaderTest extends GMTest {

      
      @Test
      void load_tiles_correctly(){
            assertDoesNotThrow(()->{
                  TileLoader.get().read();
            });
      }

      @Test
      void load_cards_correctly(){
            assertDoesNotThrow(()->{
                  CardLoader.get().read( initBoard( new HashMap<>() ), new ResourceBank(), 2 );
            });
      }
}
