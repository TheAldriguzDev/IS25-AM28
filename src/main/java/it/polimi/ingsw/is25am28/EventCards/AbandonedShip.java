package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player;

public class AbandonedShip extends EventCard {
    private final int requireCrew;
    private final int movementStep;
    private final int givenCredits;

    public AbandonedShip(String name, int cardLevel, int requireCrew, int movementStep, int givenCredits) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.requireCrew = requireCrew;
        this.movementStep = movementStep;
        this.givenCredits = givenCredits;
    }

    public int getRequirementCrew() {
        return requireCrew;
    }

    public int getMovementStep() {
        return movementStep;
    }

    public int getGivenCredits() {
        return givenCredits;
    }

    protected void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getLifeForms() > requireCrew) {
                //method getChoice: ask player to make a choice
                if (getChoice()) {
                    bonusEffect(player);
                    malusEffect(player);
                    player.setCursor(player.getCursor() - this.movementStep);
                    break;
                }
            }
        }
    }

    protected void bonusEffect(Player player) {
        player.setCredits(player.getCredits() + this.givenCredits);
    }

    protected void malusEffect(Player player) {
        player.getShip().setLifeForms(player.getShip().getLifeForms() - this.requireCrew);
    }
}
