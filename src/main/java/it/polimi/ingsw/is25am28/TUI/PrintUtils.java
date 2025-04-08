package it.polimi.ingsw.is25am28.TUI;

public class PrintUtils {
    private static final String SPACE = " ";

    /**
     * @return A single SPACE character
     */
    public static String getSpace() {
        return PrintUtils.SPACE;
    }

    /**
     * Applies a REGEX to remove UNICODE strings, needed in cases where we want
     * to calculate the real string length
     *
     * @param string
     * @return The given string with all UNICODE strings removed
     */
    public static String removeUnicodeFromString(String string) {
        String regex = "\\\\u[0-9A-Fa-f]{4}|\\u001B\\[[0-9;]*[mK]";
        return string.replaceAll(regex, "");
    }

    /**
     * @param string The string to color
     * @param unicodeColorString The color to add to the string
     * @return The colored string
     */
    public static String addColor(String string, String unicodeColorString) {
        return unicodeColorString + string + ANSIColors.RESET;
    }
}
