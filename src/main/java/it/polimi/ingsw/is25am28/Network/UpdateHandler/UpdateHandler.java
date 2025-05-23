package it.polimi.ingsw.is25am28.Network.UpdateHandler;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateHandler {
    private final ClientModel model;
    private final ViewUpdater viewUpdater;
    private final ExecutorService updateThread;
    private final ExecutorService inputThread;
    private final ExecutorService forceThread;

    public UpdateHandler(ClientModel model, ViewUpdater viewUpdater) {
        this.model = model;
        this.viewUpdater = viewUpdater;
        this.inputThread = Executors.newSingleThreadExecutor();
        this.updateThread = Executors.newSingleThreadExecutor();
        this.forceThread = Executors.newSingleThreadExecutor();
    }

    // ========== MAIN METHOD ========== //

    public void processUpdate(Answer answer) {
        StateDTO state = answer.getState();
        StateDTO nextState = answer.getNextState();
        String nickname = answer.getPlayerNickname();

        // Init the future that will be used in the methods to create a sequential update flow
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        // Handle the first state (current state or updates)
        switch (state) {
            case ConstructionComponentDTO _, PlacedComponentDTO _, TimerDTO _, PopulateShipComponentDTO _, ConstructionDeckDTO _ -> {
                future = this.acceptState(future, state, this.updateThread, "Error while executing the " + state.getStateName() + " update");
                future = this.commitCmd(future, nickname, this.inputThread);
            }
            case PlayerEndedShipDTO _ -> {
                future = this.acceptState(future, state, this.updateThread, "Error while executing the " + state.getStateName() + " update");
                if (nextState == null) {
                    future = this.commitCmd(future, nickname, this.inputThread);
                }
            }
            case DisconnectedPlayerDTO _ -> {
                future = acceptState(future, state, this.updateThread, "Error while executing the " + state.getStateName() + " update");
            }
            case ReconnectDTO data -> {
                future = acceptState(future, state, this.updateThread, "Error while executing the " + state.getStateName() + " update");

                // If the player is the one that reconnects to the game, or we were in the insufficient player state, we need to update the screen
                if (data.getWasInsufficientState() || data.getTargetNickname().equals(this.model.getNickname())) {

                    future = acceptState(future, nextState, this.inputThread, "Error while executing the " + state.getStateName() + " update");
                }

                nextState = null;
            }
            case CardRoundDTO cardData -> {
                if (nextState != null) {
                    future = this.updateCardResult(future, cardData);
                    future = this.interruptScreen(future);
                } else {
                    future = this.interruptScreen(future);
                }
                future = this.commitCmd(future, nickname, inputThread);
                if (nextState == null) {
                    future = this.acceptState(future, state, inputThread, "Error while executing the " + state.getStateName() + " input");
                }
            }
            case null -> {
                future = this.commitCmd(future, nickname, inputThread);
            }
            default -> {
                future = this.commitCmd(future, nickname, inputThread);
                future = this.acceptState(future, state, inputThread, "Error while executing the " + state.getStateName() + " input");
            }
        }

        // Handle the next state
        switch (nextState) {
            case InsufficientPlayerDTO _ -> {
                future = this.acceptState(future, nextState, forceThread, "Error while executing the " + nextState.getStateName() + " force quit");
            }
            case FixShipDTO _, PopulateShipDTO _, EndGameDTO _, CardRoundDTO _ -> {
                future = this.interruptScreen(future);
                future = this.acceptState(future, nextState, inputThread, "Error while executing the " + nextState.getStateName() + " input");
            }
            case null -> {}
            default -> {
                future = this.acceptState(future, nextState, inputThread, "Error while executing the " + nextState.getStateName() + " input");
            }
        }
    }

    public void reportErrorUpdate(ErrorAnswer error) {
        inputThread.submit(() -> {
            viewUpdater.reportError(error.getError());
        });
    }

    // ========== HELPER METHODS ========== //

    /**
     * This method will accept the state to invoke the visitor and make the state actions
     * @param future the current flow future
     * @param state the state that will be used in the visitor
     * @param executor the thread where is desired to execute the visitor
     * @param errorPrefix the error prefix that is desired to input before the caught error msg
     * */
    private CompletableFuture<Void> acceptState(CompletableFuture<Void> future, StateDTO state, ExecutorService executor, String errorPrefix) {
        if (state == null) return future;
        return future.thenRunAsync(() -> {
            try {
                state.accept(this.viewUpdater);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    /**
     * This method will try to commit the message sent to the server
     * @param future the current flow future
     * @param nickname the player nickname, stored in the Answer
     * @param executor the thread where is desired to execute the commit
     * */
    private CompletableFuture<Void> commitCmd(CompletableFuture<Void> future, String nickname, ExecutorService executor) {
        if (nickname != null && this.viewUpdater.isCTXAvailable()) {
            return future.thenRunAsync(() -> {
                try {
                    this.viewUpdater.commitCommand(nickname);
                } catch (Exception e) {
                    throw new RuntimeException("Error while commiting the command for " + nickname + ": ", e);
                }
            }, executor);
        }

        return future;
    }

    /**
     * This method will be used to interrupt the current screen when a state transition occurred on the server
     * @param future the current flow future
     * */
    private CompletableFuture<Void> interruptScreen(CompletableFuture<Void> future) {
        return future.thenRunAsync(() -> {
            try {
                this.viewUpdater.interruptCurrScreen();
            } catch (Exception e) {
                throw new RuntimeException("Error while interrupting the screen: ", e);
            }
        }, this.forceThread);
    }

    /**
     * This method will update the result of a played card
     * @param future the current flow future
     * @param cardData the card round information to update the client model
     * */
    private CompletableFuture<Void> updateCardResult(CompletableFuture<Void> future, CardRoundDTO cardData) {
        return future.thenRunAsync(() -> {
            try {
                this.viewUpdater.updateCardResult(cardData);
            } catch (Exception e) {
                throw new RuntimeException("Error while updating card results: ", e);
            }
        }, this.updateThread);
    }
}
