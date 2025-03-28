package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;

import java.util.List;

public class WarZone extends EventCard {
    private final int movementSteps;
    private final int requiredCrew;
    private final List<PlasmaShot> shootingSequence;
    private final List<WarZoneActionConsequencePair> cardActions;
    private int current_action;

    /**
     * WarZone constructor that sets:
     * - General information about the card (name, level, board)
     * - Specific information about the specs of the card (movementSteps, requiredCrew, shootingSequence)
     * - The order of the action of the card
     * */
    public WarZone(
            String name,
            int level,
            Board board,
            int movementSteps,
            int requiredCrew,
            List<PlasmaShot> shootingSequence,
            List<WarZoneActionConsequencePair> cardActions
    ) {
        super(name, level, board);
        this.movementSteps = movementSteps;
        this.requiredCrew = requiredCrew;
        this.shootingSequence = shootingSequence;
        this.cardActions = cardActions;
        this.current_action = 0;
    }

    /**
     * Methods that needs to handle the user interaction, more precisely it handles the different type of action
     * */
    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {



        return null;
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public CardStateJSON generateState() {
        return null;
    }
}
