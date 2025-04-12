package it.polimi.ingsw.is25am28.GameModel.FileLoader;

import java.util.Map;

import it.polimi.ingsw.is25am28.Model.Components.Battery;
import it.polimi.ingsw.is25am28.Model.Components.Cabin;
import it.polimi.ingsw.is25am28.Model.Components.Cannon;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Engine;
import it.polimi.ingsw.is25am28.Model.Components.Shield;
import it.polimi.ingsw.is25am28.Model.Components.Storage;
import it.polimi.ingsw.is25am28.Model.Components.Structural;
import it.polimi.ingsw.is25am28.Model.Components.Vital;

import java.util.ArrayList;
import java.util.List;

public class TileLoader extends FileLoader {
      static private TileLoader instance;

      public static TileLoader get(){
            if( instance == null )
                  instance = new TileLoader();
            return instance;
      }
      
      private TileLoader(){
            super("./json/tiles.json");
      }

      public List<Component> read(){
            final List<Component> components = new ArrayList<>();
            Map<String,List<Map<String,Object>>> json = getJSONObject();

            json.get("cannon").forEach( cannon -> 
                  components.add(
                        new Cannon( 
                              (List<Integer>)cannon.get("connectors"), 
                              (Integer)cannon.get("force")
                        )
                  )
            );

            json.get("shield").forEach( 
                  shield -> components.add(
                        new Shield( (List<Integer>)shield.get("connectors") )
                  )
            );
            
            json.get("structural").forEach( 
                  structural -> components.add(
                        new Structural( (List<Integer>)structural.get("connectors") )
                  )
            );

            json.get("cabin").forEach( 
                  cabin -> components.add(
                        new Cabin( (List<Integer>)cabin.get("connectors"), false )
                  )
            );

            json.get("engine").forEach( engine -> 
                  components.add(
                        new Engine( 
                              (List<Integer>)engine.get("connectors"), 
                              (Integer)engine.get("speed")
                        )
                  )
            );

            json.get("battery").forEach( battery -> 
                  components.add(
                        new Battery( 
                              (List<Integer>)battery.get("connectors"), 
                              (Integer)battery.get("capacity")
                        )
                  )
            );

            json.get("vital").forEach( vital -> 
                  components.add(
                        new Vital( 
                              (List<Integer>)vital.get("connectors"), 
                              (Integer)vital.get("type")
                        )
                  )
            );

            json.get("storage").forEach( storage -> 
                  components.add(
                        new Storage( 
                              (List<Integer>)storage.get("connectors"), 
                              (Integer)storage.get("capacity"),
                              (Boolean)storage.get("special")
                        )
                  )
            );
            
            for( int i = 0; i < components.size(); i++ ){
                  components.get(i).setId(i);
            }

            return components;
      }
}
