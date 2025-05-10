package it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class ClientBoard {
    private static final Map<Integer, Pair<Integer, Integer>> boardDimensions;

    static {
        boardDimensions = new HashMap<>();

        // Adding board widget dimensions for all levels (0,1,2,3)
        // NOTE: Pair is (height, width)
        boardDimensions.put(0, new Pair<>(5, 6));
        boardDimensions.put(1, new Pair<>(5, 6));
        boardDimensions.put(2, new Pair<>(6, 8));
        boardDimensions.put(3, new Pair<>(9, 10));
    }

    private final int size;
    private final int level;
    // private List<String> playerNicknames;
    private List<ClientPlayer> eliminatedPlayers;
    // private Map<String, Integer> currPlayersPositions;
    // private final List<ClientPlayer> eliminatedPlayers;

    private final Map<String, ClientPlayer> players;

    public ClientBoard(BoardJSON BoardJSON, ClientModel clientModel) {
        this.size = BoardJSON.getSize();
        this.level = BoardJSON.getLevel();
        this.players = new HashMap<>(clientModel.getAllClientPlayers());
        // Setting the starting players' positions
        for (String playerNickName : BoardJSON.getStartingPlayerPositions().keySet()) {
            this.players.get(playerNickName).setCursor(BoardJSON.getStartingPlayerPositions().get(playerNickName));
        }
        this.eliminatedPlayers = new ArrayList<>();
        for (String playerNickName : BoardJSON.getEliminatedPlayersNickname()) {
            this.eliminatedPlayers.add(this.players.get(playerNickName));
            this.players.remove(playerNickName);
        }
    }

    /**
     * This method updates only what has been changed (regarding players' info)
     * */
    public void updateBoard(CardStateJSON cardState) {
        // If a player's position has been changed we need to set it again
        if (cardState.getNeedsUpdatedPositions()) {
            for (String playerNickname : cardState.getUpdatedPositions().keySet()) {
                this.players.get(playerNickname).setCursor(cardState.getUpdatedPositions().get(playerNickname));
            }
        }
        // If a player has been eliminated we need to remove him from the player's list, and add him to the eliminatedPlayers list
        if (cardState.getNeedsUpdatedEliminatedPlayers()) {
            for (String playerNickname : cardState.getEliminatedPlayers()) {
                this.eliminatedPlayers.add(players.get(playerNickname));
                this.players.remove(playerNickname);
            }
        }
    }

    // TODO: see if necessary to delete this
    public int getLevel() { return this.level; }

    public List<ClientPlayer> getEliminatedPlayers() { return this.eliminatedPlayers; }

    public int getSize() { return this.size; }

    public List<ClientPlayer> getPlayers() {
        return this.players.values().stream().toList();
    }

    /**
     * @return A widget containing this board title (optional)
     */
    private WidgetTUI getBoardTitleWidget() {
        WidgetTUI boardTitleWidget = new WidgetTUI();

        boardTitleWidget.appendString("[BOARD - LVL: " + this.getLevel() + "]");

        return boardTitleWidget;
    }

    /**
     * @return A widget containing information about the current state of the board
     */
    private WidgetTUI getBoardInfoWidget() {
        WidgetTUI boardInfoWidget;
        List<String> placements;
        List<ClientPlayer> activePlayers, eliminatedPlayers;
        String coloredNickname;
        int playerCount, totalPlacements;

        // Initializations
        boardInfoWidget = new WidgetTUI();
        placements = new ArrayList<>();

        // Getting only the currently playing players (aka: active players)
        activePlayers = new ArrayList<>(this.getPlayers());
        activePlayers.removeAll(this.getEliminatedPlayers());
        eliminatedPlayers = new ArrayList<>(this.getEliminatedPlayers());

        // Adding the placement strings
        placements.add("1st");
        placements.add("2nd");
        placements.add("3rd");
        placements.add("4th");
        totalPlacements = placements.size();

        // Adding the leaderboard
        boardInfoWidget.appendString("Leaderboard:");
        playerCount = activePlayers.size();

        //Sorting the activePlayers list
        activePlayers.sort(Comparator.comparingInt(ClientPlayer::getCursor).reversed());

        // Adding the placement for each active player
        for (int i = 0; i < playerCount; i++) {
            coloredNickname = PrintUtils.addColor(
                    activePlayers.get(i).getNickname(),
                    activePlayers.get(i).getColor().getColorString()
            );

            boardInfoWidget.appendString(placements.get(i) + " - " + coloredNickname);
        }

        // Adding the final placement for all eliminated players (if there are any)
        playerCount = this.getEliminatedPlayers().size();

        if (playerCount > 0) {
            // Adding a height spacer and heading for the eliminated players list
            boardInfoWidget.appendString(" ");
            boardInfoWidget.appendString("Eliminated Players:");

            // Adding a big red X to symbolize that the player was eliminated
            String redX = PrintUtils.addColor("(X)", ANSIColors.RED);
            redX += SPACE;

            // Adding all the eliminated players to the info widget's screen
            for (int i = 0; i < playerCount; i++) {
                coloredNickname = PrintUtils.addColor(
                        eliminatedPlayers.get(i).getNickname(),
                        eliminatedPlayers.get(i).getColor().getColorString()
                );

                boardInfoWidget.appendString(redX + placements.get(totalPlacements - 1 - i) + " - " + coloredNickname);
            }
        }

        // Finally, wrap the board info widget with the default border
        boardInfoWidget
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();

        return boardInfoWidget;
    }

    /**
     * @return A TUIPage border-wrapped widget containing the board's text representation
     *         as well as other information about itself
     */
    public WidgetTUI generateWidget() {
        // Only create the widget if the board has been created
        if (this.getSize() > 0) {
            WidgetTUI boardWidget = new WidgetTUI();
            List<WidgetTUI> widgetList = new ArrayList<>();
            StringBuilder boardLine;

            List<String> allCells = new ArrayList<>();

            int height = boardDimensions.get(this.level).getKey();
            int width = boardDimensions.get(this.level).getValue();

            // Sets which blocks need to be colored
            // Map<String, ClientPlayer> players = new HashMap<>(this.players);
            Map<Integer, String> coloredCells = this.getIntegerStringMap();

            for (int i = 0; i < this.getSize(); i++) {
                if (coloredCells.containsKey(i)) {
                    allCells.add(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, coloredCells.get(i)));
                    //System.out.println("Added cell number " + i + " COLOR " + ANSIColors.RESET);
                } else {
                    allCells.add(UnicodeCharacters.FULL_BLOCK);
                }
            }

            List<String> topSide = new ArrayList<>(allCells.subList(0, width));
            List<String> rightSide = new ArrayList<>(allCells.subList(width, width + height - 1));
            List<String> bottomSide = new ArrayList<>(allCells.subList(width + height - 2, (2 * width) + height - 2));
            List<String> leftSide = new ArrayList<>(allCells.subList((2 * width) + height - 2, this.getSize()));

            // Top line
            boardLine = new StringBuilder();

            for (String s : topSide) {
                boardLine.append(SPACE);
                boardLine.append(s);
                boardLine.append(SPACE);
            }

            // Adding the top side
            boardWidget.appendString(boardLine.toString());
            leftSide = leftSide.reversed();

            // Middle lines
            for (int i = 0; i < height - 2; i++) {
                boardLine = new StringBuilder();

                boardLine.append(SPACE);
                boardLine.append(leftSide.get(i));
                boardLine.append(SPACE.repeat(3 * (width - 2) + 2));
                boardLine.append(rightSide.get(i));
                boardLine.append(SPACE);

                boardWidget.appendString(boardLine.toString());
            }

            // Bottom line
            boardLine = new StringBuilder();
            bottomSide = bottomSide.reversed();

            for (String s : bottomSide) {
                boardLine.append(SPACE);
                boardLine.append(s);
                boardLine.append(SPACE);
            }

            // Adding the bottom side
            boardWidget.appendString(boardLine.toString());

            // Adding a border to this widget
            boardWidget.wrapWidgetWithBorder();

            // Composing all the board widgets into the final one
            widgetList.add(this.getBoardTitleWidget());
            widgetList.add(boardWidget);
            widgetList.add(this.getBoardInfoWidget());

            boardWidget =
                    WidgetTUI.composeWidgetsVertically(widgetList)
                    .centerWidgetScreen()
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder();

            return boardWidget;
        }

        return null;
    }

    /**
     * @return A map of all the colored cells indicating
     *         where each player is positioned.
     */
    private Map<Integer, String> getIntegerStringMap() {
        Map<Integer, String> coloredCells = new HashMap<>();

        for (ClientPlayer player : this.players.values()) {
            int relCursor = player.getCursor();

            relCursor %= this.getSize();

            if (relCursor < 0) {
                relCursor += this.getSize();
            }

            coloredCells.put(relCursor, player.getColor().getColorString());
        }

        return coloredCells;
    }
}
