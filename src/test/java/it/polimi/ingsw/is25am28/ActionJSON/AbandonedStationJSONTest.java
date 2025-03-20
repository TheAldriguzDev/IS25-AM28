package it.polimi.ingsw.is25am28.ActionJSON;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationJSONTest {

//    @Test
//    void testConstructorWithEmptyJSON() {
//        AbandonedStationJSON json = new AbandonedStationJSON();
//        assertNotNull(json);
//    }
//
//    @Test
//    void testVisitStation_SetAndGet() {
//        AbandonedStationJSON json = new AbandonedStationJSON();
//        json.setVisitStation(true);
//        assertTrue(json.getVisitStation());
//
//        json.setVisitStation(false);
//        assertFalse(json.getVisitStation());
//    }
//
//    @Test
//    void testVisitStation_MissingKeyThrowsException() {
//        AbandonedStationJSON json = new AbandonedStationJSON();
//        assertThrows(IllegalStateException.class, json::getVisitStation);
//    }
//
//    @Test
//    void testSetAndGetResourcesToDropOff() {
//        AbandonedStationJSON json = new AbandonedStationJSON();
//        List<Item> items = List.of(
//                new Item(ItemColor.RED),
//                new Item(ItemColor.BLUE),
//                new Item(ItemColor.GREEN)
//        );
//
//        json.setResourcesToDropOff(items);
//        List<Item> retrievedItems = json.getResourcesToDropOff();
//
//        assertEquals(items.size(), retrievedItems.size());
//        assertEquals(items.get(0).getColor(), retrievedItems.get(0).getColor());
//        assertEquals(items.get(1).getColor(), retrievedItems.get(1).getColor());
//        assertEquals(items.get(2).getColor(), retrievedItems.get(2).getColor());
//    }
//
//    @Test
//    void testGetResourcesToDropOff_MissingKeyThrowsException() {
//        AbandonedStationJSON json = new AbandonedStationJSON();
//        assertThrows(IllegalStateException.class, json::getResourcesToDropOff);
//    }
//
//    @Test
//    void testGetResourcesToDropOff_InvalidDataThrowsException() {
//        JSONObject invalidData = new JSONObject();
//        invalidData.put("resourcesToDropOff", 123); // Is not a JSONArray
//        AbandonedStationJSON json = new AbandonedStationJSON(invalidData);
//
//        assertThrows(IllegalStateException.class, json::getResourcesToDropOff);
//    }
//
//    @Test
//    void testGetResourcesToDropOff_UnknownItemThrowsException() {
//        JSONObject invalidData = new JSONObject();
//        invalidData.put("resourcesToDropOff", List.of("RED", "UNKNOWN", "BLUE")); // UNKNOWN is not a valid resource
//        AbandonedStationJSON json = new AbandonedStationJSON(invalidData);
//
//        assertThrows(IllegalStateException.class, json::getResourcesToDropOff);
//    }
//
//    @Test
//    void testConstructorWithJSONString() throws ParseException {
//        String jsonString = "{\"visitStation\":true,\"resourcesToDropOff\":[\"RED\",\"BLUE\"]}";
//        AbandonedStationJSON json = new AbandonedStationJSON(jsonString);
//
//        assertTrue(json.getVisitStation());
//
//        List<Item> items = json.getResourcesToDropOff();
//
//        assertEquals(2, items.size());
//        assertEquals(ItemColor.RED, items.get(0).getColor());
//        assertEquals(ItemColor.BLUE, items.get(1).getColor());
//    }
}
