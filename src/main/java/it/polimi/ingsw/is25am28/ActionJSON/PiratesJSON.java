package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class PiratesJSON extends ActionJSON {
    private final boolean takeCredits;
    private final boolean shieldAbove;
    private final boolean shieldRight;
    private final boolean shieldBelow;
    private final boolean shieldLeft;
    private final int numberOfDoubleCannonsActivated;
    private final ArrayList<Integer> dicesResults;

    public PiratesJSON(@JsonProperty("PlayerNickname") String playerNickname,
                       @JsonProperty("takeCredits") boolean takeCredits,
                       @JsonProperty("shieldAbove") boolean shieldAbove,
                       @JsonProperty("shieldRight") boolean shieldRight,
                       @JsonProperty("shieldBelow") boolean shieldBelow,
                       @JsonProperty("shieldLeft") boolean shieldLeft,
                       @JsonProperty("numberOfDoubleCannonsActivated") int numberOfDoubleCannonsActivated,
                       @JsonProperty("dicesResults") ArrayList<Integer> dicesResults) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.shieldAbove = shieldAbove;
        this.shieldRight = shieldRight;
        this.shieldBelow = shieldBelow;
        this.shieldLeft = shieldLeft;
        this.numberOfDoubleCannonsActivated = numberOfDoubleCannonsActivated;
        this.dicesResults = dicesResults;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public boolean getShieldAbove() {
        return shieldAbove;
    }

    public boolean getShieldRight() {
        return shieldRight;
    }

    public boolean getShieldBelow() {
        return shieldBelow;
    }

    public boolean getShieldLeft() {
        return shieldLeft;
    }

    public int getNumberOfDoubleCannonsActivated() {
        return numberOfDoubleCannonsActivated;
    }

    public ArrayList<Integer> getDicesResults() {
        return dicesResults;
    }

    /*s
    public PiratesJSON(JSONObject data) {
        super(data);
    }

    public PiratesJSON(String dataString) throws ParseException {
        super(ActionJSON.Parse(dataString));
    }
    // Getters
    public boolean getTakeCredits() throws IllegalStateException {
        if (!data.containsKey("takeCredits")) {
            throw new IllegalStateException("Key 'takeCredits' is missing in JSON data");
        }
        return (boolean)data.get("takeCredits");
    }

    public boolean getShieldAbove() throws IllegalStateException {
        if (!data.containsKey("shieldAbove")) {
            throw new IllegalStateException("Key 'shieldAbove' is missing in JSON data");
        }
        return (boolean)data.get("shieldAbove");
    }

    public boolean getShieldBelow() throws IllegalStateException {
        if (!data.containsKey("shieldBelow")) {
            throw new IllegalStateException("Key 'shieldBelow' is missing in JSON data");
        }
        return (boolean)data.get("shieldBelow");
    }

    public boolean getShieldRight() throws IllegalStateException {
        if (!data.containsKey("shieldRight")) {
            throw new IllegalStateException("Key 'shieldRight' is missing in JSON data");
        }
        return (boolean)data.get("shieldRight");
    }

    public boolean getShieldLeft() throws IllegalStateException {
        if (!data.containsKey("shieldLeft")) {
            throw new IllegalStateException("Key 'shieldLeft' is missing in JSON data");
        }
        return (boolean)data.get("shieldLeft");
    }
    @SuppressWarnings("unchecked")
    public ArrayList<Integer> getDicesResults() throws IllegalStateException {
        if (!data.containsKey("dicesResult")) {
            throw new IllegalStateException("Key 'dicesResult' is missing in JSON data");
        }
        return (ArrayList<Integer>)data.get("dicesResult");
    }
    public int getNumberOfDoubleCannonsActivated() throws IllegalStateException {
        if (!data.containsKey("numberOfDoubleCannonsActivated")) {
            throw new IllegalStateException("Key 'numberOfDoubleCannonsActivated' is missing in JSON data");
        }
        return (int)data.get("numberOfDoubleCannonsActivated");
    }
    */
    /*
    // Setters
    @SuppressWarnings("unchecked")
    public void setTakeCredits (boolean takeCredits) throws IllegalArgumentException {
        data.put("takeCredits", takeCredits);
    }
    @SuppressWarnings("unchecked")
    public void setShieldAbove (boolean shieldAbove) throws IllegalArgumentException {
        data.put("shieldAbove", shieldAbove);
    }
    @SuppressWarnings("unchecked")
    public void setShieldBelow (boolean shieldBelow) throws IllegalArgumentException {
        data.put("shieldBelow", shieldBelow);
    }
    @SuppressWarnings("unchecked")
    public void setShieldRight (boolean shieldRight) throws IllegalArgumentException {
        data.put("shieldRight", shieldRight);
    }
    @SuppressWarnings("unchecked")
    public void setShieldLeft (boolean shieldLeft) throws IllegalArgumentException {
        data.put("shieldLeft", shieldLeft);
    }
    @SuppressWarnings("unchecked")
    public void setDicesResults (ArrayList<Integer> dicesResults) throws IllegalArgumentException {
        data.put("dices", dicesResults);
    }
    @SuppressWarnings("unchecked")
    public void setNumOfDoubleCannonsActivated (int numOfDoubleCannonsActivated) throws IllegalArgumentException {
        data.put("numOfDoubleCannonsActivated", numOfDoubleCannonsActivated);
    }
    */
}