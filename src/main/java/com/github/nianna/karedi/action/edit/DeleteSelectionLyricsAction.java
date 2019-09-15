package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.DeleteTextCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.DELETE_LYRICS;

@Component
class DeleteSelectionLyricsAction extends NewKarediAction {

    private final NoteSelection noteSelection;
    private final CommandExecutor commandExecutor;

    DeleteSelectionLyricsAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        this.noteSelection = noteSelection;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(this.noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note first = noteSelection.getFirst().get();
        commandExecutor.execute(new DeleteTextCommand(first, noteSelection.getLast().get()));
    }

    @Override
    public KarediActions handles() {
        return DELETE_LYRICS;
    }
}