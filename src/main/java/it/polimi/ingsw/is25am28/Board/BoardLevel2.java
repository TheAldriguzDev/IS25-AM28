package it.polimi.ingsw.is25am28.Board;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.TUI.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardLevel2 extends Board {

    public BoardLevel2() {
        super();
        this.setSize(24);
        this.setLevel(2);
    }

    /**
     * buildBoard builds the board and sets the cells where the players can be placed.
     * */
    public void buildBoard() {
        ArrayList<Cell> initialCells = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            Cell newCell = this.addCell(i);

            if (i == 0 || i == 1 || i == 3 || i == 6) {
                initialCells.addFirst(newCell);
            }
        }

        this.setInitialCells(initialCells);
    }

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
        List<Player> activePlayers, eliminatedPlayers;
        String coloredNickname;
        int playerCount, totalPlacements;

        // Initializations
        boardInfoWidget = new WidgetTUI();
        placements = new ArrayList<String>();

        // Getting only the currently playing players (aka: active players)
        activePlayers = new ArrayList<Player>(this.getPlayers());
        activePlayers.removeAll(this.getEliminatedPlayers());
        eliminatedPlayers = new ArrayList<Player>(this.getEliminatedPlayers());

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
                    activePlayers.get(i).getPlayerColor().getColorString()
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
            redX += PrintUtils.getSpace();

            // Adding all the eliminated players to the info widget's screen
            for (int i = 0; i < playerCount; i++) {
                coloredNickname = PrintUtils.addColor(
                        eliminatedPlayers.get(i).getNickname(),
                        eliminatedPlayers.get(i).getPlayerColor().getColorString()
                );

                boardInfoWidget.appendString(redX + placements.get(totalPlacements - 1 - i) + " - " + coloredNickname);
            }
        }

        // Finally, wrap the board info widget with the default border
        boardInfoWidget.wrapWidgetWithBorder();

        return boardInfoWidget;
    }

    /**
     * @return A TUI border-wrapped widget containing the board's text representation
     *         as well as other information about itself
     */
    public WidgetTUI generateWidget() {
        // Only create the widget if the board has been created
        if (this.getSize() > 0) {
            WidgetTUI boardWidget = new WidgetTUI();
            List<WidgetTUI> widgetList = new ArrayList<WidgetTUI>();
            Optional<Player> optionalPlayer;
            StringBuilder boardLine;

            int height = 6;
            int width = 8;

            // Throws an error if the set dimensions cannot be used to draw
            // a closed shape of the same perimeter as this board's size
            if ((height * 2) + (width * 2) - 4 != this.getSize()) {
                throw new IllegalArgumentException("ERROR: Cannot draw board with dimensions (height=" + height + ", width=" + width + ")");
            }

            List<String> allCells = new ArrayList<>();
            Cell currCell = this.getHead();

            // Getting all cells of the board
            do {
                optionalPlayer = currCell.getPlayer();

                if (optionalPlayer.isEmpty()) {
                    allCells.add(UnicodeCharacters.FULL_BLOCK);
                }
                else {
                    allCells.add(
                        PrintUtils.addColor(
                            UnicodeCharacters.FULL_BLOCK,
                            optionalPlayer.get().getPlayerColor().getColorString()
                        )
                    );
                }

                currCell = currCell.getNextCell();
            } while (currCell != this.getHead());

            List<String> topSide = new ArrayList<>(allCells.subList(0, width - 1));
            List<String> rightSide = new ArrayList<>(allCells.subList(width - 1, width + height - 3));
            List<String> bottomSide = new ArrayList<>(allCells.subList(width + height - 3, (2 * width) + height - 4));
            List<String> leftSide = new ArrayList<>(allCells.subList((2 * width) + height - 4, this.getSize() - 2));

            // Top line
            boardLine = new StringBuilder();

            for (String s : topSide) {
                boardLine.append(PrintUtils.getSpace());
                boardLine.append(s);
                boardLine.append(PrintUtils.getSpace());
            }

            // Adding the top side
            boardWidget.appendString(boardLine.toString());
            leftSide = leftSide.reversed();

            // Middle lines
            for (int i = 0; i < height - 2; i++) {
                boardLine = new StringBuilder();

                boardLine.append(PrintUtils.getSpace());
                boardLine.append(leftSide.get(i));
                boardLine.append(PrintUtils.getSpace().repeat(3 * (width - 2) - 1));
                boardLine.append(rightSide.get(i));
                boardLine.append(PrintUtils.getSpace());

                boardWidget.appendString(boardLine.toString());
            }

            // Bottom line
            boardLine = new StringBuilder();
            bottomSide = bottomSide.reversed();

            for (String s : bottomSide) {
                boardLine.append(PrintUtils.getSpace());
                boardLine.append(s);
                boardLine.append(PrintUtils.getSpace());
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
