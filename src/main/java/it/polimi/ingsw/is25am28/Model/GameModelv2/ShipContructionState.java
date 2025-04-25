package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.FileLoader.TileLoader;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.TimeObserver.TimeSubscriber;
import it.polimi.ingsw.is25am28.Model.TimeObserver.TimerObserver;

import java.util.*;

// TODO: Implement the HourGlass here (the state contains the HourGlass instance and implements the onTimerEnd method)

public final class ShipContructionState extends State implements TimeSubscriber {
    private final static int FLIP_TIME_ONE_HALF_MIN = (int)(1.5 * 1000 * 60);
    private final static int FLIP_COUNT_LV2 = 2;
    private final static int SHIP_GRID_SIZE = 12;

    private final TimerObserver clock;
    // TODO: private final SessionSubscriber controller;

    // Count the number of players that finished to build their ship --> Will be used to make the state transaction
    private final int gameLevel;
    private int flippedTimes;
    private final List<String> players_done; // List of players nickname that ended to build their ship

    private final List<Component> all_components;

    // Needed to send the data to the clients --> they need to understand in which state is each component
    private final Set<Integer> selected;
    private final Set<Integer> flipped;

    private final List<EventCard> cards;

    // The map will store the pair of sub-deck id with the playerNickname that selected it
    private final Map<Integer, String> selectedSubDecks;

    private boolean shipConfigEnded;


    public ShipContructionState(GameModel model) {
        super(model);

        this.gameLevel = model.getGameLevel();

        // Load the tiles
        this.all_components = TileLoader.get().read();
        // Collections.shuffle(this.all_components);
        this.selected = new HashSet<>();
        this.flipped = new HashSet<>();

        switch (this.gameLevel) {
            // Since there is no time limit for the test flight, we can set it to 30 minutes
            case 0: {
                this.clock = new TimerObserver( FLIP_TIME_ONE_HALF_MIN * 20 );
                break;
            }
            // For the real game levels (1, 2, and 3), each flip will last 1.5 minutes
            case 1, 2, 3: {
                this.clock = new TimerObserver(FLIP_TIME_ONE_HALF_MIN);
                break;
            }
            default:
                throw new IllegalArgumentException("The clock does not support the required level: " + this.gameLevel);
        }

        clock.observe(this);

        this.flippedTimes = 0;
        this.players_done = new ArrayList<>();
        this.shipConfigEnded = false;

        this.cards = this.model.getGameDeck();
        this.selectedSubDecks = new HashMap<>();
    }

    /**
     * Mark the given sub-deck as selected
     * @return the Component Data Object Transfer needed to update the client with the selected deck event
     * */
    public synchronized ConstructionDeckDTO selectSubDeck(String player, Integer selectedDeck) throws IllegalStateException {
        if (selectedDeck < 0 || selectedDeck > 3) {
            throw new IllegalStateException("The given sub-deck does not exist");
        }

        if (this.shipConfigEnded) {
            throw new IllegalStateException("The time to select the sub-decks has ended");
        }

        if (this.selectedSubDecks.containsKey(selectedDeck)) {
            throw new IllegalStateException("The required sub-deck has already been selected from someone else");
        }

        this.selectedSubDecks.put(selectedDeck, player);
        ConstructionDeckDTO state = new ConstructionDeckDTO()
                .setSubDeck(selectedDeck)
                .setPlayerNickname(player)
                .setSelected(true);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.DECK_EVENT.toString());

