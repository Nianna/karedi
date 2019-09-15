package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_ALL;

@Component
public class SelectAllAction extends NewKarediAction {

    private final SongContext songContext;

    private final NoteSelection noteSelection;

    private SelectAllAction(SongContext songContext, NoteSelection noteSelection) {
        this.songContext = songContext;
        this.noteSelection = noteSelection;
        setDisabledCondition(this.songContext.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        noteSelection.set(songContext.getActiveTrack().getNotes());
    }

    @Override
    public KarediActions handles() {
        return SELECT_ALL;
    }
}