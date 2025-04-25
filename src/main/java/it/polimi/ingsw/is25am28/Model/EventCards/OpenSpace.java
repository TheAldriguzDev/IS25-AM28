package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Battery;
import it.polimi.ingsw.is25am28.Model.Components.Engine;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import javafx.util.Pair;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpace extends EventCard {
    private Map<String, Integer> playerPowerResult;
    private Map<String, Integer> updatedPositions;
    private List<String> eliminatedPlayers;
    // TODO : modify the system to update from time to time, not at the end

    // TODO: Implement the specific constructor to build the card with the necessary data
    public OpenSpace(String name, int level, Board board) {
        super(name, level, board);
        this.playerPowerResult = new HashMap<>();
        this.updatedPositions = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
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
        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        OpenSpaceJSON openSpace;

        try {
            openSpace = (OpenSpaceJSON) data;
        } catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid OpenSpace JSON");
        }

        // Retrieve the data from the JSON
        String playerNickname = openSpace.getPlayerNickname();
        int usedEnergy = openSpace.getUsedEnergy();

        // Count the double engines of the used
        long availableDoubleEngines = this.getCurrentPlayer().get().getShip().getEngineList().stream().filter(Engine::requireEnergy).count();

        // Check if:
        // 1: The player match
        // 2: The player has used an amount of available energy
        if ( playerNickname != null &&
                !playerNickname.isEmpty() &&
                playerNickname.equals( this.getCurrentPlayer().get().getNickname() ) &&
                usedEnergy <= this.getCurrentPlayer().get().getShip().getAvailableEnergy() ) {

            // Calculate the ship engines power with:
            // +1 for every normal motor
            // +2 for every double motor activated
            // +2 for every alien that boost the engine power

            Ship ship = this.getCurrentPlayer().get().getShip();
            int totalPower = 0;

            // Power given by the engine
            totalPower += ship.getEngineList().stream()
                            .filter( e -> !e.requireEnergy())
                            .mapToInt(Engine::getSpeed)
                            .sum();

            // Power given by the double engines
            // While we have energy and doubleMotors then we can update the totalPower
            while ( usedEnergy > 0 && availableDoubleEngines > 0 ) {
                totalPower += 2;

                usedEnergy--;
                availableDoubleEngines--;
                ship.consumeEnergy(1);
            }

            // Power given by the alien:
            // If the power given by the engines is 0 than we cannot apply the boost given by the alien
            totalPower += totalPower == 0 ? 0 : ship.getCabinList()
                    .stream()
                    .flatMap( c -> c.getInhabitants().stream() )
                    .mapToInt( Lifeform::getPowerBoost )
                    .sum();

            // Store the power result to notify the player with the choices of the previous players
            this.playerPowerResult.put(playerNickname, totalPower);

            // Apply the effect to the player
            // if no power has been declared eliminate the player
            // otherwise move the player forward of the declared power
            if (totalPower == 0) {
                this.getBoard().eliminatePlayer(this.getCurrentPlayer().get());
                this.eliminatedPlayers.add(playerNickname);
            } else {
                this.getBoard().movePlayerForward(this.getCurrentPlayer().get(), totalPower);
                this.updatedPositions.put(playerNickname, this.getCurrentPlayer().get().getCursor());
            }

            // When we have moved the last player we need to re-validate the positions
            if (this.getCurrentPlayer().get().equals(this.players.getLast())) {
                this.cardUsed(); // Mark the card as used
                this.getBoard().validatePlayersPosition();
            } else {
                this.getNextPlayer();
            }
        } else {
            if ( usedEnergy > this.getCurrentPlayer().get().getShip().getAvailableEnergy() ) {
                throw new IllegalArgumentException("There player does not have enough energy to perform the action!");
            } else {
                throw new IllegalArgumentException("The given player does not match with the current one!");
            }
        }

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.cardLevel);
        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        cardState.setPlayersEnginePower(this.playerPowerResult);

        // If the card is finished we send all the effective changes:
        // 1. The players updated available energies
        // 2. The updated board with the new players positions
        if (this.hasFinished()) {
            Map<String, Integer> playersBatteries = new HashMap<>();

            for (Player p : this.players) {
                playersBatteries.put(p.getNickname(), p.getShip().getBatteryList().stream().mapToInt(Battery::getAvailability).sum());
            }

            cardState.setPlayersBatteries(playersBatteries);
            //cardState.setBoard(this.getBoard().generateState());
            cardState.setEliminatedPlayers(this.eliminatedPlayers);
            cardState.setUpdatedPositions(this.updatedPositions);
        }

        return cardState;
    }

    @Override
    public WidgetTUI generateWidget(CardStateJSON openSpaceJSON) {
        return null;
    }
}