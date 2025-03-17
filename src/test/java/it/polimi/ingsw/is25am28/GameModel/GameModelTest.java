package it.polimi.ingsw.is25am28.GameModel;

import org.junit.jupiter.api.BeforeEach;

import it.polimi.ingsw.is25am28.GameModel.GameModel;

public class GameModelTest {
      GameModel gm;
      
      @BeforeEach
      void init() {
            gm = new GameModel(1);
      }
}
