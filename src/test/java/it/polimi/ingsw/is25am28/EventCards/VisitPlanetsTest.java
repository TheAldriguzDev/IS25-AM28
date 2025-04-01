package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.VisitPlanetsJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;

import it.polimi.ingsw.is25am28.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class VisitPlanetsTest {
    private ResourceBank resourceBank;
    private Board board;
    private VisitPlanets visitPlanets;
    private int movementStep = 5;

//    void printAvailableResources(ResourceBank resourceBank) {
//        System.out.println("\n ==== CURR RESOURCE BANK CONTENTS ====");
//        System.out.println(" - BLUE(1) = " + resourceBank.getResources().get(ItemColor.BLUE));
//        System.out.println(" - GREEN(2) = " + resourceBank.getResources().get(ItemColor.GREEN));
//        System.out.println(" - YELLOW(3) = " + resourceBank.getResources().get(ItemColor.YELLOW));
//        System.out.println(" - RED(4) = " + resourceBank.getResources().get(ItemColor.RED));
//        System.out.println(" ===================================== \n");
//    }

    void initCustomShip(Player player) {
        Ship ship = player.getShip();

        int[] connectors = new int[4];

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery tripleBattery1 = new Battery(connectors, 6);

        Cannon singleCannon1 = new Cannon(connectors, 1);
        Cannon singleCannon2 = new Cannon(connectors, 1);
        Cannon singleCannon3 = new Cannon(connectors, 1);
        Cannon doubleCannon1 = new Cannon(connectors, 2);
        Cannon doubleCannon2 = new Cannon(connectors, 2);

        Engine singleEngine1 = new Engine(connectors, 1);
        Engine singleEngine2 = new Engine(connectors, 1);
        Engine singleEngine3 = new Engine(connectors, 1);
        Engine doubleEngine1 = new Engine(connectors, 2);

        Cabin cabin1 = new Cabin(connectors, false);

        Shield shield1 = new Shield(connectors);

        Storage normalDoubleStorage1 = new Storage(connectors, 2, false);
        Storage normalTripleStorage1 = new Storage(connectors, 3, false);
        Storage specialSingleStorage1 = new Storage(connectors, 1, true);
        Storage specialDoubleStorage1 = new Storage(connectors, 2, true);

        Structural structural1 = new Structural(connectors);

        Vital purpleVital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
        Vital brownVital1 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

        // Adding the components created above
        ship.addComponent(doubleCannon1, 7, 3);
        ship.addComponent(singleCannon3, 7, 9);
        ship.addComponent(singleCannon1, 5, 5);
        ship.addComponent(specialDoubleStorage1, 5, 6);
        ship.addComponent(singleCannon2, 5, 7);
        ship.addComponent(shield1, 6, 4);
        ship.addComponent(normalTripleStorage1, 6, 5);
        ship.addComponent(specialSingleStorage1, 6, 7);
        ship.addComponent(doubleCannon2, 6, 8);
        ship.addComponent(tripleBattery1, 7, 4);
        ship.addComponent(normalDoubleStorage1, 7, 5);
        ship.addComponent(purpleVital1, 7, 6);
        ship.addComponent(cabin1, 7, 7);
        ship.addComponent(brownVital1, 7, 8);
        ship.addComponent(singleEngine1, 8, 4);
        ship.addComponent(singleEngine2, 8, 5);
        ship.addComponent(doubleEngine1, 8, 7);
        ship.addComponent(singleEngine3, 8, 8);

        // Generating the component sub-lists right after the ship is created
        ship.generateComponentSubLists();

        // Verifying that the ship is correctly built according to
        // ship building rules and each component's positioning rules
        assertTrue(ship.validateShip());

        Item red = new Item(ItemColor.RED);
        Item yellow = new Item(ItemColor.YELLOW);
        Item green = new Item(ItemColor.GREEN);
        Item blue = new Item(ItemColor.BLUE);

        // Random seed is instantiated by the number of the current player
        Random random = new Random(player.getNickname().charAt(1) - '0');

        // Random storage assignment to each player
        int len = ship.getAvailableStorageSpace() - random.nextInt(0,ship.getAvailableStorageSpace() / 2);

        // Storing random items
        for (Storage storage : ship.getStorageList()) {
            int randomColor = random.nextInt(1, 5);

            if (len <= 0) break;

            switch (randomColor) {
                // Blue
                case 1 -> {
                    storage.storeItem(blue);
                    len--;
                }
                // Green
                case 2 -> {
                    storage.storeItem(green);
                    len--;
                }
                // Yellow
                case 3 -> {
                    storage.storeItem(yellow);
                    len--;
                }
                // Red
                case 4 -> {
                    if (storage.isSpecialStorage()) {
                        storage.storeItem(red);
                        len--;
                    }
                    else {
                        // By default, the item stored is blue if
                        // the given storage is not special
                        storage.storeItem(blue);
                    }
                }
            }
        }
    }

//    void visualizeVisitPlanetsCardStateParameters(CardStateJSON cardStateJSON) {
//        try {
//            System.out.println(cardStateJSON.getPlayerNickname());
//        }
//        catch (Exception e) {
//            System.out.println("null");
//        }
//        System.out.println(cardStateJSON.getCardName());
//        System.out.println(cardStateJSON.getCardLevel());
//        System.out.println(cardStateJSON.getIsCardUsable());
//        System.out.println(cardStateJSON.getAvailablePlanets());
//    }
//
//    void visualizeAllStoragesCoordinatesAndContents(Player player) {
//        System.out.println("\n ==== \"" + player.getNickname() + "\" STORAGE CONTENTS ==== ");
//        for (Storage storage : player.getShip().getStorageList()) {
//            System.out.println(" - (isSpecialStorage=" + storage.isSpecialStorage() + ")");
//            System.out.println(" - (" + storage.getPosition()[0] + ", " + storage.getPosition()[1] + ")");
//            System.out.println(" - (maxCapacity=" + storage.getCapacity() + ", occupied=" + storage.getStoredItems().size() + ")");
//            System.out.println(" - stored = " + storage.getStoredItems().stream().map(Item::getColor).toList() + "\n");
//        }
//        System.out.println(" ========================== \n");
//    }

    @BeforeEach
    void init() {
        this.resourceBank = new ResourceBank();
        this.board = new BoardLevel2();
        Random random = new Random(100);    // Fixed seed for predictable results
        List<Map<String, Integer>> itemsPerPlanet = new ArrayList<>();

        this.board.newPlayer("P1", PlayerColor.BLUE);
        this.board.newPlayer("P2", PlayerColor.GREEN);
        this.board.newPlayer("P3", PlayerColor.YELLOW);
        this.board.newPlayer("P4", PlayerColor.RED);

        // Creating the level 2 board
        this.board.buildBoard();

        // Putting each player into their starting positions
        this.board.addPlayerToBoard(this.board.getPlayers().get(0));
        this.board.addPlayerToBoard(this.board.getPlayers().get(1));
        this.board.addPlayerToBoard(this.board.getPlayers().get(2));
        this.board.addPlayerToBoard(this.board.getPlayers().get(3));

        for (Player player : this.board.getPlayers()) {
            initCustomShip(player);
        }

        for (int planetID = 0; planetID < 4; planetID++) {
            Map<String, Integer> planetConfig = new HashMap<>();

            // Each of the 4 planets has a random amount of items
            planetConfig.put("blue", random.nextInt(0, 4));
            planetConfig.put("green", random.nextInt(0, 3));
            planetConfig.put("yellow", random.nextInt(0, 2));
            planetConfig.put("red", random.nextInt(0, 3));

            itemsPerPlanet.add(planetID, planetConfig);
        }

        this.visitPlanets = new VisitPlanets(
            "VisitPlanets",
            2,
            this.movementStep,
            itemsPerPlanet,
            this.resourceBank,
            this.board
        );

        // Initializing the internal player list
        this.visitPlanets.initCardPlayers();
    }

    @Test
    void useCard_fourPlayersAndEachChoosesADifferentPlanet() {
        VisitPlanetsJSON visitPlanetsJSON;
        CardStateJSON cardStateJSON;
        List<ComponentHelper<ItemColor>> itemsToDrop;
        List<ComponentHelper<ItemColor>> itemsToTake;
        List<ItemColor> expectedStorageContents;
        List<Integer> chosenPlanets = new ArrayList<>();
        List<Integer> initialPositions = new ArrayList<>();
        Storage storageToCheck;
        Player currPlayer;
        int chosenPlanetIndex;


        // Compiling the initial positions of all players in the board
        initialPositions.add(this.board.getPlayers().get(0).getCursor());
        initialPositions.add(this.board.getPlayers().get(1).getCursor());
        initialPositions.add(this.board.getPlayers().get(2).getCursor());
        initialPositions.add(this.board.getPlayers().get(3).getCursor());

//        System.out.println(initialPositions);

        // Player 1 (P1) - P1 plays correctly (he's not greedy nor nihilist)
        // --> P1 will: drop (2 BLUE) and take (1 YELLOW, 1 GREEN, 2 BLUE) from planet with planetID=0
        //     (i.e.: The 2 BLUE items are moved into another storage)
        chosenPlanetIndex = 0;
        cardStateJSON = this.visitPlanets.generateState();

        // Verify that all planets are currently available
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(2));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(3));

