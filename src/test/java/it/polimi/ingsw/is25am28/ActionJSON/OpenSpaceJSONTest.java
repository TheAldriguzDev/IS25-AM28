package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.OpenSpaceJSON;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenSpaceJSONTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void test_default_constructor_and_set_playerNickname() {
        OpenSpaceJSON openSpace = new OpenSpaceJSON();

        // Verify that without the player nickname the exception is thrown
//        IllegalStateException exception = assertThrows(IllegalStateException.class, openSpace::getPlayerNickname);
//        assertEquals("Key 'playerNickname' is missing in JSON data", exception.getMessage());

        // Set the playerNickname and verify it is returned
        String nickname = "Player1";
        openSpace.setPlayerNickname(nickname);
        assertEquals(nickname, openSpace.getPlayerNickname(), "The playerNickname has not been set or retrieved correctly");
    }

    @Test
    void test_set_playerNickname_throws_exception_when_null() {
        // Verify that a null playerNickname throws an exception
        OpenSpaceJSON openSpace = new OpenSpaceJSON();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> openSpace.setPlayerNickname(null));
        assertEquals("playerNickname cannot be null or empty", exception.getMessage());
    }

    @Test
    void test_serialization() throws JsonProcessingException {
        // Create a new JSON instance and verify it contains the given data
        OpenSpaceJSON openSpace = new OpenSpaceJSON("TestPlayer", 42);

        // Serialize the object
        String json = objectMapper.writeValueAsString(openSpace);

        // Verify that the string contains the given JSON data
        assertTrue(json.contains("\"playerNickname\":\"TestPlayer\""), "The JSON does not contains the playerNickname");
        assertTrue(json.contains("\"usedEnergy\":42"), "The JSON does not contains the usedEnergy");
    }

    @Test
    void test_deserialization() throws JsonProcessingException {
        // From a given string we try to get the JSON object
        String json = "{\"playerNickname\":\"TestPlayer\",\"usedEnergy\":55}";

        // Deserialize the JSON string
        OpenSpaceJSON openSpace = objectMapper.readValue(json, OpenSpaceJSON.class);

        // Verify that we have the correct values with the class methods
        assertEquals("TestPlayer", openSpace.getPlayerNickname());
        assertEquals(55, openSpace.getUsedEnergy());
    }
}
