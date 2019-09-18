package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_ALL;

@Component
public class SelectAllAction extends KarediAction {

    private final DisplayContext displayContext;

    private final NoteSelection noteSelection;

    private SelectAllAction(DisplayContext displayContext, NoteSelection noteSelection) {
        this.displayContext = displayContext;
        this.noteSelection = noteSelection;
        setDisabledCondition(this.displayContext.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        noteSelection.set(displayContext.getActiveTrack().getNotes());
    }

    @Override
    public KarediActions handles() {
        return SELECT_ALL;
    }
}