package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.List;
import java.util.Map;

public class ClientMeteorShower extends ClientEventCard {
    private int currMeteorIndex;
    private int diceThrowResult;
    private Map<String, Integer> currMeteorDescriptor;

    private MeteorShowerJSON meteorShowerJSON;

    public ClientMeteorShower(CardStateJSON cardState) {
        super(cardState);
        this.meteorShowerJSON = new MeteorShowerJSON();

        enabledCommands.add("setShieldsToActivate");
        enabledCommands.add("setDoubleCannonsToActivate");
    }

    @Override
    public ActionJSON useCard() {
        this.meteorShowerJSON.setPlayerNickname(this.playerNickname);
        return this.meteorShowerJSON;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        this.currMeteorIndex = cardState.getCurrMeteorIndex();
        this.diceThrowResult = cardState.getDiceThrowResult();
        this.currMeteorDescriptor = cardState.getCurrMeteorDescriptor();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI meteorShowerTitle = new WidgetTUI();
        WidgetTUI meteorShower = new WidgetTUI();
        WidgetTUI meteorShowerInfo = new WidgetTUI();
        WidgetTUI meteorShowerFinal;

        meteorShowerTitle.appendString("[METEOR SHOWER]");

        meteorShower.appendString(ANSIColors.RED + " ██████                        " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "  █████████                   " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "   ████████████               " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "    █████████████            " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "     █████" + ANSIColors.BRIGHT_YELLOW + "████" + ANSIColors.RED + "███████          " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "      █████" + ANSIColors.BRIGHT_YELLOW + "███████" + ANSIColors.RED + "█████       " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "       █████" + ANSIColors.BRIGHT_YELLOW + "██████████" + ANSIColors.RED + "████    " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "        ███" + ANSIColors.BRIGHT_YELLOW + "████████████████   " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.RED + "          ██" + ANSIColors.BRIGHT_YELLOW + "██████" + ANSIColors.RESET + "███████" + ANSIColors.BRIGHT_YELLOW + "█████ " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.BRIGHT_YELLOW + "           █████" + ANSIColors.RESET + "███████████" + ANSIColors.BRIGHT_YELLOW + "███" + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.BRIGHT_YELLOW + "             █████" + ANSIColors.RESET + "███████" + ANSIColors.BRIGHT_YELLOW + "████ " + ANSIColors.RESET);
        meteorShower.appendString(ANSIColors.BRIGHT_YELLOW + "                ███████████  " + ANSIColors.RESET);
        meteorShower.wrapWidgetWithBorder();

        if (this.playerNickname != null) {
            meteorShowerInfo
                    .appendString("[CURRENT METEOR INFO]")
                    .appendString("Meteor #" + (this.currMeteorIndex + 1));

            switch (this.currMeteorDescriptor.get("meteorDirection")) {
                case 0 -> meteorShowerInfo.appendString("Inbound Direction: ABOVE");
                case 1 -> meteorShowerInfo.appendString("Outbound Direction: RIGHT");
                case 2 -> meteorShowerInfo.appendString("Outbound Direction: BELOW");
                case 3 -> meteorShowerInfo.appendString("Inbound Direction: LEFT");
            }

            meteorShowerInfo.appendString("Dice Throw Result: " + this.diceThrowResult);

            if (this.currMeteorDescriptor.get("meteorSize") == 1) {
                meteorShowerInfo.appendString("Size: SMALL METEOR");
            }
            else {
                meteorShowerInfo.appendString("Size: BIG METEOR");
            }

            meteorShowerInfo.appendString("Target: " + this.playerNickname);
        }
        // TODO : does the shootingSequence need to be shown to the clients as a whole?
        meteorShowerFinal = WidgetTUI.composeTwoWidgetsVertically(
                WidgetTUI.composeTwoWidgetsVertically(meteorShowerTitle, meteorShower),
                meteorShowerInfo
        );

        meteorShowerFinal.centerWidgetScreen();
        return meteorShowerFinal.wrapWidgetWithBorder();
    }

    @Override
    public void clearJSON() {
        this.meteorShowerJSON = new MeteorShowerJSON();
    }

    // Shields
    @Override
    public void setShieldsToActivate(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsToActivate) throws UnsupportedOperationException {
        this.meteorShowerJSON.setShieldsCoordinates(shieldsToActivate);
    }

    @Override
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getShieldsToActivate() throws UnsupportedOperationException {
        return this.meteorShowerJSON.getShieldsCoordinates();
    }

    // Cannons
    @Override
    public void setDoubleCannonsToActivate(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.meteorShowerJSON.setCannonsCoordinates(doubleCannonsToActivate);
    }

    @Override
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.meteorShowerJSON.getCannonsCoordinates();
    }
}
