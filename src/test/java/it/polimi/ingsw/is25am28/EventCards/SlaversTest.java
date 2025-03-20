package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.Battery;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Components.Vital;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

        List<Player> players = board.getPlayers();
        Ship ship_1 = players.get(0).getShip();
        ship_init1(ship_1);
        Ship ship_2 = players.get(1).getShip();
        ship_init2(ship_2);
        Ship ship_3 = players.get(2).getShip();
        ship_init3(ship_3);
        Ship ship_4 = players.get(3).getShip();
        ship_init4(ship_4);




        Slavers slavers = new Slavers("Slavers", 2, 2, 2, 4, 3, board);


        ArrayList<Cabin> crewToRemove = new ArrayList<>();
        ActionJSON actionJSON1 = new SlaversJSON("Player 1", true, crewToRemove, 0);
        ActionJSON actionJSON2 = new SlaversJSON("Player 2", true, crewToRemove, 1);
        ActionJSON actionJSON3 = new SlaversJSON("Player 3", true, crewToRemove, 4);
        ActionJSON actionJSON4 = new SlaversJSON("Player 4", true, crewToRemove, 0);

        slavers.initCardPlayers();

        slavers.useCard(actionJSON1);
        // Se dopo averli sconfitti vengono guadagnati crediti anche nelle altre esecuzioni è perchè in realà
        // non dovrebbero partire proprio, quindi i lcontroll all'interno non viene fatto, se ne occupa la session
        slavers.useCard(actionJSON2);
        slavers.useCard(actionJSON3);
        slavers.useCard(actionJSON4);

        System.out.println(players.get(0).getNickname());
        System.out.println("(1)Ship's FirePower is: " + ship_1.getFirePower(0));
        System.out.println("(1)Ship's Battery is: " + ship_1.getAvailableEnergy());
        System.out.println("(1)Player's credits: " + players.get(0).getCredits());


        System.out.println(players.get(1).getNickname());
        System.out.println("(2)Ship's FirePower is: " + ship_2.getFirePower(0));
        System.out.println("(2)Ship's Battery is: " + ship_2.getAvailableEnergy());
        System.out.println("(1)Player's credits: " + players.get(1).getCredits());

        System.out.println(players.get(2).getNickname());
        System.out.println("(3)Ship's FirePower is: " + ship_3.getFirePower(0));
        System.out.println("(3)Ship's Battery is: " + ship_3.getAvailableEnergy());
        System.out.println("(1)Player's credits: " + players.get(2).getCredits());

        System.out.println(players.get(3).getNickname());
        System.out.println("(4)Ship's FirePower is: " + ship_4.getFirePower(0));
        System.out.println("(4)Ship's Battery is: " + ship_4.getAvailableEnergy());
        System.out.println("(1)Player's credits: " + players.get(3).getCredits());
    }

    public void ship_init1(Ship ship) {

        int[] connectors = {0,0,0,0};

        Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors, false);
        Cabin cabin_2 = new Cabin(connectors, false);
        Cabin cabin_3 = new Cabin(connectors, false);
        Cannon cannon_1 = new Cannon(connectors, 2);
        Cannon cannon_2 = new Cannon(connectors, 1);
        Cannon cannon_3 = new Cannon(connectors, 1);
        Vital vital_1 = new Vital(connectors, 0);
        Battery battery_1 = new Battery(connectors, 20);
        //cannon_1.rotateLeft();

        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        ship.addComponent(core, 7, 7);
        ship.addComponent(cabin_1, 7, 6);
        ship.addComponent(cabin_2, 7, 8);
        ship.addComponent(cabin_3, 8, 7);
        ship.addComponent(cannon_1, 6, 7);
        ship.addComponent(cannon_2, 7, 5);
        ship.addComponent(cannon_3, 7, 9);
        ship.addComponent(vital_1, 8, 6);
        ship.addComponent(battery_1, 8, 8);

        ship.generateComponentSubLists();

    }

    public void ship_init2(Ship ship) {

        int[] connectors = {0,0,0,0};

        Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors, false);
        Cabin cabin_2 = new Cabin(connectors, false);
        Cabin cabin_3 = new Cabin(connectors, false);
        Cannon cannon_1 = new Cannon(connectors, 1);
        Cannon cannon_2 = new Cannon(connectors, 1);
        Cannon cannon_3 = new Cannon(connectors, 1);
        Vital vital_1 = new Vital(connectors, 0);
        Battery battery_1 = new Battery(connectors, 20);
        //cannon_1.rotateLeft();

        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        ship.addComponent(core, 7, 7);
        ship.addComponent(cabin_1, 7, 6);
        ship.addComponent(cabin_2, 7, 8);
        ship.addComponent(cabin_3, 8, 7);
        ship.addComponent(cannon_1, 6, 7);
        ship.addComponent(cannon_2, 7, 5);
        ship.addComponent(cannon_3, 7, 9);
        ship.addComponent(vital_1, 8, 6);
        ship.addComponent(battery_1, 8, 8);

        ship.generateComponentSubLists();

    }

    public void ship_init3(Ship ship) {

        int[] connectors = {0,0,0,0};

        Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors, false);
        Cabin cabin_2 = new Cabin(connectors, false);
        Cabin cabin_3 = new Cabin(connectors, false);
        Cannon cannon_1 = new Cannon(connectors, 1);
        Cannon cannon_2 = new Cannon(connectors, 1);
        Cannon cannon_3 = new Cannon(connectors, 1);
        Vital vital_1 = new Vital(connectors, 0);
        Battery battery_1 = new Battery(connectors, 20);
        //cannon_1.rotateLeft();

        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        ship.addComponent(core, 7, 7);
        ship.addComponent(cabin_1, 7, 6);
        ship.addComponent(cabin_2, 7, 8);
        ship.addComponent(cabin_3, 8, 7);
        ship.addComponent(cannon_1, 6, 7);
        ship.addComponent(cannon_2, 7, 5);
        ship.addComponent(cannon_3, 7, 9);
        ship.addComponent(vital_1, 8, 6);
        ship.addComponent(battery_1, 8, 8);

        ship.generateComponentSubLists();

    }

    public void ship_init4(Ship ship) {

        int[] connectors = {0,0,0,0};

        Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors, false);
        Cabin cabin_2 = new Cabin(connectors, false);
        Cabin cabin_3 = new Cabin(connectors, false);
        Cannon cannon_1 = new Cannon(connectors, 1);
        Cannon cannon_2 = new Cannon(connectors, 1);
        Cannon cannon_3 = new Cannon(connectors, 1);
        Vital vital_1 = new Vital(connectors, 0);
        Battery battery_1 = new Battery(connectors, 20);
        //cannon_1.rotateLeft();

        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        core.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        ship.addComponent(core, 7, 7);
        ship.addComponent(cabin_1, 7, 6);
        ship.addComponent(cabin_2, 7, 8);
        ship.addComponent(cabin_3, 8, 7);
        ship.addComponent(cannon_1, 6, 7);
        ship.addComponent(cannon_2, 7, 5);
        ship.addComponent(cannon_3, 7, 9);
        ship.addComponent(vital_1, 8, 6);
        ship.addComponent(battery_1, 8, 8);

        ship.generateComponentSubLists();

    }

}