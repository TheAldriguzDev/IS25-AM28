package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class CardStateJSON extends ActionJSON {
    /**
     * Constructor used when we need to BUILD a new JSON
     * */
    public CardStateJSON() {
        super();
    }

    /**
     * Constructor used when we need to PARSE a JSONObject
     * */
    public CardStateJSON(JSONObject data) {
        super(data);
    }

    /**
     * Constructor used when we need to PARSE a JSONObject, but we only have a String that represent the JSONObject
     * */
    public CardStateJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    /**
     * Returns the cardName that should be set in the JSON, otherwise it throws an Exception
     * */
    public String getCardName() throws IllegalStateException{
        if (!data.containsKey("cardName")) {
            throw new IllegalStateException("Key 'cardName' is missing in JSON data");
        }

        return (String) data.get("cardName");
    }

    /**
     * Set the cardName to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setCardName(String cardName) throws IllegalArgumentException {
        if (cardName == null || cardName.isEmpty()) {
            throw new IllegalArgumentException("cardName cannot be null");
        }

        data.put("cardName", cardName);
    }

    /**
     * Returns the cardLevel that should be set in the JSON, otherwise it throws an Exception
     * */
    public int getCardLevel() throws IllegalStateException{
        if (!data.containsKey("cardLevel")) {
            throw new IllegalStateException("Key 'cardLevel' is missing in JSON data");
        }

        return (int) data.get("cardLevel");
    }

    /**
     * Set the cardName to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setCardLevel(int cardLevel) throws IllegalArgumentException {
        if (cardLevel < 0) {
            throw new IllegalArgumentException("cardLevel cannot be null");
        }

        data.put("cardLevel", cardLevel);
    }


    /**
     * Returns the cardLevel that should be set in the JSON, otherwise it throws an Exception
     * */
    public boolean getIsCardUsable() throws IllegalStateException{
        if (!data.containsKey("isCardUsable")) {
            throw new IllegalStateException("Key 'isCardUsable' is missing in JSON data");
        }

        return (boolean) data.get("isCardUsable");
    }

    /**
     * Set the cardName to the given value
     * */
    @SuppressWarnings("unchecked")
    public void setIsCardUsable(boolean isCardUsable) throws IllegalArgumentException {
        data.put("cardLevel", isCardUsable);
    }
}
