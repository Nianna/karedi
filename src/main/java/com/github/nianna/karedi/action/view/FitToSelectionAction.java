package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.*;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.FIT_TO_SELECTION;

@Component
class FitToSelectionAction extends NewKarediAction {

    private final SongState songState;
    private final VisibleArea visibleArea;
    private final SongPlayer songPlayer;
    private final AppContext appContext; //TODO remove

    FitToSelectionAction(NoteSelection noteSelection, SongState songState, VisibleArea visibleArea, SongPlayer songPlayer, AppContext appContext) {
        this.songState = songState;
        this.visibleArea = visibleArea;
        this.songPlayer = songPlayer;
        this.appContext = appContext;
        setDisabledCondition(noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        songPlayer.stop(); //TODO can it be done differently?
        songState.setActiveLine(null);
        visibleArea.setBounds(visibleArea.addMargins(appContext.getSelectionBounds()));
    }

    @Override
    public KarediActions handles() {
        return FIT_TO_SELECTION;
    }
}