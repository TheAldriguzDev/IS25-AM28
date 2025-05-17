package it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleWidgetTUI extends WidgetTUI {
    public static final String TIMESTAMP_FORMAT = "MMM dd, HH:mm:ss";
    public static final String LINE_START_SYMBOL = ">" + PrintUtils.SPACE;
    private final int maxHeight;
    private final int maxWidth;
    private boolean areTimestampsEnabled;

    // Constructor
    public ConsoleWidgetTUI(int maxHeight, int maxWidth) {
        super();
        this.maxHeight = Math.max(0, maxHeight);
        this.maxWidth = Math.max(0, maxWidth);
        this.setHeight(this.maxHeight);
        this.setWidth(this.maxWidth);
        this.areTimestampsEnabled = false;
    }

    /**
     * Enables the addition of timestamps to future messages
     */
    public void enableTimestamps() {
        this.areTimestampsEnabled = true;
    }

    /**
     * Disables the addition of timestamps to future messages
     */
    public void disableTimestamps() {
        this.areTimestampsEnabled = false;
    }

    /**
     * @return If this console widget will put timestamps before each newly added message
     */
    public boolean areTimestampsEnabled() {
        return this.areTimestampsEnabled;
    }

    /**
     * @param screen The console screen to append after this console widget's screen
     */
    @Override
    public WidgetTUI appendScreen(List<String> screen) {
        if (screen != null) {
            for (String line : screen) {
                this.appendString(line);
            }
        }

        return this;
    }

    /**
     * @param sender The cardName of the entity to whom the given message belongs
     * @param message The message to add to the console
     */
    public WidgetTUI appendStringWithSender(String sender, String message) {
        return this.appendString(sender + PrintUtils.SPACE + message);
    }

    /**
     * @param string The string containing the console message to
     *               append to this console widget
     */
    @Override
    public WidgetTUI appendString(String string) {
        if (string != null) {
            int tmpBorderCount = this.borderCount;

            // Un-wrapping the console widget of all the current border layers
            for (int i = 0; i < this.borderCount; i++) {
                this.unwrapWidgetFromBorder();
            }

            // Removing any UNICODE characters from the string
            // (They can break how it looks if the string splitting is done exactly
            //  inside one of these characters (colors included))
            string = PrintUtils.removeUnicodeFromString(string);

            // Appending the timestamps if the homonymous flag is enabled
            if (this.areTimestampsEnabled) {
                string = this.getTimestamp() + string;
            }

            // Appending the line start symbol to the message
            string = LINE_START_SYMBOL + string;

//            // Partitioning the string into substrings of length maxWidth to
//            // make them fit into the console widget
//            int numOfLineBreaks = (PrintUtils.removeUnicodeFromString(string).length() / this.maxWidth) + 1;
//
//            for (int i = 0; i < numOfLineBreaks; i++) {
//                String partitionedString = string.substring((i) * this.maxWidth, Math.min(string.length(), (i + 1) * this.maxWidth));
//                this.screen.add(partitionedString);
//                this.height++;
//            }

            // NOTE: The string got all UNICODE characters removed before
            String wordSeparator = PrintUtils.SPACE;
            List<String> words = Arrays.stream(string.split(wordSeparator)).toList();
            List<String> wordLines = new ArrayList<String>();
            StringBuilder partitionedMessage = new StringBuilder();

            // Splitting those words that are still too long to fit on a single line
            for (String word : words) {
                // The first word is split by taking into account the presence of the line start symbol
                int numOfLineBreaks = (word.length() / this.maxWidth) + 1;

                if (numOfLineBreaks > 1) {
                    for (int j = 0; j < numOfLineBreaks; j++) {
                        wordLines.add(word.substring((j) * this.maxWidth, Math.min(word.length(), (j + 1) * this.maxWidth)));
                    }
                }
                else {
                    wordLines.add(word);
                }
            }

            // Creating and adding the console lines by concatenating the maximum
            // amount of strings that can fit on a single line
            for (String line : wordLines) {
                if (partitionedMessage.length() + line.length() >= this.maxWidth) {
                    this.screen.add(partitionedMessage.toString());
                    this.height++;
                    partitionedMessage = new StringBuilder();
                }

                partitionedMessage.append(line);

                if (partitionedMessage.length() < this.maxWidth) {
                    partitionedMessage.append(wordSeparator);
                }
            }

            if (!partitionedMessage.isEmpty()) {
                this.screen.add(partitionedMessage.toString());
                this.height++;
            }

//            // Splitting the string into words separated by spaces
//            String wordSeparator = " ";
//            String[] words = string.split(wordSeparator);
//            StringBuilder partitionedMessage = new StringBuilder();
//
//            for (String word : words) {
//                // If a single word is still longer than the width, then it will
//                // be forcefully split to make it fit on multiple lines
//                int numOfLineBreaks = (PrintUtils.removeUnicodeFromString(word).length() / this.maxWidth) + 1;
//                String[] wordLines = new String[numOfLineBreaks];
//
//                for (int i = 0; i < numOfLineBreaks; i++) {
//                    wordLines[i] = word.substring((i) * this.maxWidth, Math.min(word.length(), (i + 1) * this.maxWidth));
//                }
//
//                for (String line : wordLines) {
//                    if (PrintUtils.removeUnicodeFromString(partitionedMessage.toString()).length() + PrintUtils.removeUnicodeFromString(line).length() < this.maxWidth) {
//                        partitionedMessage.append(line).append(wordSeparator);
//                    }
//                    else {
//                        this.screen.add(partitionedMessage.toString());
//                        this.height++;
//                        partitionedMessage = new StringBuilder();
//                        partitionedMessage.append(line).append(wordSeparator);
//                    }
//                }
//            }
//
//            if (!partitionedMessage.isEmpty()) {
//                this.screen.add(partitionedMessage.toString());
//                this.height++;
//            }

            // Removing any excess lines that are "pushed" outside the set console height
            // (i.e.: old messages, which are at the top, are deleted)
            int excessLinesToDelete = this.height - this.maxHeight;

            // Removing any old console logs at the top of the screen (if there are any)
            if (excessLinesToDelete > 0) {
                this.screen = new ArrayList<>(this.screen.subList(excessLinesToDelete, this.height));
                this.height = this.maxHeight;
            }

            // Re-wrapping the console widget of all the previously present border layers
            for (int i = 0; i < tmpBorderCount; i++) {
                this.wrapWidgetWithBorder();
            }
        }

        return this;
    }

    /**
     * @return A timestamp with the time at which a new message is
     *         added to the console log
     */
    private String getTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);

        // Format the current date and time to the given pattern
        String timestamp = now.format(formatter);

        return "[" + timestamp + "]" + PrintUtils.SPACE;
    }
}
