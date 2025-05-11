package it.polimi.ingsw.is25am28.Loader;

import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Board.BoardTestFlight;
import it.polimi.ingsw.is25am28.Model.EventCards.*;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardLoaderTest {

    Board boardTestFlight;
    Board boardLevel2;
    ResourceBank resourceBank;

    @BeforeEach
    void init() {
        this.boardTestFlight = new BoardTestFlight();
        this.boardLevel2 = new BoardLevel2();
        this.resourceBank = new ResourceBank(0);
    }

    @Test
    void load_all_test_level_cards() throws IOException {
        CardLoader cardLoader = new CardLoader();

        List<EventCard> cards = cardLoader.getCards(boardTestFlight, resourceBank, 0);
        assertEquals(8, cards.size());
    }

    @Test
    void load_all_level_two_cards() throws IOException {
        CardLoader cardLoader = new CardLoader();

        List<EventCard> cards = cardLoader.getCards(boardTestFlight, resourceBank, 40);
        List<AbandonedShip> abandonedShips = new ArrayList<>();
        List<AbandonedStation> abandonedStations = new ArrayList<>();
        List<OpenSpace> openSpaces = new ArrayList<>();
        List<VisitPlanets> planets = new ArrayList<>();
        List<Epidemy> epidemies = new ArrayList<>();
        List<Stardust> stardusts = new ArrayList<>();
        List<Pirates> pirates = new ArrayList<>();
        List<Slavers> slavers = new ArrayList<>();
        List<Smugglers> smugglers = new ArrayList<>();
        List<MeteorShower> meteorShowers = new ArrayList<>();
        List<WarZone> warZones = new ArrayList<>();

        for (EventCard card : cards) {
            switch (card) {
                case AbandonedShip data -> {
                    abandonedShips.add(data);
                }
                case AbandonedStation data -> {
                    abandonedStations.add(data);
                }
                case OpenSpace data -> {
                    openSpaces.add(data);
                }
                case VisitPlanets data -> {
                    planets.add(data);
                }
                case Epidemy data -> {
                    epidemies.add(data);
                }
                case Stardust data -> {
                    stardusts.add(data);
                }
                case Pirates data -> {
                    pirates.add(data);
                }
                case Slavers data -> {
                    slavers.add(data);
                }
                case Smugglers data -> {
                    smugglers.add(data);
                }
                case MeteorShower data -> {
                    meteorShowers.add(data);
                }
                case WarZone data -> {
                    warZones.add(data);
                }
                default -> throw new AssertionError("Unknown card " + card);
            }
        }

        assertEquals(40, cards.size());
        assertEquals(7, openSpaces.size());
        assertEquals(1, epidemies.size());
        assertEquals(2, smugglers.size());
        assertEquals(8, planets.size());
        assertEquals(2, stardusts.size());
        assertEquals(2, pirates.size());
        assertEquals(2, slavers.size());
        assertEquals(6, meteorShowers.size());
        assertEquals(2, warZones.size());

    }
}