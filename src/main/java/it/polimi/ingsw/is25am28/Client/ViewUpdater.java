package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard.ClientBoard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.GameModelv2.ShipContructionState;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.Screen.COMPUTER_MSG_TAG;
import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType.ASTRONAUT;

/**
 * Uses the Visitor Pattern to update the {@link ClientModel} based on updates received from the server,
 * and optionally triggers a UI render.
 *
 * Each method is invoked by a concrete implementation of {@link StateDTO} to activate the appropriate handlers.
 */
public class ViewUpdater implements StateVisitor {
    private final ClientUI ui;
    private final ClientModel model;

    private boolean isFirstAccess = true;

    public ViewUpdater(ClientUI ui, ClientModel model) {
        this.ui = ui;
        this.model = model;
    }

    @Override
    public void visit(StateDTO state) {
        System.out.println(state.getStateName());
        System.out.println(state);
    }

    @Override
    public void visit(AvailableGamesDTO state) throws Exception {
        this.ui.showLobbies(state, isFirstAccess);
        this.isFirstAccess = false;
    }

    @Override
    public void visit(WaitPlayersStateDTO state) throws Exception {
        // Creating the ship of the newly connected player in all clients
        try {
            for (Map.Entry<String, PlayerColor> playerEntry : state.getUsedNicknames().entrySet()) {
                this.model.addNewPlayer(playerEntry.getKey(), playerEntry.getValue());
            }

            this.ui.showWaitingForPlayers(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(ReconnectDTO state) throws Exception {
        try {
            synchronized (this.model) {
                if (this.model.getNickname().equals(state.getTargetNickname())) {

                    this.model.setNickname(state.getTargetNickname());
                    this.model.setDifficultyLevel(state.getGameLevel());

                    // 1. Create the players --> and set their ship
                    List<PlayerJSON> players = state.getPlayers();
                    for (PlayerJSON player : players) {
                        this.model.addNewPlayer(player.getNickname(), PlayerColor.valueOf(player.getColor()), player.getCredits(), player.getLostPieces(), player.getShip());
                    }

                    // 2. Create the board
                    BoardJSON board = state.getBoard();
                    this.model.setClientBoard(new ClientBoard(board, this.model));

                    // 3. Reset the resourceBank to the correct amount of resources
                    this.model.getResourceBank().resetResourcesQuantity(state.getResourceBank());

                    // 4. cards
                    this.model.generateClientEventCards(state.getCards());

                    System.out.println("Ended reconnecting to the game.");
                } else {
                    System.out.println();
                    new WidgetTUI()
                            .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor(state.getTargetNickname() + " reconnected to the game.", ANSIColors.BRIGHT_MAGENTA))
                            .addPadding(0, 1, 0, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void visit(ShipConstructionDTO state) throws Exception {
        // Set the model state to the ShipConstructionState that will initialize all the components
        synchronized (this.model) {
            try {
                this.model.setState(new ClientShipConstructionState(this.model, state));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            this.ui.showShipConstruction(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(ConstructionComponentDTO state) throws Exception {
        synchronized (this.model) {
            if (state.getEventType().equals(ShipConstructionType.TILE_EVENT.toString())) {
                this.model.getState().getConstructionShipComponents().stream()
                        .filter(c -> c.getID() == state.getId())
                        .findFirst()
                        .ifPresent(
                            (c) -> {
                                c.setAsFlipped();
                                c.setIsVisible(!state.isSelected());
                            }
                        );

                this.ui.updateShipConstructionComponent(state);
            }
        }
    }


    @Override
    public void visit(ReservedComponentDTO state) throws Exception {
        if (state.getPlayerNickname().equals(this.model.getNickname())) {
            ClientComponent comp = this.model.getState().getConstructionShipComponents().get(state.getId());
            this.model.getState().reserveTile(comp);
        }
    }


    @Override
    public void visit(FastShipDTO state) throws Exception {
        try {
            synchronized (this.model) {
                this.model.getShipOfPlayer(state.getTargetNickname()).ifPresent(
                    (ClientShip ship) -> {
                        ship.substituteShipWithPremadeConfiguration(state.getShip(), this.model.getState().getConstructionShipComponents());

                        // After a fast build request, the player is automatically
                        // considered as if he's also already sent his ship.
                        this.model.getState().setPlayerFinishedBuildingShip(
                            state.getTargetNickname(),
                            state.getPlayerCursor()
                        );

                        this.ui.handlePlayerFastShip(state.getTargetNickname());
                        this.ui.placePlayerInTheBoard(state.getTargetNickname());
                    }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(FixedComponentDTO state) throws Exception {
        synchronized (this.model) {
            this.model.getShipOfPlayer(state.getPlayerNickname()).ifPresent(
                    (ClientShip ship) -> {
                        if (state.isShipFixed()) {
                            this.model.getState().removePlayerFromFixList(state.getPlayerNickname());
                        }

                        ship.removeComponent(state.getI(), state.getJ());
                    }
            );

            this.ui.updateShipRemovedComponent(state);
        }
    }


    @Override
    public void visit(PopulateShipComponentDTO state) throws Exception {
        synchronized (this.model) {
            this.model.getShipOfPlayer(state.getPlayerNickname()).ifPresent(
                (ClientShip ship) -> {
                    if (ship.isShipPopulated()) {
                        this.model.getState().addPlayerToPopulateList(state.getPlayerNickname());
                    }

                    state.getComponent().getItem().ifPresent(
                        (LifeformType lfType) -> {
                            ship.addLifeformToCabin(
                                state.getComponent().getI(),
                                state.getComponent().getJ(),
                                lfType
                            );

                            // Do it one more time if it's an ASTRONAUT (since they are added in pairs)
                            if (lfType == ASTRONAUT) {
                                ship.addLifeformToCabin(
                                    state.getComponent().getI(),
                                    state.getComponent().getJ(),
                                    lfType
                                );
                            }
                        }
                    );
                }
            );


            this.ui.updateShipPlacedLifeForm(state);

            if (state.isShipPopulated()) {
                this.model.getState().addPlayerToPopulateList(state.getPlayerNickname());
            }
        }
    }


    @Override
    public void visit(PlacedComponentDTO state) throws Exception {
        synchronized (this.model) {
            if (state.getEventType().equals(ShipConstructionType.PLACE_EVENT.toString())) {
                // Get the component
                ClientComponent comp = this.model.getState().getConstructionShipComponents().get(state.getId());
                comp.setRotation(state.getRotation());

                Optional<ClientShip> optionalShip = this.model.getShipOfPlayer(state.getPlayerNickname());
                optionalShip.ifPresent(ship -> ship.addComponent(comp, state.getI(), state.getJ()));

                this.ui.updateShipPlacedComponent(state);
            }
        }
    }


    @Override
    public void visit(PlayerEndedShipDTO state) throws Exception {
        synchronized (this.model) {
            try {
                // Sets this player's homonymous flag to TRUE to mask the
                // commands he can no longer use (since he sent the ship)
                this.model.getState().setPlayerFinishedBuildingShip(state.getPlayerNickname(), state.getPlayerCursors());

                // Update the game board
                this.ui.placePlayerInTheBoard(state.getPlayerNickname());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visit(TimerDTO state) throws Exception {
        this.ui.receiveTimerDTO(state);
    }

    @Override
    public void visit(ConstructionDeckDTO state) throws Exception {
        synchronized (this.model) {
            this.model.getState().setSubdeckStatus(state.getSubDeck(), state.isSelected());
        }
    }

    @Override
    public void visit(FixShipDTO state) throws Exception {
        try {
            // Set the model state to the ClientFixShipState
            synchronized (this.model) {
                this.model.setState(new ClientFixShipState(this.model, state));
            }

            this.ui.showShipFixing(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(PopulateShipDTO state) throws Exception {
        // Set the model state to the ClientPopulateShipState
        synchronized (this.model) {
            this.model.setState(new ClientPopulateShipState(this.model, state));
        }

        this.ui.showShipPopulate(state);
    }

    @Override
    public void visit(CardRoundDTO state) throws Exception {
        try {
            synchronized (this.model) {
                this.update(state);

                this.model.setState(new ClientCardRoundState(this.model, state));
            }

            this.ui.showCardRound(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invokes the {@code update(CardRoundDTO state)} method and updates the ui visuals (GUI)
     */
    public void updateCardResult(CardRoundDTO state) throws Exception {
        try {
            this.update(state);
            // Update the component in the GUI
            this.ui.updateVisuals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the client's model with the provided `CardRoundDTO` state.
     * This includes updating the client board, client ships, and client players
     * if the corresponding flags in the `CardRoundDTO` indicate updates are required.
     * The method is synchronized on the model to ensure thread safety during updates.
     *
     * @param state the `CardRoundDTO` object containing the data necessary to update
     *              the client's model, including board, card information, and potential updates
     *              for positions, ships, players, and other components.
     */
    private void update(CardRoundDTO state) {
        synchronized (this.model) {
            if (!(this.model.getState() instanceof ClientCardRoundState)) {
                if (state.getBoard() != null) { // Skip in case of reconnection
                    this.model.setClientBoard(new ClientBoard(state.getBoard(), model));
                }
            }

            // Updates the ClientBoard if necessary (Positions, EliminatedPlayers)
            if(state.getCardInfo().getNeedsBoardUpdate()) {
                this.model.getClientBoard().updateBoard(state.getCardInfo(), this.ui);
            }

            // Updates the ClientShips if necessary (Removed Components, Batteries, Dropped/Taken Resources, Removed Lifeforms)
            if (state.getCardInfo().getNeedsShipUpdate()) {
                this.model.updateShips(state.getCardInfo());
            }

            // Updates the ClientPlayers' info if necessary (Credits)
            if(state.getCardInfo().getNeedsPlayerUpdate()) {
                this.model.updatePlayers(state.getCardInfo());
            }
        }
    }

    @Override
    public void visit(EndGameDTO state) {
        this.ui.showEndGame(state);
    }

    @Override
    public void visit(DisconnectedPlayerDTO state) {
        System.out.println();

        new WidgetTUI()
                .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor(state.getNickname() + " disconnected from the game.", ANSIColors.BRIGHT_MAGENTA))
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder()
                .printWidget();
    }

    @Override
    public void visit(InsufficientPlayerDTO state) {
        this.ui.showInsufficientPlayer(state);
    }

    /**
     * Reports an error by displaying the provided error message to the user interface.
     * The method wraps the message inside an {@code ErrorAnswer} object and delegates
     * to the user interface to handle its display.
     *
     * @param message the error message to be displayed to the user
     */
    public void reportError(String message) {
        this.ui.showError(new ErrorAnswer(message));
    }

    /**
     * Determines whether the command context for invoking network callbacks is set.
     *
     * @return {@code true} if the client has an active command context; {@code false} otherwise
     */
    public boolean isCTXAvailable() {
        return this.ui.isCTXAvailable();
    }

    /**
     * Executes the command submission for the specified player. This method triggers
     * the associated onSuccess task in the active command context for the provided player nickname.
     *
     * @param playerNickname The nickname of the player whose onSuccess task needs to be executed.
     */
    public void commitCommand(String playerNickname) {
        this.ui.commitCommand(playerNickname);
    }

    /**
     * Interrupts the current screen by delegating the action to the user interface.
     * Typically used when waiting for user input, this method cancels the input request,
     * allowing the application to transition to the next state.
     */
    public void interruptCurrScreen() {
        this.ui.interruptCurrScreen();
    }
}
