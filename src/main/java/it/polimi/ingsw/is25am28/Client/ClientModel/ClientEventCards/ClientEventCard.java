package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientEventCard implements WidgetTUIGenerator {

    protected final int id;
    protected String playerNickname;
    protected String cardName;
    protected int cardLevel;
    protected boolean hasBeenUsed;
    protected boolean hasBeenActivated; // this flag allows the card to send its full static information (like when only visualized at the start of the game) only when ita has not been used a single time wit useCard()

    protected ClientModel model;
    protected InputThread inputThread;

    public ClientEventCard(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        this.model = model;
        this.inputThread = inputThread;
        this.id = cardState.getId();
        this.cardName = cardState.getCardName();
        this.cardLevel = cardState.getCardLevel();
    }

    public abstract ActionJSON useCard();

    public int getId() {
        return this.id;
    }

    /**
     * This method is in charge of updating the card's data as the round goes on
     */
    public abstract void updateCard(CardStateJSON cardState);

    /**
     * @return The client event card's widget containing
     *         all the relevant information
     */
    public abstract WidgetTUI generateWidget();
    // TODO: Place the current/target player in a separated bordered widget

    // ======== Input From Player ======== //

    // LIFEFORMS
    protected List<ComponentHelper<LifeformType>> inputLifeformsToBeRemoved() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputLifeformsToBeRemoved()' is not supported in " + this + " state");
    }

    protected boolean inputWantsToVisitStation() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputWantsToVisitStation()' is not supported in " + this + " state");
    }

    // ITEMS
    protected List<ComponentHelper<ItemColor>> inputItemsToBeRemoved() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputItemsToBeRemoved()' is not supported in " + this + " state");
    }

    protected List<ComponentHelper<ItemColor>> inputItemsToBeTaken() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputItemsToBeTaken()' is not supported in " + this + " state");
    }

    protected ArrayList<ComponentHelper<LifeformType>> inputCrewToRemove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputCrewToRemove()' is not supported in " + this + " state");
    }

    // VARIOUS FLAGS AND INDEXES
    protected boolean inputTakeLoot() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputTakeLoot()' is not supported in " + this + " state");
    }

    protected int inputChosenPlanetIndex() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputChosenPlanetIndex()' is not supported in " + this + " state");
    }

    protected boolean inputTakeCredits() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputTakeCredits()' is not supported in " + this + " state");
    }

    protected boolean inputWantsToVisitShip() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputWantsToVisitShip()' is not supported in " + this + " state");
    }

    // SHIELDS
    protected List<List<Integer>> inputShieldsCoordinates() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputShieldsCoordinates()' is not supported in " + this + " state");
    }

    protected List<ComponentHelper<Integer>> inputShieldList() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputShieldList()' is not supported in " + this + " state");
    }

    protected ArrayList<int []> inputShieldsActivatedCoordinates() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputShieldsActivatedCoordinates()' is not supported in " + this + " state");
    }

    // CANNONS
    protected List<ComponentHelper<Integer>> inputCannonList() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputCannonList()' is not supported in " + this + " state");
    }

    protected List<List<Integer>> inputDoubleCannonsToActivateCoordinates() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputDoubleCannonsToActivateCoordinates()' is not supported in " + this + " state");
    }

    // ENGINES
    protected int inputUsedEnergy() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'inputUsedEnergy()' is not supported in " + this + " state");
    }

    // ACK METHOD
    protected void inputAck() {
        System.out.print("Press any key and then press [ENTER] to continue...");

        try {
            this.inputThread.waitForInput();
        }
        catch (InterruptedException e) {
            // A forced interrupt arrived
        }
    }
}
