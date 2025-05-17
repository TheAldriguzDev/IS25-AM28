package it.polimi.ingsw.is25am28.Loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Loader.Cards.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.EventCards.*;
import it.polimi.ingsw.is25am28.Model.EventCards.AbandonedShip;
import it.polimi.ingsw.is25am28.Model.EventCards.AbandonedStation;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Model.EventCards.OpenSpace;
import it.polimi.ingsw.is25am28.Model.EventCards.Stardust;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CardLoader extends Loader<Cards> {

    public CardLoader() throws IOException {
        super("./json/cards.json", Cards.class);
    }

    public List<EventCard> getCards(Board board, ResourceBank resourceBank, int level) {
        // cards will store the generated events cards
        List<EventCard> cards = new ArrayList<>();
        // cardsData contains the read json file information
        Cards cardsData = this.getReadJSON();
        // Integer used to assign an ID to the cards
        AtomicInteger counter = new AtomicInteger(0);

        cardsData.getAbandonedShip().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            cards.add(
                    new AbandonedShip(
                            "Abandoned Ship",
                            c.getLevel(),
                            c.getPeople(),
                            c.getDays(),
                            c.getCredits(),
                            board,
                            counter.getAndIncrement(),
                            c.getPath()
                    )
            );
        });

        cardsData.getAbandonedStation().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            ArrayList<Item> items = new ArrayList<>();
            Map<ItemColor, Integer> colorQty = Map.of(
                    ItemColor.RED, c.getRed(),
                    ItemColor.YELLOW, c.getYellow(),
                    ItemColor.GREEN, c.getGreen(),
                    ItemColor.BLUE, c.getBlue()
            );

            colorQty.forEach((color, qty) -> {
                for (int i = 0; i < qty; i++) {
                    items.add(new Item(color));
                }
            });

            cards.add(
                    new AbandonedStation(
                            "Abandoned Station",
                            c.getLevel(),
                            c.getPeople(),
                            c.getDays(),
                            items,
                            board,
                            resourceBank,
                            counter.getAndIncrement(),
                            c.getPath()
                    )
            );
        });


        cardsData.getMeteors().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
           cards.add(
                   new MeteorShower(
                           "Meteor Shower",
                           c.getLevel(),
                           c.getMeteors(),
                           board,
                           counter.getAndIncrement(),
                           c.getPath()
                   )
           );
        });

        cardsData.getPirates().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            cards.add(
                    new Pirates(
                            "Pirates",
                            c.getLevel(),
                            c.getFirepower(),
                            c.getCredits(),
                            c.getDays(),
                            c.getShoots(),
                            board,
                            counter.getAndIncrement(),
                            c.getPath()
                    )
            );
        });

        cardsData.getPlanets().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            List<Map<String, Integer>> itemsPerPlanet = new ArrayList<>();

            c.getPlanets().forEach(p -> {
                itemsPerPlanet.add(
                        Map.of(
                                ItemColor.RED.toString(), p.getRed(),
                                ItemColor.YELLOW.toString(), p.getYellow(),
                                ItemColor.GREEN.toString(), p.getGreen(),
                                ItemColor.BLUE.toString(), p.getBlue()
                        )
                );
            });

           cards.add(
                   new VisitPlanets(
                           "Planets",
                           c.getLevel(),
                           c.getDays(),
                           itemsPerPlanet,
                           resourceBank,
                           board,
                           counter.getAndIncrement(),
                           c.getPath()
                   )
           );
        });

        cardsData.getOpenSpace().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
           cards.add(
                   new OpenSpace(
                           "Open Space",
                           c.getLevel(),
                           board,
                           counter.getAndIncrement(),
                           c.getPath()
                   )
           );
        });

        cardsData.getEpidemic().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
           cards.add(
                   new Epidemy(
                           "Epidemic",
                           c.getLevel(),
                           board,
                           counter.getAndIncrement(),
                           c.getPath()
                   )
           );
        });

        cardsData.getSmugglers().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            cards.add(
                    new Smugglers(
                            "Smugglers",
                            c.getLevel(),
                            c.getDays(),
                            c.getCannons(),
                            c.getPenalty(),
                            c.getStorage().getRed(),
                            c.getStorage().getYellow(),
                            c.getStorage().getGreen(),
                            c.getStorage().getBlue(),
                            board,
                            resourceBank,
                            counter.getAndIncrement(),
                            c.getPath()
                    )
            );
        });

        cardsData.getSlavers().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
           cards.add(
                   new Slavers(
                           "Slavers",
                           c.getLevel(),
                           c.getCannons(),
                           c.getDays(),
                           c.getCredits(),
                           c.getPenalty(),
                           board,
                           counter.getAndIncrement(),
                           c.getPath()
                   )
           );
        });

        cardsData.getStardust().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            cards.add(
                    new Stardust(
                            "Stardust",
                            c.getLevel(),
                            board,
                            counter.getAndIncrement(),
                            c.getPath()
                    )
            );
        });

        cardsData.getWarzone().stream().filter(c -> c.getLevel() <= level).forEach(c -> {
            List<WarZoneActionConsequencePair> actions = new ArrayList<>();

            c.getActions().forEach(a -> {
                actions.add(new WarZoneActionConsequencePair(
                        WarZoneAction.fromInteger(a.getAction()),
                        WarZoneConsequence.fromInteger(a.getConsequence())
                ));
            });

            List<PlasmaShot> plasmaShots = new ArrayList<>();
            c.getShoots().forEach(s -> {
                plasmaShots.add(new PlasmaShot(s.getFirst(), s.getLast()));
            });

            cards.add(
                    new WarZone(
                            "WarZone",
                            c.getLevel(),
                            board,
                            resourceBank,
                            c.getDays(),
                            c.getPeoples(),
                            c.getItems(),
                            plasmaShots,
                            actions,
                            counter.getAndIncrement(),
                            c.getPath()
                    )
            );
        });

        return cards;
    }
}
