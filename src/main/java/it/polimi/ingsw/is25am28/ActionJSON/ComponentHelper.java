package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonAutoDetect;


/**
 * This class helps use to deal with the JSON data to take / drop resources from / to a user
 * */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ComponentHelper<T> {
    private int i;
    private int j;

    @JsonProperty
    private Optional<T> helper;

    public ComponentHelper() {}

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

    public ComponentHelper<T> addItem(T item) {
        if (item != null) {
            this.helper = Optional.of(item);
        } else {
            this.helper = Optional.empty();
        }

        return this;
    }
}
