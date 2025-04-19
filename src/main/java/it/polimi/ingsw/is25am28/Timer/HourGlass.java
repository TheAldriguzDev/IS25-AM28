package it.polimi.ingsw.is25am28.Timer;

import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObservable;

import javax.management.timer.Timer;

public class HourGlass extends TimerObservable {
    // A user timed the hourglass duration to yield an exact value (1m 37.5s)
    // (Source: https://boardgamegeek.com/thread/1023566/timer-length)
    public static long DEFAULT_DURATION_IN_MILLIS = Timer.ONE_MINUTE + (37L * Timer.ONE_SECOND) + 500L;

    private long remainingFlips;
    private long durationInMillis;
    private long remainingDurationInMillis;
    private Thread timerThread;

    // Constructor to create a custom duration hourglass
    public HourGlass(long remainingFlips, long durationInMillis) {
        // super initializes the list of subscribers
        super();

        this.setRemainingFlips(remainingFlips);
        this.setDurationInMillis(durationInMillis);
        this.initTimerThread();
    }

    // Constructor
    public HourGlass(int difficultyLevel) {
        // super initializes the list of subscribers
        super();

        if (difficultyLevel >= 0 && difficultyLevel <= 3) {
            // In the game, the difficulty level corresponds
            // to the amount of flips available
            this.remainingFlips = difficultyLevel;
        }
        else {
            this.remainingFlips = Long.MAX_VALUE;
        }

        this.setDurationInMillis(HourGlass.DEFAULT_DURATION_IN_MILLIS);
        this.initTimerThread();
    }

    /**
     * @return The remaining times that this hourglass can be flipped again
     */
    public long getRemainingFlips() {
        return this.remainingFlips;
    }

    /**
     * Sets this timer's number of available flips
     * If <code>flipCount < 0</code>, then the timer is set at 0 remaining flips
     */
    public void setRemainingFlips(long flipCount) {
        this.remainingFlips = Math.max(0, flipCount);
    }

    /**
     * @return This timer's duration (in milliseconds)
     */
    public long getDurationInMillis() {
        return this.durationInMillis;
    }

    /**
     * Sets this timer's duration (in milliseconds) and regenerates the remaining duration attribute.
     * If <code>durationInMillis < 0</code>, then the timer is set at 0 duration
     */
    public void setDurationInMillis(long durationInMillis) {
        this.durationInMillis = Math.max(0, durationInMillis);
        this.remainingDurationInMillis = this.durationInMillis;
    }

    /**
     * Initializes a thread that sleeps for the set duration, thus mimicking the behavior of a timer.
     * If this thread is interrupted, it will try to restart itself up by creating a new thread that
     * will invoke start on the timer thread.
     */
    public void initTimerThread() {
        // Instantiating the timer thread that will sleep for the set duration

        this.timerThread = new Thread(
            () -> {
                long startTime = System.currentTimeMillis();

                try {
                    if (this.remainingDurationInMillis > 0) {
                        // More accurate measurement of the startTime
                        // (since it's done right after any overhead caused by the try-catch block)
                        startTime = System.currentTimeMillis();
                        Thread.sleep(this.remainingDurationInMillis);

                        // When the timer ends, notify all subscribers about the timer end event
                        this.onTimerEnd();
                    }
                }
                catch (InterruptedException e) {
                    // The timer attempts to restart itself after being interrupted
                    // by sleeping off the remaining time

                    this.remainingDurationInMillis -= (System.currentTimeMillis() - startTime);

                    // This thread will restart the timer thread and then instantly terminate
                    // (NOTE: The timer thread MUST be recreated, since after an interrupt the
                    //        thread is in state "TERMINATED", thus it cannot be restarted)
                    new Thread(
                        () -> {
                            this.initTimerThread();
                            this.timerThread.start();
                        }
                    ).start();
                }
            }
        );
    }

    /**
     * Flips the hourglass to restart the timer if it can still be flipped
     * and only if the previous time has already passed
     *
     * @return TRUE if the hourglass is flipped, FALSE otherwise
     */
    public boolean flip() {
        if (this.remainingFlips > 0) {
            if (this.timerThread != null && !this.timerThread.isAlive()) {
                this.remainingFlips--;
                this.timerThread.start();
                return true;
            }
        }

        return false;
    }
}
