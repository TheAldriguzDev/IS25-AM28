module it.polimi.ingsw.is25am28 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens it.polimi.ingsw.is25am28 to javafx.fxml;
    exports it.polimi.ingsw.is25am28;
    exports it.polimi.ingsw.is25am28.Exceptions;
    opens it.polimi.ingsw.is25am28.Exceptions to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Ship;
    opens it.polimi.ingsw.is25am28.Ship to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Player;
    opens it.polimi.ingsw.is25am28.Player to javafx.fxml;
    exports it.polimi.ingsw.is25am28.TimeObserver;
    opens it.polimi.ingsw.is25am28.TimeObserver to javafx.fxml;
    exports it.polimi.ingsw.is25am28.GameModel;
    opens it.polimi.ingsw.is25am28.GameModel to javafx.fxml;
}