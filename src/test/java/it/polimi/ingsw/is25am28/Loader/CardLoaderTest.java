package it.polimi.ingsw.is25am28.Loader;

import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Board.BoardTestFlight;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        assertEquals(40, cards.size());
    }
}