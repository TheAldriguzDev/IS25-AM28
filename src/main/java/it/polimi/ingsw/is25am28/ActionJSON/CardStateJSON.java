package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class needs to contain all the attribute that can be used by the clients to interact with the cards
 *
 * We can just use this single class to cover all the desired data
 * */
public class CardStateJSON extends ActionJSON {
    private String cardName;
    private int cardLevel;
    private boolean isCardUsable;

    /**
     * Default constructor
     * */
    public CardStateJSON() {
        this.cardLevel = 0;
        this.isCardUsable = true;
    }

    public CardStateJSON(@JsonProperty("playerNickname") String playerNickname,
                             @JsonProperty("cardName") String cardName,
                             @JsonProperty("cardLevel") int cardLevel,
                             @JsonProperty("isCardUsable") boolean isCardUsable) {
        super(playerNickname);
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.isCardUsable = isCardUsable;
    }

    /**
     * Returns the cardName
     * */
    public String getCardName() throws IllegalStateException {
        if (this.cardName == null || this.cardName.isEmpty()) {
            throw new IllegalStateException("Key 'cardName' is missing in JSON data");
        }

        return this.cardName;
    }

    /**
     * Set the cardName to the given data
     * */
    public void setCardName(String cardName) throws IllegalStateException {
        if (cardName == null || cardName.isEmpty()) {
            throw new IllegalStateException("cardName cannot be null or empty");
        }

        this.cardName = cardName;
    }

    /**
     * Returns the cardLevel
     * */
    public int getCardLevel() throws IllegalStateException {
        return this.cardLevel;
    }

    /**
     * Set the cardLevel to the given data
     * */
    public void setCardLevel(int cardLevel) throws IllegalStateException {
        if (cardLevel <= 0) {
            throw new IllegalStateException("cardLevel cannot be zero or negative");
        }

        this.cardLevel = cardLevel;
    }

    /**
     * Returns the isCardUsable
     * */
    public boolean getIsCardUsable() throws IllegalStateException {
        return this.isCardUsable;
    }

    /**
     * Set the isCardUsable to the given data
     * */
    public void setCardIsUsable(boolean isCardUsable) throws IllegalStateException {
        this.isCardUsable = isCardUsable;
    }
    // Other data can be added to provide the context to the clients
}
