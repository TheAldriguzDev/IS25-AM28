package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.util.ArrayList;
import java.util.List;
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
    public void visit(AvailableGamesDTO state) throws Exception {
//        System.out.println("""
//                 ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗\s
//                ██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗
//                ██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝
//                ██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗
//                ╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║
//                 ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝\s
//                """);
        System.out.println("Welcome to GALAXY TRUCKER");

        List<GameInfoDTO> availableGames = state.getAvailableGames();

        // Build the list of possibles options
        List<String> options = new ArrayList<>();

        // If present, add the available games
        for (GameInfoDTO game : availableGames) {
            options.add("Join the game with the ID: " + game.getId() +
                    " (Required players: " + game.getTotalPlayers() +
                    ", Level: " + game.getLevel() + ")");
        }

        // Extra options
        options.add("Create a new game");
        options.add("Reconnect to an existing game");

        // Display the options
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }

        int choice = -1;
        while (choice < 1 || choice > options.size()) {
            System.out.print("Choose an option: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next(); // Consuma input non valido
            }
        }

        Scanner scanner = new Scanner(System.in);

        // Evaluate the correct command
        if (choice <= availableGames.size()) {
            GameInfoDTO selectedGame = availableGames.get(choice - 1);
            System.out.println("Joining the game " + selectedGame.getId() + " ...");

            // Ask for nickname
            String playerName;
            do {
                System.out.print("Your name: ");
                playerName = scanner.nextLine().trim();
                if (playerName.isEmpty() || state.getUsedNicknames().contains(playerName)) {
                    System.err.println("Invalid input: name already used or empty.");
                }
            } while (playerName.isEmpty());
            this.playerName = playerName;

            // Ask for color
            PlayerColor playerColor = null;
            do {
                System.out.print("Choose a color " + selectedGame.getAvailableColors());
                String colorInput = scanner.nextLine().trim();
                if (colorInput.isEmpty()) {
                    System.err.println("Invalid input: color cannot be empty.");
                    continue;
                }
                try {
                    playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid input: unknown color.");
                }
            } while (playerColor == null);
            this.playerColor = playerColor;

            client.sendMessage( new NewPlayer(this.playerName, this.playerColor, selectedGame.getId()));

        } else if (choice == availableGames.size() + 1) {
            System.out.println("Creating a new game...");

            // Ask for nickname
            String playerName;
            do {
                System.out.print("Your name: ");
                playerName = scanner.nextLine().trim();
                if (playerName.isEmpty()) {
                    System.err.println("Invalid input: name cannot be empty.");
                }
            } while (playerName.isEmpty());
            this.playerName = playerName;

            // Ask for color
            PlayerColor playerColor = null;
            do {
                System.out.print("Choose a color (e.g., BLUE, GREEN, RED, YELLOW): ");
                String colorInput = scanner.nextLine().trim();
                if (colorInput.isEmpty()) {
                    System.err.println("Invalid input: color cannot be empty.");
                    continue;
                }
                try {
                    playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid input: unknown color.");
                }
            } while (playerColor == null);
            this.playerColor = playerColor;

            // Ask for game level
            int gameLevel = -1;
            do {
                System.out.print("Select game level (0 --> Test Flight, 2 = Level 2 Flight): ");
                if (scanner.hasNextInt()) {
                    gameLevel = scanner.nextInt();
                } else {
                    System.err.println("Invalid input: please enter a number.");
                    scanner.next(); // Consume invalid input
                    continue;
                }
                if (gameLevel != 0 && gameLevel != 2) {
                    System.err.println("Game level must be 0 or 2.");
                }
            } while (gameLevel != 0 && gameLevel != 2);
            scanner.nextLine(); // Consume leftover newline

            // Ask for total number of players
            int totalPlayers = -1;
            do {
                System.out.print("Enter total number of players (2 to 4): ");
                if (scanner.hasNextInt()) {
                    totalPlayers = scanner.nextInt();
                } else {
                    System.err.println("Invalid input: please enter a number.");
                    scanner.next(); // Consume invalid input
                    continue;
                }
                if (totalPlayers < 2 || totalPlayers > 4) {
                    System.err.println("Number of players must be between 2 and 4.");
                }
            } while (totalPlayers < 2 || totalPlayers > 4);
            scanner.nextLine(); // Consume leftover newline

            System.out.println("Sending");
            this.client.sendMessage( new ConfigGame(this.playerName, this.playerColor, gameLevel, totalPlayers) );
            System.out.println("Sent");

        } else {
            System.out.println("Reconnecting to an existing game...");

            // Ask for nickname
            String playerName;
            do {
                System.out.print("Your name: ");
                playerName = scanner.nextLine().trim();
                if (playerName.isEmpty()) {
                    System.err.println("Invalid input: name cannot be empty.");
                }
            } while (playerName.isEmpty());

            // TODO: reconnectToGame(playerName);
        }

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

        client.sendMessage(new ConfigGame(this.playerName, this.playerColor, this.gameLevel, this.totalGamePlayers));

        // client.configureGame(this.playerName, this.playerColor, this.gameLevel, this.totalGamePlayers);
    }

    @Override
    public void visit(WaitingForGameConfigurationDTO state) {
        System.out.println("Lobby found, waiting the leader for the game configuration");
    }

    @Override
    public void visit(WaitPlayersStateDTO state) throws Exception {
        if (!this.playerName.isEmpty()) {
            if (state.getUsedNicknames().contains(this.playerName)) {
                System.out.println("Players in the game: " + state.getUsedNicknames());
                System.out.println("Waiting for more players to connect...");
                return;
            }
        }

        System.out.println("Game is ready to start! Please choose your nickname and color:");
        System.out.println("The available colors are: " + state.getAvailableColors());

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

        // client.sendMessage(new NewPlayer(this.playerName, this.playerColor));
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
