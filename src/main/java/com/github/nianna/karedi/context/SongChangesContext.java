package main.java.com.github.nianna.karedi.context;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandHistory;
import org.springframework.stereotype.Component;

@Component
public class SongChangesContext {

    private final ObjectProperty<Command> lastSavedCommand = new SimpleObjectProperty<>();

    private final CommandHistory history;

    public SongChangesContext(CommandHistory history) {
        this.history = history;
    }

    BooleanBinding hasNoChangesProperty() {
        return lastSavedCommand.isEqualTo(history.activeCommandProperty());
    }

    boolean hasChanges() {
        return lastSavedCommand.get() != history.getActiveCommand();
    }

    void persistChanges() {
        lastSavedCommand.set(history.getActiveCommand());
    }

    void abandonChanges() {
        lastSavedCommand.set(null);
        history.clear();
    }
}