        return state;
    }

    /**
     * Removes the selected mark of the given sub-deck
     * @return the Component Data Object Transfer needed to update the client with the deselected deck event
     * */
    public synchronized ConstructionDeckDTO deselectSubDeck(String player, Integer selectedDeck) throws IllegalStateException {
        if (selectedDeck < 0 || selectedDeck > 3) {
            throw new IllegalStateException("The given sub-deck does not exist");
        }

        if (this.shipConfigEnded) {
            throw new IllegalStateException("The time to select the sub-decks has ended");
        }

        if (!this.selectedSubDecks.containsKey(selectedDeck)) {
            throw new IllegalStateException("The given sub-deck id is not selected by anyone");
        }

        if (!this.selectedSubDecks.get(selectedDeck).equals(player)) {
            throw new IllegalStateException("You cannot deselect a sub-deck selected from someone else");
        }

        this.selectedSubDecks.remove(selectedDeck);
        ConstructionDeckDTO state = new ConstructionDeckDTO()
                .setSubDeck(selectedDeck)
                .setPlayerNickname(player)
                .setSelected(false);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.DECK_EVENT.toString());
        return state;
    }

    /**
     * Select the given tile
     * @return the Component Data Object Transfer needed to update the client with the selectComponent event
     * */
    public synchronized ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to select the tiles has ended");
        }

        Integer id = i * SHIP_GRID_SIZE + j;

        // TODO: Understand if we need to put the player name in the Exception
        if (selected.contains(id)) {
            throw new IllegalStateException("The required tile has already been selected from someone else");
        }

        // Add the selected component to the flipped and selected SET
        flipped.add(id);
        selected.add(id);

        ConstructionComponentDTO state = new ConstructionComponentDTO()
                .setPlayerNickname(player)
                .setI(i)
                .setJ(j)
                .setSelected(true);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TILE_EVENT.toString());

        return state;
    }

    /**
     * Deselect the given tile
     * @return the Component Data Object Transfer needed to update the client with the deselectComponent event
     * */
    public synchronized ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to deselected the tiles has ended");
        }

        Integer id = i * SHIP_GRID_SIZE + j;

        if (!selected.contains(id)) {
            throw new SelectedConcurrencyException(player);
        }

        selected.remove(id);

        ConstructionComponentDTO state = new ConstructionComponentDTO()
                .setPlayerNickname(player)
                .setI(i)
                .setJ(j)
                .setSelected(false);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TILE_EVENT.toString());

        return state;
    }

    /**
     * Command executed by the client to place a component in his ship:
     * @param player is the playerNickname used to get the specific ship
     * @param componentID is the ID of the placed component
     * @param i is the 'i' coordinate of where the component has been placed
     * @param j is the 'j' coordinate of where the component has been placed
     *
     * @return the DTO that will contain the information about where the player placed the component
     * */
    public synchronized PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        if (this.shipConfigEnded) {
            throw new IllegalStateException("The time to place the tiles has ended");
        }

        // Get the player from the map
        Player p = this.model.getPlayers().get(player);
        if (p == null) {
            throw new IllegalStateException("The player " + player + " does not exist");
        }

        // Get the component, set the rotation and add it to the player ship
        Component baseComp = all_components.get(componentID);
        baseComp.setRotation(rotation);
        p.getShip().addComponent(baseComp, i, j);

        PlacedComponentDTO state =  new PlacedComponentDTO()
                .setPlayerNickname(player)
                .setId(componentID)
                .setI(i)
                .setJ(j)
                .setRotation(rotation);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.SHIP_EVENT.toString());

        return state;
    }

    /**
     * Execute the command that will be triggered once the player decided to confirm his ship.
     * @param player is the playerNickname that will be used to mark the player as finished
     * @param reservedTiles is the number of reservedComponents not used by the player --> will count as negative credits
     *
     * @return the DTO that contains the information of the player that submitted the ship (nickname, credits and cursor)
     * */
    public synchronized PlayerEndedShipDTO playerEndedSendShip(String player, int reservedTiles) throws IllegalStateException {
        if (this.players_done.contains(player)) {
            throw new IllegalArgumentException("The player " + player + " has already sent the ship");
        }

        Player p = model.getPlayers().get(player);
        if (p == null) {
            throw new IllegalArgumentException("No player was found with the nickname: " + player);
        }

        // Add the reserved and not used components to the stack of lost components
        p.addLostPieces(reservedTiles);
        this.players_done.add(player);

        // Add the player to the board --> The order of the players will depend on the ship submission order
        this.model.addPlayerToBoard(player);

        PlayerEndedShipDTO state = new PlayerEndedShipDTO()
                .setPlayerCredits(p.getLostPieces())
                .setPlayerCursors(p.getCursor())
                .setPlayerNicknames(player);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.SHIP_EVENT.toString());

        return state;
    }

    public synchronized TimerDTO flipTimer(String player) throws IllegalStateException, TimerFlipException {
        if (this.gameLevel != 2) {
            throw new TimerFlipException(player);
        }

        if (!clock.hasFinished()) {
            throw new TimerFlipException(player);
        }

        clock.flip();

        TimerDTO state = new TimerDTO()
                .setHasBeenFlipped(true)
                .setCanBeFlipped(flippedTimes != FLIP_COUNT_LV2);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TIMER_EVENT.toString());

        return state;
    }

    // TODO: Make the state transaction
    @Override
    public void onComplete() {
        if (players_done.size() == model.getNumPlayers()) {

            // Check the players ship
            // if all the ships are valid go to PopulateShipState
            // otherwise go to the FixShipState
            List<String> playersWithInvalidShip = new ArrayList<>();

            for (Player p : model.getPlayers().values()) {
                if (!p.getShip().validateShip()) {
                    playersWithInvalidShip.add(p.getNickname());
                }
            }

            if (playersWithInvalidShip.isEmpty()) {
                this.model.setCurrentState(new PopulateShipState(model));
            } else {
                this.model.setCurrentState(new FixShipState(model, playersWithInvalidShip));
            }
        }
    }

    // TODO: Modify the code to send, when finished, to the client a status that indicates that they need to send their ship.
    @Override
    public void onTimerEnd() {
        if (this.gameLevel != 2) return;

        synchronized (clock) {
            if (flippedTimes == FLIP_COUNT_LV2) {
                this.shipConfigEnded = true;

                // TODO: Modify how we send the data to the client --> we need to communicate that the time is over,
                //  they need to send the ship
            }
        }
    }

    @Override
    public StateDTO generateState() {
        List<CardStateJSON> cardsState = new ArrayList<>();

        for (EventCard card : this.cards) {
            cardsState.add(card.generateState());
        }

        ShipConstructionDTO state = new ShipConstructionDTO()
                .setAllComponents(this.all_components.stream().map(Component::toMap).toList())
                .setCards(cardsState)
                .setFlippedComponents(this.flipped.stream().toList())
                .setSelectedComponents(this.selected.stream().toList());

        state.setStateName(this.toString());

        return state;
    }
}
