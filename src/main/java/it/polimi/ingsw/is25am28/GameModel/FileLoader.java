package it.polimi.ingsw.is25am28.GameModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polimi.ingsw.is25am28.Board.Board;
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
import it.polimi.ingsw.is25am28.EventCards.OpenSpace;
import it.polimi.ingsw.is25am28.EventCards.Pirates;
import it.polimi.ingsw.is25am28.EventCards.Slavers;
import it.polimi.ingsw.is25am28.EventCards.VisitPlanets;
import it.polimi.ingsw.is25am28.EventCards.WarZone;
import it.polimi.ingsw.is25am28.EventCards.Smugglers;
import it.polimi.ingsw.is25am28.EventCards.Stardust;
import it.polimi.ingsw.is25am28.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.EventCards.AbandonedStation;
import it.polimi.ingsw.is25am28.EventCards.Epidemy;

public class FileLoader {
      private final JSONObject json;

      private static void forEachTile( BiConsumer<JSONObject,int[]> lambda, JSONArray array ){
            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;
                  int[] connectors = new int[4];

                  for( int i = 0; i < 4; i++ ){
                        connectors[i] = ((Long)((JSONArray)o.get("connectors")).get(i)).intValue();
                  }
                  lambda.accept(o, connectors);
            }
      }

      /**
       *  file path must be relative to "~/IS25-AM28"
       * @param fileName
       */
      public FileLoader( String fileName ){
            try {
                  System.out.println(System.getProperty("user.dir"));
                  FileReader file = new FileReader(fileName);
                  JSONParser parser = new JSONParser();
                  json = (JSONObject)parser.parse(file);
            }
            catch(FileNotFoundException e){
                  throw new Error("file not found with error: " + e.getMessage());
            }
            catch(IOException e){
                  throw new Error("file not found with error: " + e.getMessage());
            }
            catch(ParseException e){
                  throw new Error("json not correctly initialized");
            }
      }

      public List<Component> getAllComponents(){
            final List<Component> components = new ArrayList<>();

            JSONArray comp = (JSONArray)json.get("cannon" );

            FileLoader.forEachTile(
                    (o,connectors) -> components.add(new Cannon( connectors, ((Long)o.get("force")).intValue() ))
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
                    (o,connectors) -> components.add(new Engine( connectors, ((Long)o.get("speed")).intValue() ))
                    , comp);

            comp = (JSONArray)json.get("battery" );

            FileLoader.forEachTile(
                    (o,connectors) -> components.add(new Battery( connectors, ((Long)o.get("capacity")).intValue() ))
                    , comp);

            comp = (JSONArray)json.get("vital" );

            FileLoader.forEachTile(
                    (o,connectors) -> components.add(new Vital( connectors, ((Long)o.get("type")).intValue() ))
                    , comp);

            comp = (JSONArray)json.get("storage" );

            FileLoader.forEachTile(
                    (o,connectors) -> components.add(new Storage( connectors, ((Long)o.get("capacity")).intValue(), (Boolean)o.get("special")  ))
                    , comp);


            return components;
      }

      public List<EventCard> getAllCards(Board board, ResourceBank resourceBank){
            final List<EventCard> deck = new ArrayList<>();

            JSONArray array = (JSONArray)json.get("abandonedShip" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new AbandonedShip(
                          "nave abbandonata",
                          ((Long)o.get("level")).intValue(),
                          ((Long)o.get("people")).intValue(),
                          ((Long)o.get("days")).intValue(),
                          ((Long)o.get("credits")).intValue(),
                          board
                  ));
            }

            array = (JSONArray)json.get("abandonedStation" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new AbandonedStation(
                          "stazione abbandonata",
                          ((Long)o.get("level")).intValue(),
                          ((Long)o.get("people")).intValue(),
                          ((Long)o.get("days")).intValue(),
                          //((Long)o.get("red")).intValue(),
                          //((Long)o.get("yellow")).intValue(),
                          //((Long)o.get("green")).intValue(),
                          //((Long)o.get("blue")).intValue()
                          null,      // REQUIRES AN ARRAY LIST OF ITEMS
                          board,
                          resourceBank
                  ));
            }

            array = (JSONArray)json.get("meteors" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new MeteorShower(
                          "meteore",
                          ((Long)o.get("level")).intValue(),
                          ((JSONArray) o.get("Meteors")),
                          board
                  ));
            }

            array = (JSONArray)json.get("pirates" );

            for( Object proxy: array ){

                  JSONObject o = (JSONObject)proxy;

                  deck.add(new Pirates(
                          "pirati",
                          ((Long)o.get("level")).intValue(),
                          ((Long)o.get("firepower")).intValue(),
                          ((Long)o.get("credits")).intValue(),
                          ((Long)o.get("days")).intValue(),
                          ((JSONArray)o.get("shoots")),
                          board
                  ));
            }

            array = (JSONArray)json.get("planets" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new VisitPlanets(
                          "Pianeti",
                          ((Long)o.get("level")).intValue(),
                          ((Long)o.get("days")).intValue(),
                          (JSONArray)o.get("planets"),
                          board
                  ));
            }

            array = (JSONArray)json.get("space" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new OpenSpace(
                          "Spazio aperto",
                          ((Long)o.get("level")).intValue(),
                          board
                  ));
            }

            array = (JSONArray)json.get("epidemic" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new Epidemy(
                          "Epidemia",
                          ((Long)o.get("level")).intValue(),
                          board
                  ));
            }

            array = (JSONArray)json.get("smugglers" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new Smugglers(
                          "Contrabbandieri",
                          ((Long)o.get("level")).intValue(),
                          ((Long)o.get("days")).intValue(),
                          ((Long)o.get("cannons")).intValue(),
                          ((Long)o.get("penalty")).intValue(),
                          ((Long)((JSONObject)o.get("storage")).get("red")).intValue(),
                          ((Long)((JSONObject)o.get("storage")).get("yellow")).intValue(),
                          ((Long)((JSONObject)o.get("storage")).get("green")).intValue(),
                          ((Long)((JSONObject)o.get("storage")).get("blue")).intValue(),
                          board,
                          resourceBank
                  ));
            }

            array = (JSONArray)json.get("slavers" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new Slavers(
                          "Schiavisti",
                          ((Long)o.get("level")).intValue(),
                          ((Long)o.get("cannons")).intValue(),
                          ((Long)o.get("days")).intValue(),
                          ((Long)o.get("credits")).intValue(),
                          ((Long)o.get("penalty")).intValue(),
                          board
                  ));
            }

            array = (JSONArray)json.get("stardust" );

            for( Object proxy: array ){
                  JSONObject o = (JSONObject)proxy;

                  deck.add(new Stardust(
                          "Polvere Stellare",
                          ((Long)o.get("level")).intValue(),
                          board
                  ));
            }

            array = (JSONArray)json.get("warzone" );

//            for( Object proxy: array ){
//                  JSONObject o = (JSONObject)proxy;
//
//                  deck.add(new WarZone(
//                          "Zona di Guerra",
//                          ((Long)o.get("level")).intValue(),
//                          (JSONObject)o.get("engines"),
//                          (JSONObject)o.get("cannons"),
//                          (JSONObject)o.get("humans"),
//                          board
//                  ));
//            }

            return deck;
      }
}