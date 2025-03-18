package it.polimi.ingsw.is25am28.GameModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.GameModel.GameModel;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;

public class GameModelTest {
      GameModel gm;
      
      @BeforeEach
      void init() {
            gm = new GameModel(1);
      }

      @Test
      void deck_is_random_and_no_dup(){
            EventCard card = gm.nextRound();
            List<EventCard> deck = new ArrayList<>();
            int ml = 0;

            while( card != null ){
                  deck.add(card);
                  card = gm.nextRound();
            }

            assertEquals(8, deck.size());

            for( int i = 0; i < 7; i++ ){
                  if( deck.get(i).getClass().equals(deck.get(i+1).getClass()) )
                        ml++;
                  else
                        ml = 0;
                  for( int j = i + 1; j < 8; j++ )
                        assertNotEquals(deck.get(i), deck.get(j));
            }

            if( ml > 1 ){
                  throw new Error("too much duplicates");
            }
      }
}
