package it.polimi.ingsw.is25am28.Model.Board;

import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUIGenerator;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

/**
 * Abstract base class for board management, including board creation,
 * player movement, validation, and elimination.
 *
 * Subclasses provide implementations for building specific board types
 * based on the current game level.
 */

public abstract class Board implements WidgetTUIGenerator {
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

    private int size;
    private Cell head;
    final ArrayList<Cell> initialCells = new ArrayList<>();
    private final List<Player> players;
    private final List<Player> eliminatedPlayer;
    private int level;

    public Board() {
        this.players = new ArrayList<>();
        this.eliminatedPlayer = new ArrayList<>();
    }

    /**
     * @return the size of the board
     */
    public int getSize() { return size; }

    /**
     * Sets the size of the board.
     *
     * @param size the size to be set for the board
     */
    public void setSize(int size) { this.size = size; }

    /**
     * @return the head of the board
     */
    public Cell getHead() { return head; }

    /**
     * @return the ArrayList of the initials cells of the board where the players will be set when they finish their ship
     * */
    protected ArrayList<Cell> getInitialCells() {
        return initialCells;
    }

    /**
     * Sets the initial cells of the board. This method clears the current list of initial cells
     * and replaces it with the provided list of cells.
     *
     * @param initialCells the list of cells to be set as the initial cells of the board
     */
    protected void setInitialCells(ArrayList<Cell> initialCells) {
        this.initialCells.clear();
        this.initialCells.addAll(initialCells);
    }

    /**
     * Retrieves the list of all players currently on the board (not eliminated).
     *
     * @return a new list containing all players on the board
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Retrieves the list of all eliminated players.
     *
     * @return a new list containing all the eliminated players
     */
    public List<Player> getEliminatedPlayers() {
        return new ArrayList<>(eliminatedPlayer);
    }

    /**
     * @return the level of the board
     */
    public int getLevel() { return level; }

    /**
     * Sets the level of the board.
     *
     * @param level the level of the game, used to create the correct board
     */
    protected void setLevel(int level) { this.level = level; }

    /**
     * Adds a new player to the board if they are not already present in the list of players.
     *
     * @param player the player to be added to the board
     * @return the current board instance after adding the player
     */
    public Board newPlayer(Player player) {
        if( !players.contains(player) )
            players.add(player);
        return this;
    }

    /**
     * Eliminates a player from the board. If the player is present in the list of active players,
     * they are removed, marked as eliminated, and added to the list of eliminated players.
     *
     * @param player the player to be eliminated
     * @return the current board instance after the player has been eliminated
     * @throws IllegalArgumentException if the specified player is not in the list of active players
     */
    public Board eliminatePlayer(Player player) throws IllegalArgumentException {
        if (players.remove(player)) {
            player.eliminate();
            eliminatedPlayer.add(player);
        } else {
            throw new IllegalArgumentException("The given player is not in the players list");
        }

        return this;
    }

    /**
     * Adds a new cell at the specified index and links it into the circular doubly-linked list
     * of existing cells. If the list is empty, the new cell becomes the head.
     * New cells are always appended at the end of the current list.
     *
     * @param idx the index to assign to the newly added cell
     * @return the newly created cell
     */
    protected Cell addCell(int idx) {
        Cell newCell = new Cell(idx);

        if (head == null) {
            head = newCell;
            head.setPrevCell(newCell);
            head.setNextCell(newCell);
        } else {
            Cell last = head.getPrevCell();
            last.setNextCell(newCell);
            newCell.setPrevCell(last);
            newCell.setNextCell(head);
            head.setPrevCell(newCell);
        }

        return newCell;
    }

    /**
     * Abstract method responsible for constructing the board.
     * It builds the complete board structure, including its size and initial cells,
     * based on the specific type of board required for the current game level.
     */
    public abstract void buildBoard();

    /**
     * Adds the given player to the first available initial cell on the board.
     * This method is synchronized to ensure thread safety when multiple clients
     * attempt to join the board simultaneously.
     *
     * @param player the player to be added to the board
     */
    public synchronized void addPlayerToBoard(Player player) {

        for (Cell cell : initialCells) {
            if (cell.isEmpty()) {
                cell.setPlayer(player);

                player.setCurrentCell(cell);
                player.setCursor(cell.getIdx());

                return;
            }
        }

        // Re-order the current player position since the order of being added to the board can be different
        // of the order of the client registration to the game
        this.validatePlayersPosition();
    }

