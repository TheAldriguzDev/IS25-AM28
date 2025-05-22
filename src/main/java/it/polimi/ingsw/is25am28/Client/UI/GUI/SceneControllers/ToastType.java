package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

public enum ToastType {
    ERROR,
    INFO,
    SUCCESS,
    WARNING;

    @Override
    public String toString() {
        return switch (this) {
            case ERROR -> "error";
            case INFO -> "info";
            case SUCCESS -> "success";
            case WARNING -> "warning";
        };
    }
}
