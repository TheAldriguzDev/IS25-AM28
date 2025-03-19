package it.polimi.ingsw.is25am28.ActionJSON;

import it.polimi.ingsw.is25am28.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This class helps use to deal with the JSON data to take / drop resources from / to a user
 * */
public class ComponentHelper<T> {
    private int i;
    private int j;
    private Optional<T> helper;

    public ComponentHelper(int i, int j) {
        this.i = i;
        this.j = j;
        this.helper = Optional.empty();
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public Optional<T> getItem() {
        return this.helper;
    }

    public void addItem(T item) {
        if (item != null) {
            this.helper = Optional.of(item);
        } else {
            this.helper = Optional.empty();
        }
    }
}
