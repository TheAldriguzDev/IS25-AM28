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
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setCrewToRemove()' is not supported in " + this + " state");
    }

    // ITEMS
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setItemsToBeRemoved()' is not supported in " + this + " state");
    }

    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setItemsToBeTaken()' is not supported in " + this + " state");
    }

    // VARIOUS FLAGS AND INDEXES
    public void setTakeReward(boolean takeReward) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setTakeReward()' is not supported in " + this + " state");
    }

    public void setChosenPlanetIndex(int chosenPlanetIndex) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setChosenPlanetIndex()' is not supported in " + this + " state");
    }

    public void setWantsToVisitShip(boolean wantsToVisitShip) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setWantsToVisitShip()' is not supported in " + this + " state");
    }

    public void setWantsToVisitStation(boolean wantsToVisitStation) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setWantsToVisitStation()' is not supported in " + this + " state");
    }

    // SHIELDS
    public void setShieldsToActivate(List<ComponentHelper<Integer>> shieldsToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setShieldsToActivate()' is not supported in " + this + " state");
    }

    // CANNONS
    public void setDoubleCannonsToActivate(List<ComponentHelper<Integer>> doubleCannonsToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setDoubleCannonsToActivate()' is not supported in " + this + " state");
    }

    // ENGINES
    public void setUsedEnergy(int usedEnergy) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setUsedEnergy()' is not supported in " + this + " state");
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
