package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.*;

public final class Cabin extends Component {
    public static final String alias = "CABIN";
    private final boolean isCore;
    private final ArrayList<Lifeform> inhabitants;

    public Cabin(List<Integer> connectors, boolean isCore, String path) {
        super(connectors, path);
        this.isCore = isCore;
        this.inhabitants = new ArrayList<>();

        if (isCore) {
            this.inhabitants.add(new Lifeform(LifeformType.ASTRONAUT));
            this.inhabitants.add(new Lifeform(LifeformType.ASTRONAUT));
        }
    }

    /**
     * @return If the current cabin is the ship's core
     */
    public boolean isCore() {
        return isCore;
    }

    /**
     * @return This cabin's current inhabitants
     */
    public List<Lifeform> getInhabitants() {
        return inhabitants;
    }

    /**
     * @return The amount of space available in the current cabin
     */
    public int getAvailableSpace() {
        return 2 - inhabitants
                .stream()
                .mapToInt(Lifeform::getRequiredSpace)
                .sum();
    }

    /**
     * @param lifeform The lifeform that will live in this cabin
     * @throws IllegalArgumentException If anyone tries to put an alien inside the core cabin or if the
     *                                  cabin is overcrowded and thus can't house any other lifeforms
     */
    public void addInhabitant(Lifeform lifeform) throws IllegalArgumentException {
        if (isCore && (lifeform.getLifeformType() == LifeformType.PURPLE_ALIEN || lifeform.getLifeformType() == LifeformType.BROWN_ALIEN)) {
            throw new IllegalArgumentException("ERROR: You can't add an alien in the core cabin!");
        }
        if (lifeform.getRequiredSpace() > this.getAvailableSpace()) {
            throw new IllegalArgumentException("ERROR: No required space left!");
        }

        inhabitants.add(lifeform);
    }

    /**
     * @param lifeform The lifeform that will be removed from the current cabin
     */
    public void removeInhabitant(Lifeform lifeform) {
        this.inhabitants.remove(lifeform);
    }

    @Override
    public Map<String,Object> toMap(){
        Map<String,Object> map = super.toMap();

        map.put("inhabitants", inhabitants.stream().map(Lifeform::getLifeformType).toList());

        return map;
    }

    @Override
    public List<String> getComponentScreen() {
        int padding;
        StringBuilder paddedString, nameAlias;
        List<String> tmpScreen = new ArrayList<String>();
        String coreLabel = "(*)";

        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = generateComponentCustomBorder();

        nameAlias = new StringBuilder();
        String nameAliasRightPadding = SPACE.repeat(width - Cabin.alias.length() - 1);

        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();

            if (i == (height / 2) + 1) {
                if (this.getInhabitants().isEmpty()) {
                    // Case 0 - Empty cabin
                    padding = (width - 3) / 2;
                    nameAlias.append(SPACE);
                    nameAlias.append(Cabin.alias);
                    nameAlias.append(nameAliasRightPadding);

                    paddedString.append(SPACE.repeat(padding));
                    paddedString.append(addColor(SPACE, ANSIColors.BRIGHT_BACKGROUND_WHITE));
                    paddedString.append(SPACE);
                    paddedString.append(addColor(SPACE, ANSIColors.BRIGHT_BACKGROUND_WHITE));
                    paddedString.append(SPACE.repeat(padding));
                }
                else {
                    LifeformType storedLifeformType = this.getInhabitants().getFirst().getLifeformType();

                    if (storedLifeformType == LifeformType.ASTRONAUT) {
                        // Case 1 - Cabin has ASTRONAUTS
                        padding = (width - 3) / 2;

                        // Creating the name
                        nameAlias.append(SPACE);
                        nameAlias.append(addColor(Cabin.alias, ANSIColors.WHITE));

                        // Adds a GOLDEN STAR as a tag to specify that the cabin is the core
                        if (this.isCore()) {
                            nameAlias.append(SPACE);
                            nameAlias.append(addColor(coreLabel, ANSIColors.BRIGHT_YELLOW));
                            nameAlias.append(SPACE.repeat(width - coreLabel.length() - Cabin.alias.length() - 2));
                        }
                        else {
                            // Otherwise just adds the remaining right padding spaces to the name alias string
                            nameAlias.append(nameAliasRightPadding);
                        }

                        // Creating the housing string
                        // Left padding
                        paddedString.append(SPACE.repeat(padding));

                        if (this.getInhabitants().size() > 1) {
                            paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            // paddedString.append(addColor(addColor(UnicodeCharacters.ASTRONAUT_EMOJI, ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            paddedString.append(SPACE);
                            paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            // paddedString.append(addColor(addColor(UnicodeCharacters.ASTRONAUT_EMOJI, ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                        }
                        else {
                            paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            // paddedString.append(addColor(addColor(UnicodeCharacters.ASTRONAUT_EMOJI, ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            paddedString.append(SPACE);
                            paddedString.append(addColor(SPACE, ANSIColors.BRIGHT_BACKGROUND_WHITE));
                        }

                        // Right padding
                        paddedString.append(SPACE.repeat(padding));
                    }
                    else if (storedLifeformType == LifeformType.PURPLE_ALIEN) {
                        // Case 2 - Cabin has a PURPLE ALIEN

                        // Creating the name
                        nameAlias.append(SPACE);
                        nameAlias.append(addColor(Cabin.alias, ANSIColors.MAGENTA));
                        nameAlias.append(nameAliasRightPadding);

                        padding = (width - 1) / 2;
                        paddedString.append(SPACE.repeat(padding));
                        paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BACKGROUND_MAGENTA));
                        // paddedString.append(addColor(addColor(UnicodeCharacters.ALIEN_EMOJI, ANSIColors.BLACK), ANSIColors.BACKGROUND_MAGENTA));
                        paddedString.append(SPACE.repeat(padding));
                    }
                    else {
                        // Case 3 - Cabin has a BROWN ALIEN

                        // Creating the name
                        nameAlias.append(SPACE);
                        nameAlias.append(addColor(Cabin.alias, ANSIColors.YELLOW));
                        nameAlias.append(nameAliasRightPadding);

                        padding = (width - 1) / 2;
                        paddedString.append(SPACE.repeat(padding));
                        paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BACKGROUND_YELLOW));
                        // paddedString.append(addColor(addColor(UnicodeCharacters.ALIEN_EMOJI, ANSIColors.BLACK), ANSIColors.BACKGROUND_YELLOW));
                        paddedString.append(SPACE.repeat(padding));
                    }
                }

                // Adding the line containing the housed lifeforms (if there are any)
                tmpScreen.add(paddedString.toString());
            }
            else {
                // Adding a line full of spaces
                tmpScreen.add(SPACE.repeat(width));
            }
        }

        // Appending all the strings to the component widget's screen
        List<String> screen = new ArrayList<String>();

        screen.add(nameAlias.toString());
        screen.addAll(tmpScreen);

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}
