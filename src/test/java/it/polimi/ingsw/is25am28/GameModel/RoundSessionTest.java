package it.polimi.ingsw.is25am28.GameModel;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.GameModel.Session.RoundSession;
import it.polimi.ingsw.is25am28.State.FirstRoundState;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.EventCards.MeteorShower;
import it.polimi.ingsw.is25am28.EventCards.VisitPlanets;
import it.polimi.ingsw.is25am28.EventCards.OpenSpace;
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

      @Test
      void transit_correctly(){
            r.init();

            r.playCard( new MeteorShowerJSON("A", 0, 2, null, null) );

      }
}
