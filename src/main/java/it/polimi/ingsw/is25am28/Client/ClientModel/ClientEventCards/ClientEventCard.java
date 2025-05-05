package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ClientEventCard implements WidgetTUIGenerator {
    protected static final List<String> enabledCommands;

    static {
        enabledCommands = new ArrayList<>();
    }

    protected final int id;
    protected String playerNickname;
    protected String cardName;
    protected int cardLevel;
    protected boolean hasBeenUsed;
    protected boolean hasBeenActivated; // this flag allows the card to send its full static information (like when only visualized at the start of the game) only when ita has not been used a single time wit useCard()

    protected ClientModel model;
    protected InputThread inputThread;

    public ClientEventCard(CardStateJSON cardState) {
        this.id = cardState.getId();
        this.cardName = cardState.getCardName();
        this.cardLevel = cardState.getCardLevel();
    }

    /**
     * Each card will set to TRUE only the input methods it needs inside
     * its own ActionJSON to provide the server the player's choices.
     */
    public static void setAvailableCommands(Map<String, Pair<Boolean, CommandWidgetTUI>> indexedCardInputMethods) {
        // First put to false all flags
        for (Map.Entry<String, Pair<Boolean, CommandWidgetTUI>> entry : indexedCardInputMethods.entrySet()) {
            entry.getValue().setKey(false);
        }

        // Then activate only the ones specified by the current event card
        for (String command : enabledCommands) {
            indexedCardInputMethods.get(command).setKey(true);
        }
    }

    /**
     * @return This client card's ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return An ActionJSON compiled with the user input, ready to be sent.
     */
    public abstract ActionJSON useCard();

    /**
     * This method is in charge of updating the card's data as the round goes on
     */
    public abstract void updateCard(CardStateJSON cardState);

    /**
     * @return The client event card's widget containing
     *         all the relevant information
     */
    public abstract WidgetTUI generateWidget();

    // ======== Players' ActionJSON Compilation Methods ======== //

    // LIFEFORMS
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setCrewToRemove()' is not supported in " + this + " state");
    }

    public List<ComponentHelper<LifeformType>> getCrewToRemove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getCrewToRemove()' is not supported in " + this + " state");
    }

    // ITEMS
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setItemsToBeRemoved()' is not supported in " + this + " state");
    }

    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getItemsToBeRemoved()' is not supported in " + this + " state");
    }

    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setItemsToBeTaken()' is not supported in " + this + " state");
    }

    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getItemsToBeTaken()' is not supported in " + this + " state");
    }

    // VARIOUS FLAGS AND INDEXES
    public void setTakeReward(boolean takeReward) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setTakeReward()' is not supported in " + this + " state");
    }

    public boolean getTakeReward() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getTakeReward()' is not supported in " + this + " state");
    }

    public void setChosenPlanetIndex(int chosenPlanetIndex) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setChosenPlanetIndex()' is not supported in " + this + " state");
    }

    public int getChosenPlanetIndex() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getChosenPlanetIndex()' is not supported in " + this + " state");
    }

    public void setWantsToVisit(boolean wantsToVisitShip) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setWantsToVisit()' is not supported in " + this + " state");
    }

    public boolean getWantsToVisit() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getWantsToVisit()' is not supported in " + this + " state");
    }

    // SHIELDS
    public void setShieldsToActivate(List<ComponentHelper<Void>> shieldsToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setShieldsToActivate()' is not supported in " + this + " state");
    }

    public List<ComponentHelper<Void>> getShieldsToActivate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getShieldsToActivate()' is not supported in " + this + " state");
    }

    // CANNONS
    public void setDoubleCannonsToActivate(List<ComponentHelper<Void>> doubleCannonsToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setDoubleCannonsToActivate()' is not supported in " + this + " state");
    }

    public List<ComponentHelper<Void>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getDoubleCannonsToActivate()' is not supported in " + this + " state");
    }

    // ENGINES
    public void setDoubleEnginesToActivate(int doubleEnginesToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setDoubleEnginesToActivate()' is not supported in " + this + " state");
    }

    public int getDoubleEnginesToActivate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getDoubleEnginesToActivate()' is not supported in " + this + " state");
    }
}
