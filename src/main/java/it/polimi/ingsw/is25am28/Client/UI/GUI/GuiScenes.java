package it.polimi.ingsw.is25am28.Client.UI.GUI;

public enum GuiScenes {
    LOGIN_SCENE("login.fxml"),
    LOBBY_SCENE("lobby.fxml"),
    CREATE_GAME_SCENE("createGame.fxml"),
    JOIN_GAME_SCENE("joinGame.fxml"),
    RECONNECT_GAME_SCENE("reconnectGame.fxml"),
    WAITING_FOR_PLAYERS_SCENE("waitingForPlayers.fxml"),
    INSUFFICIENT_PLAYER_SCENE("insufficientPlayer.fxml"),
    END_GAME_SCENE("endGame.fxml"),
    FIX_SHIP_SCENE("fixShip.fxml"),
    POPULATE_SHIP_SCENE("populateShip.fxml"),
    CARD_ROUND_SCENE("cardRound.fxml");

    private final String fxmlFile;

    GuiScenes(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return "/GUI/FXML/" + fxmlFile;
    }

    @Override
    public String toString() {
        return this.name();
    }
}