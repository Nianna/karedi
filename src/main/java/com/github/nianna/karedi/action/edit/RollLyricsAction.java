package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.Note;

import java.util.List;
import java.util.function.Function;

class RollLyricsAction extends NewKarediAction {

    private final KarediActions handledAction;
    private final NoteSelection noteSelection;
    private final SongState songState;
    private final CommandExecutor commandExecutor;
    private final Function<List<Note>, Command> rollCommandCreator;

    RollLyricsAction(KarediActions handledAction, NoteSelection noteSelection, SongState songState, CommandExecutor commandExecutor, Function<List<Note>, Command> rollCommandCreator) {
        this.handledAction = handledAction;
        this.noteSelection = noteSelection;
        this.songState = songState;
        this.commandExecutor = commandExecutor;
        this.rollCommandCreator = rollCommandCreator;
        setDisabledCondition(this.noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> notes = songState.getActiveTrack().getNotes(noteSelection.getFirst().get(), null);
        Command cmd = rollCommandCreator.apply(notes);
        commandExecutor.execute(cmd);
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}