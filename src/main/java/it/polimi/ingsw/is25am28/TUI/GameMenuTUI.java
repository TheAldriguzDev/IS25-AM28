package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.Messages.Reconnect;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMenuTUI extends TUI {
    // Command grouping values for the input widgets below
    public static final int LOBBY_COMMANDS_PER_COLUMN = 2;
    public static final int AVAILABLE_GAMES_PER_COLUMN = 16;

    // All input widgets
    private final InputWidgetTUI menuCommandsWidget;
    private final InputWidgetTUI gameListInputWidget;

    // DTO containing the available games from which the player can choose from
    private AvailableGamesDTO availableGamesDTO;

    private int selectedGameId = -1;

    // Constructor
    public GameMenuTUI(ClientModel model) {
        super(model);

        // Initializing menuCommandsWidget
        this.menuCommandsWidget = new InputWidgetTUI();
        this.menuCommandsWidget.setNewScanner(System.in);
        this.menuCommandsWidget.setColumnGroupingAmount(LOBBY_COMMANDS_PER_COLUMN);
        this.initMenuCommands();

        // Initializing gameListWidget
        this.gameListInputWidget = new InputWidgetTUI();
        this.gameListInputWidget.setNewScanner(System.in);
        this.gameListInputWidget.setColumnGroupingAmount(AVAILABLE_GAMES_PER_COLUMN);
    }

    /**
     * Sets the input widget containing all the commands available in the game menu, which are:
     *  - (l) List available games
     *  - (c) Create a new game
     *  - (r) Reconnect to an existing game
     *  - (q) Quit game
     */
    private void initMenuCommands() throws RuntimeException {
        // Initializes (only once!) the widget containing the available menu commands
        if (this.menuCommandsWidget.getCommandMap() == null || this.menuCommandsWidget.getCommandMap().isEmpty()) {
            CommandWidgetTUI menuCommand;

            // (1) - List available games to join
            menuCommand = new CommandWidgetTUI(
                "l",
                () -> {
                    // Showing the user all the selectable games and setting
                    // the selectedGameId attribute to that choice
                    this.getGameListCommand();
                    System.out.println();

                    // Retrieving the selected game
                    GameInfoDTO selectedGame = this.availableGamesDTO.getAvailableGames().get(this.selectedGameId);

                    // Joining the selected game
                    try {
                        this.joinGameInput(selectedGame, this.availableGamesDTO.getUsedNicknames());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            menuCommand.appendString("List available games to join");
            this.menuCommandsWidget.addCommand(menuCommand);

            // (2) - Create new game
            menuCommand = new CommandWidgetTUI(
                "c",
                () -> {
                    // Create new game
                    try {
                        System.out.println();
                        this.createGameInput();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            menuCommand.appendString("Create new game");
            this.menuCommandsWidget.addCommand(menuCommand);

            // (3) - Reconnect to an existing game
            menuCommand = new CommandWidgetTUI(
                "r",
                () -> {
                    // Reconnecting the user to the game he selected
                    try {
                        System.out.println();
                        this.reconnectToGameInput(this.availableGamesDTO, this.availableGamesDTO.getUsedNicknames());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            menuCommand.appendString("Reconnect to an existing game");
            this.menuCommandsWidget.addCommand(menuCommand);

            // (-1) - Quit game
            menuCommand = new CommandWidgetTUI(
                "q",
                () -> {
                    // Return nothing and the program stops
                    System.out.println();
                    System.out.println(PrintUtils.addColor("[COMPUTER] Bye bye!", ANSIColors.BRIGHT_CYAN));
                    System.exit(0);
                }
            );
            menuCommand.appendString("Quit game");
            this.menuCommandsWidget.addCommand(menuCommand);
        }
    }

    /**
     * Creates an input widget whose commands are:
     *  - [0, n] -> The IDs of the available games
     *  - [-1] -> Go back to the menu widget
     */
    private void updateGameListWidget() {
        CommandWidgetTUI gameListCommand;

        // Resetting the game list input widget commands
        if (this.gameListInputWidget.getCommandMap() != null) {
            this.gameListInputWidget.getCommandMap().clear();
        }

        // Setting the "go back to menu" command
        gameListCommand = new CommandWidgetTUI("-1", this::getMenuCommand);
        gameListCommand.appendString("Go back to the menu");
        this.gameListInputWidget.addCommand(gameListCommand);

        // Setting all the other commands as all the currently available games
        for (GameInfoDTO game : this.availableGamesDTO.getAvailableGames()) {
            gameListCommand = new CommandWidgetTUI(
                "" + game.getId(),
                () -> {
                    this.selectedGameId = game.getId();
                }
            );

            // Describing the game config before adding it to the list
            gameListCommand.appendString("GameId=" + game.getId() + " (level=" + game.getLevel() + ", players=" + game.getActualPlayers() + "/" + game.getTotalPlayers() + ")");
            this.gameListInputWidget.addCommand(gameListCommand);
        }
    }

    /**
     * Method used to ask for nickname, color, gameLevel and totalGamePlayer, and then it will create a new Game
     */
    private void createGameInput() throws Exception {
        System.out.println("Creating a new game...");

        // Ask the user for:
        //  1) His nickname
        //  2) His preferred color
        //  3) The game difficulty level he wants to play at
        //  4) The amount of players he wants to play with

        // 1)
        this.playerNickname = this.getPlayerChosenNickname();
        // 2)
        System.out.print("Choose a color (Available=[RED, GREEN, BLUE, YELLOW]): ");
        PlayerColor playerColor = this.getPlayerChosenColor();
        // 3)
        int gameLevel = this.getPlayerChosenLevel();
        // 4)
        int totalPlayers = this.getPlayerChosenTotalPlayers();

        // Creating the command context to handle the actions to
        // take either onSuccess or onError, depending on the server's response
        this.currCommand = new CommandCTX(
            "createGame",
            () -> {
                synchronized (this.model) {
                    this.model.setNickname(this.playerNickname);
                    this.model.setPlayerColor(playerColor);
                    this.currCommand = null;
                }
            },
            () -> {
                try {
                    this.createGameInput();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        // Finally, sending the message to the server through
        // the selected virtual client
        this.client.sendMessage(new ConfigGame(this.playerNickname, playerColor, gameLevel, totalPlayers));
    }

    /**
     * Method used to ask for nickname and color to join the game
     */
    private void joinGameInput(GameInfoDTO game, List<String> usedNicknames) throws Exception {
        System.out.println("Joining the game with id " + game.getId() + "...");

        // Ask the player for the nickname and the color to use
        this.playerNickname = this.getPlayerChosenNickname();

        System.out.print("Choose a color (Available=" + game.getAvailableColors() + "): ");
        PlayerColor playerColor = this.getPlayerChosenColor();

        this.currCommand = new CommandCTX(
            "joinGame",
            () -> {
                synchronized (this.model) {
                    this.model.setNickname(this.playerNickname);
                    this.model.setPlayerColor(playerColor);
                    this.currCommand = null;
                }
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

        this.client.sendMessage(new NewPlayer(this.playerNickname, playerColor, game.getId()));
    }

    /**
     * Method used to reconnect a player to an existing game
     */
    private void reconnectToGameInput(AvailableGamesDTO availableGames, List<String> usedNicknames) throws Exception {
        if (usedNicknames.isEmpty()) {
            this.showLobbies(availableGames, false);
            return;
        }

        System.out.println("Trying to reconnect you to the game...");

        // Asking for the username
        this.playerNickname = this.getPlayerChosenNickname();

        this.client.sendMessage(new Reconnect(this.playerNickname));
    }

    /**
     * @return The current player's chosen nickname
     */
    public String getPlayerChosenNickname() {
        String playerNickname;

        do {
            System.out.print("Insert your nickname: ");
            playerNickname = scanner.nextLine().trim();

            if (playerNickname.isEmpty()) {
                System.out.println(PrintUtils.addColor("ERROR: Name cannot be empty. Please insert a nickname", ANSIColors.RED));
            }
        }
        while (playerNickname.isEmpty());

        return playerNickname;
    }

    /**
     * @return The current player's chosen color
     */
    public PlayerColor getPlayerChosenColor() {
        PlayerColor playerColor = null;

        do {
            String colorInput = scanner.nextLine().trim();

            if (colorInput.isEmpty()) {
                System.out.println(PrintUtils.addColor("ERROR: Color cannot be empty. Please insert a color", ANSIColors.RED));
                continue;
            }

            try {
                playerColor = PlayerColor.valueOf(colorInput.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                System.out.println(PrintUtils.addColor("ERROR: Unknown color. Please insert an available color", ANSIColors.RED));
            }
        }
        while (playerColor == null);

        return playerColor;
    }

    /**
     * @return The current player's chosen difficulty level
     */
    public int getPlayerChosenLevel() {
        int gameLevel = -1;

        do {
            System.out.print("Select game difficulty level: (0 -> Test flight, 2 -> Level 2 Flight): ");
            String line = scanner.nextLine().trim();

            try {
                gameLevel = Integer.parseInt(line);

                if (gameLevel != 0 && gameLevel != 2) {
                    System.out.println(PrintUtils.addColor("ERROR: Game difficulty level must be 0 or 2.", ANSIColors.RED));
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
            }
        }
        while (gameLevel != 0 && gameLevel != 2);

        return gameLevel;
    }

    /**
     * @return The current player's chosen total players amount for a game
     */
    public int getPlayerChosenTotalPlayers() {
        int totalPlayers = -1;

        do {
            System.out.print("Insert the total number of players (min=2, max=4): ");
            String line = scanner.nextLine().trim();

            try {
                totalPlayers = Integer.parseInt(line);

                if (totalPlayers < 2 || totalPlayers > 4) {
                    System.out.println(PrintUtils.addColor("ERROR: Number of players must be between 2 and 4.", ANSIColors.RED));
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please enter a number", ANSIColors.RED));
            }
        }
        while (totalPlayers < 2 || totalPlayers > 4);

        return totalPlayers;
    }

    /**
     * Stays in the menu widget until a valid command is given
     */
    public void getMenuCommand() {
        boolean existingCommandSelected;

        synchronized (this.ioLock) {
            do {
                System.out.println();
                System.out.println("Available menu commands:");

                existingCommandSelected = this.menuCommandsWidget.selectCommand(DEFAULT_INPUT_PREFIX);

                if (!existingCommandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            while (!existingCommandSelected);
        }
    }

    /**
     * Stays in the game list input widget until a valid command is given
     */
    public void getGameListCommand() {
        boolean existingCommandSelected;

        synchronized (this.ioLock) {
            do {
                System.out.println();
                System.out.println("Currently Available Games:");

                // Clearing the terminal before showing all the available games
                TUI.clearTerminal();

                existingCommandSelected = this.gameListInputWidget.selectCommand(DEFAULT_INPUT_PREFIX);

                if (!existingCommandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            while (!existingCommandSelected);
        }
    }

    /**
     * @param availableGamesDTO The DTO containing all currently available games that a player can choose from
     */
    @Override
    public void showLobbies(AvailableGamesDTO availableGamesDTO, boolean isFirstAccess) {
        // Storing the available games to later show them, thus
        // storing the update for later
        this.availableGamesDTO = availableGamesDTO;

        // Refresh all available games by recreating all the
        // game selection commands inside this gameListInputWidget
        this.updateGameListWidget();

        // TODO: Discuss the case where a player keeps observing the available games list
        //       and an update arrives. In that case, the TUI must be reprinted again!! (threads required)
        //       ACTIONS: Updated AvailableGamesDTO Arrives, then:
        //                  1 --> Save current user input (force ENTER)
        //                  2 --> Reprint everything
        //                  3 --> Put player back to where he was writing (with the previous input as well)
        //      In this way, updates are asynchronous and can happen at any time

        // Finally, show the GameMenuTUI to the player
        System.out.println("\nWelcome to...\n");
        TUI.printTitle();
        this.getMenuCommand();
    }

    /**
     * @param waitingForPlayers The DTO containing the current game status where all connected
     *                          players are waiting for other players to join before starting
     */
    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        synchronized (this.ioLock) {
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
}
