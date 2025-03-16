package it.polimi.ingsw.is25am28.Response;

import it.polimi.ingsw.is25am28.Components.Cabin;

import java.util.ArrayList;
import java.util.List;

public final class SlaversResponse extends Response{
    private final boolean takeCredits;
    private final ArrayList<Cabin> crewToRemove = new ArrayList<>();

    public SlaversResponse(boolean takeCredits) {
        this.takeCredits = takeCredits;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public List<Cabin> getCrewToRemove() {
        return crewToRemove;
    }
}
