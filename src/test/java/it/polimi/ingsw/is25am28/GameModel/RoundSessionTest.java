package it.polimi.ingsw.is25am28.GameModel;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.GameModel.Session.RoundSession;
import it.polimi.ingsw.is25am28.State.FirstRoundState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.ActionJSON.VisitPlanetsJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.EventCards.MeteorShower;
import it.polimi.ingsw.is25am28.EventCards.VisitPlanets;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

public class RoundSessionTest extends GMTest {

      RoundSession r;
      HashMap<String,Player> players;
      Board board = initBoard( players );

      @BeforeEach
      void init(){
            ArrayList<EventCard> deck = new ArrayList<>();

            ArrayList<Integer> meteor = new ArrayList<>();
            List<List<Integer>> sequence = new ArrayList<>();

            meteor.add(1);
            meteor.add(1);

            sequence.add(meteor);

            List<Map<String,Integer>> planets = new ArrayList<>();

            planets.add( new HashMap<>() );

            planets.getFirst().put("red", 3);

            

            deck.add( new MeteorShower("meteore", 2, sequence, board ) );
            deck.add( new VisitPlanets("pianeti", 2, 3, planets, new ResourceBank(), board ) );


            r = new RoundSession( board, 2, deck );
      }

      @Test
      void represent_first_state_correctly(){
            FirstRoundState state = r.init();

            assertEquals( 0, state.getBoard().getEliminatedPlayersNickname().size() );
            assertEquals( 2, state.getBoard().getPlayersNickname().size() );
            assertEquals( 2, state.getBoard().getLevel() );

            assertEquals( "meteore", state.getCard().getCardName() );

            assertEquals( 2, state.getShips().size() );
      }

      /**
       * card behavior is supposed to be correct
       */
      @Test
      void simulate_game_correctly(){
            CardStateJSON s;

            r.init();
            assertEquals( 0, r.getRound() );

            s = r.playCard( new MeteorShowerJSON("A", 0, 2, null, null) );
            assertTrue(!r.hasFinished());
            assertEquals( 0, r.getRound() );


            assertEquals( "B", s.getPlayerNickname() );
            s = r.playCard(new MeteorShowerJSON("B", 0, 2, null, null));
            assertTrue(!r.hasFinished());

            assertEquals( 1, r.getRound() );
            assertEquals( "pianeti", s.getCardName() );

            var json = new VisitPlanetsJSON(-1, null, null );

            json.setPlayerNickname("A");
            r.playCard( json );
            assertTrue(!r.hasFinished());
            assertEquals( 1, r.getRound() );

            json.setPlayerNickname("B");
            r.playCard( json );
            assertTrue(r.hasFinished());
      }
}
