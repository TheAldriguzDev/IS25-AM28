package it.polimi.ingsw.is25am28.Network.Queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue implements Runnable {
    private final BlockingQueue<Runnable> queue;

    public Queue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Append the lambda function to the end of queue
     * */
    public void enqueue(Runnable r) {
        synchronized (this.queue) {
            this.queue.offer(r);
        }
    }

    /**
     * Method used to process the Runnable that are stored in the queue
     * */
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
