package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;

import java.util.List;

class FitToVisibleAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final boolean vertically;
    private final boolean horizontally;
    private final SongContext songContext;

    FitToVisibleAction(KarediActions handledAction, boolean vertically, boolean horizontally, SongContext songContext) {
        this.handledAction = handledAction;
        this.vertically = vertically;
        this.horizontally = horizontally;
        this.songContext = songContext;
        setDisabledCondition(this.songContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        IntBounded visibleAreaBounds = songContext.getVisibleAreaBounds();
        List<Note> visibleNotes = songContext.getActiveSong().getVisibleNotes(visibleAreaBounds.getLowerXBound(), visibleAreaBounds.getUpperXBound());
        if (visibleNotes.size() > 0) {
            IntBounded bounds = songContext.addMargins(new BoundingBox<>(visibleNotes));
            if (horizontally) {
                songContext.setVisibleAreaXBounds(bounds.getLowerXBound(), bounds.getUpperXBound());
            }
            if (vertically) {
                songContext.setVisibleAreaYBounds(bounds.getLowerYBound(), bounds.getUpperYBound());
            }
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}