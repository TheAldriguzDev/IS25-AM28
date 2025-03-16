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
import it.polimi.ingsw.is25am28.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.EventCards.AbandonedStation;
import it.polimi.ingsw.is25am28.EventCards.EventCard;

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
                        (Integer)o.get("level")
                        ));
            }

            return deck;
      }
}
