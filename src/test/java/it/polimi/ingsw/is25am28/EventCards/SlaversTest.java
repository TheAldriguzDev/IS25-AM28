package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class SlaversTest {


    @BeforeEach
    void init() {
        

    }


    @Test
    public void TakeCreditsTest() {
        Board board;
        board = new BoardLevel2();
        board.buildBoard();

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        Slavers slavers = new Slavers("Slavers", 2, 2, 2, 4, 3, board);

        Ship ship = getShip();

        //Player player1 = new Player("Player 1", PlayerColor.RED, 2);
        //Player player2 = new Player("Player 2", PlayerColor.BLUE, 2);






        
        
        
        
        
        

        ArrayList<Cabin> crewToRemove = new ArrayList<>();

        SlaversJSON slaversJSON = new SlaversJSON();
        slaversJSON.setPlayerNickname("Player 1");
        slaversJSON.setTakeCredits(true);
        slaversJSON.setCrewToRemove(crewToRemove);
        slaversJSON.setNumOfDoubleCannonsActivated(0);

        JSONObject data = slaversJSON.getData();
        ActionJSON actionJSON = new SlaversJSON(data);

        slavers.initCardPlayers();

        slavers.useCard(actionJSON);

        assert


        System.out.println("Ship's FirePower is: " + ship.getFirePower(0));
    }

    public Ship getShip() {
        Ship ship = new Ship(2);

        int[] connectors = {0,0,0,0};

        Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors, false);
        Cabin cabin_2 = new Cabin(connectors, false);
        Cabin cabin_3 = new Cabin(connectors, false);
        Cannon cannon_1 = new Cannon(connectors, 1);
        Cannon cannon_2 = new Cannon(connectors, 1);
        Cannon cannon_3 = new Cannon(connectors, 1);
        cannon_1.rotateLeft();

        ship.addComponent(core, 7, 7);
        ship.addComponent(cabin_1, 7, 6);
        ship.addComponent(cabin_2, 7, 8);
        ship.addComponent(cabin_3, 8, 7);
        ship.addComponent(cannon_1, 6, 7);
        ship.addComponent(cannon_2, 7, 5);
        ship.addComponent(cannon_3, 7, 9);

        ship.generateComponentSubLists();

        return ship;
    }

}