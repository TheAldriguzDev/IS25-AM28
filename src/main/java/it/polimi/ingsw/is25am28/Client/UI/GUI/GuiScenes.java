package it.polimi.ingsw.is25am28.Client.UI.GUI;

public enum GuiScenes {
    LOGIN_SCENE("login.fxml"),
    LOBBY_SCENE("lobby.fxml"),;

    private final String fxmlFile;

    GuiScenes(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return fxmlFile;
    }

    @Override
    public String toString() {
        return switch (this) {
            case LOGIN_SCENE -> "LOGIN_SCENE";
            case LOBBY_SCENE -> "LOBBY_SCENE";
        };
    }
}