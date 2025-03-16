package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

public class Slavers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;

    public Slavers(String name, int cardLevel, int requiredFirepower, int movementSteps, int givenCredits, int takenCrew, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.givenCredits = givenCredits;
        this.takenCrew = takenCrew;
    }

    public void useCard(Player[] players) {
        for (Player player : players) {
            if (player.getShip().getFirePower() >= requiredFirepower) {
                if(/* getChoice()*/ false) {
                    bonusEffect(player);
                    player.setCursor(player.getCursor() - this.movementSteps);
                }
                break;
            }
            malusEffect(player);
        }
    }

    protected void bonusEffect(Player player) {
        player.setCredits(player.getCredits() + this.givenCredits);
    }

    protected void malusEffect(Player player) {
        // TODO: Needs to be rewritten
        // player.getShip().setLifeforms(player.getShip().getLifeforms() - this.takenCrew);
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
    public JSONObject generateState() {
        return null;
    }
}
