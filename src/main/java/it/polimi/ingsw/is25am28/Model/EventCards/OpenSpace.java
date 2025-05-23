package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Components.Engine;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

public class OpenSpace extends EventCard {
    private final Map<String, Integer> playerPowerResult;
    private final Map<String, Integer> updatedPositions;
    private final List<String> eliminatedPlayers;
    private final Map<String, List<CoordinatePair>> removedBatteries;
    private String prevPlayerNickname;

    public OpenSpace(String name, int level, Board board, int uniqueCardId, String path) {
        super(name, level, board, uniqueCardId, path);

        this.playerPowerResult = new HashMap<>();
        this.updatedPositions = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.removedBatteries = new HashMap<>();
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        List<Pair<CoordinatePair, CoordinatePair>> doubleEnginesToActivate;
        OpenSpaceJSON openSpace;
        String playerNickname;
        Ship ship;
        int totalEnginePower;

        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        // Retrieve the data from the JSON
        try {
            openSpace = (OpenSpaceJSON) data;
            playerNickname = openSpace.getPlayerNickname();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid OpenSpace JSON");
        }

        this.prevPlayerNickname = playerNickname;

        doubleEnginesToActivate = openSpace.getDoubleEnginesToActivate();

        if (playerNickname != null && !playerNickname.isEmpty()) {
            if (this.getCurrentPlayer().isPresent()) {
                if (playerNickname.equals(this.getCurrentPlayer().get().getNickname())) {
                    ship = this.getCurrentPlayer().get().getShip();

                    // Filtering out all coordinates that don't point to a double engine.
                    doubleEnginesToActivate = doubleEnginesToActivate.stream()
                            .filter(Objects::nonNull)
                            .filter(
                                (pair) -> {
                                    CoordinatePair engineCoords = pair.getKey();
                                    Component component = ship.getComponent(engineCoords.getI(), engineCoords.getJ());

                                    return switch (component) {
                                        case Engine engine -> (engine.requiresEnergy());
                                        case null, default -> false;
                                    };
                                }
                            ).toList();

                    List<Pair<CoordinatePair, CoordinatePair>> activatedDoubleEngines
                            = ship.activateComponents(doubleEnginesToActivate);

                    totalEnginePower = ship.getEnginePower(
                            activatedDoubleEngines.stream()
                                    .map(Pair::getKey).toList()
                    );

                    this.removedBatteries.put(
                        playerNickname,
                        activatedDoubleEngines.stream()
                                .map(Pair::getValue).toList()
                    );

                    // Store the power result to notify the player with the choices of the previous players
                    this.playerPowerResult.put(playerNickname, totalEnginePower);

                    // Apply the effect to the player
                    // if no power has been declared eliminate the player
                    // otherwise move the player forward of the declared power
                    if (totalEnginePower == 0) {
                        this.getBoard().eliminatePlayer(this.getCurrentPlayer().get());
                        this.eliminatedPlayers.add(playerNickname);
                    }
                    else {
                        this.getBoard().movePlayerForward(this.getCurrentPlayer().get(), totalEnginePower);
                        this.updatedPositions.put(playerNickname, this.getCurrentPlayer().get().getCursor());
                    }

                    // When we have moved the last player we need to re-validate the positions
                    if (this.getCurrentPlayer().get().equals(this.players.getLast())) {
                        this.cardUsed();
                        int tmp = getBoard().getEliminatedPlayers().size();
                        this.getBoard().validatePlayersPosition();

                        for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // TODO: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                            this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
                        }
                    }
                    else {
                        this.getNextPlayer();
                    }
                }
                else {
                    throw new IllegalArgumentException("ERROR: The given player doesn't match with the current one.");
                }
            }
        }
        else {
            throw new IllegalArgumentException("ERROR: The given player is either null or empty.");
        }

        return this;
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON cardState = new CardStateJSON();
        cardState.setUniqueCardId(this.uniqueCardId);

        if (hasBeenActivated()) {
            initStateFlags(cardState);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));
            cardState.setPrevPlayerNickname(this.prevPlayerNickname);
            setUpdatedEliminatedPlayersIfNecessary(cardState, this.eliminatedPlayers);
            setUpdatedPositionsIfNecessary(cardState, this.updatedPositions);
            setUpdatedRemovedBatteriesIfNecessary(cardState, this.removedBatteries);
        }
        else {
            cardState.setCardTypeId(this.cardTypeId);
            cardState.setCardName(this.getCardName());
            cardState.setImagePath(this.path);
            cardState.setCardLevel(this.cardLevel);
        }

        cardState.setCardEnded(this.hasFinished());

        return cardState;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardTypeId(this.cardTypeId);
        cardState.setUniqueCardId(this.uniqueCardId);
        cardState.setCardName(this.getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(this.cardLevel);
        cardState.setImagePath(this.path);

        return cardState;
    }
}
