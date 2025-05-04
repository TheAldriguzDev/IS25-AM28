package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientCannon;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PiratesJSON;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientPirates extends ClientEventCard {
    private int diceThrowResult;
    private boolean firstRound;
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    Map<String, Integer> currentPlasmaShot;
    private List<String> defeatedPlayers;

    private PiratesJSON piratesJSON;

    public ClientPirates(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        super(model, inputThread, cardState);
        this.firstRound = true;
        this.requiredFirepower = cardState.getRequiredFirepower();
        this.givenCredits = cardState.getGivenCredits();
        this.movementSteps = cardState.getMovementSteps();
        this.defeatedPlayers = new ArrayList<>();
        this.piratesJSON = new PiratesJSON();
    }

    @Override
    public ActionJSON useCard() {
        this.piratesJSON.setPlayerNickname(this.playerNickname);
        PiratesJSON tmp = this.piratesJSON;
        this.piratesJSON = new PiratesJSON();
        return tmp;
    }

    @Override
    public void updateCard(CardStateJSON piratesState) {
        this.playerNickname = piratesState.getPlayerNickname();
        this.firstRound = piratesState.getFirstRound();
        if (!this.firstRound) {
            this.diceThrowResult = piratesState.getDiceThrowResult();
            this.currentPlasmaShot = piratesState.getCurrPlasmaShotDescriptor();
            this.defeatedPlayers = piratesState.getDefeatedPlayers();
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        if (this.firstRound) {

            cardInfoWidget.appendString(ANSIColors.WHITE + "████                       ████" + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "█████                     █████" + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "  ████     " + ANSIColors.RESET + "█████████" + ANSIColors.WHITE +"     ███   " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "    ███ " + ANSIColors.RESET + "███████████████" + ANSIColors.WHITE +" ███    " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "     █ " + ANSIColors.RESET + "█████████████████" + ANSIColors.WHITE + " █     " + ANSIColors.RESET);
            cardInfoWidget.appendString("      ███      █      ███      " + ANSIColors.RESET);
            cardInfoWidget.appendString("      ███   " + ANSIColors.RED + "█" + ANSIColors.RESET + "  █  " + ANSIColors.RED +"█" + ANSIColors.RESET + "   ███      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "     █ " + ANSIColors.RESET + "█████████████████" + ANSIColors.WHITE +" █    " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "    ███   " + ANSIColors.RESET + "███████████" + ANSIColors.WHITE +"   ███    " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "  ████   " + ANSIColors.RESET + "█ █ █ █ █ █ █" + ANSIColors.WHITE +"   ████  " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "█████     " + ANSIColors.RESET + "█ █ █ █ █ █" + ANSIColors.WHITE +"     █████" + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "████      " + ANSIColors.RESET + "███████████" + ANSIColors.WHITE +"      ████" + ANSIColors.RESET);
            cardInfoWidget.wrapWidgetWithBorder();

            cardInfoWidget.appendString("Level: " + this.cardLevel);
            cardInfoWidget.appendString("Given credits: " + this.givenCredits);
            cardInfoWidget.appendString("Days: " + this.movementSteps);
            // TODO : does the shootingSequence need to be shown to the clients as a whole?
            cardInfoWidget.appendString("Required Firepower: " + this.requiredFirepower);
            if (this.playerNickname != null) {
                cardInfoWidget.appendString("Current Player: " + this.playerNickname);
            }
        } else {
            cardInfoWidget.appendString("                 █                ");
            cardInfoWidget.appendString("                ███               ");
            cardInfoWidget.appendString("               █████              ");
            cardInfoWidget.appendString("               █████              ");
            cardInfoWidget.appendString("              ███████             ");
            cardInfoWidget.appendString("       █    ███████████    █      ");
            cardInfoWidget.appendString("       ██  █████████████  ██      ");
            cardInfoWidget.appendString("  ██    ███████████████████    ██ ");
            cardInfoWidget.appendString("  ███    █████████████████    ███ ");
            cardInfoWidget.appendString("   ████ ███████████████████ ████  ");
            cardInfoWidget.appendString("     █████████████████████████    ");
            cardInfoWidget.appendString("      ███████████████████████     ");
            cardInfoWidget.appendString("         █████████████████        ");
            cardInfoWidget.appendString("            ███████████           ");
            cardInfoWidget.wrapWidgetWithBorder();

            cardInfoWidget.appendString("==== CURRENT PLASMASHOT INFO ====");
            switch (this.currentPlasmaShot.get("shotDirection")) {
                case 0 -> cardInfoWidget.appendString("Inbound Direction: ABOVE");
                case 1 -> cardInfoWidget.appendString("Outbound Direction: RIGHT");
                case 2 -> cardInfoWidget.appendString("Outbound Direction: BELOW");
                case 3 -> cardInfoWidget.appendString("Inbound Direction: LEFT");
            }
            if (this.currentPlasmaShot.get("shotSize") == 1) {
                cardInfoWidget.appendString("Size: SMALL PLASMASHOT");
            } else {
                cardInfoWidget.appendString("Size: BIG PLASMASHOT");
            }
            cardInfoWidget.appendString("Dice Throw Result: " + this.diceThrowResult);
            cardInfoWidget.appendString("Target: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    public List<List<Integer>> inputDoubleCannonsToActivateCoordinates() {
        List<List<Integer>> doubleCannonsToActivate = new ArrayList<>();

        String input;
        int selection;
        int cannonsCount = 0;
        int row;
        int column;

        int totalAvailableEnergy = 0;

        ClientShip ship;

        ship = model.getShipOfPlayer(this.playerNickname).orElse(null);
        if (ship == null) { return new ArrayList<>(); } // If no ship is found we return an empty list, it might be better to throw an exception

        totalAvailableEnergy = ship.getAvailableEnergy();

        // Asks the player what to do about the selection of the double cannons to activate
        System.out.println("Select what double cannons you want to activate:\n\t1) Select a double cannon\n\t2) DeSelect previous double cannon\n\t3) End selection (" + cannonsCount + " double cannons selected)");

        do {
            try {
                input = inputThread.waitForInput();
                selection = Integer.parseInt(input);

                if (selection == 1) {

                } else if (selection == 2) {

                } else if (selection == 3) {
                    return doubleCannonsToActivate;
                }

            } catch (InterruptedException e) {
                return new ArrayList<>(); // Returns an empty list
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please select a number");
            }
        } while (true);
    }

//    @Override
//    public List<List<Integer>> inputDoubleCannonsToActivateCoordinates() {
//        List<List<Integer>> doubleCannonsToActivate = new ArrayList<>();
//
//        String input;
//        int numberOfCannons;
//        int row;
//        int column;
//
//        int totalAvailableEnergy = 0;
//
//        ClientShip ship;
//
//        ship = model.getShipOfPlayer(this.playerNickname).orElse(null);
//        if (ship == null) { return new ArrayList<>(); } // If no ship is found we return an empty list, it might be better to throw an exception
//
//        totalAvailableEnergy = ship.getAvailableEnergy();
//
//        // Asks the player how many cannons need to be activated
//        do {
//            try {
//                input = this.inputThread.waitForInput();
//                if (input == null) { return new ArrayList<>(); }
//                numberOfCannons = Integer.parseInt(input);
//                if (numberOfCannons >= 0) {
//                    if (numberOfCannons <= totalAvailableEnergy) {
//                        if (numberOfCannons <= ship.getDoubleCannons().size()) {
//                            break;
//                        } else {
//                            System.out.print("You don't have enough double cannons, insert another number: ");
//                        }
//                    } else {
//                        System.out.println("You don't have enough energy to activate all these cannons, insert another number: ");
//                    }
//                } else {
//                    System.out.print(PrintUtils.addColor("Invalid input, value must be positive: ", ANSIColors.RED));
//                }
//            } catch (InterruptedException e) {
//                return new ArrayList<>(); // Returns an empty list
//            } catch (NullPointerException e) {
//                System.out.print(PrintUtils.addColor("Invalid input, please insert a number: ", ANSIColors.RED));
//            }
//        } while (true);
//
//        int currentCannonCount = 0;
//
//        // Asks th eplayer the coordinated of the double cannons to activate
//        while (currentCannonCount < numberOfCannons) {
//            System.out.println("Insert the coordinates (row, column) of the double cannon #" + currentCannonCount + " to activate: ");
//
//            try {
//                System.out.print("Row: ");
//                input = this.inputThread.waitForInput();
//                if (input == null) { return new ArrayList<>(); }
//                row = Integer.parseInt(input);
//                System.out.print("Column: ");
//                input = this.inputThread.waitForInput();
//                if (input == null) { return new ArrayList<>(); }
//                column = Integer.parseInt(input);
//
//                try {
//                    ClientComponent component = ship.getComponent(row, column);
//                    if (component.getClass() == ClientCannon.class) {
//                        if (((ClientCannon) component).getFirePower() == 2) {
//                            doubleCannonsToActivate.add(Arrays.asList(row, column)); // If the selected component is a double cannon, the coordinated are added to the list
//                            currentCannonCount++;
//                        } else {
//                            System.out.println("The selected cannon is not a double cannon");
//                        }
//                    } else {
//                        System.out.println("The selected component is not a cannon, try again: ");
//                    }
//                } catch (OutOfGridException e) {
//                    System.out.println(e.getMessage() + " , try again");
//                }
//            } catch (InterruptedException e) {
//                return new ArrayList<>(); // Returns an empty list
//            } catch (NumberFormatException e) {
//                System.out.println(PrintUtils.addColor("Invalid input, please insert a number: ", ANSIColors.RED));
//            }
//        }
//        return doubleCannonsToActivate;
//    }

    // Invoke this method only if the firepower is enough to defeat the pirates?
    public boolean inputTakeLoot() {
        String input;

        System.out.print("Do you want to take the credits? (YES/NO): ");

        do {
            try {
                input = this.inputThread.waitForInput();
                if (input == null) { return false; }

                if (input.equalsIgnoreCase("YES")) {
                    return true;
                } else if (input.equalsIgnoreCase("NO")) {
                    return false;
                } else {
                    System.out.print("Invalid input, try again: ");
                }
            } catch (InterruptedException e){
                return false;
            }
        } while (true);
    }

    public ArrayList<int []> inputShieldsActivatedCoordinates() {
        return null;
    }
}


