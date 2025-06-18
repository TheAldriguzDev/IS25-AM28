package it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

class ClientBoardTest {
    ClientModel clientModel;
    Board serverBoard;
    ClientBoard clientBoard;
    Player p1;
    Player p2;
    Player p3;
    Player p4;

    @BeforeEach
    void init() {
        clientModel = new ClientModel();

        serverBoard = new BoardLevel2();
        serverBoard.buildBoard();

        p1 = new Player("Player 1", PlayerColor.RED, 2);
        p2 = new Player("Player 2", PlayerColor.BLUE, 2);
        p3 = new Player("Player 3", PlayerColor.GREEN, 2);
        p4 = new Player("Player 4", PlayerColor.YELLOW, 2);

        serverBoard.newPlayer(p1);
        serverBoard.newPlayer(p2);
        serverBoard.newPlayer(p3);
        serverBoard.newPlayer(p4);

        for (Player player : serverBoard.getPlayers()) {
            serverBoard.addPlayerToBoard(player);
        }

        clientModel.addNewPlayer("Player 1", PlayerColor.RED);
        clientModel.addNewPlayer("Player 2", PlayerColor.BLUE);
        clientModel.addNewPlayer("Player 3", PlayerColor.GREEN);
        clientModel.addNewPlayer("Player 4", PlayerColor.YELLOW);

        clientModel.setClientBoard(clientBoard = new ClientBoard(serverBoard.generateState(), clientModel));


    }

    @Test
    public void printClientBoardTest () {
        WidgetTUI clientBoardWidget;
        clientBoardWidget = clientBoard.generateWidget();
        System.out.println("Client Version");
        clientBoardWidget.printWidget();

        CardStateJSON state = new CardStateJSON();
        state.setNeedsBoardUpdate(true);
        state.setNeedsUpdatedPositions(true);
        state.setUpdatedPositions(Map.of("Player 1", 15));
        state.setNeedsUpdatedEliminatedPlayers(true);
        state.setEliminatedPlayers(Arrays.asList("Player 2"));
        clientModel.getClientBoard().updateBoard(state, new TUIHandler(null, null)); // null just for testing, not in production

        System.out.println("DOPO LA MODIFICA");
        clientBoardWidget = clientBoard.generateWidget();
        clientBoardWidget.printWidget();
    }

    @Test
    public void printServerBoardTest () {
        WidgetTUI serverBoardWidget;
        serverBoardWidget = serverBoard.generateWidget();
        System.out.println("Server Version");
        serverBoardWidget.printWidget();
    }

    @Test
    void boardFullRotation() {
        WidgetTUI boardWidget;

        for (int i = 0; i < clientBoard.getSize(); i++) {
            boardWidget = clientBoard.generateWidget();
            boardWidget.printWidget();

            CardStateJSON state = new CardStateJSON();
            state.setNeedsBoardUpdate(true);
            state.setNeedsUpdatedPositions(true);
            state.setUpdatedPositions(Map.of("Player 1", i, "Player 2", i+1, "Player 3", i+2, "Player 4", i+3));

            serverBoard.validatePlayersPosition();
            serverBoard.movePlayerForward(serverBoard.getPlayers().getFirst(), 1);
            serverBoard.validatePlayersPosition();

            clientModel.getClientBoard().updateBoard(state, new TUIHandler(null, null)); // Null just in testing, not in production
        }
    }
}