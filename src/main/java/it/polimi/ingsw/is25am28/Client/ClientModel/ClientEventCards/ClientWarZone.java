package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClientWarZone extends ClientEventCard {
    private List<List<String>> actionAndConsequences;
    private final int requiredCrew;
    private final int movementSteps;
    private final int requiredResources;
    private String affectedPlayer;
    private int currActionIndex;
    private Map<String, Integer> currentPlasmaShot;
    private int diceThrowResult;

    private WarZoneJSON warZoneJSON;

    public ClientWarZone(CardStateJSON cardState) {
        super(cardState);
        this.actionAndConsequences = cardState.getActionsAndConsequences();
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementSteps = cardState.getMovementSteps();
        this.requiredResources = cardState.getRequiredResources();
        this.warZoneJSON = new WarZoneJSON();

//        enabledCommands.add("setItemsToBeRemoved");
//        enabledCommands.add("setDoubleCannonsToActivate");
//        enabledCommands.add("setDoubleEnginesToActivate");
    }

    @Override
    public ActionJSON useCard() {
        this.warZoneJSON.setPlayerNickname(this.playerNickname);
        return this.warZoneJSON;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname(); // If present, the current action (not the general card thumbnail) will be shown
        this.affectedPlayer = cardState.getAffectedPlayer();
        this.currActionIndex = cardState.getCurrActionIndex(); // Will be used in the generateWidget to determine what to display
        this.currentPlasmaShot = cardState.getCurrPlasmaShotDescriptor();
        this.diceThrowResult = cardState.getDiceThrowResult();

        enabledCommands.clear();
        enabledCommands.add("playCard");
        if (this.affectedPlayer == null || this.affectedPlayer.isEmpty()) { // Sets the commands relative to the Actions
            switch (this.actionAndConsequences.get(currActionIndex).getFirst()) {
                case "Enginepower" -> {
                    enabledCommands.add("setDoubleEnginesToActivate");
                }
                case "Firepower" -> {
                    enabledCommands.add("setDoubleCannonsToActivate");
                }
                default -> {} // "Humans" does not need user input
            }
        } else { // Sets the commands relative to the Consequences
            switch (this.actionAndConsequences.get(currActionIndex).getLast()) {
                case "RequiredCrew" -> {
                    enabledCommands.add("setCrewToRemove");
                }
                case "ShootingSequence" -> {
                    enabledCommands.add("setShieldsToActivate");
                }
                case "LossItems" -> {
                    enabledCommands.add("setItemsToBeRemoved");
                }
                default -> {} // MovementSteps does not need user input
            }
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        String tmpAction = null;
        String tmpConsequence = null;
        switch (this.actionAndConsequences.get(currActionIndex).getFirst()) {
            case "Humans" -> tmpAction = "Crew";
            case "Enginepower" -> tmpAction = "EnginePower";
            case "Firepower" -> tmpAction = "FirePower";
        }
        switch (this.actionAndConsequences.get(currActionIndex).getLast()) {
            case "RequiredCrew" -> tmpConsequence = "Taken Crew";
            case "MovementSteps" -> tmpConsequence = "Days";
            case "ShootingSequence" -> tmpConsequence = "PlasmaShots";
            case "LossItems" -> tmpConsequence = "Taken Items";
        }

        cardWidget.appendString("[" + this.cardName.toUpperCase() + " - LVL: " + this.cardLevel + "]");

        if (this.playerNickname != null) {

            switch (this.actionAndConsequences.get(currActionIndex).getLast()) {
                case "RequiredCrew" -> {
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██        ██        " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██      ██████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "      ██████      ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "        ██        ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "      ██████      ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██       ████       " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██       ████       " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██      ██████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "      ██████      ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "        ██        ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██      ██████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██        ██        " + ANSIColors.RESET);
                    cardInfoWidget.wrapWidgetWithBorder();

                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("Affected player: " + affectedPlayer);
                        cardInfoWidget.appendString("Taken crew: " + requiredCrew);
                    } else {
                        cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
                    }
                    cardInfoWidget.appendString("───────────────────────────────");
                }
                case "ShootingSequence" -> {
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

                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("==== CURRENT PLASMASHOT INFO ====");

                        switch (this.currentPlasmaShot.get("shotDirection")) {
                            case 0 -> cardInfoWidget.appendString("Inbound Direction: ABOVE");
                            case 1 -> cardInfoWidget.appendString("Outbound Direction: RIGHT");
                            case 2 -> cardInfoWidget.appendString("Outbound Direction: BELOW");
                            case 3 -> cardInfoWidget.appendString("Inbound Direction: LEFT");
                        }

                        if (this.currentPlasmaShot.get("shotSize") == 1) {
                            cardInfoWidget.appendString("Size: SMALL PLASMASHOT");
                        }
                        else {
                            cardInfoWidget.appendString("Size: BIG PLASMASHOT");
                        }

                        cardInfoWidget.appendString("Dice Throw Result: " + this.diceThrowResult);
                    } else {
                        cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
                        // TODO: INCLUDE SEQUENCE ONY WHEN THE AFFECTED PLAYER HAS NOT BEEN DETERMINED YET

                    }
                    cardInfoWidget.appendString("───────────────────────────────");
                }
                case "LossItems" -> {
                    cardInfoWidget.appendString(ANSIColors.RED + "████                       ████" + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED + "  ████                   ████  " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED + "    ████               ████    " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"      █████         █████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"       █ " + ANSIColors.WHITE + "█████████████" + ANSIColors.RED + " █       " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "         █      ███  █         " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "         █    ███    █         " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "         █  ███      █         " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"       █ " + ANSIColors.WHITE + "█████████████" + ANSIColors.RED + " █       " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"      █████         █████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"    ████               ████    " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"  ████                   ████  " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.RED +"████                       ████" + ANSIColors.RESET);
                    cardInfoWidget.wrapWidgetWithBorder();

                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("RequiredResources: " + requiredResources);
                    } else {
                        cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
                    }
                    cardInfoWidget.appendString("───────────────────────────────");
                }
                case "MovementSteps" -> {
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
                        cardInfoWidget.appendString(spaceString.toString());
                    }
                    cardInfoWidget.wrapWidgetWithBorder();

                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("Taken items: " + requiredCrew);
                    } else {
                        cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
                    }
                    cardInfoWidget.appendString("───────────────────────────────");
                }
            }

            cardInfoWidget.appendString("Current Player: " + this.playerNickname);

        }
        else {
            cardInfoWidget.appendString(ANSIColors.WHITE + "      ██  ██  ███  ██  ██      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "            ███████            " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "         █████████████         " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "        ███████████████        " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "       █████████████████       " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.RED   + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.RED   + "        ███████████████        " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.RED   + "          ███████████          " + ANSIColors.RESET);
            cardInfoWidget.wrapWidgetWithBorder();

            for (List<String> pair : actionAndConsequences) {
                switch (pair.getFirst()) {
                    case "Humans" -> tmpAction = "Crew";
                    case "Enginepower" -> tmpAction = "EnginePower";
                    case "Firepower" -> tmpAction = "FirePower";
                }
                switch (pair.getLast()) {
                    case "RequiredCrew" -> tmpConsequence = "Taken Crew";
                    case "MovementSteps" -> tmpConsequence = "Days";
                    case "ShootingSequence" -> tmpConsequence = "PlasmaShots";
                    case "LossItems" -> tmpConsequence = "Taken Items";
                }
                cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
            }
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget)
                .centerWidgetScreen()
                .wrapWidgetWithBorder();
    }

    @Override
    public void clearJSON() {
        this.warZoneJSON = new WarZoneJSON();
    }

    // Cannons
    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Void>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.warZoneJSON.setCannonList(doubleCannonsToActivate);
    }

    @Override
    public List<ComponentHelper<Void>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.warZoneJSON.getCannonList();
    }

    // Items
    @Override
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        this.warZoneJSON.setItemsToBeRemoved(itemsToBeRemoved);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        return this.warZoneJSON.getItemsToBeRemoved();
    }

    // Engines
    @Override
    public void setDoubleEnginesToActivate(int doubleEnginesToActivate) {
        this.warZoneJSON.setUsedEnergy(doubleEnginesToActivate);
    }

    @Override
    public Integer getDoubleEnginesToActivate() {
        return this.warZoneJSON.getUsedEnergy();
    }

    // Shields
    @Override
    public void setShieldsToActivate(List<ComponentHelper<Void>> shieldsToActivate) throws UnsupportedOperationException {
        this.warZoneJSON.setShieldList(shieldsToActivate);
    }

    @Override
    public List<ComponentHelper<Void>> getShieldsToActivate() throws UnsupportedOperationException {
        return this.warZoneJSON.getShieldList();
    }

    // Crew
    @Override
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) throws UnsupportedOperationException {
        this.warZoneJSON.setLifeformsToBeRemoved(crewToRemove);
    }

    @Override
    public List<ComponentHelper<LifeformType>> getCrewToRemove() throws UnsupportedOperationException {
        return this.warZoneJSON.getLifeformsToBeRemoved();
    }

}
