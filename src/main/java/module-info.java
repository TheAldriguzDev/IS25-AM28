module it.polimi.ingsw.is25am28 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires json.simple;

    opens it.polimi.ingsw.is25am28 to javafx.fxml;
    exports it.polimi.ingsw.is25am28;
    exports it.polimi.ingsw.is25am28.Board;
    exports it.polimi.ingsw.is25am28.Components;
    exports it.polimi.ingsw.is25am28.Player;
    exports it.polimi.ingsw.is25am28.Ship;
    exports it.polimi.ingsw.is25am28.Exceptions;
    exports it.polimi.ingsw.is25am28.Lifeform;
    exports it.polimi.ingsw.is25am28.EventCards;
    exports it.polimi.ingsw.is25am28.GameModel;
    exports it.polimi.ingsw.is25am28.Items;
    exports it.polimi.ingsw.is25am28.TimeObserver;
    exports it.polimi.ingsw.is25am28.ActionJSON;
    exports it.polimi.ingsw.is25am28.ResourceBank;
    opens it.polimi.ingsw.is25am28.Exceptions to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Ship to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Player to javafx.fxml;
    opens it.polimi.ingsw.is25am28.TimeObserver to javafx.fxml;
    opens it.polimi.ingsw.is25am28.GameModel to javafx.fxml;
    exports it.polimi.ingsw.is25am28.GameModel.Session;
    opens it.polimi.ingsw.is25am28.GameModel.Session to javafx.fxml;
}