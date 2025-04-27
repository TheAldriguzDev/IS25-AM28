package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.Messages.Reconnect;
import it.polimi.ingsw.is25am28.Network.Messages.RefreshGames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LobbyScreen extends Screen {
    private String playerNickname;
    private PlayerColor playerColor;

    public LobbyScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
        this.playerNickname = null;
        this.playerColor = null;
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


    /**
     * This method is used to display the possibles options that the player can make when connect to the game.
     * 1. Join an active game (that is waiting for players)
     * 2. Create e new game
     * 3. Reconnect to an existing game
     * 4. Refresh the lobbies
     *
     * TODO: Need to be reworked with the TUI
     * */
    @Override
    public void showLobbies(AvailableGamesDTO state, boolean isFirstAccess) throws Exception {
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
            String line = this.inputThread.waitForInput();
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

    /**
     * Method used to ask for nickname and color to join the game
     */
    private void joinGameInput(GameInfoDTO game, List<String> usedNicknames) throws Exception {
        System.out.println("Joining the game with id " + game.getId() + " ...");

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Enter your name: ");
            playerName = this.inputThread.waitForInput();
            if (playerName.isEmpty() || usedNicknames.contains(playerName)) {
                System.out.println("Invalid input: name already used or empty.");
            }
        } while (playerName.isEmpty() || usedNicknames.contains(playerName));
        this.playerNickname = playerName;

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color " + game.getAvailableColors() + ": ");
            String colorInput = this.inputThread.waitForInput();
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

        this.model.setNickname(playerName);
        this.ctx = new CommandCTX(
                "joinGame",
                () -> {
                    this.ctx = null;
                },
                () -> {
                    try {
                        joinGameInput(game, usedNicknames);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        this.client.sendMessage(new NewPlayer(playerName, playerColor, game.getId()));
    }

    /**
     * Method used to ask for nickname, color, gameLevel and totalGamePlayer, and then it will create a new Game
     */
    private void createGameInput() throws Exception {
        System.out.println("Creating a new game ...");

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Your name: ");
            playerName = this.inputThread.waitForInput();
            if (playerName.isEmpty()) {
                System.out.println("Invalid input: name cannot be empty.");
            }
        } while (playerName.isEmpty());
        this.playerNickname = playerName;

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color (e.g., BLUE, GREEN, RED, YELLOW): ");
            String colorInput = this.inputThread.waitForInput();
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
            String line = this.inputThread.waitForInput();
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
            String line = this.inputThread.waitForInput();
            try {
                totalPlayers = Integer.parseInt(line);
                if (totalPlayers < 2 || totalPlayers > 4) {
                    System.out.println("Number of players must be between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: please enter a number.");
            }
        } while (totalPlayers < 2 || totalPlayers > 4);

        this.model.setNickname(playerName);
        this.ctx = new CommandCTX(
                "createGame",
                () -> {
                    this.ctx = null;
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
            playerName = this.inputThread.waitForInput();
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
        } else {
            System.out.println("Player nickname not found.");
        }
    }
}
