package it.polimi.ingsw.is25am28.ActionJSON;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javafx.util.Pair;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * This class needs to contain all the attribute that can be used by the clients to interact with the cards
 *
 * We can just use this single class to cover all the desired data
 * */
public class CardStateJSON extends ActionJSON {

    private String cardName;
    private int cardLevel;
    private boolean isCardUsable;

    private int requiredFirepower;
    private int givenCredits;
    private int movementSteps;
    private int takenCrew;
    private int takenItems;
    private int redItems;
    private int yellowItems;
    private int blueItems;
    private int greenItems;
    private ArrayList<ArrayList<Integer>> shootingSequence;
    private int requiredCrewMembers;
    private List<ItemColor> stationResources;

    // ======== WarZone Card State Attributes ======== //

    private Player lowestCrewPlayer;
    private Player lowestEnginePowerPlayer;
    private Player lowestFirePowerPlayer;

    // ======== VisitPlanets Card State Attributes ======== //

    Map<Integer, Map<ItemColor, Integer>> availablePlanets;

    // ======== MeteorShower Card State Attributes ======== //

    private int currMeteorIndex;
    private int diceThrowResult;
    private Pair<Integer, Integer> currMeteorDescriptor;

    // ======== //

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
                             @JsonProperty("isCardUsable") boolean isCardUsable,
                             @JsonProperty("givenCredits") int givenCredits,
                             @JsonProperty("requiredCrewMembers") int requiredCrewMembers,
                             @JsonProperty("movementSteps") int movementSteps,
                             @JsonProperty("stationResources") List<ItemColor> stationResources) {
        super(playerNickname);
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.isCardUsable = isCardUsable;
        this.givenCredits = givenCredits;
        this.requiredCrewMembers = requiredCrewMembers;
        this.movementSteps = movementSteps;
        this.stationResources = stationResources;
    }

    // Constructor for Pirates cardState
    public CardStateJSON(@JsonProperty("playerNickname") String playerNickname,
                         @JsonProperty("cardName") String cardName,
                         @JsonProperty("cardLevel") int cardLevel,
                         @JsonProperty("isCardUsable") boolean isCardUsable,
                         @JsonProperty("requiredFirepower") int requiredFirepower,
                         @JsonProperty("givenCredits") int givenCredits,
                         @JsonProperty("movementSteps") int movementSteps,
                         @JsonProperty("shootingSequence") ArrayList<ArrayList<Integer>> shootingSequence) {
        super(playerNickname);
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.isCardUsable = isCardUsable;
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.shootingSequence = shootingSequence;
    }

    // Constructor for Slavers cardState
    public CardStateJSON(@JsonProperty("playerNickname") String playerNickname,
                         @JsonProperty("cardName") String cardName,
                         @JsonProperty("cardLevel") int cardLevel,
                         @JsonProperty("isCardUsable") boolean isCardUsable,
                         @JsonProperty("requiredFirepower") int requiredFirepower,
                         @JsonProperty("givenCredits") int givenCredits,
                         @JsonProperty("movementSteps") int movementSteps,
                         @JsonProperty("takenCrew") int takenCrew) {
        super(playerNickname);
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.isCardUsable = isCardUsable;
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.takenCrew = takenCrew;
    }

    // Constructor for Smugglers cardState
    public CardStateJSON(@JsonProperty("playerNickname") String playerNickname,
                         @JsonProperty("cardName") String cardName,
                         @JsonProperty("cardLevel") int cardLevel,
                         @JsonProperty("isCardUsable") boolean isCardUsable,
                         @JsonProperty("requiredFirepower") int requiredFirepower,
                         @JsonProperty("movementSteps") int movementSteps,
                         @JsonProperty("takenItems") int takenItems,
                         @JsonProperty("redItems") int redItems,
                         @JsonProperty("yellowItems") int yellowItems,
                         @JsonProperty("blueItems") int blueItems,
                         @JsonProperty("greenItems") int greenItems) {
        super(playerNickname);
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.isCardUsable = isCardUsable;
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.takenItems = takenItems;
        this.redItems = redItems;
        this.yellowItems = yellowItems;
        this.blueItems = blueItems;
        this.greenItems = greenItems;
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

    public void setCardIsUsable(boolean isCardUsable) throws IllegalStateException {
        this.isCardUsable = isCardUsable;
    }

    // Cards specific attributes needed to send a correct state to the client

    @JsonGetter("givenCredits")
    public int getGivenCredits() {
        return this.givenCredits;
    }

    @JsonSetter("givenCredits")
    public void setGivenCredits(int givenCredits) {
        this.givenCredits = Math.max(givenCredits, 0);
    }

    @JsonGetter("requiredCrewMembers")
    public int getRequiredCrewMembers() {
        return this.requiredCrewMembers;
    }

    @JsonSetter("requiredCrewMembers")
    public void setRequiredCrewMembers(int requiredCrewMembers) {
        this.requiredCrewMembers = Math.max(requiredCrewMembers, 0);
    }

    @JsonGetter("movementSteps")
    public int getMovementSteps() {
        return this.movementSteps;
    }

    @JsonSetter("movementSteps")
    public void setMovementSteps(int movementSteps) {
        this.movementSteps = Math.max(movementSteps, 0);
    }

    @JsonGetter("stationResources")
    public List<ItemColor> getStationResources() {
        return this.stationResources;
    }

    @JsonSetter("stationResources")
    public void setStationResources(List<ItemColor> stationResources) {
        this.stationResources = stationResources;
    }

    // Other data can be added to provide the context to the clients


    // ======== WarZone Card State Getters/Setters ======== //

    @JsonGetter("lowestCrewPlayer")
    public Player getLowestCrewPlayer() {
        return this.lowestCrewPlayer;
    }

    @JsonSetter("lowestCrewPlayer")
    public void setLowestCrewPlayer(Player lowestCrewPlayer) {
        this.lowestCrewPlayer = lowestCrewPlayer;
    }

    @JsonGetter("lowestEnginePowerPlayer")
    public Player getLowestEnginePowerPlayer() {
        return this.lowestEnginePowerPlayer;
    }

    @JsonSetter("lowestEnginePowerPlayer")
    public void setLowestEnginePowerPlayer(Player lowestEnginePowerPlayer) {
        this.lowestEnginePowerPlayer = lowestEnginePowerPlayer;
    }

    @JsonGetter("lowestFirePowerPlayer")
    public Player getLowestFirePowerPlayer() {
        return this.lowestFirePowerPlayer;
    }

    @JsonSetter("lowestFirePowerPlayer")
    public void setLowestFirePowerPlayer(Player lowestFirePowerPlayer) {
        this.lowestFirePowerPlayer = lowestFirePowerPlayer;
    }

    // ======== VisitPlanets Card State Getters/Setters ======== //

    @JsonGetter("availablePlanets")
    public Map<Integer, Map<ItemColor, Integer>> getAvailablePlanets() {
        return this.availablePlanets;
    }

    @JsonSetter("availablePlanets")
    public void setAvailablePlanets(Map<Integer, Map<ItemColor, Integer>> availablePlanets) {
        this.availablePlanets = availablePlanets;
    }

    // ======== MeteorShower Card State Getters/Setters ======== //

    @JsonGetter("currMeteorIndex")
    public int getCurrMeteorIndex() {
        return this.currMeteorIndex;
    }

    @JsonSetter("currMeteorIndex")
    public void setCurrMeteorIndex(int currMeteorIndex) {
        this.currMeteorIndex = currMeteorIndex;
    }

    @JsonGetter("diceThrowResult")
    public int getDiceThrowResult() {
        return this.diceThrowResult;
    }

    @JsonSetter("diceThrowResult")
    public void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    @JsonGetter("currMeteorDescriptor")
    public Pair<Integer, Integer> getCurrMeteorDescriptor() {
        return this.currMeteorDescriptor;
    }

    @JsonSetter("currMeteorDescriptor")
    public void setCurrMeteorDescriptor(Pair<Integer, Integer> currMeteorDescriptor) {
        this.currMeteorDescriptor = currMeteorDescriptor;
    }

    // ======== //
}