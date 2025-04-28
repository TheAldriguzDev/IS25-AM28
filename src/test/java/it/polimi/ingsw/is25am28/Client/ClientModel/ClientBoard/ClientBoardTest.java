package it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard;

import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ClientBoardTest {
    Board serverBoard;
    BoardJSON boardJSON;
    ClientBoard clientBoard;

    @BeforeEach
    void init() {
        serverBoard = new BoardLevel2();

        boardJSON = serverBoard.generateState();

        clientBoard = new ClientBoard(boardJSON);
    }

    @Test
    public void printClientBoardTest () {
        WidgetTUI clientBoardWidget;
        clientBoardWidget = clientBoard.generateWidget();

        clientBoardWidget.printWidget();
    }
}