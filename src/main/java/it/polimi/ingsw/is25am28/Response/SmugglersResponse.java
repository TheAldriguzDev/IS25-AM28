package it.polimi.ingsw.is25am28.Response;

public final class SmugglersResponse extends Response {
    private final boolean takeLoot;

    public SmugglersResponse(boolean takeLoot) {
        this.takeLoot = takeLoot;
    }

    public boolean getTakeLoot() {
        return takeLoot;
    }
}
