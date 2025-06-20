package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Loader.TileLoader;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FastShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Loader.FastShipLoader;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Timer.HourGlass;
import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObserver;

import java.io.IOException;
import java.util.*;

public final class ShipContructionState extends State implements TimerObserver {
    private final HourGlass hourGlass;

    // Count the number of players that finished to build their ship --> Will be used to make the state transaction
    private final int gameLevel;
    private final List<String> players_done; // List of players nickname that ended to build their ship

    private final List<Component> components;

    // Needed to send the data to the clients --> they need to understand in which state is each component
    private final Set<Integer> selected_components;
    private final Set<Integer> flipped_components;

    // This map links each player with their reserved component
    private final Map<String, List<Integer>> reservedComponents;

    private final List<EventCard> cards;

    // The map will store the pair of sub-deck id with the playerNickname that selected_components it
    private final Map<Integer, String> selectedSubDecks;

    private boolean shipConfigEnded;

    private FastShipLoader fastShipLoader = null;

    public ShipContructionState(GameModel model) {
        super(model);

        TileLoader loader;
        try {
            loader = new TileLoader();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while reading the json file: " + e);
        }

        this.gameLevel = model.getGameLevel();

        // Load the tiles
        this.components = loader.getTiles();
//        Collections.shuffle(this.components); // TODO uncomment in the final version of the game
        this.selected_components = new HashSet<>();
        this.flipped_components = new HashSet<>();

        // Init the reserved component list
        this.reservedComponents = new HashMap<>();
        for (Player p : this.model.getPlayers().values()) {
            this.reservedComponents.put(p.getNickname(), new ArrayList<>());
        }

        // Only initialize the hourglass if the current game
        // difficulty level is not 0 (i.e.: Test Flight)
        if (this.gameLevel != 0) {
            this.hourGlass = new HourGlass(this.gameLevel);
            this.hourGlass.addTimerSubscriber(this);

//            this.hourGlass.setDurationInMillis(3000);   // 3s
//            this.hourGlass.setDurationInMillis(10000);  // 10s

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
     * @return the Component Data Object Transfer needed to update the client with the selected_components deck event
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
                throw new IllegalStateException("The required sub-deck has already been selected_components from someone else");
            }

            this.selectedSubDecks.put(selectedDeck, player);
        }
        else {
            if (!this.selectedSubDecks.containsKey(selectedDeck)) {
                throw new IllegalStateException("The given sub-deck id is not selected_components by anyone");
            }

            if (!this.selectedSubDecks.get(selectedDeck).equals(player)) {
                throw new IllegalStateException("You cannot deselect a sub-deck selected_components from someone else");
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
    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to select the tiles has ended");
        }

        if (this.players_done.contains(player)) {
            throw new IllegalStateException("The action is not allowed since you already sent your ship!");
        }

        if (selected_components.contains(id)) {
            throw new IllegalStateException("The required tile has already been selected_components from someone else");
        }

        // Add the selected_components component to the flipped_components and selected_components SET
        flipped_components.add(id);
        selected_components.add(id);

        ConstructionComponentDTO state = new ConstructionComponentDTO()
                .setPlayerNickname(player)
                .setId(id)
                .setSelected(true);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TILE_EVENT.toString());