//        System.out.println("\n ======== P1 BEFORE ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(this.visitPlanets.currentPlayer.get());

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.YELLOW));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.GREEN));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));

        visitPlanetsJSON = new VisitPlanetsJSON(
            chosenPlanetIndex,
            itemsToDrop,
            itemsToTake
        );

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            throw new IllegalArgumentException("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P1 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P1 cannot
        // be chosen by the other players
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(2));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(3));

        // Verify that the card is not finished yet
        assertTrue(cardStateJSON.getIsCardUsable());

        // Visualize changes
//        System.out.println("\n ======== P1 AFTER ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(currPlayer);

        // Verify that the correct resources are dropped and taken by P1
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.RED);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        expectedStorageContents.add(ItemColor.YELLOW);
        expectedStorageContents.add(ItemColor.GREEN);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));



        // Player 2 (P2) - P2 will try to take more resources than available on his planet (greedy behaviour)
        //                 and he will try to overflow his storage
        // --> P2 will: drop (nothing) and take (all) from planet with planetID=2
        //              (NOTE: 2 BLUE are reorganized, they're not dropped and thus nor deposited to the resourceBank)
        chosenPlanetIndex = 2;
        cardStateJSON = this.visitPlanets.generateState();
//        System.out.println("\n ======== P2 BEFORE ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(this.visitPlanets.currentPlayer.get());

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));
        itemsToDrop.add(new ComponentHelper<ItemColor>(6, 7).addItem(ItemColor.BLUE));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.GREEN));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 7).addItem(ItemColor.YELLOW));
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.RED));
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.RED));

        visitPlanetsJSON = new VisitPlanetsJSON(
            chosenPlanetIndex,
            itemsToDrop,
            itemsToTake
        );

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            throw new IllegalArgumentException("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P2 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

//        try {
//            this.visitPlanets.useCard(visitPlanetsJSON);
//        }
//        catch (IllegalStateException e) {
//            // Overflow causes this
//            System.out.println("Storage Overflow CAUGHT");
//        }
//        catch (Exception e) {
//            throw new Error("ERROR: Storage overflow wasn't caught");
//        }

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P2 cannot
        // be chosen by the other players
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(2));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(3));

        // Verify that the card is not finished yet
        assertTrue(cardStateJSON.getIsCardUsable());

        // Visualize changes
