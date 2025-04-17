package it.polimi.ingsw.is25am28.FileLoader;

import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Model.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.Model.EventCards.AbandonedStation;
import it.polimi.ingsw.is25am28.Model.EventCards.Epidemy;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.EventCards.MeteorShower;
import it.polimi.ingsw.is25am28.Model.EventCards.OpenSpace;
import it.polimi.ingsw.is25am28.Model.EventCards.Pirates;
import it.polimi.ingsw.is25am28.Model.EventCards.Slavers;
import it.polimi.ingsw.is25am28.Model.EventCards.Smugglers;
import it.polimi.ingsw.is25am28.Model.EventCards.Stardust;
import it.polimi.ingsw.is25am28.Model.EventCards.VisitPlanets;
import it.polimi.ingsw.is25am28.Model.EventCards.WarZone;
import it.polimi.ingsw.is25am28.Model.EventCards.WarZoneAction;
import it.polimi.ingsw.is25am28.Model.EventCards.WarZoneActionConsequencePair;
import it.polimi.ingsw.is25am28.Model.EventCards.WarZoneConsequence;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardLoader extends FileLoader {
      private static CardLoader instance;

      static public CardLoader get(){
            if( instance == null )
                  instance = new CardLoader();
            return instance;
      }

      private CardLoader(){
            super("./json/cards.json");
      }

      public List<EventCard> read( Board board, ResourceBank bank, int level ){
            final List<EventCard> deck = new ArrayList<>();
            Map<String,List<Map<String,Object>>> json = getJSONObject();

            json.get("abandonedShip" ).forEach( map -> {
                  deck.add(new AbandonedShip(
                        "Abandoned Ship",
                        (Integer)map.get("level"),
                        (Integer)map.get("people"),
                        (Integer)map.get("days"),
                        (Integer)map.get("credits"),
                        board
                  ));
            });


            json.get("abandonedStation" ).forEach( map -> {
                  ArrayList<Item> items = new ArrayList<>();

                  int len = (Integer)map.get("red");

                  for( int i = 0; i < len; i++ ){
                        items.add(new Item(ItemColor.RED));
                  }

                  len = (Integer)map.get("yellow");

                  for( int i = 0; i < len; i++ ){
                        items.add(new Item(ItemColor.YELLOW));
                  }

                  len = (Integer)map.get("green");

                  for( int i = 0; i < len; i++ ){
                        items.add(new Item(ItemColor.GREEN));
                  }

                  len = (Integer)map.get("blue");

                  for( int i = 0; i < len; i++ ){
                        items.add(new Item(ItemColor.BLUE));
                  }

                  deck.add(new AbandonedStation(
                        "Abandoned Station",
                        (Integer)map.get("level"),
                        (Integer)map.get("people"),
                        (Integer)map.get("days"),
                        items,      // REQUIRES AN ARRAY LIST OF ITEMS
                        board,
                        bank
                  ));
            });

            json.get("meteors" ).forEach( map -> {
                  deck.add(new MeteorShower(
                        "Meteor Shower",
                        (Integer)map.get("level"),
                        ((List<List<Integer>>)map.get("meteors")),
                        board
                  ));
            });

            json.get("pirates" ).forEach( map -> {
                  deck.add(new Pirates(
                        "Pirates",
                        (Integer)map.get("level"),
                        (Integer)map.get("firepower"),
                        (Integer)map.get("credits"),
                        (Integer)map.get("days"),
                        ((List<List<Integer>>)map.get("shoots")),
                        board
                  ));
            });

            json.get("planets" ).forEach( map -> {
                  deck.add(new VisitPlanets(
                        "Planets",
                        (Integer)map.get("level"),
                        (Integer)map.get("days"),
                        (List<Map<String,Integer>>)map.get("planets"),
                        bank,
                        board
                  ));
            });

            json.get("space" ).forEach( map -> {
                  deck.add(new OpenSpace(
                          "Open Space",
                          (Integer)map.get("level"),
                          board
                  ));
            });

            json.get("epidemic" ).forEach( map -> {
                  deck.add(new Epidemy(
                        "Epidemy",
                        (Integer)map.get("level"),
                        board
                  ));
            });

            json.get("smugglers" ).forEach( map -> {
                  deck.add(new Smugglers(
                        "Smugglers",
                        (Integer)map.get("level"),
                        (Integer)map.get("days"),
                        (Integer)map.get("cannons"),
                        (Integer)map.get("penalty"),
                        ((Map<String,Integer>)map.get("storage")).get("red"),
                        ((Map<String,Integer>)map.get("storage")).get("yellow"),
                        ((Map<String,Integer>)map.get("storage")).get("green"),
                        ((Map<String,Integer>)map.get("storage")).get("blue"),
                        board,
                        bank
                  ));
            });

            json.get("slavers" ).forEach( map -> {
                  deck.add(new Slavers(
                        "Slavers",
                        (Integer)map.get("level"),
                        (Integer)map.get("cannons"),
                        (Integer)map.get("days"),
                        (Integer)map.get("credits"),
                        (Integer)map.get("penalty"),
                        board
                  ));
            });

            json.get("stardust" ).forEach( map -> {
                  deck.add(new Stardust(
                          "Stardust",
                          (Integer)map.get("level"),
                          board
                  ));
            });

            json.get("warzone" ).forEach( map -> {

                  List<WarZoneActionConsequencePair> actions = ((List<Map<String,Integer>>)map.get("actions"))
                        .stream()
                        .map( m -> new WarZoneActionConsequencePair(
                              WarZoneAction.fromInteger(m.get("action")), 
                              WarZoneConsequence.fromInteger(m.get("consequence")))
                        ).toList();

                  List<PlasmaShot> shoots = ((List<List<Integer>>)map.get("shoots"))
                  .stream()
                  .map( pair -> new PlasmaShot( pair.get(0), pair.get(1)))
                  .toList();                  

                  deck.add(new WarZone(
                        "WarZone",
                        (Integer)map.get("level"),
                        board,
                        bank,
                        (Integer)map.get("days"),
                        (Integer)map.get("peoples"),
                        (Integer)map.get("items"),
                        shoots,
                        actions
                  )); 
            });

            deck.removeIf( card -> card.getCardLevel() > level );
      
            return deck;
      }
}