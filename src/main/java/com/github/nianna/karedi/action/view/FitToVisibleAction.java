package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;

import java.util.List;

class FitToVisibleAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final boolean vertically;
    private final boolean horizontally;
    private final SongState songState;
    private final VisibleArea visibleArea;
    private final AppContext appContext; //TODO remove

    FitToVisibleAction(KarediActions handledAction, boolean vertically, boolean horizontally, SongState songState, VisibleArea visibleArea, AppContext appContext) {
        this.handledAction = handledAction;
        this.vertically = vertically;
        this.horizontally = horizontally;
        this.songState = songState;
        this.visibleArea = visibleArea;
        this.appContext = appContext;
        setDisabledCondition(this.songState.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> visibleNotes = appContext.getSong().getVisibleNotes(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
        if (visibleNotes.size() > 0) {
            IntBounded bounds = visibleArea.addMargins(new BoundingBox<>(visibleNotes));
            if (horizontally) {
                setVisibleAreaXBounds(bounds.getLowerXBound(), bounds.getUpperXBound());
            }
            if (vertically) {
                setVisibleAreaYBounds(bounds.getLowerYBound(), bounds.getUpperYBound());
            }
        }
    }

    private void setVisibleAreaXBounds(Integer lowerXBound, Integer upperXBound) {
        if (visibleArea.setXBounds(lowerXBound, upperXBound)) {
                songState.setActiveLine(null);
        }
    }


    private void setVisibleAreaYBounds(int lowerBound, int upperBound) {
        if (visibleArea.setYBounds(lowerBound, upperBound)) {
            songState.setActiveLine(null);;
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}