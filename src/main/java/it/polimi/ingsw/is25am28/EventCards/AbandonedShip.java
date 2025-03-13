package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;

public class AbandonedShip extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private final int givenCredits;

    public AbandonedShip(String name, int cardLevel, int requireCrew, int movementStep, int givenCredits) {
        super(name, cardLevel);
        this.requiredCrew = requireCrew;
        this.movementStep = movementStep;
        this.givenCredits = givenCredits;
    }
    
    public void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getAllLifeforms().stream().count() > requiredCrew) {
                //method getChoice: ask player to make a choice
                if (/* getChoice()*/ false) {
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
        // TODO: Needs to be rewritten
        // player.getShip().setLifeForms(player.getShip().getLifeforms() - this.requiredCrew);
    }
}
