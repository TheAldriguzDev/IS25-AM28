package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

/**
 * Class that offers a really light component representation
 */
public class ComponentJSON {
      @JsonProperty("lifeforms")    private LifeformType lifeforms;
      @JsonProperty("id")           private Integer id;
      @JsonProperty("rotation")     private Integer rotation = 0;

      @JsonCreator
      public ComponentJSON() {}

      @JsonCreator
      public ComponentJSON(
            @JsonProperty("lifeforms") LifeformType lifeforms,
            @JsonProperty("id") Integer id,
            @JsonProperty("rotation") Integer rotation
      ) {
            this.lifeforms = lifeforms;
            this.id = id;
            this.rotation = rotation;
      }

      @JsonGetter("id")
      public Integer getId() {
            return this.id;
      }

      @JsonSetter("id")
      public ComponentJSON setId(Integer id) {
            this.id = id;
            return this;
      }

      @JsonGetter("rotation")
      public Integer getRotation() {
            return rotation;
      }

      @JsonSetter("rotation")
      public ComponentJSON setRotation(Integer rotation) {
            this.rotation = rotation;
            return this;
      }

      @JsonGetter("lifeforms")
      public LifeformType getLifeforms() {
            return lifeforms;
      }

      @JsonSetter("lifeforms")
      public ComponentJSON setLifeforms(LifeformType lifeforms) {
            this.lifeforms = lifeforms;
            return this;
      }
}
