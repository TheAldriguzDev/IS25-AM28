package it.polimi.ingsw.is25am28.TUI;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.is25am28.TUI.PrintUtils.*;

public class WidgetTUI {
    public static final List<String> defaultBorderCharacters = new ArrayList<String>();
    private List<String> screen;
    private int height;
    private int width;
    private int layerCount;

    static {
        // NOTE: This is also the ordering that each custom borderCharacter list must follow
        //       to be interpreted and used correctly when drawing the custom border

        // These are the default characters, indexed in the following (Clockwise Indexing):
        //  0 - Top Left Corner
        //  1 - Top Right Corner
        //  2 - Bottom Right Corner
        //  3 - Bottom Left Corner
        //  4 - Top Side
        //  5 - Right Side
        //  6 - Bottom Side
        //  7 - Left Side
        //  8 - Top Side Center Symbol
        //  9 - Right Side Center Symbol
        //  10 - Bottom Side Center Symbol
        //  11 - Left Side Center Symbol
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_TL_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_TR_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_BR_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_BL_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE);
    }

    // Creates a no-content widget
    public WidgetTUI() {
        this.height = 0;
        this.width = 0;
        this.screen = new ArrayList<String>();
        this.layerCount = 0;
    }

    // Auto adjusts the widget's dimension based on the given screen
    public WidgetTUI(List<String> screen) {
        this.screen = new ArrayList<String>();
        this.setScreen(screen);
        this.layerCount = 0;
    }

    /**
     * @param widgets The widgets that will be composed horizontally into one
     * (NOTE: The given widgets will be composed IN THE GIVEN ORDER)
     *
     * @return A widget whose screen is the result of the horizontal
     *         composition of the given widget list
     */
    public static WidgetTUI composeWidgetsHorizontally(List<WidgetTUI> widgets) {
        // Return the composition result only if there are widgets to compose
        if (widgets != null) {
            WidgetTUI composedWidgets = new WidgetTUI();
            List<Integer> widgetHeights = new ArrayList<Integer>();
            int maxDepth, widgetAmount;

            // Removes any null widget inside the widgets list
            widgets = widgets.stream().filter(Objects::nonNull).toList();

            // Getting all widget heights needed to know when each widget ends
            for (WidgetTUI widgetTUI : widgets) {
                widgetHeights.add(widgetTUI.getHeight());
            }

            widgetAmount = widgetHeights.size();
            maxDepth = widgetHeights.getFirst();

            // Calculating the maximum depth at which the composition arrives
            for (int i = 0; i < widgetAmount; i++) {
                int depth = widgetHeights.get(i);

                if (depth > maxDepth) {
                    maxDepth = depth;
                }
            }

            // Stitching together all the widgets and printing empty space for
            // those widgets whose screens have already been fully printed
            for (int i = 0; i < maxDepth; i++) {
                StringBuilder composedLine = new StringBuilder();

                for (int j = 0; j < widgetAmount; j++) {
                    if (widgetHeights.get(j) > 0) {
                        // If this widget has still some screen content
                        // to print, then add it to the composition
                        composedLine.append(widgets.get(j).getScreen().get(i));

                        // Decrementing each widget's line counter
                        widgetHeights.set(j, widgetHeights.get(j) - 1);
                    }
                    else {
                        // Otherwise, since this widget's content has already been added to the
                        // composition, there will be placed instead an empty line of the same length
                        // of the widget's width
                        composedLine.append(getSpace().repeat(widgets.get(j).getWidth()));
                    }
                }

                // Adding each composed line to the composition widget's screen
                composedWidgets.appendString(composedLine.toString());
            }

            return composedWidgets;
        }

        return null;
    }

    /**
     * @param widgets The widgets that will be composed vertically into one<br>
     * (NOTE: The given widgets will be composed IN THE GIVEN ORDER)
     *
     * @return A widget whose screen is the result of the vertical
     *         composition of the given widget list
     *
     */
    public static WidgetTUI composeWidgetsVertically(List<WidgetTUI> widgets) {
        // Return the composition result only if there are widgets to compose
        if (widgets != null) {
            WidgetTUI composedWidgets = new WidgetTUI();

            // Removes any null widget inside the widgets list
            widgets = widgets.stream().filter(Objects::nonNull).toList();

            for (WidgetTUI widget : widgets) {
                composedWidgets.appendScreen(widget.getScreen());
            }

            // Setting width of the composed widgets (NOTE: height is auto-set by appendScreen())
            widgets.stream()
                    .mapToInt(WidgetTUI::getWidthNoUnicode)
                    .max()
                    .ifPresent(composedWidgets::setWidth);

            return composedWidgets;
        }

        return null;
    }

    /**
     * @param screens The widget's screens to compose horizontally
     * @return A single screen containing the horizontal composition result, which is done
     *         by first taking all the strings from each string list that are at a certain index/height, then
     *         they are all concatenated (in order of how they're provided) and this creates the i-th line.
     *         Repeat this process until all lists have been stitched together and you get the resulting composition
     *
     *  (NOTE: If some screens are of different heights, then the shorter ones are compensated by adding empty lines)
     */
    public static List<String> composeScreensHorizontally(List<List<String>> screens) {
        List<String> composedScreens;
        List<Integer> allScreensHeights, allScreensMaxWidths;
        AtomicInteger maxHeight;
        int screenAmount;

        if (screens != null) {
            // Removes any null lists inside the screens list
            screens = screens.stream().filter(Objects::nonNull).toList();

            composedScreens = new ArrayList<String>();
            allScreensHeights = screens.stream().map(List::size).toList();
            maxHeight = new AtomicInteger(0);
            screenAmount = screens.size();

            allScreensMaxWidths = screens.stream().mapToInt(
                (list) -> {
                    AtomicInteger maxWidth = new AtomicInteger(0);

                    list.stream()
                            .mapToInt(String::length)
                            .max()
                            .ifPresent(maxWidth::set);

                    return maxWidth.get();
                }
            ).boxed().toList();

            // Getting the max height so that we know where to stop
            allScreensHeights.stream().mapToInt(i -> i).max().ifPresent(maxHeight::set);

            for (int i = 0; i < maxHeight.get(); i++) {
                StringBuilder composedLine = new StringBuilder();

                for (int j = 0; j < screenAmount; j++) {
                    if (screens.get(j) != null && !screens.get(j).isEmpty()) {
                        if (i < allScreensHeights.get(j)) {
                            // If this screen has more content to show, then append it
                            composedLine.append(screens.get(j).get(i));
                            // composedLine.append(PrintUtils.getSpace());
                        }
                        else {
                            // Otherwise, replace every next line of this screen with a space-filled
                            // string (of length given by the current screen's max width) that fills the
                            // gap that would have been filled by the current screen if it hadn't been all
                            // concatenated already in the previous iterations.
                            composedLine.append(PrintUtils.getSpace().repeat(allScreensMaxWidths.get(j)));
                        }
                    }
                }

                composedScreens.add(composedLine.toString());
            }

            return composedScreens;
        }

        return null;
    }

    /**
     * @param screens The widget's screens to compose vertically
     * @return A single screen containing the vertical composition result, which is done
     *         by concatenating each of the given string lists into a single one
     */
    public static List<String> composeScreensVertically(List<List<String>> screens) {
        List<String> composedScreens;

        if (screens != null) {
            composedScreens = new ArrayList<String>();

            // Removes any null lists inside the screens list
            screens = screens.stream().filter(Objects::nonNull).toList();

            for (List<String> screen : screens) {
                // Removes any null string inside the current screen
                composedScreens.addAll(screen.stream().filter(Objects::nonNull).toList());
            }

            return composedScreens;
        }

        return null;
    }

    /**
     * @param height The height to set this widget to. If the given height is smaller
     *               than the current height, then the height stays unchanged
     *               (because shrinking the widget would mean to lose some screen lines)
     */
    public void setHeight(int height) {
        // Extends the screen to fit the new height
        while (this.screen.size() < height) {
            this.screen.add(getSpace().repeat(this.width));
        }

        this.height = height;
    }

    /**
     * @return This widget's height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @param width The width to set this widget to. If the given width is smaller
     *              than the current width, then the width stays unchanged
     *              (because shrinking the widget would mean to lose some screen lines)
     */
    public void setWidth(int width) {
        if (this.width < width) {
            this.width = width;
        }
        else {
            AtomicInteger minWidth = new AtomicInteger(this.width);

            this.screen.stream()
                    .map(PrintUtils::removeUnicodeFromString)
                    .mapToInt(String::length)
                    .max()
                    .ifPresent(minWidth::set);

            this.width = minWidth.get();
        }
    }

    /**
     * @return This widget's width
     */
    public int getWidth() {
        return this.width;
    }

    // TODO: Fix other stuff then deprecate this method
    /**
     * @return This widget's actual width obtained by removing all UNICODE characters
     */
    public int getWidthNoUnicode() {
        AtomicInteger widthNoUnicode = new AtomicInteger();

        this.screen.stream()
                .map(PrintUtils::removeUnicodeFromString)
                .mapToInt(String::length)
                .max()
                .ifPresent(widthNoUnicode::set);

        return widthNoUnicode.get();
    }

    /**
     * @return This widget's border layer count before reaching its screen
     */
    public int getLayerCount() {
        return this.layerCount;
    }

    /**
     * @param string The string to append to this widget's screen
     */
    public void appendString(String string) {
        if (string != null) {
            // Adding string
            this.screen.add(string);

            // Updating widget dimensions
            this.height++;
            this.width = Math.max(PrintUtils.removeUnicodeFromString(string).length(), this.width);
        }
    }

    /**
     * @param otherScreen The screen to append after this widget's screen
     */
    public void appendScreen(List<String> otherScreen) {
        if (otherScreen != null) {
            // Removes any null string inside otherScreen and
            // adds all the remaining strings to this screen
            this.screen.addAll(otherScreen.stream().filter(Objects::nonNull).toList());

            // Updating widget dimensions
            this.height += otherScreen.size();
            this.screen.stream()
                    .map(PrintUtils::removeUnicodeFromString)
                    .mapToInt(String::length)
                    .max()
                    .ifPresent(this::setWidth);
        }
    }

    // TODO: Test this
    /**
     * Centers this widget's screen contents by adding padding
     * spaces to both sides of each screen line
     */
    public void centerWidgetScreen() {
        List<String> paddedScreen;
        StringBuilder paddedString;
        String trimmed;
        int padding, strlen;

        paddedScreen = new ArrayList<String>();

        // Adding right and left padding to each string in the screen
        for (String s : this.screen) {
            trimmed = s.trim();
            strlen = trimmed.length();
            paddedString = new StringBuilder();
            padding = ((this.width - strlen) / 2) - this.layerCount;

            if (padding > 0) {
                paddedString.append(getSpace().repeat(padding));
                paddedString.append(trimmed);
                paddedString.append(getSpace().repeat(padding));

                paddedScreen.add(paddedString.toString());
            }
            else {
                paddedScreen.add(s);
            }
        }

        this.setScreen(paddedScreen);
    }

    /**
     * Sets the screen stored by this widget, which is a list of strings
     * that describe the text to print to terminal.<br>
     * Also sets the minimum width and height needed to store the given screen
     *
     * @param screen The list of string that make up this widget's output screen
     */
    public void setScreen(List<String> screen) {
        if (screen != null) {
            // Setting the screen only with non-null lines from the given screen
            this.screen = new ArrayList<>(screen.stream().filter(Objects::nonNull).toList());

            // Updating widget dimensions
            this.height = this.screen.size();
            this.screen.stream()
                    .map(PrintUtils::removeUnicodeFromString)
                    .mapToInt(String::length)
                    .max()
                    .ifPresent(this::setWidth);
        }
    }

    /**
     * @return This widget's stored screen
     */
    public List<String> getScreen() {
        return this.screen;
    }

    /**
     * Wraps this widget's screen with one layer of the default border
     */
    public void wrapScreenWithBorder() {
        // Invokes the overloaded method to use the default border characters
        this.wrapScreenWithBorder(null);
    }

    /**
     * Wraps this widget's screen with one layer of given custom border (if it's formatted correctly)
     *
     * @param borderCharacters The custom border characters to use if you want to use
     *                         something different that the default border characters<br>
     *
     *  (NOTE: For more information on the specific format, see the default border static attribute of this class)
     */
    public void wrapScreenWithBorder(List<String> borderCharacters) {
        StringBuilder tmpString;

        // Storing the old screen and clearing the previous one
        // since it's not wrapped
        List<String> unwrappedScreen = this.screen;
        this.screen = new ArrayList<String>();

        // Using defaultBorderCharacters if the given list does not have
        // all 8 characters needed to draw the full border
        if (borderCharacters == null || borderCharacters.size() < WidgetTUI.defaultBorderCharacters.size()) {
            borderCharacters = WidgetTUI.defaultBorderCharacters;
        }

        // Adding the thickness of the borders
        this.height += 2;
        this.width += 2;

        // Increase the layer counter by one
        this.layerCount++;

        // Top Left Corner
        tmpString = new StringBuilder(borderCharacters.get(0));

        // Upper border
        // tmpString.append(borderCharacters.get(4).repeat(width - 2));
        for (int i = 1; i < this.width - 1; i++) {
            if (i == (this.width / 2)) {
                tmpString.append(borderCharacters.get(8));
            }
            else {
                tmpString.append(borderCharacters.get(4));
            }
        }

        // Top Right Corner
        tmpString.append(borderCharacters.get(1));
        this.screen.add(tmpString.toString());

        // Middle
        for (int i = 1; i < this.height - 1; i++) {
            tmpString = new StringBuilder();

            if (i == (this.height / 2)) {
                // Left Side Center Special Symbol
                tmpString.append(borderCharacters.get(11));
            }
            else {
                // Left Side Special Symbol
                tmpString.append(borderCharacters.get(7));
            }

            // Old unwrapped screen goes in the middle
            tmpString.append(unwrappedScreen.get(i - 1));

            String oldLine = unwrappedScreen.get(i - 1);
            int oldLineLen = PrintUtils.removeUnicodeFromString(oldLine).length();

            // Adding right-side padding
            if (oldLineLen < this.width - 2) {
                tmpString.append(PrintUtils.getSpace().repeat(this.width - 2 - oldLineLen));
            }

            if (i == (this.height / 2)) {
                // Right Side Center Special Symbol
                tmpString.append(borderCharacters.get(9));
            }
            else {
                // Right Side Special Symbol
                tmpString.append(borderCharacters.get(5));
            }

            // Finally, add the wrapped line to the new screen
            this.screen.add(tmpString.toString());
        }

        // Bottom Left Corner
        tmpString = new StringBuilder(borderCharacters.get(3));

        // Lower border
        // tmpString.append(borderCharacters.get(6).repeat(width - 2));
        for (int i = 1; i < this.width - 1; i++) {
            if (i == (this.width / 2)) {
                tmpString.append(borderCharacters.get(10));
            }
            else {
                tmpString.append(borderCharacters.get(6));
            }
        }

        // Bottom Right Corner
        tmpString.append(borderCharacters.get(2));
        this.screen.add(tmpString.toString());
    }

    /**
     * Removes one border layer from this screen
     */
    public void unwrapScreenFromBorder() {
        if (this.layerCount > 0) {
            List<String> unwrappedScreen = new ArrayList<String>();
            int screenLen = this.screen.size() - 1;

            for (int i = 1; i < screenLen; i++) {
                String line = this.screen.get(i);
                unwrappedScreen.add(line.substring(1, this.width - 1));
            }

            // Setting the new screen
            this.screen = unwrappedScreen;

            // Removing the thickness that was added by the border from
            // the width and height parameters
            this.height -= 2;
            this.width -= 2;

            // Decrease the layer counter by one
            this.layerCount--;
        }
    }

    /**
     * Prints to terminal this widget
     */
    public void printWidget() {
        if (this.screen != null) {
            for (String s : this.screen) {
                System.out.println(s);
            }
        }
    }
}