    /**
     * Moves the given player forward by the specified number of steps from their current cell.
     * Occupied cells encountered along the movement are skipped.
     *
     * @param player the player to be moved
     * @param steps the number of steps to move the player forward
     */
    public void movePlayerForward(Player player, int steps) {
        Cell tmpCell = player.getCurrentCell();

        while (steps > 0) {
            tmpCell = tmpCell.getNextCell();

            // Decrease the current step counter only if the cell is actually empty (no player)
            if (tmpCell.isEmpty()) {
                steps--;
            }

            player.setCursor(player.getCursor() + 1);
        }

        if (!tmpCell.equals(player.getCurrentCell())) {
            player.getCurrentCell().removePlayer();
            tmpCell.setPlayer(player);

            player.setCurrentCell(tmpCell);
        }
    }

    /**
     * Moves the given player backward by the specified number of steps from their current cell.
     * Occupied cells encountered along the movement are skipped.
     *
     * @param player the player to be moved
     * @param steps the number of steps to move the player forward
     */
    public void movePlayerBackward(Player player, int steps) {
        Cell tmpCell = player.getCurrentCell();

        while (steps > 0) {
            tmpCell = tmpCell.getPrevCell();

            // Decrease the current step counter only if the cell is actually empty (no player)
            if (tmpCell.isEmpty()) {
                steps--;
            }

            player.setCursor(player.getCursor() - 1);
        }

        if (!tmpCell.equals(player.getCurrentCell())) {
            player.getCurrentCell().removePlayer();
            tmpCell.setPlayer(player);

            player.setCurrentCell(tmpCell);
        }
    }

    /**
     * Checks the players' cursors to identify any doubled players and eliminate them.
     * Also resets the player list to maintain the correct turn order.
     * @return A list containing the nicknames of the players eliminated
     */
    public synchronized List<String> validatePlayersPosition() {
        List<String> lappedPlayersNickname = new ArrayList<>();

        int maxCursor = players.stream()
                .mapToInt(Player::getCursor)
                .max()
                .orElse(0);

        List<Player> doubledPlayers = players
                .stream()
                .filter(player -> player.getCursor() + this.getSize() < maxCursor)
                .toList();

        // Remove the player from the current players and add it to the eliminated ones
        // Set the cell to null, since it has been removed from the board and mark the player as eliminated
        for (Player player : doubledPlayers) {
            lappedPlayersNickname.add(player.getNickname());
            players.remove(player);
            eliminatedPlayer.add(player);

            player.getCurrentCell().removePlayer();
            player.eliminate();
            player.setCurrentCell(null);
        }

        // Re-order the current players by theirs cursor
        players.sort((p1, p2) -> Integer.compare(p2.getCursor(), p1.getCursor()));

        return lappedPlayersNickname;
    }

    /**
     * Generates a BoardJSON representation of the current state of the board.
     *
     * @return a {@link BoardJSON} object containing details about the board's size, level,
     *         players, eliminated players, and their positions on the board.
     */
    public BoardJSON generateState(){
        return BoardJSON.fromBoard(this);
    }

    /**
     * @return A widget containing this board title (optional)
     */
    private WidgetTUI getBoardTitleWidget() {
        WidgetTUI boardTitleWidget = new WidgetTUI();

        boardTitleWidget.appendString("[LEVEL " + this.getLevel() + " BOARD]");

        return boardTitleWidget;
    }

    /**
     * @return A widget containing information about the current state of the board
     */
    private WidgetTUI getBoardInfoWidget() {
        WidgetTUI boardInfoWidget;
        List<String> placements;
        List<Player> activePlayers, eliminatedPlayers;
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
        activePlayers.sort(Comparator.comparingInt(Player::getCursor).reversed());

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

            int height, width;

            // Sets which blocks need to be colored
            Map<Integer, String> coloredCells = new HashMap<>();
            for (Player player : this.players) {
                coloredCells.put(player.getCursor(), player.getColor().getColorString());
            }

            for (int i = 0; i < this.getSize(); i++) {
                if (coloredCells.containsKey(i)) {
                    allCells.add(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, coloredCells.get(i)));
                } else {
                    allCells.add(UnicodeCharacters.FULL_BLOCK);
                }
            }

            if (boardDimensions.containsKey(this.level)) {
                height = boardDimensions.get(this.level).getKey();
                width = boardDimensions.get(this.level).getValue();

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
            }
            else {
                // Default board is printed as a line if no valid level is provided
                boardLine = new StringBuilder();

                for (String s : allCells) {
                    boardLine.append(SPACE);
                    boardLine.append(s);
                    boardLine.append(SPACE);
                }

                boardWidget
                    .appendString(boardLine.toString())
                    .addPadding(1, 1, 1, 1)
                    .wrapWidgetWithBorder();
            }

            // Composing all the board widgets into the final one
            widgetList.add(this.getBoardTitleWidget());
            widgetList.add(boardWidget);
            widgetList.add(this.getBoardInfoWidget());

            boardWidget = WidgetTUI.composeWidgetsVertically(widgetList)
                    .centerWidgetScreen()
                    .wrapWidgetWithBorder();

            return boardWidget;
        }

        return null;
    }
}
