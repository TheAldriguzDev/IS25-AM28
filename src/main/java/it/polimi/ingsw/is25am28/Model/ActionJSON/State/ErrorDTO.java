package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public final class ErrorDTO extends StateDTO {
    private String message;

    public ErrorDTO() {}

    public ErrorDTO(@JsonProperty("message") String message) {
        this.message = message;
    }

    @JsonGetter("message")
    public String getMessage() {
        return message;
    }

    @JsonSetter("message")
    public ErrorDTO setMessage(String message) {
        this.message = message;
        return this;
    }
}
