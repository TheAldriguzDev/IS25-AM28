package it.polimi.ingsw.is25am28.ActionJSON;// Updated AbandonedStationJSONTest.java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import it.polimi.ingsw.is25am28.ActionJSON.AbandonedStationJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Items.ItemColor;
public class AbandonedStationJSONTest {

    @Test
    public void testSerialization() throws Exception {
        // Create the Object to serialize
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module()); // Needed to support the Optionals

        // Steps to create the JSON

        // Create the list of items to drop
        List<ComponentHelper<ItemColor>> removedItems = new ArrayList<>();
        ComponentHelper<ItemColor> removedItem = new ComponentHelper<ItemColor>(1, 1).addItem(ItemColor.RED);
        removedItems.add(removedItem);

        // Create the list of the component to take
        List<ComponentHelper<ItemColor>> takenItems = new ArrayList<>();
        ComponentHelper<ItemColor> takenItem = new ComponentHelper<ItemColor>(2, 2).addItem(ItemColor.BLUE);
        takenItems.add(takenItem);

        // Create the abandonedStation JSON Object
        AbandonedStationJSON station = new AbandonedStationJSON("Player1", true, removedItems, takenItems);

        // Serialize the JSON Object to a string value
        String json = mapper.writeValueAsString(station);

        // Check that the JSON contains the correct values
        assertTrue(json.contains("\"playerNickname\":\"Player1\""));
        assertTrue(json.contains("\"wantToVisitStation\":true"));
        assertTrue(json.contains("RED"));
        assertTrue(json.contains("BLUE"));
    }

    @Test
    public void testDeserialization() throws Exception {
        // JSON input String to test the deserialization
        String json = "{\"playerNickname\":\"Player1\",\"wantToVisitStation\":false," +
                "\"itemsToBeRemoved\":[{\"i\":1,\"j\":1,\"helper\":\"YELLOW\"}]," +
                "\"itemsToBeTaken\":[{\"i\":2,\"j\":2,\"helper\":\"GREEN\"}]}";

        // ObjectMapper is needed for the deserialization
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module()); // Needed to support the Optional values

        // Deserialize the json string into the Object
        AbandonedStationJSON station = mapper.readValue(json, AbandonedStationJSON.class);

        // Check if the data has been correctly deserialized
        // Player and wantToVisitStation
        assertEquals("Player1", station.getPlayerNickname());
        assertFalse(station.getWantToVisitStation());

        // Items to be removed
        List<ComponentHelper<ItemColor>> removedItems = station.getItemsToBeRemoved();
        assertEquals(1, removedItems.size());

        ComponentHelper<ItemColor> removedItem = removedItems.get(0);
        Optional<ItemColor> removedItemColor = removedItem.getItem();
        assertTrue(removedItemColor.isPresent());
        assertEquals(ItemColor.YELLOW, removedItemColor.get());

        // Items to be taken
        List<ComponentHelper<ItemColor>> takenItems = station.getItemsToBeTaken();
        assertEquals(1, takenItems.size());

        ComponentHelper<ItemColor> takenItem = takenItems.get(0);
        Optional<ItemColor> takenItemColor = takenItem.getItem();
        assertTrue(takenItemColor.isPresent());
        assertEquals(ItemColor.GREEN, takenItemColor.get());
    }
}

