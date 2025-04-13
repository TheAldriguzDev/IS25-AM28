package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.EndGameDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.*;

public final class EndGameState extends State {
    private final Map<String, Integer> playersResult;
    private final Map<String, Integer> playersPositionResult;
    private final int levelMultiplier;

    public EndGameState(GameModel model) {
        super(model);
        this.playersResult = new HashMap<>();
        this.playersPositionResult = new HashMap<>();

        if (this.model.getGameLevel() == 0) {
            this.levelMultiplier = 1;
        } else {
            this.levelMultiplier = this.model.getGameLevel();
        }

        this.makeGameResult();
    }

    /**
     * This method compute the result of the game the given credits are related to:
     * 1. Alive players: credits based on their arrival position
     * 2. Alive players: credits based on the least exposed connectors
     * 3. Credits given for the amount of resources, for eliminated players this value is /2
     * 4. Credits removed for the amount of lost components
     * */
    private void makeGameResult() {
        Board board = model.getBoard();
        List<Player> players = this.model.getPlayers().values().stream().toList();
        board.validatePlayersPosition();

        List<Player> alivePlayers = this.model.getBoard().getPlayers();
        List<Player> eliminatedPlayers = this.model.getBoard().getEliminatedPlayers();

        // Credits rewards based on the position
        for (int i = 0; i < alivePlayers.size(); i++) {
            alivePlayers.get(i).addCredits( 4 * levelMultiplier - i * levelMultiplier );
        }

        // Credits reward for the best ship (given to all the players with the min amount of exposed connectors)
        if (!alivePlayers.isEmpty()) {
            int minConnExposed = alivePlayers
                    .stream()
                    .mapToInt(p -> p.getShip().getExposedConnectorAmount())
                    .min().orElse(0);

            alivePlayers.stream()
                    .filter(p -> p.getShip().getExposedConnectorAmount() == minConnExposed)
                    .forEach( p -> p.addCredits(2 * levelMultiplier));
        }

        // Add credits for resources, and remove the credits for eventual lost components
        // Alive players
        for (Player p : alivePlayers) {
            p.addCredits(p.getShip().getAllItemValue());
            p.addCredits(-1 * p.getLostPieces());


        }

        // Eliminated players
        for (Player p : eliminatedPlayers) {
            p.addCredits(p.getShip().getAllItemValue() / 2);
            p.addCredits(-1 * p.getLostPieces());
        }

        // Create the data needed to send the state
        for (Player p : players) {
            this.playersResult.put(p.getNickname(), p.getCredits());
            if (!p.isEliminated()) {
                this.playersPositionResult.put(p.getNickname(), board.getPlayers().indexOf(p) + 1);
            }
        }
    }

    @Override
    public StateDTO generateState() {
        return new EndGameDTO()
                .setPlayersCredits(this.playersResult)
                .setPlayersPositionResult(this.playersPositionResult);
    }

    @Override
    public void onComplete() {
        // TODO: Generate the state that is needed to notify the client about who won the game and the general results
    }
}
