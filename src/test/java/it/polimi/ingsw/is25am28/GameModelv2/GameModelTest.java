package it.polimi.ingsw.is25am28.GameModelv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    // TODO: FARE CASI DI TEST CHE PERMETTANO DI TESTARE GLI ERRORI DEI DATI

    @Test
    public void test_game_model_valid_complete_flow() throws JsonProcessingException {
        // 1. Check that the model is in the initial state
        assertInstanceOf(CreateGameState.class, model.getCurrentState());

        // Check if the output of the model match the first state information
        String json = mapper.writeValueAsString(model.generateState());
        String expectedState = "{\"availableColors\":[\"GREEN\",\"RED\",\"BLUE\",\"YELLOW\"],\"usedNicknames\":[],\"stateName\":\"CreateGameState\"}";
        assertEquals(expectedState, json);

        // 2.0. Test to execute invalid config command
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

        // 2.1. Execute the leader command to configure the game
        StateJSON state = model.gameConfig("Player 1", PlayerColor.RED, 2, 4);

        assertEquals(2, model.getGameLevel());
        assertEquals(4, model.getNumPlayers());
        assertEquals(1, model.getPlayers().size());
        assertEquals(3, model.getAvailableColors().size());

        // 3. Check if the model went to the WaitPlayersState
        assertInstanceOf(WaitPlayersState.class, model.getCurrentState());

        // Check the output of the model in the second state
        json = mapper.writeValueAsString(state);
        expectedState = "{\"availableColors\":[\"GREEN\",\"BLUE\",\"YELLOW\"],\"usedNicknames\":[\"Player 1\"],\"lobbyTotalSpot\":4,\"availableSpots\":3,\"stateName\":\"WaitPlayersState\"}";
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
        state = model.addNewPlayer("Player 2", PlayerColor.YELLOW);

        json = mapper.writeValueAsString(state);
        expectedState = "{\"availableColors\":[\"GREEN\",\"BLUE\"],\"usedNicknames\":[\"Player 2\",\"Player 1\"],\"lobbyTotalSpot\":4,\"availableSpots\":2,\"stateName\":\"WaitPlayersState\"}";
        assertEquals(expectedState, json);

        state = model.addNewPlayer("Player 3", PlayerColor.BLUE);
        json = mapper.writeValueAsString(state);
        expectedState = "{\"availableColors\":[\"GREEN\"],\"usedNicknames\":[\"Player 3\",\"Player 2\",\"Player 1\"],\"lobbyTotalSpot\":4,\"availableSpots\":1,\"stateName\":\"WaitPlayersState\"}";
        assertEquals(expectedState, json);

        state = model.addNewPlayer("Player 4", PlayerColor.GREEN);

        // 3. Check if the model went to the WaitPlayersState
        assertInstanceOf(ShipContructionState.class, model.getCurrentState());
    }
}