package main.java.com.github.nianna.karedi.command;

import org.springframework.stereotype.Component;

@Component
public class CommandExecutor {

    private final CommandHistory history;

    private final StateManager stateManager;

    public CommandExecutor(CommandHistory history, StateManager stateManager) {
        this.history = history;
        this.stateManager = stateManager;
    }

    public boolean execute(Command command) {
        return history.push(new BackupStateCommandDecorator(command, stateManager));
    }
}
