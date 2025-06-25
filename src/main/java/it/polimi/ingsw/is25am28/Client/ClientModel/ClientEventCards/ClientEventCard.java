package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUIGenerator;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ClientEventCard implements WidgetTUIGenerator {
    protected final List<String> enabledCommands = new ArrayList<>();

    protected final int cardTypeId;
    protected final int uniqueCardId;
    protected String playerNickname;
    protected String cardName;
    protected String path;
    protected int cardLevel;
    protected boolean hasBeenUsed;

    // This flag allows the card to send its full static information
    // (like when only visualized at the start of the game) only when
    // it has not been used a single time wit useCard()
    protected boolean hasBeenActivated;

    protected ClientModel model;
    protected InputThread inputThread;

    // Constructor
    public ClientEventCard(CardStateJSON cardState) {
        this.uniqueCardId = cardState.getUniqueCardId();
        this.cardTypeId = cardState.getCardTypeId();
        this.cardName = cardState.getCardName();
        this.cardLevel = cardState.getCardLevel();
        this.path = cardState.getImagePath();

        enabledCommands.add("playCard");
    }

    /**
     * Each card will set to TRUE only the input methods it needs inside
     * its own ActionJSON to provide the server the player's choices.
     */
    public void setAvailableCommands(Map<String, Pair<Boolean, CommandWidgetTUI>> indexedCardInputMethods) {
        for (Map.Entry<String, Pair<Boolean, CommandWidgetTUI>> entry : indexedCardInputMethods.entrySet()) {
            entry.getValue().setKey(false);
        }

        for (String command : enabledCommands) {
            if (indexedCardInputMethods.containsKey(command)) {
                indexedCardInputMethods.get(command).setKey(true);
            }
        }
    }

    /**
     * @return the {@code enabledCommands}
     */
    public List<String> getAvailableCommands() {
        return this.enabledCommands;
    }

    /**
     * @return This client card's ID (the type of the card)
     */
    public int getCardTypeId() {
        return this.cardTypeId;
    }

    /**
     * @return This client card's unique ID (progressive number)
     */
    public int getUniqueCardId() {
        return this.uniqueCardId;
    }

    /**
     * @return This client card's image path
     */
    public String getCardPath() {
        return this.path;
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
     * all the relevant information
     */
    public abstract WidgetTUI generateWidget();

    /**
     * @return The current player's nickname
     */
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    /**
     * @return Additional info on the current card (like the column where a plasmaShot is coming from)
     */
    public String getAdditionalCardInfo() {
        return "No additional info\nin this card!";
    }

    /**
     * Clears the JSON used to store the player's decision when playing the card
     */
    public abstract void clearJSON();

    // ======== Players' ActionJSON Compilation Methods ======== //

    /**
     * Sets which lifeForms to remove in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setCrewToRemove()' is not supported in " + this + " state");
    }

    /**
     * @return the lifeForms to remove from the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<ComponentHelper<LifeformType>> getCrewToRemove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getCrewToRemove()' is not supported in " + this + " state");
    }

    /**
     * Sets which items to remove in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setItemsToBeRemoved()' is not supported in " + this + " state");
    }

    /**
     * @return the items to remove from the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getItemsToBeRemoved()' is not supported in " + this + " state");
    }

    /**
     * Sets which items to take in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setItemsToBeTaken()' is not supported in " + this + " state");
    }

    /**
     * @return the items to take from the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getItemsToBeTaken()' is not supported in " + this + " state");
    }

    /**
     * Sets the takeReward attribute in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setTakeReward(boolean takeReward) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setTakeReward()' is not supported in " + this + " state");
    }

    /**
     * @return the takeReward attribute from the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public Boolean getTakeReward() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getTakeReward()' is not supported in " + this + " state");
    }

    /**
     * Sets the chosenPlanetIndex in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setChosenPlanetIndex(int chosenPlanetIndex) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setChosenPlanetIndex()' is not supported in " + this + " state");
    }

    /**
     * @return the chosenPlanetIndex form the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public Integer getChosenPlanetIndex() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getChosenPlanetIndex()' is not supported in " + this + " state");
    }

    /**
     * Sets the wantsToVisit attribute in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setWantsToVisit(boolean wantsToVisit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setWantsToVisit()' is not supported in " + this + " state");
    }

    /**
     * @return the wantsToVisit attribute from the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public Boolean getWantsToVisit() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getWantsToVisit()' is not supported in " + this + " state");
    }

    /**
     * Sets which shields to activate in the card's JSON, along with the associated battery to power them
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setShieldsToActivate(List<Pair<CoordinatePair, CoordinatePair>> shieldsToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setShieldsToActivate()' is not supported in " + this + " state");
    }

    /**
     * @return the shields to activate from the card's JSON, along with the associated battery to power them
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<Pair<CoordinatePair, CoordinatePair>> getShieldsToActivate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getShieldsToActivate()' is not supported in " + this + " state");
    }

    /**
     * Sets which doubleCannons to activate in the card's JSON, along with the associated battery to power them
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setDoubleCannonsToActivate(List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setDoubleCannonsToActivate()' is not supported in " + this + " state");
    }

    /**
     * @return the doubleCannons to activate from the card's JSON, along with the associated battery to power them
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<Pair<CoordinatePair, CoordinatePair>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getDoubleCannonsToActivate()' is not supported in " + this + " state");
    }

    /**
     * Sets which doubleEngines to activate in the card's JSON, along with the associated battery to power them
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setDoubleEnginesToActivate(List<Pair<CoordinatePair, CoordinatePair>> doubleEnginesToActivate) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setDoubleEnginesToActivate()' is not supported in " + this + " state");
    }

    /**
     * @return the doubleEngines to activate from the card's JSON, along with the associated battery to power them
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<Pair<CoordinatePair, CoordinatePair>> getDoubleEnginesToActivate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getDoubleEnginesToActivate()' is not supported in " + this + " state");
    }

    /**
     * Sets which batteries to remove in the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void setBatteriesToBeStolen(List<CoordinatePair> batteriesToBeStolen) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'setBatteriesToBeStolen()' is not supported in " + this + " state");
    }

    /**
     * @return the batteries to activate from the card's JSON
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<CoordinatePair> getBatteriesToBeStolen() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getBatteriesToBeStolen()' is not supported in " + this + " state");
    }

    // Card specific methods

    /**
     * @return The card's available itemColors
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public List<ItemColor> getAvailableItemColors() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getAvailableColors()' is not supported in " + this + " card");
    }

    /**
     * Removes a single item of the given itemColor from the card's resources
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public void removeItem(ItemColor itemColor) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'removeColor()' is not supported in " + this + " card");
    }

    /**
     * @return The card's firepower
     * @throws UnsupportedOperationException if the card does not support this method
     */
    public int getFirepower() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method 'getFirepower()' is not supported in " + this + " card");
    }

    /**
     * @return whether a player has been defeated by the card (e.g. Slavers)
     */
    public boolean isPlayerDefeated() throws UnsupportedOperationException{
        throw new UnsupportedOperationException("The method 'isPlayerDefeated()' is not supported in " + this + " card");
    }
}
