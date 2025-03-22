package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.AbandonedShipJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipTest {
    Board board;

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

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        // Add the players to the board and create the ships for each player
        // All the players have the same amount of Astronauts (that is 6) --> So they can play the card
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);

            int[] connectors = new int[4];

            for (int i = 0; i < 4; i++) {
                connectors[i] = THREE_PIPES.ordinal();
            }

            if (player.getNickname().equals("Player 1")) {
                Cannon singleCannon1 = new Cannon(connectors, 1);
                Cannon singleCannon2 = new Cannon(connectors, 1);

                Shield shield1 = new Shield(connectors);

                Storage specialStorage1 = new Storage(connectors, 1, true);
                Storage specialStorage2 = new Storage(connectors, 2, true);

                Storage storage1 = new Storage(connectors, 3, false);
                Storage storage2 = new Storage(connectors, 2, false);

                Battery battery1 = new Battery(connectors, 2);
                Battery battery2 = new Battery(connectors, 2);
                Battery battery3 = new Battery(connectors, 2);

                Structural structural1 = new Structural(connectors);
                Structural structural2 = new Structural(connectors);
                Structural structural3 = new Structural(connectors);

                Cabin cabin1 = new Cabin(connectors, false);
                cabin1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));
                Cabin cabin2 = new Cabin(connectors, false);
                cabin2.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

                Vital vital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
                Vital vital2 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

                Engine normalEngine1 = new Engine(connectors, 1);
                Engine normalEngine2 = new Engine(connectors, 1);
                Engine normalEngine3 = new Engine(connectors, 1);

                Engine doubleEngine = new Engine(connectors, 2);

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
                Cannon singleCannon1 = new Cannon(connectors, 1);
                Cannon singleCannon2 = new Cannon(connectors, 1);

                Shield shield1 = new Shield(connectors);

                Storage specialStorage1 = new Storage(connectors, 1, true);
                Storage specialStorage2 = new Storage(connectors, 2, true);

                Storage storage1 = new Storage(connectors, 3, false);
                Storage storage2 = new Storage(connectors, 2, false);

                Battery battery1 = new Battery(connectors, 2);
                Battery battery2 = new Battery(connectors, 2);
                Battery battery3 = new Battery(connectors, 2);

                Structural structural1 = new Structural(connectors);
                Structural structural2 = new Structural(connectors);
                Structural structural3 = new Structural(connectors);

                Cabin cabin1 = new Cabin(connectors, false);
                cabin1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                cabin1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                Cabin cabin2 = new Cabin(connectors, false);
                cabin2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                cabin2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

                Vital vital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
                Vital vital2 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

                Engine normalEngine1 = new Engine(connectors, 1);
                Engine normalEngine2 = new Engine(connectors, 1);
                Engine normalEngine3 = new Engine(connectors, 1);

                Engine doubleEngine = new Engine(connectors, 2);

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
    }

    /**
     * Casi di test da fare
     * 1. Astronauti in numero insufficiente --> Non devono essere messi nella lista di player X
     * 2. Astronauti in numero sufficiente, ma non si usa la card --> Passa al prossimo player e non devo cambiare niente al player corrente X
     * 3. Astronauti in numero sufficiente, ma si usa la card --> Vengono applicati gli effetti e la carta è stata utilizzata X
     * 4. Se un giocatore finisce gli astronauti usando la carta, questo deve essere eliminato
     * */

    @Test
    void test_abandoned_ship_with_only_three_players_that_can_use_the_card_and_the_third_player_will_use_it() {
        // Create the card that will be used in the simulation
        AbandonedShip abandonedShipCard = new AbandonedShip("abandonedShip", 2, 5, 1, 3, board);

        // Init the card players
        abandonedShipCard.initCardPlayers();

        List<Player> playerList = new ArrayList<>(board.getPlayers());
        List<Integer> initialCursors = new ArrayList<>();

        // Only the 2, 3, 4 players will be able to use the card for the given required crew members
        while (!abandonedShipCard.hasFinished()) {
            Player currPlayer;

            AbandonedShipJSON abandonedShipJSON = new AbandonedShipJSON();

            AbandonedShip finalAbandonedShipCard = abandonedShipCard;
            Optional<Player> optionalPlayer = playerList.stream()
                    .filter(p -> p.getNickname().equals(finalAbandonedShipCard.generateState().getPlayerNickname()))
                    .findFirst();

            // If the player is present we can execute the action
            if (optionalPlayer.isPresent()) {
                currPlayer = optionalPlayer.get();
                initialCursors.add(currPlayer.getCursor());

                // HP: The first two players don't want to use the card
                // The third and the 4th player wants to use the card --> Only the 3rd one will be able to use the card, so the 4th will be skipped
                if (currPlayer.getNickname().equals("Player 1") || currPlayer.getNickname().equals("Player 2")) {
                    abandonedShipJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedShipJSON.setWantToVisitShip(false);
                } else {
                    abandonedShipJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedShipJSON.setWantToVisitShip(true);

                    List<ComponentHelper<LifeformType>> lifeFormToBeRemoved = new ArrayList<>();

                    ComponentHelper<LifeformType> cabinComponent1 = new ComponentHelper<LifeformType>(7, 4 );
                    cabinComponent1.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent1);

                    ComponentHelper<LifeformType> cabinComponent2 = new ComponentHelper<LifeformType>(7, 4 );
                    cabinComponent2.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent2);

                    ComponentHelper<LifeformType> cabinComponent3 = new ComponentHelper<LifeformType>(7, 6 );
                    cabinComponent3.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent3);

                    ComponentHelper<LifeformType> cabinComponent4 = new ComponentHelper<LifeformType>(7, 6 );
                    cabinComponent4.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent4);

                    ComponentHelper<LifeformType> coreCabin = new ComponentHelper<LifeformType>(6, 6 );
                    coreCabin.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(coreCabin);

                    abandonedShipJSON.setLifeformsToBeRemoved(lifeFormToBeRemoved);
                }

                // Use the card
                abandonedShipCard = (AbandonedShip) abandonedShipCard.useCard(abandonedShipJSON);
            }
        }

        // Check the players positions:
        // 1st --> Skipped because he doesn't have enough crew members
        // 2nd --> Didn't use the card
        // 3rd --> Used the card --> Moved backwards of 1 step. In the reality it gets moved of 2 steps since the previous cell is not empty
        // 4th --> Didn't use the card since it has already been used

        // 1st
        assertEquals(6, playerList.get(0).getCursor());
        // 2nd
        assertEquals(initialCursors.get(0), playerList.get(1).getCursor());
        // 3rd
        assertEquals(initialCursors.get(1) - 2, playerList.get(2).getCursor());
        // 4th
        assertEquals(0, playerList.get(3).getCursor());


        // Check that the player that used the card has received the credits and has lost the crew members specified
        assertEquals(3, playerList.get(2).getCredits());

        Cabin cabin1 = (Cabin) playerList.get(2).getShip().getComponent(7,4);
        Cabin cabin2 = (Cabin) playerList.get(2).getShip().getComponent(7,6);
        Cabin coreCabin = (Cabin) playerList.get(2).getShip().getComponent(6,6);
        assertEquals(0, cabin1.getInhabitants().size());
        assertEquals(0, cabin2.getInhabitants().size());
        assertEquals(1, coreCabin.getInhabitants().size());
    }

    @Test
    void test_player_elimination_when_astronauts_are_finished() {
        // Create the card that will be used in the simulation
        AbandonedShip abandonedShipCard = new AbandonedShip("abandonedShip", 2, 6, 1, 3, board);

        // Init the card players
        abandonedShipCard.initCardPlayers();

        List<Player> playerList = new ArrayList<>(board.getPlayers());
        List<Integer> initialCursors = new ArrayList<>();

        // Only the 2, 3, 4 players will be able to use the card for the given required crew members
        while (!abandonedShipCard.hasFinished()) {
            Player currPlayer;

            AbandonedShipJSON abandonedShipJSON = new AbandonedShipJSON();

            AbandonedShip finalAbandonedShipCard = abandonedShipCard;
            Optional<Player> optionalPlayer = playerList.stream()
                    .filter(p -> p.getNickname().equals(finalAbandonedShipCard.generateState().getPlayerNickname()))
                    .findFirst();

            // If the player is present we can execute the action
            if (optionalPlayer.isPresent()) {
                currPlayer = optionalPlayer.get();
                initialCursors.add(currPlayer.getCursor());

                // HP: The first two players don't want to use the card
                // The third and the 4th player wants to use the card --> Only the 3rd one will be able to use the card, so the 4th will be skipped
                if (currPlayer.getNickname().equals("Player 1") || currPlayer.getNickname().equals("Player 2")) {
                    abandonedShipJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedShipJSON.setWantToVisitShip(false);
                } else {
                    abandonedShipJSON.setPlayerNickname(currPlayer.getNickname());
                    abandonedShipJSON.setWantToVisitShip(true);

                    List<ComponentHelper<LifeformType>> lifeFormToBeRemoved = new ArrayList<>();

                    ComponentHelper<LifeformType> cabinComponent1 = new ComponentHelper<LifeformType>(7, 4 );
                    cabinComponent1.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent1);

                    ComponentHelper<LifeformType> cabinComponent2 = new ComponentHelper<LifeformType>(7, 4 );
                    cabinComponent2.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent2);

                    ComponentHelper<LifeformType> cabinComponent3 = new ComponentHelper<LifeformType>(7, 6 );
                    cabinComponent3.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent3);

                    ComponentHelper<LifeformType> cabinComponent4 = new ComponentHelper<LifeformType>(7, 6 );
                    cabinComponent4.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(cabinComponent4);

                    ComponentHelper<LifeformType> coreCabin1 = new ComponentHelper<LifeformType>(6, 6 );
                    coreCabin1.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(coreCabin1);

                    ComponentHelper<LifeformType> coreCabin2 = new ComponentHelper<LifeformType>(6, 6 );
                    coreCabin2.addItem(LifeformType.ASTRONAUT);
                    lifeFormToBeRemoved.add(coreCabin2);

                    abandonedShipJSON.setLifeformsToBeRemoved(lifeFormToBeRemoved);
                }

                // Use the card
                abandonedShipCard = (AbandonedShip) abandonedShipCard.useCard(abandonedShipJSON);
            }
        }

        // Check the players positions:
        // 1st --> Skipped because he doesn't have enough crew members
        // 2nd --> Didn't use the card
        // 3rd --> Used the card --> Should be eliminated since it has no astronauts left --> DO NOT CHECK THE POSITION SINCE IT'S NOT USEFUL
        // 4th --> Didn't use the card since it has already been used

        // 1st
        assertEquals(6, playerList.get(0).getCursor());
        // 2nd
        assertEquals(initialCursors.get(0), playerList.get(1).getCursor());
        // 4th
        assertEquals(0, playerList.get(3).getCursor());


        // Check that the player that used the card has received the credits and has lost the crew members specified
        assertEquals(3, playerList.get(2).getCredits());

        Cabin cabin1 = (Cabin) playerList.get(2).getShip().getComponent(7,4);
        Cabin cabin2 = (Cabin) playerList.get(2).getShip().getComponent(7,6);
        Cabin coreCabin = (Cabin) playerList.get(2).getShip().getComponent(6,6);
        assertEquals(0, cabin1.getInhabitants().size());
        assertEquals(0, cabin2.getInhabitants().size());
        assertEquals(0, coreCabin.getInhabitants().size());

        assertTrue(playerList.get(2).isEliminated());
        assertTrue(board.getEliminatedPlayers().contains(playerList.get(2)) && board.getEliminatedPlayers().size() == 1 );
    }



}