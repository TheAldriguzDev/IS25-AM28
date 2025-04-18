package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClientTUI implements ClientUI {
    private final ClientModel model;
    private VirtualView client;
    private final Object ioLock;

    private final BufferedReader scanner;

    private CommandCTX currCommand;

    private String playerNickname;

    public ClientTUI(ClientModel model) {
        this.model = model;
        this.ioLock = new Object();
        this.scanner = new BufferedReader(new InputStreamReader(System.in));
        this.currCommand = null;
    }

    private static List<String> getGamesOptions(List<GameInfoDTO> availableGames) {
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
        options.add("Reconnect to an existing game");
        options.add("Refresh games");
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
    public void showLobbies(AvailableGamesDTO state) throws Exception {
        synchronized (ioLock) {
            printTitle();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            List<GameInfoDTO> availableGames = state.getAvailableGames();

            // Build the list of available games
            List<String> options = getGamesOptions(availableGames);

            // Display the options
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }

            int choice = -1;
            do {
                System.out.print("Choose an option: ");
                String line = reader.readLine().trim();
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
                // TODO: Invoke the reconnecting to game
            } else {
                // TODO: Invoke the refresh games command
            }
        }
    }

    /**
     * Method used to ask for nickname and color to join the game
     */
    private void joinGameInput(GameInfoDTO game, List<String> usedNicknames) throws Exception {
        System.out.println("Joining the game with id " + game.getId() + " ...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Ask for nickname
        String playerName;
        do {
            System.out.print("Enter your name: ");
            playerName = reader.readLine().trim();
            if (playerName.isEmpty() || usedNicknames.contains(playerName)) {
                System.out.println("Invalid input: name already used or empty.");
            }
        } while (playerName.isEmpty() || usedNicknames.contains(playerName));
        this.playerNickname = playerName;

        // Ask for color
        PlayerColor playerColor = null;
        do {
            System.out.print("Choose a color " + game.getAvailableColors() + ": ");
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

    // TODO
    private void reconnectToGameInput() {

    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        synchronized (this.model) {
            if (this.model.getNickname() != null) {
                int connected = waitingForPlayers.getLobbyTotalSpot() - waitingForPlayers.getAvailableSpots();
                int total = waitingForPlayers.getLobbyTotalSpot();
                List<String> nicknames = waitingForPlayers.getUsedNicknames();

                System.out.printf("Waiting for more players to join the game [%d/%d]...%n", connected, total);

                if (!nicknames.isEmpty()) {
                    System.out.println("Connected players: " + nicknames);
                }
            }
        }
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) {
        printTileList(shipConstruction.getAllComponents());
    }

    public static void printTileList(List<Map<String, Object>> tileMaps) {
        int index = 1;
        for (Map<String, Object> map : tileMaps) {
            int id = (int) map.get("id");
            int typeId = (int) map.get("tid");
            int row = (int) map.get("row");
            int col = (int) map.get("col");

            // Verifica che "connectors" sia effettivamente una List<Integer>
            Object connectorsObj = map.get("connectors");
            List<Integer> connectorOrdinals = null;
            if (connectorsObj instanceof List<?>) {
                // Verifica che ogni elemento della lista sia un Integer
                connectorOrdinals = new ArrayList<>();
                for (Object obj : (List<?>) connectorsObj) {
                    if (obj instanceof Integer) {
                        connectorOrdinals.add((Integer) obj);
                    } else {
                        // Gestisci errore se un elemento non è un Integer
                        System.err.println("Warning: Invalid type in connectors list.");
                    }
                }
            }

            System.out.printf("🔹 Tile #%d%n", index++);
            System.out.printf("   ID       : %d%n", id);
            System.out.printf("   Type ID  : %d%n", typeId);
            System.out.printf("   Position : (%d, %d)%n", row, col);

            if (connectorOrdinals != null) {
                System.out.printf("   Sides    : %s%n", connectorOrdinals);
            } else {
                System.out.println("   Sides    : Invalid data.");
            }

            printComponentDetails(map, typeId);

            System.out.println();
        }
    }

    public static void printComponentDetails(Map<String, Object> componentData, int typeId) {
        String indent = "  ";

        switch (typeId) {
            case 0: // Cannon
                System.out.println("🔫 Cannon:");
                System.out.println(indent + "Force : " + componentData.get("force"));
                break;

            case 1: // Cabin
                System.out.println("🏠 Cabin:");
                List<String> inhabitants = toStringList(componentData.get("inhabitants"));
                System.out.println(indent + "Inhabitants: " + inhabitants);
                break;

            case 2: // Storage
                System.out.println("📦 Storage:");
                System.out.println(indent + "Capacity   : " + componentData.get("capacity"));
                System.out.println(indent + "Special    : " + componentData.get("special"));
                List<String> storedItems = toStringList(componentData.get("storedItems"));
                System.out.println(indent + "StoredItems: " + storedItems);
                break;

            case 3: // Vital
                System.out.println("❤️ Vital:");
                System.out.println(indent + "Type       : " + componentData.get("type"));
                break;

            case 4: // Engine
                System.out.println("🚀 Engine:");
                System.out.println(indent + "Speed      : " + componentData.get("speed"));
                break;

            case 5: // Battery
                System.out.println("🔋 Battery:");
                System.out.println(indent + "Capacity   : " + componentData.get("capacity"));
                System.out.println(indent + "Available  : " + componentData.get("available"));
                break;

            case 6: // Shield
                System.out.println("🛡️ Shield:");
                // eventuali proprietà specifiche
                break;

            case 7: // Structural
                System.out.println("🧱 Structural:");
                // eventuali proprietà specifiche
                break;

            default:
                System.out.println("❓ Unknown component type: " + typeId);
        }

        System.out.println();  // separatore
    }

    /**
     * Converte in List<String> una proprietà che dovrebbe essere List<?>.
     * Se l'oggetto non è una lista, restituisce lista vuota.
     */
    private static List<String> toStringList(Object obj) {
        if (!(obj instanceof List<?>)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (Object item : (List<?>) obj) {
            result.add(String.valueOf(item));
        }
        return result;
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
            this.currCommand.handleError(error.getError());
        }
    }
}
