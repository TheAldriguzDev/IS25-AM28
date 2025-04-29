package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.HashMap;
import java.util.Map;

public class ClientWarZone extends ClientEventCard {
    private Map<String, String> actionAndConsequences;
    private final int requiredCrew;
    private final int movementSteps;
    private final int requiredResources;

    public ClientWarZone(CardStateJSON cardState) {
        super(cardState);
        this.actionAndConsequences = cardState.getActionsAndConsequences();
        this.requiredCrew = cardState.getRequiredResources();
        this.movementSteps = cardState.getMovementSteps();
        this.requiredResources = cardState.getRequiredResources();
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        if (cardState.getAffectedPlayer().isEmpty()) {

        }
        if (cardState.getApplyRequiredCrewConsequence()) {

        } else if (cardState.getApplyMovementStepsConsequence()) {

        } else if (cardState.getApplyShootingSequenceConsequence()) {

        } else if (cardState.getApplyLossItemsConsequence()) {

        }
    }

    @Override
    public WidgetTUI generateWidget() {
        return null;
    }
}
