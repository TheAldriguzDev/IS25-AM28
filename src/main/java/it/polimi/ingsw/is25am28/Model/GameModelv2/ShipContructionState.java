package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.FileLoader.TileLoader;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Timer.HourGlass;
import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObserver;

import java.util.*;

// TODO: Implement the HourGlass here (the state contains the HourGlass instance and implements the onTimerEnd method)

public final class ShipContructionState extends State implements TimerObserver {
    // Default component selection matrix (row, col) dimensions
    public static final int DEFAULT_COMPONENT_ROWS = 8;
    public static final int DEFAULT_COMPONENT_COLS = 19;

    private final HourGlass hourGlass;
    // TODO: private final SessionSubscriber controller;

    // Count the number of players that finished to build their ship --> Will be used to make the state transaction
    private final int gameLevel;
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

        // Only initialize the hourglass if the current game
        // difficulty level is not 0 (i.e.: Test Flight)
        if (this.gameLevel != 0) {
            this.hourGlass = new HourGlass(this.gameLevel);
            this.hourGlass.addTimerSubscriber(this);

            // TODO (NOTE: Add this if you want to run the "test_game_model_hourglass" in GameModelTest.java)
            //      (It only reduces the time to wait when running said test) or just test the game
            this.hourGlass.setDurationInMillis(3000);

            this.hourGlass.flip();
        }
        else {
            this.hourGlass = null;
        }

        this.players_done = new ArrayList<>();
        this.shipConfigEnded = false;

