package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 * This class helps use to deal with the JSON data to take / drop resources from / to a user
 * */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ComponentHelper<T> implements Serializable {
    @JsonProperty("i")
    private int i;
    @JsonProperty("j")
    private int j;

    @JsonProperty("item")
    private T item;

    public ComponentHelper() {}

    public ComponentHelper(int i, int j) {
        this.i = i;
        this.j = j;
        this.item = null;
    }

    @JsonGetter("i")
    public int getI() {
        return i;
    }

    @JsonGetter("j")
    public int getJ() {
        return j;
    }

    @JsonGetter("item")
    public Optional<T> getItem() {
        return Optional.ofNullable(this.item);
    }

    @JsonSetter("item")
    public ComponentHelper<T> addItem(T item) {
        this.item = item;
        return this;
    }
}
