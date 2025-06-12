package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;

public class LobbyScreen extends Screen {
    private static final int AVAILABLE_GAMES_GROUPING_FACTOR = 8;

    private InputWidgetTUI availableGamesWidget;
    private InputWidgetTUI lobbyCommandsWidget;

    // Constructor
    public LobbyScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
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
    private void initLobbyCommandsWidget(AvailableGamesDTO state) {
        CommandWidgetTUI command;

        this.lobbyCommandsWidget = new InputWidgetTUI(this.inputThread);

        // (0) - Quit game
        command = new CommandWidgetTUI(
                "0",
                () -> {
                    System.out.println();

                    new WidgetTUI()
                            .appendString(COMPUTER_MSG_TAG + "Bye bye!")
                            .addPadding(1, 1, 1, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();

                    System.exit(0);
                }
        );
        command.appendString("Quit game");
        this.lobbyCommandsWidget.addCommand(command);

        // (1) - Create new game
        command = new CommandWidgetTUI(
                "1",
            () -> {
                try {
                    this.createGameInput();
                }
                catch (Exception e) {
                    System.out.println(
                        PrintUtils.addColor("[ERROR] An error occurred. Please try again.", ANSIColors.RED)
                    );
                }
            }
        );
        command.appendString("Create new game");
        this.lobbyCommandsWidget.addCommand(command);

        // (2) - Join an existing game
        command = new CommandWidgetTUI(
            "2",
            () -> {
                boolean commandExecuted;

                if (!state.getAvailableGames().isEmpty()) {
                    this.initAvailableGamesWidget(state);

                    do {
                        try {
                            System.out.println();
                            System.out.println("Available games to join:");
                            commandExecuted = this.availableGamesWidget.selectCommand(DEFAULT_COMMAND_PREFIX);
                        }
                        catch (InterruptedException e) {
                            // A forced interrupt arrived
                            return;
                        }

                        if (!commandExecuted) {
                            System.out.println(UNKNOWN_COMMAND_ERROR);
                        }
                    }
                    while (!commandExecuted);
                }
                else {
                    System.out.println(PrintUtils.addColor("[ERROR] There aren't any available games to join. Refresh or create one first.", ANSIColors.RED));
                    this.getLobbyCommand();
                }
            }
        );
        command.appendString("Join an existing game");
        this.lobbyCommandsWidget.addCommand(command);

        // (3) - Reconnect to an existing game
        command = new CommandWidgetTUI(
            "3",
            () -> {
                if (!state.getUsedNicknames().isEmpty()) {
                    try {
                        this.reconnectToGameInput(state, state.getUsedNicknames());
                    }
                    catch (Exception e) {
                        System.out.println(
                            PrintUtils.addColor("[ERROR] An error occurred. Please try again.", ANSIColors.RED)
                        );
                    }
                }
                else {
                    System.out.println(PrintUtils.addColor("[ERROR] There aren't any available games to reconnect to. Create one first.", ANSIColors.RED));
                    this.getLobbyCommand();
                }
            }
        );
        command.appendString("Reconnect to an existing game");
        this.lobbyCommandsWidget.addCommand(command);

        // (4) - Refresh available games
        command = new CommandWidgetTUI(
            "4",
            () -> {
                try {
                    this.refreshGames();
                }
                catch (Exception e) {
                    System.out.println(
                        PrintUtils.addColor("[ERROR] An error occurred. Please try again.", ANSIColors.RED)
                    );
                }
            }
        );
        command.appendString("Refresh available games");
        this.lobbyCommandsWidget.addCommand(command);

        this.lobbyCommandsWidget.setColumnGroupingAmount(
            this.lobbyCommandsWidget.getCommandMap().size()
        );
    }

    /**
     * Initializes the widget containing all available games
     */
    private void initAvailableGamesWidget(AvailableGamesDTO state) {
        List<GameInfoDTO> availableGames = state.getAvailableGames();
        CommandWidgetTUI command;

        this.availableGamesWidget = new InputWidgetTUI(this.inputThread);
        this.availableGamesWidget.setColumnGroupingAmount(AVAILABLE_GAMES_GROUPING_FACTOR);

        // (n) - All n available games
        if (availableGames != null && !availableGames.isEmpty()) {
            for (GameInfoDTO game : availableGames) {
                command = new CommandWidgetTUI(
            "" + game.getId(),
                    () -> {
                        try {
                            this.joinGameInput(game, state.getUsedNicknames());
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                );
                command.appendString("(level=" + game.getLevel() + ", players=" + game.getActualPlayers() + "/" + game.getTotalPlayers() + ")");
                this.availableGamesWidget.addCommand(command);
            }
        }

        // (-1) - Go back to the menu
        command = new CommandWidgetTUI(
            "-1",
            () -> {
                try {
                    this.showLobbies(state, false);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );
        command.appendString("Go back to the menu");
        this.availableGamesWidget.addCommand(command);
    }

    /**
     * Method used to ask for nickname and color to join the game
     */
    private void joinGameInput(GameInfoDTO game, List<String> usedNicknames) throws Exception {
        System.out.println();
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
                    System.out.println(
                        PrintUtils.addColor("[ERROR] An error occurred. Please try again.", ANSIColors.RED)
                    );
                }
            }
        );

        this.client.joinGame(playerName, playerColor, game.getId());
    }

    /**
     * Method used to ask for nickname, color, gameLevel and totalGamePlayer, and then it will create a new Game
     */
    private void createGameInput() throws Exception {
        System.out.println();
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

        this.client.createNewGame(playerName, playerColor, gameLevel, totalPlayers);
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

        System.out.println();
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

        this.model.setNickname(playerName);

        this.ctx = new CommandCTX(
            "reconnect",
            () -> {
                new WidgetTUI()
                        .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor("Successfully reconnected to the game!", ANSIColors.BRIGHT_GREEN))
                        .addPadding(1, 1, 1, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();
            },
            this::getLobbyCommand
        );

        this.client.reconnectClient(playerName);
    }

    /**
     * Refreshes the currently available games
     */
    private void refreshGames() throws Exception {
        System.out.println();
        this.client.refreshGames();

        new WidgetTUI()
                .appendString(COMPUTER_MSG_TAG + "Refreshing games...")
                .appendString(COMPUTER_MSG_TAG + "Available games are now up to date!")
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder()
                .printWidget();
    }

    /**
     * Asks the player for a correct lobby command
     */
    private void getLobbyCommand() {
        boolean commandExecuted;

        do {
            System.out.println();
            System.out.println("Available commands:");

            try {
                commandExecuted = this.lobbyCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);
            } catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }

            if (!commandExecuted) {
                System.out.println(UNKNOWN_COMMAND_ERROR);
            }
        }
        while (!commandExecuted);
    }

    @Override
    public void showLobbies(AvailableGamesDTO state, boolean isFirstAccess) throws Exception {
        if (isFirstAccess) {
            clearTerminal();
            printTitle();
        }
        this.initLobbyCommandsWidget(state);

        this.getLobbyCommand();
    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        WidgetTUI waitingForPlayersWidget = new WidgetTUI();

        clearTerminal();

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
