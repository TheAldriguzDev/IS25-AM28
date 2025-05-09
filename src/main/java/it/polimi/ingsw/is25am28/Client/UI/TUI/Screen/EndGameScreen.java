package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.EndGameDTO;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class EndGameScreen extends Screen {

    // Constructor
    public EndGameScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    /**
     * TUI screen entry point for the end game phase
     */
    @Override
    public void showEndGame(EndGameDTO endGame) {
        WidgetTUI endGameWidget = new WidgetTUI();
        List<String> placements = new ArrayList<>();
        Map<ClientPlayer, Integer> leaderboardWithCredits = new HashMap<>();
        List<ClientPlayer> leaderboard;
        int totalPlayers;

        placements.add("1st");
        placements.add("2nd");
        placements.add("3rd");
        placements.add("4th");

        System.out.println();
        TUIHandler.clearTerminal();

        leaderboard = endGame.getPlayersPositionResult().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .map(nickname -> this.model.getAllClientPlayers().get(nickname))
                .toList();

        totalPlayers = leaderboard.size();

        for (ClientPlayer player : leaderboard) {
            leaderboardWithCredits.put(player, endGame.getPlayersCredits().get(player.getNickname()));
        }

        endGameWidget
                .appendString(COMPUTER_MSG_TAG + "The game has ended!")
                .addPadding(0, 0, 1, 0);

        if (endGame.getWinner() != null && !endGame.getWinner().isEmpty()) {
            ClientPlayer winnerPlayer = this.model.getAllClientPlayers().get(endGame.getWinner());
            String stars = PrintUtils.addColor("*", ANSIColors.BRIGHT_YELLOW).repeat(3);

            endGameWidget.appendString(
                stars + SPACE
                + PrintUtils.addColor(winnerPlayer.getNickname(), winnerPlayer.getColor().getColorString())
                + SPACE
                + PrintUtils.addColor("won the game!", ANSIColors.BRIGHT_MAGENTA)
                + SPACE + stars
            );

            endGameWidget
                    .centerWidgetScreen()
                    .addPadding(1, 1, 1, 1);
        }
        else {
            endGameWidget
                    .addPadding(1, 1, 0, 1);
        }

        endGameWidget
                .wrapWidgetWithBorder()
                .addPadding(0, 1, 1, 1);

        endGameWidget
                .appendString("Leaderboard:");

        // Printing the leaderboard and each player's final credits
        for (int i = 0; i < totalPlayers; i++) {
            ClientPlayer currPlayer = leaderboard.get(i);

            endGameWidget.appendScreen(
                new WidgetTUI()
                        .appendString(placements.get(i) + " - " + PrintUtils.addColor(currPlayer.getNickname(), currPlayer.getColor().getColorString()) + " (Final Credits: " + leaderboardWithCredits.get(currPlayer) + ")")
                        .addPadding(0, 1, 0, 1)
                        .getScreen()
            );
        }

        endGameWidget
                .centerWidgetScreen()
                .wrapWidgetWithBorder()
                .printWidget();

        System.out.println();
        System.out.println("Press any key and then [ENTER] to quit the game...");

        try {
            this.inputThread.waitForInput();
            System.exit(0);
        }
        catch (InterruptedException e) {
            System.exit(0);
        }
    }
}
