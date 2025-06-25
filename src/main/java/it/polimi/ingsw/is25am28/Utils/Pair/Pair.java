package it.polimi.ingsw.is25am28.Utils.Pair;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

/**
 * A serializable generic pair consisting of a key and a value.
 * Useful for network communication where serialization is required.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public class Pair <K, V> implements Serializable {
    @JsonProperty("key") private K key;
    @JsonProperty("value") private V value;

    @JsonCreator
    public Pair() {}

    /**
     * Constructs a pair consisting of a key and a value.
     *
     * @param key   the key of the pair, represented by the generic type K
     * @param value the value of the pair, represented by the generic type V
     */
    @JsonCreator
    public Pair(
            @JsonProperty("key") K key,
            @JsonProperty("value") V value
    ) {
        this.key = key;
        this.value = value;
    }

    /**
     * Retrieves the key of this pair.
     *
     * @return the key of the pair, represented by the generic type K
     */
    @JsonGetter("key")
    public K getKey() {
        return key;
    }

    /**
     * Sets the value of the key associated with this pair.
     *
     * @param key the new key value to be set, represented by the generic type K
     */
    @JsonSetter("key")
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Retrieves the value of this pair.
     *
     * @return the value of the pair, represented by the generic type V
     */
    @JsonGetter("value")
    public V getValue() {
        return value;
    }

    /**
     * Sets the value associated with this pair.
     *
     * @param value the new value to be set, represented by the generic type V
     */
    @JsonSetter("value")
    public void setValue(V value) {
        this.value = value;
    }
}
