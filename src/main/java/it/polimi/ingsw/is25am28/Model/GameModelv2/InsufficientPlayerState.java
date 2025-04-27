package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Timer.HourGlass;
import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObserver;

import java.util.ArrayList;
import java.util.List;

public final class InsufficientPlayerState extends State implements TimerObserver {
    public static int countdownMillis = 90 * 1000;

    // This is the previous game state that was active before we entered the "InsufficientPlayerState"
    private final State prevState;
    private final HourGlass countdown;

    public InsufficientPlayerState(GameModel model, State prevState) {
        super(model);
        this.prevState = prevState;

        // The countdown will last for one and half minute
        this.countdown = new HourGlass(1, countdownMillis);

        // Subscribe the state to the countdown
        this.countdown.addTimerSubscriber(this);

        // Start the countdown
        this.countdown.flip();
    }

    /**
     * Unsubscribe the
     * */
    private void removeTimer() {
        if (this.countdown != null) {
            this.countdown.removeTimerSubscriber(this);
        }
    }

    /**
     * onComplete() method will be triggered from the model once a player reconnects to the game.
     * It will check if there are at least 2 players connected to resume the game, otherwise it won't make any state
     * transition
     * */
    @Override
    public void onComplete() {
        this.tryResumeOrEndGame();
    }

    /**
     * Once the timer has ended if there is only one player connected we need to mark him as the winner
     * */
    @Override
    public void onTimerEnd() {
        this.tryResumeOrEndGame();
    }

    private void tryResumeOrEndGame() {
        List<Player> connectedPlayers = this.model.getPlayers().values().stream()
                .filter(Player::isConnected)
                .toList();

        if (connectedPlayers.size() == 1) {
            this.model.setCurrentState(new EndGameState(this.model, connectedPlayers.getFirst().getNickname()));

            Answer answer = new Answer()
                    .setPlayerNickname(connectedPlayers.getFirst().getNickname())
                    .setState(this.model.generateState());

            this.model.broadCastUpdate(answer);
        } else if (connectedPlayers.size() > 1) {
            this.removeTimer();
            this.model.setCurrentState(prevState);
        }
    }


    @Override
    public StateDTO generateState() {
        InsufficientPlayerDTO state = new InsufficientPlayerDTO()
                .setCountdown(countdownMillis);

        state.setStateName(this.toString());
        return state;
    }
}