//        System.out.println("\n ======== P2 AFTER ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(currPlayer);

        // Verify that the correct resources are dropped and taken by P2
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.RED);
        expectedStorageContents.add(ItemColor.RED);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));



        // Player 3 (P3) - P3 will drop everything he had and take everything the planet has
        // --> P3 will: drop (all) and take (all) from his chosen planer with planetID=3
        //     (HOWEVER: Even if he wants to drop/take stuff, he doesn't want to land, therefore his storage stays the same)
        chosenPlanetIndex = 3;
        cardStateJSON = this.visitPlanets.generateState();
//        System.out.println("\n ======== P3 BEFORE ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(this.visitPlanets.currentPlayer.get());

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));
        itemsToDrop.add(new ComponentHelper<ItemColor>(6, 7).addItem(ItemColor.GREEN));
        itemsToDrop.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.YELLOW));
        itemsToDrop.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.GREEN));

        // P3 doesn't land on the planet, therefore gives an empty/null response
//        visitPlanetsJSON = new VisitPlanetsJSON(
//            chosenPlanetIndex,
//            itemsToDrop,
//            itemsToTake
//        );
        // Equivalent to an empty ActionJSON to signal the fact
        // that the player doesn't want to land on a planet
        visitPlanetsJSON = null;

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            throw new IllegalArgumentException("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        // visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P3 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P3 is not available
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(2));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(3));

        // Verify that the card is not finished yet
        assertTrue(cardStateJSON.getIsCardUsable());

        // Visualize changes
