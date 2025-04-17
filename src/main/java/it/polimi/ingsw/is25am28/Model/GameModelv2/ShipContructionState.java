package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.FileLoader.TileLoader;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.TimeObserver.TimeSubscriber;
import it.polimi.ingsw.is25am28.Model.TimeObserver.TimerObserver;

import java.util.*;

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
    }

    /**
     * Select the given tile
     * @return the Component Data Object Transfer needed to update the client with the selectComponent event --> the controller will be able to notifyAllTheClients
     * */
    public synchronized ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws IllegalStateException, SelectedConcurrencyException {
        if (shipConfigEnded) {
            throw new IllegalStateException("The time to selected the tiles has ended");
        }

        Integer id = i * SHIP_GRID_SIZE + j;

        if (selected.contains(id)) {
            throw new SelectedConcurrencyException(player);
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
     * @return the Component Data Object Transfer needed to update the client with the deselectComponent event --> the controller will be able to notifyAllTheClients
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
     * @return the list of players that have ended their ship
     * */
    public synchronized PlayerEndedShipDTO playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles) throws IllegalStateException, SelectedConcurrencyException {
        if (this.players_done.contains(player)) {
            throw new IllegalArgumentException("The player " + player + " has already sent the ship");
        }

        Player p = model.getPlayers().get(player);
        if (p == null) {
            throw new IllegalArgumentException("No player was found with the nickname: " + player);
        }

        for (ComponentHelper<ConstructionComponentDTO> c : playerShip) {
            int i = c.getI();
            int j = c.getJ();
            int index = i * SHIP_GRID_SIZE + j;

            if (index < 0 || index >= all_components.size()) {
                throw new IllegalArgumentException("No component was found with the given index: " + i + " - " + j);
            }

            if (c.getItem().isPresent()) {
                ConstructionComponentDTO info = c.getItem().get();
                Component baseComponent = all_components.get(index);

                Component rotated = baseComponent.setRotation(info.getRotation());
                p.getShip().addComponent(rotated, info.getI(), info.getJ());
            }
        }

        // Add the reserved and not used components to the stack of lost components
        p.addLostPieces(reservedTiles);
        this.players_done.add(player);

        // Add the player to the board --> The order of the players will depend on the ship submission order
        this.model.addPlayerToBoard(player);

        PlayerEndedShipDTO state = new PlayerEndedShipDTO()
                .setPlayerNicknames(this.players_done);

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
        ShipConstructionDTO state = new ShipConstructionDTO()
                .setAllComponents(this.all_components.stream().map(Component::toMap).toList())
                .setFlippedComponents(this.flipped.stream().toList())
                .setSelectedComponents(this.selected.stream().toList());

        state.setStateName(this.toString());

        return state;
    }
}
