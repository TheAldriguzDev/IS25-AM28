package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
/**
 * Class that offers a really light component representation
 */
public class ComponentJSON {
      @JsonProperty("lifeforms")
      private LifeformType lifeforms;

      @JsonProperty("id")
      private Integer id;

      @JsonProperty("rotation")
      private Integer rotation = 0;
      
      // if null, there isn't a component 
      public Integer getId() {
            return id;
      }

      public ComponentJSON setId(Integer id) {
            this.id = id;
            return this;
      }

      public Integer getRotation() {
            return rotation;
      }

      public ComponentJSON setRotation(Integer rotation) {
            this.rotation = rotation;
            return this;
      }

      public LifeformType getLifeforms() {
            return lifeforms;
      }

      public ComponentJSON setLifeforms( LifeformType lifeforms ) {
            this.lifeforms = lifeforms;
            return this;
      }
}
