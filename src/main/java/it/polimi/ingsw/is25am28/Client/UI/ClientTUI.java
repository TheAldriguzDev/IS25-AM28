package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ClientTUI implements ClientUI {
    private final ClientModel model;
    private VirtualView client;
    private final Object ioLock;
    private final Random random;

    private final BufferedReader scanner;

    private CommandCTX currCommand;

    private String playerNickname;

    public ClientTUI(ClientModel model) {
        this.model = model;
        this.ioLock = new Object();
        this.scanner = new BufferedReader(new InputStreamReader(System.in));
        this.currCommand = null;
        this.random = new Random();
    }

    /**
     * @return the possible options available in the game lobby
     * 1. Available games
     * 2. Create a new game
     * 3. Reconnect to an existing game
     * 4. Refresh available games
     * */
    private static List<String> getLobbiesOptions(List<GameInfoDTO> availableGames) {
        List<String> options = new ArrayList<>();

        // If present, add the available games
        for (GameInfoDTO game : availableGames) {
            options.add(
                    "Join the game with the ID: " + game.getId() +
                            " - Level: " + game.getLevel() +
                            " - Players: " + game.getActualPlayers() + "/" + game.getTotalPlayers()
            );
        }

        // Extra options
        options.add("Create a new game");

        // If there is at least one game show the reconnect option
        options.add("Reconnect to an existing game");
        options.add("Refresh available games");
        return options;
    }

    /**
     * @return the options available when the player can select a tile in the shipConstructionState
     * */
    private static List<String> getShipConstructionBaseOptions(List<ClientComponent> reservedComponents) {
        List<String> options = new ArrayList<>();

        // If present, add the available games
        for (ClientComponent comp : reservedComponents) {
            options.add(
                    "Select reserved tile - " + comp.getClass().getSimpleName()
            );
        }

        // Extra options
        options.add("Select a new tile");
        options.add("Show deck");
        return options;
    }

    private static void printTitle() {
        System.out.println("""
         ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗\s
        ██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗
        ██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝
        ██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗
        ╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║
         ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝\s
        """);
    }

    private static void clearTerminal() {
        System.out.print("\033[H\033[2J");
    }

    @Override
    public void setVirtualClient(VirtualView client) {
        this.client = client;
    }

    /**
     * This method is used to display the possibles options that the player can make when connect to the game.
     * 1. Join an active game (that is waiting for players)
     * 2. Create e new game
     * 3. Reconnect to an existing game
     * 4. Refresh the lobbies
     * */
    @Override
    public void showLobbies(AvailableGamesDTO state, boolean isFirstAccess) throws Exception {
        synchronized (ioLock) {
            if (isFirstAccess) {
                printTitle();
            }

            List<GameInfoDTO> availableGames = state.getAvailableGames();

            // Build the list of available games
            List<String> options = getLobbiesOptions(availableGames);

            // Display the options
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }

            int choice = -1;
            do {
                System.out.print("Choose an option: ");
                String line = this.scanner.readLine().trim();
                try {
                    choice = Integer.parseInt(line);
                    if (choice < 1 || choice > options.size()) {
                        System.out.println("Invalid choice: please select a number between 1 and " + options.size() + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: please enter a number.");
                }
            } while (choice < 1 || choice > options.size());

            // Evaluate the correct command
            if (choice <= availableGames.size()) {
                GameInfoDTO selectedGame = availableGames.get(choice - 1);
                this.joinGameInput(selectedGame, state.getUsedNicknames());
            } else if (choice == availableGames.size() + 1) {
                this.createGameInput();
            } else if (choice == availableGames.size() + 2) {
                this.reconnectToGameInput(state, state.getUsedNicknames());
            } else {
                this.refreshGames();
            }
        }
    }

    /**
     * Method used to ask for nickname and color to join the game
     */
    private void joinGameInput(GameInfoDTO game, List<String> usedNicknames) throws Exception {
        System.out.println("Joining the game with id " + game.getId() + " ...");

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Enter your name: ");
            playerName = this.scanner.readLine().trim();
            if (playerName.isEmpty() || usedNicknames.contains(playerName)) {
                System.out.println("Invalid input: name already used or empty.");
            }
        } while (playerName.isEmpty() || usedNicknames.contains(playerName));
        this.playerNickname = playerName;

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color " + game.getAvailableColors() + ": ");
            String colorInput = this.scanner.readLine().trim();
            if (colorInput.isEmpty()) {
                System.out.println("Invalid input: color cannot be empty.");
                continue;
            }
            try {
                playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: unknown color.");
            }
        } while (playerColor == null);

        String finalPlayerName = playerName;
        PlayerColor finalPlayerColor = playerColor;
        this.currCommand = new CommandCTX(
                "joinGame",
                () -> {
                    synchronized (this.model) {
                        this.model.setNickname(finalPlayerName);
                        this.model.setPlayerColor(finalPlayerColor);
                        this.currCommand = null;
                    }
                },
                () -> {
                    try {
                        joinGameInput(game, usedNicknames);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        client.sendMessage(new NewPlayer(playerName, playerColor, game.getId()));
    }

    /**
     * Method used to ask for nickname, color, gameLevel and totalGamePlayer, and then it will create a new Game
     */
    private void createGameInput() throws Exception {
        System.out.println("Creating a new game ...");

        // use BufferedReader to avoid Scanner newline issues
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Your name: ");
            playerName = reader.readLine().trim();
            if (playerName.isEmpty()) {
                System.out.println("Invalid input: name cannot be empty.");
            }
        } while (playerName.isEmpty());
        this.playerNickname = playerName;

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color (e.g., BLUE, GREEN, RED, YELLOW): ");
            String colorInput = reader.readLine().trim();
            if (colorInput.isEmpty()) {
                System.out.println("Invalid input: color cannot be empty.");
                continue;
            }
            try {
                playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: unknown color.");
            }
        } while (playerColor == null);

        // Ask for game level
        int gameLevel = -1;
        do {
            System.out.print("Select game level (0 --> Test Flight, 2 = Level 2 Flight): ");
            String line = reader.readLine().trim();
            try {
                gameLevel = Integer.parseInt(line);
                if (gameLevel != 0 && gameLevel != 2) {
                    System.out.println("Game level must be 0 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: please enter a number.");
            }
        } while (gameLevel != 0 && gameLevel != 2);

        // Ask for total number of players
        int totalPlayers = -1;
        do {
            System.out.print("Enter total number of players (2 to 4): ");
            String line = reader.readLine().trim();
            try {
                totalPlayers = Integer.parseInt(line);
                if (totalPlayers < 2 || totalPlayers > 4) {
                    System.out.println("Number of players must be between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: please enter a number.");
            }
        } while (totalPlayers < 2 || totalPlayers > 4);

        String finalPlayerName = playerName;
        PlayerColor finalPlayerColor = playerColor;
        this.currCommand = new CommandCTX(
                "createGame",
                () -> {
                    synchronized (this.model) {
                        this.model.setNickname(finalPlayerName);
                        this.model.setPlayerColor(finalPlayerColor);
                        this.currCommand = null;
                    }
                },
                () -> {
                    try {
                        createGameInput();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        this.client.sendMessage(new ConfigGame(playerName, playerColor, gameLevel, totalPlayers));
    }

    private void reconnectToGameInput(AvailableGamesDTO availableGames, List<String> usedNicknames) throws Exception {
        if (usedNicknames.isEmpty()) {
            this.showLobbies(availableGames, false);
            return;
        }

        System.out.println("Trying to reconnect you to the game...");

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Enter your name: ");
            playerName = this.scanner.readLine().trim();
            if (playerName.isEmpty() && !usedNicknames.contains(playerName)) {
                System.out.println("Invalid input: name cannot be empty or different from an existing one.");
            }
        } while (playerName.isEmpty() && !usedNicknames.contains(playerName));
        this.playerNickname = playerName;

        this.client.sendMessage(new Reconnect(playerName));
    }

    private void refreshGames() throws Exception {
        this.client.sendMessage(new RefreshGames());
    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        synchronized (ioLock) {
            if (this.model.getNickname() != null) {
                int connected = waitingForPlayers.getLobbyTotalSpot() - waitingForPlayers.getAvailableSpots();
                int total = waitingForPlayers.getLobbyTotalSpot();
                Map<String, PlayerColor> nicknamesAndColor = waitingForPlayers.getUsedNicknames();

                System.out.printf("Waiting for more players to join the game [%d/%d]...%n", connected, total);

                if (!nicknamesAndColor.isEmpty()) {
                    System.out.print("Connected players: ");
                    String formattedNames = nicknamesAndColor.entrySet().stream()
                            .map(entry -> entry.getValue().formatColor(entry.getKey()))
                            .collect(Collectors.joining(", "));
                    System.out.println(formattedNames);
                }
            }
        }
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        synchronized (this.ioLock) {
            this.displayShipConstructionComponents();
        }
    }

    private void displayShipConstructionComponents() throws Exception {
        List<ClientComponent> reservedComponents = List.of();
        try {
            reservedComponents = this.model.getState().getReservedComponents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Build the list of available games
        List<String> options = getShipConstructionBaseOptions(reservedComponents);

        synchronized (this.ioLock) {
            // Display the options
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }

            int choice = -1;
            do {
                System.out.print("Choose an option: ");
                String line = this.scanner.readLine().trim();
                try {
                    choice = Integer.parseInt(line);
                    if (choice < 1 || choice > options.size()) {
                        System.out.println("Invalid choice: please select a number between 1 and " + options.size() + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: please enter a number.");
                }
            } while (choice < 1 || choice > options.size());

            // Evaluate the correct command
            if (choice <= reservedComponents.size()) {
                ClientComponent reservedComp = reservedComponents.get(choice - 1);
                this.selectReservedComponent(reservedComp);

            } else if (choice == reservedComponents.size() + 1) {
                this.selectNewTile();
                // TODO: Invoke the method to handle the selected comp --> flag that does not comes from reserved comp --> can be deselected and can be reserved
            } else if (choice == reservedComponents.size() + 2) {
                // TODO: Invoke the show deck
            }
        }
    }

    /**
     * Once a reserved component is selected, we can only:
     * 1. Place it in the ship
     * 2. Drop it again in the reserved components
     * */
    private void selectReservedComponent(ClientComponent reservedComponent) throws Exception {
        System.out.println("1. Place at (x, y) (e.g., 1 6 7)");
        System.out.println("2. Drop selected component to the reserved component list");

        int choice = -1;
        int x = -1, y = -1;

        do {
            System.out.print("Choose an option: ");
            String line = this.scanner.readLine().trim();
            String[] parts = line.split("\\s+");

            if (parts.length == 1) {
                try {
                    choice = Integer.parseInt(parts[0]);
                    if (choice != 2) {
                        System.out.println("Invalid choice: please select a valid option (1 or 2).");
                        choice = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: please enter a number.");
                }
            } else if (parts.length == 3) {
                try {
                    choice = Integer.parseInt(parts[0]);
                    if (choice == 1) {
                        x = Integer.parseInt(parts[1]);
                        y = Integer.parseInt(parts[2]);
                    } else {
                        System.out.println("Invalid choice: please select a valid option (1 or 2).");
                        choice = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: please enter numbers.");
                    choice = -1;
                }
            } else {
                System.out.println("Invalid input: use '1 x y' or '2'.");
            }
        } while (choice == -1);

        // If the user decides to drop the reserved component to the reservedComponent list we simply skip this clause
        // since the component is already reserved. Otherwise, we execute the command to place the tile in the player ship.
        // This command is executed locally since we send the client ship once its finished
        if (choice == 1) {

            // TODO: Send the message to the server
            // this.model.getState().placeTile(reservedComponent, x, y);
        }

        // Return to the shipConstructionComponents menu
        this.displayShipConstructionComponents();
    }

    private void selectNewTile() throws Exception {
        String input;
        int idx = -1;

        do {
            System.out.print("Enter tile index (0-151) or 'r' for random: ");
            input = this.scanner.readLine().trim();
            idx = -1;

            if (input.equalsIgnoreCase("r")) {
                idx = random.nextInt(152);
            } else {
                try {
                    int tmpIndex = Integer.parseInt(input);
                    if (tmpIndex < 0 || tmpIndex > 151) {
                        System.out.println("Invalid index: must be between 0 and 151.");
                    } else {
                        idx = tmpIndex;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: enter a number 0–151 or 'r'.");
                }
            }

            if (idx >= 0 && !this.model.getState().getConstructionShipComponents().get(idx).isVisible()) {
                System.out.println("Invalid index: the given component is already selected by someone else.");
                idx = -1; // reset to retry
            }
        } while (idx < 0);

        // We have the index of the tile that we want to use --> we need to send the command to the server and if the tile
        // is selectable we can execute the correct command to continue the player input

        int finalIdx = idx;
        this.currCommand = new CommandCTX(
                "selectTile",
                () -> {
                    // If the selection was successful we jump to the method that handles the operations
                    try {
                        this.handleSelectedTile(this.model.getState().getConstructionShipComponents().get(finalIdx));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    // If an error occurred we re-execute the command
                    try {
                        this.selectNewTile();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        int construction_i = idx / 19;
        int construction_j = idx % 19;

        this.client.sendMessage(new SelectTile(this.playerNickname, construction_i, construction_j));
    }

    /**
     * Method used to handle the selected component by the client. We can:
     * 1. Rotate the component
     * 2. Place the component in the ship
     * 3. Reserve the component
     * 4. Deselect the component
     * */
    private void handleSelectedTile(ClientComponent selectedComponent) throws Exception {
        String input;
        int selectedCommand = -1, x = -1, y = -1;;

        List<String> options = new ArrayList<>();
        options.add("Rotate (right)");
        options.add("Rotate (left)");
        options.add("Place at (x, y) (e.g. 4 x y)");
        options.add("Deselect");
        if (this.model.getState().getReservedComponents().size() < 2) {
            options.add("Reserve");
        }

        for (int i = 0; i < options.size(); i++) {
            System.out.println(i + 1 + ". " + options.get(i));
        }

        do {
            System.out.print("Select an action: ");
            input = this.scanner.readLine().trim();

            String[] tokens = input.split("\\s+");

            try {
                 selectedCommand = Integer.parseInt(tokens[0]);
                 if (selectedCommand < 1 || selectedCommand > options.size()) {
                     System.out.println("Invalid command: the given command is not valid.");
                 }
            } catch (NumberFormatException _) {
                System.out.println("Invalid command: the given command is not a number.");
            }

            if (selectedCommand == 5) {
                if (tokens.length != 3) {
                    System.out.println("Invalid input. Usage: 4 x y");
                    continue;
                }
                try {
                    x = Integer.parseInt(tokens[1]);
                    y = Integer.parseInt(tokens[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. x and y must be numbers");
                }
            }
        } while (selectedCommand < 1 || selectedCommand > options.size());

        switch (selectedCommand) {
            // Rotate right
            case 1 -> {
                selectedComponent.rotateRight();
                this.handleSelectedTile(selectedComponent);
            }
            // Rotate left
            case 2 -> {
                selectedComponent.rotateLeft();
                this.handleSelectedTile(selectedComponent);
            }
            // Place tile
            case 3 -> {
                // TODO: Send the message to the server
                // this.model.getState().placeTile(selectedComponent, x, y);
            }
            // Deselect the tile
            case 4 -> {
                this.currCommand = new CommandCTX(
                        "deselectTile",
                        () -> {
                            // Once we have deselected the tile we can return to the shipConstruction menu
                            try {
                                this.displayShipConstructionComponents();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        () -> {
                            // If an error occurred we re-execute the command
                            try {
                                this.handleSelectedTile(selectedComponent);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

                int id = selectedComponent.getID();
                int construction_i = id / 19;
                int construction_j = id % 19;

                this.client.sendMessage(new DeselectTile(this.playerNickname, construction_i, construction_j));
            }
            // Reserve the tile
            case 5 -> {
                this.model.getState().reserveTile(selectedComponent);
            }
            default -> {
                System.out.println("Invalid input: please select a valid command.");
            }
        }
    }


    @Override
    public void commitCommand(String playerNickname) {
        if (playerNickname.equals(this.playerNickname) && this.currCommand != null) {
            this.currCommand.handleSuccess();
        }
    }

    @Override
    public void showError(ErrorAnswer error) {
        synchronized (this.ioLock) {
            clearTerminal();
            if (this.currCommand != null) {
                this.currCommand.handleError(error.getError());
            } else {
                System.out.println(error.getError());
            }
        }
    }
}
