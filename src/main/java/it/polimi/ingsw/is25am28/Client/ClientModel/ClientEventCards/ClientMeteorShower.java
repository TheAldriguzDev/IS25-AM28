package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

public class ClientMeteorShower extends ClientEventCard {
    private int currMeteorIndex;
    private int diceThrowResult;
    private Map<String, Integer> currMeteorDescriptor;
//    private Map<String, List<Map<String, Object>>> previousPlayerRemovedComponents;

    public ClientMeteorShower(CardStateJSON cardState) {
        super(cardState);
    }


    @Override
    public void useCard() {
        // Needs implementation
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

        meteor1Info.appendString("==== CURRENT METEOR INFO ====");
        meteor1Info.appendString("Inbound Direction: LEFT");
        meteor1Info.appendString("Dice Throw Result: 7");
        meteor1Info.appendString("Size: BIG METEOR");
        meteor1Info.appendString("Current Player: " + PrintUtils.addColor("MasterChief216", ANSIColors.RED));

        meteor1Final = WidgetTUI.composeTwoWidgetsVertically(
                WidgetTUI.composeTwoWidgetsVertically(meteor1Title, meteor1),
                meteor1Info
        );

        meteor1Final.centerWidgetScreen();
        return meteor1Final.wrapWidgetWithBorder();
    }
}
