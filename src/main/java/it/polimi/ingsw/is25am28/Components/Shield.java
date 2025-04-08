package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.TUI.PrintUtils.*;

public final class Shield extends Component {
    public static final String alias = "SHIELD";

    public Shield(List<Integer> connectors) {
        super(connectors);
    }
    /**
     * return the two sides that are covered by the
     * shield. they are returned with the usual standard,
     * 0: top
     * 1: right
     * 2: bottom
     * 3: left
     */
    public int[] getCoveredSide() {
        int[] covered = new int[2];

        for (int i = 0; i < 2; i++) {
            covered[i] = (direction + i) % 4;
        }

        return covered;
    }

    @Override
    public List<String> getComponentScreen() {
        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;

        int padding;

        List<String> screen = new ArrayList<String>();
        String nameAlias = Shield.alias;
        StringBuilder paddedString;

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = new ArrayList<String>(WidgetTUI.defaultBorderCharacters);

        // Adding this component's connectors to the border scheme
        customBorderScheme.set(8, "" + this.getTopSide().ordinal());
        customBorderScheme.set(9, "" + this.getRightSide().ordinal());
        customBorderScheme.set(10, "" + this.getBottomSide().ordinal());
        customBorderScheme.set(11, "" + this.getLeftSide().ordinal());

        switch (this.getDirection()) {
            // 0 --> Shield is covering top and right sides
            case 0 -> {
                paddedString = new StringBuilder();
                paddedString.append(getSpace());
                paddedString.append(addColor(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE, ANSIColors.GREEN).repeat(width - 3));
                paddedString.append(addColor(UnicodeCharacters.SINGLE_LINE_TR_CORNER, ANSIColors.GREEN));
                paddedString.append(getSpace());
                screen.add(paddedString.toString());

                for (int i = 1; i < height; i++) {
                    paddedString = new StringBuilder();

                    if (i == (height / 2)) {
                        padding = (width - nameAlias.length()) / 2;
                        paddedString.append(getSpace().repeat(padding));
                        paddedString.append(addColor(nameAlias, ANSIColors.GREEN));
                        paddedString.append(getSpace().repeat(padding - 1));
                    }
                    else {
                        paddedString.append(getSpace().repeat(width - 2));
                    }
                    paddedString.append(addColor(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE, ANSIColors.GREEN));
                    paddedString.append(getSpace());

                    screen.add(paddedString.toString());
                }
            }
            // 1 --> Shield is covering right and bottom sides
            case 1 -> {
                for (int i = 1; i < height; i++) {
                    paddedString = new StringBuilder();

                    if (i == (height / 2) + 1) {
                        padding = (width - nameAlias.length()) / 2;
                        paddedString.append(getSpace().repeat(padding));
                        paddedString.append(addColor(nameAlias, ANSIColors.GREEN));
                        paddedString.append(getSpace().repeat(padding - 1));
                    }
                    else {
                        paddedString.append(getSpace().repeat(width - 2));
                    }
                    paddedString.append(addColor(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE, ANSIColors.GREEN));
                    paddedString.append(getSpace());

                    screen.add(paddedString.toString());
                }

                paddedString = new StringBuilder();
                paddedString.append(getSpace());
                paddedString.append(addColor(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE, ANSIColors.GREEN).repeat(width - 3));
                paddedString.append(addColor(UnicodeCharacters.SINGLE_LINE_BR_CORNER, ANSIColors.GREEN));
                paddedString.append(getSpace());
                screen.add(paddedString.toString());
            }
            // 2 --> Shield is covering bottom and left sides
            case 2 -> {
                for (int i = 1; i < height; i++) {
                    paddedString = new StringBuilder();
                    paddedString.append(getSpace());
                    paddedString.append(addColor(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE, ANSIColors.GREEN));

                    if (i == (height / 2) + 1) {
                        padding = (width - nameAlias.length()) / 2;
                        paddedString.append(getSpace().repeat(padding - 1));
                        paddedString.append(addColor(nameAlias, ANSIColors.GREEN));
                        paddedString.append(getSpace().repeat(padding));
                    }
                    else {
                        paddedString.append(getSpace().repeat(width - 2));
                    }

                    screen.add(paddedString.toString());
                }

                paddedString = new StringBuilder();
                paddedString.append(getSpace());
                paddedString.append(addColor(UnicodeCharacters.SINGLE_LINE_BL_CORNER, ANSIColors.GREEN));
                paddedString.append(addColor(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE, ANSIColors.GREEN).repeat(width - 3));
                paddedString.append(getSpace());
                screen.add(paddedString.toString());
            }
            // 3 --> Shield is covering left and top sides
            case 3 -> {
                paddedString = new StringBuilder();
                paddedString.append(getSpace());
                paddedString.append(addColor(UnicodeCharacters.SINGLE_LINE_TL_CORNER, ANSIColors.GREEN));
                paddedString.append(addColor(UnicodeCharacters.HORIZONTAL_TOP_SINGLE_LINE, ANSIColors.GREEN).repeat(width - 3));
                paddedString.append(getSpace());
                screen.add(paddedString.toString());

                for (int i = 1; i < height; i++) {
                    paddedString = new StringBuilder();
                    paddedString.append(getSpace());
                    paddedString.append(addColor(UnicodeCharacters.VERTICAL_RIGHT_SINGLE_LINE, ANSIColors.GREEN));

                    if (i == (height / 2)) {
                        padding = (width - nameAlias.length()) / 2;
                        paddedString.append(getSpace().repeat(padding - 1));
                        paddedString.append(addColor(nameAlias, ANSIColors.GREEN));
                        paddedString.append(getSpace().repeat(padding));
                    }
                    else {
                        paddedString.append(getSpace().repeat(width - 2));
                    }

                    screen.add(paddedString.toString());
                }
            }
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}