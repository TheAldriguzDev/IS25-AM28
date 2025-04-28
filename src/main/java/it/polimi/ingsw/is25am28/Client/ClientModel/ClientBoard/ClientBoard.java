package it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Board.Cell;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class ClientBoard {
    private int size;
    private int level;
//    private List<String> playerNicknames;
    private List<String> eliminatedPlayerNicknames;
    private Map<String, Integer> currPlayersPositions;

    private final List<ClientPlayer> players;
    private final List<ClientPlayer> eliminatedPlayers;

    public ClientBoard(BoardJSON BoardJSON) {
        this.size = BoardJSON.getSize();
        this.level = BoardJSON.getLevel();
        this.currPlayersPositions = BoardJSON.getCurrPlayerPositions();
        this.players = new ArrayList<>();
        this.eliminatedPlayers = new ArrayList<>();
    }

    public void newClientPlayer(ClientPlayer newPlayer) {
        this.players.add(newPlayer);
    }

    public void updateBoard(CardStateJSON cardState) {
        // If a player's position has been changed we need to set it again
        if (cardState.getNeedsUpdatedPositions()) {
            for (ClientPlayer player : players) {
                if (cardState.getUpdatedPositions().containsKey(player.getNickname())) {
                    player.setCursor(cardState.getUpdatedPositions().get(player.getNickname()));
                }
            }
        }
        // If a player has been eliminated we need to remove him from the player's list, and to add him to the eliminatedPlayers list
        if (cardState.getNeedsUpdatedEliminatedPlayers()) {
            for (ClientPlayer player : players) {
                if (cardState.getEliminatedPlayers().contains(player.getNickname())) {
                    eliminatedPlayers.add(player);
                    players.remove(player);
                }
            }
        }
    }

    public int getLevel() { return this.level; }

    public List<ClientPlayer> getPlayers() { return this.players; }

    public List<ClientPlayer> getEliminatedPlayers() { return this.eliminatedPlayers; }

    public int getSize() { return this.size; }

//    public Cell getHead() { return this.head; }

    /**
     * @return A widget containing this board title (optional)
     */
    private WidgetTUI getBoardTitleWidget() {
        WidgetTUI boardTitleWidget = new WidgetTUI();

        boardTitleWidget.appendString(" ==== LEVEL " + this.getLevel() + " BOARD ==== ");

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
        placements = new ArrayList<String>();

        // Getting only the currently playing players (aka: active players)
        activePlayers = new ArrayList<ClientPlayer>(this.getPlayers());
        activePlayers.removeAll(this.getEliminatedPlayers());
        eliminatedPlayers = new ArrayList<ClientPlayer>(this.getEliminatedPlayers());

        // Adding the placement strings
        placements.add("1st");
        placements.add("2nd");
        placements.add("3rd");
        placements.add("4th");
        totalPlacements = placements.size();

        // Adding the leaderboard
        boardInfoWidget.appendString("Leaderboard:");
        playerCount = activePlayers.size();

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
        boardInfoWidget.wrapWidgetWithBorder();

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
            List<WidgetTUI> widgetList = new ArrayList<WidgetTUI>();
//            Optional<ClientPlayer> optionalPlayer;
            String optionalClientPlayer;
            StringBuilder boardLine;

            int height = 6;
            int width = 8;

            // Throws an error if the set dimensions cannot be used to draw
            // a closed shape of the same perimeter as this board's size
            if ((height * 2) + (width * 2) - 4 != this.getSize()) {
                throw new IllegalArgumentException("ERROR: Cannot draw board with dimensions (height=" + height + ", width=" + width + ")");
            }

            List<String> allCells = new ArrayList<>();
//            Cell currCell = this.getHead();

            // Sets which blocks need to be colored
            Map<Integer, String> coloredCells = new HashMap<>();
            for (ClientPlayer player : this.getPlayers()) {
                if (currPlayersPositions.containsKey(player.getNickname())) {
                    coloredCells.put(currPlayersPositions.get(player.getNickname()), player.getColor().getColorString());
                }
            }




            for (int i = 0; i < this.getSize(); i++) {
                for (int j = 0; j < this.getSize(); j++) {
                    if (coloredCells.containsKey(i)) {
                        allCells.add(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, coloredCells.get(i)));
                    } else {
                        allCells.add(UnicodeCharacters.FULL_BLOCK);
                    }
                }
            }

//            // Getting all cells of the board
//            do {
//
//                optionalClientPlayer = boardCells.get(currCellIndex);
//
//                if (optionalClientPlayer.isEmpty()) {
//                    allCells.add(UnicodeCharacters.FULL_BLOCK);
//                }
//                else {
//                    PlayerColor color = null;
//                    for (ClientPlayer player : this.getPlayers()) {
//                        if (player.getNickname().equals(optionalClientPlayer)) {
//                            color = player.getColor();
//                        }
//                    }
//                    allCells.add(
//                            PrintUtils.addColor(
//                                    UnicodeCharacters.FULL_BLOCK,
//                                    color.getColorString()
//                            )
//                    );
//                }
//
//
//
////                currCell = currCell.getNextCell();
//            } while (currCellIndex < getSize());

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

            boardWidget = WidgetTUI.composeWidgetsVertically(widgetList);
            boardWidget.wrapWidgetWithBorder();

            return boardWidget;
        }

        return null;
    }


}
