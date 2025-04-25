package it.polimi.ingsw.is25am28.Model.ActionJSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * This class needs to contain all the attribute that can be used by the clients to interact with the cards
 *
 * We can just use this single class to cover all the desired data
 * */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardStateJSON extends ActionJSON {
    private String cardName;
    private int cardLevel;
    private boolean isCardUsable;
    private Map<String, Integer> updatedPositions;
    private List<String> eliminatedPlayers;
    private boolean needsBoardUpdate;
    private boolean needsShipsUpdate;
    private boolean hasBeenActivated;

    // ======== RESOURCES/CREW INFORMATION ======== //
    private List<ComponentHelper<ItemColor>> resourcesToTake;
    private List<ComponentHelper<ItemColor>> resourcesToDrop;
    private List<ComponentHelper<LifeformType>> lifeformsToRemove;

    // ======== START MODEL INFORMATION ======== //

    private BoardJSON board;

    // ======== END MODEL INFORMATION ======== //

    // ======== START PLAYER INFORMATION ======== //

    private Map<String, Float> playersFirePower;
    private Map<String, Integer> playersEnginePower;
    private Map<String, Integer> playersBatteries;
    private Map<String, PlayerJSON> playersInfo;

    // ======== END PLAYER INFORMATION ======== //

    private int requiredFirepower;
    private int givenCredits;
    private int movementSteps;
    private int takenCrew;
    private int takenItems;
    private int redItems;
    private int yellowItems;
    private int blueItems;
    private int greenItems;
    private ArrayList<String> defeatedPlayers;

    private ArrayList<ArrayList<Integer>> shootingSequence;
    private int requiredCrewMembers;
    private List<ItemColor> stationResources;

    // ======== WarZone Card State Attributes ======== //

    private String affectedPlayer;
    private int requiredResources;
    private Map<String, Integer> currPlasmaShotDescriptor;

    // ======== VisitPlanets Card State Attributes ======== //

    private Map<Integer, Map<ItemColor, Integer>> availablePlanets;
    private int chosenPlanetIndex;

    // ======== MeteorShower Card State Attributes ======== //

    private int currMeteorIndex;
    private int diceThrowResult;
    private Map<String, Integer> currMeteorDescriptor;
    private Map<String, List<Map<String, Object>>> previousPlayerRemovedComponents;

    // ======== Model Information Methods ======== //

    @JsonGetter("board")
    public BoardJSON getBoard() {
        return board;
    }

    @JsonSetter("board")
    public void setBoard(BoardJSON board) {
        this.board = board;
    }

    // ======== Player information methods ======== //
    @JsonGetter("playersFirePower")
    public Map<String, Float> getPlayersFirePower() {
        return this.playersFirePower;
    }

    @JsonSetter("playersFirePower")
    public void setPlayersFirePower(Map<String, Float> playersFirePower) {
        this.playersFirePower = playersFirePower;
    }

    @JsonGetter("playersEnginePower")
    public Map<String, Integer> getPlayersEnginePower() {
        return this.playersEnginePower;
    }

    @JsonSetter("playersEnginePower")
    public void setPlayersEnginePower(Map<String, Integer> playersEnginePower) {
        this.playersEnginePower = playersEnginePower;
    }

    @JsonGetter("playersBatteries")
    public Map<String, Integer> getPlayersBatteries() {
        return this.playersBatteries;
    }

    @JsonSetter("playersBatteries")
    public void setPlayersBatteries(Map<String, Integer> playersBatteries) {
        this.playersBatteries = playersBatteries;
    }

    @JsonGetter("playersInfo")
    public Map<String, PlayerJSON> getPlayersInfo() {
        return playersInfo;
    }

    @JsonSetter("playersInfo")
    public void setPlayersInfo(Map<String, PlayerJSON> playersInfo) {
        this.playersInfo = playersInfo;
    }

    // ======== Resource/Crew Methods ========//
    @JsonSetter("resourcesToDrop")
    public void setResourcesToDrop(List<ComponentHelper<ItemColor>> resourcesToDrop) {
        this.resourcesToDrop = resourcesToDrop;
    }
    @JsonGetter("resourcesToDrop")
    public List<ComponentHelper<ItemColor>> getResourcesToDrop() {
        return this.resourcesToDrop;
    }
    @JsonSetter("resourcesToTake")
    public void setResourcesToTake(List<ComponentHelper<ItemColor>> resourcesToTake) {
        this.resourcesToTake = resourcesToTake;
    }
    @JsonGetter("resourcesToTake")
    public List<ComponentHelper<ItemColor>> getResourcesToTake() {
        return this.resourcesToTake;
    }
    @JsonSetter("lifeformsToRemove")
    public void setLifeformsToRemove(List<ComponentHelper<LifeformType>> lifeformsToRemove) {
        this.lifeformsToRemove = lifeformsToRemove;
    }
    @JsonGetter
    public void getLifeformsToRemove(List<ComponentHelper<LifeformType>> lifeformsToRemove) {
        this.lifeformsToRemove = lifeformsToRemove;
    }


    // ======== Enemies Card State Attributes ========//
    @JsonSetter("requiredFirepower")
    public void setRequiredFirepower(int requiredFirepower) {
        this.requiredFirepower = requiredFirepower;
    }

    @JsonGetter("requiredFirepower")
    public int getRequiredFirepower() {
        return Math.max(requiredFirepower, 0);
    }

    @JsonSetter("defeatedPlayers")
    public void setDefeatedPlayers(List<String> defeatedPlayers) {
        this.defeatedPlayers = new ArrayList<>();
    }

    @JsonGetter("defeatedPlayers")
    public List<String> getDefeatedPlayers() {
        return defeatedPlayers;
    }


    // ======== Pirates Card State Attributes ========//
    private boolean firstRound;

    @JsonSetter("firstRound")
    public void setFirstRound(boolean firstRound) {
        this.firstRound = firstRound;
    }

    @JsonGetter("firstRound")
    public boolean getFirstRound() {
        return firstRound;
    }


    // ======== Smugglers Card State Attributes ========//
    @JsonSetter("takenItems")
    public void setTakenItems(int takenItems) {
        this.takenItems = takenItems;
    }

    @JsonSetter("takenItems")
    public int getTakenItems() {
        return takenItems;
    }

    @JsonSetter("redItems")
    public void setRedItems(int redItems) {
        this.redItems = redItems;
    }

    @JsonGetter("redItems")
    public int getRedItems() {
        return redItems;
    }

    @JsonSetter("yellowItems")
    public void setYellowItems(int yellowItems) {
        this.yellowItems = yellowItems;
    }

    @JsonGetter("yellowItems")
    public int getYellowItems() {
        return yellowItems;
    }

    @JsonSetter("blueItems")
    public void setBlueItems(int blueItems) {
        this.blueItems = blueItems;
    }

    @JsonGetter("blueItems")
    public int getBlueItems() {
        return blueItems;
    }

    @JsonSetter("greenItems")
    public void setGreenItems(int greenItems) {
        this.greenItems = greenItems;
    }

    @JsonGetter("greenItems")
    public int getGreenItems() {
        return greenItems;
    }


    // ======== Slavers Card State Attributes ========//
    @JsonSetter("takenCrew")
    public int getTakenCrew() {
        return takenCrew;
    }

    @JsonGetter("takenCrew")
    public void setTakenCrew(int takenCrew) {
        this.takenCrew = takenCrew;
    }


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
    @JsonGetter("cardName")
    public String getCardName() throws IllegalStateException {
        return this.cardName;
    }

    /**
     * Set the cardName to the given data
     * */
    @JsonSetter("cardName")
    public void setCardName(String cardName) throws IllegalStateException {
        this.cardName = cardName;
    }

    /**
     * Returns the cardLevel
     * */
    @JsonGetter("cardLevel")
    public int getCardLevel() throws IllegalStateException {
        return this.cardLevel;
    }

    /**
     * Set the cardLevel to the given data
     * */
    @JsonSetter("cardLevel")
    public void setCardLevel(int cardLevel) throws IllegalStateException {
        this.cardLevel = cardLevel;
    }

    /**
     * Returns the isCardUsable
     * */
    @JsonGetter("isCardUsable")
    public boolean getIsCardUsable() throws IllegalStateException {
        return this.isCardUsable;
    }

    @JsonSetter("isCardUsable")
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

    @JsonSetter("updatedPositions")
    public void setUpdatedPositions(Map<String, Integer> updatedPositions) {
        this.updatedPositions = updatedPositions;
    }
    @JsonGetter("updatedPositions")
    public Map<String, Integer> getUpdatedPositions() {
        return this.updatedPositions;
    }

    @JsonSetter("eliminatedPlayers")
    public void setEliminatedPlayers(List<String> eliminatedPlayers) {
        this.eliminatedPlayers = eliminatedPlayers;
    }

    @JsonGetter("eliminatedPlayers")
    public List<String> getEliminatedPlayers() {
        return this.eliminatedPlayers;
    }

    @JsonSetter("needsBoardUpdate")
    public void setNeedsBoardUpdate(boolean needsBoardUpdate) {
        this.needsBoardUpdate = needsBoardUpdate;
    }

    @JsonGetter("needsBoardUpdate")
    public boolean getNeedsBoardUpdate() {
        return this.needsBoardUpdate;
    }

    @JsonSetter("hasBeenActivated")
    public void setHasBeenActivated(boolean hasBeenActivated) {
        this.hasBeenActivated = hasBeenActivated;
    }

    @JsonGetter("hasBeenActivated")
    public boolean getHasBeenActivated() {
        return this.hasBeenActivated;
    }

    @JsonSetter("needsShipsUpdate")
    public void setNeedsShipsUpdate(boolean needsShipsUpdate) {
        this.needsShipsUpdate = needsShipsUpdate;
    }

    @JsonGetter("needsShipsUpdate")
    public boolean getNeedsShipsUpdate() {
        return this.needsShipsUpdate;
    }

    // Other data can be added to provide the context to the clients


    // ======== WarZone Card State Getters/Setters ======== //

    @JsonGetter("affectedPlayer")
    public String getAffectedPlayer() {
        return this.affectedPlayer;
    }

    @JsonSetter("affectedPlayer")
    public void setAffectedPlayer(String affectedPlayer) {
        this.affectedPlayer = affectedPlayer;
    }

    @JsonGetter("requiredResources")
    public int getRequiredResources() {
        return this.requiredResources;
    }

    @JsonSetter("requiredResources")
    public void setRequiredResources(int requiredResources) {
        this.requiredResources = requiredResources;
    }

    @JsonGetter("currPlasmaShotDescriptor")
    public Map<String, Integer> getCurrPlasmaShotDescriptor() {
        return this.currPlasmaShotDescriptor;
    }

    @JsonSetter("currPlasmaShotDescriptor")
    public void setCurrPlasmaShotDescriptor(Map<String, Integer> currPlasmaShotDescriptor) {
        this.currPlasmaShotDescriptor = currPlasmaShotDescriptor;
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

    @JsonGetter("chosenPlanetIndex")
    public int getChosenPlanetIndex() {
        return this.chosenPlanetIndex;
    }
    @JsonSetter("chosenPlanetIndex")
    public void setChosenPlanetIndex(int chosenPlanetIndex) {
        this.chosenPlanetIndex = chosenPlanetIndex;
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
    public Map<String, Integer> getCurrMeteorDescriptor() {
        return this.currMeteorDescriptor;
    }

    @JsonSetter("currMeteorDescriptor")
    public void setCurrMeteorDescriptor(Map<String, Integer> currMeteorDescriptor) {
        this.currMeteorDescriptor = currMeteorDescriptor;
    }

    @JsonGetter("previousPlayerRemovedComponents")
    public Map<String, List<Map<String, Object>>> getPreviousPlayerRemovedComponents() {
        return this.previousPlayerRemovedComponents;
    }

    @JsonSetter("previousPlayerRemovedComponents")
    public void setPreviousPlayerRemovedComponents(Map<String, List<Map<String, Object>>> removedComponentsPerPlayer) {
        this.previousPlayerRemovedComponents = removedComponentsPerPlayer;
    }

    // ======== //
}