package it.polimi.ingsw.is25am28.Model.GameModelv2;


import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CreateGameStateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.ArrayList;

public final class CreateGameState extends State {

    public CreateGameState(GameModel model) {
        super(model);
    }

    @Override
    public void gameConfig(
            String playerNickname,
            PlayerColor playerColor,
            int level,
            int numPlayers ) throws IllegalStateException, IllegalArgumentException {

        if (level != 0 && level != 2) {
            throw new IllegalArgumentException("The given level is not supported: " + level);
        }
        if (numPlayers < 2 || numPlayers > 4) {
            throw new IllegalArgumentException("The given totalPlayer is not valid: " + numPlayers);
        }

        model.setGameLevel(level);
        model.setGamePlayersNumber(numPlayers);
        model.addPlayer(playerNickname, playerColor);
    }

    @Override
    public void onComplete() {
        model.setCurrentState(new WaitPlayersState(model));
    }

    @Override
    public StateDTO generateState() {
        CreateGameStateDTO state = new CreateGameStateDTO();

        state.setStateName(this.toString());
        state.setAvailableColors(model.getAvailableColors());
        state.setUsedNicknames(new ArrayList<String>());

        return state;
    }
}
