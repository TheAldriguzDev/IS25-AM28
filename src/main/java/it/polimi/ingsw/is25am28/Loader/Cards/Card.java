package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public sealed abstract class Card permits AbandonedShip, AbandonedStation, Epidemic, Meteor, OpenSpace, Pirate, Planet, Slaver, Smuggler, Stardust, Warzone {
    private Integer level;
    private String path;


    @JsonGetter("level")
    public Integer getLevel() {
        return level;
    }

    @JsonSetter("level")
    public void setLevel(Integer level) {
        this.level = level;
    }

    @JsonGetter("path")
    public String getPath() {
        return path;
    }

    @JsonSetter("path")
    public void setPath(String path) {
        this.path = path;
    }
}
