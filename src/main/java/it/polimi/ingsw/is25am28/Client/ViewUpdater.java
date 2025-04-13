package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.VirtualView;

import java.util.Scanner;

/**
 * Thanks to the VisitorPattern we can display different updates that arrives from the server.
 * TODO: In addition, we will be also able to modify the state and select input from the user
 * */

public class ViewUpdater implements StateVisitor {
    private final Scanner scanner;
    VirtualView client;

    // TODO: Remove the info from here, just for this first phase of testing
    private String playerName;
    private PlayerColor playerColor;
    private int gameLevel;
    private int totalGamePlayers;

    public ViewUpdater(VirtualView client) {
        this.scanner = new Scanner(System.in);
        this.client = client;

        this.playerName = "";
        this.playerColor = null;
        this.gameLevel = 0;
        this.totalGamePlayers = 0;
    }

    @Override
    public void visit(StateDTO state) {
        System.out.println(state.getStateName());
    }

    @Override
    public void visit(CreateGameStateDTO state) throws Exception {
        System.out.println("A new game has been just created, you are the leader!");
        System.out.println("The available colors are: " + state.getAvailableColors());

        System.out.println("Please, configure the game:");

        String playerName;
        do {
            System.out.print("Your name: ");
            playerName = scanner.nextLine().trim();
            if (playerName.isEmpty()) {
                System.err.println("Invalid input: name cannot be empty.");
            }
        } while (playerName.isEmpty());
        this.playerName = playerName;

        String color;
        do {
            System.out.print("Your color: ");
            color = scanner.nextLine().trim();
            if (!state.getAvailableColors().contains(color)) {
                System.err.println("Invalid input: please choose a color from the list " + state.getAvailableColors());
                color = "";
            }
        } while (color.isEmpty());
        this.playerColor = PlayerColor.fromString(color);

        int level = -1;
        do {
            System.out.print("Game level (0 to 3): ");
            try {
                level = Integer.parseInt(scanner.nextLine().trim());
                if (level < 0 || level > 3) {
                    System.err.println("Invalid input: level must be between 0 and 3.");
                    level = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input: please enter a valid number.");
            }
        } while (level == -1);
        this.gameLevel = level;

        int totalPlayers = -1;
        do {
            System.out.print("Total number of players (2 to 4): ");
            try {
                totalPlayers = Integer.parseInt(scanner.nextLine().trim());
                if (totalPlayers < 2 || totalPlayers > 4) {
                    System.err.println("Invalid input: number of players must be between 2 and 4.");
                    totalPlayers = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input: please enter a valid number.");
            }
        } while (totalPlayers == -1);
        this.totalGamePlayers = totalPlayers;

        client.configureGame(this.playerName, this.playerColor, this.gameLevel, this.totalGamePlayers);
    }

    @Override
    public void visit(WaitingForGameConfigurationDTO state) {
        System.out.println("Lobby found, waiting the leader for the game configuration");
    }

    @Override
    public void visit(WaitPlayersStateDTO state) throws Exception {
        if (!this.playerName.isEmpty()) {
            System.out.println("Waiting for more players to connect...");
            return;
        }


        System.out.println("Game has been configured! Please choose your nickname and color:");
        System.out.println("The available colors are: " + state.getAvailableColors());

        String playerName;
        do {
            System.out.print("Your name: ");
            playerName = scanner.nextLine().trim();
            if (playerName.isEmpty()) {
                System.err.println("Invalid input: name cannot be empty.");
            }
        } while (playerName.isEmpty());

        String color;
        do {
            System.out.print("Your color: ");
            color = scanner.nextLine().trim();
            if (!state.getAvailableColors().contains(color)) {
                System.err.println("Invalid input: please choose a color from the list " + state.getAvailableColors());
                color = "";
            }
        } while (color.isEmpty());

        this.client.newPlayer(playerName, PlayerColor.fromString(color));
    }

    @Override
    public void visit(ShipConstructionDTO state) {
        System.out.println("SHIP CONSTRUCTION");
    }

    @Override
    public void visit(FixShipDTO state) {

    }

    @Override
    public void visit(PopulateShipDTO state) {

    }

    @Override
    public void visit(CardRoundDTO state) {

    }

    @Override
    public void visit(EndGameDTO state) {

    }
}
