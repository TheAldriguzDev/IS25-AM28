package it.polimi.ingsw.is25am28.GameModel.FileLoader;

import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.EventCards.EventCard;

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

    public List<EventCard> read( Board board, ResourceBank bank ){
        final List<EventCard> deck = new ArrayList<>();
        Map<String,List<Map<String,Object>>> json = getJSONObject();

        json.get("abandonedShip" ).forEach( map -> {
            deck.add(new AbandonedShip(
                    "nave abbandonata",
                    (Integer)map.get("level"),
                    (Integer)map.get("people"),
                    (Integer)map.get("days"),
                    (Integer)map.get("credits"),
                    board
            ));
        });


            /*json.get("abandonedStation" ).forEach( map -> {
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
                        "stazione abbandonata",
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
                        "meteore",
                        (Integer)map.get("level"),
                        ((List<List<Integer>>)map.get("Meteors")),
                        board
                  ));
            });

            json.get("pirates" ).forEach( map -> {
                  deck.add(new Pirates(
                        "pirati",
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
                        "Pianeti",
                        (Integer)map.get("level"),
                        (Integer)map.get("days"),
                        (List<Object>)map.get("planets"),
                        board
                  ));
            });

            json.get("space" ).forEach( map -> {
                  deck.add(new OpenSpace(
                          "Spazio aperto",
                          (Integer)map.get("level"),
                          board
                  ));
            });

            json.get("epidemic" ).forEach( map -> {
                  deck.add(new Epidemy(
                        "Epidemia",
                        (Integer)map.get("level"),
                        board
                  ));
            });

            json.get("smugglers" ).forEach( map -> {
                  deck.add(new Smugglers(
                        "Contrabbandieri",
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
                        "Schiavisti",
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
                          "Polvere Stellare",
                          (Integer)map.get("level"),
                          board
                  ));
            });

            json.get("warzone" ).forEach( map -> {
                  deck.add(new WarZone(
                          "Zona di Guerra",
                          (Integer)map.get("level"),
                          (Map<String,Object>)map.get("engines"),
                          (Map<String,Object>)map.get("cannons"),
                          (Map<String,Object>)map.get("humans"),
                          board
                  ));
            });*/

        return deck;
    }
}