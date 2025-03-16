package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenSpaceJSONTest {

    @Test
    void test_default_constructor_and_playerNickname() {
        // Verify that the default constructor initializes a non-null JSONObject
        OpenSpaceJSON openSpace = new OpenSpaceJSON();
        assertNotNull(openSpace.getData(), "Internal JSONObject should not be null");

        // Set a nickname and verify that it is correctly returned
        String nickname = "Player1";
        openSpace.setPlayerNickname(nickname);
        assertEquals(nickname, openSpace.getPlayerNickname(), "The nickname should be correctly set and retrieved");
    }

    @Test
    void test_get_playerNickname_missing_throws_exception() {
        // Verify that if no playerNickname is set, an exception is thrown
        OpenSpaceJSON openSpace = new OpenSpaceJSON();
        IllegalStateException exception = assertThrows(IllegalStateException.class, openSpace::getPlayerNickname);
        assertEquals("Key 'playerNickname' is missing in JSON data", exception.getMessage());
    }

    @Test
    void test_set_playerNickname_throws_exception_when_null() {
        // Verify that setting a null nickname throws an exception
        OpenSpaceJSON openSpace = new OpenSpaceJSON();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            openSpace.setPlayerNickname(null);
        });
        assertEquals("Player nickname cannot be null", exception.getMessage());
    }

    @Test
    void test_set_data() {
        // Create a new JSONObject and set it in the OpenSpaceJSON object
        OpenSpaceJSON openSpace = new OpenSpaceJSON();
        JSONObject newData = new JSONObject();
        newData.put("playerNickname", "Player2");
        newData.put("score", 100);

        openSpace.setData(newData);
        JSONObject data = openSpace.getData();

        // Verify that the JSONObject contains the expected values
        assertEquals("Player2", data.get("playerNickname"));
        assertEquals(100, data.get("score"));
    }

    @Test
    void test_stringify_and_parse() throws ParseException {
        // Set a nickname and convert the JSONObject to a string
        OpenSpaceJSON openSpace = new OpenSpaceJSON();
        openSpace.setPlayerNickname("TestPlayer");
        String jsonString = ActionJSON.Stringify(openSpace.getData());

        // Parse the string to obtain the JSONObject again and verify the value
        JSONObject parsedData = ActionJSON.Parse(jsonString);
        assertEquals("TestPlayer", parsedData.get("playerNickname"));
    }

    @Test
    void test_constructor_with_JSONString() throws ParseException {
        // Create a JSON string containing keys, including playerNickname
        String jsonString = "{\"playerNickname\":\"TestPlayer2\", \"level\":5}";
        OpenSpaceJSON openSpace = new OpenSpaceJSON(jsonString);

        // Verify that the constructor accepting a JSON string works correctly
        assertEquals("TestPlayer2", openSpace.getPlayerNickname());
        JSONObject data = openSpace.getData();
        // Note: the parser might interpret numbers as Long
        assertEquals(5L, data.get("level"));
    }

    @Test
    void test_stringify_throws_exception_for_empty_JSONObject() {
        // Verify that the static Stringify method throws an exception if the JSONObject is empty
        JSONObject emptyData = new JSONObject();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ActionJSON.Stringify(emptyData);
        });
        assertEquals("The JSON string is either null or empty", exception.getMessage());
    }
}