//        System.out.println("\n ======== P3 AFTER ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(currPlayer);

        // Verify that the correct resources are dropped and taken by P3
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.GREEN);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));



        // Player 4 (P4) - Chooses the last planet and fills his storage
        chosenPlanetIndex = 1;
        cardStateJSON = this.visitPlanets.generateState();
//        System.out.println("\n ======== P4 BEFORE ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(this.visitPlanets.currentPlayer.get());

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.RED));
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.RED));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));

        // P3 doesn't land on the planet, therefore just occupies it in orbit,
        // preventing P4 from choosing such planet
        visitPlanetsJSON = new VisitPlanetsJSON(
            chosenPlanetIndex,
            itemsToDrop,
            itemsToTake
        );

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            throw new IllegalArgumentException("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P4 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P4 cannot
        // be chosen by the other players
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(1));
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(2));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(3));

        // Since P4 is the last player, the state should say that the card is not usable anymore
        assertFalse(cardStateJSON.getIsCardUsable());

        // Visualize changes
//        System.out.println("\n ======== P4 AFTER ========");
//        printAvailableResources(this.resourceBank);
//        visualizeVisitPlanetsCardStateParameters(cardStateJSON);
//        visualizeAllStoragesCoordinatesAndContents(currPlayer);

        // Verify that the correct resources are dropped and taken by P4
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.RED);
        expectedStorageContents.add(ItemColor.RED);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.RED);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.GREEN);
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

