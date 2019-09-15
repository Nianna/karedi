package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.MoveCollectionCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Note;

class MoveSelectionAction extends NewKarediAction {
    private final KarediActions handledAction;

    private Direction direction;

    private final NoteSelection noteSelection;

    private final CommandExecutor commandExecutor;

    MoveSelectionAction(KarediActions handledAction, Direction direction, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        this.handledAction = handledAction;
        this.direction = direction;
        this.noteSelection = noteSelection;
        setDisabledCondition(this.noteSelection.isEmptyProperty());
        this.commandExecutor = commandExecutor;
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(new MoveCollectionCommand<Integer, Note>(noteSelection.get(), direction, 1));
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
