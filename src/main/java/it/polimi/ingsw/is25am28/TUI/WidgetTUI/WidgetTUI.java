package it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.*;

public class WidgetTUI {
    public static final List<String> defaultBorderCharacters = new ArrayList<String>();
    // protected List<List<WidgetTUI>> components;
    protected List<String> screen;
    protected int height;
    protected int width;
    protected int borderCount;
    protected String widgetId;
    // protected WidgetTUI parent;

    static {
        // NOTE: This is also the ordering that each custom borderCharacter list must follow
        //       to be interpreted and used correctly when drawing the custom border
        //       (i.e.: a custom border pattern is applied iff all symbols are specified)

        // These are the default characters, indexed in the following (Clockwise Indexing):
        //  0 - Top Left Corner
        //  1 - Top Right Corner
        //  2 - Bottom Right Corner
        //  3 - Bottom Left Corner

        //  4 - Top Side
        //  5 - Right Side
        //  6 - Bottom Side
        //  7 - Left Side

        //  8 - Top Side Center Left Symbol
        //  9 - Top Side Center Symbol
        //  10 - Top Side Center Right Symbol

        //  11 - Right Side Center Top Symbol
        //  12 - Right Side Center Symbol
        //  13 - Right Side Center Bottom Symbol

        //  14 - Bottom Side Center Right Symbol
        //  15 - Bottom Side Center Symbol
        //  16 - Bottom Side Center Left Symbol

        //  17 - Left Side Center Bottom Symbol
        //  18 - Left Side Center Symbol
        //  19 - Left Side Center Top Symbol

        // Symbols 0, 1, 2, 3
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_TL_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_TR_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_BR_CORNER);
        defaultBorderCharacters.add(UnicodeCharacters.SINGLE_LINE_BL_CORNER);

