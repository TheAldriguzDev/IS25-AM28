package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class WarZone extends EventCard {
    // Precalculated table that associates each direction name to its value
    private final Map<Integer, String> directionNameToValue;

    // Lowest crew conditions
    private final int takenCrewForLowestCrew;
    private final int takenStorageForLowestCrew;
    private final int movementStepsForLowestCrew;
    private final List<PlasmaShot> shootingSequenceForLowestCrew;

    // Lowest engine power conditions
    private final int takenCrewForLowestEnginePower;
    private final int takenStorageForLowestEnginePower;
    private final int movementStepsForLowestEnginePower;
    private final List<PlasmaShot> shootingSequenceForLowestEnginePower;

    // Lowest firepower conditions
    private final int takenCrewForLowestFirepower;
    private final int takenStorageForLowestFirepower;
    private final int movementStepsForLowestFirepower;
    private final List<PlasmaShot> shootingSequenceForLowestFirepower;

    // private final String imagePath;

    public WarZone(
            String cardName,
            int cardLevel,
            JSONObject humans,
            JSONObject engines,
            JSONObject cannons,
            Board board
            // String imagePath
    ) {
        super(cardName, cardLevel, board);
        // this.imagePath = imagePath;

        // Initializing the direction name to value map
        this.directionNameToValue = new HashMap<Integer, String>();
        this.directionNameToValue.put(0, "top");
        this.directionNameToValue.put(1, "right");
        this.directionNameToValue.put(2, "bottom");
        this.directionNameToValue.put(3, "left");

        // Variables
        JSONObject shootingSequenceJSON;
        JSONArray directionSequence;
        int totalDirections = directionNameToValue.size();

        // (1) - Initializing the conditions for the player with the lowest crew
        this.takenCrewForLowestCrew = (int) humans.get("humans");
        this.takenStorageForLowestCrew = (int) humans.get("storage");
        this.movementStepsForLowestCrew = (int) humans.get("days");
        this.shootingSequenceForLowestCrew = new ArrayList<PlasmaShot>();

        shootingSequenceJSON = (JSONObject) humans.get("shoot");

        for (int i = 0; i < totalDirections; i++) {
            directionSequence = (JSONArray) shootingSequenceJSON.get(directionNameToValue.get(i));
            for (Object sizeIndicator : directionSequence) {
                shootingSequenceForLowestCrew.add(new PlasmaShot((Integer) sizeIndicator, i));
            }
        }

        // (2) - Initializing the conditions for the player with the lowest engine power
        this.takenCrewForLowestEnginePower = (int) engines.get("humans");
        this.takenStorageForLowestEnginePower = (int) engines.get("storage");
        this.movementStepsForLowestEnginePower = (int) engines.get("days");
        this.shootingSequenceForLowestEnginePower = new ArrayList<PlasmaShot>();

        shootingSequenceJSON = (JSONObject) humans.get("shoot");

        for (int i = 0; i < totalDirections; i++) {
            directionSequence = (JSONArray) shootingSequenceJSON.get(directionNameToValue.get(i));
            for (Object sizeIndicator : directionSequence) {
                shootingSequenceForLowestEnginePower.add(new PlasmaShot((Integer) sizeIndicator, i));
            }
        }

        // (3) - Initializing the conditions for the player with the lowest firepower
        this.takenCrewForLowestFirepower = (int) cannons.get("humans");
        this.takenStorageForLowestFirepower = (int) cannons.get("storage");
        this.movementStepsForLowestFirepower = (int) cannons.get("days");
        this.shootingSequenceForLowestFirepower = new ArrayList<PlasmaShot>();

        shootingSequenceJSON = (JSONObject) humans.get("shoot");

        for (int i = 0; i < totalDirections; i++) {
            directionSequence = (JSONArray) shootingSequenceJSON.get(directionNameToValue.get(i));
            for (Object sizeIndicator : directionSequence) {
                shootingSequenceForLowestFirepower.add(new PlasmaShot((Integer) sizeIndicator, i));
            }
        }
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public CardStateJSON generateState() {
        return null;
    }
}
