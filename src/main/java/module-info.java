module it.polimi.ingsw.is25am {
    // Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.core;

    // JavaFX
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    // Altre dipendenze
    requires org.controlsfx.controls;
    requires json.simple;
    requires java.smartcardio;
    requires java.desktop;
    requires java.rmi;
    requires java.management;
    requires java.sql;

    // Export e Open principali
    opens it.polimi.ingsw.is25am28 to javafx.fxml;

    // Jackson opens
    opens it.polimi.ingsw.is25am28.Model.ActionJSON to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Model.ActionJSON.State to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Network.Messages to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Loader.Cards to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Loader.Tiles to com.fasterxml.jackson.databind;

    // JavaFX opens
    opens it.polimi.ingsw.is25am28.Model to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Model.Exceptions to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Model.Ship to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Model.Player to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Network to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Network.Server to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client.UI to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client.ClientModel to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Loader.FastShipTiles to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Loader to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client.UI.GUI to javafx.fxml;

    // Export for RMI
    exports it.polimi.ingsw.is25am28.Network to java.rmi;
    exports it.polimi.ingsw.is25am28.Client to java.rmi;
    exports it.polimi.ingsw.is25am28.Network.Server to java.rmi;
    exports it.polimi.ingsw.is25am28.Client.UI to java.rmi;
    exports it.polimi.ingsw.is25am28.Client.ClientModel to java.rmi;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent to java.rmi;

    // Other generics exports
    exports it.polimi.ingsw.is25am28.Model;
    exports it.polimi.ingsw.is25am28.Model.Board;
    exports it.polimi.ingsw.is25am28.Model.Components;
    exports it.polimi.ingsw.is25am28.Model.Player;
    exports it.polimi.ingsw.is25am28.Model.Ship;
    exports it.polimi.ingsw.is25am28.Model.Exceptions;
    exports it.polimi.ingsw.is25am28.Model.Lifeform;
    exports it.polimi.ingsw.is25am28.Model.EventCards;
    exports it.polimi.ingsw.is25am28.Model.Items;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON;
    exports it.polimi.ingsw.is25am28.Model.ResourceBank;
    exports it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON.State;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;
    exports it.polimi.ingsw.is25am28.Network.RMI.Client;
    exports it.polimi.ingsw.is25am28.Network.RMI.Server;
    exports it.polimi.ingsw.is25am28.Controller;
    exports it.polimi.ingsw.is25am28.Network.Messages;
    exports it.polimi.ingsw.is25am28.Network.Answer;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer;
    exports it.polimi.ingsw.is25am28.Utils.Pair;
    exports it.polimi.ingsw.is25am28.Utils.CoordinatePair;
    exports it.polimi.ingsw.is25am28.Loader.Cards;
    exports it.polimi.ingsw.is25am28.Loader.Tiles;
    exports it.polimi.ingsw.is25am28.Client.UI.GUI;
    exports it.polimi.ingsw.is25am28.Network.Queue;
    exports it.polimi.ingsw.is25am28.Loader.FastShipTiles;
    exports it.polimi.ingsw.is25am28.Loader;
    exports it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientBoard;
}
