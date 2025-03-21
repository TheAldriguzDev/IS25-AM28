package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.Exceptions.InsufficientEnergyException;
import it.polimi.ingsw.is25am28.Exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final JSONArray shootingSequence;
    private boolean hasBeenDefeated;

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, JSONArray shootingSequence, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.shootingSequence = shootingSequence;
        this.hasBeenDefeated = false;
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
                        this.hasBeenDefeated = true;
                        if (piratesData.getTakeCredits()) {
                            bonusEffect();
                            getBoard().movePlayerBackwards(player, movementSteps);
                            //player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect(piratesData);
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        getNextPlayer();
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
                    ArrayList<ArrayList<Integer>> shots = new ArrayList<>();




                    Boolean[] shieldedSides = new Boolean[] {false, false, false, false};

                    List<Shield> shields;

                    player.getShip().consumeEnergy(piratesData.getShieldsActivatedCoordinates().size()); // Il controllo sulle batterie disponibili è fatto dal client


                    // Funzione che consuma le batterie un'unica volta all'inizio
//                    for (Shield shield : piratesData.getShieldsActivated()) {
//                        try {
//                            player.getShip().consumeEnergy(1);
//                        } catch (InsufficientEnergyException e) {
//                            // Non viene attivato
//                        }
//                    }


                    for (Object pairObject : shootingSequence) {
                        JSONArray pair = (JSONArray) pairObject;
                        int shotSize = (int) pair.getFirst(); // 1 -> small, 2 -> big
                        int shotDirection = (int) pair.getLast(); // 0 -> up, 1 -> right, 2 -> bottom, 3 -> left

                        shields = player.getShip().getShieldList();
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
                                            //System.out.println("(U)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            break;
                                        }
                                    }
                                }
                                break;
                                case 1: {
                                    int row = dicesResults.get(dicesResult);
                                    for (int column = 3; column < 10; column++) {
                                        if (player.getShip().getComponent(row, column) != null) { // Innesca la exception se non c'è niente
                                            //System.out.println("(R)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    int column = dicesResults.get(dicesResult);
                                    for (int row = 8; row > 3; row--) {
                                        if (player.getShip().getComponent(row, column) != null) {
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
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
                                            //System.out.println("(L)Removed component:" + player.getShip().getComponent(row, column) + ", in i: " + row + ", column: " + column);
                                            player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
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

//    @Override
//    public boolean hasFinished() {
//        return currentPlayer.map(player -> player.equals(players.getLast())).orElse(false) || this.hasBeenDefeated;
//    }

    @Override @SuppressWarnings("unchecked")
    public CardStateJSON generateState() {
        JSONObject piratesState = new JSONObject();

        if (this.getCurrentPlayer().isPresent()) {
            piratesState.put("playerNickname", getCurrentPlayer().get().getNickname());
        }
        piratesState.put("cardName", this.name);
        piratesState.put("cardLevel", cardLevel);
        piratesState.put("requiredFirepower", requiredFirepower);
        piratesState.put("givenCredits", givenCredits);
        piratesState.put("movementSteps", movementSteps); // Il client calcola la size per il lancio dei dadi, ne ha anche bisigno per vedere da dove arrivano/dimensione
        piratesState.put("shootingSequence", shootingSequence);
        piratesState.put("hasBeenDefeated", hasBeenDefeated);

        //return piratesState;
        return null;
    }
}
