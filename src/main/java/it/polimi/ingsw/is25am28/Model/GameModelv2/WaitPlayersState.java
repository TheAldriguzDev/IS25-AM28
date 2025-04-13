package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

public final class WaitPlayersState extends State {
    public WaitPlayersState(GameModel model) {
        super(model);
    }

    @Override
    public void addNewPlayer(String playerNickname, PlayerColor playerColor) throws IllegalStateException, IllegalArgumentException {
        model.addPlayer(playerNickname, playerColor);
    }

    @Override
    public void onComplete() {
        if (model.getPlayers().size() == model.getNumPlayers()) {
            model.setCurrentState(new ShipContructionState(model));
        }
    }

    @Override
    public StateDTO generateState() {
        WaitPlayersStateDTO state = new WaitPlayersStateDTO();

        // Set the information needed for the state
        // 1. StateName
        // 2. Available colors left
        // 3. List of already used nicknames
        // 4. Number of players that are required to start the game
        // 5. Number of players that can join the game (Required - Actual)
        state.setStateName(this.toString());
        state.setAvailableColors(model.getAvailableColors());
        state.setUsedNicknames(model.getPlayers().keySet().stream().toList());
        state.setLobbyTotalSpot(model.getNumPlayers());
        state.setAvailableSpots(model.getNumPlayers() - model.getPlayers().size());

        return state;
    }

}
