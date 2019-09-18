package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.FIT_TO_SELECTION;

@Component
class FitToSelectionAction extends NewKarediAction {

    private final SongContext songContext;
    private final VisibleArea visibleArea;
    private final SongPlayer songPlayer;
    private final NoteSelection noteSelection;

    FitToSelectionAction(NoteSelection noteSelection, SongContext songContext, VisibleArea visibleArea, SongPlayer songPlayer) {
        this.songContext = songContext;
        this.visibleArea = visibleArea;
        this.songPlayer = songPlayer;
        this.noteSelection = noteSelection;
        setDisabledCondition(noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        songPlayer.stop(); //TODO can it be done differently?
        songContext.setActiveLine(null);
        visibleArea.setBounds(visibleArea.addMargins(noteSelection.getSelectionBounds()));
    }

    @Override
    public KarediActions handles() {
        return FIT_TO_SELECTION;
    }
}