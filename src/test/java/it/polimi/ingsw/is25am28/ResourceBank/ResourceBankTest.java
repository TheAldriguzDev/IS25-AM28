package it.polimi.ingsw.is25am28.ResourceBank;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceBankTest {
    ResourceBank resourceBank;
    List<Player> players;


    @BeforeEach
    void init() {
        resourceBank = new ResourceBank();
        players = new ArrayList<>();

        // Initialize the bank with 8 resources for each type
        for (ItemColor color : ItemColor.values()) {
            for (int i = 0; i < 8; i++) {
                resourceBank.addResourceToBank( new Item(color));
            }
        }

        // Add the players to the game
        players.add( new Player("Player 1", PlayerColor.RED, 2) );
        players.add( new Player("Player 2", PlayerColor.YELLOW, 2) );
        players.add( new Player("Player 3", PlayerColor.BLUE, 2 ));
        players.add( new Player("Player 4", PlayerColor.GREEN, 2));
    }

    @Test
    void test_get_resource_by_color_from_bank() {
        assertEquals(8, resourceBank.getResourcesByColor(ItemColor.RED).size());
        assertEquals(8, resourceBank.getResourcesByColor(ItemColor.BLUE).size());
        assertEquals(8, resourceBank.getResourcesByColor(ItemColor.YELLOW).size());
        assertEquals(8, resourceBank.getResourcesByColor(ItemColor.GREEN).size());
    }

    // When we have an instance of the SHIP for each player we can test other methods for example give resource to player
}