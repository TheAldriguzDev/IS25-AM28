package it.polimi.ingsw.is25am28.ActionJSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class SmugglersJSON extends ActionJSON {

    public SmugglersJSON () {
        super();
    }

    public SmugglersJSON (JSONObject data) {
        super(data);
    }

    public SmugglersJSON (String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    // Getters
    public boolean getTakeLoot() throws IllegalStateException {
        if (!data.containsKey("takeCredits")) {
            throw new IllegalStateException("Key 'takeCredits' is missing in JSON data");
        }
        return (boolean)data.get("takeLoot");
    }

    public int getRedToTake() throws IllegalStateException {
        if (!data.containsKey("redToTake")) {
            throw new IllegalStateException("Key 'redToTake' is missing in JSON data");
        }
        return (int)data.get("redToTake");
    }

    public int getYellowToTake() throws IllegalStateException {
        if (!data.containsKey("yellowToTake")) {
            throw new IllegalStateException("Key 'yellowToTake' is missing in JSON data");
        }
        return (int)data.get("yellowToTake");
    }

    public int getBlueToTake() throws IllegalStateException {
        if (!data.containsKey("blueToTake")) {
            throw new IllegalStateException("Key 'blueToTake' is missing in JSON data");
        }
        return (int)data.get("blueToTake");
    }

    public int getGreenToTake() throws IllegalStateException {
        if (!data.containsKey("greenToTake")) {
            throw new IllegalStateException("Key 'greenToTake' is missing in JSON data");
        }
        return (int)data.get("greenToTake");
    }


    // Setters
    @SuppressWarnings("unchecked")
    public void setTakeLoot (boolean takeLoot) throws IllegalArgumentException {
        data.put("takeLoot", takeLoot);
    }
    @SuppressWarnings("unchecked")
    public void setRedToTake (int redToTake) throws IllegalArgumentException {
        data.put("redToTake", redToTake);
    }
    @SuppressWarnings("unchecked")
    public void setYellowToTake (int redToTake) throws IllegalArgumentException {
        data.put("redToTake", redToTake);
    }
    @SuppressWarnings("unchecked")
    public void setBlueToTake (int redToTake) throws IllegalArgumentException {
        data.put("redToTake", redToTake);
    }
    @SuppressWarnings("unchecked")
    public void setGreenToTake (int redToTake) throws IllegalArgumentException {
        data.put("redToTake", redToTake);
    }
}
