package it.polimi.ingsw.is25am28.Network.Server;

/**
 * The ServerLogger class provides utility methods for logging messages to the console with various
 * log levels (INFO, DEBUG, WARN, ERROR). This class supports both generic logging and logging
 * specific to a game instance by accepting an optional game ID.
 */
public class ServerLogger {
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String GRAY = "\u001B[90m";
    private static final String BLUE = "\u001B[34m";

    private static String timestamp() {
        return "[" + GRAY + java.time.LocalTime.now().withNano(0) + RESET + "]";
    }

    private static String format(String level, String color, String tag, String gameId, String message) {
        String gameLabel = (gameId != null) ? BLUE + "[Game " + gameId + "]" + RESET + " " : "";
        return String.format("%s %s%s%s %s%s%s %s%s",
                timestamp(),
                color, level, RESET,
                MAGENTA, tag, RESET,
                gameLabel,
                message
        );
    }

    // ========== Methods with the GAME-ID ========== //

    /**
     * Logs an informational message to the console with a specific tag and game ID.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param gameId the identifier of the associated game instance; may be null if the message is not game-specific
     * @param message the content of the log message to be displayed
     */
    public static void info(String tag, String gameId, String message) {
        System.out.println(format("INFO", CYAN, tag, gameId, message));
    }

    /**
     * Logs a debug message to the console with a specific tag and game ID.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param gameId the identifier of the associated game instance; may be null if the message is not game-specific
     * @param message the content of the log message to be displayed
     */
    public static void debug(String tag, String gameId, String message) {
        System.out.println(format("DEBUG", GREEN, tag, gameId, message));
    }

    /**
     * Logs a warning message to the console with a specific tag and game ID.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param gameId the identifier of the associated game instance; may be null if the message is not game-specific
     * @param message the content of the log message to be displayed
     */
    public static void warn(String tag, String gameId, String message) {
        System.out.println(format("WARN", YELLOW, tag, gameId, message));
    }

    /**
     * Logs an error message to the console with a specific tag and game ID.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param gameId the identifier of the associated game instance; may be null if the message is not game-specific
     * @param message the content of the log message to be displayed
     */
    public static void error(String tag, String gameId, String message) {
        System.out.println(format("ERROR", RED, tag, gameId, message));
    }

    // ========== Methods without the GAME-ID ========== //

    /**
     * Logs an informational message to the console with a specific tag.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param message the content of the log message to be displayed
     */
    public static void info(String tag, String message) {
        info(tag, null, message);
    }

    /**
     * Logs a debug message to the console with a specific tag.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param message the content of the log message to be displayed
     */
    public static void debug(String tag, String message) {
        debug(tag, null, message);
    }

    /**
     * Logs a warning message to the console with a specific tag.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param message the content of the log message to be displayed
     */
    public static void warn(String tag, String message) {
        warn(tag, null, message);
    }

    /**
     * Logs an error message to the console with a specific tag.
     *
     * @param tag a descriptive label used to categorize or identify the source of the log message
     * @param message the content of the log message to be displayed
     */
    public static void error(String tag, String message) {
        error(tag, null, message);
    }
}
