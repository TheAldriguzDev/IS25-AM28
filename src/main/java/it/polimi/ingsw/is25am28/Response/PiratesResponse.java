package it.polimi.ingsw.is25am28.Response;

import java.util.ArrayList;

public final class PiratesResponse extends Response {
    private final boolean takeCredits;
    private final ArrayList<Integer> dicesResults = new ArrayList<>();
    private final boolean shieldAbove;
    private final boolean shieldBelow;
    private final boolean shieldRight;
    private final boolean shieldLeft;

    public PiratesResponse(boolean takeCredits, int firstDice, int secondDice, boolean shieldAbove, boolean shieldBelow, boolean shieldRight, boolean shieldLeft) {
        this.takeCredits = takeCredits;
        this.shieldAbove = shieldAbove;
        this.shieldBelow = shieldBelow;
        this.shieldRight = shieldRight;
        this.shieldLeft = shieldLeft;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public boolean getShieldAbove() {
        return shieldAbove;
    }

    public boolean getShieldBelow() {
        return shieldBelow;
    }

    public boolean getShieldRight() {
        return shieldRight;
    }

    public boolean getShieldLeft() {
        return shieldLeft;
    }

    public ArrayList<Integer> getDicesResults() {
        return dicesResults;
    }
}