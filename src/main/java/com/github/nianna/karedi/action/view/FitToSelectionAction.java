package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.FIT_TO_SELECTION;

@Component
class FitToSelectionAction extends NewKarediAction {

    private final DisplayContext displayContext;
    private final SongPlayer songPlayer;
    private final NoteSelection noteSelection;

    FitToSelectionAction(NoteSelection noteSelection, DisplayContext displayContext, SongPlayer songPlayer) {
        this.displayContext = displayContext;
        this.songPlayer = songPlayer;
        this.noteSelection = noteSelection;
        setDisabledCondition(noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        songPlayer.stop(); //TODO can it be done differently?
        displayContext.setActiveLine(null);
        displayContext.setBoundsWithMargin(noteSelection.getSelectionBounds());
    }

    @Override
    public KarediActions handles() {
        return FIT_TO_SELECTION;
    }
}