package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OpenSpace extends EventCard {
    private Map<Player, Integer> playerChoice;


    // TODO: Implement the specific constructor to build the card with the necessary data
    public OpenSpace(String name, int level) {
        super(name, level);
        this.playerChoice = new HashMap<>();
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }


    // Power dei motori normali la posso pre-calcolare per ogni player
    // Mi devono inviare quanti segnalini batteria vogliono utilizzare per poter accendere i motori doppi
    // Calcolo il valore finale di potenza che hanno
    // Se è pari a 0 il player è eliminato, altrimenti lo sposto di X posizioni (alla fine, quando tutti i player hanno risposto cosa fare)

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        // Try-catch needed to prevent crash for bad params type
        try {
            // Get the data that are needed to compute the request
            String playerName = (String)data.get("playerNickname");
            Integer requestedDoubleMotors = (Integer) data.get("requestedDoubleMotors");

            // Check if the data is correct before computing it
            if (playerName != null && requestedDoubleMotors != null && currentPlayer.isPresent() && playerName.equals(currentPlayer.get().getNickname()) && requestedDoubleMotors >= 0) {

                // Get the normal motor
                // Get the boosted motor

                // Compute the final power with the correct informations


            } else {
                throw new IllegalArgumentException("The input data is not valid");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while parsing the user requested action: " + e.getMessage());
        }

        return null;
    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}