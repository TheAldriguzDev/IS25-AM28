package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class ClientOpenSpace extends ClientEventCard {
    private static final List<String> enabledCommands;

    // Commands that this card will enable are added here
    static {
        enabledCommands = new ArrayList<>();
        enabledCommands.add("setDoubleEnginesToActivate");
    }

    private OpenSpaceJSON openSpaceJSON;

    public ClientOpenSpace(CardStateJSON cardState) {
        super(cardState);
        openSpaceJSON = new OpenSpaceJSON();
    }

    @Override
    public List<String> getEnabledCommands() {
        return enabledCommands;
    }

    @Override
    public ActionJSON useCard() {
        this.openSpaceJSON.setPlayerNickname(this.playerNickname);
        OpenSpaceJSON tmp = openSpaceJSON;
        this.openSpaceJSON = new OpenSpaceJSON();

        return tmp;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI twinkling_space = new WidgetTUI();

        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

        List<String> colorPool = new ArrayList<>();
        Random rand = new Random();
        StringBuilder spaceString;
        int randIndex, randColor;

        int height = 12;
        int width = 31;

        // Aggregates all the possible colors that the space symbols can have
        colorPool.add(ANSIColors.MAGENTA);
        colorPool.add(ANSIColors.RED);
        colorPool.add(ANSIColors.YELLOW);
        colorPool.add(ANSIColors.CYAN);

        // Indicates how much the stars should be spread apart
        int spreadFactor = 60;
        int symbolPoolSize = UnicodeCharacters.SPACE_SYMBOLS.length + spreadFactor;

        for (int i = 0; i < height; i++) {
            spaceString = new StringBuilder();

            for (int j = 0; j < width; j++) {
                randIndex = rand.nextInt(0, symbolPoolSize);
                randColor = rand.nextInt(0, colorPool.size());

                if (randIndex < UnicodeCharacters.SPACE_SYMBOLS.length) {
                    spaceString.append(PrintUtils.addColor(UnicodeCharacters.SPACE_SYMBOLS[randIndex], colorPool.get(randColor)));
                }
                else {
                    spaceString.append(SPACE);
                }
            }
            twinkling_space.appendString(spaceString.toString());
        }
        twinkling_space.wrapWidgetWithBorder();
        if(this.playerNickname != null) {
            twinkling_space.appendString("Current player: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, twinkling_space).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public void setDoubleEnginesToActivate(int doubleEnginesToActivate) {
        this.openSpaceJSON.setUsedEnergy(doubleEnginesToActivate);
    }

    @Override
    public int getDoubleEnginesToActivate() {
        return this.openSpaceJSON.getUsedEnergy();
    }
}