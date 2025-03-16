package it.polimi.ingsw.is25am28.ActionJSON;

import it.polimi.ingsw.is25am28.Components.Cabin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;

public class SlaversJSON extends ActionJSON {

    public SlaversJSON () {
        super();
    }

    public SlaversJSON (JSONObject data) {
        super(data);
    }

    public SlaversJSON (String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }

    // Getters
    public boolean getTakeCredits() throws IllegalStateException {
        if (!data.containsKey("takeCredits")) {
            throw new IllegalStateException("Key 'takeCredits' is missing in JSON data");
        }
        return (boolean)data.get("takeCredits");
    }
    @SuppressWarnings("unchecked")
    public ArrayList<Cabin> getCrewToRemove() throws IllegalStateException {
        if (!data.containsKey("crewToRemove")) {
            throw new IllegalStateException("Key 'crewToRemove' is missing in JSON data");
        }
        return (ArrayList<Cabin>) data.get("crewToRemove");
    }


    // Setters
    @SuppressWarnings("unchecked")
    public void setTakeCredits (boolean takeCredits) throws IllegalArgumentException {
        data.put("takeCredits", takeCredits);
    }
    @SuppressWarnings("unchecked")
    public void setCrewToRemove (ArrayList<String> crewToRemove) {
        data.put("crewToRemove", crewToRemove);
    }

}
