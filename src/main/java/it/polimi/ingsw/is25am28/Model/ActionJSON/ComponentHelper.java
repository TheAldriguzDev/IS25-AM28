package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonAutoDetect;


/**
 * This class helps use to deal with the JSON data to take / drop resources from / to a user
 * */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ComponentHelper<T> implements Serializable {
    private int i;
    private int j;

    @JsonProperty
    private T helper;

    public ComponentHelper() {}

    public ComponentHelper(int i, int j) {
        this.i = i;
        this.j = j;
        this.helper = null;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public Optional<T> getItem() {
        return Optional.ofNullable(this.helper);
    }

    public ComponentHelper<T> addItem(T item) {
        this.helper = item;
        return this;
    }
}
