package it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;

import java.util.*;

public class InputWidgetTUI extends WidgetTUI {
    public static int DEFAULT_GROUPING_FACTOR = 1;

    private Map<String, CommandWidgetTUI> commands;
    private int commandsPerCol;
    private InputThread inputThread;

    // Constructor
    public InputWidgetTUI() {
        super();
        this.commands = null;
        this.commandsPerCol = DEFAULT_GROUPING_FACTOR;
        this.inputThread = null;
    }

    /**
     * Sets this input widget's input thread
     */
    public void setInputThread(InputThread inputThread) {
        this.inputThread = inputThread;

        if (!this.inputThread.isAlive()) {
            this.inputThread.start();
        }
    }

    /**
     * @param commands A list of commands to store, each mapped to its ID for
     *                 easy retrieval when selected by the user
     */
    public void setCommands(List<CommandWidgetTUI> commands) {
        if (this.commands == null) {
            this.commands = new HashMap<>();
        }

        if (commands != null && !commands.isEmpty() && !commands.contains(null)) {
            for (CommandWidgetTUI command : commands) {
                this.commands.put(command.getCommandId(), command);
            }
        }
    }

    /**
     * @param command The command to add to the available commands
     *                in this input widget
     */
    public void addCommand(CommandWidgetTUI command) {
        if (this.commands == null) {
            this.commands = new HashMap<>();
        }

        if (command != null) {
            String commandIdentifier = command.getCommandId();

            if (commandIdentifier != null && !commandIdentifier.isEmpty()) {
                this.commands.put(commandIdentifier, command);
            }
        }
    }

    /**
     * @return This input widget's command map
     */
    public Map<String, CommandWidgetTUI> getCommandMap() {
        return this.commands;
    }

    /**
     * @param commandsPerCol The amount of commands to group together for each
     *                       column that gets printed to stdout
     * <p>
     * (NOTE: Grouping is done on a per-column basis since this allows all command
     *        descriptions (i.e.: their screens) to be always left-aligned by
     *       default with respect to the column (due to the horizontal composition))
     */
    public void setColumnGroupingAmount(int commandsPerCol) {
        this.commandsPerCol = Math.max(1, commandsPerCol);
    }

    /**
     * Asks the user to input the ID of the command to run
     *
     * @return A <code>Future</code> of <code>Boolean</code> that contains whether
     *         the user input resulted in the selection of a command or not.
     */
    public boolean selectCommand(String prefix) {
        CommandWidgetTUI commandWidget;
        String input;

        if (this.commands != null && !this.commands.isEmpty()) {
            this.printWidget();

            // Quick change of the prefix
            if (prefix != null && !prefix.isEmpty()) {
                System.out.print(prefix);
            }

            try {
                input = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (input == null) return false;
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return false;
            }

            commandWidget = this.commands.get(input);

            if (commandWidget != null) {
                commandWidget.runCommand();
                return true;
            }
            else {
                return false;
            }
        }

        return false;
    }

    /**
     * Prints the widget to stdout and also takes into account the
     * column grouping value, given by the commandsPerCol attribute
     */
    @Override
    public void printWidget() {
        List<WidgetTUI> commandsColumns;
        List<WidgetTUI> commandsToCompose;
        List<CommandWidgetTUI> commandList;
        int commandCount;

        if (this.commands != null && !this.commands.isEmpty()) {
            this.resetScreenAndDimensions();
            commandList = this.commands.values().stream().toList();
            commandCount = commandList.size();
            commandsColumns = new ArrayList<>();

            for (int i = 0; i < commandCount; i += this.commandsPerCol) {
                if (i + this.commandsPerCol < commandCount) {
                    commandsToCompose = new ArrayList<>(commandList.subList(i, i + this.commandsPerCol));
                }
                else {
                    commandsToCompose = new ArrayList<>(commandList.subList(i, commandCount));
                }

                commandsColumns.add(
                    WidgetTUI.fillScreenWithSpaces(
                        WidgetTUI.composeWidgetsVertically(commandsToCompose)
                    ).addPadding(0, 1, 0, 1)
                );
            }

            this.setScreen(WidgetTUI.composeWidgetsHorizontally(commandsColumns).getScreen());
            this.wrapWidgetWithBorder();

            super.printWidget();
        }
    }
}
