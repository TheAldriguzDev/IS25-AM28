package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class SlaversTest {
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

    Slavers slavers;

    ArrayList<ComponentHelper<LifeformType>> crewToRemove1;
    ArrayList<ComponentHelper<LifeformType>>  crewToRemove2;
    ArrayList<ComponentHelper<LifeformType>>  crewToRemove3;
    ArrayList<ComponentHelper<LifeformType>>  crewToRemove4;

    List<Cabin> cabinList1;
    List<Cabin> cabinList2;
    List<Cabin> cabinList3;
    List<Cabin> cabinList4;

    @BeforeEach
    void init() {

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

        crewToRemove1 = new ArrayList<>();
        crewToRemove2 = new ArrayList<>();
        crewToRemove3 = new ArrayList<>();
        crewToRemove4 = new ArrayList<>();

        cabinList1 = new ArrayList<>();
        cabinList2 = new ArrayList<>();
        cabinList3 = new ArrayList<>();
        cabinList4 = new ArrayList<>();


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

    }

    @Test
    public void test_first_player_loses_crew_second_player_eliminated_third_player_defeats_slavers_and_takes_loot_fourth_player_does_not_use_the_card() {
        slavers = new Slavers("Slavers", 2, 4, 2, 4, 6, board);

        ArrayList<Pair<Integer, Integer>> doubleCannonActivated = new ArrayList<>();
        doubleCannonActivated.add(new Pair<>(7, 9));

        actionJSON1 = new SlaversJSON("Player 1", false, crewToRemove1, new ArrayList<>()); // Total FirePower: 3
        actionJSON2 = new SlaversJSON("Player 2", false, crewToRemove2, new ArrayList<>()); // Total FirePower: 3
        actionJSON3 = new SlaversJSON("Player 3", true, crewToRemove3, doubleCannonActivated); // Total FirePower: 5
        actionJSON4 = new SlaversJSON("Player 4", false, crewToRemove4, new ArrayList<>()); // Total FirePower: 3

        // Player 1 should lose all crew members except for a single Astronaut in the core
        crewToRemove1.add(new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6, 7).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(7, 6).addItem(LifeformType.BROWN_ALIEN));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6, 7).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));

        ArrayList<Integer> occupiedSpace1Before = new ArrayList<>();
        for(Cabin cabin : cabinList1) {
            occupiedSpace1Before.add(cabin.getInhabitants().size());
        }
        // Player 2 should be eliminated
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT)));


        Player eliminatedPlayer = board.getPlayers().get(1);

        ArrayList<Integer> playerPositionsBefore = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            playerPositionsBefore.add(player.getCursor());
        }

        slavers.initCardPlayers();

        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON1);
        }
        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON2);
        }
        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON3);
        }

        assertTrue(slavers.hasFinished());

        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON4);
        }


        assertEquals(occupiedSpace1Before.get(0) - 1, cabinList1.get(0).getInhabitants().size());
        assertEquals(occupiedSpace1Before.get(1) - 2, cabinList1.get(1).getInhabitants().size());
        assertEquals(occupiedSpace1Before.get(2) - 2, cabinList1.get(2).getInhabitants().size());
        assertEquals(occupiedSpace1Before.get(3) - 1, cabinList1.get(3).getInhabitants().size());

        assertEquals(board.getEliminatedPlayers().size(), 1);
        assertEquals(eliminatedPlayer, board.getEliminatedPlayers().getFirst());

        assertEquals(playerPositionsBefore.get(0), p1.getCursor());
        assertEquals(playerPositionsBefore.get(2) - 2 - 1, p3.getCursor()); // 2 steps backwards + jump over p4
        assertEquals(playerPositionsBefore.get(3), p4.getCursor());

        assertEquals(0, p1.getCredits());

        assertEquals(4, p3.getCredits());
        assertEquals(0, p4.getCredits());

    }

    @Test
    public void test_first_three_players_tie_fourth_one_wins() {
        slavers = new Slavers("Slavers", 2, 3, 2, 4, 6, board);

        ArrayList<Pair<Integer, Integer>> doubleCannonActivated = new ArrayList<>();
        doubleCannonActivated.add(new Pair<>(7, 9));

        actionJSON1 = new SlaversJSON("Player 1", false, crewToRemove1, new ArrayList<>()); // Total FirePower: 3
        actionJSON2 = new SlaversJSON("Player 2", false, crewToRemove2, new ArrayList<>()); // Total FirePower: 3
        actionJSON3 = new SlaversJSON("Player 3", false, crewToRemove3, new ArrayList<>()); // Total FirePower: 3
        actionJSON4 = new SlaversJSON("Player 4", true, crewToRemove4, doubleCannonActivated); // Total FirePower: 5

        ArrayList<Integer> playerPositionsBefore = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            playerPositionsBefore.add(player.getCursor());
        }

        slavers.initCardPlayers();

        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON1);
        }

        assertFalse(slavers.hasFinished());

        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON2);
        }

        assertFalse(slavers.hasFinished());

        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON3);
        }

        assertFalse(slavers.hasFinished());

        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON4);
        }

        assertTrue(slavers.hasFinished());

        assertEquals(playerPositionsBefore.get(0), p1.getCursor());
        assertEquals(playerPositionsBefore.get(1), p2.getCursor());
        assertEquals(playerPositionsBefore.get(2), p3.getCursor());
        assertEquals(playerPositionsBefore.get(3) - 2, p4.getCursor());

        assertEquals(0, p1.getCredits());
        assertEquals(0, p2.getCredits());
        assertEquals(0, p3.getCredits());
        assertEquals(4, p4.getCredits());


    }

    public void ship_init1(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio viene attivato


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 2);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 0}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Cannon cannon_4 = new Cannon(new int[] {0, 0, 0, 1}, 1);
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
          ship.addComponent(cannon_4, 7, 8);
          ship.addComponent(vital_1, 7, 5);
          ship.addComponent(battery_1, 7, 7);

          ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));






        cabinList1.add(ship.getCabinList().getFirst());
        cabinList1.add(cabin_1);
        cabinList1.add(cabin_2);
        cabinList1.add(cabin_3);

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
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 0}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Cannon cannon_4 = new Cannon(new int[] {0, 0, 0, 1}, 1);
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
        ship.addComponent(cannon_4, 7, 9);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabinList2.add(ship.getCabinList().getFirst());
        cabinList2.add(cabin_1);
        cabinList2.add(cabin_2);
        cabinList2.add(cabin_3);

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
        Cannon cannon_4 = new Cannon(new int[] {0, 0, 0, 1}, 2);
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
        ship.addComponent(cannon_4, 7, 9);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabinList3.add(ship.getCabinList().getFirst());
        cabinList3.add(cabin_1);
        cabinList3.add(cabin_2);
        cabinList3.add(cabin_3);

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
        Cannon cannon_4 = new Cannon(new int[] {0, 0, 0, 1}, 2);
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
        ship.addComponent(cannon_4, 7, 9);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabinList4.add(ship.getCabinList().getFirst());
        cabinList4.add(cabin_1);
        cabinList4.add(cabin_2);
        cabinList4.add(cabin_3);

    }

}