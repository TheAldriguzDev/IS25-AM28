package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Engine;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class OpenSpace extends EventCard {
    private Map<Player, Integer> playerPowerResult;


    // TODO: Implement the specific constructor to build the card with the necessary data
    public OpenSpace(String name, int level, Board board) {
        super(name, level, board);
        this.playerPowerResult = new HashMap<>();
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
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        if (this.getCurrentPlayer().isPresent()) {

            long availableDoubleEngines = this.getCurrentPlayer().get().getShip().getEngineList().stream().filter(Engine::requireEnergy).count();

            try {
                OpenSpaceJSON openSpace = (OpenSpaceJSON) data;

                // Retrieve the data from the JSON --> if an error occurs we throw the exception to the caller
                String playerNickname = openSpace.getPlayerNickname();
                int usedEnergy = openSpace.getPlayerUsedEnergy();

                // Check if:
                // 1: The player match
                // 2: The player has enough energy to use
                // 3: The player has enough engine to use the required energy
                if ( playerNickname != null
                        && !playerNickname.isEmpty()
                        && !playerNickname.equals( this.getCurrentPlayer().get().getNickname())
                        && usedEnergy > this.getCurrentPlayer().get().getShip().getAvailableEnergy()
                        && usedEnergy > availableDoubleEngines ) {

                    // Compute the ship engine power with:
                    // +1 for every normal motor X
                    // +2 for every double motor activated
                    // +2 for every alient that give the boost engine power X

                    Ship ship = this.getCurrentPlayer().get().getShip();

                    int totalPower = 0;

                    totalPower += ship.getCabinList()
                            .stream()
                            .flatMap( c -> c.getInhabitants().stream() )
                            .mapToInt( Lifeform::getPowerBoost )
                            .sum();

                    totalPower += ship.getEngineList().stream()
                            .filter( e -> !e.requireEnergy())
                            .mapToInt(Engine::getSpeed)
                            .sum();

                    totalPower +=  usedEnergy * 2;

                    // Apply the effect to the player
                    // If no power has been declared --> eliminate the player
                    // Otherwise move the player forward of the declared power
                    if (totalPower == 0) {
                        this.getBoard().eliminatePlayer(this.getCurrentPlayer().get());
                    } else {
                        this.getBoard().movePlayerForward(this.getCurrentPlayer().get(), totalPower);

                        // TODO: Remove the player used battery
                    }

                    // If all the players have played, then we need to revalidate their positions
                    if (this.hasFinished()) {
                        this.getBoard().validatePlayersPosition();
                    } else {
                        getNextPlayer();
                    }
                } else {
                    if ( usedEnergy > this.getCurrentPlayer().get().getShip().getAvailableEnergy() ) {
                        throw new IllegalArgumentException("The player does not have enough energy to use");
                    } else if (usedEnergy > availableDoubleEngines) {
                        throw new IllegalArgumentException("The given player does not have the required engines");
                    } else {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while parsing the user requested action: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        return this;
    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public JSONObject generateState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.getCardLevel());

        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        return cardState.getData();
    }
}