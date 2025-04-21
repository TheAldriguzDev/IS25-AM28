package it.polimi.ingsw.is25am28.Timer.TimerObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class TimerObservable {
    protected List<TimerObserver> timerSubscribers;

    // Constructor
    public TimerObservable() {
        this.timerSubscribers = new ArrayList<>();
    }

    /**
     * @param subscriber The subscriber to add to this subscriber list
     */
    public void addTimerSubscriber(TimerObserver subscriber) {
        this.timerSubscribers.add(subscriber);
    }

    /**
     * @param subscriber The subscriber to remove from this subscriber list
     * @return TRUE if the given subscriber was present and successfully removed
     *              from the subscriber list, FALSE otherwise.
     */
    public boolean removeTimerSubscriber(TimerObserver subscriber) {
        return this.timerSubscribers.remove(subscriber);
    }

    /**
     * Triggers an update to all the subscribers by calling the
     * onTimerEnd method on all of them, one at a time.
     */
    protected void onTimerEnd() {
        for (TimerObserver subscriber : this.timerSubscribers) {
            subscriber.onTimerEnd();
        }
    }
}
