package main.java.com.github.nianna.karedi.action.edit;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.ResizeNotesCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.region.Direction;

class ResizeAction extends KarediAction {
    private final KarediActions handledAction;
    private final Direction direction;
    private final int by;
    private final NoteSelection noteSelection;
    private final CommandExecutor commandExecutor;
    private BooleanProperty disabled;

    ResizeAction(KarediActions handledAction, Direction direction, int by, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        this.handledAction = handledAction;
        this.direction = direction;
        this.by = by;
        this.noteSelection = noteSelection;
        this.commandExecutor = commandExecutor;

        if (by < 0) {
            disabled = new SimpleBooleanProperty(true);
            noteSelection.getObservableSelection().addListener((InvalidationListener) inv -> refreshDisabled());
            setDisabledCondition(disabled);
        } else {
            setDisabledCondition(noteSelection.isEmptyProperty());
        }
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(new ResizeNotesCommand(noteSelection.get(), direction, by));
    }

    private void refreshDisabled() {
        disabled.set(!ResizeNotesCommand.canExecute(noteSelection.get(), direction, by));
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
