package it.polimi.ingsw.is25am28.Client.UI.TUI.Input;

import java.io.IOException;

public class InputThread extends Thread {
    private final Object inputLock;
    private final StringBuilder buffer;
    private String line;
    private boolean isReadingEnable;
    private boolean hasBeenForced;

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

                    // If there was ha force request we need to stop to read from the input
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
     * Will set the hasBeenForce flag to true in order to interrupt the current reading request
     * */
    public void interruptInputReader() {
        synchronized (this.inputLock) {
            this.hasBeenForced = true;
            this.isReadingEnable = false;
            this.inputLock.notifyAll();
        }
    }

    /**
     * @return a String that contains the input inputted by the client.
     * The string will be null if the screen was forced to quit
     * <br>
     * IMPORTANT --> IF NULL IN THE CHECK WE NEED TO RETURN THE METHOD IN THE SCREEN --> WILL END THE VISUALIZATION
     * */
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