//        System.out.println(this.visitPlanets.getBoard().getPlayers().get(0).getCursor());
//        System.out.println(this.visitPlanets.getBoard().getPlayers().get(1).getCursor());
//        System.out.println(this.visitPlanets.getBoard().getPlayers().get(2).getCursor());
//        System.out.println(this.visitPlanets.getBoard().getPlayers().get(3).getCursor());

        // (FINAL) Check that each player that decided to land got moved backwards
        // A list of the previous positions is needed to confront them with the new positions
        assertEquals(initialPositions.get(3) - this.movementStep, this.visitPlanets.getBoard().getPlayers().get(3).getCursor());
        assertEquals(initialPositions.get(2), this.visitPlanets.getBoard().getPlayers().get(2).getCursor());
        assertEquals(initialPositions.get(1) - this.movementStep - 1, this.visitPlanets.getBoard().getPlayers().get(1).getCursor());
        assertEquals(initialPositions.get(0) - this.movementStep - 1, this.visitPlanets.getBoard().getPlayers().get(0).getCursor());
    }

    @Test
    void useCard_twoPlayersAndTwoPlanets() {
        VisitPlanets twoPlanetVisitPlanets;
        VisitPlanetsJSON visitPlanetsJSON;
        CardStateJSON cardStateJSON;
        List<ComponentHelper<ItemColor>> itemsToDrop;
        List<ComponentHelper<ItemColor>> itemsToTake;
        List<ItemColor> expectedStorageContents;
        List<Integer> chosenPlanets = new ArrayList<>();
        List<Integer> initialPositions = new ArrayList<>();
        Storage storageToCheck;
        Player currPlayer;
        int chosenPlanetIndex;
        Random random = new Random(100);
        List<Map<String, Integer>> itemsPerPlanet = new ArrayList<>();

        // Compiling the initial positions of all players in the board
        initialPositions.add(this.board.getPlayers().get(0).getCursor());
        initialPositions.add(this.board.getPlayers().get(1).getCursor());
        initialPositions.add(this.board.getPlayers().get(2).getCursor());
        initialPositions.add(this.board.getPlayers().get(3).getCursor());

        // Removing the last player since this test works with 2 planets and 3 players
        // (1 player does not land on a planet, whereas the other 2 do so)
        this.board.eliminatePlayer(this.board.getPlayers().getLast());

        // Creating the two planet VisitPlanets card
        for (int planetID = 0; planetID < 2; planetID++) {
            Map<String, Integer> planetConfig = new HashMap<>();

            // Each of the 4 planets has a random amount of items
            planetConfig.put("blue", random.nextInt(0, 4));
            planetConfig.put("green", random.nextInt(0, 3));
            planetConfig.put("yellow", random.nextInt(0, 2));
            planetConfig.put("red", random.nextInt(0, 3));

            itemsPerPlanet.add(planetID, planetConfig);
        }

        this.visitPlanets = new VisitPlanets(
            "VisitPlanets",
            2,
            this.movementStep,
            itemsPerPlanet,
            this.resourceBank,
            this.board
        );

        // Initializing the internal player list
        this.visitPlanets.initCardPlayers();

        // Compiling the initial positions of all players in the board
        initialPositions.add(this.board.getPlayers().get(0).getCursor());
        initialPositions.add(this.board.getPlayers().get(1).getCursor());
        initialPositions.add(this.board.getPlayers().get(2).getCursor());

        // Player 1 (P1) - P1 plays correctly (he's not greedy nor nihilist)
        // --> P1 will: drop (2 BLUE) and take (1 YELLOW, 1 GREEN, 2 BLUE) from planet with planetID=0
        //     (i.e.: The 2 BLUE items are moved into another storage)
        chosenPlanetIndex = 0;
        cardStateJSON = this.visitPlanets.generateState();

        // Verify that all planets are currently available
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.YELLOW));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.GREEN));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));

        visitPlanetsJSON = new VisitPlanetsJSON(
            chosenPlanetIndex,
            itemsToDrop,
            itemsToTake
        );

        currPlayer = null;

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            fail("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P1 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P1 cannot
        // be chosen by the other players
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));

        // Verify that the card is not finished yet
        assertTrue(cardStateJSON.getIsCardUsable());

        // Verify that the correct resources are dropped and taken by P1
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.RED);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        expectedStorageContents.add(ItemColor.YELLOW);
        expectedStorageContents.add(ItemColor.GREEN);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));



        // Player 2 (P2) - P2 will drop everything he had and take everything the planet has
        // --> P2 will: drop (all) and take (all) from his chosen planer with planetID=3
        //     (HOWEVER: Even if he wants to drop/take stuff, he doesn't want to land, therefore his storage stays the same)
        chosenPlanetIndex = -1; // Doesn't care about landing
        cardStateJSON = this.visitPlanets.generateState();

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));
        itemsToDrop.add(new ComponentHelper<ItemColor>(6, 7).addItem(ItemColor.GREEN));
        itemsToDrop.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.YELLOW));
        itemsToDrop.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.GREEN));

        // P2 doesn't land on the planet
        visitPlanetsJSON = new VisitPlanetsJSON(
            chosenPlanetIndex,
            itemsToDrop,
            itemsToTake
        );

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            throw new IllegalArgumentException("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P2 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P2 is still available,
        // since he specified that he did not want to land
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertTrue(cardStateJSON.getAvailablePlanets().containsKey(1));

        // Verify that the card is not finished yet
        assertTrue(cardStateJSON.getIsCardUsable());

        // Verify that the correct resources are dropped and taken by P2
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));



        // Player 3 (P3) - P3 will try to take more resources than available on his planet (greedy behaviour)
        //                 and he will try to overflow his storage
        // --> P3 will: drop (nothing) and take (all) from planet with planetID=2
        //              (NOTE: 1 BLUE and 1 GREEN are reorganized, they're not dropped and thus nor deposited to the resourceBank)
        chosenPlanetIndex = 1;
        cardStateJSON = this.visitPlanets.generateState();

        itemsToDrop = new ArrayList<>();
        itemsToDrop.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.BLUE));
        itemsToDrop.add(new ComponentHelper<ItemColor>(6, 7).addItem(ItemColor.GREEN));

        itemsToTake = new ArrayList<>();
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.BLUE));
        itemsToTake.add(new ComponentHelper<ItemColor>(7, 5).addItem(ItemColor.GREEN));
        itemsToTake.add(new ComponentHelper<ItemColor>(6, 7).addItem(ItemColor.YELLOW));
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.RED));
        itemsToTake.add(new ComponentHelper<ItemColor>(5, 6).addItem(ItemColor.RED));

        visitPlanetsJSON = new VisitPlanetsJSON(
            chosenPlanetIndex,
            itemsToDrop,
            itemsToTake
        );

        if (this.visitPlanets.getCurrentPlayer().isPresent()) {
            currPlayer = this.visitPlanets.getCurrentPlayer().get();
        }
        else {
            throw new IllegalArgumentException("ERROR: Current player is null");
        }

        // Adding the current player to the payload to identify who sent it
        visitPlanetsJSON.setPlayerNickname(currPlayer.getNickname());

        // P3 uses the card
        this.visitPlanets.useCard(visitPlanetsJSON);

        // Adding the chosen planetID to the list of used ones
        chosenPlanets.add(chosenPlanetIndex);

        // Verifying that the planetID chosen by P3 cannot
        // be chosen by the other players
        cardStateJSON = this.visitPlanets.generateState();
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(0));
        assertFalse(cardStateJSON.getAvailablePlanets().containsKey(1));

        // Verify that, since all planets have been chosen, P4 will not be able to choose, thus
        // he should be skipped and the card should be then marked as used
        assertFalse(cardStateJSON.getIsCardUsable());

        // Verify that the correct resources are dropped and taken by P3
        // specialDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(5, 6);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.RED);
        expectedStorageContents.add(ItemColor.RED);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // specialSingleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 7);
        expectedStorageContents = new ArrayList<ItemColor>();
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalTripleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(6, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.YELLOW);
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // normalDoubleStorage1
        storageToCheck = (Storage) currPlayer.getShip().getComponent(7, 5);
        expectedStorageContents = new ArrayList<ItemColor>();
        expectedStorageContents.add(ItemColor.BLUE);
        expectedStorageContents.add(ItemColor.BLUE);
        assertEquals(expectedStorageContents.size(), storageToCheck.getStoredItems().size());
        assertTrue(storageToCheck.getStoredItems().stream().map(Item::getColor).toList().containsAll(expectedStorageContents));

        // (FINAL) Check that each player that decided to land got moved backwards
        // A list of the previous positions is needed to confront them with the new positions
        assertEquals(initialPositions.get(2) - this.movementStep, this.visitPlanets.getBoard().getPlayers().get(2).getCursor());
        assertEquals(initialPositions.get(1), this.visitPlanets.getBoard().getPlayers().get(1).getCursor());
        assertEquals(initialPositions.get(0) - this.movementStep - 1, this.visitPlanets.getBoard().getPlayers().get(0).getCursor());
    }

    @Test
    void generateState() {
        CardStateJSON state = this.visitPlanets.generateState();

//        visualizeVisitPlanetsCardStateParameters(state);

        assertEquals("VisitPlanets", state.getCardName());
        assertEquals(2, state.getCardLevel());
        assertTrue(state.getIsCardUsable());

        for (Map.Entry<Integer, Map<ItemColor, Integer>> entry : state.getAvailablePlanets().entrySet()) {
            assertEquals(4, entry.getValue().keySet().size());
            for (Map.Entry<ItemColor, Integer> innerEntry : entry.getValue().entrySet()){
                switch (innerEntry.getKey()) {
                    case BLUE, GREEN, YELLOW, RED -> {}
                    case null, default -> throw new IllegalStateException("ERROR: Only 4 item colors allowed");
                }
            }
        }
    }
}