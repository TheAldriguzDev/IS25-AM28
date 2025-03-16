package it.polimi.ingsw.is25am28.GameModel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polimi.ingsw.is25am28.Components.Battery;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Engine;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.Components.Storage;
import it.polimi.ingsw.is25am28.Components.Structural;
import it.polimi.ingsw.is25am28.Components.Vital;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.EventCards.MeteorShower;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.EventCards.AbandonedStation;

public class FileLoader {
      private final JSONObject json;

      private static void forEachTile( BiConsumer<JSONObject,int[]> lambda, JSONArray array ){
            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;
                  int[] connectors = new int[4];

                  for( int i = 0; i < 4; i++ ){
                        connectors[i] = (Integer)((JSONArray)o.get("connectors")).get(i);
                  }
                  lambda.accept(o, connectors);
            }
      }

      public FileLoader( String fileName ){
            try {
                  FileReader file = new FileReader(fileName);
                  JSONParser parser = new JSONParser();
                  json = (JSONObject)parser.parse(file);
            }
            catch(FileNotFoundException e){
                  throw new Error("file not found");
            }
            catch(IOException e){
                  throw new Error("file not found");
            }
            catch(ParseException e){
                  throw new Error("json not correctly initialized");
            }
      }

      public List<Component> getAllComponents(){
            final List<Component> components = new ArrayList<>();

            JSONArray comp = (JSONArray)json.get("cannon" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Cannon( connectors, (Integer)o.get("force") ))
            , comp);
            

            comp = (JSONArray)json.get("shield" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Shield( connectors ))
            , comp);

            comp = (JSONArray)json.get("structural" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Structural( connectors ))
            , comp);

            comp = (JSONArray)json.get("cabin" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Cabin( connectors, false ))
            , comp);

            comp = (JSONArray)json.get("engine" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Engine( connectors, (Integer)o.get("speed") ))
            , comp);

            comp = (JSONArray)json.get("battery" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Battery( connectors, (Integer)o.get("capacity") ))
            , comp);

            comp = (JSONArray)json.get("vitals" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Vital( connectors, (Integer)o.get("type") ))
            , comp);

            comp = (JSONArray)json.get("storage" );

            FileLoader.forEachTile(
                  (o,connectors) -> components.add(new Storage( connectors, (Integer)o.get("capacity"), (Boolean)o.get("special")  ))
            , comp);


            return components;
      }

      public List<EventCard> getAllCards(){
            final List<EventCard> deck = new ArrayList<>();

            JSONArray array = (JSONArray)json.get("abandonedShip" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new AbandonedShip(
                        "nave abbandonata", 
                        (Integer)o.get("level"), 
                        (Integer)o.get("people"), 
                        (Integer)o.get("days"), 
                        (Integer)o.get("credits")
                  ));
            }

            array = (JSONArray)json.get("abandonedStation" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new AbandonedStation(
                        "stazione abbandonata", 
                        (Integer)o.get("level"), 
                        (Integer)o.get("people"), 
                        (Integer)o.get("days"), 
                        (Integer)o.get("red"),
                        (Integer)o.get("yellow"),
                        (Integer)o.get("green"),
                        (Integer)o.get("blue")
                        ));
            }

            array = (JSONArray)json.get("meteors" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;
                  List<Integer> top = new ArrayList<>();
                  List<Integer> bottom = new ArrayList<>();
                  List<Integer> left = new ArrayList<>();
                  List<Integer> right = new ArrayList<>();

                  for(Object meteor: (JSONArray)o.get("top") ){
                        top.add((Integer)meteor);
                  }

                  for(Object meteor: (JSONArray)o.get("bottom") ){
                        bottom.add((Integer)meteor);
                  }

                  for(Object meteor: (JSONArray)o.get("left") ){
                        left.add((Integer)meteor);
                  }

                  for(Object meteor: (JSONArray)o.get("right") ){
                        right.add((Integer)meteor);
                  }

                  deck.add(new MeteorShower(
                        "meteore", 
                        (Integer)o.get("level"), 
                        top,
                        bottom,
                        left,
                        right
                  ));
            }

            return deck;
      }
}
