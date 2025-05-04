package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

public class ClientMeteorShower extends ClientEventCard {
    private int currMeteorIndex;
    private int diceThrowResult;
    private Map<String, Integer> currMeteorDescriptor;

    private MeteorShowerJSON meteorShowerJSON;

    public ClientMeteorShower(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        super(model, inputThread, cardState);
        this.meteorShowerJSON = new MeteorShowerJSON();
    }


    @Override
    public ActionJSON useCard() {
        this.meteorShowerJSON.setPlayerNickname(this.playerNickname);
        MeteorShowerJSON tmp = this.meteorShowerJSON;
        this.meteorShowerJSON = new MeteorShowerJSON();
        return tmp;
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
        WidgetTUI meteor1Title = new WidgetTUI();
        WidgetTUI meteor1 = new WidgetTUI();
        WidgetTUI meteor1Info = new WidgetTUI();
        WidgetTUI meteor1Final;

        meteor1Title.appendString(" ==== METEOR SHOWER ====");

        meteor1.appendString(ANSIColors.RED + " ██████                        " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "  █████████                   " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "   ████████████               " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "    █████████████            " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "     █████" + ANSIColors.BRIGHT_YELLOW + "████" + ANSIColors.RED + "███████          " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "      █████" + ANSIColors.BRIGHT_YELLOW + "███████" + ANSIColors.RED + "█████       " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "       █████" + ANSIColors.BRIGHT_YELLOW + "██████████" + ANSIColors.RED + "████    " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "        ███" + ANSIColors.BRIGHT_YELLOW + "████████████████   " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.RED + "          ██" + ANSIColors.BRIGHT_YELLOW + "██████" + ANSIColors.RESET + "███████" + ANSIColors.BRIGHT_YELLOW + "█████ " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.BRIGHT_YELLOW + "           █████" + ANSIColors.RESET + "███████████" + ANSIColors.BRIGHT_YELLOW + "███" + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.BRIGHT_YELLOW + "             █████" + ANSIColors.RESET + "███████" + ANSIColors.BRIGHT_YELLOW + "████ " + ANSIColors.RESET);
        meteor1.appendString(ANSIColors.BRIGHT_YELLOW + "                ███████████  " + ANSIColors.RESET);
        meteor1.wrapWidgetWithBorder();

        if (this.playerNickname != null) {
            meteor1Info.appendString("==== CURRENT METEOR INFO ====");
            switch (this.currMeteorDescriptor.get("meteorDirection")) {
                case 0 -> meteor1Info.appendString("Inbound Direction: ABOVE");
                case 1 -> meteor1Info.appendString("Outbound Direction: RIGHT");
                case 2 -> meteor1Info.appendString("Outbound Direction: BELOW");
                case 3 -> meteor1Info.appendString("Inbound Direction: LEFT");
            }
            meteor1Info.appendString("Dice Throw Result: " + this.diceThrowResult);
            if (this.currMeteorDescriptor.get("meteorSize") == 1) {
                meteor1Info.appendString("Size: SMALL METEOR");
            } else {
                meteor1Info.appendString("Size: BIG METEOR");
            }
            meteor1Info.appendString("Target: " + this.playerNickname);
        }
        // TODO : does the shootingSequence need to be shown to the clients as a whole?
        meteor1Final = WidgetTUI.composeTwoWidgetsVertically(
                WidgetTUI.composeTwoWidgetsVertically(meteor1Title, meteor1),
                meteor1Info
        );

        meteor1Final.centerWidgetScreen();
        return meteor1Final.wrapWidgetWithBorder();
    }

    @Override
    public void setShieldsToActivate(List<ComponentHelper<Integer>> shieldsToActivate) throws UnsupportedOperationException {
        this.meteorShowerJSON.setShieldsCoordinates(shieldsToActivate);
    }

    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Integer>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.meteorShowerJSON.setCannonsCoordinates(doubleCannonsToActivate);
    }
}
