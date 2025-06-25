package it.polimi.ingsw.is25am28.Client.UI.TUI.Input;

import java.io.IOException;

/**
 * A specialized thread for handling user input from {@code System.in}.
 * Reads input character by character, capturing complete input strings when a newline is encountered.
 * <br>
 * Provides thread-safe methods to control input behavior, allowing input interruption or waiting for user input.
 * Particularly useful for force-quitting the input screen to display new game states,
 * even when the thread is waiting for user input.
 */
public class InputThread extends Thread {
    private final Object inputLock;
    private final StringBuilder buffer;
    private String line;
    private boolean isReadingEnable;
    private boolean hasBeenForced;

    // Constructor
    public InputThread() {
        this.inputLock = new Object();
        this.buffer = new StringBuilder();
        this.line = null;
        this.isReadingEnable = false;
        this.hasBeenForced = false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int ch = System.in.read();
                synchronized (this.inputLock) {
                    // System.in has been closed --> stop reading from the source
                    if (ch == -1) {
                        break;
                    }

                    // If there was a force request we need to stop to read from the input
                    if (this.hasBeenForced) {
                        this.line = null;
                        this.buffer.setLength(0);
                        this.inputLock.notifyAll();
                        continue;
                    }

                    // If the reading is not enable, ignore the char given in input
                    if (!this.isReadingEnable) {
                        continue;
                    }

                    // If we encounter the newLine char we notify the caller about the end of the input phase
                    // otherwise we add the char to the buffer
                    if (ch == '\n') {
                        this.line = this.buffer.toString();
                        this.buffer.setLength(0);
                        this.isReadingEnable = false;
                        this.inputLock.notifyAll();
                    } else {
                        this.buffer.append((char) ch);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Forces the interruption of the current input reading request.
     * Sets the internal flag to stop reading and notifies all waiting threads.
     */
    public void interruptInputReader() {
        synchronized (this.inputLock) {
            this.hasBeenForced = true;
            this.isReadingEnable = false;
            this.inputLock.notifyAll();
        }
    }

    /**
     * Requests user input from another thread. If input is not yet available, the calling thread
     * waits to be notified by the {@code InputThread}. Returns the input once available,
     * or {@code null} if the request was interrupted.
     *
     * @return the user input string, or {@code null} if interrupted
     */
    public String waitForInput() throws InterruptedException {
        synchronized (this.inputLock) {
            // Attributes reset to handle a new input request
            this.isReadingEnable = true;
            this.line = null;
            this.hasBeenForced = false;

            while (this.line == null && !this.hasBeenForced) {
                this.inputLock.wait();
            }

            if (this.hasBeenForced) {
                this.hasBeenForced = false;
                return null;
            }

            String input = this.line;
            this.line = null;
            return input;
        }
    }
}