package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Timer.HourGlass;
import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObserver;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class InsufficientPlayerState extends State implements TimerObserver {
    private final int countdownMillis = 120 * 1000;
    private final AtomicInteger currentCountdown = new AtomicInteger(countdownMillis / 1000);
    private final ScheduledExecutorService countDownScheduler = Executors.newSingleThreadScheduledExecutor();

    // This is the previous game state that was active before we entered the "InsufficientPlayerState"
    private final State prevState;
    private final HourGlass countdown;

    private boolean isTimerEnded;


    // Constructor
    public InsufficientPlayerState(GameModel model, State prevState) {
        super(model);
        this.prevState = prevState;

        // The countdown will last for one and half minute
        this.countdown = new HourGlass(1, countdownMillis);
        this.startCountDown();

        // Subscribe the state to the countdown
        this.countdown.addTimerSubscriber(this);

        this.isTimerEnded = false;

        // Start the countdown
        this.countdown.flip();
    }

    /**
     * Unsubscribe the countdown
     * */
    private void removeTimer() {
        if (this.countdown != null) {
            this.countdown.removeTimerSubscriber(this);
        }
    }

    private void startCountDown() {
        countDownScheduler.scheduleAtFixedRate(currentCountdown::decrementAndGet, 1, 1, TimeUnit.SECONDS);
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
        this.isTimerEnded = true;
        this.tryResumeOrEndGame();
    }

    private void tryResumeOrEndGame() {
        List<Player> connectedPlayers = this.model.getPlayers().values().stream()
                .filter(Player::isConnected)
                .toList();

        // If the timer has ended and the number of connected players is less or equal to 1 we switch to the EndGameState
        if (connectedPlayers.size() <= 1 && isTimerEnded) {
            this.model.setCurrentState(new EndGameState(this.model, !connectedPlayers.isEmpty() ? connectedPlayers.getFirst().getNickname() : null));

            Answer answer = new Answer()
                    .setPlayerNickname(!connectedPlayers.isEmpty() ? connectedPlayers.getFirst().getNickname() : "No players were connected")
                    .setState(this.model.generateState());

            this.model.broadCastUpdate(answer);
        }
        // After a client reconnect, if the number of player is enough for playing we can resume the game
        else if (connectedPlayers.size() > 1) {
            this.removeTimer();
            this.model.setCurrentState(prevState);
        }
    }

    @Override
    public StateDTO generateState() {
        InsufficientPlayerDTO state = new InsufficientPlayerDTO()
                .setCountdown(this.currentCountdown.get() * 1000);

        state.setStateName(this.toString());
        return state;
    }
}
