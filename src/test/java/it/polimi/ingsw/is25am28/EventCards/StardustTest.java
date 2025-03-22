package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.StardustJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StardustTest {
    Board board;
    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Ship ship_1;
    Ship ship_2;
    Ship ship_3;
    Ship ship_4;
    ActionJSON actionJSON1;
    ActionJSON actionJSON2;
    ActionJSON actionJSON3;
    ActionJSON actionJSON4;

    Stardust stardust;

    @BeforeEach
    public void init() {

        board = new BoardLevel2();
        board.buildBoard();

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        List<Player> players = board.getPlayers();
        p1 = players.get(0);
        p2 = players.get(1);
        p3 = players.get(2);
        p4 = players.get(3);

        ship_1 = p1.getShip();
        ship_init1(ship_1);
        ship_2 = p2.getShip();
        ship_init2(ship_2);
        ship_3 = p3.getShip();
        ship_init3(ship_3);
        ship_4 = p4.getShip();
        ship_init4(ship_4);

        board.addPlayerToBoard(p1);
        board.addPlayerToBoard(p2);
        board.addPlayerToBoard(p3);
        board.addPlayerToBoard(p4);

        stardust = new Stardust("Stardust", 2, board);

    }

    @Test
    public void movementTest() {



        ActionJSON actionJSON1 = new StardustJSON("Player 1");
        ActionJSON actionJSON2 = new StardustJSON("Player 2"); // 3 connettori esposti
        ActionJSON actionJSON3 = new StardustJSON("Player 3"); // 2 connettori esposti
        ActionJSON actionJSON4 = new StardustJSON("Player 4"); // 4 connettori esposti

        stardust.initCardPlayers();

        for(Player p : board.getPlayers()) {
            ActionJSON actionJSON = new ActionJSON(p.getNickname());
            stardust.useCard(actionJSON);
        }

        stardust.useCard(actionJSON4);
        stardust.useCard(actionJSON3);
        stardust.useCard(actionJSON2);
        stardust.useCard(actionJSON1);

        assert p1.getCursor() == 6 : "p1 cursor should be 6, not " + p1.getCursor();
        assert p2.getCursor() == 0 : "p2 cursor should be 0, not " + p2.getCursor();
        assert p3.getCursor() == -1 : "p3 cursor should be -1, not " + p3.getCursor();
        assert p4.getCursor() == -4 : "p4 cursor should be -4, not " + p4.getCursor();
    }

    public void ship_init1(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio viene attivato


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 2);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 0}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 0, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 0, 0, 1}, 3);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);

        ship.generateComponentSubLists();


        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

    }

    public void ship_init2(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio non viene attivato


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 1, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 2);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 0, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 1, 0, 1}, 3);
        Storage storage_1 = new Storage(new int[] {0, 0, 0, 1}, 3, false);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        storage_1.storeItem(new Item(ItemColor.GREEN));
        storage_1.storeItem(new Item(ItemColor.GREEN));
        storage_1.storeItem(new Item(ItemColor.YELLOW));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);

        ship.generateComponentSubLists();

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

    }

    public void ship_init3(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone



        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 1);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 1, 0, 1}, 3);
        Storage storage_1 = new Storage(new int[] {0, 0, 0, 1}, 3, false);
        Storage storage_2 = new Storage(new int[] {0, 0, 0, 1}, 2, true);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        storage_1.storeItem(new Item(ItemColor.BLUE));
        storage_1.storeItem(new Item(ItemColor.YELLOW));
        storage_1.storeItem(new Item(ItemColor.GREEN));

        storage_2.storeItem(new Item(ItemColor.RED));
        storage_2.storeItem(new Item(ItemColor.RED));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

    }

    public void ship_init4(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 1, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 1);
        Cannon cannon_2 = new Cannon(new int[] {1, 1, 0, 1}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 1, 0, 1}, 3);
        Storage storage_1 = new Storage(new int[] {0, 0, 0, 1}, 3, false);
        Storage storage_2 = new Storage(new int[] {0, 0, 0, 1}, 2, true);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        storage_1.storeItem(new Item(ItemColor.BLUE));
        storage_1.storeItem(new Item(ItemColor.YELLOW));
        storage_1.storeItem(new Item(ItemColor.GREEN));

        storage_2.storeItem(new Item(ItemColor.RED));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

    }
}

