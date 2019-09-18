package main.java.com.github.nianna.karedi.action.edit;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.command.CommandHistory;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.UNDO;

@Component
class UndoAction extends KarediAction {

    private final CommandHistory history;

    private UndoAction(CommandHistory history) {
        this.history = history;
        setDisabledCondition(Bindings.createBooleanBinding(
                () -> !this.history.canUndo(),
                this.history.activeIndexProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        history.undo();
    }

    @Override
    public KarediActions handles() {
        return UNDO;
    }
}