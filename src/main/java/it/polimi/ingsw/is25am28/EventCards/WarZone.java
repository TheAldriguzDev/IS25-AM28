package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class WarZone extends EventCard {
    private final int movementStepsForLowestCrew;
    private final int takenCrewForLowestEnginePower;
    private List<PlasmaShot> shootingSequenceForLowestFirePower = new ArrayList<>();

    public WarZone(String name, int cardLevel, int movementStepsForLowestCrew, int takenCrewForLowestEnginePower) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.movementStepsForLowestCrew = movementStepsForLowestCrew;
        this.takenCrewForLowestEnginePower = takenCrewForLowestEnginePower;
    }

    public int getMovementSteps() {
        return movementStepsForLowestCrew;
    }

    public int getTakenCrewAmount() {
        return takenCrewForLowestEnginePower;
    }

    public List<PlasmaShot> getShootingSequence() {
        return shootingSequenceForLowestFirePower;
    }

    public void useCard(Player[] players) {
        Player LowestCrewPlayer = players[0];
        Player LowestFirePowerPlayer =  players[0];
        Player LowestEnginePowerPlayer = players[0];
        for (Player player : players) {
            if ((long) player.getShip().getAllLifeforms().size() < (long) LowestCrewPlayer.getShip().getAllLifeforms().size()) {
                LowestCrewPlayer = player;
            }
            if (player.getShip().getFirePower() < LowestFirePowerPlayer.getShip().getFirePower()) {
                LowestFirePowerPlayer = player;
            }
            if (player.getShip().getEnginePower() < LowestEnginePowerPlayer.getShip().getEnginePower()) {
                LowestEnginePowerPlayer = player;
            }
        }
        for (Player player : players) {
            malusEffect(player, LowestCrewPlayer, LowestFirePowerPlayer, LowestEnginePowerPlayer);
        }
    }

    @Override
    void bonusEffect(Player player) {

    }

    @Override
    void malusEffect(Player player) {

    }

    protected void malusEffect(Player player, Player LowestCrewPlayer, Player LowestFirePowerPlayer, Player LowestEnginePowerPlayer) {
        if (player == LowestCrewPlayer) {
            player.setCursor(player.getCursor() - movementStepsForLowestCrew);
        }
        if (player == LowestFirePowerPlayer) {
            // Exposed to shooting sequence
        }
        if (player == LowestEnginePowerPlayer) {
            // TODO: Needs to be rewritten
            // player.getShip().setLifeForms(player.getShip().getLifeForms() - takenCrewForLowestEnginePower);
        }
    }
}
