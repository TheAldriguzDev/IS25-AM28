package it.polimi.ingsw.is25am28.Client.UI.GUI;

public enum GuiScenes {
    LOGIN_SCENE("login.fxml"),
    LOBBY_SCENE("lobby.fxml"),
    CREATE_GAME_SCENE("createGame.fxml"),
    JOIN_GAME_SCENE("joinGame.fxml"),
    RECONNECT_GAME_SCENE("reconnectGame.fxml"),
    WAITING_FOR_PLAYERS_SCENE("waitingForPlayers.fxml"),
    INSUFFICIENT_PLAYER_SCENE("insufficientPlayer.fxml");

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