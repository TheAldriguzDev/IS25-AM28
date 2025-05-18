package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleEnginesToActivate = new ArrayList<>();

        doubleEnginesToActivate.add(
            new Pair<>(
                new Pair<>(7, 5),
                new Pair<>(7, 8)
            )
        );

        // Create a new JSON instance and verify it contains the given data
        OpenSpaceJSON openSpace = new OpenSpaceJSON("TestPlayer", doubleEnginesToActivate);

        // Serialize the object
        String json = objectMapper.writeValueAsString(openSpace);

//        System.out.println(json);

        // Verify that the string contains the given JSON data
        assertTrue(json.contains("\"playerNickname\":\"TestPlayer\""), "The JSON does not contains the playerNickname");
    }

    @Test
    void test_deserialization() throws JsonProcessingException {
        // From a given string we try to get the JSON object
        String json = "{\"playerNickname\":\"TestPlayer\",\"doubleEnginesToActivate\":[{\"key\":{\"key\":7,\"value\":5},\"value\":{\"key\":7,\"value\":8}}]}";

        // Deserialize the JSON string
        OpenSpaceJSON openSpace = objectMapper.readValue(json, OpenSpaceJSON.class);

        // Verify that we have the correct values with the class methods
        assertEquals("TestPlayer", openSpace.getPlayerNickname());
        assertEquals(1, openSpace.getDoubleEnginesToActivate().size());
        assertEquals(7, openSpace.getDoubleEnginesToActivate().getFirst().getKey().getKey());
        assertEquals(5, openSpace.getDoubleEnginesToActivate().getFirst().getKey().getValue());
        assertEquals(7, openSpace.getDoubleEnginesToActivate().getFirst().getValue().getKey());
        assertEquals(8, openSpace.getDoubleEnginesToActivate().getFirst().getValue().getValue());
    }
}
