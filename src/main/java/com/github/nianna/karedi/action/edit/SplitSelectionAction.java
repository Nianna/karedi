package main.java.com.github.nianna.karedi.action.edit;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.SplitNoteCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.BindingsUtils;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SPLIT_SELECTION;

@Component
class SplitSelectionAction extends NewKarediAction {
    private BooleanProperty disabled = new SimpleBooleanProperty();
    private final NoteSelection noteSelection;
    private final CommandExecutor commandExecutor;
    private final ObjectBinding<Note> splitNote;

    SplitSelectionAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        this.noteSelection = noteSelection;
        this.commandExecutor = commandExecutor;
        splitNote = BindingsUtils.valueAt(noteSelection.get(), 0);
        ;
        InvalidationListener lengthListener = ((inv) -> {
            refreshDisabled();
        });

        splitNote.addListener((obsVal, oldVal, newVal) -> {
            if (oldVal != null) {
                oldVal.lengthProperty().removeListener(lengthListener);
            }
            if (newVal != null) {
                newVal.lengthProperty().addListener(lengthListener);
            }
            refreshDisabled();
        });

        refreshDisabled();
        setDisabledCondition(disabled);
    }

    private void refreshDisabled() {
        Note note = splitNote.get();
        disabled.set(!SplitNoteCommand.canExecute(note, splitPoint(note)));
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note note = splitNote.get();
        Command cmd = new SplitNoteCommand(note, splitPoint(note));
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
            noteSelection.selectOnly(note);
        }));
    }

    private int splitPoint(Note note) {
        if (note == null) {
            return 0;
        }
        return (int) Math.ceil(note.getLength() / 2.0);
    }

    @Override
    public KarediActions handles() {
        return SPLIT_SELECTION;
    }
}
