package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class ActionJSON {
    protected JSONObject data;

    /**
     * Constructor used to build a new JSONObject (client side)
     * */
    public ActionJSON() {
        data = new JSONObject();
    }

    /**
     * Constructor used when we need to parse a given JSONObject (server side)
     * */
    public ActionJSON(JSONObject data) {
        this.data = data;
    }

    /**
     * Returns the JSON data
     * */
    public JSONObject getData() {
        return data;
    }

    /**
     * Set the JSON data
     * */
    @SuppressWarnings("unchecked")
    public void setData(JSONObject data) {
        this.data.clear();
        this.data.putAll(data);
    }

    /**
     * Returns the player nickname that should be set in the JSON, otherwise it throws an Exception
     * */
    public String getPlayerNickname() throws IllegalStateException{
        if (!data.containsKey("playerNickname")) {
            throw new IllegalStateException("Key 'playerNickname' is missing in JSON data");
        }

        return (String) data.get("playerNickname");
    }

    /**
     * Set the player nickname to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setPlayerNickname(String playerNickname) throws IllegalArgumentException {
        if (playerNickname == null) {
            throw new IllegalArgumentException("Player nickname cannot be null");
        }

        data.put("playerNickname", playerNickname);
    }

    /**
     * Returns the string of the given JSONObject
     * */
    public static String Stringify(JSONObject data) throws IllegalArgumentException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("The JSON string is either null or empty");
        }

        return data.toString();
    }

    /**
     * Returns the JSONObject of the given JSON String
     * */
    public static JSONObject Parse(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data);
    }
}
