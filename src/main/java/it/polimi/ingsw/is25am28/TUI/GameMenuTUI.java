package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameMenuTUI extends TUI {
    // Testing
    public static void main(String[] args) {
        GameMenuTUI gameMenuTUI = new GameMenuTUI();

        List<GameInfoDTO> availableGames = new ArrayList<>();

        List<String> availableColors;
        GameInfoDTO gameInfoDTO;

        // Game 1
        availableColors = new ArrayList<>();
        availableColors.add("RED");
        availableColors.add("GREEN");
        gameInfoDTO = new GameInfoDTO(0, 2, 4, 2, availableColors);
        availableGames.add(gameInfoDTO);

        // Game 2
        availableColors = new ArrayList<>();
        availableColors.add("RED");
        availableColors.add("GREEN");
        availableColors.add("BLUE");
        gameInfoDTO = new GameInfoDTO(1, 1, 4, 2, availableColors);
        availableGames.add(gameInfoDTO);

        // Game 3
        availableColors = new ArrayList<>();
        availableColors.add("GREEN");
        gameInfoDTO = new GameInfoDTO(2, 2, 4, 3, availableColors);
        availableGames.add(gameInfoDTO);

        gameMenuTUI.setAvailableGames(availableGames);
        gameMenuTUI.showTUI();
    }

    public static final int LOBBY_COMMANDS_PER_COLUMN = 1;
    public static final int AVAILABLE_GAMES_PER_COLUMN = 16;

    private InputWidgetTUI menuCommandsWidget;
    private InputWidgetTUI gameListInputWidget;

    private int selectedGameId = -1;
    private List<GameInfoDTO> availableGames;

    // Constructor
    public GameMenuTUI() {
        // TODO:
        super(null);

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

            // (1) - List available games
            menuCommand = new CommandWidgetTUI(
                "l",
                () -> {
                    boolean existingCommandSelected;

                    synchronized (this.ioLock) {
                        do {
                            System.out.println();
                            System.out.println("Currently Available Games:");

                            // First refresh all available games, then list them and
                            // finally ask the user to select one of the available options
                            this.updateGameListWidget();

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
            );
            menuCommand.appendString("List available games");
            this.menuCommandsWidget.addCommand(menuCommand);

            // (2) - Create new game
            menuCommand = new CommandWidgetTUI(
                "c",
                () -> {
                    // Create new game
                    synchronized (this.ioLock) {
                        try {
                            System.out.println();
                            this.createGameInput();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            );
            menuCommand.appendString("Create new game");
            this.menuCommandsWidget.addCommand(menuCommand);

            // (3) - Reconnect to an existing game
            menuCommand = new CommandWidgetTUI(
                "r",
                () -> {
                    boolean existingCommandSelected;

                    synchronized (this.ioLock) {
                        do {
                            System.out.println();
                            System.out.println("Currently Available Games:");

                            // First refresh all available games, then list them and
                            // finally ask the user to select one of the available options
                            this.updateGameListWidget();

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

        // Setting the "go back to menu" command
        gameListCommand = new CommandWidgetTUI("-1", this::getLobbyCommand);
        gameListCommand.appendString("Go back to the menu");
        this.gameListInputWidget.addCommand(gameListCommand);

        // Setting all the other commands as all the currently available games
        for (GameInfoDTO game : this.availableGames) {
            gameListCommand = new CommandWidgetTUI(
                "" + game.getId(),
                () -> {
                    this.selectedGameId = game.getId();

                    System.out.println("SELECTED: " + this.selectedGameId);
                }
            );

            // Describing the game config before adding it to the list
            gameListCommand.appendString("GameId=" + game.getId() + " (level=" + game.getLevel() + ", players=" + game.getActualPlayers() + "/" + game.getTotalPlayers() + ")");
            this.gameListInputWidget.addCommand(gameListCommand);
        }
    }

    /**
     * Sets the list of currently available games
     */
    public void setAvailableGames(List<GameInfoDTO> availableGames) {
        this.availableGames = availableGames;
    }

    /**
     * Method used to ask for nickname, color, gameLevel and totalGamePlayer, and then it will create a new Game
     */
    private void createGameInput() throws Exception {
        System.out.println("Creating a new game ...");

        // Use BufferedReader to avoid Scanner newline issues
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
                    this.createGameInput();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        this.client.sendMessage(new ConfigGame(playerName, playerColor, gameLevel, totalPlayers));
    }

    // TODO: Wait that Matteo implements it
    private void reconnectToGameInput() {

    }

    /**
     * Stays in the menu until a valid command was given
     */
    public void getLobbyCommand() {
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

    @Override
    public void showTUI() {
        System.out.println("Welcome to...");
        TUI.printTitle();
        this.getLobbyCommand();
    }
}
