package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.*;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.TUI.PrintUtils.*;

public final class Storage extends Component {
    public static final String alias = "STORAGE";
    final int capacity;
    final boolean isSpecialStorage;
    final List<Item> storedItems;

    public Storage(List<Integer> connectors, int capacity, boolean isSpecialStorage) {
        super(connectors);
        this.capacity = capacity;
        this.isSpecialStorage = isSpecialStorage;
        storedItems = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isSpecialStorage() {
        return isSpecialStorage;
    }

    public List<Item> getStoredItems() {
        return storedItems;
    }

    /**
     * @param item The item to store inside this Storage component
     * @throws IllegalArgumentException If the storage is full or if someone tries to put
     *         a RED item into a non-special storage component
     */
    public void storeItem(Item item) throws IllegalArgumentException{
        if (!isSpecialStorage && item.getColor() == ItemColor.RED) {
            throw new IllegalArgumentException("You can't store a special item in a normal storage unit!");
        }

        if (this.capacity == storedItems.size()) {
            throw new IllegalArgumentException("You can't store more than " + this.capacity + " items!");
        }

        // Store the items in order of value
        int idx = 0;
        while (idx < storedItems.size() && item.getValue() < storedItems.get(idx).getValue()) {
            idx++;
        }

        storedItems.add(idx, item);
    }

    /**
     * @param item The item to remove from this storage component
     */
    public void removeItem(Item item) {
        storedItems.remove(item);
    }

    /**
     * @return The units of space that are currently free
     */
    public int availableSpace() {
        return capacity - storedItems.size();
    }

    @Override
    public Map<String,Object> toMap() {
        Map<String,Object> map = super.toMap();

        map.put("capacity", capacity );
        map.put("special", isSpecialStorage );
        map.put("storedItems", storedItems.stream().map( item -> item.getValue() ).toList() );


        return map;
    }

    @Override
    public List<String> getComponentScreen() {
        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;

        int maxCapacity = this.getCapacity();
        int occupiedSlots = this.getStoredItems().size();
        int storageStringLength = 2 * maxCapacity - 1;
        int currItemIndex = 0;
        int padding;

        List<String> screen = new ArrayList<String>();
        StringBuilder paddedString;
        String nameAlias;

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = generateComponentCustomBorder();

        // Adding the name (if this is a special storage, it gets colored with RED)
        if (this.isSpecialStorage) {
            nameAlias = SPACE + addColor(Storage.alias, ANSIColors.RED);
        }
        else {
            nameAlias = SPACE + Storage.alias;
        }

        screen.add(nameAlias + SPACE.repeat(width - Storage.alias.length() - 1));

        // Adding the storage string and the padding
        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();

            for (int j = 0; j < width; j++) {
                if (i == ((height / 2) + 1) && j == ((width / 2) + 1)) {
                    padding = (width - storageStringLength) / 2;
                    paddedString = new StringBuilder();

                    // Padding before the storage indicator
                    paddedString.append(SPACE.repeat(padding));

                    // Adding alternated storage indicators
                    for (int k = 0; k < storageStringLength; k++) {
                        if (k % 2 == 0) {
                            if (occupiedSlots > 0) {
                                switch (this.getStoredItems().get(currItemIndex).getColor()) {
                                    case RED -> paddedString.append(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, ANSIColors.BRIGHT_RED));
                                    case YELLOW -> paddedString.append(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, ANSIColors.BRIGHT_YELLOW));
                                    case GREEN -> paddedString.append(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, ANSIColors.BRIGHT_GREEN));
                                    case BLUE -> paddedString.append(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, ANSIColors.BRIGHT_BLUE));
                                }
                                occupiedSlots--;
                                currItemIndex++;
                            }
                            else {
                                paddedString.append(UnicodeCharacters.FULL_BLOCK);
                            }
                        }
                        else {
                            paddedString.append(SPACE);
                        }
                    }

                    // Padding after the storage indicator
                    paddedString.append(SPACE.repeat(padding));
                    break;
                }
                else {
                    paddedString.append(SPACE);
                }
            }
            screen.add(paddedString.toString());
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}