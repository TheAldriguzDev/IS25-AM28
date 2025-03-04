module it.polimi.ingsw.is25am28 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.polimi.ingsw.is25am28 to javafx.fxml;
    exports it.polimi.ingsw.is25am28;
}