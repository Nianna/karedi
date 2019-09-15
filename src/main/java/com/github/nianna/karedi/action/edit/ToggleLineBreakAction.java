package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.ToggleLineBreakCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.TOGGLE_LINEBREAK;

@Component
class ToggleLineBreakAction extends NewKarediAction {

    private final NoteSelection noteSelection;
    private final CommandExecutor commandExecutor;
    private final SongState songState;

    ToggleLineBreakAction(NoteSelection noteSelection, CommandExecutor commandExecutor, SongState songState) {
        this.noteSelection = noteSelection;
        this.commandExecutor = commandExecutor;
        this.songState = songState;
        setDisabledCondition(this.noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note splittingNote = noteSelection.getFirst().get();
        Command cmd = new ToggleLineBreakCommand(splittingNote);
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
            songState.setActiveLine(splittingNote.getLine());
            noteSelection.selectOnly(splittingNote);
        }));
    }

    @Override
    public KarediActions handles() {
        return TOGGLE_LINEBREAK;
    }
}