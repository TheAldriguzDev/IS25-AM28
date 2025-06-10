package it.polimi.ingsw.is25am28.Network.Answer;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

/**
 * ErrorAnswer is a subclass of Answer that allows the server to target a specific client
 * in order to notify them about an error that has occurred.
 */

public final class ErrorAnswer extends Answer implements Serializable {
    private String error;

    public ErrorAnswer(@JsonProperty("message") String error) {
        this.error = error;
    }

    @JsonGetter("message")
    public String getError() {
        return error;
    }

    @JsonSetter("message")
    public void setError(String error) {
        this.error = error;
    }
}
