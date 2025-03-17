package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

public class Pirates extends EventCard {
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    private final int numberOfShots;
    private boolean hasBeenDefeated;
    /*
    * Liste che i PlasmaShot che arrivano dalle 4 direzioni:
    * 0 -> piccolo,
    * 1 -> grande
    * */
    ArrayList<Integer> shootingSequenceFromAbove = new ArrayList<>();
    ArrayList<Integer> shootingSequenceFromBelow = new ArrayList<>();
    ArrayList<Integer> shootingSequenceFromRight = new ArrayList<>();
    ArrayList<Integer> shootingSequenceFromLeft = new ArrayList<>();

    public Pirates(String name, int cardLevel, int requiredFirepower, int givenCredits, int movementSteps, int numberOfShots, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.givenCredits = givenCredits;
        this.movementSteps = movementSteps;
        this.numberOfShots = numberOfShots;
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

                    if (player.getShip().getFirePower() >= requiredFirepower) {
                        this.hasBeenDefeated = true;
                        if (piratesData.getTakeCredits()) {
                            bonusEffect();
                            player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect();
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
                    //Component component;
                    ArrayList<Integer> dicesResults = piratesData.getDicesResults(); // La parte client si assicura di fare tiri pari al numero di PlasmaShots
                    // side // 0->Above, 1->Below, 2->Right, 3->Left
                    // Coordinates
                    for (int plasmaShot : shootingSequenceFromAbove) {
                        if ((plasmaShot == 0 && !piratesData.getShieldAbove()) || plasmaShot == 1) {
                            int column = dicesResults.get(dicesResult);
                            for (int row = 4; row < 9; row++) {
                                try {
                                    player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                                    player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                } catch (NullComponentException e) {
                                    // Nella casella non c'è un componente, non va fatto nulla
                                }
                            }
                        }
                        dicesResult++;
                    }

                    for (int plasmaShot : shootingSequenceFromBelow) {
                        if ((plasmaShot == 0 && !piratesData.getShieldBelow()) || plasmaShot == 1) {
                            int column = dicesResults.get(dicesResult);
                            for (int row = 8; row > 3; row--) {
                                try {
                                    player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                                    player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                } catch (NullComponentException e) {
                                    // Nella casella non c'è un componente, non va fatto nulla
                                }
                            }
                        }
                        dicesResult++;
                    }

                    for (int plasmaShot : shootingSequenceFromRight) {
                        if ((plasmaShot == 0 && !piratesData.getShieldRight()) || plasmaShot == 1) {
                            int row = dicesResults.get(dicesResult);
                            for (int column = 3; column < 10; column++) {
                                try {
                                    player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                                    player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                } catch (NullComponentException e) {
                                    // Nella casella non c'è un componente, non va fatto nulla
                                }
                            }
                        }
                        dicesResult++;
                    }

                    for (int plasmaShot : shootingSequenceFromLeft) {
                        if ((plasmaShot == 0 && !piratesData.getShieldLeft()) || plasmaShot == 1) {
                            int row = dicesResults.get(dicesResult);
                            for (int column = 9; column > 2; column--) {
                                try {
                                    player.getShip().getComponent(row, column); // Innesca la exception se non c'è niente
                                    player.getShip().removeComponent(row, column); // Eseguito solo se c'è un componenete
                                } catch (NullComponentException e) {
                                    // Nella casella non c'è un componente, non va fatto nulla
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
    public boolean hasFinished() {
        return currentPlayer.map(player -> player.equals(players.getLast())).orElse(false) || this.hasBeenDefeated;
    }

    @Override @SuppressWarnings("unchecked")
    public JSONObject generateState() {
        JSONObject piratesState = new JSONObject();

        if (this.getCurrentPlayer().isPresent()) {
            piratesState.put("playerNickname", getCurrentPlayer().get().getNickname());
        }
        piratesState.put("cardName", this.name);
        piratesState.put("cardLevel", cardLevel);
        piratesState.put("requiredFirepower", requiredFirepower);
        piratesState.put("givenCredits", givenCredits);
        piratesState.put("movementSteps", movementSteps);
        piratesState.put("numberOfShots", numberOfShots);
        piratesState.put("hasBeenDefeated", hasBeenDefeated);

        return piratesState;
    }
}