        return state;
    }

    /**
     * Deselect the given tile
     * @return the Component Data Object Transfer needed to update the client with the deselectComponent event
     * */
    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to deselected the tiles has ended");
        }

        if (this.players_done.contains(player)) {
            throw new IllegalStateException("The action is not allowed since you already sent your ship!");
        }

        if (!selected_components.contains(id)) {
            throw new IllegalArgumentException("The player: " + player + " cannot deselect the tile since it's not a selected one");
        }

        List<Integer> reservedIDs = this.reservedComponents.values().stream().flatMap(Collection::stream).toList();
        if (reservedIDs.contains(id)) {
            throw new IllegalArgumentException("You cannot deselect a reserved tile");
        }

        selected_components.remove(id);

        ConstructionComponentDTO state = new ConstructionComponentDTO()
                .setPlayerNickname(player)
                .setId(id)
                .setSelected(false);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TILE_EVENT.toString());

        return state;
    }

    public ReservedComponentDTO reserveTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to deselected the tiles has ended");
        }

        if (this.players_done.contains(player)) {
            throw new IllegalStateException("The action is not allowed since you already sent your ship!");
        }

        if (!this.selected_components.contains(id)) {
            throw new IllegalStateException("The given component cannot be reserved since it's not selected");
        }

        if (this.reservedComponents.get(player).size() >= 2) {
            throw new IllegalStateException("You cannot reserve more than two components");
        }

        if (this.reservedComponents.get(player).contains(id)) {
            throw new IllegalStateException("The given component cannot be reserved since it has already been reserved");
        }

        // Add the reserved component to the player
        this.reservedComponents.get(player).add(id);

        ReservedComponentDTO state = new ReservedComponentDTO()
                .setPlayerNickname(player)
                .setId(id);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TILE_EVENT.toString());

        return state;
    }

    @Override
    public FastShipDTO fastShip(String playerNickname) throws IllegalStateException, IllegalArgumentException {
        // Creating the ship from the JSON
        Player targetPlayer = this.model.getPlayers().get(playerNickname);
        Ship targetShip = targetPlayer.getShip();

        if (fastShipLoader == null) {
            try {
                this.fastShipLoader = new FastShipLoader();
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while reading the json file: " + e);
            }
        }
        this.fastShipLoader.loadShipFromJSON(targetShip);

        // Adding the player to the ones that have finished
        this.playerEndedSendShip(playerNickname, 0);

        // Creating the FastShipDTO
        FastShipDTO state = new FastShipDTO();

        state.setTargetNickname(playerNickname);
        state.setPlayerCursor(targetPlayer.getCursor());
        state.setShip(targetShip.generateState());

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

        if (this.players_done.contains(player)) {
            throw new IllegalStateException("The action is not allowed since you already sent your ship!");
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
        Component baseComp = components.get(componentID);
        baseComp.setRotation(rotation);
        ship.addComponent(baseComp, i, j);

        // remove the reserved component, if present
        this.reservedComponents.get(player).remove(componentID);

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
            throw new IllegalStateException("ERROR: Hourglass cannot be flipped_components again (all flips have been consumed)");
        }

        if (this.hourGlass.getRemainingFlips() == 1 && !this.players_done.contains(player)) {
            throw new IllegalStateException("ERROR: You cannot flip the timer since you've not finished the ship yet!");
        }

        if (!this.hourGlass.flip()) {
            throw new IllegalStateException("ERROR: Hourglass cannot be flipped_components at this time");
        }

        TimerDTO state = new TimerDTO()
                .setIsServerAction(false)
                .setHasEnded(false)
                .setCanBeFlipped(this.hourGlass.getRemainingFlips() > 0);

        state.setStateName(this.toString());
        state.setEventType(ShipConstructionType.TIMER_EVENT.toString());

        return state;
    }

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
//                System.out.println(p.getNickname());
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
                    .setCanBeFlipped(this.hourGlass.getRemainingFlips() > 0)
                    .setIsTimeFlowing(false);

            state.setStateName(this.toString());
            state.setEventType(ShipConstructionType.TIMER_EVENT.toString());

            if (this.hourGlass.getRemainingFlips() == 0) {
                this.shipConfigEnded = true;
            }

            Answer answer = new Answer()
                    .setState(state);

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
                .setAllComponents(this.components.stream().map(Component::toMap).toList())
                .setCards(cardsState)
                .setFlippedComponents(this.flipped_components.stream().toList())
                .setSelectedComponents(this.selected_components.stream().toList())
                .setPlayerFinished(this.players_done.stream().toList())
                .setReservedComponents(this.reservedComponents);

        if (this.hourGlass != null) {
            if (this.hourGlass.isTimeFlowing()) {
                // A null TimerDTO means that the client will wait
                // for the onTimerEnd before receiving one
                state.setTimerDTO(null);
            }
            else {
                state.setTimerDTO(
                    new TimerDTO()
                        .setIsServerAction(true)
                        .setHasEnded(this.hourGlass.getRemainingFlips() == 0)
                        .setCanBeFlipped(this.hourGlass.getRemainingFlips() > 0)
                        .setIsTimeFlowing(false)
                );
            }
        }

        state.setStateName(this.toString());

        return state;
    }

    @Override
    public void handlePlayerDisconnection(String player) {
        for (Map.Entry<Integer, String> entry : this.selectedSubDecks.entrySet()) {
            if (entry.getValue().equals(player)) {
                this.selectedSubDecks.remove(entry.getKey());
            }
        }
    }
}
