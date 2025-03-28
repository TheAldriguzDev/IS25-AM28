package it.polimi.ingsw.is25am28.EventCards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Exceptions.CoreDeletionAttemptException;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.Optional;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final ArrayList<ArrayList<Integer>> shootingSequence;

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, ArrayList<ArrayList<Integer>> shootingSequence, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.shootingSequence = shootingSequence;
    }

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        PiratesJSON piratesData;
        try {
            piratesData = (PiratesJSON) data;
        } catch (ClassCastException e) {
            throw new ClassCastException("Card data type in invalid");
        }
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresentOrElse(
                (Player player) -> {

                    String playerNickname = piratesData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }

                    if (player.getShip().getFirePower(piratesData.getNumberOfDoubleCannonsActivated()) >= requiredFirepower) {
                        // Pirates defeated, even if the player who defeated them does not take the credits, the card won't be used by other players
                        cardUsed();
                        if (piratesData.getTakeCredits()) {
                            bonusEffect();
                            getBoard().movePlayerBackwards(player, movementSteps);
                            getBoard().validatePlayersPosition();
                        }
                    } else {
                        malusEffect(piratesData);
                    }
                    if (player.equals(this.players.getLast())) {
                        this.cardUsed(); // Mark the card as used
                        this.getBoard().validatePlayersPosition();
                    } else {
                        this.getNextPlayer();
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        return this;
    }

    protected void bonusEffect() {
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    player.setCredits(player.getCredits() + this.givenCredits);
                }
        );
    }

    protected void malusEffect(ActionJSON data) throws ClassCastException {
        Optional<Player> playerOptional = getCurrentPlayer();
        PiratesJSON piratesData = (PiratesJSON) data;
        playerOptional.ifPresent(
                (Player player) -> {
                    int dicesResult = 0;
                    ArrayList<Integer> dicesResults = piratesData.getDicesResults(); // La parte client si assicura di fare tiri pari al numero di PlasmaShots
                    //Conversione da JSONArray di JSONArray a Lista di Liste

                    Boolean[] shieldedSides = new Boolean[] {false, false, false, false};

                    player.getShip().consumeEnergy(piratesData.getShieldsActivatedCoordinates().size()); // Il controllo sulle batterie disponibili è fatto dal client

                    for (ArrayList<Integer> pair : shootingSequence) {
                        int shotSize = (int) pair.getFirst(); // 1 -> small, 2 -> big
                        int shotDirection = (int) pair.getLast(); // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left

                        // Prima di ogni colpo resetta shieldedSides per poi ricalcolarli
                        for (int i = 0; i < 2; i++) {
                            shieldedSides[i] = false;
                        }
                        // Funzione prima di ogni colpo imposta i lati protetti
                        for (int[] coordinates : piratesData.getShieldsActivatedCoordinates()) {
                            try {
                                Shield shield = (Shield) player.getShip().getComponent(coordinates[0], coordinates[1]);
                                switch (shield.getCoveredSide()[0]) {
                                    case 0:
                                        shieldedSides[0] = true;
                                        shieldedSides[1] = true;
                                        break;
                                    case 1:
                                        shieldedSides[1] = true;
                                        shieldedSides[2] = true;
                                        break;
                                    case 2:
                                        shieldedSides[2] = true;
                                        shieldedSides[3] = true;
                                        break;
                                    case 3:
                                        shieldedSides[3] = true;
                                        shieldedSides[0] = true;
                                }
                            } catch (NullPointerException e) {
                                //Non attiva lo scudo in quanto è stato distrutto
                            }
                        }
                        if ((shotSize == 1 && !shieldedSides[shotDirection]) || shotSize == 2) {
                            switch (shotDirection) {
                                case 0: {
                                    int column = dicesResults.get(dicesResult);
                                    for (int row = 4; row < 9; row++) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                                // System.out.println("Eliminated " + player.getNickname());
                                            }
                                            //System.out.println("(U)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 1: {
                                    int row = dicesResults.get(dicesResult);
                                    for (int column = 3; column < 10; column++) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            }
                                            //System.out.println("(R)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    int column = dicesResults.get(dicesResult);
                                    for (int row = 8; row > 3; row--) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            }
                                            //System.out.println("(D)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                break;
                                }
                                case 3: {
                                    int row = dicesResults.get(dicesResult);
                                    for (int column = 9; column > 2; column--) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            try {
                                                player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            } catch (CoreDeletionAttemptException e) {
                                                getBoard().eliminatePlayer(player); // Core destroyed, player eliminated
                                            }
                                            //System.out.println("(L)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        dicesResult++;
                    }
                }
        );
    }

    protected void malusEffect() {}

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON piratesStateJSON;
        if(playerOptional.isPresent()) {
            piratesStateJSON = new CardStateJSON(
                    playerOptional.get().getNickname(),
                    getCardName(),
                    getCardLevel(),
                    !hasFinished(),
                    this.requiredFirepower,
                    this.givenCredits,
                    this.movementSteps,
                    this.shootingSequence);
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }
        return piratesStateJSON;
    }
}
