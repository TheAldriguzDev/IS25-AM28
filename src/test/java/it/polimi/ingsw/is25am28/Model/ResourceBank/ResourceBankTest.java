package it.polimi.ingsw.is25am28.Model.ResourceBank;

import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Model.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class ResourceBankTest {
    ResourceBank resourceBank;
    Board board;

    @BeforeEach
    void init() {
        this.resourceBank = new ResourceBank(2);
        this.board = new BoardLevel2();

        this.board.buildBoard();

        List<Player> players = new ArrayList<Player>();

        players.add(new Player("Player 1", PlayerColor.RED, 2));
        players.add(new Player("Player 2", PlayerColor.BLUE, 2));
        players.add(new Player("Player 3", PlayerColor.GREEN, 2));
        players.add(new Player("Player 4", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            this.board.newPlayer(player);
        }

        // Add the players to the board and create the ships for them
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);

            List<Integer> connectors = new ArrayList<Integer>();

            for (int i = 0; i < 4; i++) {
                connectors.add(THREE_PIPES.ordinal());
            }

            Cannon singleCannon1 = new Cannon(connectors, 1, "");
            Cannon singleCannon2 = new Cannon(connectors, 1, "");

            Shield shield1 = new Shield(connectors, "");

            Storage specialStorage1 = new Storage(connectors, 1, true, "");
            Storage specialStorage2 = new Storage(connectors, 2, true, "");

            Storage storage1 = new Storage(connectors, 3, false, "");
            Storage storage2 = new Storage(connectors, 2, false, "");

            Battery battery1 = new Battery(connectors, 2, "");
            Battery battery2 = new Battery(connectors, 2, "");
            Battery battery3 = new Battery(connectors, 2, "");

            Structural structural1 = new Structural(connectors, "");
            Structural structural2 = new Structural(connectors, "");
            Structural structural3 = new Structural(connectors, "");

            Cabin cabin1 = new Cabin(connectors, false, "");
            cabin1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));
            Cabin cabin2 = new Cabin(connectors, false, "");
            cabin2.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

            Vital vital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal(), "");
            Vital vital2 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal(), "");

            Engine normalEngine1 = new Engine(connectors, 1, "");
            Engine normalEngine2 = new Engine(connectors, 1, "");
            Engine normalEngine3 = new Engine(connectors, 1, "");

            Engine doubleEngine = new Engine(connectors, 2, "");

            player.getShip().addComponent(singleCannon1, 5, 6);
            player.getShip().addComponent(singleCannon2, 5, 4);
            player.getShip().addComponent(shield1, 5, 7);

            player.getShip().addComponent(specialStorage1, 6, 3);
            player.getShip().addComponent(specialStorage2, 6, 9);

            player.getShip().addComponent(battery1, 6, 4);
            player.getShip().addComponent(battery2, 6, 8);

            player.getShip().addComponent(structural1, 6, 5);
            player.getShip().addComponent(structural2, 6, 7);

            player.getShip().addComponent(structural3, 7, 3);
            player.getShip().addComponent(cabin1, 7, 4);
            player.getShip().addComponent(vital1, 7, 5);
            player.getShip().addComponent(cabin2, 7, 6);
            player.getShip().addComponent(vital2, 7, 7);
            player.getShip().addComponent(battery3, 7, 8);

            player.getShip().addComponent(normalEngine1, 8, 3);
            player.getShip().addComponent(normalEngine2, 8, 4);
            player.getShip().addComponent(normalEngine3, 8, 5);

            player.getShip().addComponent(storage1, 8, 7);
            player.getShip().addComponent(storage2, 8, 8);
            player.getShip().addComponent(doubleEngine, 8, 9);

            player.getShip().generateComponentSubLists();
        }

        // To be sure we validate the players positions
        board.validatePlayersPosition();
    }

    @Test
    void test_invalid_component_given_as_input_exception() {
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 5, 4);
        });
        assertEquals("The given component i: 5 j: 4 is not a valid Storage component", thrown.getMessage());
    }

    @Test
    void test_component_with_no_more_space_exception() {
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 8, 7);
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.YELLOW, 8, 7);
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 8, 7);

        // Instead of throwing an exception, this action is simply ignored
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 8, 7);

        /*
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 8, 7);
        });
        // We agreed on the fact that, like in the case where removing an item from a player in a
        // storage component that doesn't have that item, the exception is simply ignored, and thus not thrown
        // In this way if the player gives wrong instructions on how to manage his storage, the particular
        // actions that would have caused some problems now are simply ignored.
        // assertEquals("The given storage component has no more space", thrown.getMessage());
         */

        // Check that only the correct invocations have decreased the amount available
        assertEquals(12, resourceBank.getResourceAvailabilityFromColor(ItemColor.BLUE));
        assertEquals(16, resourceBank.getResourceAvailabilityFromColor(ItemColor.YELLOW));

        // Check that the resources are unchanged
        assertEquals(13, resourceBank.getResourceAvailabilityFromColor(ItemColor.GREEN));
        assertEquals(12, resourceBank.getResourceAvailabilityFromColor(ItemColor.RED));
    }

    @Test
    void test_normal_component_with_red_item_exception() {
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.RED, 8, 7);
        });
        assertEquals("The given storage component cannot store RED resources", thrown.getMessage());

        // Check that the resources are unchanged
        assertEquals(14, resourceBank.getResourceAvailabilityFromColor(ItemColor.BLUE));
        assertEquals(17, resourceBank.getResourceAvailabilityFromColor(ItemColor.YELLOW));
        assertEquals(13, resourceBank.getResourceAvailabilityFromColor(ItemColor.GREEN));
        assertEquals(12, resourceBank.getResourceAvailabilityFromColor(ItemColor.RED));
    }

    @Test
    void test_add_resource_to_player_from_bank() {
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 8, 7);
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.YELLOW, 8, 7);

        // Test if the resource has been added correctly
        Storage p1_storage1 = (Storage) this.board.getPlayers().get(0).getShip().getComponent(8, 7);
        assertEquals(2, p1_storage1.getStoredItems().size());
        assertEquals(1, p1_storage1.getStoredItems().stream().filter( i -> i.getColor().equals(ItemColor.BLUE)).count());
        assertEquals(1, p1_storage1.getStoredItems().stream().filter( i -> i.getColor().equals(ItemColor.YELLOW)).count());

        // Check if the bank does not have that resources anymore
        assertEquals(13, resourceBank.getResourceAvailabilityFromColor(ItemColor.BLUE));
        assertEquals(16, resourceBank.getResourceAvailabilityFromColor(ItemColor.YELLOW));
        // Check that the other resource are unchanged
        assertEquals(13, resourceBank.getResourceAvailabilityFromColor(ItemColor.GREEN));
        assertEquals(12, resourceBank.getResourceAvailabilityFromColor(ItemColor.RED));
    }

    @Test
    void test_add_resource_to_bank_from_player() {
        // Add the resource to the player
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.BLUE, 8, 7);
        resourceBank.addResourceToPlayerFromBank(this.board.getPlayers().get(0), ItemColor.YELLOW, 8, 7);

        // Get the resource from the player and add it to the bank
        resourceBank.addResourceToBankFromPlayer(this.board.getPlayers().get(0), ItemColor.YELLOW, 8, 7);

        // Test if the resource has been added and removed correctly
        Storage p1_storage1 = (Storage) this.board.getPlayers().get(0).getShip().getComponent(8, 7);
        assertEquals(1, p1_storage1.getStoredItems().size());
        assertEquals(1, p1_storage1.getStoredItems().stream().filter( i -> i.getColor().equals(ItemColor.BLUE)).count());
        assertEquals(0, p1_storage1.getStoredItems().stream().filter( i -> i.getColor().equals(ItemColor.YELLOW)).count());

        // Check if the bank does not have that resources anymore
        assertEquals(13, resourceBank.getResourceAvailabilityFromColor(ItemColor.BLUE));
        // Check that the other resource are unchanged
        assertEquals(17, resourceBank.getResourceAvailabilityFromColor(ItemColor.YELLOW));
        assertEquals(13, resourceBank.getResourceAvailabilityFromColor(ItemColor.GREEN));
        assertEquals(12, resourceBank.getResourceAvailabilityFromColor(ItemColor.RED));
    }
}