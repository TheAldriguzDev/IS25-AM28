package it.polimi.ingsw.is25am28.Utils.Pair;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

public class Pair <K, V> implements Serializable {
    @JsonProperty("key") private K key;
    @JsonProperty("value") private V value;

    @JsonCreator
    public Pair() {}

    @JsonCreator
    public Pair(
            @JsonProperty("key") K key,
            @JsonProperty("value") V value
    ) {
        this.key = key;
        this.value = value;
    }

    @JsonGetter("key")
    public K getKey() {
        return key;
    }

    @JsonSetter("key")
    public void setKey(K key) {
        this.key = key;
    }

    @JsonGetter("value")
    public V getValue() {
        return value;
    }

    @JsonSetter("value")
    public void setValue(V value) {
        this.value = value;
    }
}
