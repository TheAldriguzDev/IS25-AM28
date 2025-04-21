package it.polimi.ingsw.is25am28.Model.GameModelv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.AbandonedShipJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionType;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {
    private GameModel model;
    ObjectMapper mapper;

    @BeforeEach
    public void init() {
        // Init the gameModel
        model = new GameModel();
        mapper = new ObjectMapper();
    }

    @Test
    public void test_game_model_valid_complete_flow() throws JsonProcessingException, SelectedConcurrencyException, ShipPopulationFailException, FixNotRequiredError {
        // ========================================
        // NEW GAME HAS BEEN CREATED
        // ========================================
        assertInstanceOf(CreateGameState.class, model.getCurrentState());

        // Check if the output of the game is about the game configuration
        String json = mapper.writeValueAsString(model.generateState());
        String expectedState = "{\"type\":\"CreateGameStateDTO\",\"availableColors\":[\"GREEN\",\"RED\",\"BLUE\",\"YELLOW\"],\"usedNicknames\":[],\"stateName\":\"CreateGameState\"}";
        assertEquals(expectedState, json);

        // ========================================
        // GAME CONFIGURATION
        // ========================================
        assertThrows(
                IllegalArgumentException.class,
                () -> model.gameConfig("", PlayerColor.RED, 2, 4),
                "The player nickname should not be empty"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> model.gameConfig(null, PlayerColor.RED, 2, 4),
                "The player nickname should not be null"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> model.gameConfig("Player 1", PlayerColor.RED, -1, 4),
                "The model level should not be negative"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> model.gameConfig("Player 1", PlayerColor.RED, 10, 4),
                "The model level should not be grader than 3"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> model.gameConfig("Player 1", PlayerColor.RED, 2, 1),
                "The numPlayer should not be lower than 2"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> model.gameConfig("Player 1", PlayerColor.RED, 2, 5),
                "The numPlayer should not be greater than 4"
        );

        // 2.1. The leader execute the command to configure the game
        int gameLevel = 0;
        StateDTO state = model.gameConfig("Player 1", PlayerColor.RED, gameLevel, 4);

        assertEquals(gameLevel, model.getGameLevel());
        assertEquals(4, model.getNumPlayers());
        assertEquals(1, model.getPlayers().size());
        assertEquals(3, model.getAvailableColors().size());

        // ========================================
        // WAIT PLAYERS STATE --> THE GAME ACCEPT CONNECTION FROM NEW PLAYERS
        // ========================================
        assertInstanceOf(WaitPlayersState.class, model.getCurrentState());

        // Check if the output of the match is about the waiting for players state
        json = mapper.writeValueAsString(state);

        expectedState = "{\"type\":\"WaitPlayersStateDTO\",\"availableColors\":[\"GREEN\",\"BLUE\",\"YELLOW\"],\"usedNicknames\":{\"Player 1\":\"RED\"},\"lobbyTotalSpot\":4,\"availableSpots\":3,\"stateName\":\"WaitPlayersState\"}";
        assertEquals(expectedState, json);

        // 3.1 Test invalid newPlayerInput
        assertThrows(
                IllegalArgumentException.class,
                () -> model.addNewPlayer("Player 1", PlayerColor.YELLOW),
                "The nickname should be different from another player"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> model.addNewPlayer("Player 2", PlayerColor.RED),
                "The color should be different from another player"
        );

        // Add three player to the game --> the state should change to the ship construction session
        List<StateDTO> states = model.addNewPlayer("Player 2", PlayerColor.YELLOW);
        assertEquals(1, states.size());

        json = mapper.writeValueAsString(states.getFirst());
        expectedState = "{\"type\":\"WaitPlayersStateDTO\",\"availableColors\":[\"GREEN\",\"BLUE\"],\"usedNicknames\":{\"Player 2\":\"YELLOW\",\"Player 1\":\"RED\"},\"lobbyTotalSpot\":4,\"availableSpots\":2,\"stateName\":\"WaitPlayersState\"}";
        assertEquals(expectedState, json);

        states = model.addNewPlayer("Player 3", PlayerColor.BLUE);
        assertEquals(1, states.size());

        json = mapper.writeValueAsString(states.getFirst());
        expectedState = "{\"type\":\"WaitPlayersStateDTO\",\"availableColors\":[\"GREEN\"],\"usedNicknames\":{\"Player 3\":\"BLUE\",\"Player 2\":\"YELLOW\",\"Player 1\":\"RED\"},\"lobbyTotalSpot\":4,\"availableSpots\":1,\"stateName\":\"WaitPlayersState\"}";
        assertEquals(expectedState, json);

        states = model.addNewPlayer("Player 4", PlayerColor.GREEN);
        assertEquals(2, states.size());

        // ========================================
        // SHIP CONSTRUCTION STATE --> THE PLAYERS WILL BE ABLE TO CREATE THEIR SHIP
        // ========================================
        assertInstanceOf(ShipContructionState.class, model.getCurrentState());
        json = mapper.writeValueAsString(model.getCurrentState().generateState());
        System.out.println(json);

        // Select the tile
        ConstructionComponentDTO tileState = model.selectTile("Player 1", 1, 9);

        assertEquals(tileState.getEventType(), ShipConstructionType.TILE_EVENT.toString());
        assertEquals(tileState.getPlayerNickname(), "Player 1");
        assertTrue(tileState.isSelected());

        // Try to select the tile while is already selected by another player --> should throw an error
        assertThrows(
                SelectedConcurrencyException.class,
                () -> model.selectTile("Player 2", 1, 9),
                "The selected tile should not be available for a select"
        );

        // Deselect the tile
        tileState = model.deselectTile("Player 1", 1, 9);

        assertEquals(tileState.getEventType(), ShipConstructionType.TILE_EVENT.toString());
        assertEquals(tileState.getPlayerNickname(), "Player 1");
        assertFalse(tileState.isSelected());

        // Select again the tile
        tileState = model.selectTile("Player 2", 1, 9);

        assertEquals(tileState.getEventType(), ShipConstructionType.TILE_EVENT.toString());
        assertEquals(tileState.getPlayerNickname(), "Player 2");
        assertTrue(tileState.isSelected());

        // TODO: Test flip timer

        // Create the ship for each player:
        // Player 1: Valid ship
        // Player 2: Valid ship
        // Player 3: Invalid ship
        // Player 4: Invalid ship
        List<ComponentHelper<ConstructionComponentDTO>> playerShipComponents = new ArrayList<>();

        // Cannon level one over the core cabin
        playerShipComponents.add(
                new ComponentHelper<ConstructionComponentDTO>(1, 9) // Represent the index of the selected component from the view
                        .addItem(new ConstructionComponentDTO().setI(5).setJ(6).setRotation(0))); // Represent the i and j of the position where the component has been placed on the ship

        // Brown vital on the right of the cannon
        playerShipComponents.add(
                new ComponentHelper<ConstructionComponentDTO>(9, 9)
                        .addItem(new ConstructionComponentDTO().setI(5).setJ(7).setRotation(2)));

        // Cabin on the right of the core
        playerShipComponents.add(
                new ComponentHelper<ConstructionComponentDTO>(4, 12)
                        .addItem(new ConstructionComponentDTO().setI(6).setJ(7).setRotation(0)));

        List<StateDTO> playerEndedShipStates = model.playerEndedSendShip("Player 1", playerShipComponents, 2);
        assertEquals(1, playerEndedShipStates.size());

        // Try to send another time the ship --> should throw an error
        assertThrows(
                IllegalArgumentException.class,
                () -> model.playerEndedSendShip("Player 1", playerShipComponents, 0),
                "The player should have already sent the ship"
        );

        // Engine under the core cabin
        playerShipComponents.add(
                new ComponentHelper<ConstructionComponentDTO>(5, 11)
                        .addItem(new ConstructionComponentDTO().setI(7).setJ(6).setRotation(0)));

        playerEndedShipStates = model.playerEndedSendShip("Player 2", playerShipComponents, 0);
        assertEquals(1, playerEndedShipStates.size());

        // Invalid ship for the player 3 and 4
        playerShipComponents.add(
                new ComponentHelper<ConstructionComponentDTO>(2, 10)
                        .addItem(new ConstructionComponentDTO().setI(6).setJ(4).setRotation(0)));

        playerEndedShipStates = model.playerEndedSendShip("Player 3", playerShipComponents, 1);
        assertEquals(1, playerEndedShipStates.size());

        playerEndedShipStates = model.playerEndedSendShip("Player 4", playerShipComponents, 0);

        // We should have two states since the state should change in FIX_SHIP
        assertEquals(2, playerEndedShipStates.size());

        // ========================================
        // FIX SHIP STATE --> THE PLAYERS WITH A WRONG SHIP NEEDS TO CORRECT THEM
        // ========================================
        String expectedWrongInitialShipState = "{\"type\":\"FixShipDTO\",\"playerWithInvalidShip\":[\"Player 4\",\"Player 3\"],\"stateName\":\"FixShipState\"}";
        json = mapper.writeValueAsString(playerEndedShipStates.getLast());
        assertEquals(expectedWrongInitialShipState, json);

        // All the player have sent their ship --> We should be in the FixShipState since p3 and p4 have an invalid ship
        assertInstanceOf(FixShipState.class, model.getCurrentState());

        // Player 3 - Player 4:
        // p3 --> Remove and component and does not fix the ship
        // p4 --> Remove the wrong component --> should fix his ship
        // List of components that the players wants to remove
        List<ComponentHelper<Integer>> p3FixedComponents = new ArrayList<>(); // Player 3 component list
        List<ComponentHelper<Integer>> p4FixedComponents = new ArrayList<>(); // Player 4 component list

        // Removed the cannon over the core cabin --> is not the invalid component
        p3FixedComponents.add( new ComponentHelper<Integer>(5, 6));
        // Removed the wrong component
        p4FixedComponents.add( new ComponentHelper<Integer>(6, 4));

        // Player 1 tries to fix his ship, but since it's valid it should throw an error
        assertThrows(
                FixNotRequiredError.class,
                () -> model.fixShip("Player 1", new ArrayList<>()),
                "Player 1 should already have a valid ship"
        );

        // The player 3 tries to fix his ship, but he fails :(
        List<StateDTO> fixShipStates = model.fixShip("Player 3", p3FixedComponents);
        assertEquals(1, fixShipStates.size()); // There is no state transaction

        // Check the new state of players that needs to fix the ship
        FixShipDTO fixShipDTO = (FixShipDTO) fixShipStates.getFirst();
        assertEquals(2, fixShipDTO.getPlayerWithInvalidShip().size());

        // Player 4 fixes his ship
        fixShipStates = model.fixShip("Player 4", p4FixedComponents);
        assertEquals(1, fixShipStates.size()); // There is no state transaction since the p3 still have an invalid ship
        fixShipDTO = (FixShipDTO) fixShipStates.getFirst();
        assertEquals(1, fixShipDTO.getPlayerWithInvalidShip().size()); // Since the p3 has fixed his ship, only the p3 needs to fix it

        // Player 3 fixes his ship
        fixShipStates = model.fixShip("Player 3", p4FixedComponents);
        assertEquals(2, fixShipStates.size()); // it also contains the next state (populateShip)
        fixShipDTO = (FixShipDTO) fixShipStates.getFirst();
        assertEquals(0, fixShipDTO.getPlayerWithInvalidShip().size()); // No player needs to fix their ship


        // ========================================
        // POPULATE SHIP STATE --> THE PLAYERS NEED TO POPULATE THEIR SHIP
        // ========================================
        assertInstanceOf(PopulateShipState.class, model.getCurrentState());

        String expectedPopulateJJSON = "{\"type\":\"PopulateShipDTO\",\"playersReady\":[],\"stateName\":\"PopulateShipState\"}";
        json = mapper.writeValueAsString(this.model.generateState());
        assertEquals(expectedPopulateJJSON, json);

        // p1 - p2 - p4 add astronauts to their ship, instead p3 add a brown alien

        // TODO: Add some errors to test the correct behavior of the model

        List<ComponentHelper<LifeformType>> addAstronauts = new ArrayList<>();
        addAstronauts.add(new ComponentHelper<LifeformType>(6, 7).addItem(LifeformType.ASTRONAUT));

        List<ComponentHelper<LifeformType>> addBrownAlien = new ArrayList<>();
        addBrownAlien.add(new ComponentHelper<LifeformType>(6, 7).addItem(LifeformType.BROWN_ALIEN));

        List<StateDTO> populateStates = model.populateShip("Player 1", addAstronauts);
        PopulateShipDTO populateShipDTO = (PopulateShipDTO) populateStates.getFirst();
        assertEquals(1, populateShipDTO.getPlayersReady().size());
        assertEquals(1, populateStates.size());

        populateStates = model.populateShip("Player 2", addAstronauts);
        populateShipDTO = (PopulateShipDTO) populateStates.getFirst();
        assertEquals(2, populateShipDTO.getPlayersReady().size());
        assertEquals(1, populateStates.size());

        populateStates = model.populateShip("Player 3", addBrownAlien);
        populateShipDTO = (PopulateShipDTO) populateStates.getFirst();
        assertEquals(3, populateShipDTO.getPlayersReady().size());
        assertEquals(1, populateStates.size());

        populateStates = model.populateShip("Player 4", addAstronauts);
        populateShipDTO = (PopulateShipDTO) populateStates.getFirst();
        assertEquals(4, populateShipDTO.getPlayersReady().size());
        assertEquals(2, populateStates.size());

        // ========================================
        // CARD ROUND STATE --> THE PLAYERS WITH A WRONG SHIP NEEDS TO CORRECT THEM
        // ========================================
        assertInstanceOf(CardRoundState.class, model.getCurrentState());

        StateDTO firstRoundState = new StateDTO();

        switch (model.getCurrentState()) {
            case CardRoundState cardState -> {
                cardState.setFakeDeck(getFakeDeck());
                firstRoundState = cardState.generateFirstState();
            }
            default -> {
                System.out.println("ERROR: THE MODEL IS NOT IN THE CardRoundState");
            }
        }

        assertInstanceOf(CardRoundDTO.class, firstRoundState);
        CardRoundDTO cardRoundDTO = (CardRoundDTO) firstRoundState;
        assertEquals("Player 1", cardRoundDTO.getCardInfo().getPlayerNickname(), "Player 1 should be the leader");

        List<StateDTO> cardRoundStates = new ArrayList<>();

        // In this test the first 4 cards are playable, the others one are not since the players does not have the requirements

        // Play the first round --> all the player could play this card, HP: the first player will use it
        for (int i = 0; i < 4; i++) {
            AbandonedShipJSON actionData = new AbandonedShipJSON();
            actionData.setPlayerNickname("Player " + (i + 1));

            // The first player will use the card, instead the others player won't use the card
            if (i == 0) {
                actionData.setWantToVisitShip(true);

                // One astronaut will be removed from the ship
                List<ComponentHelper<LifeformType>> lifeFormToBeRemoved = new ArrayList<>();
                ComponentHelper<LifeformType> cabinComponent1 = new ComponentHelper<LifeformType>(6, 7 );
                cabinComponent1.addItem(LifeformType.ASTRONAUT);
                lifeFormToBeRemoved.add(cabinComponent1);

                actionData.setLifeformsToBeRemoved(lifeFormToBeRemoved);
            } else {
                actionData.setWantToVisitShip(false);
                break; // Since the card will be marked as used we don't need to continue to send actions for the other players
            }

            cardRoundStates = model.playCard(actionData);
        }

        // The first State is about the response of the command --> it has the information about the usage of the card
        // the secondo State is about the new card
        assertEquals(2, cardRoundStates.size());
        assertInstanceOf(CardRoundDTO.class, cardRoundStates.getLast());

        CardRoundDTO cardRoundDTO1 = (CardRoundDTO) cardRoundStates.getFirst();
        CardRoundDTO cardRoundDTO2 = (CardRoundDTO) cardRoundStates.getLast();
        // Player 2 should be the new leader
        assertEquals("Player 2", cardRoundDTO2.getCardInfo().getPlayerNickname(), "The leader should be the Player 2");
        assertEquals(1, cardRoundDTO2.getRound(), "The round should be 1");
        assertEquals(0, cardRoundDTO1.getCardInfo().getBoard().getEliminatedPlayersNickname().size(), "No players should be eliminated");

        // Execute the rest of the cards
        for (int i = 0; i < 7; i ++) {
            String[] nicknames = { "Player 2", "Player 3", "Player 4", "Player 1" };
            for (int j = 0; j < 4; j ++) {
                // The player don't want to use the card (only for testing purposes)
                AbandonedShipJSON actionData = new AbandonedShipJSON();
                actionData.setPlayerNickname(nicknames[j]);
                actionData.setWantToVisitShip(false);

                cardRoundStates = model.playCard(actionData);
            }
        }

        // After all this computation we expect to be in the last state of the game
        assertEquals(2, cardRoundStates.size());
        assertInstanceOf(EndGameState.class, model.getCurrentState());

        json = mapper.writeValueAsString(this.model.generateState());
        System.out.println(json);


        // TODO: try to execute some command with errors
    }

    /**
     * @return a fake deck to test the flow of the game. The length of the deck is 8, since we are testing the test flight
     * */
    private List<EventCard> getFakeDeck() {
        List<EventCard> deck = new ArrayList<>();
        Board board = model.getBoard();
        deck.add(new AbandonedShip("Abandoned Ship", 2, 1, 2, 4, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 1, 2, 3, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 1, 2, 2, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 1, 2, 1, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 6, 2, 2, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 6, 2, 2, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 6, 2, 2, board));
        deck.add(new AbandonedShip("Abandoned Ship", 2, 6, 2, 2, board));

        // Initialize the first card player (same logic used by the CardRoundState)
        deck.getFirst().initCardPlayers();

        return deck;
    }
}