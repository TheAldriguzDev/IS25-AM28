package it.polimi.ingsw.is25am28.Network.Queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread-safe task queue that executes Runnable tasks sequentially.
 * This class uses a blocking queue to store tasks and processes them one at a time.
 * It is intended to run continuously in its own thread, executing tasks as they are enqueued.
 *
 * Primarily used to make RMI communication asynchronous and prevent UI blocking
 * when sending messages between client and server.
 */

public class Queue implements Runnable {
    private final BlockingQueue<Runnable> queue;

    public Queue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Adds a task to the queue for processing. The task is represented as a {@code Runnable}
     * and will be executed sequentially when dequeued.
     *
     * @param r the {@code Runnable} task to be added to the queue for execution
     */
    public void enqueue(Runnable r) {
            this.queue.offer(r);
    }

    /**
     * Continuously takes and executes {@code Runnable} tasks from the blocking queue.
     * If the thread is interrupted, it restores the interrupt status and stops execution.
     */
    public void run() {
        while (true) {
            try {
                Runnable exec = this.queue.take();
                exec.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Error while reading from the network queue");
                return;
            }
        }
    }
}
