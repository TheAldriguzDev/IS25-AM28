package it.polimi.ingsw.is25am28.TUI;

import java.util.ArrayList;
import java.util.List;

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
        defaultBorderCharacters.add(UnicodeBlockElements.SINGLE_LINE_TL_CORNER);
        defaultBorderCharacters.add(UnicodeBlockElements.SINGLE_LINE_TR_CORNER);
        defaultBorderCharacters.add(UnicodeBlockElements.SINGLE_LINE_BR_CORNER);
        defaultBorderCharacters.add(UnicodeBlockElements.SINGLE_LINE_BL_CORNER);
        defaultBorderCharacters.add(UnicodeBlockElements.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.VERTICAL_LEFT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeBlockElements.VERTICAL_LEFT_SINGLE_LINE);
    }

    // Empty constructor
    public WidgetTUI() {
        this.height = 0;
        this.width = 0;
        this.screen = null;
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
        // Return the composition result only if
        // there are widgets to compose
        if (widgets != null && !widgets.isEmpty()) {
            WidgetTUI composedWidgets = new WidgetTUI();
            List<Integer> widgetHeights = new ArrayList<Integer>();
            int maxDepth, widgetAmount;

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
                        composedLine.append(PrintUtils.getSpace().repeat(widgets.get(j).getWidth()));
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
        // Return the composition result only if
        // there are widgets to compose
        if (widgets != null && !widgets.isEmpty()) {
            WidgetTUI composedWidgets = new WidgetTUI();

            // Setting width of the composed widgets (NOTE: height is auto-set by appendScreen())
            widgets.stream().mapToInt(WidgetTUI::getWidth).max().ifPresent(composedWidgets::setWidth);

            for (WidgetTUI widget : widgets) {
                composedWidgets.appendScreen(widget.getScreen());
            }

            return composedWidgets;
        }

        return null;
    }

    /**
     * @param height The height to set this widget to
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return This widget's height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @param width The width to set this widget to
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return this widget's width
     */
    public int getWidth() {
        return this.width;
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
            if (this.screen == null) {
                this.screen = new ArrayList<String>();
            }

            // Adding string
            this.screen.add(string);

            // Updating widget dimensions
            this.height++;
            this.screen.stream().mapToInt(String::length).max().ifPresent(this::setWidth);
        }
    }

    /**
     * @param otherScreen The screen to append after this widget's screen
     */
    public void appendScreen(List<String> otherScreen) {
        if (otherScreen != null && !otherScreen.contains(null)) {
            StringBuilder paddedString;
            int padding, lines;

            if (this.screen == null) {
                this.screen = new ArrayList<String>();
            }

            // Adding all string in the other screen
            this.screen.addAll(otherScreen);

            // Updating widget dimensions
            this.height += otherScreen.size();
            this.screen.stream().mapToInt(String::length).max().ifPresent(this::setWidth);

            lines = this.screen.size();

            // Padding all lines to the widget's width
            for (int i = 0; i < lines; i++) {
                paddedString = new StringBuilder(this.screen.get(i));
                padding = this.width - paddedString.length();
                paddedString.append(PrintUtils.getSpace().repeat(padding));
                this.screen.set(i, paddedString.toString());
            }
        }
    }

    // TODO: Not reliable (see tests)
    /**
     * Centers this widget's screen contents by adding padding spaces
     */
    public void centerWidgetScreen() {
        StringBuilder unpaddedString;
        StringBuilder paddedString;
        int i, screenLen, strLen, padding;

        screenLen = this.screen.size();

        for (i = 0; i < screenLen; i++) {
            strLen = this.screen.get(i).length();
            unpaddedString = new StringBuilder(this.screen.get(i).trim());

            // Calculating padding
            padding = (this.width - unpaddedString.length() - 2 * this.layerCount) / 2;
            if (strLen % 2 == 0) padding++;

            // If the string requires padding
            if (padding > 0) {
                paddedString = new StringBuilder();

                // Adding left-side padding spaces
                paddedString.append(PrintUtils.getSpace().repeat(padding));

                paddedString.append(unpaddedString);

                // Adding right-side padding spaces
                paddedString.append(PrintUtils.getSpace().repeat(padding));

                // Setting the padded string as the new value
                this.screen.set(i, paddedString.toString());
            }
        }
    }

    /**
     * Sets the screen stored by this widget, which is a list of strings
     * that describe the text to print to terminal
     *
     * Also sets the minimum width and height needed to store the given screen
     *
     * @param screen The list of string that make up this widget's output screen
     */
    public void setScreen(List<String> screen) {
        this.screen = screen;
        this.height = screen.size();
    }

    /**
     * @return This widget's stored screen
     */
    public List<String> getScreen() {
        return this.screen;
    }

    /**
     * Wraps this widget's screen with the default border
     */
    public void wrapScreenWithBorder() {
        // Invokes the overloaded method to use the default border characters
        this.wrapScreenWithBorder(null);
    }

    /**
     * Wraps this widget's screen with a custom border (if given one is formatted correctly)
     *
     * @param borderCharacters The custom border characters to use if you want to use
     *                         something different that the default border characters<br>
     *
     *  (NOTE: They need to be given as a list of 8 strings, otherwise the default behavior is to use the defaultBorderCharacters to draw the border)
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
     * Removes the border from this screen
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
