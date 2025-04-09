package it.polimi.ingsw.is25am28.GameModelv2;


import it.polimi.ingsw.is25am28.ActionJSON.State.CreateGameStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Player.PlayerColor;

import java.util.ArrayList;

public class CreateGameState extends State {

    public CreateGameState(GameModel model) {
        super(model);
    }

    @Override
    public void gameConfig(
            String playerNickname,
            PlayerColor playerColor,
            int level,
            int numPlayers ) throws IllegalStateException {
        model.setGameLevel(level);
        model.setGamePlayersNumber(numPlayers);
        model.addPlayer(playerNickname, playerColor);
    }

    @Override
    public void onComplete() {
        model.setCurrentState(new WaitPlayersState(model));
    }

    @Override
    public StateJSON generateState() {
        CreateGameStateJSON state = new CreateGameStateJSON();

        state.setStateName(this.toString());
        state.setAvailableColors(model.getAvailableColors());
        state.setUsedNicknames(new ArrayList<String>());

        return state;
    }
}
