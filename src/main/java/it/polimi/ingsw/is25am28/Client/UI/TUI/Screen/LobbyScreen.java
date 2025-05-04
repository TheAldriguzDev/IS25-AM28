package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.Messages.Reconnect;
import it.polimi.ingsw.is25am28.Network.Messages.RefreshGames;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

public class LobbyScreen extends Screen {
    private WidgetTUI availableGamesWidget;
    private WidgetTUI lobbyCommandsWidget;

    public LobbyScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
        this.initLobbyCommandsWidget();
    }

    /**
     * Prints an ASCII art of the game title
     */
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
     * Initializes the widget containing all available lobby commands
     */
    private void initLobbyCommandsWidget() {
        this.lobbyCommandsWidget = new WidgetTUI();

        this.lobbyCommandsWidget.appendString("(0) Quit game");
        this.lobbyCommandsWidget.appendString("(1) Create new game");
        this.lobbyCommandsWidget.appendString("(2) Join an existing game");
        this.lobbyCommandsWidget.appendString("(3) Reconnect to an existing game");
        this.lobbyCommandsWidget.appendString("(4) Refresh available games");

        this.lobbyCommandsWidget.addPadding(0, 1, 0, 1);
        this.lobbyCommandsWidget.wrapWidgetWithBorder();
    }

    /**
     * Initializes the widget containing all available games
     */
    private void initAvailableGamesWidget(List<GameInfoDTO> availableGames) {
        this.availableGamesWidget = new WidgetTUI();

        if (availableGames != null && !availableGames.isEmpty()) {
            for (GameInfoDTO game : availableGames) {
                this.availableGamesWidget.appendString("(" + game.getId() + ") (level=" + game.getLevel() + ", players=" + game.getActualPlayers() + "/" + game.getTotalPlayers() + ")");
            }
        }

        this.availableGamesWidget.appendString("(-1) Go back to the menu");
        this.availableGamesWidget.addPadding(0, 1, 0, 1);
        this.availableGamesWidget.wrapWidgetWithBorder();
    }

    /**
     * Method used to ask for nickname and color to join the game
     */
    private void joinGameInput(GameInfoDTO game, List<String> usedNicknames) throws Exception {
        System.out.println("Joining the game with id " + game.getId() + "...");

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Enter your name: ");
            playerName = this.inputThread.waitForInput();

            if (playerName.isEmpty() || usedNicknames.contains(playerName)) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Given name is already used or empty.", ANSIColors.RED));
            }
        }
        while (playerName.isEmpty() || usedNicknames.contains(playerName));

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color " + game.getAvailableColors() + ": ");
            String colorInput = this.inputThread.waitForInput();

            if (colorInput.isEmpty()) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Color cannot be empty.", ANSIColors.RED));
                continue;
            }
            try {
                playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Unknown color.", ANSIColors.RED));
            }
        } while (playerColor == null);

        // Setting the model parameters
        this.model.setNickname(playerName);
        this.model.setDifficultyLevel(game.getLevel());

        this.ctx = new CommandCTX(
            "joinGame",
            () -> {
                this.ctx = null;
            },
            () -> {
                try {
                    this.joinGameInput(game, usedNicknames);
                }
                catch (Exception e) {
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
            System.out.print("Insert your name: ");
            playerName = this.inputThread.waitForInput();

            if (playerName.isEmpty()) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Name cannot be empty.", ANSIColors.RED));
            }
        } while (playerName.isEmpty());

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color (e.g., BLUE, GREEN, RED, YELLOW): ");
            String colorInput = this.inputThread.waitForInput();

            if (colorInput.isEmpty()) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Given color cannot be empty.", ANSIColors.RED));
                continue;
            }
            try {
                playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Unknown color.", ANSIColors.RED));
            }
        } while (playerColor == null);

        // Ask for game level
        int gameLevel = -1;
        do {
            System.out.print("Select game level ([0] = Test Flight, [2] = Level 2 Flight): ");
            String line = this.inputThread.waitForInput();

            try {
                gameLevel = Integer.parseInt(line);

                if (gameLevel != 0 && gameLevel != 2) {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Game level must be 0 or 2.", ANSIColors.RED));
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] please enter a number.", ANSIColors.RED));
            }
        } while (gameLevel != 0 && gameLevel != 2);

        // Ask for total number of players
        int totalPlayers = -1;
        do {
            System.out.print("Enter total number of players (from 2 to 4): ");
            String line = this.inputThread.waitForInput();

            try {
                totalPlayers = Integer.parseInt(line);

                if (totalPlayers < 2 || totalPlayers > 4) {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Number of players must be between 2 and 4.", ANSIColors.RED));
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please enter a number.", ANSIColors.RED));
            }
        } while (totalPlayers < 2 || totalPlayers > 4);

        // Setting the model parameters
        this.model.setNickname(playerName);
        this.model.setDifficultyLevel(gameLevel);

        this.ctx = new CommandCTX(
            "createGame",
            () -> {
                this.ctx = null;
            },
            () -> {
                try {
                    this.createGameInput();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        this.client.sendMessage(new ConfigGame(playerName, playerColor, gameLevel, totalPlayers));
    }

    /**
     * Asks the user the nickname with which he last player and
     * attempts to reconnect him to the game he previously player in.
     */
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
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Name cannot be empty or different from an existing one.", ANSIColors.RED));
            }
        }
        while (playerName.isEmpty() && !usedNicknames.contains(playerName));

        this.client.sendMessage(new Reconnect(playerName));
    }

    /**
     * Refreshes the currently available games
     */
    private void refreshGames() throws Exception {
        System.out.println(COMPUTER_MSG_TAG + "Refreshing games...");
        this.client.sendMessage(new RefreshGames());
        System.out.println(COMPUTER_MSG_TAG + "Available games are now up to date!");
    }

    /**
     * This method is used to display the possibles options that the player can make when connect to the game.
     *  0) Quit game
     *  1) Create new game
     *  2) Join an existing game
     *  3) Reconnect to an existing game
     *  4) Refresh the lobbies
     * */
    @Override
    public void showLobbies(AvailableGamesDTO state, boolean isFirstAccess) throws Exception {
        boolean commandExecuted;
        String line;
        int choice;

        if (isFirstAccess) { printTitle(); }

        this.initAvailableGamesWidget(state.getAvailableGames());

        do {
            commandExecuted = false;

            System.out.println("Available commands:");
            this.lobbyCommandsWidget.printWidget();

            System.out.print(DEFAULT_COMMAND_PREFIX);
            line = this.inputThread.waitForInput();

            try {
                choice = Integer.parseInt(line);
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please enter a number.", ANSIColors.RED));
                continue;
            }

            switch (choice) {
                case 0 -> {
                    // (0) - Quit game
                    // Return nothing and the program stops
                    System.out.println();

                    new WidgetTUI()
                            .appendString(COMPUTER_MSG_TAG + "Bye bye!")
                            .addPadding(1, 1, 1, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();

                    System.exit(0);
                }
                case 1 -> {
                    // (1) - Create new game
                    this.createGameInput();
                    commandExecuted = true;
                }
                case 2 -> {
                    // (2) - Join an existing game
                    if (!state.getAvailableGames().isEmpty()) {
                        List<Integer> availableGameIDs = state.getAvailableGames().stream().map(GameInfoDTO::getId).toList();

                        GameInfoDTO game = null;

                        do {
                            System.out.println("Currently available games:");
                            this.availableGamesWidget.printWidget();

                            System.out.print(DEFAULT_COMMAND_PREFIX);
                            line = this.inputThread.waitForInput();

                            try {
                                choice = Integer.parseInt(line);
                            }
                            catch (NumberFormatException e) {
                                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please enter a number.", ANSIColors.RED));
                            }

                            if (choice == -1) {
                                // Go back to the lobby commands
                                break;
                            }
                            int finalChoice = choice;

                            game = state.getAvailableGames().stream()
                                    .filter(g -> g.getId() == finalChoice)
                                    .findFirst()
                                    .orElse(null);

                            if (game == null) {
                                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Game with ID=" + choice + " does not exist. Please choose an existing game.", ANSIColors.RED));
                            }
                        }
                        while (!availableGameIDs.contains(choice) || game == null);

                        if (choice != -1) {
                            this.joinGameInput(game, state.getUsedNicknames());
                            commandExecuted = true;
                        }
                    }
                    else {
                        System.out.println(PrintUtils.addColor("[ERROR] There aren't any available games to join. Refresh or create one first.", ANSIColors.RED));
                    }
                }
                case 3 -> {
                    // (3) - Reconnect to an existing game
                    if (!state.getUsedNicknames().isEmpty()) {
                        this.reconnectToGameInput(state, state.getUsedNicknames());
                        commandExecuted = true;
                    }
                    else {
                        System.out.println(PrintUtils.addColor("[ERROR] There aren't any available games to join. Refresh or create one first.", ANSIColors.RED));
                    }
                }
                case 4 -> {
                    // (4) - Refresh available games
                    this.refreshGames();
                    commandExecuted = true;
                }
                default -> {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
        }
        while (!commandExecuted);
    }

    /**
     * Puts the current player's client into the waiting state
     * and shows how many players are currently connected and
     * also how many are left before the game starts
     */
    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        WidgetTUI waitingForPlayersWidget = new WidgetTUI();

        // Creating the ship of the newly connected player in all clients
        for (Map.Entry<String, PlayerColor> playerEntry : waitingForPlayers.getUsedNicknames().entrySet()) {
            this.model.addNewPlayer(playerEntry.getKey(), playerEntry.getValue());
        }

        if (this.model.getNickname() != null) {
            int connected = waitingForPlayers.getLobbyTotalSpot() - waitingForPlayers.getAvailableSpots();
            int total = waitingForPlayers.getLobbyTotalSpot();
            Map<String, PlayerColor> nicknamesAndColor = waitingForPlayers.getUsedNicknames();

            // Resetting the widget
            waitingForPlayersWidget.resetScreenAndDimensions();

            // Adding the currently connected players and total players counters to the widget
            waitingForPlayersWidget.appendString(PrintUtils.addColor("[STATUS]", ANSIColors.BRIGHT_CYAN));
            waitingForPlayersWidget.appendString("Waiting for more players to join the game [" + connected + "/" + total + "]...");

            // Then adding all the currently present players
            waitingForPlayersWidget.appendString("Connected players:");
            int i = 1;

            if (!nicknamesAndColor.isEmpty()) {
                for (Map.Entry<String, PlayerColor> entry : nicknamesAndColor.entrySet()) {
                    waitingForPlayersWidget.appendString(
                            i + " - " + PrintUtils.addColor(entry.getKey(), entry.getValue().getColorString())
                    );
                    i++;
                }
            }

            // Adding some left and right padding
            waitingForPlayersWidget.addPadding(0, 1, 0, 1);

            // Finally, wrap and print the widget
            waitingForPlayersWidget.wrapWidgetWithBorder().printWidget();

            // Show a start game message if the goal player amount is reached
            if (connected == total) {
                WidgetTUI startGameMessage = new WidgetTUI();

                startGameMessage.appendString(COMPUTER_MSG_TAG + "All players are connected! Starting game...");
                startGameMessage.addPadding(0, 1, 0, 1);
                startGameMessage.wrapWidgetWithBorder().printWidget();
            }
        }
    }
}
