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
    private int id;
    private String cardName;
    private int cardLevel;
    private boolean isCardUsable;
    private boolean hasBeenActivated;

    // ==== CLIENT PLAYER FLAGS/INFORMATION ==== //
    private boolean needsPlayerUpdate;
    private boolean needsUpdatedCredits;
    private Map<String, Integer> updatedCredits;
    // ========================================= //

    // ======== CLIENT BOARD FLAGS/INFORMATION ======== //
        private boolean needsBoardUpdate;
        private boolean needsUpdatedPositions;
        private boolean needsUpdatedEliminatedPlayers;
        private Map<String, Integer> updatedPositions;
        private List<String> eliminatedPlayers;
    // =================================================//

    // ======= CLIENT SHIP FLAGS/INFORMATION ======== //
        // TODO: revise the maps with isEmpty check, there is the possibility they're not emptied after use, so once used they'll always be set // can just reset them after setting the state
        private boolean needsShipUpdate;
        private boolean needsUpdatedDroppedResources;
        private boolean needsUpdatedTakenResources;
        private boolean needsUpdatedRemovedLifeforms;
        private boolean needsUpdatedBatteries;
        private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
        private Map<String, List<ComponentHelper<ItemColor>>> takenResources;
        private Map<String , List<ComponentHelper<LifeformType>>> removedLifeforms;
        //private Map<String , List<ComponentHelper<Battery>>> removedBatteries;
        private Map<String, Integer> removedBatteries; // Temporary solution while deciding what to do about batteries
    // ===============================================//


    // ======== RESOURCES/CREW INFORMATION ======== //
        private List<ComponentHelper<ItemColor>> resourcesToTake;
        private List<ComponentHelper<ItemColor>> resourcesToDrop;
        private List<ComponentHelper<LifeformType>> lifeformsToRemove;
    // =============================================//


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
    private Map<String, String> actionsAndConsequences;
    private int currActionIndex;
    private boolean applyRequiredCrewConsequence;
    private boolean applyMovementStepsConsequence;
    private boolean applyShootingSequenceConsequence;
    private boolean applyLossItemsConsequence;

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

    public CardStateJSON(
            @JsonProperty("id") int id,
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("isCardUsable") boolean isCardUsable
    ) {
        super(playerNickname);
        this.id = id;
        this.cardName = cardName;
        this.cardLevel = cardLevel;
        this.isCardUsable = isCardUsable;
    }

    /**
     * Returns the card's id
     */
    @JsonGetter("id")
    public int getId() {
        return this.id;
    }

    /**
     * Sets the card's id
     */
    @JsonSetter("id")
    public void setId(int id) {
        this.id = id;
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

    @JsonSetter("hasBeenActivated")
    public void setHasBeenActivated(boolean hasBeenActivated) {
        this.hasBeenActivated = hasBeenActivated;
    }

    @JsonGetter("hasBeenActivated")
    public boolean getHasBeenActivated() {
        return this.hasBeenActivated;
    }


    // Other data can be added to provide the context to the clients

    // ======== CLIENT PLAYER STATE GETTERS/SETTERS ======== //
        // ==== MAIN FLAG ==== //
        @JsonSetter("needsPlayerUpdate")
        public void setNeedsPlayerUpdate(boolean needsPlayerUpdate) {
            this.needsPlayerUpdate = needsPlayerUpdate;
        }
        @JsonGetter("needsPlayerUpdate")
        public boolean getNeedsPlayerUpdate() {
            return this.needsPlayerUpdate;
        }

        // ==== CREDITS ==== //
        @JsonSetter("needsUpdatedCredits")
        public void setNeedsUpdatedCredits(boolean needsUpdatedCredits) {
            this.needsUpdatedCredits = needsUpdatedCredits;
        }
        @JsonGetter("needsUpdatedCredits")
        public boolean getNeedsUpdatedCredits() {
            return this.needsUpdatedCredits;
        }
        @JsonSetter("updatedCredits")
        public void setUpdatedCredits(Map<String, Integer> updatedCredits) {
            this.updatedCredits = updatedCredits;
        }
        @JsonGetter("updatedCredits")
        public Map<String, Integer> getUpdatedCredits() {
            return this.updatedCredits;
        }
    // ==================================================== //


    // ======== CLIENT BOARD STATE GETTERS/SETTERS ======== //
        // ==== MAIN FLAG ==== //
        @JsonSetter("needsBoardUpdate")
        public void setNeedsBoardUpdate(boolean needsBoardUpdate) {
            this.needsBoardUpdate = needsBoardUpdate;
        }
        @JsonGetter("needsBoardUpdate")
        public boolean getNeedsBoardUpdate() {
            return this.needsBoardUpdate;
        }

        // ==== POSITIONS ==== //
        @JsonSetter("needsUpdatedPositions")
        public void setNeedsUpdatedPositions(boolean needsUpdatedPositions) {
            this.needsUpdatedPositions = needsUpdatedPositions;
        }
        @JsonGetter("needsUpdatedPositions")
        public boolean getNeedsUpdatedPositions() {
            return this.needsUpdatedPositions;
        }
        @JsonSetter("updatedPositions")
        public void setUpdatedPositions(Map<String, Integer> updatedPositions) {
            this.updatedPositions = updatedPositions;
        }
        @JsonGetter("updatedPositions")
        public Map<String, Integer> getUpdatedPositions() {
            return this.updatedPositions;
    }

        // ==== ELIMINATED PLAYERS ==== //
        @JsonSetter("needsEliminatedPlayers")
        public void setNeedsUpdatedEliminatedPlayers(boolean needsUpdatedEliminatedPlayers) {
            this.needsUpdatedEliminatedPlayers = needsUpdatedEliminatedPlayers;
        }
        @JsonGetter("needsEliminatedPlayers")
        public boolean getNeedsUpdatedEliminatedPlayers() {
            return this.needsUpdatedEliminatedPlayers;
        }
        @JsonSetter("eliminatedPlayers")
        public void setEliminatedPlayers(List<String> eliminatedPlayers) {
            this.eliminatedPlayers = eliminatedPlayers;
        }
        @JsonGetter("eliminatedPlayers")
        public List<String> getEliminatedPlayers() {
            return this.eliminatedPlayers;
        }
    // =====================================================//


    // ======== CLIENT SHIP STATE GETTERS/SETTERS ======== //
        // ==== MAIN FLAG ==== //
        @JsonSetter("needsShipsUpdate")
        public void setNeedsShipUpdate(boolean needsShipUpdate) {
            this.needsShipUpdate = needsShipUpdate;
        }
        @JsonGetter("needsShipsUpdate")
        public boolean getNeedsShipUpdate() {
            return this.needsShipUpdate;
        }
        // ==== DROPPED RESOURCES ==== //
        @JsonSetter("needsUpdatedDroppedResources")
        public void setNeedsUpdatedDroppedResources(boolean needsUpdatedDroppedResources) {
            this.needsUpdatedDroppedResources = needsUpdatedDroppedResources;
        }
        @JsonGetter("updatedDroppedResources")
        public boolean getNeedsUpdatedDroppedResources() {
            return needsUpdatedDroppedResources;
        }
        @JsonSetter("droppedResources")
        public void setDroppedResources(Map<String, List<ComponentHelper<ItemColor>>> droppedResources) {
            this.droppedResources = droppedResources;
        }
        @JsonGetter("droppedResources")
        public Map<String, List<ComponentHelper<ItemColor>>> getDroppedResources() {
        return this.droppedResources;
    }

        // ==== TAKEN RESOURCES ==== //
        @JsonSetter("needsUpdatedTakenResources")
        public void setNeedsUpdatedTakenResources(boolean needsUpdatedTakenResources) {
            this.needsUpdatedTakenResources = needsUpdatedTakenResources;
        }
        @JsonGetter("needsUpdatedTakenResources")
        public boolean getNeedsUpdatedTakenResources() {
            return this.needsUpdatedTakenResources;
        }
        @JsonSetter("takenResources")
        public void setTakenResources(Map<String, List<ComponentHelper<ItemColor>>> takenResources) {
            this.takenResources = takenResources;
        }
        @JsonGetter("takenResources")
        public Map<String, List<ComponentHelper<ItemColor>>> getTakenResources() {
            return this.takenResources;
        }

        // ==== REMOVED LIFEFORMS ==== //
        @JsonSetter("needsUpdatedRemovedLifeforms")
        public void setNeedsUpdatedRemovedLifeforms(boolean needsUpdatedRemovedLifeforms) {
            this.needsUpdatedRemovedLifeforms = needsUpdatedRemovedLifeforms;
        }
        @JsonGetter("needsUpdatedRemovedLifeforms")
        public boolean getNeedsUpdatedRemovedLifeforms() {
            return this.needsUpdatedRemovedLifeforms;
        }
        @JsonSetter("removedLifeforms")
        public void setRemovedLifeforms(Map<String, List<ComponentHelper<LifeformType>>> removedLifeforms) {
            this.removedLifeforms = removedLifeforms;
        }
        @JsonGetter("removedLifeforms")
        public Map<String, List<ComponentHelper<LifeformType>>> getRemovedLifeforms() {
            return this.removedLifeforms;
        }

        // ==== REMOVED BATTERIES ==== //
        @JsonSetter("needsUpdatedBatteries")
        public void setNeedsUpdatedBatteries(boolean needsUpdatedBatteries) {
            this.needsUpdatedBatteries = needsUpdatedBatteries;
        }
        @JsonGetter("needsUpdatedBatteries")
        public boolean getNeedsUpdatedBatteries() {
            return this.needsUpdatedBatteries;
        }
        // ComponentHelper version
//        @JsonSetter("removedBatteries")
//        public void setRemovedBatteries(Map<String, List<ComponentHelper<Battery>>> removedBatteries) {
//            this.removedBatteries = removedBatteries;
//        }
//        @JsonSetter("removedBatteries")
//        public Map<String, List<ComponentHelper<Battery>>> getRemovedBatteries() {
//            return this.removedBatteries;
//        }
        // Integer version
        @JsonSetter("removedBatteries")
        public void setRemovedBatteries(Map<String, Integer> removedBatteries) {
            this.removedBatteries = removedBatteries;
        }
        @JsonGetter("removedBatteries")
        public Map<String, Integer> getRemovedBatteries() {
        return this.removedBatteries;
        }

    // ==================================================  //

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

    @JsonSetter("actionsAndConsequences")
    public void setActionsAndConsequences(Map<String, String> actionsAndConsequences) {
        this.actionsAndConsequences = actionsAndConsequences;
    }
    @JsonGetter("actionsAndConsequences")
    public Map<String, String> getActionsAndConsequences() {
        return this.actionsAndConsequences;
    }

    @JsonSetter("currActionIndex")
    public void setCurrActionIndex(int currActionIndex) {
        this.currActionIndex = currActionIndex;
    }
    @JsonGetter("currActionIndex")
    public int getCurrActionIndex() {
        return this.currActionIndex;
    }

    @JsonSetter("applyRequiredCrewConsequences")
    public void setApplyRequiredCrewConsequences(boolean applyRequiredCrewConsequences) {
        this.applyRequiredCrewConsequence = applyRequiredCrewConsequences;
    }
    @JsonGetter("applyRequiredCrewConsequence")
    public boolean getApplyRequiredCrewConsequence() {
        return this.applyRequiredCrewConsequence;
    }

    @JsonSetter("applyMovementStepsConsequence")
    public void setApplyMovementStepsConsequence(boolean applyMovementStepsConsequence) {
        this.applyMovementStepsConsequence = applyMovementStepsConsequence;
    }
    @JsonGetter("applyMovementStepsConsequence")
    public boolean getApplyMovementStepsConsequence() {
        return this.applyMovementStepsConsequence;
    }

    @JsonSetter("applyShootingSequenceConsequence")
    public void setApplyShootingSequenceConsequence(boolean applyShootingSequenceConsequence) {
        this.applyShootingSequenceConsequence = applyShootingSequenceConsequence;
    }
    @JsonGetter("applyShootingSequenceConsequence")
    public boolean getApplyShootingSequenceConsequence() {
        return this.applyShootingSequenceConsequence;
    }

    @JsonSetter("applyLossItemsConsequence")
    public void setApplyLossItemsConsequence(boolean applyLossItemsConsequence) {
        this.applyLossItemsConsequence = applyLossItemsConsequence;
    }
    @JsonGetter("applyLossItemsConsequence")
    public boolean getApplyLossItemsConsequence() {
        return this.applyLossItemsConsequence;
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