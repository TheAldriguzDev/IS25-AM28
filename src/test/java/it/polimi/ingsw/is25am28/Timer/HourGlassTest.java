package it.polimi.ingsw.is25am28.Timer;

import it.polimi.ingsw.is25am28.Timer.TimerObserver.TimerObserver;
import org.junit.jupiter.api.Test;

import javax.management.timer.Timer;

import static org.junit.jupiter.api.Assertions.*;

class HourGlassTest {

    // Mockup timer subscriber
    protected static class HourGlassSubscriber implements TimerObserver {
        private long invocationTime = Long.MIN_VALUE;

        // Constructor
        protected HourGlassSubscriber() {}

        /**
         * Sets the time at which it gets invoked to
         * understand if the timer works as intended
         */
        @Override
        public void onTimerEnd() {
            this.invocationTime = System.currentTimeMillis();
        }

        /**
         * @return The time (in millis) when the onTimerEnd is
         *         invoked by the timer to which this subscriber is
         *         currently subscribed
         */
        public long getInvocationTime() {
            return this.invocationTime;
        }
    }

    @Test
    void testHourGlassDurationLevel2() {
        // REMOVED FOR WHEN ALL TESTS ARE RUN, BELOW THERE'S STILL
        // THE SAME TEST BUT WITH A MUCH SHORTER DURATION (2s (test below) vs. 1m 37.5s (this test))
        /*
        long durationInMillis = HourGlass.DEFAULT_DURATION_IN_MILLIS;

        HourGlass hourGlass = new HourGlass(2);
        HourGlassSubscriber subscriber1 = new HourGlassSubscriber();
        HourGlassSubscriber subscriber2 = new HourGlassSubscriber();
        HourGlassSubscriber subscriber3 = new HourGlassSubscriber();

        // Registering  mockup subscribers to the hourglass
        hourGlass.addTimerSubscriber(subscriber1);
        hourGlass.addTimerSubscriber(subscriber2);
        hourGlass.addTimerSubscriber(subscriber3);

        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        long deltaTime;

        // Flipping the hourglass once
        assertTrue(hourGlass.flip());

        // Waiting the thread to stop
        try {
            // +200 ms to ensure the end of the other thread
            Thread.sleep(HourGlass.DEFAULT_DURATION_IN_MILLIS + 200);
            endTime = System.currentTimeMillis();
        }
        catch (InterruptedException e) {
            fail("INTERRUPTED TEST THREAD");
        }

        deltaTime = endTime - startTime;

        System.out.println("START: " + startTime + " | END: " + endTime + " | ACTUAL_DELTA: " + deltaTime + ", IDEAL_DELTA: " + durationInMillis);

        // Printing the time in millis when each subscriber got called by the hourglass onTimeEnd method
        System.out.println("Sub1 updated after: " + (subscriber1.getInvocationTime() - startTime) + "ms");
        System.out.println("Sub2 updated after: " + (subscriber2.getInvocationTime() - startTime) + "ms");
        System.out.println("Sub3 updated after: " + (subscriber3.getInvocationTime() - startTime) + "ms");

        assertNotEquals(Long.MIN_VALUE, subscriber1.getInvocationTime());
        assertNotEquals(Long.MIN_VALUE, subscriber2.getInvocationTime());
        assertNotEquals(Long.MIN_VALUE, subscriber3.getInvocationTime());
         */
    }

    @Test
    void testHourGlassCustomDuration() {
        long durationInMillis = Timer.ONE_SECOND;

        HourGlass hourGlass = new HourGlass(1, durationInMillis);
        HourGlassSubscriber subscriber1 = new HourGlassSubscriber();
        HourGlassSubscriber subscriber2 = new HourGlassSubscriber();
        HourGlassSubscriber subscriber3 = new HourGlassSubscriber();

        // Registering  mockup subscribers to the hourglass
        hourGlass.addTimerSubscriber(subscriber1);
        hourGlass.addTimerSubscriber(subscriber2);
        hourGlass.addTimerSubscriber(subscriber3);

        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        long deltaTime;

        // Flipping the hourglass once
        assertTrue(hourGlass.flip());

        // Waiting the thread to stop
        try {
            // +200 ms to ensure the end of the other thread
            Thread.sleep(durationInMillis + 200L);
            endTime = System.currentTimeMillis();
        }
        catch (InterruptedException e) {
            fail("ERROR: INTERRUPTED TEST THREAD");
        }

        deltaTime = endTime - startTime;

//        System.out.println("START: " + startTime + " | END: " + endTime + " | ACTUAL_DELTA: " + deltaTime + ", IDEAL_DELTA: " + durationInMillis);
//
//        // Printing the time in millis when each subscriber got called by the hourglass onTimeEnd method
//        System.out.println("Sub1 updated after: " + (subscriber1.getInvocationTime() - startTime) + "ms");
//        System.out.println("Sub2 updated after: " + (subscriber2.getInvocationTime() - startTime) + "ms");
//        System.out.println("Sub3 updated after: " + (subscriber3.getInvocationTime() - startTime) + "ms");

        // 10ms is the max delay for the timer thread
        long delayThreshold = 10L;

        assertNotEquals(Long.MIN_VALUE, subscriber1.getInvocationTime());
        assertNotEquals(Long.MIN_VALUE, subscriber2.getInvocationTime());
        assertNotEquals(Long.MIN_VALUE, subscriber3.getInvocationTime());

        assertTrue((subscriber1.getInvocationTime() >= durationInMillis && (subscriber1.getInvocationTime() - startTime) - durationInMillis <= delayThreshold));
        assertTrue((subscriber2.getInvocationTime() >= durationInMillis && (subscriber2.getInvocationTime() - startTime) - durationInMillis <= delayThreshold));
        assertTrue((subscriber3.getInvocationTime() >= durationInMillis && (subscriber3.getInvocationTime() - startTime) - durationInMillis <= delayThreshold));
    }

    @Test
    void testHourGlassConsumedAllFlips() {
        long durationInMillis = Timer.ONE_SECOND;
        HourGlass hourGlass = new HourGlass(1, durationInMillis);

        assertTrue(hourGlass.flip());

        try {
            // +200 ms to ensure the end of the other thread
            Thread.sleep(durationInMillis + 200L);
        }
        catch (InterruptedException e) {
            fail("INTERRUPTED TEST THREAD");
        }

        assertFalse(hourGlass.flip());
    }
}