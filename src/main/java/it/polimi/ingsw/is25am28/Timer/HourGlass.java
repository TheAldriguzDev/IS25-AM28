package it.polimi.ingsw.is25am28.Timer;

import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObservable;

import javax.management.timer.Timer;

public class HourGlass extends TimerObservable {
    // A user timed the hourglass duration to yield an exact value (1m 37.5s)
    // (Source: https://boardgamegeek.com/thread/1023566/timer-length)
    public static long DEFAULT_DURATION_IN_MILLIS = Timer.ONE_MINUTE + (37L * Timer.ONE_SECOND) + 500L;

    private long flipCounter;
    private boolean isTimeFlowing;
    public Thread timerThread;

    // Constructor to create a custom duration hourglass
    public HourGlass(long flipCount, long durationInMillis) {
        super();
        this.flipCounter = flipCount;
        this.isTimeFlowing = false;
        this.initTimerThread(durationInMillis);
    }

    // Constructor
    public HourGlass(int difficultyLevel) {
        if (difficultyLevel >= 0 && difficultyLevel <= 3) {
            // In the game, the difficultyLevel corresponds to the amount
            // of times the hourglass can be restarted by flipping it
            // (NOTE: difficultyLevel 0 is the Learner mode)
            this.flipCounter = difficultyLevel;
        }
        else {
            // Otherwise, the hourglass can be flipped infinitely
            this.flipCounter = Long.MAX_VALUE;
        }

        // Initially, the hourglass is not flipped
        this.isTimeFlowing = false;

        // Initializes the thread with the default duration
        this.initTimerThread(HourGlass.DEFAULT_DURATION_IN_MILLIS);
    }

    /**
     * Initializes a busy-sleeping thread that sleeps for the given duration.
     * @param durationInMillis The actual duration of the thread sleeping period
     */
    public void initTimerThread(long durationInMillis) {
        // This is the thread that sleeps for the set duration, mimicking
        // the time flow that would occur when using an hourglass
        this.timerThread = new Thread(
            () -> {
                long startTime = System.currentTimeMillis();
                long endTime = durationInMillis + startTime;
                long deltaTime;

                // Time will be flowing once the thread has been started
                this.isTimeFlowing = true;

                while (System.currentTimeMillis() < endTime) {
                    deltaTime = endTime - System.currentTimeMillis();

                    try {
                        Thread.sleep(deltaTime);
                    }
                    catch (InterruptedException e) {
                        // An interrupt stops the thread momentarily, but then
                        // it will restart sleeping for the remaining time
                        System.out.println("INTERRUPTED");
                    }
                }

                // When the timer ends, set the flowing time flag to false
                // and notify all subscribers about the timer end event
                this.isTimeFlowing = false;
                this.onTimerEnd();
            }
        );
    }

    /**
     * Flips the hourglass to restart the timer if it can still be flipped
     * and only if the previous time has already passed
     * @return TRUE if the hourglass is flipped, FALSE otherwise
     */
    public boolean flip() {
        if (this.timerThread != null && !this.timerThread.isAlive()) {
            if (this.flipCounter > 0 && !this.isTimeFlowing) {
                this.flipCounter--;
                this.timerThread.start();
                return true;
            }
        }

        return false;
    }

    /**
     * @return The remaining times that this hourglass can be flipped again
     */
    public long getRemainingFlips() {
        return this.flipCounter;
    }
}
