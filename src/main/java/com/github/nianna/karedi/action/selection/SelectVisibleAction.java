package main.java.com.github.nianna.karedi.action.selection;


import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import java.util.List;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_VISIBLE;

@Component
public class SelectVisibleAction extends NewKarediAction {

    private final SongState songState;

    private final VisibleArea visibleArea;

    private final NoteSelection noteSelection;

    private SelectVisibleAction(SongState songState, VisibleArea visibleArea, NoteSelection noteSelection) {
        this.songState = songState;
        this.visibleArea = visibleArea;
        this.noteSelection = noteSelection;
        setDisabledCondition(this.songState.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> notes;
        if (songState.getActiveLine() != null) {
            notes = songState.getActiveLine().getNotes();
        } else {
            notes = songState.getActiveTrack().getNotes(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
        }
        noteSelection.set(notes);
    }

    @Override
    public KarediActions handles() {
        return SELECT_VISIBLE;
    }
}
