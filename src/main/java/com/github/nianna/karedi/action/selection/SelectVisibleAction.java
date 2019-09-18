package main.java.com.github.nianna.karedi.action.selection;


import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import java.util.List;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_VISIBLE;

@Component
public class SelectVisibleAction extends NewKarediAction {

    private final DisplayContext displayContext;

    private final NoteSelection noteSelection;

    private SelectVisibleAction(DisplayContext displayContext, NoteSelection noteSelection) {
        this.displayContext = displayContext;
        this.noteSelection = noteSelection;
        setDisabledCondition(this.displayContext.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> notes;
        if (displayContext.getActiveLine() != null) {
            notes = displayContext.getActiveLine().getNotes();
        } else {
            notes = displayContext.getActiveTrack().getNotes(displayContext.getVisibleAreaBounds().getLowerXBound(), displayContext.getVisibleAreaBounds().getUpperXBound());
        }
        noteSelection.set(notes);
    }

    @Override
    public KarediActions handles() {
        return SELECT_VISIBLE;
    }
}
