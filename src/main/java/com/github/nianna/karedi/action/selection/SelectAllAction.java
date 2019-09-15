package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongState;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_ALL;

@Component
public class SelectAllAction extends NewKarediAction {

    private final SongState songState;

    private final NoteSelection noteSelection;

    private SelectAllAction(SongState songState, NoteSelection noteSelection) {
        this.songState = songState;
        this.noteSelection = noteSelection;
        setDisabledCondition(this.songState.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        noteSelection.set(songState.getActiveTrack().getNotes());
    }

    @Override
    public KarediActions handles() {
        return SELECT_ALL;
    }
}