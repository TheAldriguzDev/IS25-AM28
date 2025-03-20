package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Components.Cabin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;

public class SlaversJSON extends ActionJSON {
    private final boolean takeCredits;
    private final ArrayList<Cabin> crewToRemove;
    private final int numberOfDoubleCannonsActivated;

    public SlaversJSON (@JsonProperty("playerNickname") String playerNickname,
                        @JsonProperty("takeCredits") boolean takeCredits,
                        @JsonProperty("crewToRemove") ArrayList<Cabin> crewToRemove,
                        @JsonProperty("numberOfDoubleCannonsActivated") int numberOfDoubleCannonsActivated) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.crewToRemove = crewToRemove;
        this.numberOfDoubleCannonsActivated = numberOfDoubleCannonsActivated;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public ArrayList<Cabin> getCrewToRemove() {
        return crewToRemove;
    }

    public int getNumberOfDoubleCannonsActivated() {
        return numberOfDoubleCannonsActivated;
    }
    /*
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
    public int getNumOfDoubleCannonsActivated() throws IllegalStateException {
        if (!data.containsKey("numOfDoubleCannonsActivated")) {
            throw new IllegalStateException("Key 'numberOfDoubleCannonsActivated' is missing in JSON data");
        }
        return (int)data.get("numOfDoubleCannonsActivated");
    }*/

    /*
    // Setters
    @SuppressWarnings("unchecked")
    public void setTakeCredits (boolean takeCredits) throws IllegalArgumentException {
        data.put("takeCredits", takeCredits);
    }
    @SuppressWarnings("unchecked")
    public void setCrewToRemove (ArrayList<Cabin> crewToRemove) {
        data.put("crewToRemove", crewToRemove);
    }
    @SuppressWarnings("unchecked")
    public void setNumOfDoubleCannonsActivated (int numOfDoubleCannonsActivated) throws IllegalArgumentException {
        data.put("numOfDoubleCannonsActivated", numOfDoubleCannonsActivated);
    }*/
}