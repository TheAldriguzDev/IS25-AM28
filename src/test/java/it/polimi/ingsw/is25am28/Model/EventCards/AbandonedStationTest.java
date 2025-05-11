package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientAbandonedStation;
import it.polimi.ingsw.is25am28.Model.ActionJSON.AbandonedStationJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static it.polimi.ingsw.is25am28.Model.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationTest {
    Board board;
    ResourceBank resourceBank;

    /**
     * Method used to initialize:
     * 1. The board
     * 2. The players and add them to the board
     * 3. The players ship
     * */
    @BeforeEach
    void init() {
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

        // Add the players to the board and create the ships for each player
        // The players have the same ship
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);

            List<Integer> connectors = new ArrayList<Integer>();

            // Default connector is THREE_PIPES
            for (int i = 0; i < 4; i++) {
                connectors.add(THREE_PIPES.ordinal());
            }

            // The first player has only 4 lifeforms in his ship: 2 astronauts in the core cabin and then 2 aliens
            // The others player have 6 astronauts --> They can play the card
            if (player.getNickname().equals("Player 1")) {
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
            } else {
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
                cabin1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                cabin1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                Cabin cabin2 = new Cabin(connectors, false, "");
                cabin2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                cabin2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

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
            }

            player.getShip().generateComponentSubLists();
        }

        // To be sure we validate the players positions
        board.validatePlayersPosition();

        this.resourceBank = new ResourceBank(2);
    }

    @Test
    void test_abandoned_station_with_simple_resource_management() {
        // Set the items that will be given by the card
        ArrayList<Item> givenItems = new ArrayList<>();
        givenItems.add( new Item(ItemColor.RED));
        givenItems.add( new Item(ItemColor.YELLOW));

        // Create the card with its specs
        AbandonedStation abandonedStationCard = new AbandonedStation(
                    "abandonedStation",
                    2,
                    5,
                    1,
                    givenItems,
                    board,
                    resourceBank,
                0,
                ""
                );

        ClientAbandonedStation clientAbandonedStation;

        // ======== WIDGET TESTING ======== //
        clientAbandonedStation = new ClientAbandonedStation(abandonedStationCard.generateState());
        clientAbandonedStation.generateWidget().printWidget();
        // ================================ //

        abandonedStationCard.initCardPlayers();

        // ======== WIDGET TESTING ======== //
        clientAbandonedStation.updateCard(abandonedStationCard.generateState());
        clientAbandonedStation.generateWidget().printWidget();
        // ================================ //

        List<Player> playerList = new ArrayList<>(board.getPlayers());
        List<Integer> initialCursors = new ArrayList<>();

        // Use the cards for the given player (the cards return the player that needs to play in the state)
        while (!abandonedStationCard.hasFinished()) {
            Player currPlayer;

            AbandonedStationJSON abandonedStationJSON = new AbandonedStationJSON();

            AbandonedStation finalAbandonedStationCard = abandonedStationCard;
            Optional<Player> optionalPlayer = playerList.stream()
                    .filter(p -> p.getNickname().equals(finalAbandonedStationCard.generateState().getPlayerNickname()))
                    .findFirst();

            // If the player is present, then we can execute the action
            if (optionalPlayer.isPresent()) {
                currPlayer = optionalPlayer.get();
                initialCursors.add(currPlayer.getCursor());

                // Hp: the second player doesn't want to use the card
                if (currPlayer.getNickname().equals("Player 2")) {
                    abandonedStationJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedStationJSON.setWantToVisitStation(false);
                } else {
                    abandonedStationJSON.setPlayerNickname(currPlayer.getNickname());

                    // If the player can use the card, then we can set setWantToVisitStation as true, otherwise we must set to false
                    if (abandonedStationCard.generateState().getIsCardUsable()) {
                        abandonedStationJSON.setWantToVisitStation(true);

                        // Set the items that needs to be dropped
                        List<ComponentHelper<ItemColor>> itemsToBeRemoved = new ArrayList<>();
                        abandonedStationJSON.setItemsToBeRemoved( itemsToBeRemoved );

                        // Set the items that needs to be stored
                        List<ComponentHelper<ItemColor>> itemsToBeTaken = new ArrayList<>();

                        ComponentHelper<ItemColor> specialStorageComponent =  new ComponentHelper<ItemColor>(6, 3); // special storage for the red item
                        specialStorageComponent.addItem(ItemColor.RED);
                        itemsToBeTaken.add( specialStorageComponent );

                        ComponentHelper<ItemColor> normalStorageComponent =  new ComponentHelper<ItemColor>(8, 7); // normal storage for the yellow item
                        normalStorageComponent.addItem(ItemColor.YELLOW);
                        itemsToBeTaken.add( normalStorageComponent );

                        abandonedStationJSON.setItemsToBeTaken( itemsToBeTaken );
                    } else {
                        abandonedStationJSON.setWantToVisitStation(false);
                    }
                }

                // Use the card
                abandonedStationCard = (AbandonedStation) abandonedStationCard.useCard(abandonedStationJSON);

                // ======== WIDGET TESTING ======== //
                clientAbandonedStation.updateCard(abandonedStationCard.generateState());
                clientAbandonedStation.generateWidget().printWidget();
                // ================================ //
            }
        }

        // Check the result of the card

        // Positions
        // 1st --> cursor unchanged since he can't use the card (not enough crew members)
        // 2nd --> Didn't use the card, so the cursor remains unchanged
        // 3rd --> Use the card --> One step back, but since the previous cell is not empty this action will result in 2 steps backwards
        // 4th --> Didn't use the card since the 3rd player already used it

        assertEquals( initialCursors.get(0), playerList.get(0).getCursor() );
        assertEquals( initialCursors.get(1), playerList.get(1).getCursor() );
        assertEquals( initialCursors.get(2) - 2, playerList.get(2).getCursor() );
        assertEquals( 0, playerList.get(3).getCursor() );

        // Check if the given components have stored the correct qty and type of elements
        Storage specialStorage = (Storage) playerList.get(2).getShip().getComponent(6, 3);
        Storage normalStorage = (Storage) playerList.get(2).getShip().getComponent(8, 7);

        assertEquals(1, specialStorage.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.RED)).count());
        assertEquals(1, normalStorage.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.YELLOW)).count());
    }

    @Test
    void test_abandoned_station_with_complex_resource_management() {
        // Set the items that will be given by the card
        ArrayList<Item> givenItems = new ArrayList<>();
        givenItems.add( new Item(ItemColor.RED));
        givenItems.add( new Item(ItemColor.YELLOW));

        // Create the card with its specs
        AbandonedStation abandonedStationCard = new AbandonedStation(
                "abandonedStation",
                2,
                5,
                1,
                givenItems,
                board,
                resourceBank,
                0,
                ""
        );

        abandonedStationCard.initCardPlayers();


        List<Player> playerList = new ArrayList<>(board.getPlayers());

        // Initialize the storage component of the players with some resources

        // The special storage is full, instead the normal storage has one space left
        for (Player player : playerList) {

            // Special storage
            resourceBank.addResourceToPlayerFromBank(player, ItemColor.BLUE, 6, 3); // Add a blue item in the special storage

            // Normal storage
            resourceBank.addResourceToPlayerFromBank(player, ItemColor.BLUE, 8, 7);
            resourceBank.addResourceToPlayerFromBank(player, ItemColor.GREEN, 8, 7);
        }


        List<Integer> initialCursors = new ArrayList<>();

        // Use the cards for the given player (the cards return the player that needs to play in the state)
        while (!abandonedStationCard.hasFinished()) {
            Player currPlayer;

            AbandonedStationJSON abandonedStationJSON = new AbandonedStationJSON();

            AbandonedStation finalAbandonedStationCard = abandonedStationCard;
            Optional<Player> optionalPlayer = playerList.stream()
                    .filter(p -> p.getNickname().equals(finalAbandonedStationCard.generateState().getPlayerNickname()))
                    .findFirst();

            // If the player is present, then we can execute the action
            if (optionalPlayer.isPresent()) {
                currPlayer = optionalPlayer.get();
                initialCursors.add(currPlayer.getCursor());

                // Hp: the second player doesn't want to use the card
                if (currPlayer.getNickname().equals("Player 2")) {
                    abandonedStationJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedStationJSON.setWantToVisitStation(false);
                } else {
                    abandonedStationJSON.setPlayerNickname(currPlayer.getNickname());
                    // If the player can use the card, then we can set setWantToVisitStation as true, otherwise we must set to false
                    if (abandonedStationCard.generateState().getIsCardUsable()) {
                        abandonedStationJSON.setWantToVisitStation(true);

                        // Set the items that needs to be dropped
                        List<ComponentHelper<ItemColor>> itemsToBeRemoved = new ArrayList<>();
                        itemsToBeRemoved.add( new ComponentHelper<ItemColor>(6, 3).addItem(ItemColor.BLUE) );

                        abandonedStationJSON.setItemsToBeRemoved( itemsToBeRemoved );

                        // Set the items that needs to be stored
                        List<ComponentHelper<ItemColor>> itemsToBeTaken = new ArrayList<>();

                        itemsToBeTaken.add( new ComponentHelper<ItemColor>(6, 3).addItem(ItemColor.RED) );
                        itemsToBeTaken.add( new ComponentHelper<ItemColor>(8, 7).addItem(ItemColor.YELLOW) );
                        itemsToBeTaken.add( new ComponentHelper<ItemColor>(8, 8).addItem(ItemColor.BLUE) ); // Re add the resource that we have took from the player to change its position

                        abandonedStationJSON.setItemsToBeTaken( itemsToBeTaken );
                    } else {
                        abandonedStationJSON.setWantToVisitStation(false);
                    }
                }

                // Use the card
                abandonedStationCard = (AbandonedStation) abandonedStationCard.useCard(abandonedStationJSON);
            }
        }

        // Check the result of the card

        // Positions
        // 1st --> cursor unchanged since he can't use the card (not enough crew members)
        // 2nd --> Didn't use the card, so the cursor remains unchanged
        // 3rd --> Use the card --> One step back, but since the previous cell is not empty this action will result in 2 steps backwards
        // 4th --> Didn't use the card since the 3rd player already used it

        assertEquals( initialCursors.get(0), playerList.get(0).getCursor() );
        assertEquals( initialCursors.get(1), playerList.get(1).getCursor() );
        assertEquals( initialCursors.get(2) - 2, playerList.get(2).getCursor() );
        assertEquals( 0, playerList.get(3).getCursor() );

        // Check if the given components have stored the correct qty and type of elements
        // Check if the given components have stored the correct qty and type of elements
        Storage specialStorage = (Storage) playerList.get(2).getShip().getComponent(6, 3);
        Storage normalStorage1 = (Storage) playerList.get(2).getShip().getComponent(8, 7);
        Storage normalStorage2 = (Storage) playerList.get(2).getShip().getComponent(8, 7);

        assertEquals(1, specialStorage.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.RED)).count());

        assertEquals(1, normalStorage1.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.YELLOW)).count());
        assertEquals(1, normalStorage1.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.GREEN)).count());
        assertEquals(1, normalStorage1.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.BLUE)).count());

        assertEquals(1, normalStorage2.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.BLUE)).count());
    }

    @Test
    void test_abandoned_station_with_no_players_that_want_to_use_the_card() {
        // Set the items that will be given by the card
        ArrayList<Item> givenItems = new ArrayList<>();
        givenItems.add( new Item(ItemColor.RED));
        givenItems.add( new Item(ItemColor.YELLOW));

        // Create the card with its specs
        AbandonedStation abandonedStationCard = new AbandonedStation(
                "abandonedStation",
                2,
                5,
                1,
                givenItems,
                board,
                resourceBank,
                0,
                ""
        );

        abandonedStationCard.initCardPlayers();


        List<Player> playerList = new ArrayList<>(board.getPlayers());

        // Initialize the storage component of the players with some resources

        // The special storage is full, instead the normal storage has one space left
        for (Player player : playerList) {

            // Special storage
            resourceBank.addResourceToPlayerFromBank(player, ItemColor.BLUE, 6, 3); // Add a blue item in the special storage

            // Normal storage
            resourceBank.addResourceToPlayerFromBank(player, ItemColor.BLUE, 8, 7);
            resourceBank.addResourceToPlayerFromBank(player, ItemColor.GREEN, 8, 7);
        }


        List<Integer> initialCursors = new ArrayList<>();

        // Use the cards for the given player (the cards return the player that needs to play in the state)
        while (!abandonedStationCard.hasFinished()) {
            Player currPlayer;

            AbandonedStationJSON abandonedStationJSON = new AbandonedStationJSON();

            AbandonedStation finalAbandonedStationCard = abandonedStationCard;
            Optional<Player> optionalPlayer = playerList.stream()
                    .filter(p -> p.getNickname().equals(finalAbandonedStationCard.generateState().getPlayerNickname()))
                    .findFirst();

            // If the player is present, then we can execute the action
            if (optionalPlayer.isPresent()) {
                currPlayer = optionalPlayer.get();
                initialCursors.add(currPlayer.getCursor());

                // Hp: the second player doesn't want to use the card
                if (currPlayer.getNickname().equals("Player 2")) {
                    abandonedStationJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedStationJSON.setWantToVisitStation(false);
                } else {
                    abandonedStationJSON.setPlayerNickname(currPlayer.getNickname());
                    // If the player can use the card, then we can set setWantToVisitStation as true, otherwise we must set to false
                    if (abandonedStationCard.generateState().getIsCardUsable()) {
                        abandonedStationJSON.setWantToVisitStation(true);

                        // Set the items that needs to be dropped
                        List<ComponentHelper<ItemColor>> itemsToBeRemoved = new ArrayList<>();
                        itemsToBeRemoved.add( new ComponentHelper<ItemColor>(6, 3).addItem(ItemColor.BLUE) );

                        abandonedStationJSON.setItemsToBeRemoved( itemsToBeRemoved );

                        // Set the items that needs to be stored
                        List<ComponentHelper<ItemColor>> itemsToBeTaken = new ArrayList<>();

                        itemsToBeTaken.add( new ComponentHelper<ItemColor>(6, 3).addItem(ItemColor.RED) );
                        itemsToBeTaken.add( new ComponentHelper<ItemColor>(8, 7).addItem(ItemColor.YELLOW) );
                        itemsToBeTaken.add( new ComponentHelper<ItemColor>(8, 8).addItem(ItemColor.BLUE) ); // Re add the resource that we have took from the player to change its position

                        abandonedStationJSON.setItemsToBeTaken( itemsToBeTaken );
                    } else {
                        abandonedStationJSON.setWantToVisitStation(false);
                    }
                }

                // Use the card
                abandonedStationCard = (AbandonedStation) abandonedStationCard.useCard(abandonedStationJSON);
            }
        }

        // Check the result of the card

        // Positions
        // 1st --> cursor unchanged since he can't use the card (not enough crew members)
        // 2nd --> Didn't use the card, so the cursor remains unchanged
        // 3rd --> Use the card --> One step back, but since the previous cell is not empty this action will result in 2 steps backwards
        // 4th --> Didn't use the card since the 3rd player already used it

        assertEquals( initialCursors.get(0), playerList.get(0).getCursor() );
        assertEquals( initialCursors.get(1), playerList.get(1).getCursor() );
        assertEquals( initialCursors.get(2) - 2, playerList.get(2).getCursor() );
        assertEquals( 0, playerList.get(3).getCursor() );

        // Check if the given components have stored the correct qty and type of elements
        // Check if the given components have stored the correct qty and type of elements
        Storage specialStorage = (Storage) playerList.get(2).getShip().getComponent(6, 3);
        Storage normalStorage1 = (Storage) playerList.get(2).getShip().getComponent(8, 7);
        Storage normalStorage2 = (Storage) playerList.get(2).getShip().getComponent(8, 7);

        assertEquals(1, specialStorage.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.RED)).count());

        assertEquals(1, normalStorage1.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.YELLOW)).count());
        assertEquals(1, normalStorage1.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.GREEN)).count());
        assertEquals(1, normalStorage1.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.BLUE)).count());

        assertEquals(1, normalStorage2.getStoredItems().stream().filter(i -> i.getColor().equals(ItemColor.BLUE)).count());
    }
}