        // Symbols 4, 5, 6, 7
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE);

        // Symbols 8, 9, 10
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE);

        // Symbols 11, 12, 13
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE);

        // Symbols 14, 15, 16
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_BOTTOM_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.HORIZONTAL_BOTTOM_SINGLE_LINE);

        // Symbols 17, 18, 19
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE);
        defaultBorderCharacters.add(UnicodeCharacters.VERTICAL_LEFT_SINGLE_LINE);
    }

    // Creates a no-content widget
    public WidgetTUI() {
        // this.components = null;
        this.screen = new ArrayList<String>();
        this.height = 0;
        this.width = 0;
        this.borderCount = 0;
        this.widgetId = "DEFAULT_ID";
        // this.parent = null;
    }

    // Auto adjusts the widget's dimension based on the given screen
    public WidgetTUI(List<String> screen) {
        // this.components = null;
        this.setScreen(screen);
        this.borderCount = 0;
        this.widgetId = "DEFAULT_ID";
        // this.parent = null;
    }

    /**
     * Composes two widgets horizontally by returning the main widget with its screen
     * replaced with the horizontal composition of the given widgets' screens.
     *
     * @param mainWidget The main widget onto which the composition result will be placed on
     * @param donorWidget The donor widget whose screen will be put inside the main widget
     *
     * @return The widget resulting from the composition of the two given widgets
     */
    public static WidgetTUI composeTwoWidgetsHorizontally(WidgetTUI mainWidget, WidgetTUI donorWidget) {
        List<WidgetTUI> widgets = new ArrayList<>();

        widgets.add(mainWidget);
        widgets.add(donorWidget);
        mainWidget = WidgetTUI.composeWidgetsHorizontally(widgets);

        return mainWidget;
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
        if (widgets != null && !widgets.isEmpty()) {
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
                        composedLine.append(SPACE.repeat(widgets.get(j).getWidth()));
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
     * Composes two widgets vertically by returning the main widget with its screen
     * replaced with the vertical composition of the given widgets' screens.
     *
     * @param mainWidget The main widget onto which the composition result will be placed on
     * @param donorWidget The donor widget whose screen will be put inside the main widget
     *
     * @return The widget resulting from the composition of the two given widgets
     */
    public static WidgetTUI composeTwoWidgetsVertically(WidgetTUI mainWidget, WidgetTUI donorWidget) {
        List<WidgetTUI> widgets = new ArrayList<>();

        widgets.add(mainWidget);
        widgets.add(donorWidget);
        mainWidget = WidgetTUI.composeWidgetsVertically(widgets);

        return mainWidget;
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
        if (widgets != null && !widgets.isEmpty()) {
            WidgetTUI composedWidgets = new WidgetTUI();

            // Removes any null widget inside the widgets list
            widgets = widgets.stream().filter(Objects::nonNull).toList();

            for (WidgetTUI widget : widgets) {
                composedWidgets.appendScreen(widget.getScreen());
            }

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

        if (screens != null && !screens.isEmpty()) {
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
                            composedLine.append(SPACE.repeat(allScreensMaxWidths.get(j)));
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

        if (screens != null && !screens.isEmpty()) {
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

//    // TODO: Test this
//    // TODO: Finish this
//    // TODO: Create the "opposite" version that recomposes the tree by going upwards from a single node
//    /**
//     * Composes all the stored widgets into a single widget and
//     * saves the composition result into this widget's screen
//     */
//    public void composeAllStoredWidgets() {
//
//    }

    /**
     * Adds the remaining spaces to the given screen so that all lines
     * are of the same length as the longest one found inside it
     *
     * It's equivalent to performing wrapWithBorder and unwrapBorder right after
     */
    public static WidgetTUI fillScreenWithSpaces(WidgetTUI widget) {
        String line;
        int screenLen, maxWidth;

        maxWidth = widget.getWidth();
        screenLen = widget.getHeight();

        for (int i = 0; i < screenLen; i++) {
            line = widget.getScreen().get(i);
            widget.getScreen().set(i, line + SPACE.repeat(maxWidth - PrintUtils.removeUnicodeFromString(line).length()));
        }

        return widget;
    }

    /**
     * Wraps this widget's screen with one layer of the default border
     */
    public static List<String> wrapScreenWithBorder(List<String> screen) {
        return WidgetTUI.wrapScreenWithBorder(screen, null);
    }

    /**
     * Wraps this widget's screen with one layer of given custom border (if it's formatted correctly)
     *
     * @param borderCharacters The custom border characters to use if you want to use
     *                         something different that the default border characters<br>
     *
     *  (NOTE: For more information on the specific format, see the default border static attribute of this class)
     */
    public static List<String> wrapScreenWithBorder(List<String> screen, List<String> borderCharacters) {
        StringBuilder tmpString;

        // Instantiating the to-be-wrapped screen
        List<String> wrappedScreen = new ArrayList<String>();

        // Using defaultBorderCharacters if the given list
        // does not have all the symbols specified
        if (borderCharacters == null || borderCharacters.size() < WidgetTUI.defaultBorderCharacters.size()) {
            borderCharacters = WidgetTUI.defaultBorderCharacters;
        }

        // Calculating the max width of the given screen
        AtomicInteger maxWidth = new AtomicInteger(0);

        screen.stream()
                .map(PrintUtils::removeUnicodeFromString)
                .mapToInt(String::length)
                .max()
                .ifPresent(maxWidth::set);

        int height = screen.size() + 2;
        int width = maxWidth.get() + 2;

        // Top Left Corner
        tmpString = new StringBuilder(borderCharacters.get(0));

        // Upper border
        for (int i = 1; i < width - 1; i++) {
            if (i == (width / 2) - 2) {
                // Top Side Center Left Symbol
                tmpString.append(borderCharacters.get(8));
            }
            else if (i == (width / 2)) {
                // Top Side Center Symbol
                tmpString.append(borderCharacters.get(9));
            }
            else if (i == (width / 2) + 2) {
                // Top Side Center Right Symbol
                tmpString.append(borderCharacters.get(10));
            }
            else {
                // Top Side
                tmpString.append(borderCharacters.get(4));
            }
        }

        // Top Right Corner
        tmpString.append(borderCharacters.get(1));
        wrappedScreen.add(tmpString.toString());

        // Middle
        for (int i = 1; i < height - 1; i++) {
            tmpString = new StringBuilder();

            // Middle Left Side
            if (i == (height / 2) - 1) {
                // Left Side Center Top Symbol
                tmpString.append(borderCharacters.get(19));
            }
            else if (i == (height / 2)) {
                // Left Side Center Symbol
                tmpString.append(borderCharacters.get(18));
            }
            else if (i == (height / 2) + 1) {
                // Left Side Center Bottom Symbol
                tmpString.append(borderCharacters.get(17));
            }
            else {
                // Left Side
                tmpString.append(borderCharacters.get(7));
            }

            // Old unwrapped screen goes in the middle
            String oldLine = screen.get(i - 1);
            tmpString.append(oldLine);

            int oldLineLen = PrintUtils.removeUnicodeFromString(oldLine).length();

            // Adding right-side padding
            if (oldLineLen < width - 2) {
                tmpString.append(SPACE.repeat(width - 2 - oldLineLen));
            }

            // Middle Right Side
            if (i == (height / 2) - 1) {
                // Right Side Center Top Symbol
                tmpString.append(borderCharacters.get(11));
            }
            else if (i == (height / 2)) {
                // Right Side Center Symbol
                tmpString.append(borderCharacters.get(12));
            }
            else if (i == (height / 2) + 1) {
                // Right Side Center Bottom Symbol
                tmpString.append(borderCharacters.get(13));
            }
            else {
                // Right Side
                tmpString.append(borderCharacters.get(5));
            }

            // Finally, add the wrapped line to the new screen
            wrappedScreen.add(tmpString.toString());
        }

        // Bottom Left Corner
        tmpString = new StringBuilder(borderCharacters.get(3));

        // Lower border
        for (int i = 1; i < width - 1; i++) {
            if (i == (width / 2) + 2) {
                // Bottom Side Center Right Symbol
                tmpString.append(borderCharacters.get(14));
            }
            else if (i == (width / 2)) {
                // Bottom Side Center Symbol
                tmpString.append(borderCharacters.get(15));
            }
            else if (i == (width / 2) - 2) {
                // Bottom Side Center Left Symbol
                tmpString.append(borderCharacters.get(16));
            }
            else {
                // Bottom Side
                tmpString.append(borderCharacters.get(6));
            }
        }

        // Bottom Right Corner
        tmpString.append(borderCharacters.get(2));
        wrappedScreen.add(tmpString.toString());

        return wrappedScreen;
    }

//    /**
//     * Removes one border layer from this screen
//     * (NOTE: Since the passed object is not a widget, there's no way to know if
//     *        the screen was wrapped in the past, therefore this method should be
//     *        used with care as it can delete parts of the screen if used incorrectly)
//     */
//    public static List<String> unwrapScreenFromBorder(List<String> screen) {
//        if (screen != null) {
//            List<String> unwrappedScreen = new ArrayList<String>();
//
//            // Calculating the max width of the given screen
//            AtomicInteger maxWidth = new AtomicInteger(0);
//
//            screen.stream()
//                    .map(PrintUtils::removeUnicodeFromString)
//                    .mapToInt(String::length)
//                    .max()
//                    .ifPresent(maxWidth::set);
//
//            int height = screen.size() + 2;
//            int width = maxWidth.get() + 2;
//
//            for (int i = 1; i < height; i++) {
//                String line = screen.get(i);
//                unwrappedScreen.add(line.substring(1, width - 1));
//            }
//
//            return unwrappedScreen;
//        }
//
//        return null;
//    }

//    // TODO: Test this
//    /**
//     * Adds a component to this widget's component list.
//     * If <code>this.components</code> is not extended to the given coordinates
//     * (i.e.: there hasn't been an initialization that reached that far), then
//     * the method puts nulls until it reaches the row and column where the widget
//     * needs to be placed
//     */
//    public void setWidgetComponentAtCoordinates(WidgetTUI widget, int rowIndex, int colIndex) {
//        if (widget != null) {
//            if (this.components == null) {
//                this.components = new ArrayList<>();
//            }
//
//            // Extending the amount of rows until the given
//            // widget can be placed at the given row
//            while (this.components.size() < rowIndex) {
//                this.components.add(null);
//            }
//            this.components.add(new ArrayList<>());
//
//            // Extending the current row until the given
//            // widget can be placed at the given column
//            List<WidgetTUI> currRow = this.components.get(rowIndex);
//
//            while (currRow.size() <= colIndex) {
//                currRow.add(null);
//            }
//            currRow.set(colIndex, widget);
//
//            this.components.set(colIndex, currRow);
//        }
//    }
//
//    // TODO: Test this
//    /**
//     * Returns the widget stored in this widget's component list of lists by
//     * querying if there's a component at the given coordinates (rowIndex, colIndex)
//     *
//     * @param rowIndex The row index of the component to return (if present)
//     * @param colIndex The column index of the component to retunr (if present)
//     *
//     * @return The component at coordinates (rowIndex, colIndex) found inside <code>this.components</code>.
//     *
//     * @throws NullPointerException If <code>this.components</code> is null
//     * @throws IndexOutOfBoundsException If either rowIndex or colIndex are out of bounds
//     */
//    public WidgetTUI getWidgetComponentAtCoordinates(int rowIndex, int colIndex) throws NullPointerException, IndexOutOfBoundsException {
//        if (this.components != null) {
//            if (this.components.size() > rowIndex) {
//                List<WidgetTUI> currRow = this.components.get(rowIndex);
//
//                if (currRow.size() > colIndex) {
//                    return currRow.get(colIndex);
//                }
//                else {
//                    throw new IndexOutOfBoundsException("ERROR: colIndex=" + colIndex + " is out of bounds");
//                }
//            }
//            else {
//                throw new IndexOutOfBoundsException("ERROR: rowIndex=" + rowIndex + " is out of bounds");
//            }
//        }
//        else {
//            throw new NullPointerException("ERROR: \"this.components\" list is null");
//        }
//    }

//    /**
//     * @return The list of lists of WidgetTUI that compose this widget
//     */
//    public List<List<WidgetTUI>> getAllWidgetComponents() {
//        return this.components;
//    }

    /**
     * @param widgetId The ID to give to this widget
     */
    public void setWidgetId(String widgetId) {
        if (widgetId != null && !widgetId.isEmpty()) {
            this.widgetId = widgetId;
        }
    }

    /**
     * @return This widget's ID
     */
    public String getWidgetId() {
        return this.widgetId;
    }

    /**
     * @param height The height to set this widget to. If the given height is smaller
     *               than the current height, then the height stays unchanged
     *               (because shrinking the widget would mean to lose some screen lines)
     */
    public WidgetTUI setHeight(int height) {
        // Extends the screen to fit the new height
        if (this.height < height) {
            while (this.screen.size() < height) {
                this.screen.add(SPACE.repeat(this.width));
            }

            this.height = height;
        }

        return this;
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
    public WidgetTUI setWidth(int width) {
        if (this.width < width) {
            String tmp;
            int padding;

            this.width = width;

            // Adds the remaining padding to each line in the screen
            // so that all lines now have the set width
            for (int i = 0; i < this.height; i++) {
                tmp = this.screen.get(i);
                padding = this.width - PrintUtils.removeUnicodeFromString(tmp).length();

                if (padding > 0) {
                    tmp += SPACE.repeat(padding);
                    this.screen.set(i, tmp);
                }
            }
        }

        return this;
    }

    /**
     * @return This widget's width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return This widget's border count before reaching its screen
     */
    public int getBorderCount() {
        return this.borderCount;
    }

    /**
     * @param string The string to append to this widget's screen
     */
    public WidgetTUI appendString(String string) {
        if (string != null) {
            // Adding string
            this.screen.add(string);

            // Updating widget dimensions
            this.height++;
            this.width = Math.max(PrintUtils.removeUnicodeFromString(string).length(), this.width);
        }

        return this;
    }

    /**
     * @param otherScreen The screen to append after this widget's screen
     */
    public WidgetTUI appendScreen(List<String> otherScreen) {
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

        return this;
    }

    /**
     * Centers this widget's screen contents by adding padding
     * spaces to both sides of each screen line, in equal amount
     */
    public WidgetTUI centerWidgetScreen() {
        List<String> paddedScreen;
        StringBuilder paddedString;
        String trimmed;
        int padding, strlen;

        paddedScreen = new ArrayList<String>();

        // Adding right and left padding to each string in the screen
        for (String s : this.screen) {
            trimmed = s.trim();
            strlen = PrintUtils.removeUnicodeFromString(trimmed).length();
            paddedString = new StringBuilder();
            padding = ((this.width - strlen) / 2) - this.borderCount;

            if (padding > 0) {
                paddedString.append(SPACE.repeat(padding));
                paddedString.append(trimmed);
                paddedString.append(SPACE.repeat(padding));

                paddedScreen.add(paddedString.toString());
            }
            else {
                paddedScreen.add(s);
            }
        }

        this.setScreen(paddedScreen);

        return this;
    }

    /**
     * Sets the screen stored by this widget, which is a list of strings
     * that describe the text to print to terminal.<br>
     * Also sets the minimum width and height needed to store the given screen
     *
     * @param screen The list of string that make up this widget's output screen
     */
    public WidgetTUI setScreen(List<String> screen) {
        if (screen != null) {
            // Setting the screen only with non-null lines from the given screen
            this.screen = new ArrayList<String>(screen.stream().filter(Objects::nonNull).toList());

            // Resetting the screen removes any borders inside, therefore
            // the border count will be zeroed
            this.borderCount = 0;

            // Updating widget dimensions
            this.height = this.screen.size();
            this.screen.stream()
                    .map(PrintUtils::removeUnicodeFromString)
                    .mapToInt(String::length)
                    .max()
                    .ifPresent(this::setWidth);
        }

        return this;
    }

    /**
     * @return This widget's stored screen
     */
    public List<String> getScreen() {
        return this.screen;
    }

    /**
     * Resets the screen by eliminating its content and
     * also sets width and height back to 0
     */
    public WidgetTUI resetScreenAndDimensions() {
        this.screen = new ArrayList<>();
        this.height = 0;
        this.width = 0;

        return this;
    }

    /**
     * Adds the given amount of padding spaces for each side.
     * The idea is to follow the HTML box model, thus this method works just like
     * adding padding pixels in CSS to an HTML tag
     *
     * @param top The amount of padding to add to the top of this widget's screen
     * @param right The amount of padding to add to the right of this widget's screen
     * @param bottom The amount of padding to add to the bottom of this widget's screen
     * @param left The amount of padding to add to the left of this widget's scren
     */
    public WidgetTUI addPadding(int top, int right, int bottom, int left) {
        List<String> screen;
        String line;

        // Adding top padding to this widget's screen
        if (top > 0) {
            screen = new ArrayList<>();
            line = SPACE.repeat(this.getWidth());
            this.height += top;

            while (top > 0) {
                screen.add(line);
                top--;
            }

            screen.addAll(this.screen);
            this.screen = screen;
        }

        // Adding right padding to this widget's screen
        this.setWidth(this.width + right);

        // Adding bottom padding to this widget's screen
        this.setHeight(this.height + bottom);

        // Adding left padding to this widget's screen
        if (left > 0) {
            line = SPACE.repeat(left);

            for (int i = 0; i < this.height; i++) {
                this.screen.set(i, line + this.screen.get(i));
            }

            this.width += left;
        }

        return this;
    }

    /**
     * Wraps this widget's screen with one layer of the default border
     */
    public WidgetTUI wrapWidgetWithBorder() {
        // Invokes the overloaded method to use the default border characters
       return this.wrapWidgetWithBorder(null);
    }

    /**
     * Wraps this widget's screen with one layer of given custom border (if it's formatted correctly)
     *
     * @param borderCharacters The custom border characters to use if you want to use
     *                         something different that the default border characters<br>
     *
     *  (NOTE: For more information on the specific format, see the default border static attribute of this class)
     */
    public WidgetTUI wrapWidgetWithBorder(List<String> borderCharacters) {
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

        // Increase the border counter by one
        this.borderCount++;

        // Top Left Corner
        tmpString = new StringBuilder(borderCharacters.get(0));

        // Upper border
        for (int i = 1; i < this.width - 1; i++) {
            if (i == (this.width / 2) - 2) {
                // Top Side Center Left Symbol
                tmpString.append(borderCharacters.get(8));
            }
            else if (i == (this.width / 2)) {
                // Top Side Center Symbol
                tmpString.append(borderCharacters.get(9));
            }
            else if (i == (this.width / 2) + 2) {
                // Top Side Center Right Symbol
                tmpString.append(borderCharacters.get(10));
            }
            else {
                // Top Side
                tmpString.append(borderCharacters.get(4));
            }
        }

        // Top Right Corner
        tmpString.append(borderCharacters.get(1));
        this.screen.add(tmpString.toString());

        // Middle
        for (int i = 1; i < this.height - 1; i++) {
            tmpString = new StringBuilder();

            // Middle Left Side
            if (i == (this.height / 2) - 1) {
                // Left Side Center Top Symbol
                tmpString.append(borderCharacters.get(19));
            }
            else if (i == (this.height / 2)) {
                // Left Side Center Symbol
                tmpString.append(borderCharacters.get(18));
            }
            else if (i == (this.height / 2) + 1) {
                // Left Side Center Bottom Symbol
                tmpString.append(borderCharacters.get(17));
            }
            else {
                // Left Side
                tmpString.append(borderCharacters.get(7));
            }

            // Old unwrapped screen goes in the middle
            String oldLine = unwrappedScreen.get(i - 1);
            tmpString.append(oldLine);

            int oldLineLen = PrintUtils.removeUnicodeFromString(oldLine).length();

            // Adding right-side padding
            if (oldLineLen < this.width - 2) {
                tmpString.append(SPACE.repeat(this.width - 2 - oldLineLen));
            }

            // Middle Right Side
            if (i == (this.height / 2) - 1) {
                // Right Side Center Top Symbol
                tmpString.append(borderCharacters.get(11));
            }
            else if (i == (this.height / 2)) {
                // Right Side Center Symbol
                tmpString.append(borderCharacters.get(12));
            }
            else if (i == (this.height / 2) + 1) {
                // Right Side Center Bottom Symbol
                tmpString.append(borderCharacters.get(13));
            }
            else {
                // Right Side
                tmpString.append(borderCharacters.get(5));
            }

            // Finally, add the wrapped line to the new screen
            this.screen.add(tmpString.toString());
        }

        // Bottom Left Corner
        tmpString = new StringBuilder(borderCharacters.get(3));

        // Lower border
        for (int i = 1; i < this.width - 1; i++) {
            if (i == (this.width / 2) + 2) {
                // Bottom Side Center Right Symbol
                tmpString.append(borderCharacters.get(14));
            }
            else if (i == (this.width / 2)) {
                // Bottom Side Center Symbol
                tmpString.append(borderCharacters.get(15));
            }
            else if (i == (this.width / 2) - 2) {
                // Bottom Side Center Left Symbol
                tmpString.append(borderCharacters.get(16));
            }
            else {
                // Bottom Side
                tmpString.append(borderCharacters.get(6));
            }
        }

        // Bottom Right Corner
        tmpString.append(borderCharacters.get(2));
        this.screen.add(tmpString.toString());

        return this;
    }

    /**
     * Removes one border layer from this screen
     */
    public WidgetTUI unwrapWidgetFromBorder() {
        if (this.borderCount > 0) {
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

            // Decrease the border counter by one
            this.borderCount--;
        }

        return this;
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
