package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Storage extends Component {
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
      protected void setComponentScreen(WidgetTUI componentWidget) throws NullWidgetException {
            if (componentWidget == null) {
                  throw new NullWidgetException("ERROR: Given widget is null (Cannot add screen)");
            }

            int height = componentWidget.getHeight();
            int width = componentWidget.getWidth();

            List<String> screen = new ArrayList<String>();
            String nameAlias = PrintUtils.getSpace() + ComponentAlias.STORAGE.getAlias();

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace().repeat(width));
            }

            componentWidget.setScreen(screen);
      }
      /*
      // Writes the component's name
          padding = width - 2 - 1 - ComponentAlias.STORAGE.getAlias().length();
          paddedString = new StringBuilder(SPACE + ComponentAlias.STORAGE.getAlias());

          paddedString.append(SPACE.repeat(padding));
          componentInfo.add(paddedString.toString());

          int maxCapacity = storage.getCapacity();
          int occupiedSlots = storage.getStoredItems().size();
          int storageStringLength = 2 * maxCapacity - 1;
          int currItemIndex = 0;

          for (int i = 1; i < height - 2; i++) {
              paddedString = new StringBuilder();

              for (int j = 1; j < width - 1; j++) {
                  if (i == height / 2 && j == width / 2) {
                      padding = (width - 2 - storageStringLength) / 2;
                      paddedString = new StringBuilder();

                      // Padding before the storage indicator
                      paddedString.append(SPACE.repeat(padding));

                      // Adding alternated storage indicators
                      for (int k = 0; k < storageStringLength; k++) {
                          if (k % 2 == 0) {
                              if (occupiedSlots > 0) {
                                  switch (storage.getStoredItems().get(currItemIndex).getColor()) {
                                      case RED -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_RED));
                                      case YELLOW -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_YELLOW));
                                      case GREEN -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_GREEN));
                                      case BLUE -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_BLUE));
                                  }
                                  occupiedSlots--;
                                  currItemIndex++;
                              }
                              else {
                                  paddedString.append("\u2588");
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
              componentInfo.add(paddedString.toString());
          }
       */
}