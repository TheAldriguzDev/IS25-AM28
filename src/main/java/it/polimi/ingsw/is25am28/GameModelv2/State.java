package it.polimi.ingsw.is25am28.GameModelv2;

import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Player.PlayerColor;

public abstract class State {
    protected GameModel model;

    public State(GameModel model) {
        this.model = model;
    }

    public void gameConfig(String playerNickname, PlayerColor playerColor, int level, int numPlayers) throws IllegalStateException {
        throw new IllegalStateException("The 'gameConfig' command is not allowed in the " + this + " state");
    }

    public void addNewPlayer(String playerNickname, PlayerColor playerColor) throws IllegalStateException {
        throw new IllegalStateException("The 'addNewPlayer' command is not allowed in the " + this + " state");
    }

    public abstract void onComplete();


    // State generation
    public StateJSON generateState() {
        StateJSON state = new StateJSON();

        state.setStateName(this.toString());

        return state;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