        this.cards = this.model.getGameDeck();
        this.selectedSubDecks = new HashMap<>();
    }

    /**
     * Either selects or deselects the given subdeck
     *
     * @param player The player that initiated this action
     * @param selectedDeck The subdeck ID that the player wants to select or deselect
     * @param isSelectAction TRUE if the player wants to select the given subdeck, and
     *                       FALSE if the player wants to deselect the given subdeck
     *
     * @return the Component Data Object Transfer needed to update the client with the selected deck event
     * */
    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        if (selectedDeck < 0 || selectedDeck > 3) {
            throw new IllegalStateException("The given sub-deck does not exist");
        }

        if (this.shipConfigEnded) {
            throw new IllegalStateException("The time to select the sub-decks has ended");
        }

        if (isSelectAction) {
            if (this.selectedSubDecks.containsKey(selectedDeck)) {
                throw new IllegalStateException("The required sub-deck has already been selected from someone else");
            }

            this.selectedSubDecks.put(selectedDeck, player);
        }
        else {
            if (!this.selectedSubDecks.containsKey(selectedDeck)) {
                throw new IllegalStateException("The given sub-deck id is not selected by anyone");
            }

            if (!this.selectedSubDecks.get(selectedDeck).equals(player)) {
                throw new IllegalStateException("You cannot deselect a sub-deck selected from someone else");
            }

            this.selectedSubDecks.remove(selectedDeck, player);
        }

        ConstructionDeckDTO state = new ConstructionDeckDTO()
                .setSubDeck(selectedDeck)
                .setPlayerNickname(player)
                .setSelected(isSelectAction);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.DECK_EVENT.toString());

        return state;
    }

    /**
     * Select the given tile
     * @return the Component Data Object Transfer needed to update the client with the selectComponent event
     * */
    public ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to select the tiles has ended");
        }

        Integer id = i * DEFAULT_COMPONENT_COLS + j;

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
    public ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to deselected the tiles has ended");
        }

        Integer id = i * DEFAULT_COMPONENT_COLS + j;

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
    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        if (this.shipConfigEnded) {
            throw new IllegalStateException("The time to place the tiles has ended");
        }

        // Get the player from the map
        Player p = this.model.getPlayers().get(player);
        if (p == null) {
            throw new IllegalStateException("The player " + player + " does not exist");
        }

        Ship ship = p.getShip();

        if (ship.getComponent(i, j) != null) {
            throw new IllegalStateException("The given coordinates have already stored a component");
        }

        // Get the component, set the rotation and add it to the player ship
        Component baseComp = all_components.get(componentID);
        baseComp.setRotation(rotation);
        ship.addComponent(baseComp, i, j);

        PlacedComponentDTO state =  new PlacedComponentDTO()
                .setPlayerNickname(player)
                .setId(componentID)
                .setI(i)
                .setJ(j)
                .setRotation(rotation);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.PLACE_EVENT.toString());

        return state;
    }

    /**
     * Execute the command that will be triggered once the player decided to confirm his ship.
     * @param player is the playerNickname that will be used to mark the player as finished
     * @param reservedTiles is the number of reservedComponents not used by the player --> will count as negative credits
     *
     * @return the DTO that contains the information of the player that submitted the ship (nickname, credits and cursor)
     * */
    public PlayerEndedShipDTO playerEndedSendShip(String player, int reservedTiles) throws IllegalStateException {
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

    public TimerDTO flipTimer(String player) throws IllegalStateException {
        if (this.hourGlass == null) {
            throw new IllegalStateException("ERROR: Hourglass is null because the current game doesn't require a hourglass (i.e.: Test Flight)");
        }

        if (this.hourGlass.getRemainingFlips() == 0) {
            throw new IllegalStateException("ERROR: Hourglass cannot be flipped again (all flips have been consumed)");
        }

        if (this.hourGlass.getRemainingFlips() == 1 && !this.players_done.contains(player)) {
            throw new IllegalStateException("ERROR: You cannot flip the timer since you've not finished the ship yet!");
        }

        if (!this.hourGlass.flip()) {
            throw new IllegalStateException("ERROR: Hourglass cannot be flipped at this time");
        }

        TimerDTO state = new TimerDTO()
                .setIsServerAction(false)
                .setHasEnded(false)
                .setCanBeFlipped(this.hourGlass.getRemainingFlips() > 0);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TIMER_EVENT.toString());

        return state;
    }

    // TODO: Make the state transition
    @Override
    public void onComplete() {
        if (players_done.size() == model.getNumPlayers()) {

            // Unsubscribe the state from receiving updated when the HourGlass ends
            if (this.gameLevel != 0) {
                this.hourGlass.removeTimerSubscriber(this);
            }

            // Check the players ship
            // if all the ships are valid go to PopulateShipState
            // otherwise go to the FixShipState
            List<String> playersWithInvalidShip = new ArrayList<>();
            // Store the players that needs to populate the ship
            List<String> playerWithoutPopulatedShip = new ArrayList<>();

            for (Player p : model.getPlayers().values()) {
                p.getShip().generateComponentSubLists();

                // Check if the player has an invalid ship
                if (!p.getShip().validateShip()) {
                    playersWithInvalidShip.add(p.getNickname());
                }

                // Check if the player needs to populate the ship
                if (!p.getShip().isShipPopulated()) {
                    playerWithoutPopulatedShip.add(p.getNickname());
                }
            }

            if (playersWithInvalidShip.isEmpty()) {
                if (playerWithoutPopulatedShip.isEmpty()) {
                    this.model.setCurrentState(new CardRoundState(model));
                } else {
                    this.model.setCurrentState(new PopulateShipState(model));
                }
            } else {
                this.model.setCurrentState(new FixShipState(model, playersWithInvalidShip));
            }
        }
    }

    @Override
    public void onTimerEnd() {
         synchronized (this.hourGlass) {
            TimerDTO state = new TimerDTO()
                    .setIsServerAction(true)
                    .setHasEnded(this.hourGlass.getRemainingFlips() == 0)
                    .setCanBeFlipped(this.hourGlass.getRemainingFlips() > 0);
            state.setStateName(this.toString());
            state.setEventType(ShipConstructionType.TIMER_EVENT.toString());

            if (this.hourGlass.getRemainingFlips() == 0) {
                this.shipConfigEnded = true;
            }

            Answer answer = new Answer()
                    .setState(state);

            // TODO: Understand if this is the correct solution to update the client about the timer end
            if (this.model.getCurrentState().equals(this)) {
                this.model.broadCastUpdate(answer);
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
