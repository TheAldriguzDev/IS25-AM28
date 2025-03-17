package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;

import it.polimi.ingsw.is25am28.Player.Player;
import javafx.util.Pair;
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
            JSONObject cannons
            // String imagePath
    ) {
        super(cardName, cardLevel);
        // this.imagePath = imagePath;

        // Initializing the direction name to value map
        this.directionNameToValue = new HashMap<Integer, String>();
        this.directionNameToValue.put(0, "top");
        this.directionNameToValue.put(1, "right");
        this.directionNameToValue.put(2, "bottom");
        this.directionNameToValue.put(3, "left");

        // Variables
        JSONArray plasmaShotSequenceJSON;
        int totalDirections = directionNameToValue.size();

        // (1) - Initializing the conditions for the player with the lowest crew
        this.takenCrewForLowestCrew = (int) humans.get("humans");
        this.takenStorageForLowestCrew = (int) humans.get("storage");
        this.movementStepsForLowestCrew = (int) humans.get("days");
        this.shootingSequenceForLowestCrew = new ArrayList<PlasmaShot>();

        plasmaShotSequenceJSON = (JSONArray) humans.get("shoot");

        for (Object plasmaShot : plasmaShotSequenceJSON) {
            JSONArray plasmaShotDescriptor = (JSONArray) plasmaShot;

            shootingSequenceForLowestCrew.add(
                new PlasmaShot(
                    (int) plasmaShotDescriptor.get(0),
                    (int) plasmaShotDescriptor.get(1)
                )
            );
        }

        // (2) - Initializing the conditions for the player with the lowest engine power
        this.takenCrewForLowestEnginePower = (int) engines.get("humans");
        this.takenStorageForLowestEnginePower = (int) engines.get("storage");
        this.movementStepsForLowestEnginePower = (int) engines.get("days");
        this.shootingSequenceForLowestEnginePower = new ArrayList<PlasmaShot>();

        plasmaShotSequenceJSON = (JSONArray) engines.get("shoot");

        for (Object plasmaShot : plasmaShotSequenceJSON) {
            JSONArray plasmaShotDescriptor = (JSONArray) plasmaShot;

            shootingSequenceForLowestEnginePower.add(
                new PlasmaShot(
                    (int) plasmaShotDescriptor.get(0),
                    (int) plasmaShotDescriptor.get(1)
                )
            );
        }

        // (3) - Initializing the conditions for the player with the lowest firepower
        this.takenCrewForLowestFirepower = (int) cannons.get("humans");
        this.takenStorageForLowestFirepower = (int) cannons.get("storage");
        this.movementStepsForLowestFirepower = (int) cannons.get("days");
        this.shootingSequenceForLowestFirepower = new ArrayList<PlasmaShot>();

        plasmaShotSequenceJSON = (JSONArray) cannons.get("shoot");

        for (Object plasmaShot : plasmaShotSequenceJSON) {
            JSONArray plasmaShotDescriptor = (JSONArray) plasmaShot;

            shootingSequenceForLowestFirepower.add(
                new PlasmaShot(
                    (int) plasmaShotDescriptor.get(0),
                    (int) plasmaShotDescriptor.get(1)
                )
            );
        }
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        return null;
    }

    public void useCard(WarZoneJSON data) throws IllegalArgumentException {
        List<Player> players = this.getBoard().getPlayers();
        Map<Player, List<Float>> shipStatsPerPlayer = new HashMap<>();
        Map<Player, List<Integer>> shieldedSidesPerPlayer = new HashMap<>();
        List<Integer> currPlayerShieldedSides;
        List<Float> currPlayerShipStats;
        int batteriesForEngines;
        int batteriesForCannons;
        int batteriesForShields, totalShieldsToActivate;
        JSONArray shieldsToActivate;

        // Elaborating each player's chosen config to handle the WarZone card
        for (Player player : players) {
            JSONObject currPlayerChoices = (JSONObject) data.getData().get(player.getNickname());
            batteriesForEngines = (int) currPlayerChoices.get("engines");
            batteriesForCannons = (int) currPlayerChoices.get("cannons");
            shieldsToActivate = (JSONArray) currPlayerChoices.get("shieldsToActivate");

            // (1) - Increasing the ship's stats and setting the shielding patter for the duration of the card

            // (1.1) - Creating the list describing the current player's ship stats, which are
            //      1 - totalFirepower (including the activated double cannons)
            //      2 - totalEnginePower (including the activated double engines)
            //      3 - totalCrew
            currPlayerShipStats = new ArrayList<Float>(3);

            // (1.2) - Adding the totalFirepower
            //       + Consuming the batteries required to activate the requested double cannons
            currPlayerShipStats.add(0, player.getShip().getFirePower(batteriesForCannons));

            // (1.3) - Adding the totalEnginePower
            //       + Consuming the batteries required to activate the requested double engines
            currPlayerShipStats.add(1, (float) player.getShip().getEnginePower(batteriesForEngines));

            // (1.4) - Adding the totalCrew
            currPlayerShipStats.add(2, (float) player.getShip().getAllLifeforms().size());

            // (1.5) - Adding that list to the map of all configs
            shipStatsPerPlayer.put(player, currPlayerShipStats);

            // (2) - Activating the requested shields

            // (2.1) - Getting the amount of batteries needed to protect the desired side
            batteriesForShields = shieldsToActivate.size();
            totalShieldsToActivate = shieldsToActivate.size();
            currPlayerShieldedSides = new ArrayList<Integer>(4);

            // Getting the activated shielding pattern as requested by the current player
            for (int i = 0; i < totalShieldsToActivate; i++) {
                JSONArray coordinates = (JSONArray) shieldsToActivate.get(i);
                currPlayerShieldedSides.set((int) coordinates.get(0), 1);
                currPlayerShieldedSides.set((int) coordinates.get(1), 1);
            }

            // (2.2) - Consuming the energy required to activate the selected shields
            player.getShip().consumeEnergy(batteriesForShields);

            // (2.3) - Adding the activated shielding pattern to the map
            shieldedSidesPerPlayer.put(player, currPlayerShieldedSides);
        }

        // (3) - Once all the players have tailored their ships, next is to
        //       determine to which category does each player belong.
        //     - Finding the players that have:
        //          1 - lowestFirepower
        //          2 - lowestEnginePower
        //          3 - lowestCrew
        // NOTE: There's the possibility that, for example, two players have the lowest crew
        //       count, thus the relative condition will be applied to both
        List<Player> lowestFirepowerPlayers = new ArrayList<>();
        List<Player> lowestEnginePowerPlayers = new ArrayList<>();
        List<Player> lowestCrewPlayers = new ArrayList<>();
        int playerCount = players.size();
        int i, j;
        float tmp1, tmp2;

        // (3.1) - Adding the players with the lowest firepower to the lowestFirePower list
        for (i = 0; i < playerCount; i++) {
            for (j = 0; j < playerCount; j++) {
                if (i != j) {
                    tmp1 = shipStatsPerPlayer.get(players.get(i)).get(0);
                    tmp2 = shipStatsPerPlayer.get(players.get(j)).get(0);

                    if (tmp1 < tmp2) {
                        lowestCrewPlayers.add(players.get(i));
                    } else if (tmp1 == tmp2) {
                        lowestFirepowerPlayers.add(players.get(i));
                        lowestFirepowerPlayers.add(players.get(j));
                    }
                }
            }
        }

        // (3.2) - Adding the players with the lowest engine power to the lowestEnginePower list
        for (i = 0; i < playerCount; i++) {
            for (j = 0; j < playerCount; j++) {
                if (i != j) {
                    tmp1 = shipStatsPerPlayer.get(players.get(i)).get(1);
                    tmp2 = shipStatsPerPlayer.get(players.get(j)).get(1);

                    if (tmp1 < tmp2) {
                        lowestEnginePowerPlayers.add(players.get(i));
                    } else if (tmp1 == tmp2) {
                        lowestEnginePowerPlayers.add(players.get(i));
                        lowestEnginePowerPlayers.add(players.get(j));
                    }
                }
            }
        }

        // (3.3) - Adding the players with the lowest crew to the lowestCrewPlayer list
        for (i = 0; i < playerCount; i++) {
            for (j = 0; j < playerCount; j++) {
                if (i != j) {
                    tmp1 = shipStatsPerPlayer.get(players.get(i)).get(2);
                    tmp2 = shipStatsPerPlayer.get(players.get(j)).get(2);

                    if (tmp1 < tmp2) {
                        lowestCrewPlayers.add(players.get(i));
                    } else if (tmp1 == tmp2) {
                        lowestCrewPlayers.add(players.get(i));
                        lowestCrewPlayers.add(players.get(j));
                    }
                }
            }
        }

        // (4) - Once the player categories are established, the only thing remaining
        //       is to apply the conditions to each player for each category

        // (4.1) - Applying lowestFirepower war zone conditions to the respective players



        // (4.2) - Applying lowestEnginePower war zone conditions to the respective players
        // (4.3) - Applying lowestCrew war zone conditions to the respective players
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}
