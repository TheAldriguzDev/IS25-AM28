package it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI;

import java.util.List;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public class CommandWidgetTUI extends WidgetTUI {
    private final String commandId;
    private Runnable command;

    // Constructor
    public CommandWidgetTUI(String commandId, Runnable command) {
        super();
        this.commandId = commandId;
        this.command = command;
        this.appendString(this.commandId);
    }

    /**
     * @return This command's ID
     */
    public String getCommandId() {
        return this.commandId;
    }

    /**
     * @param command The command that this widget will run when selected (given as a Runnable function)
     */
    public void setCommand(Runnable command) {
        this.command = command;
    }

    /**
     * Runs this widget's command (if present)
     */
    public void runCommand() {
        if (this.command != null) {
            this.command.run();
        }
    }

    @Override
    public WidgetTUI appendScreen(List<String> screen) {
        if (screen != null && !screen.isEmpty()) {
            this.appendString(screen.getFirst());
        }

        return this;
    }

    @Override
    public WidgetTUI appendString(String string) {
        if (string != null) {
            if (this.screen.size() == 1) {
                // Enables adding a description to the command right after its ID
                // without having to put it right underneath it
                this.screen.set(0, "(" + this.commandId + ")" + SPACE + string);
            }
            else {
                super.appendString("(" + ")" + SPACE + string);
            }
        }

        return this;
    }
}
