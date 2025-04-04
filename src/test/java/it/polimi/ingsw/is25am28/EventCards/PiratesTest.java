package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PiratesJSON;
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
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PiratesTest {
    Board board;
    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Ship ship_1;
    Ship ship_2;
    Ship ship_3;
    Ship ship_4;
    ActionJSON actionJSON;
    ActionJSON actionJSON1;
    ActionJSON actionJSON2;
    ActionJSON actionJSON3;
    ActionJSON actionJSON4;

    Pirates pirates;

    ArrayList<int[]> ShieldsToActivate;
    ArrayList<int[]> shieldsToActivate1;
    ArrayList<int[]> shieldsToActivate2;
    ArrayList<int[]> shieldsToActivate3;
    ArrayList<int[]> shieldsToActivate4;

    ArrayList<Integer> dicesResults;

    List<List<Integer>> shootingSequence;

    ArrayList<Integer> plasmaShot1;
    ArrayList<Integer> plasmaShot2;
    ArrayList<Integer> plasmaShot3;

    @BeforeEach
    public void init() {

        board = new BoardLevel2();
        board.buildBoard();

        List<Player> players = new ArrayList<Player>();

        players.add(new Player("Player 1", PlayerColor.RED, 2));
        players.add(new Player("Player 2", PlayerColor.BLUE, 2));
        players.add(new Player("Player 3", PlayerColor.GREEN, 2));
        players.add(new Player("Player 4", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            this.board.newPlayer(player);
        }

        players = board.getPlayers();
        p1 = players.get(0);
        p2 = players.get(1);
        p3 = players.get(2);
        p4 = players.get(3);

        shieldsToActivate1 = new ArrayList<>();
        shieldsToActivate2 = new ArrayList<>();
        shieldsToActivate3 = new ArrayList<>();
        shieldsToActivate4 = new ArrayList<>();

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

//        dicesResults = new ArrayList<>();
//        dicesResults.add(7); // dall'alto, grosso
//        dicesResults.add(5); // da sinistra, piccolo
//        dicesResults.add(6); // dall'alto, piccolo

        shootingSequence = new ArrayList<>();

        plasmaShot1 = new ArrayList<Integer>();
        plasmaShot1.add(2); // grande
        plasmaShot1.add(0); // dall'alto
        plasmaShot2 = new ArrayList<Integer>();
        plasmaShot2.add(1); // piccolo
        plasmaShot2.add(3); // da sinistra
        plasmaShot3 = new ArrayList<Integer>();
        plasmaShot3.add(1); // piccolo
        plasmaShot3.add(0); // dall'alto

        shootingSequence.add(plasmaShot1);
        shootingSequence.add(plasmaShot2);
        shootingSequence.add(plasmaShot3);


    }

    @Test void test_first_eliminated_second_third_fourth_hit() {



        pirates = new Pirates("Pirates", 2, 4, 4, 4, shootingSequence, board);



        pirates.initCardPlayers();

        Player eliminatedPlayer = p1;
        ShieldsToActivate = new ArrayList<>();
        // First round
        actionJSON = new PiratesJSON("Player 1", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 2
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        actionJSON = new PiratesJSON("Player 2", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 2
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        actionJSON3 = new PiratesJSON("Player 3", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 3
        pirates.useCard(actionJSON3);
        assertFalse(pirates.hasFinished());

        actionJSON4 = new PiratesJSON("Player 4", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 3
        pirates.useCard(actionJSON4);
        assertFalse(pirates.hasFinished());
        // End of first round

        // Start of destruction and defense rounds
        // First shot : big from above on column 7
        pirates.setDiceThrowResult(7);

        ShieldsToActivate = new ArrayList<>();
        actionJSON = new PiratesJSON("Player 1", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());


        ShieldsToActivate = new ArrayList<>();
        actionJSON = new PiratesJSON("Player 2", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        ShieldsToActivate = new ArrayList<>();
        actionJSON = new PiratesJSON("Player 3", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        ShieldsToActivate = new ArrayList<>();
        ShieldsToActivate.add(new int[] {5, 7}); // Attivazione inutile, verrà distrutto, però verifico il consumo di energia
        actionJSON = new PiratesJSON("Player 4", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        // Second shot : small from the left on row 5
        pirates.setDiceThrowResult(5);

        // First player should be no more

        ShieldsToActivate = new ArrayList<>();
        actionJSON = new PiratesJSON("Player 1", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        ShieldsToActivate = new ArrayList<>();
        ShieldsToActivate.add(new int[] {8, 5}); // Non lo proteggerà
        actionJSON = new PiratesJSON("Player 2", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        ShieldsToActivate = new ArrayList<>();
        ShieldsToActivate.add(new int[] {8, 5}); // Lo proteggerà
        actionJSON = new PiratesJSON("Player 3", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        ShieldsToActivate = new ArrayList<>();
        ShieldsToActivate.add(new int[] {8, 5});
        actionJSON = new PiratesJSON("Player 4", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        // Third shot : small from above on column 6
        pirates.setDiceThrowResult(6);

        ShieldsToActivate = new ArrayList<>();
        actionJSON = new PiratesJSON("Player 1", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());

        ShieldsToActivate = new ArrayList<>();
        ShieldsToActivate.add(new int[] {8, 5});
        actionJSON = new PiratesJSON("Player 2", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());


        ShieldsToActivate = new ArrayList<>();
        ShieldsToActivate.add(new int[] {8, 5});
        actionJSON = new PiratesJSON("Player 3", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertFalse(pirates.hasFinished());


        ShieldsToActivate = new ArrayList<>();
        actionJSON = new PiratesJSON("Player 4", false, ShieldsToActivate, new ArrayList<>());
        pirates.useCard(actionJSON);
        assertTrue(pirates.hasFinished());












//        // System.out.println("ship_1 before destruction");
//        printGrid(ship_1);
//        pirates.useCard(actionJSON1);
//        assertFalse(pirates.hasFinished());
//        // System.out.println("ship_1 after destruction");
//        printGrid(ship_1);
//
//        // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
//
//        // System.out.println("ship_2 before destruction");
//        printGrid(ship_2);
//        assertFalse(pirates.hasFinished());
//        pirates.useCard(actionJSON2);
//        // System.out.println("ship_2 after destruction");
//        printGrid(ship_2);
//
//        // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
//
//        // System.out.println("ship_3 before destruction");
//        printGrid(ship_3);
//        assertFalse(pirates.hasFinished());
//        pirates.useCard(actionJSON3);
//        // System.out.println("ship_3 after destruction");
//        printGrid(ship_3);
//
//        // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
//
//        // System.out.println("ship_4 before destruction");
//        printGrid(ship_4);
//        assertFalse(pirates.hasFinished());
//        pirates.useCard(actionJSON4);
//        // System.out.println("ship_4 after destruction");
//        printGrid(ship_4);
//
//        assertTrue(pirates.hasFinished());

        assertNull(ship_2.getComponent(6, 7));
        assertNull(ship_2.getComponent(5, 6));

        assertNull(ship_3.getComponent(6, 7));
        assertNotNull(ship_3.getComponent(5, 6));

        assertNull(ship_4.getComponent(5, 7));
        assertNull(ship_4.getComponent(5, 6));

        assertEquals(board.getEliminatedPlayers().size(), 1);
        assertEquals(eliminatedPlayer, board.getEliminatedPlayers().getFirst());
        // In visual representation the core is still present since trying to remove it triggers an exception
    }

    @Test
    public void test_first_defeats_and_takes_credits() {

        ShieldsToActivate = new ArrayList<>();

        ArrayList<Pair<Integer, Integer>> doubleCannonActivated = new ArrayList<>();
        doubleCannonActivated.add(new Pair<>(5, 6));

        actionJSON1 = new PiratesJSON("Player 1", true, ShieldsToActivate, doubleCannonActivated); // Total FirePower: 4
        actionJSON2 = new PiratesJSON("Player 2", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 2
        actionJSON3 = new PiratesJSON("Player 3", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 3
        actionJSON4 = new PiratesJSON("Player 4", false, ShieldsToActivate, new ArrayList<>()); // Total FirePower: 3

        pirates = new Pirates("Pirates", 2, 3, 4, 4, shootingSequence, board);

        pirates.initCardPlayers();

        ArrayList<Integer> playerPositionsBefore = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            playerPositionsBefore.add(player.getCursor());
        }

        pirates.useCard(actionJSON1);
        assertFalse(pirates.hasFinished());

        assertFalse(pirates.hasFinished());
        pirates.useCard(actionJSON2);

        assertFalse(pirates.hasFinished());
        pirates.useCard(actionJSON3);

        assertFalse(pirates.hasFinished());
        pirates.useCard(actionJSON4);

        assertTrue(pirates.hasFinished());

        assertEquals(playerPositionsBefore.get(0) - 7, p1.getCursor()); // 4 steps + 3 jumps over players (2, 3, 4)
        assertEquals(playerPositionsBefore.get(1), p2.getCursor());
        assertEquals(playerPositionsBefore.get(2), p3.getCursor());
        assertEquals(playerPositionsBefore.get(3), p4.getCursor());

        assertEquals(4, p1.getCredits());
        assertEquals(0, p2.getCredits());
        assertEquals(0, p3.getCredits());
        assertEquals(0, p4.getCredits());

    }

    public void printGrid(Ship ship) {
        char[][] grid = new char[12][12];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = '-';
            }
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (ship.getComponent(i, j) != null) {
                    if(i == 6 && j == 6) {
                        grid[i][j] = 'C';
                    } else {
                        grid[i][j] = 'X';
                    }

                }
            }
        }
        for (int i = 0; i < grid.length; i++) {
            // System.out.println();
            for (int j = 0; j < grid[i].length; j++) {
                // System.out.print(grid[i][j] + " ");
            }
        }
        // System.out.println();
    }

    public void ship_init1(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio viene attivato

        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors1, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors2, false);
        Cannon cannon_1 = new Cannon(connectors3, 2);
        Cannon cannon_2 = new Cannon(connectors4, 1);
        Cannon cannon_3 = new Cannon(connectors5, 1);
        Vital vital_1 = new Vital(connectors4, 0);
        Battery battery_1 = new Battery(connectors5, 3);
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

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));



    }

    public void ship_init2(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio non viene attivato

        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors2, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors6, false);
        Cannon cannon_1 = new Cannon(connectors3, 2);
        Cannon cannon_2 = new Cannon(connectors1, 1);
        Cannon cannon_3 = new Cannon(connectors5, 1);
        Vital vital_1 = new Vital(connectors7, 0);
        Battery battery_1 = new Battery(connectors1, 3);
        Storage storage_1 = new Storage(connectors5, 3, false);
        Shield shield_1 = new Shield(connectors8);
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
        ship.addComponent(shield_1, 8, 5);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));




    }

    public void ship_init3(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone

        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors2, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors2, false);
        Cannon cannon_1 = new Cannon(connectors3, 1);
        Cannon cannon_2 = new Cannon(connectors1, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
        Vital vital_1 = new Vital(connectors7, 0);
        Battery battery_1 = new Battery(connectors1, 3);
        Storage storage_1 = new Storage(connectors5, 3, false);
        Storage storage_2 = new Storage(connectors5, 2, true);
        Shield shield_1 = new Shield(connectors8);
        shield_1.rotateLeft();


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
        ship.addComponent(shield_1, 8, 5);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        shieldsToActivate3.add(new int[] {8, 5});

    }

    public void ship_init4(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone

        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors2, false);
        Cabin cabin_2 = new Cabin(connectors2, false);
        Cabin cabin_3 = new Cabin(connectors6, false);
        Cannon cannon_1 = new Cannon(connectors3, 1);
        Cannon cannon_2 = new Cannon(connectors2, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
        Vital vital_1 = new Vital(connectors7, 0);
        Battery battery_1 = new Battery(connectors1, 3);
        Storage storage_1 = new Storage(connectors5, 3, false);
        Storage storage_2 = new Storage(connectors5, 2, true);
        Shield shield_1 = new Shield(connectors8);
        Shield shield_2 = new Shield(connectors3);
        shield_1.rotateLeft();
        shield_1.rotateLeft();
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
        ship.addComponent(shield_1, 8, 5);
        ship.addComponent(shield_2, 5, 7);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));



    }
}