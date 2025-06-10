module it.polimi.ingsw.is25am {
    requires com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Model.ActionJSON to com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.core;

    exports it.polimi.ingsw.is25am28.Network to java.rmi;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires json.simple;
    requires java.smartcardio;
    requires java.desktop;
    requires junit;
    requires java.rmi;
    requires java.management;
    requires java.sql;

    opens it.polimi.ingsw.is25am28 to javafx.fxml;
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
    opens it.polimi.ingsw.is25am28.Model.Exceptions to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Model.Ship to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Model.Player to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON.State;
    opens it.polimi.ingsw.is25am28.Model.ActionJSON.State to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;
    opens it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.is25am28.Model to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Network.RMI.Client;
    exports it.polimi.ingsw.is25am28.Network.RMI.Server;
    exports it.polimi.ingsw.is25am28.Client to java.rmi;
    opens it.polimi.ingsw.is25am28.Client to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Network to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Controller;
    exports it.polimi.ingsw.is25am28.Network.Messages;
    opens it.polimi.ingsw.is25am28.Network.Messages to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.is25am28.Network.Server to java.rmi;
    opens it.polimi.ingsw.is25am28.Network.Server to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Client.UI to java.rmi;
    opens it.polimi.ingsw.is25am28.Client.UI to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Network.Answer;
    exports it.polimi.ingsw.is25am28.Client.ClientModel to java.rmi;
    opens it.polimi.ingsw.is25am28.Client.ClientModel to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent to java.rmi;
    opens it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip;
    exports it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer;
    exports it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer;
    exports it.polimi.ingsw.is25am28.Utils.Pair;
    exports it.polimi.ingsw.is25am28.Utils.CoordinatePair;

    exports it.polimi.ingsw.is25am28.Loader.Cards to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.is25am28.Loader.Tiles to com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.is25am28.Client.UI.GUI;

    opens it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers to javafx.fxml;
    opens it.polimi.ingsw.is25am28.Client.UI.GUI to javafx.fxml;

    exports it.polimi.ingsw.is25am28.Network.Queue;
    exports it.polimi.ingsw.is25am28.Loader.FastShipTiles;
    opens it.polimi.ingsw.is25am28.Loader.FastShipTiles to javafx.fxml;
    exports it.polimi.ingsw.is25am28.Loader;
    opens it.polimi.ingsw.is25am28.Loader to javafx.fxml;
}