package it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

/**
 * Represents a data transfer data object that holds information about a disconnected player
 *
 * * Annotations from the Jackson library are used for JSON serialization and deserialization,
 *  * ensuring that only non-null values are included in the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DisconnectedPlayerDTO extends StateDTO {
    private String nickname;

    public DisconnectedPlayerDTO() {}

    public DisconnectedPlayerDTO(
            @JsonProperty("nickname") String nickname
    ) {
        this.nickname = nickname;
    }

    @JsonGetter("nickname")
    public String getNickname() {
        return nickname;
    }

    @JsonSetter("nickname")
    public DisconnectedPlayerDTO setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
