package main.java.com.github.nianna.karedi.action.edit;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandHistory;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.REDO;

@Component
class RedoAction extends NewKarediAction {

    private final CommandHistory history;

    RedoAction(CommandHistory history) {
        this.history = history;
        BooleanBinding redoDisabled = Bindings.createBooleanBinding(
                () -> !this.history.canRedo(),
                this.history.activeIndexProperty(),
                this.history.sizeProperty()
        );
        setDisabledCondition(redoDisabled);
    }

    @Override
    protected void onAction(ActionEvent event) {
        history.redo();
    }

    @Override
    public KarediActions handles() {
        return REDO;
    }
}
