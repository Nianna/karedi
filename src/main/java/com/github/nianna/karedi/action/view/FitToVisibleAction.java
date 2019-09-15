package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;

import java.util.List;

class FitToVisibleAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final boolean vertically;
    private final boolean horizontally;
    private final SongContext songContext;
    private final VisibleArea visibleArea;

    FitToVisibleAction(KarediActions handledAction, boolean vertically, boolean horizontally, SongContext songContext, VisibleArea visibleArea) {
        this.handledAction = handledAction;
        this.vertically = vertically;
        this.horizontally = horizontally;
        this.songContext = songContext;
        this.visibleArea = visibleArea;
        setDisabledCondition(this.songContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> visibleNotes = songContext.getActiveSong().getVisibleNotes(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
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
                songContext.setActiveLine(null);
        }
    }


    private void setVisibleAreaYBounds(int lowerBound, int upperBound) {
        if (visibleArea.setYBounds(lowerBound, upperBound)) {
            songContext.setActiveLine(null);;
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}