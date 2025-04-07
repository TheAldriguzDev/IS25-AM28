package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Lifeform.*;
import it.polimi.ingsw.is25am28.TUI.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.TUI.PrintUtils.*;

public final class Cabin extends Component {
    private final boolean isCore;
    private final ArrayList<Lifeform> inhabitants;

    public Cabin(List<Integer> connectors, boolean isCore) {
        super(connectors);
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
        inhabitants.remove(lifeform);
    }

    @Override
    public Map<String,Object> toMap(){
        Map<String,Object> map = super.toMap();

        map.put("inhabitants", inhabitants.stream().map( lifeform -> lifeform.getLifeformType() ).toList());


        return map;
    }

    @Override
    protected void setComponentScreen(WidgetTUI componentWidget) throws NullWidgetException {
        if (componentWidget == null) {
            throw new NullWidgetException("ERROR: Given widget is null (Cannot add screen)");
        }

        int height = componentWidget.getHeight();
        int width = componentWidget.getWidth();
        int padding;
        StringBuilder paddedString, nameAlias;
        List<String> tmpScreen = new ArrayList<String>();
        String cabinAlias = ComponentAlias.CABIN.getAlias();
        String coreLabel = "(*)";

        nameAlias = new StringBuilder();
        String nameAliasRightPadding = getSpace().repeat(width - cabinAlias.length() - 1);

        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();

            if (i == (height / 2) + 1) {
                if (this.getInhabitants().isEmpty()) {
                    // Case 0 - Empty cabin
                    padding = (width - 3) / 2;
                    nameAlias.append(getSpace());
                    nameAlias.append(cabinAlias);
                    nameAlias.append(nameAliasRightPadding);

                    paddedString.append(getSpace().repeat(padding));
                    paddedString.append(addColor(getSpace(), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                    paddedString.append(getSpace());
                    paddedString.append(addColor(getSpace(), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                    paddedString.append(getSpace().repeat(padding));
                }
                else {
                    LifeformType storedLifeformType = this.getInhabitants().getFirst().getLifeformType();

                    if (storedLifeformType == LifeformType.ASTRONAUT) {
                        // Case 1 - Cabin has ASTRONAUTS
                        padding = (width - 3) / 2;

                        // Creating the name
                        nameAlias.append(getSpace());
                        nameAlias.append(addColor(cabinAlias, ANSIColors.WHITE));

                        // Adds a GOLDEN STAR as a tag to specify that the cabin is the core
                        if (this.isCore()) {
                            nameAlias.append(getSpace());
                            nameAlias.append(addColor(coreLabel, ANSIColors.BRIGHT_YELLOW));
                            nameAlias.append(getSpace().repeat(width - coreLabel.length() - cabinAlias.length() - 2));
                        }
                        else {
                            // Otherwise just adds the remaining right padding spaces to the name alias string
                            nameAlias.append(nameAliasRightPadding);
                        }

                        // Creating the housing string
                        // Left padding
                        paddedString.append(getSpace().repeat(padding));

                        if (this.getInhabitants().size() > 1) {
                            paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            paddedString.append(getSpace());
                            paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                        }
                        else {
                            paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                            paddedString.append(getSpace());
                            paddedString.append(addColor(getSpace(), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                        }

                        // Right padding
                        paddedString.append(getSpace().repeat(padding));
                    }
                    else if (storedLifeformType == LifeformType.PURPLE_ALIEN) {
                        // Case 2 - Cabin has a PURPLE ALIEN

                        // Creating the name
                        nameAlias.append(getSpace());
                        nameAlias.append(addColor(ComponentAlias.CABIN.getAlias(), ANSIColors.MAGENTA));
                        nameAlias.append(nameAliasRightPadding);

                        padding = (width - 1) / 2;
                        paddedString.append(getSpace().repeat(padding));
                        paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BACKGROUND_MAGENTA));
                        paddedString.append(getSpace().repeat(padding));
                    }
                    else {
                        // Case 3 - Cabin has a BROWN ALIEN

                        // Creating the name
                        nameAlias.append(getSpace());
                        nameAlias.append(addColor(cabinAlias, ANSIColors.YELLOW));
                        nameAlias.append(nameAliasRightPadding);

                        padding = (width - 1) / 2;
                        paddedString.append(getSpace().repeat(padding));
                        paddedString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BACKGROUND_YELLOW));
                        paddedString.append(getSpace().repeat(padding));
                    }
                }

                // Adding the line containing the housed lifeforms (if there are any)
                tmpScreen.add(paddedString.toString());
            }
            else {
                // Adding a line full of spaces
                tmpScreen.add(getSpace().repeat(width));
            }
        }

        // Appending all the strings to the component widget's screen
        List<String> finalScreen = new ArrayList<String>();

        finalScreen.add(nameAlias.toString());
        finalScreen.addAll(tmpScreen);

        componentWidget.setScreen(finalScreen);
    }
}
