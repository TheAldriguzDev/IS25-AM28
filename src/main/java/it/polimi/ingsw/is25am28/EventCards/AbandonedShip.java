package it.polimi.ingsw.is25am28.EventCards;

public class AbandonedShip extends EventCard {
    private final int requireCrew;
    private final int movementStep;
    private final int givenCredits;

    public AbandonedShip(String name, int cardLevel, int requireCrew, int movementStep, int givenCredits) {
        super(name, cardLevel);
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

    }

    protected void bonusEffect() {

    }

    protected void malusEffect() {

    }
}
